package com.daynest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestLavenderDark
import com.daynest.ui.theme.NestLavenderLight
import com.daynest.ui.theme.NestOutline
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.ui.theme.NestOutlineVariant
import com.daynest.ui.theme.NestSurface
import com.daynest.ui.theme.NestSurfaceVariant

// Local gradient helpers
private fun nestGradientPrimary(dark: Boolean = false): Brush =
    Brush.linearGradient(
        colors = if (dark) listOf(NestLavenderDark, NestLavender) else listOf(NestLavender, NestLavenderLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )
private fun nestGradientLavender(): Brush =
    Brush.linearGradient(
        colors = listOf(NestLavender, NestLavenderLight),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

// ============================================================================
// PREMIUM CARD
// ============================================================================

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = 1.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp),
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    val cardModifier = modifier.fillMaxWidth().clipToBounds()

    if (onClick != null) {
        Card(
            modifier = cardModifier.clickable(onClick = onClick),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.TopStart,
                content = content
            )
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.TopStart,
                content = content
            )
        }
    }
}

// ============================================================================
// GRADIENT CARD
// ============================================================================

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gradient: Brush = nestGradientPrimary(),
    cornerRadius: Dp = 20.dp,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp),
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    val cardModifier = modifier.fillMaxWidth()

    if (onClick != null) {
        Surface(
            modifier = cardModifier.clickable(onClick = onClick).clipToBounds(),
            shape = RoundedCornerShape(cornerRadius),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(gradient).padding(contentPadding),
                contentAlignment = Alignment.TopStart,
                content = content
            )
        }
    } else {
        Surface(
            modifier = cardModifier.clipToBounds(),
            shape = RoundedCornerShape(cornerRadius),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(gradient).padding(contentPadding),
                contentAlignment = Alignment.TopStart,
                content = content
            )
        }
    }
}

// ============================================================================
// PREMIUM BUTTON
// ============================================================================

@Composable
fun PremiumButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    text: String,
    gradient: Brush = nestGradientPrimary()
) {
    val density = LocalDensity.current
    val heightDp = 52.dp

    Box(
        modifier = modifier.fillMaxWidth().height(heightDp).clipToBounds()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(gradient).clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.width(20.dp).height(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

// ============================================================================
// OUTLINE BUTTON
// ============================================================================

@Composable
fun PremiumOutlineButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.Transparent.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                )
            }
        }
    }
    Box(
        modifier = modifier.fillMaxWidth().height(52.dp).clickable(enabled = enabled, onClick = onClick)
            .border(1.dp, borderColor, MaterialTheme.shapes.large)
    )
}

// ============================================================================
// PREMIUM TEXT FIELD
// ============================================================================

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    maxLines: Int = 1,
    enabled: Boolean = true,
    error: Boolean = false,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = maxLines,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyMedium
    )

    if (error && errorText != null) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 8.dp)
        ) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}

// ============================================================================
// ICON BUTTON
// ============================================================================

@Composable
fun PremiumIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    tint: Color = Color.White,
    backgroundColor: Color? = null,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = backgroundColor ?: MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f),
            contentColor = tint ?: MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

// ============================================================================
// MOOD PICKER
// ============================================================================

@Composable
fun PremiumMoodPicker(
    selectedMood: String?,
    onMoodSelected: (String?) -> Unit,
    moods: List<MoodOption> = defaultMoods(),
    modifier: Modifier = Modifier
) {
    val spacing = LocalDaynestSpacing.current
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        moods.forEach { mood ->
            val isSelected = selectedMood == mood.id
            PremiumMoodOption(
                mood = mood,
                isSelected = isSelected,
                onClick = { onMoodSelected(mood.id) }
            )
        }
    }
}

@Composable
private fun PremiumMoodOption(
    mood: MoodOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(56.dp).clipToBounds(),
            shape = CircleShape,
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mood.emoji,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class MoodOption(
    val id: String,
    val emoji: String,
    val label: String
)

private fun defaultMoods(): List<MoodOption> = listOf(
    MoodOption("great", "\uD83D\uDE04", "Great"),
    MoodOption("good", "\uD83D\uDE42", "Good"),
    MoodOption("okay", "\uD83D\uDE10", "Okay"),
    MoodOption("low", "\uD83D\uDE15", "Low"),
    MoodOption("awful", "\uD83D\uDE1E", "Awful")
)

// ============================================================================
// PREMIUM EMPTY STATE
// ============================================================================

@Composable
fun PremiumEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Add,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ContainerWithGradient(
            modifier = Modifier.size(80.dp),
            gradient = nestGradientLavender(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(20.dp))
            PremiumButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ContainerWithGradient(
    modifier: Modifier = Modifier,
    gradient: Brush,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(gradient),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

// ============================================================================
// SECTION HEADER
// ============================================================================

@Composable
fun PremiumSectionHeader(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            trailing?.invoke()
        }
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ============================================================================
// QUICK ACTION ROW
// ============================================================================

@Composable
fun PremiumQuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color = NestLavender,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ContainerWithGradient(
            modifier = Modifier.size(56.dp),
            gradient = Brush.linearGradient(colors = listOf(color.copy(alpha = 0.15f))),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
    }
}

// ============================================================================
// SUMMARY CARD
// ============================================================================

@Composable
fun PremiumSummaryCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconColor: Color = NestLavender,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (icon != null) {
                ContainerWithGradient(
                    modifier = Modifier.size(40.dp),
                    gradient = Brush.linearGradient(colors = listOf(iconColor.copy(alpha = 0.15f))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// PROGRESS RING
// ============================================================================

@Composable
fun PremiumProgressRing(
    progress: Float,
    size: Dp = 80.dp,
    lineWidth: Dp = 6.dp,
    color: Color = NestLavender,
    backgroundColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
    centerContent: @Composable (() -> Unit)? = null
) {
    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }
    val lineWidthPx = with(density) { lineWidth.toPx() }

    Box(
        modifier = Modifier.width(size).height(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = sizePx / 2f
            val radius = center - lineWidthPx / 2f
            val startAngle = 270f
            val sweepAngle = progress * 360f

            // Background ring
            drawArc(
                color = backgroundColor,
                startAngle = startAngle,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center - radius, center - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )

            // Progress ring
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center - radius, center - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )
        }

        centerContent?.invoke()
    }
}

// ============================================================================
// CHIPS
// ============================================================================

@Composable
fun PremiumChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    color: Color = NestLavender,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier
    )
}
