package com.daynest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = NestLavenderDark,
    onPrimary = Color.White,
    primaryContainer = NestLavenderLight,
    onPrimaryContainer = NestLavenderDark,
    secondary = NestPowderBlueDark,
    onSecondary = Color.White,
    secondaryContainer = NestPowderBlueLight,
    onSecondaryContainer = NestPowderBlueDark,
    tertiary = NestBlushPinkDark,
    onTertiary = Color.White,
    tertiaryContainer = NestBlushPinkLight,
    onTertiaryContainer = NestBlushPinkDark,
    error = NestError,
    onError = Color.White,
    background = NestBackground,
    onBackground = NestOnSurface,
    surface = Color.White,
    onSurface = NestOnSurface,
    surfaceVariant = NestSurfaceVariant,
    onSurfaceVariant = NestOnSurfaceVariant,
    outline = NestOutline,
    outlineVariant = NestOutlineVariant,
    scrim = Color(0xFF000000),
    inverseSurface = NestDarkSurface,
    inverseOnSurface = NestDarkOnSurface,
    inversePrimary = NestLavenderLight,
    surfaceDim = NestSurfaceVariant,
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = NestSurfaceVariant,
    surfaceContainer = NestSurfaceElevated,
    surfaceContainerHigh = Color(0xFFE6DEFB),
    surfaceContainerHighest = Color(0xFFD8CFF4)
)

private val DarkColors = darkColorScheme(
    primary = NestLavenderLight,
    onPrimary = NestDarkBackground,
    primaryContainer = NestLavenderDark,
    onPrimaryContainer = NestLavenderLight,
    secondary = NestPowderBlueLight,
    onSecondary = NestDarkBackground,
    secondaryContainer = NestPowderBlueDark,
    onSecondaryContainer = NestPowderBlueLight,
    tertiary = NestBlushPinkLight,
    onTertiary = NestDarkBackground,
    tertiaryContainer = NestBlushPinkDark,
    onTertiaryContainer = NestBlushPinkLight,
    error = NestError,
    onError = Color.White,
    background = NestDarkBackground,
    onBackground = NestDarkOnSurface,
    surface = NestDarkSurface,
    onSurface = NestDarkOnSurface,
    surfaceVariant = NestDarkSurfaceVariant,
    onSurfaceVariant = NestDarkOnSurfaceVariant,
    outline = NestDarkOutline,
    outlineVariant = Color(0xFF3A344A),
    scrim = Color(0xFF000000),
    inverseSurface = Color.White,
    inverseOnSurface = NestDarkBackground,
    inversePrimary = NestLavenderDark,
    surfaceDim = NestDarkSurfaceVariant,
    surfaceBright = NestDarkSurface,
    surfaceContainerLowest = NestDarkSurface,
    surfaceContainerLow = NestDarkSurfaceVariant,
    surfaceContainer = Color(0xFF332E42),
    surfaceContainerHigh = Color(0xFF3B3650),
    surfaceContainerHighest = Color(0xFF443E5A)
)

@Composable
fun DaynestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = DaynestTypography,
        shapes = DaynestShapes,
        content = {
            DaynestSpacingProvider(content = content)
        }
    )
}

// ============================================================================
// SPACING COMPOSITION LOCAL — consistent spacing throughout the app
// ============================================================================

val LocalDaynestSpacing = staticCompositionLocalOf { DaynestSpacing }

@Composable
fun DaynestSpacingProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDaynestSpacing provides DaynestSpacing) {
        content()
    }
}
