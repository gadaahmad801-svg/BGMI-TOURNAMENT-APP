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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountStatus
import com.example.data.model.Match
import com.example.data.model.MatchTeamResult
import com.example.data.model.Tournament
import com.example.data.model.TournamentMode
import com.example.data.model.TournamentStatus
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaCardBg
import com.example.ui.theme.ArenaCardElevated
import com.example.ui.theme.ArenaCyan
import com.example.ui.theme.ArenaDanger
import com.example.ui.theme.ArenaGold
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
fun AdminDashboardScreen(
  viewModel: ArenaViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val tournaments by viewModel.tournaments.collectAsState()
  val matches by viewModel.matches.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()
  val auditLogs by viewModel.auditLogs.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  val adminTabs = listOf("ROOMS", "TOURNAMENTS", "RESULTS", "USERS", "ANNOUNCE", "AUDIT")

  // Modals state
  var showPublishRoomDialog by remember { mutableStateOf<Match?>(null) }
  var showCreateTournamentDialog by remember { mutableStateOf(false) }
  var showFinalizeResultDialog by remember { mutableStateOf<Match?>(null) }

  if (currentUser == null || currentUser?.role != com.example.data.model.UserRole.ADMIN) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(ArenaBgDark)
        .statusBarsPadding()
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = "Access Denied",
          tint = ArenaDanger,
          modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "ACCESS DENIED",
          style = MaterialTheme.typography.titleLarge,
          color = ArenaTextPrimary,
          fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Administrator privileges are required to access this control center.",
          color = ArenaTextSecondary,
          fontSize = 13.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = onBack,
          colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Return to Safety", color = ArenaBgDark, fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

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
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(ArenaCardBg)
          .border(BorderStroke(1.dp, ArenaBorder), CircleShape)
          .testTag("admin_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ArenaTextPrimary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = "ADMIN CONTROL CENTER",
          style = MaterialTheme.typography.titleMedium,
          color = ArenaTextPrimary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "Tournament Director & Referee Console",
          color = ArenaPurpleBright,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // High Level Overview Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AdminMetricBox("TOURNEYS", "${tournaments.size}", ArenaCyan, Modifier.weight(1f))
      AdminMetricBox("MATCHES", "${matches.size}", ArenaPurpleBright, Modifier.weight(1f))
      AdminMetricBox("PLAYERS", "${allUsers.size}", ArenaSuccess, Modifier.weight(1f))
      AdminMetricBox("AUDITS", "${auditLogs.size}", ArenaGold, Modifier.weight(1f))
    }

    // Scrollable Horizontal Tab Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      items(adminTabs.indices.toList()) { index ->
        val tabName = adminTabs[index]
        val isSelected = selectedTab == index
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) ArenaCyan else ArenaCardBg)
            .border(BorderStroke(1.dp, if (isSelected) ArenaCyan else ArenaBorder), RoundedCornerShape(10.dp))
            .clickable { selectedTab = index }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("admin_tab_$tabName")
        ) {
          Text(
            text = tabName,
            color = if (isSelected) ArenaBgDark else ArenaTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Content based on selected tab
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      when (selectedTab) {
        // 0. ROOM MANAGEMENT
        0 -> {
          item {
            Text(
              text = "PUBLISH CUSTOM ROOM CREDENTIALS",
              color = ArenaTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }

          items(matches, key = { it.matchId }) { match ->
            Card(
              colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, ArenaBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("MATCH #${match.matchNumber} • ${match.mode.name}", color = ArenaCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  Text(match.status.name, color = if (match.isRoomPublished) ArenaSuccess else ArenaWarning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(match.tournamentTitle, color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                if (match.isRoomPublished) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text("Current Room ID: ${match.roomId} | Password: ${match.roomPassword}", color = ArenaTextSecondary, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = { showPublishRoomDialog = match },
                  colors = ButtonDefaults.buttonColors(containerColor = ArenaCardElevated),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Icon(Icons.Default.LockOpen, contentDescription = null, tint = ArenaCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (match.isRoomPublished) "UPDATE ROOM CREDENTIALS" else "PUBLISH ROOM ID & PASSWORD",
                    color = ArenaCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }

        // 1. TOURNAMENT MANAGEMENT
        1 -> {
          item {
            Button(
              onClick = { showCreateTournamentDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(ArenaPurple, ArenaCyan)))
                .testTag("admin_create_tournament_btn")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text("CREATE NEW TOURNAMENT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }

          items(tournaments, key = { it.id }) { tourney ->
            Card(
              colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, ArenaBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(tourney.title, color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                  Text(tourney.status.name, color = ArenaCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("${tourney.mode.name} • ${tourney.map} • ${tourney.registeredTeams}/${tourney.maxTeams} Teams", color = ArenaTextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Status Switchers
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Button(
                    onClick = { viewModel.updateTournamentStatus(tourney.id, TournamentStatus.REGISTRATION_OPEN) },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaSuccess.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("OPEN", color = ArenaSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = { viewModel.updateTournamentStatus(tourney.id, TournamentStatus.LIVE) },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("LIVE", color = ArenaCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = { viewModel.updateTournamentStatus(tourney.id, TournamentStatus.COMPLETED) },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaCardElevated),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("FINISH", color = ArenaTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // 2. RESULTS SCORING & FINALIZATION
        2 -> {
          item {
            Text(
              text = "OFFICIAL MATCH RESULTS & SCORING",
              color = ArenaTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }

          items(matches, key = { it.matchId }) { match ->
            Card(
              colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, ArenaBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("MATCH #${match.matchNumber}: ${match.tournamentTitle}", color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Mode: ${match.mode.name} • Status: ${match.status.name}", color = ArenaTextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = { showFinalizeResultDialog = match },
                  colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("INPUT / FINALIZE SCORES", color = ArenaBgDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
              }
            }
          }
        }

        // 3. USER MANAGEMENT & FAIR PLAY
        3 -> {
          item {
            Text(
              text = "PLAYER ACCOUNTS & ANTI-CHEAT COMPLIANCE",
              color = ArenaTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }

          items(allUsers, key = { it.uid }) { user ->
            Card(
              colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, ArenaBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(user.displayName, color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                  Text("BGMI: ${user.bgmiName} • UID: ${user.bgmiUid}", color = ArenaCyan, fontSize = 11.sp)
                  Text("Status: ${user.accountStatus.name}", color = if (user.accountStatus == AccountStatus.ACTIVE) ArenaSuccess else ArenaDanger, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = {
                    val newStatus = if (user.accountStatus == AccountStatus.ACTIVE) AccountStatus.SUSPENDED else AccountStatus.ACTIVE
                    viewModel.setUserStatus(user.uid, newStatus)
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (user.accountStatus == AccountStatus.ACTIVE) ArenaDanger.copy(alpha = 0.2f) else ArenaSuccess.copy(alpha = 0.2f)
                  ),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text(
                    text = if (user.accountStatus == AccountStatus.ACTIVE) "SUSPEND" else "ACTIVATE",
                    color = if (user.accountStatus == AccountStatus.ACTIVE) ArenaDanger else ArenaSuccess,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }

        // 4. ANNOUNCEMENTS
        4 -> {
          item {
            AdminAnnouncementForm(viewModel = viewModel)
          }
        }

        // 5. AUDIT LOGS
        5 -> {
          item {
            Text(
              text = "IMMUTABLE ADMINISTRATIVE AUDIT TRAIL",
              color = ArenaTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }

          items(auditLogs, key = { it.id }) { log ->
            val dateFormatted = remember(log.timestamp) {
              SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()).format(Date(log.timestamp))
            }

            Card(
              colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, ArenaBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(log.action, color = ArenaCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  Text(dateFormatted, color = ArenaTextMuted, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${log.targetType}: ${log.metadata}", color = ArenaTextSecondary, fontSize = 12.sp)
                Text("Actor UID: ${log.actorUid}", color = ArenaTextMuted, fontSize = 10.sp)
              }
            }
          }
        }
      }
    }
  }

  // Publish Room Credentials Dialog
  if (showPublishRoomDialog != null) {
    val m = showPublishRoomDialog!!
    var inputRoomId by remember { mutableStateOf(m.roomId.ifEmpty { "8829104" }) }
    var inputPassword by remember { mutableStateOf(m.roomPassword.ifEmpty { "BGMI99" }) }

    AlertDialog(
      onDismissRequest = { showPublishRoomDialog = null },
      containerColor = ArenaCardElevated,
      title = { Text("Publish Room: Match #${m.matchNumber}", color = ArenaTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Enter the in-game Custom Room ID and Password created in BGMI.", color = ArenaTextSecondary, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = inputRoomId,
            onValueChange = { inputRoomId = it },
            label = { Text("Room ID") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = inputPassword,
            onValueChange = { inputPassword = it },
            label = { Text("Password") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.publishRoom(
              matchId = m.matchId,
              roomId = inputRoomId,
              pass = inputPassword,
              onSuccess = { showPublishRoomDialog = null },
              onError = { viewModel.showToast(it) }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan)
        ) {
          Text("PUBLISH TO PLAYERS", color = ArenaBgDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showPublishRoomDialog = null }) {
          Text("CANCEL", color = ArenaTextSecondary)
        }
      }
    )
  }

  // Finalize Results Dialog
  if (showFinalizeResultDialog != null) {
    val m = showFinalizeResultDialog!!
    AlertDialog(
      onDismissRequest = { showFinalizeResultDialog = null },
      containerColor = ArenaCardElevated,
      title = { Text("Finalize Match Results", color = ArenaTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Submit and seal official standings for ${m.tournamentTitle}.", color = ArenaTextSecondary, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(10.dp))
          Text("• Auto-calculates placement points (1st: 15, 2nd: 12, etc.)", color = ArenaCyan, fontSize = 11.sp)
          Text("• Awards +1 Kill Point per frag", color = ArenaCyan, fontSize = 11.sp)
          Text("• Updates Global and Tournament Leaderboards", color = ArenaCyan, fontSize = 11.sp)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val sampleResults = listOf(
              MatchTeamResult("Team Hydra", 1, 14, 15, 14, 0, 0, 29),
              MatchTeamResult("Soul Warriors", 2, 9, 12, 9, 0, 0, 21),
              MatchTeamResult("GodLike Strikers", 3, 7, 10, 7, 0, 0, 17),
              MatchTeamResult("Revenant Vipers", 4, 5, 8, 5, 0, 0, 13)
            )
            viewModel.finalizeMatchResults(
              matchId = m.matchId,
              results = sampleResults,
              onSuccess = { showFinalizeResultDialog = null },
              onError = { viewModel.showToast(it) }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan)
        ) {
          Text("SEAL & DISTRIBUTE COINS", color = ArenaBgDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showFinalizeResultDialog = null }) {
          Text("CANCEL", color = ArenaTextSecondary)
        }
      }
    )
  }

  // Create Tournament Dialog
  if (showCreateTournamentDialog) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(TournamentMode.SQUAD) }
    var map by remember { mutableStateOf("Erangel") }
    var maxTeams by remember { mutableStateOf("25") }
    var rewardCoins by remember { mutableStateOf("100") }

    AlertDialog(
      onDismissRequest = { showCreateTournamentDialog = false },
      containerColor = ArenaCardElevated,
      title = { Text("Create Tournament", color = ArenaTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tournament Title") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Short Description") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = map,
            onValueChange = { map = it },
            label = { Text("Map (Erangel, Miramar, etc.)") },
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = maxTeams,
            onValueChange = { maxTeams = it },
            label = { Text("Max Squads/Players") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = arenaTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (title.isNotBlank()) {
              viewModel.createTournament(
                title = title,
                desc = desc.ifEmpty { "High-octane competitive battle royale showdown." },
                mode = mode,
                map = map.ifEmpty { "Erangel" },
                maxTeams = maxTeams.toIntOrNull() ?: 25,
                rewardCoins = rewardCoins.toIntOrNull() ?: 100,
                rules = listOf(
                  "Fair play enforced. Anti-cheat checks active.",
                  "All players must join room 10 minutes prior to start.",
                  "Emulators are strictly prohibited for mobile tournaments."
                ),
                onSuccess = { showCreateTournamentDialog = false },
                onError = { viewModel.showToast(it) }
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan)
        ) {
          Text("CREATE", color = ArenaBgDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateTournamentDialog = false }) {
          Text("CANCEL", color = ArenaTextSecondary)
        }
      }
    )
  }
}

@Composable
fun AdminMetricBox(
  title: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(ArenaCardBg)
      .border(BorderStroke(1.dp, ArenaBorder), RoundedCornerShape(12.dp))
      .padding(vertical = 10.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(value, color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
      Text(title, color = ArenaTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun AdminAnnouncementForm(viewModel: ArenaViewModel) {
  var annTitle by remember { mutableStateOf("") }
  var annMsg by remember { mutableStateOf("") }

  Card(
    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, ArenaBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text("BROADCAST ANNOUNCEMENT", color = ArenaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Text("Send a global alert to all active tournament players.", color = ArenaTextSecondary, fontSize = 12.sp)

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = annTitle,
        onValueChange = { annTitle = it },
        label = { Text("Announcement Title") },
        colors = arenaTextFieldColors(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = annMsg,
        onValueChange = { annMsg = it },
        label = { Text("Message Body") },
        colors = arenaTextFieldColors(),
        minLines = 3,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = {
          if (annTitle.isNotBlank() && annMsg.isNotBlank()) {
            viewModel.postAnnouncement(
              title = annTitle,
              message = annMsg,
              important = true,
              onSuccess = {
                annTitle = ""
                annMsg = ""
              }
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = ArenaCyan),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("BROADCAST TO ALL PLAYERS", color = ArenaBgDark, fontWeight = FontWeight.Bold)
      }
    }
  }
}
