package com.example.model

enum class UserRole {
    PLAYER,
    ADMIN
}

enum class ContestMode {
    FREE_CONTEST,
    VIRTUAL_POINTS,
    PROMOTIONAL_REWARD,
    APPROVED_PAID_CONTEST // Disabled by default per PRD Section 3 & 83
}

enum class ContestStatus {
    DRAFT,
    UPCOMING,
    OPEN,
    FULL,
    MATCHING,
    LIVE,
    COMPLETED,
    CANCELLED,
    SUSPENDED
}

enum class MatchStatus {
    MATCH_CREATED,
    WAITING,
    MATCHING,
    READY,
    COUNTDOWN,
    LIVE,
    FINISHING,
    RESULT_PROCESSING,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

enum class TransactionType {
    CONTEST_WIN,
    CONTEST_ENTRY,
    BONUS,
    REFUND,
    ADMIN_ADJUSTMENT,
    DRAW,
    PROMOTION
}

enum class DisputeReason(val title: String) {
    OPPONENT_CHEATING("Opponent Cheating"),
    INCORRECT_SCORE("Incorrect Score"),
    GAME_CRASHED("Game Crashed"),
    CONNECTION_PROBLEM("Connection Problem"),
    INCORRECT_RESULT("Incorrect Result"),
    OTHER("Other")
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val phone: String,
    val email: String,
    val level: Int = 1,
    val xp: Int = 0,
    val points: Int = 2450,
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val role: UserRole = UserRole.PLAYER,
    val isSuspended: Boolean = false,
    val isBlocked: Boolean = false,
    val avatar: String = "avatar_1"
) {
    val winRate: Float
        get() = if (matchesPlayed > 0) (wins.toFloat() / matchesPlayed) * 100f else 0f
}

data class Game(
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val category: String,
    val durationSeconds: Int = 300,
    val maxScore: Int = 1000,
    val status: String = "ACTIVE",
    val version: String = "v1.0"
)

data class Contest(
    val id: String,
    val name: String,
    val gameId: String,
    val gameName: String,
    val contestMode: ContestMode = ContestMode.FREE_CONTEST,
    val durationSeconds: Int = 300,
    val maxPlayers: Int = 2,
    val rewardPoints: Int = 150,
    val entryFeePoints: Int = 0,
    val minimumLevel: Int = 1,
    val maximumLevel: Int = 99,
    val status: ContestStatus = ContestStatus.OPEN,
    val createdBy: String = "admin",
    val tier: String = "Silver",
    val activeParticipants: Int = 0
)

data class MatchPlayer(
    val userId: String,
    val username: String,
    val avatar: String,
    val score: Int = 0,
    val isWinner: Boolean = false,
    val isConnected: Boolean = true
)

data class GameEvent(
    val id: String,
    val matchId: String,
    val userId: String,
    val eventType: String,
    val eventData: String,
    val clientTimestamp: Long,
    val serverTimestamp: Long
)

data class Match(
    val id: String,
    val contestId: String,
    val contestName: String,
    val gameId: String,
    val gameName: String,
    val durationSeconds: Int,
    val rewardPoints: Int,
    val status: MatchStatus,
    val playerA: MatchPlayer,
    val playerB: MatchPlayer,
    val serverStartTime: Long,
    val serverEndTime: Long,
    val winnerId: String? = null,
    val isDraw: Boolean = false,
    val events: List<GameEvent> = emptyList(),
    val riskScore: Int = 0
)

data class RewardTransaction(
    val id: String,
    val userId: String,
    val type: TransactionType,
    val amount: Int,
    val referenceType: String,
    val referenceId: String,
    val status: String = "COMPLETED",
    val timestamp: Long = System.currentTimeMillis()
)

data class Dispute(
    val id: String,
    val matchId: String,
    val reporterId: String,
    val reporterName: String,
    val reason: DisputeReason,
    val description: String,
    val status: String = "PENDING", // PENDING, RESOLVED, REJECTED
    val adminNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val username: String,
    val points: Int,
    val wins: Int,
    val winRate: Float,
    val avatar: String
)

data class PlatformSettings(
    val paidContestsEnabled: Boolean = false, // Strictly false per PRD Section 3 & 83
    val minimumAge: Int = 18,
    val maintenanceMode: Boolean = false,
    val defaultContestDuration: Int = 300,
    val defaultRewardPoints: Int = 150,
    val supportEmail: String = "support@contestarena.com"
)
