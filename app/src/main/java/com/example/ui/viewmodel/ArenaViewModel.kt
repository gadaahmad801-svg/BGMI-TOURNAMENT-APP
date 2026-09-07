package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AccountStatus
import com.example.data.model.Announcement
import com.example.data.model.AuditLog
import com.example.data.model.CoinTransaction
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Match
import com.example.data.model.MatchTeamResult
import com.example.data.model.NotificationItem
import com.example.data.model.SupportTicket
import com.example.data.model.Team
import com.example.data.model.TeamPlayer
import com.example.data.model.Tournament
import com.example.data.model.TournamentMode
import com.example.data.model.TournamentStatus
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.ArenaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArenaViewModel(
  private val repository: ArenaRepository = ArenaRepository.instance
) : ViewModel() {

  val currentUser: StateFlow<User?> = repository.currentUser
  val allUsers: StateFlow<List<User>> = repository.users
  val tournaments: StateFlow<List<Tournament>> = repository.tournaments
  val teams: StateFlow<List<Team>> = repository.teams
  val matches: StateFlow<List<Match>> = repository.matches
  val leaderboard: StateFlow<List<LeaderboardEntry>> = repository.leaderboard
  val transactions: StateFlow<List<CoinTransaction>> = repository.transactions
  val notifications: StateFlow<List<NotificationItem>> = repository.notifications
  val supportTickets: StateFlow<List<SupportTicket>> = repository.supportTickets
  val announcements: StateFlow<List<Announcement>> = repository.announcements
  val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs

  // Derived unread notifications count
  val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
    list.count { !it.read }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // Tournament Discovery Filters
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedMode = MutableStateFlow(TournamentMode.ALL)
  val selectedMode: StateFlow<TournamentMode> = _selectedMode.asStateFlow()

  // Filtered Tournaments
  val filteredTournaments: StateFlow<List<Tournament>> = combine(
    tournaments,
    _searchQuery,
    _selectedMode
  ) { list, query, mode ->
    list.filter { t ->
      val matchesQuery = query.isBlank() ||
        t.title.contains(query, ignoreCase = true) ||
        t.map.contains(query, ignoreCase = true) ||
        t.mode.name.contains(query, ignoreCase = true)
      val matchesMode = (mode == TournamentMode.ALL) || (t.mode == mode)
      matchesQuery && matchesMode
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Matches filter: 0: ONGOING, 1: UPCOMING, 2: COMPLETED
  private val _matchesTab = MutableStateFlow(0)
  val matchesTab: StateFlow<Int> = _matchesTab.asStateFlow()

  // Leaderboard tab: 0: GLOBAL, 1: TOURNAMENT, 2: SEASON
  private val _leaderboardTab = MutableStateFlow(0)
  val leaderboardTab: StateFlow<Int> = _leaderboardTab.asStateFlow()

  // Active Toast message
  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  // Network State Simulation
  private val _isOffline = MutableStateFlow(false)
  val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

  // Selected Item details for direct navigation
  private val _selectedTournamentId = MutableStateFlow<String?>(null)
  val selectedTournamentId: StateFlow<String?> = _selectedTournamentId.asStateFlow()

  private val _selectedMatchId = MutableStateFlow<String?>(null)
  val selectedMatchId: StateFlow<String?> = _selectedMatchId.asStateFlow()

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedMode(mode: TournamentMode) {
    _selectedMode.value = mode
  }

  fun setMatchesTab(index: Int) {
    _matchesTab.value = index
  }

  fun setLeaderboardTab(index: Int) {
    _leaderboardTab.value = index
  }

  fun selectTournament(id: String?) {
    _selectedTournamentId.value = id
  }

  fun selectMatch(id: String?) {
    _selectedMatchId.value = id
  }

  fun showToast(message: String) {
    _toastMessage.value = message
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  fun toggleOfflineSimulation() {
    _isOffline.value = !_isOffline.value
    showToast(if (_isOffline.value) "Device is now offline" else "Back online!")
  }


  fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val result = repository.login(email, pass)
      result.onSuccess {
        showToast("Welcome back, ${it.displayName}!")
        onSuccess()
      }.onFailure {
        onError(it.message ?: "Authentication failed")
      }
    }
  }

  fun register(
    name: String,
    email: String,
    pass: String,
    confirmPass: String,
    bgmiName: String,
    bgmiUid: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.register(name, email, pass, confirmPass, bgmiName, bgmiUid)
      result.onSuccess {
        showToast("Account created with 5 Virtual Coins!")
        onSuccess()
      }.onFailure {
        onError(it.message ?: "Registration failed")
      }
    }
  }

  fun getFirebaseAuthConfig(context: android.content.Context, webClientId: String = ""): com.example.data.auth.FirebaseAuthConfig {
    return com.example.data.auth.FirebaseAuthManager(context).checkAuthConfig(webClientId)
  }

  fun signInWithGoogle(context: android.content.Context, webClientId: String = "", onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val authManager = com.example.data.auth.FirebaseAuthManager(context)
      val result = authManager.signInWithGoogle(webClientId)
      result.onSuccess { user ->
        repository.setCurrentUser(user)
        showToast("Signed in as ${user.displayName}")
        onSuccess()
      }.onFailure { e ->
        onError(e.message ?: "Google Sign-In failed")
      }
    }
  }

  fun sendPasswordResetEmail(context: android.content.Context, email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val authManager = com.example.data.auth.FirebaseAuthManager(context)
      if (authManager.isAvailable()) {
        val result = authManager.sendPasswordResetEmail(email)
        result.onSuccess {
          showToast("Password reset email sent to $email")
          onSuccess()
        }.onFailure { e ->
          onError(e.message ?: "Failed to send reset email")
        }
      } else {
        // Fallback local simulation when Firebase is offline
        showToast("Simulation: Password reset email sent to $email")
        onSuccess()
      }
    }
  }

  fun logout(onSuccess: () -> Unit) {
    repository.logout()
    showToast("Logged out safely.")
    onSuccess()
  }

  fun updateProfile(name: String, bgmiName: String, bgmiUid: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val res = repository.updateProfile(name, bgmiName, bgmiUid)
    res.onSuccess {
      showToast("Profile updated successfully!")
      onSuccess()
    }.onFailure {
      onError(it.message ?: "Failed to update profile")
    }
  }

  fun updateAvatar(avatarUri: String) {
    val res = repository.updateAvatar(avatarUri)
    res.onSuccess {
      showToast("Avatar updated successfully!")
    }.onFailure {
      showToast(it.message ?: "Failed to update avatar")
    }
  }

  fun registerTournament(
    tournamentId: String,
    teamName: String,
    players: List<TeamPlayer>,
    onSuccess: (Team) -> Unit,
    onError: (String) -> Unit
  ) {
    if (_isOffline.value) {
      onError("Cannot register while offline. Check internet connection.")
      return
    }
    val res = repository.registerForTournament(tournamentId, teamName, players)
    res.onSuccess { team ->
      showToast("Registered successfully for tournament!")
      onSuccess(team)
    }.onFailure {
      onError(it.message ?: "Registration failed")
    }
  }

  fun publishRoom(matchId: String, roomId: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val res = repository.publishMatchRoom(matchId, roomId, pass)
    res.onSuccess {
      showToast("Room credentials published to registered players!")
      onSuccess()
    }.onFailure {
      onError(it.message ?: "Failed to publish room")
    }
  }

  fun finalizeMatchResults(matchId: String, results: List<MatchTeamResult>, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val res = repository.finalizeMatchResults(matchId, results)
    res.onSuccess {
      showToast("Official match results finalized & rewards distributed!")
      onSuccess()
    }.onFailure {
      onError(it.message ?: "Failed to finalize results")
    }
  }

  fun createTournament(
    title: String,
    desc: String,
    mode: TournamentMode,
    map: String,
    maxTeams: Int,
    rewardCoins: Int,
    rules: List<String>,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    val res = repository.createTournament(title, desc, mode, map, maxTeams, rewardCoins, rules)
    res.onSuccess {
      showToast("Tournament created: ${it.title}")
      onSuccess()
    }.onFailure {
      onError(it.message ?: "Failed to create tournament")
    }
  }

  fun updateTournamentStatus(tournamentId: String, status: TournamentStatus) {
    repository.updateTournamentStatus(tournamentId, status)
    showToast("Tournament status updated to $status")
  }

  fun setUserStatus(uid: String, status: AccountStatus) {
    repository.setUserStatus(uid, status)
    showToast("User status updated to $status")
  }

  fun postAnnouncement(title: String, message: String, important: Boolean = false, onSuccess: () -> Unit) {
    val res = repository.postAnnouncement(title, message, important)
    res.onSuccess {
      showToast("Announcement published successfully!")
      onSuccess()
    }
  }

  fun submitSupportTicket(category: String, subject: String, message: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val res = repository.submitSupportTicket(category, subject, message)
    res.onSuccess {
      showToast("Ticket ${it.ticketId} submitted. Support team will respond shortly.")
      onSuccess()
    }.onFailure {
      onError(it.message ?: "Failed to submit ticket")
    }
  }

  fun markNotificationAsRead(id: String) {
    repository.markNotificationAsRead(id)
  }

  fun markAllNotificationsAsRead() {
    repository.markAllNotificationsAsRead()
    showToast("All notifications marked as read.")
  }

  fun updatePreferences(notifications: Boolean, sound: Boolean, reducedMotion: Boolean) {
    repository.updatePreferences(notifications, sound, reducedMotion)
    showToast("Settings saved.")
  }
}
