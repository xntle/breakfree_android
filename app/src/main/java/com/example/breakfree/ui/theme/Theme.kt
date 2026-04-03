package com.example.breakfree.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    primary = TextPrimary,
    onPrimary = AppBackground,
    secondary = TextSecondary,
    background = AppBackground,
    surface = AppSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun BreakfreeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
