package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBgSecondary
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaDanger
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun AuthScreen(
  viewModel: ArenaViewModel,
  onAuthSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isRegisterMode by remember { mutableStateOf(false) }

  // Form Fields
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var bgmiName by remember { mutableStateOf("") }
  var bgmiUid by remember { mutableStateOf("") }

  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  var showForgotPasswordDialog by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Header Branding
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(ArenaPurple, ArenaCyan)
            )
          )
          .padding(2.dp)
          .clip(CircleShape)
          .background(ArenaBgSecondary),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_app_icon),
          contentDescription = "Logo",
          modifier = Modifier.size(52.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "BGMI ARENA",
        style = MaterialTheme.typography.headlineMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )

      Text(
        text = "YOUR BATTLEGROUND. YOUR RANK.",
        style = MaterialTheme.typography.labelSmall,
        color = ArenaCyan,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Tab Switcher: LOGIN vs REGISTER
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(ArenaCardBg)
          .border(BorderStroke(1.dp, ArenaBorder), RoundedCornerShape(14.dp))
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (!isRegisterMode) ArenaCyan else Color.Transparent)
            .clickable {
              isRegisterMode = false
              errorMessage = null
            }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "LOGIN",
            color = if (!isRegisterMode) ArenaBgDark else ArenaTextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isRegisterMode) ArenaCyan else Color.Transparent)
            .clickable {
              isRegisterMode = true
              errorMessage = null
            }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "CREATE ACCOUNT",
            color = if (isRegisterMode) ArenaBgDark else ArenaTextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Error message callout
      if (errorMessage != null) {
        Card(
          colors = CardDefaults.cardColors(containerColor = ArenaDanger.copy(alpha = 0.15f)),
          border = BorderStroke(1.dp, ArenaDanger.copy(alpha = 0.4f)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Text(
            text = errorMessage!!,
            color = ArenaDanger,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      // Fields
      if (isRegisterMode) {
        // Full Name
        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("Full Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ArenaCyan) },
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_full_name")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // BGMI In-Game Name
        OutlinedTextField(
          value = bgmiName,
          onValueChange = { bgmiName = it },
          label = { Text("BGMI In-Game Name") },
          leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = null, tint = ArenaCyan) },
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_bgmi_name")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // BGMI UID
        OutlinedTextField(
          value = bgmiUid,
          onValueChange = { if (it.all { c -> c.isDigit() }) bgmiUid = it },
          label = { Text("BGMI Character UID (Digits only)") },
          leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = ArenaCyan) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_bgmi_uid")
        )

        Spacer(modifier = Modifier.height(10.dp))
      }

      // Email
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email Address") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ArenaCyan) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = arenaTextFieldColors(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_email")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Password
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ArenaCyan) },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
              contentDescription = "Toggle password",
              tint = ArenaTextMuted
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = arenaTextFieldColors(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_password")
      )

      if (isRegisterMode) {
        Spacer(modifier = Modifier.height(10.dp))

        // Confirm Password
        OutlinedTextField(
          value = confirmPassword,
          onValueChange = { confirmPassword = it },
          label = { Text("Confirm Password") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ArenaCyan) },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_confirm_password")
        )
      } else {
        // Forgot Password Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.End
        ) {
          Text(
            text = "Forgot Password?",
            color = ArenaCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
              .clickable { showForgotPasswordDialog = true }
              .padding(4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Submit Button
      Button(
        onClick = {
          errorMessage = null
          isLoading = true
          if (isRegisterMode) {
            viewModel.register(
              name = fullName,
              email = email,
              pass = password,
              confirmPass = confirmPassword,
              bgmiName = bgmiName,
              bgmiUid = bgmiUid,
              onSuccess = {
                isLoading = false
                onAuthSuccess()
              },
              onError = {
                isLoading = false
                errorMessage = it
              }
            )
          } else {
            viewModel.login(
              email = email,
              pass = password,
              onSuccess = {
                isLoading = false
                onAuthSuccess()
              },
              onError = {
                isLoading = false
                errorMessage = it
              }
            )
          }
        },
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Brush.horizontalGradient(listOf(ArenaPurple, ArenaCyan)))
          .testTag("auth_submit_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(color = ArenaBgDark, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
          Text(
            text = if (isRegisterMode) "CREATE ACCOUNT" else "LOGIN TO ARENA",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      val context = LocalContext.current

      // Google Sign-In via Credential Manager & Firebase Auth
      Button(
        onClick = {
          errorMessage = null
          isLoading = true
          viewModel.signInWithGoogle(
            context = context,
            onSuccess = {
              isLoading = false
              onAuthSuccess()
            },
            onError = {
              isLoading = false
              errorMessage = it
            }
          )
        },
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = ArenaCardElevated),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("google_sign_in_button")
      ) {
        Text(
          text = "G   Sign in with Google",
          color = ArenaTextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Quick Demo Access Shortcuts for Easy Testing
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "DEMO ACCOUNTS (ONE-TAP ACCESS)",
            color = ArenaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                viewModel.login("ahmed@example.com", "user123", onAuthSuccess) { errorMessage = it }
              },
              colors = ButtonDefaults.buttonColors(containerColor = ArenaCardElevated),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Player: Ahmed", fontSize = 11.sp, color = ArenaCyan, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                viewModel.login("admin@bgmiarena.com", "admin123", onAuthSuccess) { errorMessage = it }
              },
              colors = ButtonDefaults.buttonColors(containerColor = ArenaCardElevated),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Admin Control", fontSize = 11.sp, color = ArenaPurpleBright, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "By continuing, you agree to Fair Play Guidelines & Platform Rules.",
        color = ArenaTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }

  // Forgot Password Dialog
  if (showForgotPasswordDialog) {
    var resetEmail by remember { mutableStateOf(email) }
    var resetSent by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { showForgotPasswordDialog = false },
      containerColor = ArenaCardElevated,
      title = { Text("Reset Password", color = ArenaTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "Enter your registered email address to receive password reset instructions.",
            color = ArenaTextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = resetEmail,
            onValueChange = { resetEmail = it },
            label = { Text("Email Address") },
            colors = arenaTextFieldColors(),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          if (resetSent) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Reset instructions sent to $resetEmail!", color = ArenaCyan, fontSize = 12.sp)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (resetEmail.contains("@")) {
              resetSent = true
              viewModel.showToast("Reset instructions sent.")
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan)
        ) {
          Text("SEND RESET LINK", color = ArenaBgDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showForgotPasswordDialog = false }) {
          Text("CLOSE", color = ArenaTextSecondary)
        }
      }
    )
  }
}

@Composable
fun arenaTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = ArenaTextPrimary,
  unfocusedTextColor = ArenaTextPrimary,
  focusedContainerColor = ArenaCardBg,
  unfocusedContainerColor = ArenaCardBg,
  focusedBorderColor = ArenaCyan,
  unfocusedBorderColor = ArenaBorder,
  focusedLabelColor = ArenaCyan,
  unfocusedLabelColor = ArenaTextSecondary,
  cursorColor = ArenaCyan
)
