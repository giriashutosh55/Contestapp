package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contest
import com.example.model.ContestStatus
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

@Composable
fun HeaderPointsCard(
    points: Int,
    level: Int,
    xp: Int,
    onViewHistory: () -> Unit,
    onViewRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkSurfaceVariant.copy(alpha = 0.6f), DarkSurface)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AVAILABLE BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Points Trophy",
                            tint = AccentGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("%,d", points),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = " PTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                        )
                    }
                }

                // Level Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryPurple.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurpleGlow)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Level",
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LVL $level",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Action Buttons per PRD Section 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewHistory,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_history_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Match History",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onViewRewards,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_rewards_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Rewards Ledger",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ContestCard(
    contest: Contest,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clickable { onJoinClick() }
            .testTag("contest_card_${contest.id}"),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Tier & Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getTierColor(contest.tier).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, getTierColor(contest.tier))
                ) {
                    Text(
                        text = contest.tier.uppercase(),
                        color = getTierColor(contest.tier),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (contest.status == ContestStatus.OPEN) AccentGreen else AccentRed,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = contest.status.name,
                        color = if (contest.status == ContestStatus.OPEN) AccentGreen else AccentRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = contest.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = contest.gameName,
                fontSize = 13.sp,
                color = AccentCyan,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Metadata Row: Duration, Players, Reward
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contest.durationSeconds / 60}m ${contest.durationSeconds % 60}s",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contest.maxPlayers} Players",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${contest.rewardPoints} PTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onJoinClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("join_contest_${contest.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (contest.entryFeePoints > 0) "ENTER (${contest.entryFeePoints} PTS)" else "PLAY FREE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun getTierColor(tier: String): Color {
    return when (tier.lowercase()) {
        "beginner" -> AccentCyan
        "bronze" -> Color(0xFFCD7F32)
        "silver" -> Color(0xFFC0C0C0)
        "gold" -> AccentGold
        "platinum" -> Color(0xFFE5E4E2)
        else -> PrimaryPurpleGlow
    }
}
