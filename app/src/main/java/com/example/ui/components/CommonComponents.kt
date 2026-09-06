package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentStatus
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBgSecondary
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaCyanSecondary
import com.example.ui.theme.ArenaDanger
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaSuccess
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.ArenaWarning

@Composable
fun EsportsTopHeader(
  userName: String,
  virtualCoins: Int,
  unreadCount: Int,
  onNotificationsClick: () -> Unit,
  onWalletClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = ArenaBgDark,
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Logo and Welcome text
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clickable { onProfileClick() }
          .testTag("header_profile_target")
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(
                listOf(ArenaPurple, ArenaCyan)
              )
            )
            .padding(1.5.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(ArenaBgSecondary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "BGMI Arena Logo",
            tint = ArenaCyan,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "BGMI ARENA",
            style = MaterialTheme.typography.labelSmall,
            color = ArenaCyan,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "WELCOME, ${userName.uppercase()}",
            style = MaterialTheme.typography.titleMedium,
            color = ArenaTextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Actions: Bell & Coins Pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Notification bell with unread badge
        BadgedBox(
          badge = {
            if (unreadCount > 0) {
              Badge(
                containerColor = ArenaCyan,
                contentColor = ArenaBgDark,
                modifier = Modifier.size(16.dp)
              ) {
                Text(
                  text = unreadCount.toString(),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        ) {
          IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(ArenaCardBg)
              .border(BorderStroke(1.dp, ArenaBorder), RoundedCornerShape(12.dp))
              .testTag("bell_notifications_button")
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications",
              tint = if (unreadCount > 0) ArenaCyan else ArenaTextSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Virtual Coin Balance Pill (clickable -> Wallet)
        VirtualCoinPill(
          coins = virtualCoins,
          onClick = onWalletClick,
          modifier = Modifier.testTag("coin_balance_pill")
        )
      }
    }
  }
}

@Composable
fun VirtualCoinPill(
  coins: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(
        Brush.horizontalGradient(
          listOf(Color(0xFF131D31), Color(0xFF10182A))
        )
      )
      .border(BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.4f)), RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 7.dp)
  ) {
    // Custom non-cash virtual coin glyph
    Box(
      modifier = Modifier
        .size(18.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(Color(0xFFFFD700), Color(0xFFFFA000))
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "C",
        color = ArenaBgDark,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.width(6.dp))

    Text(
      text = "$coins COINS",
      style = MaterialTheme.typography.labelMedium,
      color = ArenaTextPrimary,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun EsportsBottomNavigation(
  currentRoute: String,
  onNavigate: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavigationTabItem("home", "HOME", Icons.Default.Home),
    NavigationTabItem("matches", "MATCHES", Icons.Default.SportsEsports),
    NavigationTabItem("leaderboard", "LEADERBOARD", Icons.Default.EmojiEvents),
    NavigationTabItem("profile", "PROFILE", Icons.Default.Person)
  )

  NavigationBar(
    containerColor = ArenaBgSecondary,
    contentColor = ArenaTextSecondary,
    tonalElevation = 8.dp,
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .border(BorderStroke(1.dp, ArenaBorder))
  ) {
    items.forEach { item ->
      val selected = currentRoute == item.route
      NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(item.route) },
        icon = {
          Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = ArenaCyan,
          selectedTextColor = ArenaCyan,
          indicatorColor = ArenaCyan.copy(alpha = 0.15f),
          unselectedIconColor = ArenaTextMuted,
          unselectedTextColor = ArenaTextMuted
        ),
        modifier = Modifier.testTag("nav_${item.route}")
      )
    }
  }
}

data class NavigationTabItem(
  val route: String,
  val label: String,
  val icon: ImageVector
)

@Composable
fun StatusBadge(
  status: TournamentStatus,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, label) = when (status) {
    TournamentStatus.REGISTRATION_OPEN -> Triple(ArenaSuccess.copy(alpha = 0.15f), ArenaSuccess, "REGISTRATION OPEN")
    TournamentStatus.UPCOMING -> Triple(ArenaCyan.copy(alpha = 0.15f), ArenaCyan, "UPCOMING")
    TournamentStatus.FULL -> Triple(ArenaWarning.copy(alpha = 0.15f), ArenaWarning, "FULL")
    TournamentStatus.LIVE -> Triple(ArenaCyan.copy(alpha = 0.2f), ArenaCyan, "LIVE NOW")
    TournamentStatus.COMPLETED -> Triple(ArenaTextMuted.copy(alpha = 0.15f), ArenaTextMuted, "COMPLETED")
    TournamentStatus.CANCELLED -> Triple(ArenaDanger.copy(alpha = 0.15f), ArenaDanger, "CANCELLED")
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .border(BorderStroke(0.8.dp, textColor.copy(alpha = 0.4f)), RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    if (status == TournamentStatus.LIVE) {
      Icon(
        imageVector = Icons.Default.Circle,
        contentDescription = "Live",
        tint = ArenaCyan,
        modifier = Modifier.size(8.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
    }
    Text(
      text = label,
      color = textColor,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun OfflineBanner(
  isOffline: Boolean,
  onDismissOrRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = isOffline,
    enter = slideInVertically() + fadeIn(),
    exit = slideOutVertically() + fadeOut()
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .background(ArenaWarning.copy(alpha = 0.9f))
        .padding(horizontal = 16.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.WifiOff,
          contentDescription = "Offline",
          tint = ArenaBgDark,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "You're offline. Live updates paused.",
          color = ArenaBgDark,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "RETRY",
        color = ArenaBgDark,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
          .clickable { onDismissOrRetry() }
          .padding(4.dp)
      )
    }
  }
}

@Composable
fun ToastNotification(
  message: String?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = message != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    if (message != null) {
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardElevated),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.5f)),
        modifier = Modifier
          .padding(horizontal = 20.dp, vertical = 10.dp)
          .shadow(8.dp, RoundedCornerShape(12.dp))
          .clickable { onDismiss() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(ArenaCyan)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = message,
            color = ArenaTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
