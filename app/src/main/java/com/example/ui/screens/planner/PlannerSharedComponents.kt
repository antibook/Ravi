package com.example.ui.screens.planner

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.components.NavyPrimaryButton
import com.example.ui.components.WhiteOutlinedButton
import com.example.ui.theme.*

private val moduleStepLabels = listOf(
    "Basics", "Places", "Budget", "Hotels", "Transport", "Map", "Review", "Dashboard"
)

@Composable
fun SmartPlannerScaffold(
    step: Int,
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit,
    bottomBackLabel: String = "Back",
    bottomNextLabel: String = "Next →",
    showBottomBack: Boolean = true,
    onBottomBack: () -> Unit = onBack,
    onBottomNext: (() -> Unit)? = null,
    nextEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val progress by animateFloatAsState(targetValue = (step / 8f).coerceIn(0f, 1f), label = "step_progress")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding()
    ) {
        // Top Header with Progress Indicator
        Surface(
            color = SurfaceWhite,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("planner_top_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = CoralSoft,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Step $step of 8",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Coral,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "· Auto-saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = Green,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    TextButton(
                        onClick = onSaveAndExit,
                        modifier = Modifier.testTag("planner_save_exit")
                    ) {
                        Text("Save & Exit", color = Indigo, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = Coral,
                    trackColor = Lavender
                )
                Spacer(Modifier.height(8.dp))

                // Quick Step Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moduleStepLabels.forEachIndexed { idx, label ->
                        val sNum = idx + 1
                        val isCurrent = sNum == step
                        val isDone = sNum < step
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onStepJump(sNum) },
                            color = when {
                                isCurrent -> Navy
                                isDone -> TileGreenBg
                                else -> Lavender
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isCurrent -> Navy
                                    isDone -> TileGreenBorder
                                    else -> CardBorder
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDone) "✓ $sNum.$label" else "$sNum.$label",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCurrent) SurfaceWhite else if (isDone) Green else TextSecondary,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scrollable Body Area (responsive max width for tablets/desktop)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .align(Alignment.CenterHorizontally),
            content = content
        )

        // Sticky Bottom Navigation Bar
        if (onBottomNext != null || showBottomBack) {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, DividerLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp)
                        .align(Alignment.CenterHorizontally)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBottomBack) {
                        WhiteOutlinedButton(
                            text = bottomBackLabel,
                            onClick = onBottomBack,
                            modifier = Modifier.weight(1f),
                            testTag = "planner_bottom_back"
                        )
                    }
                    if (onBottomNext != null) {
                        Button(
                            onClick = onBottomNext,
                            enabled = nextEnabled,
                            modifier = Modifier
                                .weight(1.3f)
                                .heightIn(min = 48.dp)
                                .testTag("planner_bottom_next"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Coral,
                                contentColor = SurfaceWhite
                            )
                        ) {
                            Text(
                                text = bottomNextLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchableDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "searchable_dropdown"
) {
    var showSheet by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                query = ""
                showSheet = true
            }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.4.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = selectedValue.ifBlank { "Select $label" },
                    style = MaterialTheme.typography.titleSmall,
                    color = Navy,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.Search, contentDescription = "Search $label", tint = Indigo, modifier = Modifier.size(18.dp))
        }
    }

    if (showSheet) {
        val filtered = remember(options, query) {
            if (query.isBlank()) options
            else options.filter { it.contains(query.trim(), ignoreCase = true) }
        }
        AlertDialog(
            onDismissRequest = { showSheet = false },
            containerColor = SurfaceWhite,
            title = {
                Text("Select $label", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Type to search $label...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Indigo) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        if (filtered.isEmpty()) {
                            Text("No matching results", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.padding(12.dp))
                        } else {
                            filtered.take(12).forEach { opt ->
                                val selected = opt.equals(selectedValue, ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) CoralSoft else Color.Transparent)
                                        .clickable {
                                            onSelect(opt)
                                            showSheet = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = opt,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selected) Coral else Navy,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (selected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Coral, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSheet = false }) { Text("Close", color = Navy) }
            }
        )
    }
}

@Composable
fun PriorityStarsBadge(stars: Int, modifier: Modifier = Modifier) {
    val filled = "★".repeat(stars.coerceIn(1, 5))
    val empty = "☆".repeat((5 - stars).coerceIn(0, 5))
    val color = when {
        stars >= 5 -> Coral
        stars == 4 -> Indigo
        else -> TextSecondary
    }
    val bg = when {
        stars >= 5 -> CoralSoft
        stars == 4 -> IndigoSoft
        else -> Lavender
    }
    Surface(
        modifier = modifier,
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "$filled$empty",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
