package com.vibecoding.calculator.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CatppuccinMochaColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = BgDark,
    primaryContainer = AccentBlue,
    onPrimaryContainer = BgDark,
    secondary = AccentMauve,
    onSecondary = BgDark,
    secondaryContainer = AccentMauve,
    onSecondaryContainer = BgDark,
    tertiary = AccentPeach,
    onTertiary = BgDark,
    tertiaryContainer = AccentPeach,
    onTertiaryContainer = BgDark,
    error = AccentRed,
    onError = BgDark,
    background = BgDark,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgOverlay,
    onSurfaceVariant = TextDim,
    outline = TextSubtle
)

@Composable
fun MultiCalculatorProTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = CatppuccinMochaColorScheme,
        typography = Typography,
        content = content
    )
}
