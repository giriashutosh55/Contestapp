package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

/**
 * Interactive 1v1 Ludo Game Arena
 * Features:
 * - 4 interactive Player Tokens (Green/Cyan team)
 * - 4 Opponent Tokens (Red/Purple team)
 * - Animated 3D-styled Cyber Dice Roller with authentic rules (roll a 6 to spawn, exact roll for home)
 * - Track progression indicators & Home Stretch
 * - Real-time Server Validation Integration
 */
@Composable
fun LudoGamePlayArea(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val diceValue by viewModel.diceValue.collectAsStateWithLifecycle()
    val isRolling by viewModel.isRollingDice.collectAsStateWithLifecycle()
    val playerTokens by viewModel.playerTokens.collectAsStateWithLifecycle()
    val opponentTokens by viewModel.opponentTokens.collectAsStateWithLifecycle()
    val isPlayerTurn by viewModel.isPlayerTurn.collectAsStateWithLifecycle()

    val diceRotation by animateFloatAsState(
        targetValue = if (isRolling) 360f else 0f,
        label = "dice_roll_anim"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .testTag("ludo_game_area"),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Opponent Base / Token Strip (Red / Purple)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AccentRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Opponent (Red Team)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRed
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    opponentTokens.forEachIndexed { idx, pos ->
                        OpponentTokenBadge(tokenIndex = idx, position = pos)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center Board Grid & Progress Visualizer
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkBackground.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Board Top: Turn Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val turnText = if (isPlayerTurn) "YOUR TURN TO ROLL" else "OPPONENT'S TURN..."
                    val turnColor = if (isPlayerTurn) AccentGreen else AccentGold
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = turnColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, turnColor)
                    ) {
                        Text(
                            text = turnText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = turnColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Tokens Track Progress Matrix
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "YOUR TOKENS (TAP TO MOVE AFTER ROLL):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.sp
                    )

                    playerTokens.forEachIndexed { index, position ->
                        val canMove = isPlayerTurn && !isRolling &&
                            ((position == 0 && diceValue == 6) || (position > 0 && position + diceValue <= 57))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (canMove) AccentCyan.copy(alpha = 0.15f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (canMove) AccentCyan else CardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = canMove) {
                                    viewModel.moveLudoToken(index)
                                }
                                .testTag("ludo_token_$index")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                if (position == 57) AccentGold else AccentGreen,
                                                CircleShape
                                            )
                                            .border(1.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "T${index + 1}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DarkBackground
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = when {
                                                position == 0 -> "At Base Yard (Need 6)"
                                                position == 57 -> "HOME (Finished! +100)"
                                                position > 50 -> "Home Stretch (Step $position/57)"
                                                else -> "Track Step $position/57"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (position == 57) AccentGold else TextPrimary
                                        )
                                        Text(
                                            text = if (canMove) "Tap to move +$diceValue steps" else "Inactive",
                                            fontSize = 9.sp,
                                            color = if (canMove) AccentGreen else TextMuted
                                        )
                                    }
                                }

                                if (position == 57) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Home",
                                        tint = AccentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (canMove) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AccentCyan
                                    ) {
                                        Text(
                                            text = "MOVE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DarkBackground,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cyber Ludo Dice Roller Area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryPurple)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive 3D styled Dice Block
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AccentGold,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                    modifier = Modifier
                        .size(54.dp)
                        .rotate(diceRotation)
                        .clickable(enabled = isPlayerTurn && !isRolling) {
                            viewModel.rollLudoDice()
                        }
                        .testTag("ludo_dice_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = diceValue.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRolling) "Rolling..." else "Dice Roll: $diceValue",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                    Text(
                        text = if (isPlayerTurn) "Tap dice to roll (6 gets bonus turn!)" else "Opponent thinking...",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { viewModel.rollLudoDice() },
                    enabled = isPlayerTurn && !isRolling,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        disabledContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("roll_dice_action")
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Roll",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ROLL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OpponentTokenBadge(tokenIndex: Int, position: Int) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (position == 57) AccentGold.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (position == 57) AccentGold else AccentRed.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "T${tokenIndex + 1}: ",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AccentRed
            )
            Text(
                text = when (position) {
                    0 -> "Base"
                    57 -> "★ Home"
                    else -> "$position"
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = if (position == 57) AccentGold else TextPrimary
            )
        }
    }
}
