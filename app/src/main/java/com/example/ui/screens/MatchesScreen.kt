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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchStatus
import com.example.ui.components.EsportsTopHeader
import com.example.ui.components.MatchCard
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.viewmodel.ArenaViewModel

@Composable
fun MatchesScreen(
  viewModel: ArenaViewModel,
  onNavigateToMatchDetail: (String) -> Unit,
  onNavigateToTournaments: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToWallet: () -> Unit,
  onNavigateToProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val matches by viewModel.matches.collectAsState()
  val matchesTab by viewModel.matchesTab.collectAsState()
  val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

  val tabs = listOf("ONGOING", "UPCOMING", "COMPLETED")

  val filteredMatches = when (matchesTab) {
    0 -> matches.filter { it.status == MatchStatus.LIVE }
    1 -> matches.filter { it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.ROOM_PENDING || it.status == MatchStatus.ROOM_PUBLISHED }
    else -> matches.filter { it.status == MatchStatus.COMPLETED }
  }

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

    // Screen Title
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
      Text(
        text = "MY MATCHES",
        style = MaterialTheme.typography.titleLarge,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )
      Text(
        text = "Track your registered rooms, live battles & results",
        color = ArenaTextSecondary,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 3 Custom Tabs: ONGOING, UPCOMING, COMPLETED
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(ArenaCardBg)
          .border(BorderStroke(1.dp, ArenaBorder), RoundedCornerShape(12.dp))
          .padding(4.dp)
      ) {
        tabs.forEachIndexed { index, tabName ->
          val isSelected = matchesTab == index
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) ArenaCyan else Color.Transparent)
              .clickable { viewModel.setMatchesTab(index) }
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

    // Matches List or Empty State
    if (filteredMatches.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = "No matches",
            tint = ArenaTextMuted,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No ${tabs[matchesTab]} Matches",
            color = ArenaTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Text(
            text = "Your registered matches will appear here.",
            color = ArenaTextMuted,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onNavigateToTournaments,
            colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("explore_tournaments_empty_button")
          ) {
            Text("EXPLORE TOURNAMENTS", color = ArenaBgDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredMatches, key = { it.matchId }) { match ->
          MatchCard(
            match = match,
            onClick = { onNavigateToMatchDetail(match.matchId) }
          )
        }
      }
    }
  }
}
