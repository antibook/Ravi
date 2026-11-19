package com.example.ui.screens.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SmartAttraction
import com.example.data.SmartCityPlan
import com.example.data.SmartTripCatalog
import com.example.data.SmartTripPlan
import com.example.ui.components.DesignCard
import com.example.ui.components.FilterPillChip
import com.example.ui.theme.*
import com.example.viewmodel.SmartTripController

@Composable
fun Module2DestinationScreen(
    plan: SmartTripPlan,
    controller: SmartTripController,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit
) {
    var searchPlaceDialogCity by remember { mutableStateOf<SmartCityPlan?>(null) }
    var rearrangeDialogCity by remember { mutableStateOf<SmartCityPlan?>(null) }

    val totalCities = plan.cityPlans.size
    val totalAttractions = plan.cityPlans.sumOf { it.attractions.size }
    val allStates = remember { SmartTripCatalog.allStatesWithCities.keys.toList() }

    SmartPlannerScaffold(
        step = 2,
        title = "Smarter Destination Selection",
        subtitle = "$totalCities cities · $totalAttractions attractions organized",
        onBack = onBack,
        onStepJump = onStepJump,
        onSaveAndExit = onSaveAndExit,
        onBottomBack = { onStepJump(1) },
        bottomNextLabel = "Next: Budget ($totalCities Cities · $totalAttractions Spots) →",
        onBottomNext = { onStepJump(3) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Two Entry Cards: Option A vs Option B
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isKnown = plan.selectionMode == "Known"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { controller.setSelectionMode("Known") }
                            .testTag("m2_mode_known"),
                        color = if (isKnown) IndigoSoft else SurfaceWhite,
                        border = BorderStroke(1.5.dp, if (isKnown) Indigo else CardBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Default.Explore, contentDescription = null, tint = Indigo)
                            Spacer(Modifier.height(4.dp))
                            Text("I Know My Destination", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text("Pick states, cities & spots directly", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

                    val isSuggest = plan.selectionMode == "Suggest"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { controller.setSelectionMode("Suggest") }
                            .testTag("m2_mode_suggest"),
                        color = if (isSuggest) CoralSoft else SurfaceWhite,
                        border = BorderStroke(1.5.dp, if (isSuggest) Coral else CardBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Coral)
                            Spacer(Modifier.height(4.dp))
                            Text("Suggest Me a Destination", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text("Curated multi-city circuits", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
            }

            // Smart Suggestions Panel when Option B active
            if (plan.selectionMode == "Suggest") {
                item {
                    DesignCard(containerColor = TileAmberBg, borderColor = TileAmberBorder, contentPadding = PaddingValues(14.dp)) {
                        Text("Recommended Indian Circuits", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        val suggestions = listOf(
                            Triple("Royal Rajasthan Forts & Lakes", "Rajasthan", listOf("Jaipur", "Jodhpur", "Udaipur")),
                            Triple("Golden Thar & Sacred Pushkar", "Rajasthan", listOf("Jaipur", "Pushkar", "Jaisalmer")),
                            Triple("Himalayan Snow & Pines", "Himachal Pradesh", listOf("Shimla", "Manali", "Dharamshala")),
                            Triple("Kerala Tea & Backwaters", "Kerala", listOf("Kochi", "Munnar", "Alleppey")),
                            Triple("Spiritual Ganga & Taj Trail", "Uttar Pradesh", listOf("Agra", "Ayodhya", "Varanasi"))
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            suggestions.forEach { (title, st, cities) ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { controller.applySuggestedDestinationPack(st, cities) },
                                    color = SurfaceWhite,
                                    border = BorderStroke(1.dp, CardBorder),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                            Text("$st · ${cities.joinToString(" → ")}", style = MaterialTheme.typography.labelSmall, color = Indigo)
                                        }
                                        Text("Apply", style = MaterialTheme.typography.labelMedium, color = Coral, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Level 1: Multi-State Selection + Level 2: Multi-City Selection
            item {
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Text("LEVEL 1 · SELECT STATES", style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allStates.forEach { st ->
                            FilterPillChip(
                                label = st,
                                selected = plan.selectedStates.contains(st),
                                onClick = { controller.toggleStateSelection(st) }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text("LEVEL 2 · SELECT CITIES (TAP TO ADD / REMOVE)", style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    plan.selectedStates.forEach { st ->
                        val cities = SmartTripCatalog.allStatesWithCities[st].orEmpty()
                        Text(st, style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            cities.forEach { city ->
                                val isAdded = plan.cityPlans.any { it.cityName == city }
                                FilterPillChip(
                                    label = if (isAdded) "✓ $city" else "+ $city",
                                    selected = isAdded,
                                    onClick = { controller.toggleCitySelection(st, city) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Priority Star Legend
            item {
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DragIndicator, contentDescription = null, tint = Indigo, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Sightseeing Priority Legend · Drag or use ↑↓ to reorder cities & places",
                                style = MaterialTheme.typography.labelMedium,
                                color = Navy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("★★★★★ Must Visit", style = MaterialTheme.typography.labelSmall, color = Coral, fontWeight = FontWeight.Bold)
                            Text("★★★★ Highly Recommended", style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.SemiBold)
                            Text("★★★ Optional Visit", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
            }

            // Level 3: Expandable City Cards with Independent Drag-and-Drop Reordering (Level 1: Cities, Level 2: Attractions)
            itemsIndexed(plan.cityPlans, key = { _, cp -> cp.cityName }) { cityIdx, cityPlan ->
                val availableAttractions = remember(cityPlan.cityName, cityPlan.stateName) {
                    SmartTripCatalog.attractionsForCity(cityPlan.cityName, cityPlan.stateName)
                }

                DesignCard(
                    borderColor = Indigo.copy(alpha = 0.3f),
                    contentPadding = PaddingValues(14.dp),
                    modifier = Modifier.pointerInput(cityIdx, plan.cityPlans.size) {
                        var accumulatedY = 0f
                        detectDragGesturesAfterLongPress(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                accumulatedY += dragAmount.y
                                if (accumulatedY > 90f && cityIdx < plan.cityPlans.lastIndex) {
                                    controller.moveCityOrder(cityIdx, cityIdx + 1)
                                    accumulatedY = 0f
                                } else if (accumulatedY < -90f && cityIdx > 0) {
                                    controller.moveCityOrder(cityIdx, cityIdx - 1)
                                    accumulatedY = 0f
                                }
                            },
                            onDragEnd = { accumulatedY = 0f }
                        )
                    }
                ) {
                    // City Header with Reorder Up/Down & Expand Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { controller.toggleCityExpanded(cityPlan.cityName) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DragIndicator, contentDescription = "Long press to drag city", tint = TextMuted)
                        Spacer(Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${cityIdx + 1}. ${cityPlan.cityName.uppercase()}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Navy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cityPlan.stateName} · Selected Places: ${cityPlan.attractions.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Coral,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(
                            onClick = { controller.moveCityOrder(cityIdx, cityIdx - 1) },
                            enabled = cityIdx > 0,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move city up", tint = if (cityIdx > 0) Indigo else TextMuted)
                        }
                        IconButton(
                            onClick = { controller.moveCityOrder(cityIdx, cityIdx + 1) },
                            enabled = cityIdx < plan.cityPlans.lastIndex,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move city down", tint = if (cityIdx < plan.cityPlans.lastIndex) Indigo else TextMuted)
                        }
                        if (plan.cityPlans.size > 1) {
                            IconButton(
                                onClick = { controller.toggleCitySelection(cityPlan.stateName, cityPlan.cityName) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove city", tint = DangerRed, modifier = Modifier.size(18.dp))
                            }
                        }
                        Icon(
                            imageVector = if (cityPlan.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = Navy
                        )
                    }

                    AnimatedVisibility(visible = cityPlan.isExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Selected ordered attractions with drag / ↑↓ reorder
                            if (cityPlan.attractions.isEmpty()) {
                                Text("No attractions selected yet. Add from below or search.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            } else {
                                cityPlan.attractions.forEachIndexed { attrIdx, attr ->
                                    Surface(
                                        color = Lavender,
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, CardBorder),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .pointerInput(cityPlan.cityName, attrIdx, cityPlan.attractions.size) {
                                                var dragDelta = 0f
                                                detectDragGesturesAfterLongPress(
                                                    onDrag = { change, amount ->
                                                        change.consume()
                                                        dragDelta += amount.y
                                                        if (dragDelta > 60f && attrIdx < cityPlan.attractions.lastIndex) {
                                                            controller.moveAttractionInCity(cityPlan.cityName, attrIdx, attrIdx + 1)
                                                            dragDelta = 0f
                                                        } else if (dragDelta < -60f && attrIdx > 0) {
                                                            controller.moveAttractionInCity(cityPlan.cityName, attrIdx, attrIdx - 1)
                                                            dragDelta = 0f
                                                        }
                                                    },
                                                    onDragEnd = { dragDelta = 0f }
                                                )
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.DragHandle, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${attrIdx + 1}. ${attr.name}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = Navy,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    PriorityStarsBadge(attr.priorityStars)
                                                    Text("${attr.category} · ${attr.duration}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                                }
                                            }
                                            IconButton(
                                                onClick = { controller.moveAttractionInCity(cityPlan.cityName, attrIdx, attrIdx - 1) },
                                                enabled = attrIdx > 0,
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = if (attrIdx > 0) Navy else TextMuted, modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(
                                                onClick = { controller.moveAttractionInCity(cityPlan.cityName, attrIdx, attrIdx + 1) },
                                                enabled = attrIdx < cityPlan.attractions.lastIndex,
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = if (attrIdx < cityPlan.attractions.lastIndex) Navy else TextMuted, modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(
                                                onClick = { controller.toggleAttractionInCity(cityPlan.cityName, attr) },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Remove place", tint = DangerRed, modifier = Modifier.size(17.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            // Suggested Quick Picks not yet selected
                            val unselected = availableAttractions.filter { sug ->
                                cityPlan.attractions.none { it.name.equals(sug.name, ignoreCase = true) }
                            }
                            if (unselected.isNotEmpty()) {
                                Text("Suggested in ${cityPlan.cityName}:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    unselected.forEach { sug ->
                                        FilterPillChip(
                                            label = "+ ${sug.name} (${"★".repeat(sug.priorityStars)})",
                                            selected = false,
                                            onClick = { controller.toggleAttractionInCity(cityPlan.cityName, sug) }
                                        )
                                    }
                                }
                            }

                            // Action Bar: [+ Add Place] [Rearrange] [View on Map]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { searchPlaceDialogCity = cityPlan },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("+ Add Place", style = MaterialTheme.typography.labelSmall, color = Coral, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { rearrangeDialogCity = cityPlan },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Rearrange", style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { onStepJump(6) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("View Map", style = MaterialTheme.typography.labelSmall, color = Green, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Search & Add Custom Place Dialog
    searchPlaceDialogCity?.let { city ->
        var query by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Sightseeing") }
        var stars by remember { mutableIntStateOf(4) }
        val pool = remember(city.cityName) {
            SmartTripCatalog.attractionsForCity(city.cityName, city.stateName)
        }
        val filtered = remember(pool, query) {
            if (query.isBlank()) pool else pool.filter { it.name.contains(query, true) || it.category.contains(query, true) }
        }

        AlertDialog(
            onDismissRequest = { searchPlaceDialogCity = null },
            containerColor = SurfaceWhite,
            title = { Text("Search / Add Place in ${city.cityName}", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search or type custom attraction") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (query.isNotBlank() && filtered.none { it.name.equals(query.trim(), true) }) {
                        Text("Priority Stars:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(5 to "★★★★★", 4 to "★★★★", 3 to "★★★").forEach { (s, lbl) ->
                                FilterPillChip(lbl, stars == s, { stars = s })
                            }
                        }
                        Button(
                            onClick = {
                                controller.addCustomAttractionToCity(city.cityName, query, category, stars)
                                searchPlaceDialogCity = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Coral)
                        ) {
                            Text("Add Custom \"$query\"")
                        }
                    }
                    filtered.take(5).forEach { spot ->
                        val added = plan.cityPlans.firstOrNull { it.cityName == city.cityName }
                            ?.attractions?.any { it.name.equals(spot.name, true) } == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { controller.toggleAttractionInCity(city.cityName, spot) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(spot.name, style = MaterialTheme.typography.titleSmall, color = Navy)
                                PriorityStarsBadge(spot.priorityStars)
                            }
                            Text(if (added) "✓ Added" else "+ Add", color = if (added) Green else Coral, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { searchPlaceDialogCity = null }) { Text("Done", color = Navy) }
            }
        )
    }

    // Quick Reorder Modal
    rearrangeDialogCity?.let { target ->
        val liveCity = plan.cityPlans.firstOrNull { it.cityName == target.cityName } ?: target
        AlertDialog(
            onDismissRequest = { rearrangeDialogCity = null },
            containerColor = SurfaceWhite,
            title = { Text("Reorder ${liveCity.cityName} Places", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    liveCity.attractions.forEachIndexed { i, attr ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${i + 1}. ${attr.name}", modifier = Modifier.weight(1f), color = Navy)
                            IconButton(onClick = { controller.moveAttractionInCity(liveCity.cityName, i, i - 1) }, enabled = i > 0) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Move up")
                            }
                            IconButton(onClick = { controller.moveAttractionInCity(liveCity.cityName, i, i + 1) }, enabled = i < liveCity.attractions.lastIndex) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Move down")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { rearrangeDialogCity = null }) { Text("Done", color = Coral) }
            }
        )
    }
}
