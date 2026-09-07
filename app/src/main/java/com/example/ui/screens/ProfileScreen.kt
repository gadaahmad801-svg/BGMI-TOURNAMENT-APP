package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.EsportsTopHeader
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaDanger
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaSuccess
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun ProfileScreen(
  viewModel: ArenaViewModel,
  onEditProfile: () -> Unit,
  onNavigateToAdmin: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToSupport: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToWallet: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentUser by viewModel.currentUser.collectAsState()
  val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

  val user = currentUser ?: return

  fun copyUid() {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("BGMI UID", user.bgmiUid)
    clipboard.setPrimaryClip(clip)
    viewModel.showToast("BGMI UID copied: ${user.bgmiUid}")
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
  ) {
    // Header
    EsportsTopHeader(
      userName = user.displayName,
      virtualCoins = user.virtualCoins,
      unreadCount = unreadCount,
      onNotificationsClick = onNavigateToNotifications,
      onWalletClick = onNavigateToWallet,
      onProfileClick = {}
    )

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. User Identity & BGMI UID Card
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
          shape = RoundedCornerShape(22.dp),
          border = BorderStroke(1.dp, ArenaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Glowing Avatar Ring
            Box(
              modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(listOf(ArenaPurpleBright, ArenaCyan))
                )
                .padding(2.5.dp)
                .clip(CircleShape)
                .background(ArenaBgDark),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = user.displayName.take(2).uppercase(),
                color = ArenaCyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = user.displayName,
              style = MaterialTheme.typography.titleLarge,
              color = ArenaTextPrimary,
              fontWeight = FontWeight.Bold
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "IGN: ${user.bgmiName}",
                color = ArenaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )

              if (user.role == UserRole.ADMIN) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ArenaPurple.copy(alpha = 0.3f))
                    .border(0.8.dp, ArenaPurpleBright, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("ADMIN", color = ArenaPurpleBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BGMI UID Copy Row
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(ArenaCardElevated)
                .border(BorderStroke(1.dp, ArenaBorder), RoundedCornerShape(12.dp))
                .clickable { copyUid() }
                .padding(horizontal = 14.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "UID: ${user.bgmiUid}",
                color = ArenaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy UID",
                tint = ArenaCyan,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      // 2. Career Performance Stats
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
          shape = RoundedCornerShape(20.dp),
          border = BorderStroke(1.dp, ArenaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "CAREER BATTLE INTEL",
              style = MaterialTheme.typography.titleMedium,
              color = ArenaTextPrimary,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4-quadrant metrics
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ProfileStatCard(title = "MATCHES", value = "${user.stats.matchesPlayed}", modifier = Modifier.weight(1f))
              ProfileStatCard(title = "WINS", value = "${user.stats.wins}", accentColor = ArenaSuccess, modifier = Modifier.weight(1f))
              ProfileStatCard(title = "TOTAL KILLS", value = "${user.stats.kills}", accentColor = ArenaCyan, modifier = Modifier.weight(1f))
              ProfileStatCard(title = "POINTS", value = "${user.stats.points}", accentColor = ArenaPurpleBright, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Additional performance bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ArenaCardElevated)
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("WIN RATE", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                val winRate = if (user.stats.matchesPlayed > 0) (user.stats.wins * 100 / user.stats.matchesPlayed) else 0
                Text("$winRate%", color = ArenaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CURRENT RANK", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(user.stats.currentRank, color = ArenaCyan, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("WIN STREAK", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("${user.stats.currentStreak} STREAK", color = ArenaPurpleBright, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
              }
            }
          }
        }
      }

      // 3. Action Menu Options
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
          shape = RoundedCornerShape(20.dp),
          border = BorderStroke(1.dp, ArenaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            ProfileMenuRow(
              icon = Icons.Default.Edit,
              label = "EDIT PROFILE",
              subtitle = "Update in-game name and character UID",
              onClick = onEditProfile
            )

            // Admin Panel Access (Visible to Admins)
            if (user.role == UserRole.ADMIN) {
              ProfileMenuRow(
                icon = Icons.Default.AdminPanelSettings,
                label = "ADMIN CONTROL CENTER",
                subtitle = "Manage tournaments, rooms, users & results",
                accentColor = ArenaPurpleBright,
                onClick = onNavigateToAdmin
              )
            }

            ProfileMenuRow(
              icon = Icons.Default.Settings,
              label = "APP SETTINGS & POLICIES",
              subtitle = "Notifications, fair play & disclosures",
              onClick = onNavigateToSettings
            )

            ProfileMenuRow(
              icon = Icons.Default.HelpOutline,
              label = "HELP & SUPPORT",
              subtitle = "FAQs, rules & support tickets",
              onClick = onNavigateToSupport
            )

            ProfileMenuRow(
              icon = Icons.Default.Logout,
              label = "LOG OUT",
              subtitle = "Safely end current session",
              accentColor = ArenaDanger,
              onClick = onLogout
            )
          }
        }
      }
    }
  }
}

@Composable
fun ProfileStatCard(
  title: String,
  value: String,
  accentColor: Color = ArenaTextPrimary,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(ArenaCardElevated)
      .padding(vertical = 10.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = value,
        color = accentColor,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = title,
        color = ArenaTextMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
    }
  }
}

@Composable
fun ProfileMenuRow(
  icon: ImageVector,
  label: String,
  subtitle: String,
  accentColor: Color = ArenaTextPrimary,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = label, tint = accentColor, modifier = Modifier.size(20.dp))
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(text = label, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
      Text(text = subtitle, color = ArenaTextSecondary, fontSize = 11.sp)
    }
  }
}
