package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
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

@Composable
fun AdminLoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var codeInput by remember { mutableStateOf("") }
    var isCodeMasked by remember { mutableStateOf(false) }
    val authError by viewModel.adminAuthError.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = { viewModel.navigateTo(AppScreen.Home) },
                modifier = Modifier.testTag("admin_login_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Return to Arenas", color = AccentCyan, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shield / Security Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(PrimaryPurple.copy(alpha = 0.5f), DarkSurfaceVariant)
                    )
                )
                .border(2.dp, AccentGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin Security Shield",
                tint = AccentGold,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "ADMIN AUTHENTICATION",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "An 8-digit authorization passcode is strictly required to unlock the Admin Dashboard and manage contests.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 8-Digit Display Boxes
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ENTER 8-DIGIT SECURITY CODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // PIN / Code digit visualization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0 until 8) {
                        val digit = codeInput.getOrNull(i)?.toString() ?: ""
                        val isFilled = digit.isNotEmpty()

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFilled) PrimaryPurple.copy(alpha = 0.25f) else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isFilled) AccentGold else CardBorder,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isCodeMasked && isFilled) "•" else digit,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isFilled) AccentGold else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Input Field
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        if (filtered.length <= 8) {
                            codeInput = filtered
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_code_input"),
                    label = { Text("8-Digit Passcode") },
                    placeholder = { Text("e.g. 84920153") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = if (isCodeMasked) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        IconButton(onClick = { isCodeMasked = !isCodeMasked }) {
                            Icon(
                                imageVector = if (isCodeMasked) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = TextSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Error Message if invalid
                if (authError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = authError!!,
                        color = AccentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        viewModel.verifyAdminCode(codeInput)
                    },
                    enabled = codeInput.length == 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VERIFY & LOGIN AS ADMIN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Security Key Hint Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
            color = DarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Key",
                    tint = AccentGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ADMINISTRATOR 8-DIGIT ACCESS CODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Authorized Admin Key: ${viewModel.adminSecurityCode}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tap code below to autofill during developer review:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { codeInput = viewModel.adminSecurityCode },
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Fill ${viewModel.adminSecurityCode}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentCyan
                        )
                    }
                }
            }
        }
    }
}
