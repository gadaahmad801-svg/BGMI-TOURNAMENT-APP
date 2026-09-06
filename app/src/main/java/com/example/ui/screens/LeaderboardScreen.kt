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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardEntry
import com.example.ui.components.EsportsTopHeader
import com.example.ui.components.LeaderboardRowItem
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaBronze
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaGold
import com.example.ui.theme.ArenaPurple
import com.example.ui.theme.ArenaSilver
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun LeaderboardScreen(
  viewModel: ArenaViewModel,
  onNavigateToNotifications: () -> Unit,
  onNavigateToWallet: () -> Unit,
  onNavigateToProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val leaderboard by viewModel.leaderboard.collectAsState()
  val leaderboardTab by viewModel.leaderboardTab.collectAsState()
  val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

  val tabs = listOf("GLOBAL", "TOURNAMENT", "SEASON")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
  ) {
    // Header
    EsportsTopHeader(
      userName = currentUser?.displayName ?: "WARRIOR",
      virtualCoins = currentUser?.virtualCoins ?: 5,
      unreadCount = unreadCount,
      onNotificationsClick = onNavigateToNotifications,
      onWalletClick = onNavigateToWallet,
      onProfileClick = onNavigateToProfile
    )

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Title
      item {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
          Text(
            text = "LEADERBOARD & RANKINGS",
            style = MaterialTheme.typography.titleLarge,
            color = ArenaTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Competitive rankings calculated via official placements & kill points",
            color = ArenaTextSecondary,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Tab Switcher
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(ArenaCardBg)
              .border(BorderStroke(1.dp, ArenaBorder), RoundedCornerShape(12.dp))
              .padding(4.dp)
          ) {
            tabs.forEachIndexed { index, tabName ->
              val isSelected = leaderboardTab == index
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) ArenaCyan else Color.Transparent)
                  .clickable { viewModel.setLeaderboardTab(index) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = tabName,
                  color = if (isSelected) ArenaBgDark else ArenaTextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }
        }
      }

      // PODIUM: Top 3 (2nd, 1st, 3rd)
      if (leaderboard.size >= 3) {
        item {
          val first = leaderboard.getOrNull(0)
          val second = leaderboard.getOrNull(1)
          val third = leaderboard.getOrNull(2)

          if (first != null && second != null && third != null) {
            EsportsPodiumSection(first = first, second = second, third = third)
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }

      // Rest of the ranks (#4 onwards)
      item {
        Text(
          text = "CHALLENGERS LINEUP",
          color = ArenaTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          modifier = Modifier.padding(vertical = 8.dp)
        )
      }

      val remainingEntries = if (leaderboard.size > 3) leaderboard.drop(3) else leaderboard
      items(remainingEntries, key = { it.rank }) { entry ->
        LeaderboardRowItem(entry = entry)
      }
    }
  }
}

@Composable
fun EsportsPodiumSection(
  first: LeaderboardEntry,
  second: LeaderboardEntry,
  third: LeaderboardEntry,
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(22.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
      ) {
        // 2nd Place Podium Pillar
        PodiumPillar(
          entry = second,
          rank = 2,
          pillarHeight = 90.dp,
          color = ArenaSilver,
          modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 1st Place Podium Pillar (Taller & Gold)
        PodiumPillar(
          entry = first,
          rank = 1,
          pillarHeight = 120.dp,
          color = ArenaGold,
          isChampion = true,
          modifier = Modifier.weight(1.1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 3rd Place Podium Pillar
        PodiumPillar(
          entry = third,
          rank = 3,
          pillarHeight = 75.dp,
          color = ArenaBronze,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun PodiumPillar(
  entry: LeaderboardEntry,
  rank: Int,
  pillarHeight: androidx.compose.ui.unit.Dp,
  color: Color,
  isChampion: Boolean = false,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    // Avatar / Icon
    Box(
      modifier = Modifier
        .size(if (isChampion) 52.dp else 44.dp)
        .clip(CircleShape)
        .background(color.copy(alpha = 0.2f))
        .border(BorderStroke(1.5.dp, color), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      if (isChampion) {
        Icon(Icons.Default.EmojiEvents, contentDescription = "Champion", tint = color, modifier = Modifier.size(24.dp))
      } else {
        Text(
          text = "#$rank",
          color = color,
          fontSize = 14.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = entry.teamOrPlayerName,
      color = ArenaTextPrimary,
      fontSize = if (isChampion) 12.sp else 11.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      textAlign = TextAlign.Center
    )

    Text(
      text = "${entry.totalPoints} PTS",
      color = ArenaCyan,
      fontSize = 11.sp,
      fontWeight = FontWeight.ExtraBold
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Pillar Block
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(pillarHeight)
        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
        .background(
          Brush.verticalGradient(
            listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.08f))
          )
        )
        .border(
          BorderStroke(1.dp, color.copy(alpha = 0.4f)),
          RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "#$rank",
        color = color,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
