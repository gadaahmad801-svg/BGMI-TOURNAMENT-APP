package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaDanger
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun EditProfileScreen(
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val user = currentUser ?: return

  var name by remember { mutableStateOf<String>(user.displayName) }
  var bgmiName by remember { mutableStateOf<String>(user.bgmiName) }
  var bgmiUid by remember { mutableStateOf<String>(user.bgmiUid) }
  var errorMsg by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(16.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(ArenaCardBg)
          .border(BorderStroke(1.dp, ArenaBorder), CircleShape)
          .testTag("edit_profile_back")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ArenaTextPrimary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = "EDIT PROFILE",
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, ArenaBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        if (errorMsg != null) {
          Text(
            text = errorMsg!!,
            color = ArenaDanger,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Display Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ArenaCyan) },
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = bgmiName,
          onValueChange = { bgmiName = it },
          label = { Text("BGMI In-Game Name") },
          leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = null, tint = ArenaCyan) },
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = bgmiUid,
          onValueChange = { if (it.all { c -> c.isDigit() }) bgmiUid = it },
          label = { Text("BGMI Character UID (Digits)") },
          leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = ArenaCyan) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = arenaTextFieldColors(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = user.email,
          onValueChange = {},
          label = { Text("Email (Locked)") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ArenaTextSecondary) },
          colors = arenaTextFieldColors(),
          enabled = false,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (name.isBlank() || bgmiName.isBlank() || bgmiUid.isBlank()) {
              errorMsg = "Please fill in all fields."
              return@Button
            }
            viewModel.updateProfile(
              name = name,
              bgmiName = bgmiName,
              bgmiUid = bgmiUid,
              onSuccess = onBack,
              onError = { errorMsg = it }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(ArenaPurple, ArenaCyan)))
            .testTag("save_profile_button")
        ) {
          Text("SAVE CHANGES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}
