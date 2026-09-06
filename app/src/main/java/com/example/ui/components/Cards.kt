package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinTransaction
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Tournament
import com.example.data.model.TournamentStatus
import com.example.data.model.TransactionType
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaBorderSubtle
import com.example.ui.theme.ArenaBronze
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaGold
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaPurpleBright
import com.example.ui.theme.ArenaSilver
import com.example.ui.theme.ArenaSuccess
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TournamentCard(
  tournament: Tournament,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("tournament_card_${tournament.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top header: Mode, Map and Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(ArenaPurple.copy(alpha = 0.25f))
              .border(BorderStroke(0.8.dp, ArenaPurpleBright.copy(alpha = 0.5f)), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = tournament.mode.name,
              color = ArenaPurpleBright,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Map",
              tint = ArenaTextMuted,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = tournament.map,
              color = ArenaTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        StatusBadge(status = tournament.status)
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Title
      Text(
        text = tournament.title,
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Progress bar for slots
      val progress = (tournament.registeredTeams.toFloat() / tournament.maxTeams.toFloat()).coerceIn(0f, 1f)
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = "Teams",
              tint = ArenaTextMuted,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${tournament.registeredTeams} / ${tournament.maxTeams} TEAMS",
              color = ArenaTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Text(
            text = "${(progress * 100).toInt()}% FULL",
            color = if (progress >= 0.9f) ArenaCyan else ArenaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = ArenaCyan,
          trackColor = ArenaCardElevated
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bottom Row: Entry Requirement, Reward & CTA
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "ENTRY",
            color = ArenaTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = tournament.entryRequirement,
            color = ArenaSuccess,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }

        Column {
          Text(
            text = "REWARD",
            color = ArenaTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "+${tournament.rewardCoins} COINS",
            color = ArenaCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }

        Button(
          onClick = onClick,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.horizontalGradient(
                listOf(ArenaPurple, ArenaCyan)
              )
            )
            .height(38.dp)
        ) {
          Text(
            text = if (tournament.status == TournamentStatus.REGISTRATION_OPEN) "JOIN NOW" else "VIEW",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun MatchCard(
  match: Match,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Live Countdown state
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

  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, if (match.status == MatchStatus.LIVE) ArenaCyan.copy(alpha = 0.5f) else ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("match_card_${match.matchId}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MATCH #${match.matchNumber} • ${match.mode.name}",
          color = ArenaCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        // Status tag
        val (badgeBg, badgeColor, badgeText) = when (match.status) {
          MatchStatus.LIVE -> Triple(ArenaCyan.copy(alpha = 0.2f), ArenaCyan, "LIVE NOW")
          MatchStatus.ROOM_PUBLISHED -> Triple(ArenaSuccess.copy(alpha = 0.2f), ArenaSuccess, "ROOM RELEASED")
          MatchStatus.COMPLETED -> Triple(ArenaTextMuted.copy(alpha = 0.2f), ArenaTextMuted, "RESULT READY")
          MatchStatus.ROOM_PENDING -> Triple(ArenaPurple.copy(alpha = 0.2f), ArenaPurpleBright, "ROOM PENDING")
          else -> Triple(ArenaCardElevated, ArenaTextSecondary, "SCHEDULED")
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = badgeText,
            color = badgeColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = match.tournamentTitle,
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Countdown / Status Info Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(ArenaCardElevated)
          .border(BorderStroke(1.dp, ArenaBorderSubtle), RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AccessTime,
              contentDescription = "Time",
              tint = ArenaCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (match.status == MatchStatus.LIVE) {
                "LIVE IN GAME"
              } else if (match.status == MatchStatus.COMPLETED) {
                "COMPLETED"
              } else {
                String.format(Locale.getDefault(), "STARTS IN %02d:%02d:%02d", hours, minutes, seconds)
              },
              color = ArenaTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }

          // Room status indicator
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (match.isRoomPublished) Icons.Default.LockOpen else Icons.Default.Lock,
              contentDescription = "Room Status",
              tint = if (match.isRoomPublished) ArenaSuccess else ArenaTextMuted,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (match.isRoomPublished) "ROOM OPEN" else "ROOM LOCKED",
              color = if (match.isRoomPublished) ArenaSuccess else ArenaTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun LeaderboardRowItem(
  entry: LeaderboardEntry,
  modifier: Modifier = Modifier
) {
  val (rankColor, rankBg) = when (entry.rank) {
    1 -> Pair(ArenaGold, ArenaGold.copy(alpha = 0.15f))
    2 -> Pair(ArenaSilver, ArenaSilver.copy(alpha = 0.15f))
    3 -> Pair(ArenaBronze, ArenaBronze.copy(alpha = 0.15f))
    else -> Pair(ArenaTextMuted, ArenaCardElevated)
  }

  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, if (entry.rank <= 3) rankColor.copy(alpha = 0.3f) else ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Rank Badge
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(rankBg),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "#${entry.rank}",
            color = rankColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = entry.teamOrPlayerName,
            color = ArenaTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${entry.kills} KILLS • ${entry.placementPts} PLACE PTS",
            color = ArenaTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Total Points Pill
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${entry.totalPoints}",
          color = ArenaCyan,
          fontSize = 16.sp,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = "POINTS",
          color = ArenaTextMuted,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun TransactionRowItem(
  transaction: CoinTransaction,
  modifier: Modifier = Modifier
) {
  val dateFormatted = remember(transaction.createdAt) {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(transaction.createdAt))
  }

  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ArenaCyan.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Transaction",
            tint = ArenaCyan,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = transaction.reason,
            color = ArenaTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = dateFormatted,
            color = ArenaTextMuted,
            fontSize = 11.sp
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "+${transaction.amount} COINS",
          color = ArenaSuccess,
          fontSize = 14.sp,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = "Bal: ${transaction.balanceAfter}",
          color = ArenaTextSecondary,
          fontSize = 10.sp
        )
      }
    }
  }
}
