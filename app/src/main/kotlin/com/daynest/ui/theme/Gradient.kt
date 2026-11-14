package com.daynest.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ============================================================================
// PREMIUM GRADIENTS
// ============================================================================

fun NestGradientBackground(dark: Boolean = false): Brush =
    Brush.verticalGradient(
        if (dark) listOf(NestDarkBackground, NestDarkSurface) else listOf(NestBackground, NestBackgroundVariant)
    )

fun NestGradientCard(dark: Boolean = false): Brush =
    Brush.verticalGradient(
        if (dark)
            listOf(NestDarkSurface, NestDarkSurfaceVariant)
        else
            listOf(Color.White, NestSurfaceVariant.copy(alpha = 0.5f))
    )

fun NestGradientLavender(): Brush =
    Brush.linearGradient(
        colors = listOf(NestLavender, NestLavenderLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

fun NestGradientPowderBlue(): Brush =
    Brush.linearGradient(
        colors = listOf(NestPowderBlue, NestPowderBlueLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

fun NestGradientBlushPink(): Brush =
    Brush.linearGradient(
        colors = listOf(NestBlushPink, NestBlushPinkLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

fun NestGradientSageGreen(): Brush =
    Brush.linearGradient(
        colors = listOf(NestSageGreen, NestSageGreenLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

fun NestGradientPrimary(dark: Boolean = false): Brush =
    Brush.linearGradient(
        colors = if (dark) listOf(NestLavenderDark, NestLavender) else listOf(NestLavender, NestLavenderLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

// Subtle glow effect for premium feel
fun NestGlow(color: Color, radius: Float = 24f): Brush =
    Brush.radialGradient(
        colors = listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0f)),
        center = Offset(0.5f, 0.5f),
        radius = radius
    )
