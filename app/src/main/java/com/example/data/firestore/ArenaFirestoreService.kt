package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.data.model.AuditLog
import com.example.data.model.CoinTransaction
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Match
import com.example.data.model.Team
import com.example.data.model.Tournament
import com.example.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ArenaFirestoreService(private val context: Context? = null) {

  companion object {
    private const val TAG = "ArenaFirestoreService"
    const val USERS_COLLECTION = "users"
    const val TOURNAMENTS_COLLECTION = "tournaments"
    const val TEAMS_COLLECTION = "teams"
    const val MATCHES_COLLECTION = "matches"
    const val LEADERBOARD_COLLECTION = "leaderboard"
    const val TRANSACTIONS_COLLECTION = "transactions"
    const val AUDIT_LOGS_COLLECTION = "audit_logs"

    val instance: ArenaFirestoreService by lazy { ArenaFirestoreService() }
  }

  fun isFirebaseConnected(): Boolean {
    return try {
      val apps = FirebaseApp.getApps(context ?: return false)
      apps.isNotEmpty()
    } catch (e: Exception) {
      false
    }
  }

  private fun getFirestore(): FirebaseFirestore? {
    return try {
      if (context != null && FirebaseApp.getApps(context).isEmpty()) {
        null
      } else {
        FirebaseFirestore.getInstance()
      }
    } catch (e: Exception) {
      Log.d(TAG, "Firestore not initialized or google-services.json not configured: ${e.message}")
      null
    }
  }

  suspend fun syncUserToFirestore(user: User): Result<Unit> {
    val db = getFirestore() ?: return Result.failure(Exception("Firestore is not connected. Requires google-services.json."))
    return try {
      val userMap = mapOf(
        "uid" to user.uid,
        "displayName" to user.displayName,
        "email" to user.email,
        "bgmiName" to user.bgmiName,
        "bgmiUid" to user.bgmiUid,
        "avatarUrl" to user.avatarUrl,
        "role" to user.role.name,
        "virtualCoins" to user.virtualCoins,
        "accountStatus" to user.accountStatus.name,
        "updatedAt" to System.currentTimeMillis()
      )
      db.collection(USERS_COLLECTION).document(user.uid).set(userMap, SetOptions.merge()).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to sync user to Firestore", e)
      Result.failure(e)
    }
  }

  suspend fun syncTeamRegistrationToFirestore(team: Team): Result<Unit> {
    val db = getFirestore() ?: return Result.failure(Exception("Firestore is not connected."))
    return try {
      val teamMap = mapOf(
        "teamId" to team.teamId,
        "tournamentId" to team.tournamentId,
        "teamName" to team.teamName,
        "captainUid" to team.captainUid,
        "players" to team.players.map { mapOf("name" to it.name, "bgmiUid" to it.bgmiUid, "isCaptain" to it.isCaptain) },
        "status" to team.status,
        "createdAt" to team.createdAt
      )
      db.collection(TEAMS_COLLECTION).document(team.teamId).set(teamMap).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to register team in Firestore", e)
      Result.failure(e)
    }
  }

  suspend fun logAuditToFirestore(auditLog: AuditLog): Result<Unit> {
    val db = getFirestore() ?: return Result.failure(Exception("Firestore is not connected."))
    return try {
      val logMap = mapOf(
        "id" to auditLog.id,
        "actorUid" to auditLog.actorUid,
        "action" to auditLog.action,
        "targetType" to auditLog.targetType,
        "targetId" to auditLog.targetId,
        "timestamp" to auditLog.timestamp,
        "metadata" to auditLog.metadata
      )
      db.collection(AUDIT_LOGS_COLLECTION).document(auditLog.id).set(logMap).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to write audit log to Firestore", e)
      Result.failure(e)
    }
  }

  suspend fun recordCoinTransactionToFirestore(transaction: CoinTransaction): Result<Unit> {
    val db = getFirestore() ?: return Result.failure(Exception("Firestore is not connected."))
    return try {
      val txMap = mapOf(
        "transactionId" to transaction.transactionId,
        "uid" to transaction.uid,
        "type" to transaction.type.name,
        "amount" to transaction.amount,
        "balanceBefore" to transaction.balanceBefore,
        "balanceAfter" to transaction.balanceAfter,
        "reason" to transaction.reason,
        "referenceId" to transaction.referenceId,
        "createdAt" to transaction.createdAt
      )
      db.collection(TRANSACTIONS_COLLECTION).document(transaction.transactionId).set(txMap).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to write transaction to Firestore", e)
      Result.failure(e)
    }
  }
}
