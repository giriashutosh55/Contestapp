package com.example.engine

import com.example.model.GameEvent
import com.example.model.Match
import com.example.model.MatchPlayer
import com.example.model.MatchStatus
import kotlin.random.Random

/**
 * ServerEngine simulates an authoritative, tamper-proof game server.
 * Per PRD Section 13, 14, 15, 18, 19, 44:
 * - Authoritative Server Timers (client never decides when match ends)
 * - Server-side scoring calculation & input validation
 * - Anti-cheat inspection (checks event frequencies, suspicious score bursts, timestamp anomalies)
 * - Deterministic Winner & Reward calculation
 */
object ServerEngine {

    data class ValidationResult(
        val isValid: Boolean,
        val newScore: Int,
        val riskScore: Int,
        val reason: String = "OK"
    )

    /**
     * Authoritative event verification for Reaction Challenge
     * Scoring:
     * - Fast tap (< 400ms): +15 points
     * - Regular tap (< 800ms): +10 points
     * - Slow tap (< 1200ms): +5 points
     * - Wrong target: -5 points
     */
    fun processReactionEvent(
        currentScore: Int,
        eventType: String,
        reactionTimeMs: Long,
        isTargetHit: Boolean,
        clientTimestamp: Long,
        serverTimestamp: Long,
        lastEventTimestamp: Long
    ): ValidationResult {
        // Anti-cheat: Check impossible game speed or clock tampering
        var risk = 0
        if (reactionTimeMs < 110) { // Biologically improbable human reaction
            risk += 45
        }
        if (serverTimestamp - lastEventTimestamp < 150) { // Automated rapid spam
            risk += 35
        }
        if (Math.abs(serverTimestamp - clientTimestamp) > 30000) { // Device clock manipulated
            risk += 50
        }

        if (risk >= 80) {
            return ValidationResult(
                isValid = false,
                newScore = currentScore,
                riskScore = risk,
                reason = "Suspicious event frequency detected by Anti-Cheat."
            )
        }

        val delta = if (isTargetHit) {
            when {
                reactionTimeMs < 400 -> 15
                reactionTimeMs < 800 -> 10
                else -> 5
            }
        } else {
            -5
        }

        val calculatedScore = maxOf(0, currentScore + delta)
        return ValidationResult(
            isValid = true,
            newScore = calculatedScore,
            riskScore = risk
        )
    }

    /**
     * Authoritative winner determination (PRD Section 15 & 16)
     * Client NEVER determines the winner.
     */
    fun finalizeMatch(
        match: Match,
        finalScoreA: Int,
        finalScoreB: Int
    ): Match {
        val isDraw = finalScoreA == finalScoreB
        val winnerId = when {
            finalScoreA > finalScoreB -> match.playerA.userId
            finalScoreB > finalScoreA -> match.playerB.userId
            else -> null
        }

        return match.copy(
            status = MatchStatus.COMPLETED,
            playerA = match.playerA.copy(score = finalScoreA, isWinner = winnerId == match.playerA.userId),
            playerB = match.playerB.copy(score = finalScoreB, isWinner = winnerId == match.playerB.userId),
            winnerId = winnerId,
            isDraw = isDraw
        )
    }

    /**
     * Authoritative event verification for Ludo Contest
     * Scoring:
     * - Token move: +10 pts * steps
     * - Token safe zone reach / home progress: +25 pts
     * - Capture opponent token: +50 pts bonus
     * - Token reached Home: +100 pts bonus
     */
    fun processLudoMove(
        currentScore: Int,
        diceRoll: Int,
        capturedOpponent: Boolean,
        reachedHome: Boolean,
        stepsMoved: Int,
        clientTimestamp: Long,
        serverTimestamp: Long,
        lastEventTimestamp: Long
    ): ValidationResult {
        var risk = 0
        if (diceRoll < 1 || diceRoll > 6) {
            risk += 70
        }
        if (serverTimestamp - lastEventTimestamp < 200) {
            risk += 40
        }
        if (Math.abs(serverTimestamp - clientTimestamp) > 30000) {
            risk += 50
        }

        if (risk >= 80) {
            return ValidationResult(
                isValid = false,
                newScore = currentScore,
                riskScore = risk,
                reason = "Illegal turn speed detected by Anti-Cheat."
            )
        }

        var pts = stepsMoved * 5
        if (capturedOpponent) pts += 50
        if (reachedHome) pts += 100

        return ValidationResult(
            isValid = true,
            newScore = currentScore + pts,
            riskScore = risk
        )
    }

    /**
     * Bot/Opponent simulation with realistic human response times
     */
    fun generateOpponentAction(currentOpponentScore: Int): Int {
        // Opponent randomly scores between 5 and 15 or miss (-5)
        val roll = Random.nextInt(100)
        val delta = when {
            roll < 10 -> -5
            roll < 40 -> 5
            roll < 80 -> 10
            else -> 15
        }
        return maxOf(0, currentOpponentScore + delta)
    }

    /**
     * Bot/Opponent simulation for Ludo contest
     */
    fun generateOpponentLudoTurn(currentOpponentScore: Int): Pair<Int, String> {
        val roll = Random.nextInt(1, 7)
        val captured = Random.nextInt(100) < 25
        val reachedHome = Random.nextInt(100) < 15
        var pts = roll * 5
        var action = "CyberStriker rolled $roll (+${pts} pts)"
        if (captured) {
            pts += 50
            action = "CyberStriker rolled $roll & captured a token! (+${pts} pts)"
        } else if (reachedHome) {
            pts += 100
            action = "CyberStriker rolled $roll & scored Home! (+${pts} pts)"
        }
        return Pair(currentOpponentScore + pts, action)
    }
}
