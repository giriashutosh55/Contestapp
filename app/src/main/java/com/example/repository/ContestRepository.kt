package com.example.repository

import android.content.Context
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.ContestEntity
import com.example.data.DisputeEntity
import com.example.data.MatchEntity
import com.example.data.RewardTransactionEntity
import com.example.data.UserEntity
import com.example.model.Contest
import com.example.model.ContestMode
import com.example.model.ContestStatus
import com.example.model.Dispute
import com.example.model.DisputeReason
import com.example.model.Game
import com.example.model.LeaderboardEntry
import com.example.model.Match
import com.example.model.MatchPlayer
import com.example.model.MatchStatus
import com.example.model.PlatformSettings
import com.example.model.RewardTransaction
import com.example.model.TransactionType
import com.example.model.User
import com.example.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class ContestRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "contest_arena_db"
    ).build()

    private val userDao = db.userDao()
    private val contestDao = db.contestDao()
    private val matchDao = db.matchDao()
    private val rewardDao = db.rewardTransactionDao()
    private val disputeDao = db.disputeDao()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Current Session State
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_ashutosh",
            name = "Ashutosh Giri",
            username = "Ashutosh",
            phone = "+91 9876543210",
            email = "giriashutoshgiri34@gmail.com",
            level = 12,
            xp = 1850,
            points = 2450,
            matchesPlayed = 128,
            wins = 84,
            losses = 40,
            draws = 4,
            role = UserRole.PLAYER
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Platform Settings (PRD Section 3 & 83: paidContestsEnabled = false)
    private val _platformSettings = MutableStateFlow(
        PlatformSettings(
            paidContestsEnabled = false,
            minimumAge = 18,
            maintenanceMode = false,
            defaultContestDuration = 300,
            defaultRewardPoints = 150
        )
    )
    val platformSettings: StateFlow<PlatformSettings> = _platformSettings.asStateFlow()

    // Supported Games List (PRD Section 31, 73, 74)
    private val _games = MutableStateFlow(
        listOf(
            Game(
                id = "game_reaction",
                name = "Reaction Master",
                slug = "reaction",
                description = "Test your reflex speed against your opponent. High precision tap challenge!",
                category = "Reaction",
                durationSeconds = 180,
                maxScore = 1000,
                status = "ACTIVE"
            ),
            Game(
                id = "game_ludo",
                name = "Ludo Master 1v1",
                slug = "ludo",
                description = "Classic 1-vs-1 speed Ludo showdown. Roll dice, advance tokens, capture opponent, and reach Home!",
                category = "Ludo",
                durationSeconds = 180,
                maxScore = 500,
                status = "ACTIVE"
            ),
            Game(
                id = "game_quiz",
                name = "Esports Quiz Blitz",
                slug = "quiz",
                description = "Rapid 4-choice trivia questions with strict time bonuses.",
                category = "Quiz",
                durationSeconds = 180,
                maxScore = 500,
                status = "ACTIVE"
            ),
            Game(
                id = "game_carrom",
                name = "Carrom Strike 1v1",
                slug = "carrom",
                description = "Skill coin-pocketing duel with authentic pocket physics.",
                category = "Carrom",
                durationSeconds = 300,
                maxScore = 200,
                status = "ACTIVE"
            ),
            Game(
                id = "game_puzzle",
                name = "Speed Matrix Puzzle",
                slug = "puzzle",
                description = "Solve pattern puzzles faster than your live challenger.",
                category = "Puzzle",
                durationSeconds = 240,
                maxScore = 600,
                status = "ACTIVE"
            )
        )
    )
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    init {
        coroutineScope.launch {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        val u = _currentUser.value
        userDao.insertUser(
            UserEntity(
                id = u.id,
                name = u.name,
                username = u.username,
                phone = u.phone,
                email = u.email,
                level = u.level,
                xp = u.xp,
                points = u.points,
                matchesPlayed = u.matchesPlayed,
                wins = u.wins,
                losses = u.losses,
                draws = u.draws,
                role = u.role,
                isSuspended = u.isSuspended,
                isBlocked = u.isBlocked,
                avatar = u.avatar
            )
        )

        // Seed Contests across tiers per PRD Section 8
        val initialContests = listOf(
            ContestEntity(
                id = "c_beginner_1",
                name = "Reflex Quickfire (Beginner)",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                contestMode = ContestMode.FREE_CONTEST,
                durationSeconds = 60,
                maxPlayers = 2,
                rewardPoints = 25,
                entryFeePoints = 0,
                minimumLevel = 1,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Beginner",
                activeParticipants = 14
            ),
            ContestEntity(
                id = "c_bronze_1",
                name = "Speed Duel Bronze Cup",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                contestMode = ContestMode.VIRTUAL_POINTS,
                durationSeconds = 90,
                maxPlayers = 2,
                rewardPoints = 50,
                entryFeePoints = 10,
                minimumLevel = 2,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Bronze",
                activeParticipants = 28
            ),
            ContestEntity(
                id = "c_silver_1",
                name = "5-Minute Silver Arena",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                contestMode = ContestMode.VIRTUAL_POINTS,
                durationSeconds = 120,
                maxPlayers = 2,
                rewardPoints = 150,
                entryFeePoints = 25,
                minimumLevel = 3,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Silver",
                activeParticipants = 45
            ),
            ContestEntity(
                id = "c_gold_1",
                name = "Gold Championship Showdown",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                contestMode = ContestMode.VIRTUAL_POINTS,
                durationSeconds = 180,
                maxPlayers = 2,
                rewardPoints = 300,
                entryFeePoints = 50,
                minimumLevel = 5,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Gold",
                activeParticipants = 19
            ),
            ContestEntity(
                id = "c_platinum_1",
                name = "Platinum Masters Cup",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                contestMode = ContestMode.PROMOTIONAL_REWARD,
                durationSeconds = 300,
                maxPlayers = 2,
                rewardPoints = 750,
                entryFeePoints = 100,
                minimumLevel = 10,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Platinum",
                activeParticipants = 8
            ),
            ContestEntity(
                id = "c_ludo_beginner_1",
                name = "Ludo Quick Dice (Beginner)",
                gameId = "game_ludo",
                gameName = "Ludo Master 1v1",
                contestMode = ContestMode.FREE_CONTEST,
                durationSeconds = 90,
                maxPlayers = 2,
                rewardPoints = 40,
                entryFeePoints = 0,
                minimumLevel = 1,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Beginner",
                activeParticipants = 32
            ),
            ContestEntity(
                id = "c_ludo_silver_1",
                name = "Ludo Speed Clash (Silver)",
                gameId = "game_ludo",
                gameName = "Ludo Master 1v1",
                contestMode = ContestMode.VIRTUAL_POINTS,
                durationSeconds = 150,
                maxPlayers = 2,
                rewardPoints = 180,
                entryFeePoints = 30,
                minimumLevel = 2,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Silver",
                activeParticipants = 56
            ),
            ContestEntity(
                id = "c_ludo_gold_1",
                name = "Ludo Grandmaster Duel",
                gameId = "game_ludo",
                gameName = "Ludo Master 1v1",
                contestMode = ContestMode.VIRTUAL_POINTS,
                durationSeconds = 240,
                maxPlayers = 2,
                rewardPoints = 450,
                entryFeePoints = 75,
                minimumLevel = 4,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = "Gold",
                activeParticipants = 24
            )
        )
        contestDao.insertContests(initialContests)

        // Seed initial transaction history
        val now = System.currentTimeMillis()
        rewardDao.insertTransaction(
            RewardTransactionEntity(
                id = "tx_welcome",
                userId = u.id,
                type = TransactionType.BONUS,
                amount = 2000,
                referenceType = "WELCOME_BONUS",
                referenceId = "SYS_INIT",
                status = "COMPLETED",
                timestamp = now - 86400000L * 3
            )
        )
        rewardDao.insertTransaction(
            RewardTransactionEntity(
                id = "tx_win_1",
                userId = u.id,
                type = TransactionType.CONTEST_WIN,
                amount = 150,
                referenceType = "MATCH_WIN",
                referenceId = "M_PAST_01",
                status = "COMPLETED",
                timestamp = now - 86400000L * 2
            )
        )
        rewardDao.insertTransaction(
            RewardTransactionEntity(
                id = "tx_win_2",
                userId = u.id,
                type = TransactionType.CONTEST_WIN,
                amount = 300,
                referenceType = "MATCH_WIN",
                referenceId = "M_PAST_02",
                status = "COMPLETED",
                timestamp = now - 3600000L * 4
            )
        )

        // Seed matches for history
        matchDao.insertMatch(
            MatchEntity(
                id = "M_PAST_01",
                contestId = "c_silver_1",
                contestName = "5-Minute Silver Arena",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                durationSeconds = 120,
                rewardPoints = 150,
                status = MatchStatus.COMPLETED,
                playerAId = u.id,
                playerAName = u.name,
                playerAScore = 145,
                playerBId = "opp_101",
                playerBName = "CyberStriker",
                playerBScore = 120,
                serverStartTime = now - 86400000L * 2,
                serverEndTime = now - 86400000L * 2 + 120000L,
                winnerId = u.id,
                isDraw = false,
                riskScore = 0
            )
        )
        matchDao.insertMatch(
            MatchEntity(
                id = "M_PAST_02",
                contestId = "c_gold_1",
                contestName = "Gold Championship Showdown",
                gameId = "game_reaction",
                gameName = "Reaction Master",
                durationSeconds = 180,
                rewardPoints = 300,
                status = MatchStatus.COMPLETED,
                playerAId = u.id,
                playerAName = u.name,
                playerAScore = 160,
                playerBId = "opp_102",
                playerBName = "VortexGamer",
                playerBScore = 140,
                serverStartTime = now - 3600000L * 4,
                serverEndTime = now - 3600000L * 4 + 180000L,
                winnerId = u.id,
                isDraw = false,
                riskScore = 0
            )
        )
    }

    // Contests
    fun getContests(): Flow<List<Contest>> {
        return contestDao.getAllContests().map { entities ->
            entities.map { e ->
                Contest(
                    id = e.id,
                    name = e.name,
                    gameId = e.gameId,
                    gameName = e.gameName,
                    contestMode = e.contestMode,
                    durationSeconds = e.durationSeconds,
                    maxPlayers = e.maxPlayers,
                    rewardPoints = e.rewardPoints,
                    entryFeePoints = e.entryFeePoints,
                    minimumLevel = e.minimumLevel,
                    maximumLevel = e.maximumLevel,
                    status = e.status,
                    createdBy = e.createdBy,
                    tier = e.tier,
                    activeParticipants = e.activeParticipants
                )
            }
        }
    }

    suspend fun createContest(contest: Contest) {
        contestDao.insertContest(
            ContestEntity(
                id = contest.id,
                name = contest.name,
                gameId = contest.gameId,
                gameName = contest.gameName,
                contestMode = contest.contestMode,
                durationSeconds = contest.durationSeconds,
                maxPlayers = contest.maxPlayers,
                rewardPoints = contest.rewardPoints,
                entryFeePoints = contest.entryFeePoints,
                minimumLevel = contest.minimumLevel,
                maximumLevel = contest.maximumLevel,
                status = contest.status,
                createdBy = contest.createdBy,
                tier = contest.tier,
                activeParticipants = 0
            )
        )
    }

    suspend fun toggleContestStatus(contestId: String, newStatus: ContestStatus) {
        contestDao.updateContestStatus(contestId, newStatus)
    }

    // Matches & Transactions
    fun getMatchHistory(): Flow<List<Match>> {
        return matchDao.getMatchesForUser(_currentUser.value.id).map { list ->
            list.map { m ->
                Match(
                    id = m.id,
                    contestId = m.contestId,
                    contestName = m.contestName,
                    gameId = m.gameId,
                    gameName = m.gameName,
                    durationSeconds = m.durationSeconds,
                    rewardPoints = m.rewardPoints,
                    status = m.status,
                    playerA = MatchPlayer(userId = m.playerAId, username = m.playerAName, avatar = "avatar_1", score = m.playerAScore, isWinner = m.winnerId == m.playerAId),
                    playerB = MatchPlayer(userId = m.playerBId, username = m.playerBName, avatar = "avatar_2", score = m.playerBScore, isWinner = m.winnerId == m.playerBId),
                    serverStartTime = m.serverStartTime,
                    serverEndTime = m.serverEndTime,
                    winnerId = m.winnerId,
                    isDraw = m.isDraw,
                    riskScore = m.riskScore
                )
            }
        }
    }

    fun getAllMatchesForAdmin(): Flow<List<Match>> {
        return matchDao.getAllMatches().map { list ->
            list.map { m ->
                Match(
                    id = m.id,
                    contestId = m.contestId,
                    contestName = m.contestName,
                    gameId = m.gameId,
                    gameName = m.gameName,
                    durationSeconds = m.durationSeconds,
                    rewardPoints = m.rewardPoints,
                    status = m.status,
                    playerA = MatchPlayer(userId = m.playerAId, username = m.playerAName, avatar = "avatar_1", score = m.playerAScore, isWinner = m.winnerId == m.playerAId),
                    playerB = MatchPlayer(userId = m.playerBId, username = m.playerBName, avatar = "avatar_2", score = m.playerBScore, isWinner = m.winnerId == m.playerBId),
                    serverStartTime = m.serverStartTime,
                    serverEndTime = m.serverEndTime,
                    winnerId = m.winnerId,
                    isDraw = m.isDraw,
                    riskScore = m.riskScore
                )
            }
        }
    }

    suspend fun recordCompletedMatch(match: Match) {
        matchDao.insertMatch(
            MatchEntity(
                id = match.id,
                contestId = match.contestId,
                contestName = match.contestName,
                gameId = match.gameId,
                gameName = match.gameName,
                durationSeconds = match.durationSeconds,
                rewardPoints = match.rewardPoints,
                status = match.status,
                playerAId = match.playerA.userId,
                playerAName = match.playerA.username,
                playerAScore = match.playerA.score,
                playerBId = match.playerB.userId,
                playerBName = match.playerB.username,
                playerBScore = match.playerB.score,
                serverStartTime = match.serverStartTime,
                serverEndTime = match.serverEndTime,
                winnerId = match.winnerId,
                isDraw = match.isDraw,
                riskScore = match.riskScore
            )
        )

        // Authoritative ledger transaction if won or draw
        val current = _currentUser.value
        val isUserA = match.playerA.userId == current.id
        val userWon = match.winnerId == current.id

        if (userWon) {
            val rewardAmount = match.rewardPoints
            rewardDao.insertTransaction(
                RewardTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = current.id,
                    type = TransactionType.CONTEST_WIN,
                    amount = rewardAmount,
                    referenceType = "MATCH_WIN",
                    referenceId = match.id,
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Update user balance in DB & state
            userDao.recordMatchResult(
                userId = current.id,
                delta = rewardAmount,
                winDelta = 1,
                lossDelta = 0,
                drawDelta = 0,
                xpDelta = 50
            )

            _currentUser.value = current.copy(
                points = current.points + rewardAmount,
                matchesPlayed = current.matchesPlayed + 1,
                wins = current.wins + 1,
                xp = current.xp + 50
            )
        } else if (match.isDraw) {
            val drawAmount = match.rewardPoints / 2
            rewardDao.insertTransaction(
                RewardTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = current.id,
                    type = TransactionType.DRAW,
                    amount = drawAmount,
                    referenceType = "MATCH_DRAW",
                    referenceId = match.id,
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis()
                )
            )
            userDao.recordMatchResult(
                userId = current.id,
                delta = drawAmount,
                winDelta = 0,
                lossDelta = 0,
                drawDelta = 1,
                xpDelta = 25
            )
            _currentUser.value = current.copy(
                points = current.points + drawAmount,
                matchesPlayed = current.matchesPlayed + 1,
                draws = current.draws + 1,
                xp = current.xp + 25
            )
        } else {
            // Loss
            userDao.recordMatchResult(
                userId = current.id,
                delta = 0,
                winDelta = 0,
                lossDelta = 1,
                drawDelta = 0,
                xpDelta = 10
            )
            _currentUser.value = current.copy(
                matchesPlayed = current.matchesPlayed + 1,
                losses = current.losses + 1,
                xp = current.xp + 10
            )
        }
    }

    // Transactions Ledger (PRD Section 20, 21)
    fun getTransactions(): Flow<List<RewardTransaction>> {
        return rewardDao.getTransactionsForUser(_currentUser.value.id).map { list ->
            list.map {
                RewardTransaction(
                    id = it.id,
                    userId = it.userId,
                    type = it.type,
                    amount = it.amount,
                    referenceType = it.referenceType,
                    referenceId = it.referenceId,
                    status = it.status,
                    timestamp = it.timestamp
                )
            }
        }
    }

    // Disputes (PRD Section 28, 40)
    fun getDisputes(): Flow<List<Dispute>> {
        return disputeDao.getAllDisputes().map { list ->
            list.map {
                Dispute(
                    id = it.id,
                    matchId = it.matchId,
                    reporterId = it.reporterId,
                    reporterName = it.reporterName,
                    reason = DisputeReason.values().find { r -> r.name == it.reason } ?: DisputeReason.OTHER,
                    description = it.description,
                    status = it.status,
                    adminNotes = it.adminNotes,
                    timestamp = it.timestamp
                )
            }
        }
    }

    suspend fun submitDispute(matchId: String, reason: DisputeReason, description: String) {
        val current = _currentUser.value
        val dispute = DisputeEntity(
            id = "DSP_${System.currentTimeMillis()}",
            matchId = matchId,
            reporterId = current.id,
            reporterName = current.name,
            reason = reason.name,
            description = description,
            status = "PENDING",
            adminNotes = "",
            timestamp = System.currentTimeMillis()
        )
        disputeDao.insertDispute(dispute)
    }

    suspend fun resolveDispute(disputeId: String, status: String, notes: String) {
        disputeDao.resolveDispute(disputeId, status, notes)
    }

    // Leaderboard (PRD Section 24)
    fun getLeaderboard(): List<LeaderboardEntry> {
        val current = _currentUser.value
        return listOf(
            LeaderboardEntry(1, "u_pro1", "ApexTitan", 6450, 192, 78.4f, "avatar_3"),
            LeaderboardEntry(2, current.id, current.username + " (You)", current.points, current.wins, current.winRate, current.avatar),
            LeaderboardEntry(3, "u_pro2", "ShadowNinja", 2320, 76, 68.2f, "avatar_2"),
            LeaderboardEntry(4, "u_pro3", "Valkyrie", 2150, 71, 64.8f, "avatar_4"),
            LeaderboardEntry(5, "u_pro4", "HyperGamer", 1980, 62, 60.1f, "avatar_5"),
            LeaderboardEntry(6, "u_pro5", "NeonStriker", 1850, 58, 59.3f, "avatar_1"),
            LeaderboardEntry(7, "u_pro6", "PixelQueen", 1720, 52, 57.0f, "avatar_3")
        ).sortedByDescending { it.points }.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }
    }

    // Role Switching for Testing/Admin Experience (PRD Section 4 & 29)
    fun setUserRole(role: UserRole) {
        val current = _currentUser.value
        _currentUser.value = current.copy(role = role)
    }

    fun toggleUserRole() {
        val current = _currentUser.value
        val newRole = if (current.role == UserRole.PLAYER) UserRole.ADMIN else UserRole.PLAYER
        _currentUser.value = current.copy(role = newRole)
    }
}
