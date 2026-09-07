package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.firestore.ArenaFirestoreService
import com.example.data.model.AccountStatus
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.UserStats
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

data class FirebaseAuthConfig(
  val isFirebaseInitialized: Boolean,
  val hasWebClientId: Boolean,
  val webClientId: String,
  val message: String
)

class FirebaseAuthManager(private val context: Context) {

  companion object {
    private const val TAG = "FirebaseAuthManager"
  }

  val auth: FirebaseAuth?
    get() = try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        FirebaseAuth.getInstance()
      } else {
        null
      }
    } catch (e: Exception) {
      Log.d(TAG, "Firebase not initialized: ${e.message}")
      null
    }

  fun isAvailable(): Boolean = auth != null

  /**
   * Resolves the Web Client ID either from auto-generated google-services resources or custom input.
   */
  fun resolveWebClientId(customClientId: String = ""): String {
    if (customClientId.isNotBlank()) return customClientId.trim()
    return try {
      val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
      if (resId != 0) context.getString(resId).trim() else ""
    } catch (e: Exception) {
      ""
    }
  }

  fun checkAuthConfig(customClientId: String = ""): FirebaseAuthConfig {
    val isInit = isAvailable()
    val clientId = resolveWebClientId(customClientId)
    val hasClient = clientId.isNotBlank()

    val msg = when {
      !isInit -> "Firebase is not initialized. Place 'google-services.json' in the app/ directory."
      !hasClient -> "Web Client ID not found. Ensure Google provider is enabled in Firebase Console and google-services.json is added."
      else -> "Firebase Authentication and Google Sign-In are ready."
    }

    return FirebaseAuthConfig(
      isFirebaseInitialized = isInit,
      hasWebClientId = hasClient,
      webClientId = clientId,
      message = msg
    )
  }

  suspend fun signInWithGoogle(customWebClientId: String = ""): Result<User> {
    val firebaseAuth = auth ?: return Result.failure(
      Exception("Firebase is not initialized. Please place 'google-services.json' in the app/ directory.")
    )

    val webClientId = resolveWebClientId(customWebClientId)
    if (webClientId.isBlank()) {
      return Result.failure(
        Exception(
          "Google Sign-In setup incomplete: Web Client ID is missing. " +
          "Enable Google Sign-In in Firebase Console (Authentication > Sign-in method) " +
          "and add the updated google-services.json to the app/ directory."
        )
      )
    }

    return try {
      val credentialManager = CredentialManager.create(context)
      val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(false)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val result = credentialManager.getCredential(context = context, request = request)
      val credential = result.credential
      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
      val idToken = googleIdTokenCredential.idToken

      val authCredential = GoogleAuthProvider.getCredential(idToken, null)
      val authResult = firebaseAuth.signInWithCredential(authCredential).await()
      val fbUser = authResult.user ?: return Result.failure(Exception("Google Sign-In returned null user."))

      val user = User(
        uid = fbUser.uid,
        displayName = fbUser.displayName ?: fbUser.email?.substringBefore("@") ?: "Arena Warrior",
        email = fbUser.email ?: "",
        bgmiName = "Warrior_${fbUser.uid.take(5)}",
        bgmiUid = "5${System.currentTimeMillis().toString().takeLast(9)}",
        avatarUrl = fbUser.photoUrl?.toString() ?: "",
        role = UserRole.USER,
        virtualCoins = 5,
        stats = UserStats(),
        accountStatus = AccountStatus.ACTIVE,
        createdAt = System.currentTimeMillis()
      )

      ArenaFirestoreService.instance.syncUserToFirestore(user)
      Result.success(user)
    } catch (e: GetCredentialException) {
      Log.e(TAG, "Credential Manager error: ${e.message}", e)
      Result.failure(Exception("Google Sign-In cancelled or failed: ${e.message}"))
    } catch (e: Exception) {
      Log.e(TAG, "Google Sign-In failed: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun signInWithEmail(email: String, pass: String): Result<String> {
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase not connected."))
    return try {
      val res = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
      Result.success(res.user?.uid ?: "")
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun createUserWithEmail(email: String, pass: String): Result<String> {
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase not connected."))
    return try {
      val res = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
      Result.success(res.user?.uid ?: "")
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase not connected."))
    return try {
      firebaseAuth.sendPasswordResetEmail(email).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun signOut() {
    try {
      auth?.signOut()
    } catch (e: Exception) {
      Log.d(TAG, "Sign out: ${e.message}")
    }
  }
}
