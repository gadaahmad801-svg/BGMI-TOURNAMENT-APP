package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Tournament
import com.example.data.model.TournamentMode
import com.example.ui.components.EsportsTopHeader
import com.example.ui.components.OfflineBanner
import com.example.ui.components.TournamentCard
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBgSecondary
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

@Composable
fun HomeScreen(
  viewModel: ArenaViewModel,
  onNavigateToTournament: (String) -> Unit,
  onNavigateToMatchesWithTab: (Int) -> Unit,
  onNavigateToLeaderboard: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToWallet: () -> Unit,
  onNavigateToProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val filteredTournaments by viewModel.filteredTournaments.collectAsState()
  val selectedMode by viewModel.selectedMode.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val unreadCount by viewModel.unreadNotificationsCount.collectAsState()
  val isOffline by viewModel.isOffline.collectAsState()
  val announcements by viewModel.announcements.collectAsState()
  val leaderboard by viewModel.leaderboard.collectAsState()

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

    // Offline Banner
    OfflineBanner(
      isOffline = isOffline,
      onDismissOrRetry = { viewModel.toggleOfflineSimulation() }
    )

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 1. Featured Tournament Hero Banner
      item {
        FeaturedTournamentHeroBanner(
          onRegisterClick = {
            val featured = filteredTournaments.firstOrNull()
            if (featured != null) onNavigateToTournament(featured.id)
          }
        )
      }

      // 2. MY MATCHES Quick Stats Cards
      item {
        MyMatchesOverviewSection(
          onCardClick = { tabIndex -> onNavigateToMatchesWithTab(tabIndex) }
        )
      }

      // 3. Search & Tournament Categories (Chips)
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOURNAMENTS",
              style = MaterialTheme.typography.titleLarge,
              color = ArenaTextPrimary,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            )

            Text(
              text = "${filteredTournaments.size} ACTIVE",
              color = ArenaCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Search Bar
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by name, mode, or map...", color = ArenaTextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ArenaCyan) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = arenaTextFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("home_tournament_search")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Mode Chips: ALL, SOLO, DUO, SQUAD, TDM
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(TournamentMode.entries) { mode ->
              val isSelected = selectedMode == mode
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(
                    if (isSelected) {
                      Brush.horizontalGradient(listOf(ArenaPurple, ArenaCyan))
                    } else {
                      Brush.horizontalGradient(listOf(ArenaCardBg, ArenaCardBg))
                    }
                  )
                  .border(
                    BorderStroke(1.dp, if (isSelected) ArenaCyan else ArenaBorder),
                    RoundedCornerShape(20.dp)
                  )
                  .clickable { viewModel.setSelectedMode(mode) }
                  .padding(horizontal = 16.dp, vertical = 8.dp)
                  .testTag("filter_chip_${mode.name}")
              ) {
                Text(
                  text = mode.name,
                  color = if (isSelected) Color.White else ArenaTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // 4. Tournament Cards List
      if (filteredTournaments.isEmpty()) {
        item {
          EmptyTournamentState()
        }
      } else {
        items(filteredTournaments, key = { it.id }) { tournament ->
          TournamentCard(
            tournament = tournament,
            onClick = { onNavigateToTournament(tournament.id) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }
      }

      // 5. Announcements Section
      if (announcements.isNotEmpty()) {
        item {
          AnnouncementsCardSection(announcements = announcements)
        }
      }

      // 6. Leaderboard Preview Section
      item {
        LeaderboardPreviewCard(
          topEntries = leaderboard.take(3),
          onViewAll = onNavigateToLeaderboard
        )
      }
    }
  }
}

@Composable
fun FeaturedTournamentHeroBanner(
  onRegisterClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(22.dp),
    border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.35f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
    ) {
      // Background Image
      Image(
        painter = painterResource(id = R.drawable.img_hero_banner),
        contentDescription = "Featured Tournament Banner",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Gradient Overlays for readable text
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Transparent,
                ArenaBgDark.copy(alpha = 0.7f),
                ArenaBgDark.copy(alpha = 0.95f)
              )
            )
          )
      )

      // Content inside Hero Banner
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(ArenaCyan)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "FEATURED BATTLE",
              color = ArenaBgDark,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(ArenaSuccess.copy(alpha = 0.2f))
              .border(0.8.dp, ArenaSuccess, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "FREE ENTRY",
              color = ArenaSuccess,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "WEEKEND CLASH BATTLE ROYALE",
          color = ArenaTextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.ExtraBold
        )

        Text(
          text = "SQUAD • ERANGEL • REWARD: +100 COINS",
          color = ArenaCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onRegisterClick,
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(listOf(ArenaPurpleBright, ArenaCyan)))
            .testTag("hero_register_now_button")
        ) {
          Text(
            text = "REGISTER NOW",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun MyMatchesOverviewSection(
  onCardClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "MY MATCHES",
        style = MaterialTheme.typography.titleMedium,
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = "VIEW ALL",
        color = ArenaCyan,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable { onCardClick(0) }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Ongoing Card (Index 0)
      MatchSummaryCard(
        title = "ONGOING",
        count = "01",
        accentColor = ArenaCyan,
        onClick = { onCardClick(0) },
        modifier = Modifier.weight(1f)
      )

      // Upcoming Card (Index 1)
      MatchSummaryCard(
        title = "UPCOMING",
        count = "03",
        accentColor = ArenaPurpleBright,
        onClick = { onCardClick(1) },
        modifier = Modifier.weight(1f)
      )

      // Completed Card (Index 2)
      MatchSummaryCard(
        title = "COMPLETED",
        count = "12",
        accentColor = ArenaSuccess,
        onClick = { onCardClick(2) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
fun MatchSummaryCard(
  title: String,
  count: String,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = modifier
      .clickable { onClick() }
      .testTag("summary_card_$title")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 14.dp, horizontal = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.SportsEsports,
          contentDescription = title,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = count,
        color = ArenaTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold
      )

      Text(
        text = title,
        color = ArenaTextMuted,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
    }
  }
}

@Composable
fun AnnouncementsCardSection(
  announcements: List<com.example.data.model.Announcement>,
  modifier: Modifier = Modifier
) {
  val latest = announcements.firstOrNull() ?: return

  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardElevated),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, ArenaPurple.copy(alpha = 0.4f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(ArenaPurple.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Campaign,
          contentDescription = "Announcement",
          tint = ArenaPurpleBright,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = latest.title,
          color = ArenaTextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = latest.message,
          color = ArenaTextSecondary,
          fontSize = 11.sp,
          maxLines = 2
        )
      }
    }
  }
}

