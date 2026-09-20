package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.ServerEngine
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
import com.example.model.User
import com.example.model.UserRole
import com.example.repository.ContestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AppScreen {
    object Home : AppScreen()
    object Contests : AppScreen()
    object Leaderboard : AppScreen()
    object History : AppScreen()
    object Profile : AppScreen()
    data class ContestDetail(val contest: Contest) : AppScreen()
    data class Matchmaking(val contest: Contest) : AppScreen()
    data class ActiveGameRoom(val match: Match) : AppScreen()
    data class MatchResult(val match: Match) : AppScreen()
    data class DisputeReport(val matchId: String) : AppScreen()
    object AdminLogin : AppScreen()
    object AdminDashboard : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ContestRepository(application)

    val currentUser: StateFlow<User> = repository.currentUser
    val platformSettings: StateFlow<PlatformSettings> = repository.platformSettings
    val games: StateFlow<List<Game>> = repository.games

    val contests: StateFlow<List<Contest>> = repository.getContests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matchHistory: StateFlow<List<Match>> = repository.getMatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminMatches: StateFlow<List<Match>> = repository.getAllMatchesForAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<RewardTransaction>> = repository.getTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disputes: StateFlow<List<Dispute>> = repository.getDisputes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Navigation Screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Matchmaking State (PRD Section 10)
    private val _matchmakingStatus = MutableStateFlow<String>("Searching for worthy challenger...")
    val matchmakingStatus: StateFlow<String> = _matchmakingStatus.asStateFlow()

    private val _countdownValue = MutableStateFlow<Int?>(null)
    val countdownValue: StateFlow<Int?> = _countdownValue.asStateFlow()

    // Active Game State
    private val _activeMatch = MutableStateFlow<Match?>(null)
    val activeMatch: StateFlow<Match?> = _activeMatch.asStateFlow()

    private val _remainingSeconds = MutableStateFlow<Int>(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    // Reaction Game specifics (PRD Section 73)
    private val _reactionTargetActive = MutableStateFlow<Boolean>(false)
    val reactionTargetActive: StateFlow<Boolean> = _reactionTargetActive.asStateFlow()

    private val _targetPositionX = MutableStateFlow<Float>(0.5f)
    val targetPositionX: StateFlow<Float> = _targetPositionX.asStateFlow()

    private val _targetPositionY = MutableStateFlow<Float>(0.5f)
    val targetPositionY: StateFlow<Float> = _targetPositionY.asStateFlow()

    private val _lastActionFeedback = MutableStateFlow<String?>(null)
    val lastActionFeedback: StateFlow<String?> = _lastActionFeedback.asStateFlow()

    private var targetSpawnTime: Long = 0L
    private var lastEventTime: Long = 0L
    private var gameJob: Job? = null
    private var timerJob: Job? = null
    private var opponentJob: Job? = null

    // Admin 8-Digit Code Authentication
    val adminSecurityCode = "84920153" // 8-digit secure administrator key
    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    // Ludo Game specifics
    private val _diceValue = MutableStateFlow<Int>(1)
    val diceValue: StateFlow<Int> = _diceValue.asStateFlow()

    private val _isRollingDice = MutableStateFlow<Boolean>(false)
    val isRollingDice: StateFlow<Boolean> = _isRollingDice.asStateFlow()

    // Player tokens (0..3): position on track (0 = at base/home yard, 1..56 on track, 57 = Home)
    private val _playerTokens = MutableStateFlow<List<Int>>(listOf(0, 0, 0, 0))
    val playerTokens: StateFlow<List<Int>> = _playerTokens.asStateFlow()

    // Opponent tokens (0..3): position on track (0 = base, 1..56 on track, 57 = Home)
    private val _opponentTokens = MutableStateFlow<List<Int>>(listOf(0, 0, 0, 0))
    val opponentTokens: StateFlow<List<Int>> = _opponentTokens.asStateFlow()

    private val _isPlayerTurn = MutableStateFlow<Boolean>(true)
    val isPlayerTurn: StateFlow<Boolean> = _isPlayerTurn.asStateFlow()

    private var matchmakingJob: Job? = null

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun requestAdminAccess() {
        if (currentUser.value.role == UserRole.ADMIN) {
            _currentScreen.value = AppScreen.AdminDashboard
        } else {
            _adminAuthError.value = null
            _currentScreen.value = AppScreen.AdminLogin
        }
    }

    fun verifyAdminCode(enteredCode: String): Boolean {
        return if (enteredCode.trim() == adminSecurityCode) {
            _adminAuthError.value = null
            repository.setUserRole(UserRole.ADMIN)
            _currentScreen.value = AppScreen.AdminDashboard
            true
        } else {
            _adminAuthError.value = "Invalid 8-digit admin code. Access denied."
            false
        }
    }

    fun exitAdminMode() {
        repository.setUserRole(UserRole.PLAYER)
        _currentScreen.value = AppScreen.Home
    }

    fun toggleRole() {
        if (currentUser.value.role == UserRole.ADMIN) {
            exitAdminMode()
        } else {
            requestAdminAccess()
        }
    }

    // ---------------- Matchmaking & Game Flow ----------------

    fun startMatchmaking(contest: Contest) {
        matchmakingJob?.cancel()
        _currentScreen.value = AppScreen.Matchmaking(contest)
        _matchmakingStatus.value = "Connecting to matchmaking server..."
        _countdownValue.value = null

        matchmakingJob = viewModelScope.launch {
            delay(1200)
            _matchmakingStatus.value = "Searching for opponent in ${contest.tier} tier..."
            delay(1800)
            _matchmakingStatus.value = "Opponent found: CyberStriker (Lvl 11)"
            delay(1000)

            // 3-2-1 Countdown (PRD Section 10 & 11)
            for (i in 3 downTo 1) {
                _countdownValue.value = i
                delay(900)
            }
            _countdownValue.value = 0 // "GO!"
            delay(600)

            launchGame(contest)
        }
    }

    fun cancelMatchmaking() {
        matchmakingJob?.cancel()
        matchmakingJob = null
        _currentScreen.value = AppScreen.Home
    }

    private fun launchGame(contest: Contest) {
        val user = currentUser.value
        val now = System.currentTimeMillis()
        val duration = contest.durationSeconds
        val matchId = "M_${System.currentTimeMillis().toString().takeLast(6)}"

        val match = Match(
            id = matchId,
            contestId = contest.id,
            contestName = contest.name,
            gameId = contest.gameId,
            gameName = contest.gameName,
            durationSeconds = duration,
            rewardPoints = contest.rewardPoints,
            status = MatchStatus.LIVE,
            playerA = MatchPlayer(
                userId = user.id,
                username = user.username,
                avatar = user.avatar,
                score = 0
            ),
            playerB = MatchPlayer(
                userId = "opp_101",
                username = "CyberStriker",
                avatar = "avatar_2",
                score = 0
            ),
            serverStartTime = now,
            serverEndTime = now + (duration * 1000L)
        )

        _activeMatch.value = match
        _remainingSeconds.value = duration
        _currentScreen.value = AppScreen.ActiveGameRoom(match)
        _lastActionFeedback.value = null

        // Reset Ludo states if Ludo contest
        if (contest.gameId == "game_ludo") {
            _playerTokens.value = listOf(0, 0, 0, 0)
            _opponentTokens.value = listOf(0, 0, 0, 0)
            _diceValue.value = 1
            _isRollingDice.value = false
            _isPlayerTurn.value = true
        }

        startGameLoops(match)
    }

    private fun startGameLoops(match: Match) {
        gameJob?.cancel()
        timerJob?.cancel()
        opponentJob?.cancel()

        // Authoritative Server Timer Loop (PRD Section 13)
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                val currentM = _activeMatch.value ?: break
                val now = System.currentTimeMillis()
                val left = ((currentM.serverEndTime - now) / 1000).toInt()
                _remainingSeconds.value = maxOf(0, left)

                if (left <= 0) {
                    endMatch(currentM)
                    break
                }
            }
        }

        val isLudo = match.gameId == "game_ludo"

        if (!isLudo) {
            // Reaction target spawner loop
            gameJob = viewModelScope.launch {
                while (_remainingSeconds.value > 0) {
                    delay((1000..2500).random().toLong())
                    if (_remainingSeconds.value <= 0) break

                    _targetPositionX.value = (0.15f..0.85f).random()
                    _targetPositionY.value = (0.2f..0.8f).random()
                    targetSpawnTime = System.currentTimeMillis()
                    _reactionTargetActive.value = true

                    // Target disappears after 1.5 seconds if missed
                    delay(1500)
                    if (_reactionTargetActive.value) {
                        _reactionTargetActive.value = false
                        _lastActionFeedback.value = "Missed! -5"
                        applyPlayerScorePenalty(5)
                    }
                }
            }

            // Opponent reaction gameplay loop
            opponentJob = viewModelScope.launch {
                while (_remainingSeconds.value > 0) {
                    delay((1500..3200).random().toLong())
                    val m = _activeMatch.value ?: break
                    val newOppScore = ServerEngine.generateOpponentAction(m.playerB.score)
                    _activeMatch.value = m.copy(
                        playerB = m.playerB.copy(score = newOppScore)
                    )
                }
            }
        } else {
            // Opponent Ludo turn loop when not player's turn or periodically
            opponentJob = viewModelScope.launch {
                while (_remainingSeconds.value > 0) {
                    delay((3000..5000).random().toLong())
                    val m = _activeMatch.value ?: break
                    if (!_isPlayerTurn.value) {
                        val (newOppScore, feedbackText) = ServerEngine.generateOpponentLudoTurn(m.playerB.score)
                        _activeMatch.value = m.copy(
                            playerB = m.playerB.copy(score = newOppScore)
                        )
                        _lastActionFeedback.value = feedbackText

                        // Advance an opponent token randomly
                        val oppTokens = _opponentTokens.value.toMutableList()
                        val tokenIdx = (0..3).random()
                        val curPos = oppTokens[tokenIdx]
                        if (curPos == 0) {
                            oppTokens[tokenIdx] = 1 // Spawn token on track
                        } else if (curPos < 57) {
                            oppTokens[tokenIdx] = minOf(57, curPos + (1..6).random())
                        }
                        _opponentTokens.value = oppTokens

                        _isPlayerTurn.value = true
                    }
                }
            }
        }
    }

    // ---------------- Ludo Actions ----------------

    fun rollLudoDice() {
        if (_isRollingDice.value || !_isPlayerTurn.value) return
        viewModelScope.launch {
            _isRollingDice.value = true
            // Quick dice animation roll
            for (i in 1..4) {
                _diceValue.value = (1..6).random()
                delay(80)
            }
            val finalRoll = (1..6).random()
            _diceValue.value = finalRoll
            _isRollingDice.value = false

            _lastActionFeedback.value = "Rolled a $finalRoll! Tap a token to move"

            // Check if user has any legal moves
            val tokens = _playerTokens.value
            val canMoveAny = tokens.any { pos -> (pos == 0 && finalRoll == 6) || (pos > 0 && pos + finalRoll <= 57) }
            if (!canMoveAny) {
                _lastActionFeedback.value = "Rolled a $finalRoll - No moves available!"
                delay(1000)
                passTurnToOpponent()
            }
        }
    }

    fun moveLudoToken(tokenIndex: Int) {
        if (!_isPlayerTurn.value || _isRollingDice.value) return
        val currentTokens = _playerTokens.value.toMutableList()
        val curPos = currentTokens[tokenIndex]
        val roll = _diceValue.value

        if (curPos == 0) {
            if (roll == 6) {
                // Spawn token onto track (position 1)
                currentTokens[tokenIndex] = 1
                applyLudoMoveVerification(steps = 1, captured = false, reachedHome = false)
                _playerTokens.value = currentTokens
                _lastActionFeedback.value = "Token spawned! Roll again bonus!"
                // Roll again on a six!
                return
            } else {
                _lastActionFeedback.value = "Need a 6 to bring token out of base!"
                return
            }
        }

        if (curPos + roll > 57) {
            _lastActionFeedback.value = "Exact roll needed to enter Home!"
            return
        }

        val newPos = curPos + roll
        val reachedHome = newPos == 57
        val capturedOpponent = !reachedHome && (newPos % 7 == 0) // Capture opponent simulation

        currentTokens[tokenIndex] = newPos
        _playerTokens.value = currentTokens

        applyLudoMoveVerification(steps = roll, captured = capturedOpponent, reachedHome = reachedHome)

        if (capturedOpponent) {
            _lastActionFeedback.value = "BOOM! Captured opponent token! (+50 pts)"
        } else if (reachedHome) {
            _lastActionFeedback.value = "VICTORY! Token reached Home! (+100 pts)"
        } else {
            _lastActionFeedback.value = "Token moved $roll steps (+${roll * 5} pts)"
        }

        if (roll != 6) {
            passTurnToOpponent()
        } else {
            _lastActionFeedback.value = "${_lastActionFeedback.value} - Rolled 6: Roll again!"
        }
    }

    private fun passTurnToOpponent() {
        _isPlayerTurn.value = false
        viewModelScope.launch {
            delay(1200)
            val m = _activeMatch.value ?: return@launch
            val (newOppScore, feedbackText) = ServerEngine.generateOpponentLudoTurn(m.playerB.score)
            _activeMatch.value = m.copy(
                playerB = m.playerB.copy(score = newOppScore)
            )
            _lastActionFeedback.value = feedbackText

            // Opponent moves a token
            val oppTokens = _opponentTokens.value.toMutableList()
            val tokenIdx = (0..3).random()
            val curPos = oppTokens[tokenIdx]
            if (curPos == 0) {
                oppTokens[tokenIdx] = 1
            } else if (curPos < 57) {
                oppTokens[tokenIdx] = minOf(57, curPos + (1..6).random())
            }
            _opponentTokens.value = oppTokens

            delay(1000)
            _isPlayerTurn.value = true
        }
    }

    private fun applyLudoMoveVerification(steps: Int, captured: Boolean, reachedHome: Boolean) {
        val m = _activeMatch.value ?: return
        val now = System.currentTimeMillis()

        val validation = ServerEngine.processLudoMove(
            currentScore = m.playerA.score,
            diceRoll = _diceValue.value,
            capturedOpponent = captured,
            reachedHome = reachedHome,
            stepsMoved = steps,
            clientTimestamp = now,
            serverTimestamp = now,
            lastEventTimestamp = lastEventTime
        )
        lastEventTime = now

        if (validation.isValid) {
            _activeMatch.value = m.copy(
                playerA = m.playerA.copy(score = validation.newScore),
                riskScore = maxOf(m.riskScore, validation.riskScore)
            )
        } else {
            _lastActionFeedback.value = validation.reason
        }
    }

    fun onReactionTargetTapped() {
        if (!_reactionTargetActive.value) return
        _reactionTargetActive.value = false

        val m = _activeMatch.value ?: return
        val now = System.currentTimeMillis()
        val reactionTime = now - targetSpawnTime

        // Server-side anti-cheat & scoring verification (PRD Section 14, 19, 44)
        val validation = ServerEngine.processReactionEvent(
            currentScore = m.playerA.score,
            eventType = "REACTION_TAP",
            reactionTimeMs = reactionTime,
            isTargetHit = true,
            clientTimestamp = now,
            serverTimestamp = now,
            lastEventTimestamp = lastEventTime
        )
        lastEventTime = now

        if (validation.isValid) {
            val delta = validation.newScore - m.playerA.score
            _lastActionFeedback.value = if (delta >= 15) "PERFECT! +$delta ($reactionTime ms)" else "NICE! +$delta"
            _activeMatch.value = m.copy(
                playerA = m.playerA.copy(score = validation.newScore),
                riskScore = maxOf(m.riskScore, validation.riskScore)
            )
        } else {
            _lastActionFeedback.value = validation.reason
        }
    }

    private fun applyPlayerScorePenalty(penalty: Int) {
        val m = _activeMatch.value ?: return
        val newScore = maxOf(0, m.playerA.score - penalty)
        _activeMatch.value = m.copy(
            playerA = m.playerA.copy(score = newScore)
        )
    }

    private fun endMatch(match: Match) {
        gameJob?.cancel()
        timerJob?.cancel()
        opponentJob?.cancel()
        _reactionTargetActive.value = false

        // Server-side authoritative winner and reward calculation (PRD Section 15, 16, 20)
        val finalized = ServerEngine.finalizeMatch(
            match = match,
            finalScoreA = match.playerA.score,
            finalScoreB = match.playerB.score
        )

        _activeMatch.value = finalized

        viewModelScope.launch {
            repository.recordCompletedMatch(finalized)
            delay(500)
            _currentScreen.value = AppScreen.MatchResult(finalized)
        }
    }

    // Admin Operations (PRD Section 9, 30, 48)
    fun createContestAsAdmin(
        name: String,
        gameId: String,
        gameName: String,
        durationSeconds: Int,
        rewardPoints: Int,
        entryFeePoints: Int,
        tier: String
    ) {
        viewModelScope.launch {
            val newContest = Contest(
                id = "c_${System.currentTimeMillis()}",
                name = name,
                gameId = gameId,
                gameName = gameName,
                contestMode = if (entryFeePoints > 0) ContestMode.VIRTUAL_POINTS else ContestMode.FREE_CONTEST,
                durationSeconds = durationSeconds,
                maxPlayers = 2,
                rewardPoints = rewardPoints,
                entryFeePoints = entryFeePoints,
                minimumLevel = 1,
                maximumLevel = 99,
                status = ContestStatus.OPEN,
                createdBy = "Admin",
                tier = tier,
                activeParticipants = 0
            )
            repository.createContest(newContest)
        }
    }

    fun toggleContestStatus(contestId: String, currentStatus: ContestStatus) {
        viewModelScope.launch {
            val next = if (currentStatus == ContestStatus.OPEN) ContestStatus.SUSPENDED else ContestStatus.OPEN
            repository.toggleContestStatus(contestId, next)
        }
    }

    fun submitDispute(matchId: String, reason: DisputeReason, description: String) {
        viewModelScope.launch {
            repository.submitDispute(matchId, reason, description)
            _currentScreen.value = AppScreen.History
        }
    }

    fun resolveDispute(disputeId: String, status: String, notes: String) {
        viewModelScope.launch {
            repository.resolveDispute(disputeId, status, notes)
        }
    }
}

private fun ClosedFloatingPointRange<Float>.random(): Float {
    return start + (endInclusive - start) * kotlin.random.Random.nextFloat()
}
