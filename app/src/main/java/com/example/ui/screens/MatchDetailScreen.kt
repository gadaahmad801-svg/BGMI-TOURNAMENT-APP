package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchStatus
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaGold
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaSuccess
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MatchDetailScreen(
  matchId: String,
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val matches by viewModel.matches.collectAsState()
  val match = matches.find { it.matchId == matchId }

  if (match == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(ArenaBgDark),
      contentAlignment = Alignment.Center
    ) {
      Text("Match not found", color = ArenaTextPrimary)
    }
    return
  }

  // Live countdown timer
  var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
  LaunchedEffect(match.scheduledTime) {
    while (true) {
      delay(1000L)
      currentTime = System.currentTimeMillis()
    }
  }

  val diffMillis = (match.scheduledTime - currentTime).coerceAtLeast(0L)
  val hours = diffMillis / (1000 * 3600)
  val minutes = (diffMillis % (1000 * 3600)) / (1000 * 60)
  val seconds = (diffMillis % (1000 * 60)) / 1000

  fun copyToClipboard(text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    viewModel.showToast("$label copied to clipboard!")
  }

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
          .testTag("match_detail_back")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ArenaTextPrimary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = "MATCH ROOM & INTEL",
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Match Header Info
    Card(
      colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, ArenaBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "MATCH #${match.matchNumber} • ${match.mode.name}",
            color = ArenaCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )

          val (badgeBg, badgeColor, badgeText) = when (match.status) {
            MatchStatus.LIVE -> Triple(ArenaCyan.copy(alpha = 0.2f), ArenaCyan, "LIVE NOW")
            MatchStatus.ROOM_PUBLISHED -> Triple(ArenaSuccess.copy(alpha = 0.2f), ArenaSuccess, "ROOM RELEASED")
            MatchStatus.COMPLETED -> Triple(ArenaTextMuted.copy(alpha = 0.2f), ArenaTextMuted, "COMPLETED")
            else -> Triple(ArenaCardElevated, ArenaTextSecondary, "SCHEDULED")
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(badgeBg)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(badgeText, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = match.tournamentTitle,
          style = MaterialTheme.typography.titleLarge,
          color = ArenaTextPrimary,
          fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "MAP: ${match.map.uppercase()}",
          color = ArenaTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Timer Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArenaCardElevated)
            .padding(12.dp)
        ) {
          Icon(Icons.Default.AccessTime, contentDescription = null, tint = ArenaCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (match.status == MatchStatus.LIVE) "BATTLE CURRENTLY IN PROGRESS"
            else if (match.status == MatchStatus.COMPLETED) "OFFICIAL MATCH FINISHED"
            else String.format(Locale.getDefault(), "STARTS IN %02d:%02d:%02d", hours, minutes, seconds),
            color = ArenaTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECURE ROOM DETAILS SECTION
    Card(
      colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, if (match.isRoomPublished) ArenaCyan.copy(alpha = 0.6f) else ArenaBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (match.isRoomPublished) Icons.Default.LockOpen else Icons.Default.Lock,
              contentDescription = null,
              tint = if (match.isRoomPublished) ArenaSuccess else ArenaTextMuted,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CUSTOM ROOM ACCESS",
              color = ArenaTextPrimary,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          if (match.isRoomPublished) {
            Text("AUTHORIZED", color = ArenaSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (match.isRoomPublished) {
          // ROOM ID ROW
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(ArenaCardElevated)
              .padding(12.dp)
          ) {
            Text("ROOM ID", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = match.roomId,
                color = ArenaCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              )
              Button(
                onClick = { copyToClipboard(match.roomId, "Room ID") },
                colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_room_id_button")
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ArenaCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("COPY", color = ArenaCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // PASSWORD ROW
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(ArenaCardElevated)
              .padding(12.dp)
          ) {
            Text("ROOM PASSWORD", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = match.roomPassword,
                color = ArenaPurpleBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              )
              Button(
                onClick = { copyToClipboard(match.roomPassword, "Password") },
                colors = ButtonDefaults.buttonColors(containerColor = ArenaPurpleBright.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_room_password_button")
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ArenaPurpleBright, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("COPY", color = ArenaPurpleBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          val publishedTimeFormatted = remember(match.roomPublishedAt) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(match.roomPublishedAt))
          }
          Text(
            text = "Credentials released at $publishedTimeFormatted. Join the in-game room immediately.",
            color = ArenaTextSecondary,
            fontSize = 11.sp
          )
        } else {
          // UNPUBLISHED PLACEHOLDER
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(ArenaCardElevated)
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = ArenaTextMuted, modifier = Modifier.size(32.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "ROOM DETAILS NOT RELEASED YET",
                color = ArenaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Credentials will be automatically unlocked 15 minutes before the match start time.",
                color = ArenaTextMuted,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // OFFICIAL RESULTS TABLE IF COMPLETED
    if (match.status == MatchStatus.COMPLETED && match.results.isNotEmpty()) {
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ArenaGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("OFFICIAL MATCH STANDINGS", color = ArenaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          match.results.forEach { res ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "#${res.placement}",
                  color = if (res.placement == 1) ArenaGold else ArenaTextMuted,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  modifier = Modifier.width(28.dp)
                )
                Column {
                  Text(res.teamName, color = ArenaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  Text("${res.kills} Kills • ${res.placementPoints} Place Pts", color = ArenaTextSecondary, fontSize = 10.sp)
                }
              }

              Text("${res.totalPoints} PTS", color = ArenaCyan, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Announcements and Briefing
    if (match.announcements.isNotEmpty()) {
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = ArenaCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("MATCH BRIEFING", color = ArenaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          match.announcements.forEach { ann ->
            Text("• $ann", color = ArenaTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
          }
        }
      }
    }
  }
}