@Composable
fun LeaderboardPreviewCard(
  topEntries: List<com.example.data.model.LeaderboardEntry>,
  onViewAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy",
            tint = ArenaGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "LEADERBOARD PREVIEW",
            style = MaterialTheme.typography.titleMedium,
            color = ArenaTextPrimary,
            fontWeight = FontWeight.Bold
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onViewAll() }
        ) {
          Text(
            text = "VIEW FULL",
            color = ArenaCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "View all",
            tint = ArenaCyan,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Podium Top 3 Rows
      topEntries.forEach { entry ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "#${entry.rank}",
              color = if (entry.rank == 1) ArenaGold else ArenaTextMuted,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp,
              modifier = Modifier.width(28.dp)
            )
            Text(
              text = entry.teamOrPlayerName,
              color = ArenaTextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Text(
            text = "${entry.totalPoints} PTS",
            color = ArenaCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}

@Composable
fun EmptyTournamentState(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = Icons.Default.SportsEsports,
        contentDescription = "No tournaments",
        tint = ArenaTextMuted,
        modifier = Modifier.size(48.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "No Tournaments Found",
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
      )
      Text(
        text = "Try adjusting your search query or filter mode.",
        color = ArenaTextMuted,
        fontSize = 12.sp
      )
    }
  }
}
