package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Contest
import com.example.model.ContestStatus
import com.example.model.DisputeReason
import com.example.model.Match
import com.example.model.UserRole
import com.example.ui.components.ContestCard
import com.example.ui.components.HeaderPointsCard
import com.example.ui.components.getTierColor
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------------- 1. HOME SCREEN (PRD Section 6) ----------------

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val contests by viewModel.contests.collectAsStateWithLifecycle()
    val games by viewModel.games.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Ludo", "Reaction", "Quiz", "Carrom", "Puzzle", "Arcade", "Strategy")

    val filteredContests = if (selectedCategory == "All") {
        contests
    } else {
        contests.filter { it.gameName.contains(selectedCategory, ignoreCase = true) }
    }

    val featuredContest = contests.firstOrNull { it.tier == "Silver" } ?: contests.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(PrimaryPurple, AccentCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "App Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CONTEST ARENA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (user.role == UserRole.ADMIN) "Mode: Administrator" else "PvP Skill Arena",
                            fontSize = 11.sp,
                            color = if (user.role == UserRole.ADMIN) AccentGold else AccentCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Role Toggle Button for quick testing between Player and Admin view
                    IconButton(
                        onClick = { viewModel.toggleRole() },
                        modifier = Modifier.testTag("toggle_role_button")
                    ) {
                        Icon(
                            imageVector = if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = "Switch Role",
                            tint = if (user.role == UserRole.ADMIN) AccentGold else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Profile) },
                        modifier = Modifier.testTag("home_profile_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // Header Points Card per PRD Section 6 & 20
        item {
            HeaderPointsCard(
                points = user.points,
                level = user.level,
                xp = user.xp,
                onViewHistory = { viewModel.navigateTo(AppScreen.History) },
                onViewRewards = { viewModel.navigateTo(AppScreen.History) }
            )
        }

        // Featured Live Contest Banner (PRD Section 6)
        if (featuredContest != null) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, PrimaryPurpleGlow.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.navigateTo(AppScreen.ContestDetail(featuredContest)) }
                        .testTag("featured_contest_banner"),
                    color = DarkSurface
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        PrimaryPurple.copy(alpha = 0.25f),
                                        DarkSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(AccentGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "FEATURED LIVE CONTEST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AccentGreen,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = "${featuredContest.activeParticipants} Active Players",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = featuredContest.name,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )

                            Text(
                                text = "1-vs-1 • ${featuredContest.durationSeconds / 60} Min Duration • Win +${featuredContest.rewardPoints} Points",
                                fontSize = 13.sp,
                                color = AccentCyan,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.ContestDetail(featuredContest)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("featured_play_now_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentCyan,
                                    contentColor = DarkBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "PLAY NOW",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Game Categories Filter per PRD Section 6
        item {
            Column {
                Text(
                    text = "GAME CATEGORIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PrimaryPurple else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) PrimaryPurpleGlow else CardBorder
                            ),
                            modifier = Modifier.clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Contests List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE ARENAS (${filteredContests.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.Contests) }) {
                    Text("View All", color = AccentCyan, fontSize = 12.sp)
                }
            }
        }

        items(filteredContests) { contest ->
            ContestCard(
                contest = contest,
                onJoinClick = { viewModel.navigateTo(AppScreen.ContestDetail(contest)) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ---------------- 2. CONTEST DETAILS SCREEN (PRD Section 7, 95) ----------------

@Composable
fun ContestDetailScreen(
    contest: Contest,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    var agreedToRules by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                    Text("← Back to Arenas", color = AccentCyan, fontSize = 14.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getTierColor(contest.tier).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, getTierColor(contest.tier))
                ) {
                    Text(
                        text = "${contest.tier} Tier",
                        color = getTierColor(contest.tier),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = contest.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Game: ${contest.gameName}",
                fontSize = 15.sp,
                color = AccentCyan,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Contest Key Specs
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(label = "Match Duration", value = "${contest.durationSeconds / 60}m ${contest.durationSeconds % 60}s (Server authoritative)")
                    DetailRow(label = "Contest Type", value = "1 vs 1 Live Head-to-Head")
                    DetailRow(label = "Reward Winner", value = "+${contest.rewardPoints} Reward Points")
                    DetailRow(label = "Entry Fee", value = if (contest.entryFeePoints > 0) "${contest.entryFeePoints} Points" else "FREE ENTRY")
                    DetailRow(label = "Contest Mode", value = contest.contestMode.name)
                    DetailRow(label = "Anti-Cheat", value = "Server Event Frequency & Clock Verification")
                }
            }
        }

        // Official Contest Rules per PRD Section 95
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                color = DarkSurfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "OFFICIAL CONTEST RULES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (contest.gameId == "game_ludo") {
                            "1. Server Authority: Dice rolls and token moves are validated on the server with anti-tamper.\n" +
                            "2. Movement & Spawn: Roll a 6 to bring token out of base onto the track. Exact roll needed for Home.\n" +
                            "3. Scoring: Moving tokens gains +5 pts/step. Capturing opponent tokens awards +50 pts bonus! Reaching Home awards +100 pts.\n" +
                            "4. Bonus Turns: Rolling a 6 grants an immediate extra roll.\n" +
                            "5. Winner: Highest score when the server timer expires wins the contest reward!"
                        } else {
                            "1. Server Authority: Device clocks are not trusted; all timers and scores are verified on the server.\n" +
                            "2. Scoring: Rapid taps score up to +15 pts, standard taps +10 pts. Misses deduct -5 pts.\n" +
                            "3. Winner Determination: Player with highest server score wins configured reward points.\n" +
                            "4. Tie Outcome: Equal points result in a draw; rewards are split equally.\n" +
                            "5. Anti-Cheat: Automated input or manipulated timing triggers automatic penalty."
                        },
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreedToRules = !agreedToRules }
            ) {
                Switch(
                    checked = agreedToRules,
                    onCheckedChange = { agreedToRules = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I have read and acknowledge the contest rules and fair-play agreement.",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }
        }

        item {
            Button(
                onClick = { viewModel.startMatchmaking(contest) },
                enabled = agreedToRules,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_join_contest_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENTER WAITING ROOM",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

// ---------------- 3. MATCHMAKING & WAITING ROOM (PRD Section 10 & 11) ----------------

@Composable
fun MatchmakingScreen(
    contest: Contest,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val status by viewModel.matchmakingStatus.collectAsStateWithLifecycle()
    val countdown by viewModel.countdownValue.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = contest.name.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dual Player Cards (Match preview)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player 1 (You)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                            .border(2.dp, AccentCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.username.take(2).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = user.username, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Lvl ${user.level}", fontSize = 11.sp, color = AccentGold)
                }

                Text(
                    text = "VS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )

                // Player 2 (Opponent)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .border(2.dp, PrimaryPurpleGlow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (countdown != null) {
                            Text(
                                text = "CS",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = AccentCyan,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (countdown != null) "CyberStriker" else "Matching...",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (countdown != null) "Lvl 11" else "Searching...",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Countdown Display per PRD Section 10: "3, 2, 1, GO!"
            if (countdown != null) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryPurple.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, AccentGold),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (countdown == 0) "GO!" else countdown.toString(),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = if (countdown == 0) AccentGreen else AccentGold
                        )
                    }
                }
            } else {
                CircularProgressIndicator(
                    color = AccentCyan,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = status,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Text(
                    text = "Server authoritative timer initialized",
                    fontSize = 11.sp,
                    color = AccentGreen,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { viewModel.cancelMatchmaking() },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .testTag("cancel_matchmaking_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel Matchmaking", fontSize = 13.sp)
            }
        }
    }
}

// ---------------- 4. ACTIVE GAME ROOM (PRD Section 12, 13, 73) ----------------

@Composable
fun ActiveGameRoomScreen(
    match: Match,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeMatch by viewModel.activeMatch.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val targetActive by viewModel.reactionTargetActive.collectAsStateWithLifecycle()
    val posX by viewModel.targetPositionX.collectAsStateWithLifecycle()
    val posY by viewModel.targetPositionY.collectAsStateWithLifecycle()
    val feedback by viewModel.lastActionFeedback.collectAsStateWithLifecycle()

    val currentMatch = activeMatch ?: match
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Top Header: Player A Score vs Player B Score per PRD Section 12
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            color = DarkSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player A (You)
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${currentMatch.playerA.username} (You)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Text(
                        text = "${currentMatch.playerA.score}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                // Server Timer (PRD Section 12 & 13)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TIME REMAINING",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (remainingSeconds < 30) AccentRed.copy(alpha = 0.2f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (remainingSeconds < 30) AccentRed else CardBorder
                        )
                    ) {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (remainingSeconds < 30) AccentRed else AccentGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Player B (Opponent)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currentMatch.playerB.username,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "${currentMatch.playerB.score}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Action Feedback Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (feedback != null) {
                Text(
                    text = feedback!!,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (feedback!!.contains("+") || feedback!!.contains("PERFECT")) AccentGreen else AccentRed
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // GAME PLAY AREA (PRD Section 12 & 73)
        if (currentMatch.gameId == "game_ludo") {
            LudoGamePlayArea(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .testTag("reaction_game_area")
            ) {
                if (targetActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Position target dynamically using absolute constraints
                        Button(
                            onClick = { viewModel.onReactionTargetTapped() },
                            modifier = Modifier
                                .align(
                                    androidx.compose.ui.BiasAlignment(
                                        horizontalBias = (posX * 2f) - 1f,
                                        verticalBias = (posY * 2f) - 1f
                                    )
                                )
                                .size(76.dp)
                                .testTag("reaction_tap_target"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentGold,
                                contentColor = DarkBackground
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Tap Target",
                                tint = DarkBackground,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Get Ready! Tap neon targets instantly as they spawn...",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Server Status Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(AccentGreen, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Server Authoritative Sync: 60Hz", fontSize = 11.sp, color = TextMuted)
            }
            Text(
                text = "Anti-Cheat Active",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
        }
    }
}

// ---------------- 5. RESULT SCREEN (PRD Section 26) ----------------

@Composable
fun MatchResultScreen(
    match: Match,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val isUserWinner = match.winnerId == user.id
    val isDraw = match.isDraw

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MATCH COMPLETED",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Outcome Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = when {
                isUserWinner -> AccentGreen.copy(alpha = 0.2f)
                isDraw -> AccentGold.copy(alpha = 0.2f)
                else -> AccentRed.copy(alpha = 0.2f)
            },
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when {
                    isUserWinner -> AccentGreen
                    isDraw -> AccentGold
                    else -> AccentRed
                }
            )
        ) {
            Text(
                text = when {
                    isUserWinner -> "VICTORY!"
                    isDraw -> "MATCH DRAW"
                    else -> "DEFEAT"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = when {
                    isUserWinner -> AccentGreen
                    isDraw -> AccentGold
                    else -> AccentRed
                },
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dual Player Result Box (PRD Section 26)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
            color = DarkSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // You
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = user.username, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = "${match.playerA.score} PTS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isUserWinner) AccentGreen else TextSecondary
                    )
                }

                Text(text = "VS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMuted)

                // Opponent
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = match.playerB.username, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = "${match.playerB.score} PTS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (match.winnerId == match.playerB.userId) AccentGreen else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reward Box (PRD Section 15 & 26)
        if (isUserWinner) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = AccentGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+${match.rewardPoints} Reward Points Credited",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons per PRD Section 26
        Button(
            onClick = { viewModel.navigateTo(AppScreen.Home) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("result_play_again_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("PLAY AGAIN", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.History) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View History", color = TextPrimary)
            }

            Button(
                onClick = { viewModel.navigateTo(AppScreen.DisputeReport(match.id)) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Report Dispute", color = AccentRed)
            }
        }
    }
}

// ---------------- 6. MATCH HISTORY & REWARD LEDGER (PRD Section 20, 21, 25) ----------------

@Composable
fun MatchHistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matchHistory.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Matches, 1: Rewards Ledger

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "MATCH & REWARD LEDGER",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = "Server Authoritative Records & Transactions",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Dual Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 0) PrimaryPurple else Color.Transparent,
                    contentColor = if (selectedTab == 0) Color.White else TextSecondary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Matches (${matches.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 1) PrimaryPurple else Color.Transparent,
                    contentColor = if (selectedTab == 1) Color.White else TextSecondary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Reward Ledger", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(matches) { m ->
                    val isWin = m.winnerId == user.id
                    val isDraw = m.isDraw

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        color = DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = m.gameName, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    text = "vs ${m.playerB.username} • ${m.playerA.score} - ${m.playerB.score}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(m.serverStartTime)),
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    isWin -> AccentGreen.copy(alpha = 0.2f)
                                    isDraw -> AccentGold.copy(alpha = 0.2f)
                                    else -> AccentRed.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = when {
                                        isWin -> "+${m.rewardPoints} PTS"
                                        isDraw -> "+${m.rewardPoints / 2} PTS"
                                        else -> "0 PTS"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isWin -> AccentGreen
                                        isDraw -> AccentGold
                                        else -> AccentRed
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Reward Transaction Ledger Table per PRD Section 20 & 21
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(transactions) { tx ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        color = DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = tx.type.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text(text = "Ref: ${tx.referenceId}", fontSize = 11.sp, color = TextMuted)
                            }
                            Text(
                                text = "${if (tx.amount >= 0) "+" else ""}${tx.amount} PTS",
                                fontWeight = FontWeight.Black,
                                color = if (tx.amount >= 0) AccentGold else AccentRed,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- 7. LEADERBOARD (PRD Section 24) ----------------

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboard = remember { viewModel.repository.getLeaderboard() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "ARENA LEADERBOARD",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = "Calculated from Authoritative Server Records",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(leaderboard) { entry ->
                val isSelf = entry.username.contains("(You)")

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (isSelf) AccentGold else CardBorder,
                            RoundedCornerShape(14.dp)
                        ),
                    color = if (isSelf) DarkSurfaceVariant else DarkSurface
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${entry.rank}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = when (entry.rank) {
                                    1 -> AccentGold
                                    2 -> Color(0xFFC0C0C0)
                                    3 -> Color(0xFFCD7F32)
                                    else -> TextMuted
                                },
                                modifier = Modifier.width(36.dp)
                            )
                            Column {
                                Text(
                                    text = entry.username,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelf) AccentGold else TextPrimary
                                )
                                Text(
                                    text = "${entry.wins} Wins • ${String.format("%.1f", entry.winRate)}% Win Rate",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Text(
                            text = String.format("%,d PTS", entry.points),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = AccentCyan
                        )
                    }
                }
            }
        }
    }
}

// ---------------- 8. USER PROFILE (PRD Section 23) ----------------

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val settings by viewModel.platformSettings.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PrimaryPurple, AccentCyan))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.username.take(2).uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = "@${user.username} • Level ${user.level}", fontSize = 13.sp, color = AccentGold)
            }
        }

        // Stats Matrix (PRD Section 23)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                color = DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatBox(title = "Matches", value = user.matchesPlayed.toString())
                    StatBox(title = "Wins", value = user.wins.toString())
                    StatBox(title = "Losses", value = user.losses.toString())
                    StatBox(title = "Win Rate", value = "${String.format("%.1f", user.winRate)}%")
                }
            }
        }

        // Compliance & Legal Settings Card (PRD Section 3 & 83)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                color = DarkSurfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COMPLIANCE & LEGAL CONFIG",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Paid Contests Mode: ${if (settings.paidContestsEnabled) "ENABLED" else "DISABLED (Default Safe MVP)"}\n" +
                               "• Real-Money Wagering: Strictly Inactive\n" +
                               "• Minimum Age: ${settings.minimumAge}+ Years\n" +
                               "• Support: ${settings.supportEmail}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Quick Admin Switcher (requires 8-digit verification)
        item {
            Button(
                onClick = { viewModel.requestAdminAccess() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (user.role == UserRole.ADMIN) "Open Admin Dashboard" else "Admin Login (8-Digit Code Required)",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatBox(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(text = title, fontSize = 11.sp, color = TextSecondary)
    }
}

// ---------------- 9. DISPUTE REPORT SCREEN (PRD Section 28 & 40) ----------------

@Composable
fun DisputeReportScreen(
    matchId: String,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedReason by remember { mutableStateOf(DisputeReason.OPPONENT_CHEATING) }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = { viewModel.navigateTo(AppScreen.History) }) {
            Text("← Back to History", color = AccentCyan)
        }

        Text(
            text = "REPORT MATCH DISPUTE",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = "Match ID: $matchId • Audited by Admin",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Select Reason", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        DisputeReason.values().forEach { r ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedReason = r }
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.dp, AccentCyan, CircleShape)
                        .background(if (selectedReason == r) AccentCyan else Color.Transparent, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = r.title, fontSize = 13.sp, color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe the issue in detail") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.submitDispute(matchId, selectedReason, description) },
            enabled = description.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
        ) {
            Text("SUBMIT DISPUTE AUDIT", fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------- 10. ADMIN DASHBOARD (PRD Section 29, 30, 31) ----------------

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val contests by viewModel.contests.collectAsStateWithLifecycle()
    val adminMatches by viewModel.adminMatches.collectAsStateWithLifecycle()
    val disputes by viewModel.disputes.collectAsStateWithLifecycle()

    var newContestName by remember { mutableStateOf("") }
    var newContestTier by remember { mutableStateOf("Silver") }
    var newRewardPoints by remember { mutableStateOf("150") }
    var newDuration by remember { mutableStateOf("180") }
    var selectedGameId by remember { mutableStateOf("game_ludo") }
    var selectedGameName by remember { mutableStateOf("Ludo Master 1v1") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ADMIN CONSOLE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                    Text(text = "Match Oversight & Contest Creator", fontSize = 12.sp, color = TextSecondary)
                }
                TextButton(onClick = { viewModel.exitAdminMode() }) {
                    Text("← Exit Admin", color = AccentCyan)
                }
            }
        }

        // Admin KPI Cards (PRD Section 29)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(title = "Contests", value = contests.size.toString(), modifier = Modifier.weight(1f))
                AdminStatCard(title = "Matches", value = adminMatches.size.toString(), modifier = Modifier.weight(1f))
                AdminStatCard(title = "Disputes", value = disputes.size.toString(), modifier = Modifier.weight(1f))
            }
        }

        // CREATE NEW CONTEST FORM per PRD Section 9
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CREATE NEW CONTEST",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newContestName,
                        onValueChange = { newContestName = it },
                        label = { Text("Contest Name (e.g. 3-Minute Reflex Blitz)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newRewardPoints,
                            onValueChange = { newRewardPoints = it },
                            label = { Text("Reward PTS") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = newDuration,
                            onValueChange = { newDuration = it },
                            label = { Text("Duration (sec)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Game Selection:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedGameId == "game_ludo",
                            onClick = {
                                selectedGameId = "game_ludo"
                                selectedGameName = "Ludo Master 1v1"
                            },
                            label = { Text("Ludo Master 1v1") }
                        )
                        FilterChip(
                            selected = selectedGameId == "game_reaction",
                            onClick = {
                                selectedGameId = "game_reaction"
                                selectedGameName = "Reaction Master"
                            },
                            label = { Text("Reaction Master") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (newContestName.isNotBlank()) {
                                viewModel.createContestAsAdmin(
                                    name = newContestName,
                                    gameId = selectedGameId,
                                    gameName = selectedGameName,
                                    durationSeconds = newDuration.toIntOrNull() ?: 180,
                                    rewardPoints = newRewardPoints.toIntOrNull() ?: 150,
                                    entryFeePoints = 0,
                                    tier = newContestTier
                                )
                                newContestName = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text("PUBLISH CONTEST", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Contest Management List
        item {
            Text(
                text = "MANAGE ACTIVE CONTESTS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        items(contests) { c ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                color = DarkSurface
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = c.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "${c.tier} Tier • Reward: +${c.rewardPoints} PTS", fontSize = 12.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = { viewModel.toggleContestStatus(c.id, c.status) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (c.status == ContestStatus.OPEN) AccentRed else AccentGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (c.status == ContestStatus.OPEN) "Suspend" else "Activate", fontSize = 11.sp)
                    }
                }
            }
        }

        // Pending Disputes Section per PRD Section 28
        if (disputes.isNotEmpty()) {
            item {
                Text(
                    text = "AUDIT DISPUTES (${disputes.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentRed
                )
            }

            items(disputes) { d ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, AccentRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    color = DarkSurface
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Match: ${d.matchId} • Reported by ${d.reporterName}", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Reason: ${d.reason.title}", color = AccentGold, fontSize = 12.sp)
                        Text(text = d.description, color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.resolveDispute(d.id, "RESOLVED_APPROVE", "Match confirmed valid.") },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve Match", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.resolveDispute(d.id, "RESOLVED_CANCEL", "Cancelled and points refunded.") },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel Match", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = AccentCyan)
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
