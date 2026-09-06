package com.example.data.model

enum class UserRole {
  USER,
  ADMIN
}

enum class AccountStatus {
  ACTIVE,
  SUSPENDED,
  BANNED
}

data class UserStats(
  val matchesPlayed: Int = 0,
  val wins: Int = 0,
  val kills: Int = 0,
  val points: Int = 0,
  val currentRank: String = "Bronze III",
  val bestFinish: String = "#1",
  val currentStreak: Int = 0
)

data class UserPreferences(
  val notifications: Boolean = true,
  val sound: Boolean = true,
  val reducedMotion: Boolean = false
)

data class User(
  val uid: String,
  val displayName: String,
  val email: String,
  val bgmiName: String,
  val bgmiUid: String,
  val avatarUrl: String = "",
  val role: UserRole = UserRole.USER,
  val virtualCoins: Int = 5,
  val accountStatus: AccountStatus = AccountStatus.ACTIVE,
  val stats: UserStats = UserStats(),
  val preferences: UserPreferences = UserPreferences(),
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

enum class TournamentMode {
  ALL,
  SOLO,
  DUO,
  SQUAD,
  TDM
}

enum class TournamentStatus {
  UPCOMING,
  REGISTRATION_OPEN,
  FULL,
  LIVE,
  COMPLETED,
  CANCELLED
}

data class ScoringConfig(
  val placementPoints: Map<Int, Int> = mapOf(
    1 to 20,
    2 to 15,
    3 to 12,
    4 to 10,
    5 to 8,
    6 to 6,
    7 to 4,
    8 to 2
  ),
  val killPointValue: Int = 1
)

data class Tournament(
  val id: String,
  val title: String,
  val description: String,
  val mode: TournamentMode,
  val map: String,
  val status: TournamentStatus,
  val maxTeams: Int,
  val registeredTeams: Int,
  val startAt: Long,
  val registrationDeadline: Long,
  val rules: List<String>,
  val rewardCoins: Int = 100,
  val entryRequirement: String = "FREE ENTRY",
  val bannerUrl: String = "",
  val scoringConfig: ScoringConfig = ScoringConfig(),
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "ADMIN"
)

data class TeamPlayer(
  val name: String,
  val bgmiUid: String,
  val isCaptain: Boolean = false
)

data class Team(
  val teamId: String,
  val tournamentId: String,
  val teamName: String,
  val captainUid: String,
  val players: List<TeamPlayer>,
  val status: String = "REGISTERED",
  val createdAt: Long = System.currentTimeMillis()
)

enum class MatchStatus {
  SCHEDULED,
  ROOM_PENDING,
  ROOM_PUBLISHED,
  LIVE,
  COMPLETED,
  CANCELLED
}

data class MatchTeamResult(
  val teamName: String,
  val placement: Int,
  val kills: Int,
  val placementPoints: Int,
  val killPoints: Int,
  val bonus: Int = 0,
  val penalties: Int = 0,
  val totalPoints: Int
)

data class Match(
  val matchId: String,
  val tournamentId: String,
  val tournamentTitle: String,
  val matchNumber: Int,
  val map: String,
  val mode: TournamentMode,
  val scheduledTime: Long,
  val status: MatchStatus,
  val roomId: String = "",
  val roomPassword: String = "",
  val isRoomPublished: Boolean = false,
  val roomPublishedAt: Long = 0L,
  val participatingTeamIds: List<String> = emptyList(),
  val rules: List<String> = emptyList(),
  val announcements: List<String> = emptyList(),
  val results: List<MatchTeamResult> = emptyList()
)

data class LeaderboardEntry(
  val rank: Int,
  val teamOrPlayerName: String,
  val bgmiUid: String,
  val kills: Int,
  val placementPts: Int,
  val totalPoints: Int,
  val matchesPlayed: Int = 1,
  val wins: Int = 0,
  val type: String = "GLOBAL" // GLOBAL, TOURNAMENT, SEASON
)

enum class TransactionType {
  REWARD,
  BONUS,
  ADJUSTMENT,
  ENTRY_DEDUCTION
}

data class CoinTransaction(
  val transactionId: String,
  val uid: String,
  val type: TransactionType,
  val amount: Int,
  val balanceBefore: Int,
  val balanceAfter: Int,
  val reason: String,
  val referenceId: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val status: String = "SUCCESS"
)

enum class NotificationType {
  TOURNAMENT_OPEN,
  MATCH_REMINDER,
  ROOM_RELEASED,
  RESULT_PUBLISHED,
  REWARD_RECEIVED,
  ANNOUNCEMENT,
  SYSTEM
}

data class NotificationItem(
  val id: String,
  val recipientUid: String,
  val title: String,
  val body: String,
  val type: NotificationType,
  val tournamentId: String? = null,
  val matchId: String? = null,
  val read: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

enum class TicketStatus {
  OPEN,
  IN_PROGRESS,
  RESOLVED
}

data class SupportTicket(
  val ticketId: String,
  val userId: String,
  val userName: String,
  val category: String,
  val subject: String,
  val message: String,
  val status: TicketStatus = TicketStatus.OPEN,
  val createdAt: Long = System.currentTimeMillis(),
  val adminNotes: String = ""
)

data class Announcement(
  val id: String,
  val title: String,
  val message: String,
  val target: String = "ALL_USERS",
  val createdAt: Long = System.currentTimeMillis(),
  val important: Boolean = false
)

data class AuditLog(
  val id: String,
  val actorUid: String,
  val action: String,
  val targetType: String,
  val targetId: String,
  val timestamp: Long = System.currentTimeMillis(),
  val metadata: String = ""
)
