package com.heimdall.aetherloop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = HeimdallGold,
    secondary = BifrostCyan,
    tertiary = ValkyrieViolet,
    background = CosmosDark,
    surface = CosmosSurface,
    onPrimary = CosmosDark,
    onSecondary = CosmosDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun HeimdallAetherLoopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
