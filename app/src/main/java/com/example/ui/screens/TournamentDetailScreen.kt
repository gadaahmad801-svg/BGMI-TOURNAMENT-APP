package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Team
import com.example.data.model.TeamPlayer
import com.example.data.model.TournamentMode
import com.example.data.model.TournamentStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBgSecondary
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
import com.example.ui.theme.ArenaWarning
import com.example.ui.viewmodel.ArenaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TournamentDetailScreen(
  tournamentId: String,
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  onNavigateToMatches: () -> Unit,
  modifier: Modifier = Modifier
) {
  val tournaments by viewModel.tournaments.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val tournament = tournaments.find { it.id == tournamentId }

  var showRegistrationModal by remember { mutableStateOf(false) }
  var registeredTeamResult by remember { mutableStateOf<Team?>(null) }

  val scrollState = rememberScrollState()

  if (tournament == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(ArenaBgDark),
      contentAlignment = Alignment.Center
    ) {
      Text("Tournament not found", color = ArenaTextPrimary)
    }
    return
  }

  val startDateFormatted = remember(tournament.startAt) {
    SimpleDateFormat("EEEE, dd MMM • hh:mm a", Locale.getDefault()).format(Date(tournament.startAt))
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ArenaBgDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .statusBarsPadding()
        .padding(bottom = 90.dp)
    ) {
      // Top Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(ArenaCardBg)
            .border(BorderStroke(1.dp, ArenaBorder), CircleShape)
            .testTag("detail_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = ArenaTextPrimary
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = "TOURNAMENT DETAILS",
          style = MaterialTheme.typography.titleMedium,
          color = ArenaTextPrimary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      // Title & Badges
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(ArenaPurple.copy(alpha = 0.25f))
              .border(0.8.dp, ArenaPurpleBright, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = tournament.mode.name,
              color = ArenaPurpleBright,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          StatusBadge(status = tournament.status)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = tournament.title,
          style = MaterialTheme.typography.headlineMedium,
          color = ArenaTextPrimary,
          fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = tournament.description,
          style = MaterialTheme.typography.bodyMedium,
          color = ArenaTextSecondary
        )
      }

      // Meta Grid Cards: Schedule, Map, Entry, Reward
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Date & Time
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = "Date",
              tint = ArenaCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("SCHEDULED DATE & TIME", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text(startDateFormatted, color = ArenaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Map
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Map",
              tint = ArenaCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("MAP", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text(tournament.map, color = ArenaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Entry & Reward
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("ENTRY REQUIREMENT", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text(tournament.entryRequirement, color = ArenaSuccess, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }

            Column(horizontalAlignment = Alignment.End) {
              Text("PRIZE POOL", color = ArenaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("+${tournament.rewardCoins} VIRTUAL COINS", color = ArenaCyan, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
          }
        }
      }

      // Slots Capacity
      val progress = (tournament.registeredTeams.toFloat() / tournament.maxTeams.toFloat()).coerceIn(0f, 1f)
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "Slots",
                tint = ArenaCyan,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${tournament.registeredTeams} / ${tournament.maxTeams} SLOTS FILLED",
                color = ArenaTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }

            Text(
              text = "${(progress * 100).toInt()}%",
              color = ArenaCyan,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = ArenaCyan,
            trackColor = ArenaCardElevated
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Tournament Rules Section
      Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Rules",
              tint = ArenaCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "TOURNAMENT RULES & FAIR PLAY",
              color = ArenaTextPrimary,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          tournament.rules.forEachIndexed { index, rule ->
            Row(
              modifier = Modifier.padding(vertical = 4.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text(
                text = "${index + 1}.",
                color = ArenaCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.width(20.dp)
              )
              Text(
                text = rule,
                color = ArenaTextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
              )
            }
          }
        }
      }
    }

    // Sticky Bottom Action CTA
    Surface(
      color = ArenaBgDark,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding()
        .border(BorderStroke(1.dp, ArenaBorder))
        .padding(16.dp)
    ) {
      val isRegistrationOpen = tournament.status == TournamentStatus.REGISTRATION_OPEN
      val isFull = tournament.status == TournamentStatus.FULL
      val isLive = tournament.status == TournamentStatus.LIVE
      val isCompleted = tournament.status == TournamentStatus.COMPLETED

      Button(
        onClick = {
          if (isRegistrationOpen) {
            showRegistrationModal = true
          } else if (isLive || isCompleted) {
            onNavigateToMatches()
          }
        },
        enabled = isRegistrationOpen || isLive || isCompleted,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(
            if (isRegistrationOpen) {
              Brush.horizontalGradient(listOf(ArenaPurpleBright, ArenaCyan))
            } else if (isLive) {
              Brush.horizontalGradient(listOf(ArenaCyan, ArenaCyan.copy(alpha = 0.7f)))
            } else {
              Brush.horizontalGradient(listOf(ArenaCardElevated, ArenaCardElevated))
            }
          )
          .testTag("tournament_cta_button")
      ) {
        Text(
          text = when {
            isRegistrationOpen -> "JOIN TOURNAMENT (FREE ENTRY)"
            isFull -> "TOURNAMENT FULL"
            isLive -> "VIEW LIVE MATCH"
            isCompleted -> "VIEW RESULTS"
            else -> "REGISTRATION CLOSED"
          },
          color = Color.White,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 14.sp,
          letterSpacing = 0.5.sp
        )
      }
    }
  }

  // Registration Dialog
  if (showRegistrationModal) {
    TournamentRegistrationDialog(
      tournament = tournament,
      currentUserUid = currentUser?.uid ?: "",
      currentUserName = currentUser?.displayName ?: "",
      currentUserBgmiUid = currentUser?.bgmiUid ?: "",
      onDismiss = { showRegistrationModal = false },
      onSubmit = { teamName, players ->
        viewModel.registerTournament(
          tournamentId = tournament.id,
          teamName = teamName,
          players = players,
          onSuccess = { team ->
            showRegistrationModal = false
            registeredTeamResult = team
          },
          onError = { errorMsg ->
            viewModel.showToast(errorMsg)
          }
        )
      }
    )
  }

  // Success Confirmation Dialog
  if (registeredTeamResult != null) {
    val team = registeredTeamResult!!
    AlertDialog(
      onDismissRequest = { registeredTeamResult = null },
      containerColor = ArenaCardElevated,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ArenaSuccess)
          Spacer(modifier = Modifier.width(8.dp))
          Text("YOU'RE REGISTERED!", color = ArenaTextPrimary, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column {
          Text("Squad '${team.teamName}' has been successfully registered for ${tournament.title}.", color = ArenaTextSecondary)
          Spacer(modifier = Modifier.height(10.dp))
          Text("• Match starts on schedule.", color = ArenaTextMuted, fontSize = 12.sp)
          Text("• Room ID and Password will be posted 15 mins before start.", color = ArenaTextMuted, fontSize = 12.sp)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            registeredTeamResult = null
            onNavigateToMatches()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan)
        ) {
          Text("VIEW MATCHES", color = ArenaBgDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { registeredTeamResult = null }) {
          Text("CLOSE", color = ArenaTextSecondary)
        }
      }
    )
  }
}

@Composable
fun TournamentRegistrationDialog(
  tournament: com.example.data.model.Tournament,
  currentUserUid: String,
  currentUserName: String,
  currentUserBgmiUid: String,
  onDismiss: () -> Unit,
  onSubmit: (String, List<TeamPlayer>) -> Unit
) {
  var teamName by remember { mutableStateOf("$currentUserName Squad") }

  // Player fields based on mode
  var p1Name by remember { mutableStateOf(currentUserName) }
  var p1Uid by remember { mutableStateOf(currentUserBgmiUid) }

  var p2Name by remember { mutableStateOf("") }
  var p2Uid by remember { mutableStateOf("") }

  var p3Name by remember { mutableStateOf("") }
  var p3Uid by remember { mutableStateOf("") }

  var p4Name by remember { mutableStateOf("") }
  var p4Uid by remember { mutableStateOf("") }

  var validationError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = ArenaCardElevated,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = "Register: ${tournament.mode.name}",
        color = ArenaTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        if (validationError != null) {
          Text(
            text = validationError!!,
            color = ArenaDanger,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }

        if (tournament.mode != TournamentMode.SOLO) {
          OutlinedTextField(
            value = teamName,
            onValueChange = { teamName = it },
            label = { Text("Team Name") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        // Captain / Player 1 (Pre-filled from current user)
        Text(
          text = if (tournament.mode == TournamentMode.SOLO) "Player Profile" else "Captain (You)",
          color = ArenaCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
          value = p1Name,
          onValueChange = { p1Name = it },
          label = { Text("Player Name") },
          colors = arenaTextFieldColors(),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = p1Uid,
          onValueChange = { if (it.all { c -> c.isDigit() }) p1Uid = it },
          label = { Text("BGMI Character UID") },
          colors = arenaTextFieldColors(),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Player 2 for Duo and Squad
        if (tournament.mode == TournamentMode.DUO || tournament.mode == TournamentMode.SQUAD) {
          Spacer(modifier = Modifier.height(14.dp))
          Text("Teammate 2", color = ArenaPurpleBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          OutlinedTextField(
            value = p2Name,
            onValueChange = { p2Name = it },
            label = { Text("Player 2 Name") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = p2Uid,
            onValueChange = { if (it.all { c -> c.isDigit() }) p2Uid = it },
            label = { Text("Player 2 BGMI UID") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        // Players 3 and 4 for Squad
        if (tournament.mode == TournamentMode.SQUAD) {
          Spacer(modifier = Modifier.height(14.dp))
          Text("Teammate 3", color = ArenaPurpleBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          OutlinedTextField(
            value = p3Name,
            onValueChange = { p3Name = it },
            label = { Text("Player 3 Name") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = p3Uid,
            onValueChange = { if (it.all { c -> c.isDigit() }) p3Uid = it },
            label = { Text("Player 3 BGMI UID") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(14.dp))
          Text("Teammate 4", color = ArenaPurpleBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          OutlinedTextField(
            value = p4Name,
            onValueChange = { p4Name = it },
            label = { Text("Player 4 Name") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = p4Uid,
            onValueChange = { if (it.all { c -> c.isDigit() }) p4Uid = it },
            label = { Text("Player 4 BGMI UID") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          // Validation
          if (p1Uid.isBlank()) {
            validationError = "Player 1 UID is required."
            return@Button
          }

          val playersList = mutableListOf(TeamPlayer(p1Name, p1Uid, isCaptain = true))

          if (tournament.mode == TournamentMode.DUO || tournament.mode == TournamentMode.SQUAD) {
            if (p2Name.isBlank() || p2Uid.isBlank()) {
              validationError = "Teammate 2 name and UID are required."
              return@Button
            }
            playersList.add(TeamPlayer(p2Name, p2Uid))
          }

          if (tournament.mode == TournamentMode.SQUAD) {
            if (p3Name.isBlank() || p3Uid.isBlank() || p4Name.isBlank() || p4Uid.isBlank()) {
              validationError = "All 4 squad players are required."
              return@Button
            }
            playersList.add(TeamPlayer(p3Name, p3Uid))
            playersList.add(TeamPlayer(p4Name, p4Uid))
          }

          // Anti-duplicate UID check in team
          val uids = playersList.map { it.bgmiUid.trim() }
          if (uids.toSet().size != uids.size) {
            validationError = "Duplicate BGMI UID detected in the lineup."
            return@Button
          }

          onSubmit(teamName, playersList)
        },
        colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan),
        modifier = Modifier.testTag("submit_registration_button")
      ) {
        Text("CONFIRM JOIN", color = ArenaBgDark, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = ArenaTextSecondary)
      }
    }
  )
}
