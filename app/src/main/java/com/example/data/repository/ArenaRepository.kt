package com.example.data.repository

import com.example.data.model.AccountStatus
import com.example.data.model.Announcement
import com.example.data.model.AuditLog
import com.example.data.model.CoinTransaction
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.MatchTeamResult
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.ScoringConfig
import com.example.data.model.SupportTicket
import com.example.data.model.Team
import com.example.data.model.TeamPlayer
import com.example.data.model.TicketStatus
import com.example.data.model.Tournament
import com.example.data.model.TournamentMode
import com.example.data.model.TournamentStatus
import com.example.data.model.TransactionType
import com.example.data.model.User
import com.example.data.model.UserPreferences
import com.example.data.model.UserRole
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ArenaRepository {

  companion object {
    const val MIN_VIRTUAL_COINS = 5
    val instance: ArenaRepository by lazy { ArenaRepository() }
  }

  // Current User Session
  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

  // Registered Users (for admin & auth lookup)
  private val _users = MutableStateFlow<List<User>>(emptyList())
  val users: StateFlow<List<User>> = _users.asStateFlow()

  // Tournaments
  private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
  val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

  // Teams / Registrations
  private val _teams = MutableStateFlow<List<Team>>(emptyList())
  val teams: StateFlow<List<Team>> = _teams.asStateFlow()

  // Matches
  private val _matches = MutableStateFlow<List<Match>>(emptyList())
  val matches: StateFlow<List<Match>> = _matches.asStateFlow()

  // Leaderboard
  private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
  val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

  // Coin Transactions Ledger
  private val _transactions = MutableStateFlow<List<CoinTransaction>>(emptyList())
  val transactions: StateFlow<List<CoinTransaction>> = _transactions.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  // Support Tickets
  private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
  val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

  // Announcements
  private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
  val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

  // Audit Logs (Server Authoritative)
  private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
  val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

  init {
    seedInitialData()
  }

  // ==========================================
  // AUTHENTICATION & PROFILE
  // ==========================================

  fun login(email: String, pass: String): Result<User> {
    val cleanEmail = email.trim().lowercase()
    if (cleanEmail.isEmpty() || pass.isEmpty()) {
      return Result.failure(Exception("Please enter both email and password."))
    }
    val user = _users.value.find { it.email.lowercase() == cleanEmail }
      ?: return Result.failure(Exception("Account not found. Please check your credentials."))

    if (user.accountStatus == AccountStatus.BANNED) {
      return Result.failure(Exception("This account has been banned due to fair play violations."))
    }

    _currentUser.value = user
    return Result.success(user)
  }

  fun register(
    fullName: String,
    email: String,
    pass: String,
    confirmPass: String,
    bgmiName: String,
    bgmiUid: String
  ): Result<User> {
    val cleanName = fullName.trim()
    val cleanEmail = email.trim().lowercase()
    val cleanBgmiName = bgmiName.trim()
    val cleanBgmiUid = bgmiUid.trim()

    if (cleanName.length < 2) return Result.failure(Exception("Full Name must be at least 2 characters."))
    if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) return Result.failure(Exception("Please enter a valid email."))
    if (pass.length < 6) return Result.failure(Exception("Password must be at least 6 characters."))
    if (pass != confirmPass) return Result.failure(Exception("Passwords do not match."))
    if (cleanBgmiName.isEmpty()) return Result.failure(Exception("BGMI In-Game Name is required."))
    if (cleanBgmiUid.length < 6 || !cleanBgmiUid.all { it.isDigit() }) {
      return Result.failure(Exception("BGMI UID must be digits only (minimum 6 digits)."))
    }

    if (_users.value.any { it.email.lowercase() == cleanEmail }) {
      return Result.failure(Exception("An account with this email already exists."))
    }
    if (_users.value.any { it.bgmiUid == cleanBgmiUid }) {
      return Result.failure(Exception("This BGMI UID is already registered with another account."))
    }

    val newUser = User(
      uid = "usr_${UUID.randomUUID().toString().take(8)}",
      displayName = cleanName,
      email = cleanEmail,
      bgmiName = cleanBgmiName,
      bgmiUid = cleanBgmiUid,
      role = UserRole.USER,
      virtualCoins = MIN_VIRTUAL_COINS, // strictly 5 initial virtual coins
      accountStatus = AccountStatus.ACTIVE,
      stats = UserStats(
        matchesPlayed = 0,
        wins = 0,
        kills = 0,
        points = 0,
        currentRank = "Bronze I",
        bestFinish = "-",
        currentStreak = 0
      )
    )

    _users.value = _users.value + newUser
    _currentUser.value = newUser

    // Record welcome bonus transaction
    addCoinTransaction(
      uid = newUser.uid,
      type = TransactionType.BONUS,
      amount = MIN_VIRTUAL_COINS,
      balanceBefore = 0,
      balanceAfter = MIN_VIRTUAL_COINS,
      reason = "Welcome Bonus (Starting Balance)"
    )

    // Send welcome notification
    addNotification(
      recipientUid = newUser.uid,
      title = "Welcome to BGMI ARENA!",
      body = "Your account is activated with 5 virtual coins. Browse tournaments and compete!",
      type = NotificationType.SYSTEM
    )

    return Result.success(newUser)
  }

  fun updateProfile(displayName: String, bgmiName: String, bgmiUid: String): Result<User> {
    val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated."))
    val cleanName = displayName.trim()
    val cleanBgmi = bgmiName.trim()
    val cleanUid = bgmiUid.trim()

    if (cleanName.length < 2) return Result.failure(Exception("Display name is too short."))
    if (cleanBgmi.isEmpty()) return Result.failure(Exception("BGMI Username cannot be empty."))
    if (cleanUid.length < 6 || !cleanUid.all { it.isDigit() }) {
      return Result.failure(Exception("BGMI UID must be valid digits."))
    }

    val updated = user.copy(
      displayName = cleanName,
      bgmiName = cleanBgmi,
      bgmiUid = cleanUid,
      updatedAt = System.currentTimeMillis()
    )

    _currentUser.value = updated
    _users.value = _users.value.map { if (it.uid == user.uid) updated else it }
    return Result.success(updated)
  }

  fun updateAvatar(avatarUrl: String): Result<User> {
    val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated."))
    val cleanUrl = avatarUrl.trim()
    if (cleanUrl.isNotEmpty() && !cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://") && !cleanUrl.startsWith("content://") && !cleanUrl.startsWith("android.resource://")) {
      return Result.failure(Exception("Invalid avatar URI format."))
    }
    if (cleanUrl.length > 1000) {
      return Result.failure(Exception("Avatar URL exceeds maximum allowed length."))
    }
    val updated = user.copy(avatarUrl = cleanUrl, updatedAt = System.currentTimeMillis())
    _currentUser.value = updated
    _users.value = _users.value.map { if (it.uid == user.uid) updated else it }
    return Result.success(updated)
  }

  fun updatePreferences(notifications: Boolean, sound: Boolean, reducedMotion: Boolean) {
    val user = _currentUser.value ?: return
    val updated = user.copy(
      preferences = UserPreferences(
        notifications = notifications,
        sound = sound,
        reducedMotion = reducedMotion
      )
    )
    _currentUser.value = updated
    _users.value = _users.value.map { if (it.uid == user.uid) updated else it }
  }

  fun setCurrentUser(user: User) {
    _currentUser.value = user
    if (_users.value.none { it.uid == user.uid }) {
      _users.value = _users.value + user
    } else {
      _users.value = _users.value.map { if (it.uid == user.uid) user else it }
    }
  }

  fun logout() {
    _currentUser.value = null
  }

  // ==========================================
  // VIRTUAL COIN SYSTEM & TRANSACTION LEDGER
  // ==========================================

  @Synchronized
  fun addCoinReward(uid: String, amount: Int, reason: String, referenceId: String = ""): Result<Int> {
    val caller = _currentUser.value
    if (caller != null && caller.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Only administrators or official match referees can issue coin rewards."))
    }
    if (amount <= 0) return Result.failure(Exception("Reward amount must be positive."))
    val targetUser = _users.value.find { it.uid == uid }
      ?: return Result.failure(Exception("User not found."))

    val before = targetUser.virtualCoins
    val after = before + amount

    val updatedUser = targetUser.copy(virtualCoins = after)
    _users.value = _users.value.map { if (it.uid == uid) updatedUser else it }
    if (_currentUser.value?.uid == uid) {
      _currentUser.value = updatedUser
    }

    addCoinTransaction(
      uid = uid,
      type = TransactionType.REWARD,
      amount = amount,
      balanceBefore = before,
      balanceAfter = after,
      reason = reason,
      referenceId = referenceId
    )

    addNotification(
      recipientUid = uid,
      title = "Coins Awarded: +$amount COINS",
      body = "You received $amount virtual coins for $reason. Current balance: $after COINS.",
      type = NotificationType.REWARD_RECEIVED
    )

    logAudit(
      actorUid = _currentUser.value?.uid ?: "SYSTEM",
      action = "COIN_REWARD_ISSUED",
      targetType = "USER",
      targetId = uid,
      metadata = "Amount: $amount, Reason: $reason"
    )

    return Result.success(after)
  }

  @Synchronized
  fun deductCoins(uid: String, amount: Int, reason: String, referenceId: String = ""): Result<Int> {
    val caller = _currentUser.value
    if (caller != null && caller.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Only administrators can adjust coin balances."))
    }
    if (amount <= 0) return Result.failure(Exception("Deduction amount must be positive."))
    val targetUser = _users.value.find { it.uid == uid }
      ?: return Result.failure(Exception("User not found."))

    val before = targetUser.virtualCoins
    val after = before - amount
    if (after < MIN_VIRTUAL_COINS) {
      return Result.failure(Exception("Virtual coin balance cannot fall below $MIN_VIRTUAL_COINS coins."))
    }

    val updatedUser = targetUser.copy(virtualCoins = after)
    _users.value = _users.value.map { if (it.uid == uid) updatedUser else it }
    if (_currentUser.value?.uid == uid) {
      _currentUser.value = updatedUser
    }

    addCoinTransaction(
      uid = uid,
      type = TransactionType.ADJUSTMENT,
      amount = -amount,
      balanceBefore = before,
      balanceAfter = after,
      reason = reason,
      referenceId = referenceId
    )

    return Result.success(after)
  }

  private fun addCoinTransaction(
    uid: String,
    type: TransactionType,
    amount: Int,
    balanceBefore: Int,
    balanceAfter: Int,
    reason: String,
    referenceId: String = ""
  ) {
    val tx = CoinTransaction(
      transactionId = "tx_${UUID.randomUUID().toString().take(10)}",
      uid = uid,
      type = type,
      amount = amount,
      balanceBefore = balanceBefore,
      balanceAfter = balanceAfter,
      reason = reason,
      referenceId = referenceId,
      createdAt = System.currentTimeMillis()
    )
    _transactions.value = listOf(tx) + _transactions.value
  }

  // ==========================================
  // TOURNAMENT DISCOVERY & REGISTRATION
  // ==========================================

  @Synchronized
  fun registerForTournament(
    tournamentId: String,
    teamName: String,
    players: List<TeamPlayer>
  ): Result<Team> {
    val user = _currentUser.value ?: return Result.failure(Exception("Please log in to join tournaments."))

    if (user.accountStatus == AccountStatus.SUSPENDED) {
      return Result.failure(Exception("Your account is currently suspended. Please contact support."))
    }
    if (user.accountStatus == AccountStatus.BANNED) {
      return Result.failure(Exception("Banned accounts cannot register for tournaments."))
    }

    val tournament = _tournaments.value.find { it.id == tournamentId }
      ?: return Result.failure(Exception("Tournament no longer available."))

    if (tournament.status != TournamentStatus.REGISTRATION_OPEN) {
      return Result.failure(Exception("Registration is currently closed for this tournament."))
    }

    if (System.currentTimeMillis() > tournament.registrationDeadline) {
      return Result.failure(Exception("Registration deadline has passed."))
    }

    if (tournament.registeredTeams >= tournament.maxTeams) {
      return Result.failure(Exception("Tournament is full."))
    }

    // Check if user already registered for this tournament
    val userAlreadyRegistered = _teams.value.any { team ->
      team.tournamentId == tournamentId && (
        team.captainUid == user.uid || team.players.any { it.bgmiUid == user.bgmiUid }
      )
    }
    if (userAlreadyRegistered) {
      return Result.failure(Exception("You or your team is already registered for this tournament."))
    }

    // Validate players
    if (players.isEmpty()) {
      return Result.failure(Exception("At least one player is required."))
    }
    val uids = players.map { it.bgmiUid.trim() }
    if (uids.toSet().size != uids.size) {
      return Result.failure(Exception("Duplicate BGMI UIDs detected in the team lineup."))
    }

    // Create Team
    val newTeam = Team(
      teamId = "tm_${UUID.randomUUID().toString().take(8)}",
      tournamentId = tournamentId,
      teamName = teamName.ifBlank { "${user.displayName}'s Squad" },
      captainUid = user.uid,
      players = players,
      status = "REGISTERED",
      createdAt = System.currentTimeMillis()
    )

    // Atomic update
    _teams.value = _teams.value + newTeam
    val updatedTournament = tournament.copy(
      registeredTeams = tournament.registeredTeams + 1,
      status = if (tournament.registeredTeams + 1 >= tournament.maxTeams) TournamentStatus.FULL else tournament.status
    )
    _tournaments.value = _tournaments.value.map { if (it.id == tournamentId) updatedTournament else it }

    // Associate team with upcoming match if available
    _matches.value = _matches.value.map { match ->
      if (match.tournamentId == tournamentId) {
        match.copy(participatingTeamIds = match.participatingTeamIds + newTeam.teamId)
      } else {
        match
      }
    }

    // Update user stats
    val updatedStats = user.stats.copy(matchesPlayed = user.stats.matchesPlayed + 1)
    val updatedUser = user.copy(stats = updatedStats)
    _currentUser.value = updatedUser
    _users.value = _users.value.map { if (it.uid == user.uid) updatedUser else it }

    // Send confirmation notification
    addNotification(
      recipientUid = user.uid,
      title = "Registered: ${tournament.title}",
      body = "Your squad '${newTeam.teamName}' is registered! Room credentials will be published 15 minutes before start.",
      type = NotificationType.TOURNAMENT_OPEN,
      tournamentId = tournamentId
    )

    return Result.success(newTeam)
  }

  // ==========================================
  // MATCH MANAGEMENT & ROOM INFO
  // ==========================================

  @Synchronized
  fun publishMatchRoom(matchId: String, roomId: String, pass: String): Result<Unit> {
    val admin = _currentUser.value
    if (admin == null || admin.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Admin privileges required."))
    }

    if (roomId.isBlank() || pass.isBlank()) {
      return Result.failure(Exception("Room ID and Password are required."))
    }

    val match = _matches.value.find { it.matchId == matchId }
      ?: return Result.failure(Exception("Match not found."))

    val updatedMatch = match.copy(
      roomId = roomId.trim(),
      roomPassword = pass.trim(),
      isRoomPublished = true,
      roomPublishedAt = System.currentTimeMillis(),
      status = MatchStatus.ROOM_PUBLISHED
    )

    _matches.value = _matches.value.map { if (it.matchId == matchId) updatedMatch else it }

    // Notify registered participants
    _users.value.forEach { u ->
      addNotification(
        recipientUid = u.uid,
        title = "Room Details Released!",
        body = "${match.tournamentTitle} - Match #${match.matchNumber} room details are now available. Join in-game immediately.",
        type = NotificationType.ROOM_RELEASED,
        matchId = matchId,
        tournamentId = match.tournamentId
      )
    }

    logAudit(
      actorUid = admin.uid,
      action = "ROOM_PUBLISHED",
      targetType = "MATCH",
      targetId = matchId,
      metadata = "Room ID: $roomId released for Match #${match.matchNumber}"
    )

    return Result.success(Unit)
  }

  @Synchronized
  fun finalizeMatchResults(matchId: String, results: List<MatchTeamResult>): Result<Unit> {
    val admin = _currentUser.value
    if (admin == null || admin.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Admin privileges required."))
    }
    if (results.isEmpty()) {
      return Result.failure(Exception("Results list cannot be empty."))
    }

    val match = _matches.value.find { it.matchId == matchId }
      ?: return Result.failure(Exception("Match not found."))

    val updatedMatch = match.copy(
      results = results,
      status = MatchStatus.COMPLETED
    )
    _matches.value = _matches.value.map { if (it.matchId == matchId) updatedMatch else it }

    // Award virtual coins to top 3 teams
    val tournament = _tournaments.value.find { it.id == match.tournamentId }
    val rewardBase = tournament?.rewardCoins ?: 100

    results.forEach { res ->
      val coinsAwarded = when (res.placement) {
        1 -> rewardBase
        2 -> (rewardBase * 0.7).toInt()
        3 -> (rewardBase * 0.4).toInt()
        else -> 0
      }

      if (coinsAwarded > 0) {
        // Find captain or user of this team
        val team = _teams.value.find { it.teamName == res.teamName }
        val recipientUid = team?.captainUid ?: _currentUser.value?.uid
        if (recipientUid != null) {
          addCoinReward(
            uid = recipientUid,
            amount = coinsAwarded,
            reason = "Tournament Winner #${res.placement} (${match.tournamentTitle})",
            referenceId = matchId
          )
        }
      }
    }

    // Update global leaderboard
    recalculateLeaderboard(results)

    logAudit(
      actorUid = admin.uid,
      action = "RESULT_PUBLISHED",
      targetType = "MATCH",
      targetId = matchId,
      metadata = "Official results submitted for ${results.size} teams"
    )

    return Result.success(Unit)
  }

  private fun recalculateLeaderboard(newResults: List<MatchTeamResult>) {
    val current = _leaderboard.value.toMutableList()
    newResults.forEach { r ->
      val existingIdx = current.indexOfFirst { it.teamOrPlayerName == r.teamName }
      if (existingIdx != -1) {
        val old = current[existingIdx]
        current[existingIdx] = old.copy(
          kills = old.kills + r.kills,
          placementPts = old.placementPts + r.placementPoints,
          totalPoints = old.totalPoints + r.totalPoints,
          matchesPlayed = old.matchesPlayed + 1,
          wins = if (r.placement == 1) old.wins + 1 else old.wins
        )
      } else {
        current.add(
          LeaderboardEntry(
            rank = 0,
            teamOrPlayerName = r.teamName,
            bgmiUid = "N/A",
            kills = r.kills,
            placementPts = r.placementPoints,
            totalPoints = r.totalPoints,
            matchesPlayed = 1,
            wins = if (r.placement == 1) 1 else 0
          )
        )
      }
    }

    // Deterministic ranking: Total Points desc -> Kills desc -> Wins desc
    val sorted = current.sortedWith(
      compareByDescending<LeaderboardEntry> { it.totalPoints }
        .thenByDescending { it.kills }
        .thenByDescending { it.wins }
    ).mapIndexed { index, entry ->
      entry.copy(rank = index + 1)
    }

    _leaderboard.value = sorted
  }

  // ==========================================
  // NOTIFICATIONS
  // ==========================================

  fun addNotification(
    recipientUid: String,
    title: String,
    body: String,
    type: NotificationType,
    tournamentId: String? = null,
    matchId: String? = null
  ) {
    val item = NotificationItem(
      id = "notif_${UUID.randomUUID().toString().take(8)}",
      recipientUid = recipientUid,
      title = title,
      body = body,
      type = type,
      tournamentId = tournamentId,
      matchId = matchId,
      read = false,
      createdAt = System.currentTimeMillis()
    )
    _notifications.value = listOf(item) + _notifications.value
  }

  fun markNotificationAsRead(id: String) {
    _notifications.value = _notifications.value.map {
      if (it.id == id) it.copy(read = true) else it
    }
  }

  fun markAllNotificationsAsRead() {
    val uid = _currentUser.value?.uid ?: return
    _notifications.value = _notifications.value.map {
      if (it.recipientUid == uid) it.copy(read = true) else it
    }
  }

  // ==========================================
  // SUPPORT TICKETS
  // ==========================================

  fun submitSupportTicket(category: String, subject: String, message: String): Result<SupportTicket> {
    val user = _currentUser.value ?: return Result.failure(Exception("User must be logged in."))
    if (subject.isBlank() || message.isBlank()) {
      return Result.failure(Exception("Subject and message cannot be empty."))
    }

    val ticket = SupportTicket(
      ticketId = "TCK-${UUID.randomUUID().toString().take(6).uppercase()}",
      userId = user.uid,
      userName = user.displayName,
      category = category,
      subject = subject.trim(),
      message = message.trim(),
      status = TicketStatus.OPEN,
      createdAt = System.currentTimeMillis()
    )

    _supportTickets.value = listOf(ticket) + _supportTickets.value
    return Result.success(ticket)
  }

  // ==========================================
  // ADMIN DASHBOARD OPERATIONS
  // ==========================================

  fun createTournament(
    title: String,
    description: String,
    mode: TournamentMode,
    map: String,
    maxTeams: Int,
    rewardCoins: Int,
    rules: List<String>
  ): Result<Tournament> {
    val admin = _currentUser.value
    if (admin == null || admin.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Admin privileges required."))
    }

    val newTournament = Tournament(
      id = "trn_${UUID.randomUUID().toString().take(8)}",
      title = title.trim(),
      description = description.trim(),
      mode = mode,
      map = map,
      status = TournamentStatus.REGISTRATION_OPEN,
      maxTeams = maxTeams,
      registeredTeams = 0,
      startAt = System.currentTimeMillis() + 86400000L, // Tomorrow
      registrationDeadline = System.currentTimeMillis() + 80000000L,
      rules = rules,
      rewardCoins = rewardCoins,
      entryRequirement = "FREE ENTRY",
      createdAt = System.currentTimeMillis(),
      createdBy = admin.displayName
    )

    _tournaments.value = listOf(newTournament) + _tournaments.value

    logAudit(
      actorUid = admin.uid,
      action = "TOURNAMENT_CREATED",
      targetType = "TOURNAMENT",
      targetId = newTournament.id,
      metadata = "Created ${newTournament.title} ($mode - $map)"
    )

    return Result.success(newTournament)
  }

  fun updateTournamentStatus(tournamentId: String, status: TournamentStatus): Result<Unit> {
    val admin = _currentUser.value
    if (admin == null || admin.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Admin privileges required."))
    }
    _tournaments.value = _tournaments.value.map {
      if (it.id == tournamentId) it.copy(status = status) else it
    }
    logAudit(
      actorUid = admin.uid,
      action = "TOURNAMENT_UPDATED",
      targetType = "TOURNAMENT",
      targetId = tournamentId,
      metadata = "Status changed to $status"
    )
    return Result.success(Unit)
  }

  fun setUserStatus(uid: String, status: AccountStatus): Result<Unit> {
    val admin = _currentUser.value
    if (admin == null || admin.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Admin privileges required."))
    }
    _users.value = _users.value.map {
      if (it.uid == uid) it.copy(accountStatus = status) else it
    }
    logAudit(
      actorUid = admin.uid,
      action = if (status == AccountStatus.SUSPENDED) "USER_SUSPENDED" else "USER_RESTORED",
      targetType = "USER",
      targetId = uid,
      metadata = "Account status set to $status"
    )
    return Result.success(Unit)
  }

  fun postAnnouncement(title: String, message: String, important: Boolean = false): Result<Announcement> {
    val admin = _currentUser.value
    if (admin == null || admin.role != UserRole.ADMIN) {
      return Result.failure(Exception("Unauthorized: Admin privileges required."))
    }

    val ann = Announcement(
      id = "ann_${UUID.randomUUID().toString().take(8)}",
      title = title.trim(),
      message = message.trim(),
      target = "ALL_USERS",
      createdAt = System.currentTimeMillis(),
      important = important
    )
    _announcements.value = listOf(ann) + _announcements.value

    _users.value.forEach { u ->
      addNotification(
        recipientUid = u.uid,
        title = ann.title,
        body = ann.message,
        type = NotificationType.ANNOUNCEMENT
      )
    }

    return Result.success(ann)
  }

  private fun logAudit(
    actorUid: String,
    action: String,
    targetType: String,
    targetId: String,
    metadata: String
  ) {
    val log = AuditLog(
      id = "aud_${UUID.randomUUID().toString().take(8)}",
      actorUid = actorUid,
      action = action,
      targetType = targetType,
      targetId = targetId,
      timestamp = System.currentTimeMillis(),
      metadata = metadata
    )
    _auditLogs.value = listOf(log) + _auditLogs.value
  }

  // ==========================================
  // INITIAL SEED DATA
  // ==========================================

  private fun seedInitialData() {
    // 1. Seed Default Admin and Demo User
    val adminUser = User(
      uid = "admin_01",
      displayName = "Arena Master",
      email = "admin@bgmiarena.com",
      bgmiName = "ARENA_OFFICIAL",
      bgmiUid = "5109283746",
      role = UserRole.ADMIN,
      virtualCoins = 500,
      accountStatus = AccountStatus.ACTIVE,
      stats = UserStats(
        matchesPlayed = 42,
        wins = 18,
        kills = 142,
        points = 890,
        currentRank = "Ace Master",
        bestFinish = "#1",
        currentStreak = 4
      )
    )

    val demoUser = User(
      uid = "user_ahmed",
      displayName = "Ahmed",
      email = "ahmed@example.com",
      bgmiName = "SNIPER_VIPER",
      bgmiUid = "5829104729",
      role = UserRole.USER,
      virtualCoins = MIN_VIRTUAL_COINS, // strictly 5 initial virtual coins
      accountStatus = AccountStatus.ACTIVE,
      stats = UserStats(
        matchesPlayed = 16,
        wins = 4,
        kills = 38,
        points = 240,
        currentRank = "Crown II",
        bestFinish = "#1",
        currentStreak = 2
      )
    )

    val proUser2 = User(
      uid = "user_kabir",
      displayName = "Kabir Singh",
      email = "kabir@example.com",
      bgmiName = "GOD_KABIR",
      bgmiUid = "5991823741",
      role = UserRole.USER,
      virtualCoins = 75,
      accountStatus = AccountStatus.ACTIVE,
      stats = UserStats(matchesPlayed = 28, wins = 9, kills = 88, points = 510, currentRank = "Ace")
    )

    val proUser3 = User(
      uid = "user_rishi",
      displayName = "Rishi Raj",
      email = "rishi@example.com",
      bgmiName = "SOUL_HUNTER",
      bgmiUid = "5449201832",
      role = UserRole.USER,
      virtualCoins = 45,
      accountStatus = AccountStatus.ACTIVE,
      stats = UserStats(matchesPlayed = 20, wins = 5, kills = 56, points = 340, currentRank = "Crown IV")
    )

    _users.value = listOf(adminUser, demoUser, proUser2, proUser3)
    // Default logged in user for immediate seamless inspection
    _currentUser.value = demoUser

    // Seed Starting Balance Transaction for demo user
    _transactions.value = listOf(
      CoinTransaction(
        transactionId = "tx_welcome_01",
        uid = demoUser.uid,
        type = TransactionType.BONUS,
        amount = 5,
        balanceBefore = 0,
        balanceAfter = 5,
        reason = "Initial Account Activation Bonus",
        createdAt = System.currentTimeMillis() - 86400000L
      )
    )

    // 2. Seed Tournaments
    val now = System.currentTimeMillis()
    val t1 = Tournament(
      id = "trn_weekend_clash",
      title = "WEEKEND BATTLE ROYALE",
      description = "Official Free-Entry BGMI Squad Championship. Battle across Erangel for glory and virtual coin rewards.",
      mode = TournamentMode.SQUAD,
      map = "Erangel",
      status = TournamentStatus.REGISTRATION_OPEN,
      maxTeams = 100,
      registeredTeams = 64,
      startAt = now + (2 * 3600 * 1000L + 42 * 60 * 1000L), // ~2h 42m from now
      registrationDeadline = now + (2 * 3600 * 1000L),
      rules = listOf(
        "Only Mobile devices allowed (No Emulators/iPads).",
        "All 4 players must have registered BGMI UID matching in-game profile.",
        "Teaming up with other teams results in immediate disqualification.",
        "Room ID and Password will be posted in Match Details 15 minutes before start.",
        "Fair play strictly enforced. System recordings are monitored."
      ),
      rewardCoins = 100,
      entryRequirement = "FREE ENTRY",
      scoringConfig = ScoringConfig()
    )

    val t2 = Tournament(
      id = "trn_solo_showdown",
      title = "SOLO SNIPER SHOWDOWN",
      description = "Intense solo tactical tournament on Miramar. Eliminate opponents and survive to claim virtual coin dominance.",
      mode = TournamentMode.SOLO,
      map = "Miramar",
      status = TournamentStatus.REGISTRATION_OPEN,
      maxTeams = 80,
      registeredTeams = 38,
      startAt = now + 18 * 3600 * 1000L,
      registrationDeadline = now + 16 * 3600 * 1000L,
      rules = listOf(
        "Solo mode only. Zero alliances allowed.",
        "Any third-party crosshairs or scripts result in immediate ban.",
        "Players must join the custom room within 10 minutes of release."
      ),
      rewardCoins = 50,
      entryRequirement = "FREE ENTRY"
    )

    val t3 = Tournament(
      id = "trn_duo_domination",
      title = "DUO APEX RIVALS",
      description = "Partner up for high-octane duo combat in Sanhok's dense rainforests. Dynamic placement and kill multiplier.",
      mode = TournamentMode.DUO,
      map = "Sanhok",
      status = TournamentStatus.LIVE,
      maxTeams = 50,
      registeredTeams = 50,
      startAt = now - 15 * 60 * 1000L,
      registrationDeadline = now - 30 * 60 * 1000L,
      rules = listOf(
        "Both players must join on time.",
        "Strict 1 kill point per verified elimination.",
        "Disputes must be submitted within 30 minutes of match end."
      ),
      rewardCoins = 75,
      entryRequirement = "FREE ENTRY"
    )

    val t4 = Tournament(
      id = "trn_classic_tdm",
      title = "WAREHOUSE TDM CLASH",
      description = "4v4 close-quarters combat tournament. Rapid eliminations with virtual coin bounties.",
      mode = TournamentMode.TDM,
      map = "Warehouse",
      status = TournamentStatus.UPCOMING,
      maxTeams = 32,
      registeredTeams = 18,
      startAt = now + 48 * 3600 * 1000L,
      registrationDeadline = now + 44 * 3600 * 1000L,
      rules = listOf(
        "First team to reach 40 eliminations advances.",
        "No slide or glitch abuse allowed."
      ),
      rewardCoins = 40,
      entryRequirement = "FREE ENTRY"
    )

    _tournaments.value = listOf(t1, t2, t3, t4)

    // 3. Seed Teams
    val sampleTeam = Team(
      teamId = "tm_viper_squad",
      tournamentId = t1.id,
      teamName = "VIPER ESPORTS",
      captainUid = demoUser.uid,
      players = listOf(
        TeamPlayer(name = "Ahmed (Captain)", bgmiUid = demoUser.bgmiUid, isCaptain = true),
        TeamPlayer(name = "Kabir", bgmiUid = "5991823741"),
        TeamPlayer(name = "Rishi", bgmiUid = "5449201832"),
        TeamPlayer(name = "Aman", bgmiUid = "5118273940")
      )
    )
    _teams.value = listOf(sampleTeam)

    // 4. Seed Matches (Upcoming, Live, Completed)
    val upcomingMatch = Match(
      matchId = "m_up_101",
      tournamentId = t1.id,
      tournamentTitle = t1.title,
      matchNumber = 1,
      map = t1.map,
      mode = t1.mode,
      scheduledTime = t1.startAt,
      status = MatchStatus.SCHEDULED,
      roomId = "ARENA-4921",
      roomPassword = "bgmi-pass-78",
      isRoomPublished = false, // Securely hidden until admin releases
      participatingTeamIds = listOf(sampleTeam.teamId),
      rules = t1.rules,
      announcements = listOf(
        "Official Stream link will be shared 10 minutes prior.",
        "Be seated in your assigned slot number matching registration ID."
      )
    )

    val liveMatch = Match(
      matchId = "m_live_202",
      tournamentId = t3.id,
      tournamentTitle = t3.title,
      matchNumber = 2,
      map = t3.map,
      mode = t3.mode,
      scheduledTime = now - 15 * 60 * 1000L,
      status = MatchStatus.LIVE,
      roomId = "LIVE-ROOM-882",
      roomPassword = "duo-rivals-99",
      isRoomPublished = true,
      roomPublishedAt = now - 25 * 60 * 1000L,
      rules = t3.rules,
      announcements = listOf("Match in progress. Spectators active on caster stream.")
    )

    val completedMatch = Match(
      matchId = "m_comp_303",
      tournamentId = "trn_past_championship",
      tournamentTitle = "MIDWEEK MAYHEM FINALS",
      matchNumber = 3,
      map = "Erangel",
      mode = TournamentMode.SQUAD,
      scheduledTime = now - 86400000L,
      status = MatchStatus.COMPLETED,
      roomId = "ROOM-PAST-11",
      roomPassword = "done",
      isRoomPublished = true,
      results = listOf(
        MatchTeamResult("SOUL ESPORTS", 1, 14, 20, 14, bonus = 5, totalPoints = 39),
        MatchTeamResult("VIPER ESPORTS", 2, 8, 15, 8, bonus = 1, totalPoints = 24),
        MatchTeamResult("TEAM GODLIKE", 3, 9, 12, 9, bonus = 0, totalPoints = 21),
        MatchTeamResult("BLIND ESPORTS", 4, 6, 10, 6, bonus = 0, totalPoints = 16),
        MatchTeamResult("REVENANT", 5, 4, 8, 4, bonus = 0, totalPoints = 12)
      )
    )

    _matches.value = listOf(upcomingMatch, liveMatch, completedMatch)

    // 5. Seed Leaderboard
    _leaderboard.value = listOf(
      LeaderboardEntry(rank = 1, teamOrPlayerName = "SOUL ESPORTS", bgmiUid = "5991823741", kills = 46, placementPts = 65, totalPoints = 111, matchesPlayed = 4, wins = 3),
      LeaderboardEntry(rank = 2, teamOrPlayerName = "VIPER ESPORTS", bgmiUid = demoUser.bgmiUid, kills = 38, placementPts = 52, totalPoints = 90, matchesPlayed = 4, wins = 1),
      LeaderboardEntry(rank = 3, teamOrPlayerName = "TEAM GODLIKE", bgmiUid = "5449201832", kills = 34, placementPts = 45, totalPoints = 79, matchesPlayed = 4, wins = 1),
      LeaderboardEntry(rank = 4, teamOrPlayerName = "BLIND ESPORTS", bgmiUid = "5118273940", kills = 28, placementPts = 38, totalPoints = 66, matchesPlayed = 4, wins = 0),
      LeaderboardEntry(rank = 5, teamOrPlayerName = "REVENANT", bgmiUid = "5882910471", kills = 24, placementPts = 32, totalPoints = 56, matchesPlayed = 4, wins = 0),
      LeaderboardEntry(rank = 6, teamOrPlayerName = "GLOBAL ESPORTS", bgmiUid = "5772910381", kills = 19, placementPts = 26, totalPoints = 45, matchesPlayed = 3, wins = 0),
      LeaderboardEntry(rank = 7, teamOrPlayerName = "ORANGUTAN", bgmiUid = "5662910291", kills = 16, placementPts = 22, totalPoints = 38, matchesPlayed = 3, wins = 0)
    )

    // 6. Seed Announcements
    _announcements.value = listOf(
      Announcement(
        id = "ann_1",
        title = "Weekend Battle Royale Registrations Open",
        message = "Free registration is now open for Weekend Battle Royale (Squad). Grab your slot before capacity is reached!",
        important = true
      ),
      Announcement(
        id = "ann_2",
        title = "Fair Play & Anti-Teaming Policy",
        message = "Reminder: All players must ensure BGMI UID matches registration. Any emulator detection will lead to immediate match ban.",
        important = false
      )
    )

    // 7. Seed Notifications
    _notifications.value = listOf(
      NotificationItem(
        id = "n_1",
        recipientUid = demoUser.uid,
        title = "Welcome to BGMI ARENA!",
        body = "Your account has been activated with 5 virtual coins. Check out upcoming tournaments!",
        type = NotificationType.SYSTEM,
        read = false,
        createdAt = now - 3600000L
      ),
      NotificationItem(
        id = "n_2",
        recipientUid = demoUser.uid,
        title = "Tournament Registered",
        body = "You are confirmed for WEEKEND BATTLE ROYALE with team VIPER ESPORTS.",
        type = NotificationType.TOURNAMENT_OPEN,
        read = false,
        createdAt = now - 1800000L
      )
    )

    // 8. Seed Audit Logs
    _auditLogs.value = listOf(
      AuditLog(
        id = "aud_init_1",
        actorUid = adminUser.uid,
        action = "TOURNAMENT_CREATED",
        targetType = "TOURNAMENT",
        targetId = t1.id,
        metadata = "Initial tournament seed published",
        timestamp = now - 7200000L
      ),
      AuditLog(
        id = "aud_init_2",
        actorUid = adminUser.uid,
        action = "ROOM_PUBLISHED",
        targetType = "MATCH",
        targetId = liveMatch.matchId,
        metadata = "Room credentials issued for live match",
        timestamp = now - 1500000L
      )
    )
  }
}
