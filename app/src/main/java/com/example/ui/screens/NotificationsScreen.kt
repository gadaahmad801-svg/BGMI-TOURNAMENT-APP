package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaSuccess
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val notifications by viewModel.notifications.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(ArenaCardBg)
            .border(BorderStroke(1.dp, ArenaBorder), CircleShape)
            .testTag("notifications_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = ArenaTextPrimary
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = "NOTIFICATIONS",
          style = MaterialTheme.typography.titleMedium,
          color = ArenaTextPrimary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      // Mark all as read button
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clickable { viewModel.markAllNotificationsAsRead() }
          .padding(8.dp)
          .testTag("mark_all_read_button")
      ) {
        Icon(
          imageVector = Icons.Default.DoneAll,
          contentDescription = "Read All",
          tint = ArenaCyan,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "READ ALL",
          color = ArenaCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    if (notifications.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = null,
            tint = ArenaTextMuted,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No Notifications",
            color = ArenaTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Tournament and room updates will be listed here.",
            color = ArenaTextMuted,
            fontSize = 12.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(notifications, key = { it.id }) { notif ->
          NotificationRowItem(
            notification = notif,
            onClick = { viewModel.markNotificationAsRead(notif.id) }
          )
        }
      }
    }
  }
}

@Composable
fun NotificationRowItem(
  notification: NotificationItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormatted = remember(notification.createdAt) {
    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notification.createdAt))
  }

  val (iconVector, iconTint) = when (notification.type) {
    com.example.data.model.NotificationType.MATCH_REMINDER,
    com.example.data.model.NotificationType.ROOM_RELEASED -> Pair(Icons.Default.SportsEsports, ArenaCyan)
    com.example.data.model.NotificationType.TOURNAMENT_OPEN,
    com.example.data.model.NotificationType.RESULT_PUBLISHED -> Pair(Icons.Default.Shield, ArenaPurpleBright)
    com.example.data.model.NotificationType.REWARD_RECEIVED -> Pair(Icons.Default.EmojiEvents, ArenaSuccess)
    else -> Pair(Icons.Default.Campaign, ArenaCyan)
  }

  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (!notification.read) ArenaCardBg else ArenaBgDark
    ),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, if (!notification.read) ArenaCyan.copy(alpha = 0.4f) else ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(iconTint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = notification.title,
            color = ArenaTextPrimary,
            fontSize = 13.sp,
            fontWeight = if (!notification.read) FontWeight.Bold else FontWeight.Medium
          )

          if (!notification.read) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ArenaCyan)
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = notification.body,
          color = ArenaTextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = dateFormatted,
          color = ArenaTextMuted,
          fontSize = 10.sp
        )
      }
    }
  }
}
