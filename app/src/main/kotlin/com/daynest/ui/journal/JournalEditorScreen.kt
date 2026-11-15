package com.daynest.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.detectDragGestures
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.journal.JournalEntryEntity
import com.daynest.data.local.journal.Mood
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumMoodPicker
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditorScreen(entryId: Long? = null, onBack: () -> Unit = {}) {
    val viewModel: JournalViewModel = viewModel(factory = LocalViewModelFactory.current)
    val spacing = LocalDaynestSpacing.current

    var existingEntry by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var mood by remember { mutableStateOf(Mood.OKAY) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showStickers by remember { mutableStateOf(false) }
    var showDrawing by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    // Load existing entry if editing
    androidx.compose.runtime.LaunchedEffect(entryId) {
        if (entryId != null && entryId >= 0) {
            val loaded = viewModel.getById(entryId)
            existingEntry = loaded
            if (loaded != null) {
                mood = loaded.mood
                content = loaded.text
                title = loaded.title
                selectedDate = java.time.LocalDate.ofEpochDay(loaded.entryDateEpochDay)
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(saved) {
        if (saved) {
            kotlinx.coroutines.delay(600)
            onBack()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { /* stay open */ },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingEntry == null) "New Memory" else "Edit Memory",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Close")
                }
            }
            Spacer(modifier = Modifier.height(spacing.md))

            // Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Date")
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(
                    text = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(spacing.md))

            // Title
            PremiumTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("Give your memory a title...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Mood picker
            Text("Mood", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            PremiumMoodPicker(
                selectedMood = mood.name,
                onMoodSelected = { mood = Mood.valueOf(it ?: Mood.OKAY.name) }
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Content
            Text("What's on your mind?", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            PremiumTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write your entry...") },
                maxLines = 8,
                singleLine = false,
                error = content.isBlank()
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Media options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                PremiumMediaOption(
                    icon = Icons.Filled.InsertPhoto,
                    label = "Photo",
                    onClick = { /* Open gallery */ }
                )
                PremiumMediaOption(
                    icon = Icons.Filled.Mic,
                    label = "Voice",
                    onClick = { /* Record voice */ }
                )
                PremiumMediaOption(
                    icon = Icons.Filled.InsertEmoticon,
                    label = "Stickers",
                    onClick = { showStickers = true }
                )
                PremiumMediaOption(
                    icon = Icons.Filled.Edit,
                    label = "Draw",
                    onClick = { showDrawing = true }
                )
            }

            if (showStickers) {
                StickerPickerSheet(
                    onStickerSelected = { /* Add sticker to entry */ },
                    onDismiss = { showStickers = false }
                )
            }

            if (showDrawing) {
                DrawingCanvasSheet(
                    onDrawingSaved = { /* Add drawing to entry */ },
                    onDismiss = { showDrawing = false }
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Save button
            androidx.compose.material3.Button(
                onClick = {
                    if (content.isNotBlank()) {
                        viewModel.saveEntry(
                            existingEntry,
                            mood,
                            content,
                            selectedDate,
                            title
                        )
                        saved = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (saved) {
                        Icon(Icons.Default.Check, contentDescription = "Saved")
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (saved) "Saved!" else "Save Memory",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun PremiumMediaOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(48.dp),
            shape = com.daynest.ui.theme.full,
            background = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// STICKER PICKER SHEET
// ============================================================================

@Composable
private fun StickerPickerSheet(
    onStickerSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val stickers = listOf(
        "\uD83C\uDF38", "\uD83C\uDF39", "\uD83C\uDF3A", "\uD83C\uDF3B", "\uD83C\uDF3C",
        "\uD83D\uDC00", "\uD83D\uDC01", "\uD83D\uDC02", "\uD83D\uDC03", "\uD83D\uDC04",
        "\uD83E\uDD8E", "\uD83E\uDD8F", "\uD83E\uDD90", "\uD83E\uDD91", "\uD83E\uDD92",
        "\u2764", "\uD83D\uDC95", "\uD83D\uDC96", "\uD83D\uDC97", "\uD83D\uDC98",
        "\uD83C\uDF34", "\uD83C\uDF35", "\uD83C\uDF36", "\uD83C\uDF37", "\uD83C\uDF39"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.md)
    ) {
        Text(
            text = "Stickers",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            items(stickers, key = { it }) { sticker ->
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onStickerSelected(sticker); onDismiss() }),
                    shape = com.daynest.ui.theme.DaynestShapes.medium,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm, horizontal = spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sticker,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(
                            text = "Add to entry",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// DRAWING CANVAS SHEET
// ============================================================================

@Composable
private fun DrawingCanvasSheet(
    onDrawingSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    var brushColor by remember { mutableStateOf(com.daynest.ui.theme.NestLavenderDark) }
    var brushSize by remember { mutableStateOf(4f) }
    var paths by remember { mutableStateOf<List<DrawingPath>>(emptyList()) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.md)
    ) {
        Text(
            text = "Drawing Canvas",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        // Drawing canvas
        androidx.compose.material3.Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = com.daynest.ui.theme.DaynestShapes.medium,
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            DrawingCanvas(
                paths = paths,
                onPathAdded = { paths = paths + it },
                brushColor = brushColor,
                brushSize = brushSize
            )
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Color:", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(4.dp))
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(24.dp).clickable(onClick = { showColorPicker = true }).background(brushColor, shape = com.daynest.ui.theme.full)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Size:", style = MaterialTheme.typography.labelSmall)
            androidx.compose.material3.Slider(
                value = brushSize,
                onValueChange = { brushSize = it },
                valueRange = 2f..12f,
                steps = 5
            )
            Spacer(modifier = Modifier.width(1.dp))

            androidx.compose.material3.TextButton(
                onClick = { paths = emptyList() }
            ) {
                Text(text = "Clear")
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            androidx.compose.material3.TextButton(
                onClick = { onDismiss() }
            ) {
                Text(text = "Cancel")
            }
            androidx.compose.material3.Button(
                onClick = {
                    // Save drawing as image path (simplified - in real app would save to file)
                    val drawingId = java.util.UUID.randomUUID().toString()
                    onDrawingSaved(drawingId)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Save Drawing")
            }
        }
    }
}

// ============================================================================
// DRAWING CANVAS COMPONENT
// ============================================================================

private data class DrawingPoint(
    val x: Float,
    val y: Float,
    val color: androidx.compose.ui.graphics.Color,
    val size: Float
)

private data class DrawingPath(
    val points: List<DrawingPoint>
)

@Composable
private fun DrawingCanvas(
    paths: List<DrawingPath>,
    onPathAdded: (DrawingPath) -> Unit,
    brushColor: androidx.compose.ui.graphics.Color,
    brushSize: Float
) {
    var currentPath by remember { mutableStateOf(mutableListOf<DrawingPoint>()) }
    val density = androidx.compose.ui.platform.LocalDensity.current

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = mutableListOf(
                            DrawingPoint(
                                x = offset.x,
                                y = offset.y,
                                color = brushColor,
                                size = brushSize
                            )
                        )
                    },
                    onDragEnd = {
                        if (currentPath.isNotEmpty()) {
                            onPathAdded(DrawingPath(currentPath.toList()))
                        }
                        currentPath = mutableListOf()
                    },
                    onDragCancel = {
                        currentPath = mutableListOf()
                    },
                    onDrag = { change, dragAmount ->
                        change.consumePositionChange()
                        val newPoint = DrawingPoint(
                            x = change.position.x,
                            y = change.position.y,
                            color = brushColor,
                            size = brushSize
                        )
                        currentPath.add(newPoint)
                    }
                )
            }
    ) {
        // Draw existing paths
        paths.forEach { path ->
            for (i in path.points.indices.drop(1)) {
                val p1 = path.points[i - 1]
                val p2 = path.points[i]
                drawLine(
                    color = p1.color,
                    start = androidx.compose.ui.geometry.Offset(p1.x, p1.y),
                    end = androidx.compose.ui.geometry.Offset(p2.x, p2.y),
                    strokeWidth = p1.size
                )
            }
        }

        // Draw current path being drawn
        for (i in currentPath.indices.drop(1)) {
            val p1 = currentPath[i - 1]
            val p2 = currentPath[i]
            drawLine(
                color = p1.color,
                start = androidx.compose.ui.geometry.Offset(p1.x, p1.y),
                end = androidx.compose.ui.geometry.Offset(p2.x, p2.y),
                strokeWidth = p1.size
            )
        }
    }
}
