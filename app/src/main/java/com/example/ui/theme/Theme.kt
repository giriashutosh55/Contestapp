package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = AccentCyan,
    secondary = AccentCyan,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Color.White,
    tertiary = AccentGold,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to gaming dark theme per PRD Section 49
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
