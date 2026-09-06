package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun SettingsScreen(
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val prefs = currentUser?.preferences

  var notificationsEnabled by remember { mutableStateOf(prefs?.notifications ?: true) }
  var soundEffectsEnabled by remember { mutableStateOf(prefs?.sound ?: true) }
  var reducedMotion by remember { mutableStateOf(prefs?.reducedMotion ?: false) }

  var activeDialogTitle by remember { mutableStateOf<String?>(null) }
  var activeDialogContent by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
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
          .testTag("settings_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ArenaTextPrimary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = "SETTINGS & POLICIES",
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Preferences Section
    Text(
      text = "PREFERENCES",
      color = ArenaTextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, ArenaBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Notifications
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Match Notifications", color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Receive Room ID & password alerts", color = ArenaTextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = notificationsEnabled,
            onCheckedChange = {
              notificationsEnabled = it
              viewModel.updatePreferences(it, soundEffectsEnabled, reducedMotion)
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = ArenaCyan,
              checkedTrackColor = ArenaCyan.copy(alpha = 0.3f),
              uncheckedThumbColor = ArenaTextMuted,
              uncheckedTrackColor = ArenaCardElevated
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sound Effects
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Sound Effects", color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Countdown and victory audio cues", color = ArenaTextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = soundEffectsEnabled,
            onCheckedChange = {
              soundEffectsEnabled = it
              viewModel.updatePreferences(notificationsEnabled, it, reducedMotion)
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = ArenaCyan,
              checkedTrackColor = ArenaCyan.copy(alpha = 0.3f),
              uncheckedThumbColor = ArenaTextMuted,
              uncheckedTrackColor = ArenaCardElevated
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Theme note
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Appearance", color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Esports Dark Canvas (Always Active)", color = ArenaCyan, fontSize = 11.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Policies Section
    Text(
      text = "LEGAL & COMPLIANCE POLICIES",
      color = ArenaTextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, ArenaBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(8.dp)) {
        PolicyClickableRow(
          title = "Fair Play & Anti-Cheat Policy",
          onClick = {
            activeDialogTitle = "Fair Play & Anti-Cheat Policy"
            activeDialogContent = "BGMI ARENA strictly prohibits hacks, scripts, emulators on mobile tournaments, teaming, and toxic behavior. Violators receive permanent account bans and forfeit all standings."
          }
        )

        PolicyClickableRow(
          title = "Virtual Coin Non-Cash Policy",
          onClick = {
            activeDialogTitle = "Virtual Coin Non-Cash Policy"
            activeDialogContent = "Virtual coins in BGMI ARENA are purely cosmetic, non-monetary tournament scores and tokens. They have no monetary exchange value and cannot be withdrawn, redeemed for real money, or transferred for real value."
          }
        )

        PolicyClickableRow(
          title = "Terms of Service",
          onClick = {
            activeDialogTitle = "Terms of Service"
            activeDialogContent = "By participating in BGMI ARENA, all users agree to adhere to tournament schedules, verify their BGMI character UID accurately, and respect referees and fellow competitors."
          }
        )

        PolicyClickableRow(
          title = "Privacy Policy",
          onClick = {
            activeDialogTitle = "Privacy Policy"
            activeDialogContent = "We collect only necessary account identification (email, display name, and BGMI UID) to administer tournaments and publish official standings. Data is never sold to third parties."
          }
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("BGMI ARENA • ESPORTS PLATFORM", color = ArenaCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Text("Version 1.0.0 (Production Build)", color = ArenaTextMuted, fontSize = 11.sp)
    }
  }

  // Policy Modal Dialog
  if (activeDialogTitle != null && activeDialogContent != null) {
    AlertDialog(
      onDismissRequest = {
        activeDialogTitle = null
        activeDialogContent = null
      },
      containerColor = ArenaCardElevated,
      shape = RoundedCornerShape(18.dp),
      title = { Text(activeDialogTitle!!, color = ArenaTextPrimary, fontWeight = FontWeight.Bold) },
      text = { Text(activeDialogContent!!, color = ArenaTextSecondary, fontSize = 13.sp, lineHeight = 18.sp) },
      confirmButton = {
        TextButton(
          onClick = {
            activeDialogTitle = null
            activeDialogContent = null
          }
        ) {
          Text("I UNDERSTAND", color = ArenaCyan, fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}

@Composable
fun PolicyClickableRow(
  title: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(title, color = ArenaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = ArenaTextMuted,
      modifier = Modifier.size(18.dp)
    )
  }
}
