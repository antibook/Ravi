package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.PlannerDraft

@Composable
fun PlannerDestinationScreen(
    draft: PlannerDraft,
    onBack: () -> Unit,
    onUpdateDestination: (state: String, city: String, fullState: Boolean) -> Unit,
    onAddCity: (String) -> Unit,
    onNext: () -> Unit
) {
    val states = listOf("Rajasthan", "Kerala", "Goa", "Himachal")
    val citiesByState = mapOf(
        "Rajasthan" to listOf("Jaipur", "Udaipur", "Jodhpur", "Jaisalmer", "Pushkar"),
        "Kerala" to listOf("Kochi", "Munnar", "Alleppey"),
        "Goa" to listOf("North Goa", "South Goa"),
        "Himachal" to listOf("Shimla", "Manali", "Dharamshala")
    )
    val availableCities = citiesByState[draft.state] ?: listOf("Jaipur")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Plan Your Trip", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Choose destination", style = MaterialTheme.typography.headlineMedium, color = Navy, fontWeight = FontWeight.Bold)
            Text("Step 1 of 4 · Country → State → City", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("COUNTRY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(Modifier.height(4.dp))
                Text("🇮🇳 India ▾", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("STATE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    states.forEach { st ->
                        FilterPillChip(
                            label = st,
                            selected = draft.state == st,
                            onClick = {
                                val firstCity = citiesByState[st]?.firstOrNull() ?: "Jaipur"
                                onUpdateDestination(st, firstCity, false)
                            }
                        )
                    }
                }
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("CITY / REGION", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = draft.selectedCities.joinToString(" + "),
                    style = MaterialTheme.typography.titleSmall,
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableCities.take(3).forEach { c ->
                        FilterPillChip(
                            label = "+ $c",
                            selected = draft.selectedCities.contains(c),
                            onClick = { onAddCity(c) }
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                FilterPillChip(
                    label = "Full ${draft.state}",
                    selected = draft.fullStateMode,
                    onClick = { onUpdateDestination(draft.state, availableCities.first(), !draft.fullStateMode) }
                )
            }

            Spacer(Modifier.weight(1f))
            CoralPrimaryButton(
                text = "Next →",
                onClick = onNext,
                testTag = "planner_step1_next"
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlannerDatesScreen(
    draft: PlannerDraft,
    onBack: () -> Unit,
    onUpdateDates: (days: Int, startDay: Int) -> Unit,
    onNext: () -> Unit
) {
    val quickDurations = listOf(3 to "3 Days", 5 to "5 Days", 7 to "7 Days", 10 to "10 Days", 14 to "12–15 Days")
    val calendarDays = (7..27).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Plan Your Trip", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Dates & duration", style = MaterialTheme.typography.headlineMedium, color = Navy, fontWeight = FontWeight.Bold)

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text(
                    text = "December 2026",
                    style = MaterialTheme.typography.titleSmall,
                    color = Navy,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                calendarDays.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { day ->
                            val isSelected = day == draft.startDayOfMonth ||
                                (day > draft.startDayOfMonth && day <= draft.startDayOfMonth + draft.selectedDaysCount)
                            val isAnchor = day == draft.startDayOfMonth
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isAnchor -> Coral
                                            isSelected -> CoralSoft
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { onUpdateDates(draft.selectedDaysCount, day) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$day",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isAnchor) SurfaceWhite else Navy,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }

            Text("Quick duration", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickDurations.take(4).forEach { (days, label) ->
                    FilterPillChip(
                        label = label,
                        selected = draft.selectedDaysCount == days,
                        onClick = { onUpdateDates(days, draft.startDayOfMonth) }
                    )
                }
            }
            FilterPillChip(
                label = quickDurations[4].second,
                selected = draft.selectedDaysCount == 14,
                onClick = { onUpdateDates(14, draft.startDayOfMonth) }
            )

            Spacer(Modifier.weight(1f))
            CoralPrimaryButton(
                text = "Next →",
                onClick = onNext,
                testTag = "planner_step2_next"
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlannerTravellersScreen(
    draft: PlannerDraft,
    isGenerating: Boolean,
    onBack: () -> Unit,
    onUpdateTravellers: (adults: Int, children: Int, travelType: String, budgetRange: String, prefToggle: String?) -> Unit,
    onGeneratePlan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Plan Your Trip", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Who is travelling?", style = MaterialTheme.typography.headlineMedium, color = Navy, fontWeight = FontWeight.Bold)
            }

            item {
                StepperCard(
                    label = "Adults",
                    value = draft.adults,
                    onDecrement = { onUpdateTravellers(draft.adults - 1, draft.children, draft.travelType, draft.budgetRange, null) },
                    onIncrement = { onUpdateTravellers(draft.adults + 1, draft.children, draft.travelType, draft.budgetRange, null) }
                )
            }

            item {
                StepperCard(
                    label = "Children",
                    value = draft.children,
                    onDecrement = { onUpdateTravellers(draft.adults, draft.children - 1, draft.travelType, draft.budgetRange, null) },
                    onIncrement = { onUpdateTravellers(draft.adults, draft.children + 1, draft.travelType, draft.budgetRange, null) }
                )
            }

            item {
                Text("Travel type", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Family", "Couple", "Friends").forEach { type ->
                        FilterPillChip(
                            label = type,
                            selected = draft.travelType == type,
                            onClick = { onUpdateTravellers(draft.adults, draft.children, type, draft.budgetRange, null) }
                        )
                    }
                }
            }

            item {
                Text("Budget range", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Budget", "Standard", "Premium").forEach { range ->
                        FilterPillChip(
                            label = range,
                            selected = draft.budgetRange == range,
                            onClick = { onUpdateTravellers(draft.adults, draft.children, draft.travelType, range, null) }
                        )
                    }
                }
            }

            item {
                Text("Preferences", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Heritage", "Food", "Shopping", "Adventure").forEach { pref ->
                        FilterPillChip(
                            label = pref,
                            selected = draft.preferences.contains(pref),
                            onClick = { onUpdateTravellers(draft.adults, draft.children, draft.travelType, draft.budgetRange, pref) }
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                CoralPrimaryButton(
                    text = if (isGenerating) "Generating Plan..." else "Generate Plan ✨",
                    enabled = !isGenerating,
                    onClick = onGeneratePlan,
                    testTag = "generate_plan_button"
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StepperCard(
    label: String,
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    DesignCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Lavender
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrement) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease $label", tint = Navy)
                    }
                    Text(
                        text = "$value",
                        style = MaterialTheme.typography.titleMedium,
                        color = Navy,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrement) {
                        Icon(Icons.Default.Add, contentDescription = "Increase $label", tint = Coral)
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestedItineraryScreen(
    trip: PlannedTrip,
    copy: AppStrings.LocalizedCopy,
    isFavorited: Boolean = false,
    isOnline: Boolean = true,
    isCachedOffline: Boolean = false,
    cachedAtMillis: Long? = null,
    onBack: () -> Unit,
    onToggleFavorite: (() -> Unit)? = null,
    onOpenDayDetail: (Int) -> Unit,
    onOpenRouteMap: (Int) -> Unit,
    onOpenBudget: () -> Unit,
    onSaveTrip: () -> Unit,
    onDownloadOffline: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Overview") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(
            title = "Suggested Itinerary",
            onBack = onBack,
            trailing = if (onToggleFavorite != null) {
                {
                    WishlistHeartButton(
                        isSaved = isFavorited,
                        onToggle = onToggleFavorite,
                        testTag = "itinerary_wishlist_button"
                    )
                }
            } else null
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(IndigoBannerGradient)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(trip.title, style = MaterialTheme.typography.headlineSmall, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${trip.dateRange} · ${trip.adults} Adults${if (trip.children > 0) ", ${trip.children} Child" else ""} · ${trip.travelType} · ${trip.budgetRange}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onOpenBudget),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Est. budget (Tap to edit)", style = MaterialTheme.typography.bodySmall, color = SurfaceWhite.copy(alpha = 0.85f))
                            Text(formatInr(trip.budget.total), style = MaterialTheme.typography.titleMedium, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OfflineCacheStatusBanner(
                    isOnline = isOnline,
                    isCached = isCachedOffline,
                    cachedAtMillis = cachedAtMillis,
                    actionLabel = if (isCachedOffline) "Refresh" else "Cache",
                    onActionClick = onDownloadOffline,
                    modifier = Modifier.testTag("itinerary_offline_banner")
                )
            }

            if (trip.isPlaceholderAi) {
                item {
                    AiNoticeBanner(text = copy.aiNoticeText)
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Overview", "Day-wise", "Map").forEach { tab ->
                        FilterPillChip(
                            label = tab,
                            selected = activeTab == tab,
                            onClick = {
                                if (tab == "Map") onOpenRouteMap(1)
                                else activeTab = tab
                            }
                        )
                    }
                }
            }

            items(trip.itineraryDays) { day ->
                DesignCard(
                    onClick = { onOpenDayDetail(day.dayNumber) },
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CoralSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("D${day.dayNumber}", style = MaterialTheme.typography.labelLarge, color = Coral, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(day.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text(day.summary, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Text(">", color = Coral, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WhiteOutlinedButton(
                        text = "Save Trip",
                        icon = Icons.Outlined.FavoriteBorder,
                        onClick = onSaveTrip,
                        modifier = Modifier.weight(1f),
                        testTag = "save_trip_button"
                    )
                    NavyPrimaryButton(
                        text = if (isCachedOffline) "✓ Offline Cached" else "⬇ Save Offline",
                        onClick = onDownloadOffline,
                        modifier = Modifier.weight(1f),
                        testTag = "download_trip_button"
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DayDetailScreen(
    trip: PlannedTrip,
    dayNumber: Int,
    onBack: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onOpenMap: (Int) -> Unit
) {
    val day = trip.itineraryDays.firstOrNull { it.dayNumber == dayNumber }
        ?: trip.itineraryDays.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(
            title = "Day ${day.dayNumber} · ${day.city}",
            onBack = onBack,
            trailing = {
                Surface(color = CoralSoft, shape = RoundedCornerShape(10.dp)) {
                    Text(
                        text = "${day.dayNumber}/${trip.days}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Coral,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                DesignCard(contentPadding = PaddingValues(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start: ${day.startLocation}", style = MaterialTheme.typography.labelLarge, color = Navy)
                        Text("${day.distanceKm} km · 🚕 ${day.travelMode}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            items(day.stops) { stop ->
                val dotColor = when (stop.category) {
                    "Food" -> Green
                    "Stay" -> Amber
                    else -> Coral
                }
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("${stop.time} · ${stop.title}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(stop.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = AmberSoft,
                    border = BorderStroke(1.dp, Amber.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = day.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = Navy,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WhiteOutlinedButton(
                        text = "← Prev",
                        onClick = { if (day.dayNumber > 1) onSelectDay(day.dayNumber - 1) },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { onOpenMap(day.dayNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = SurfaceWhite),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("day_open_map_button")
                    ) {
                        Text("🗺 Map")
                    }
                    NavyPrimaryButton(
                        text = "Next →",
                        onClick = { if (day.dayNumber < trip.itineraryDays.size) onSelectDay(day.dayNumber + 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun RouteMapScreen(
    trip: PlannedTrip,
    dayNumber: Int,
    onBack: () -> Unit,
    onOpenBudget: () -> Unit,
    onViewPlaces: () -> Unit,
    onBookTaxi: () -> Unit
) {
    val context = LocalContext.current
    val day = trip.itineraryDays.firstOrNull { it.dayNumber == dayNumber } ?: trip.itineraryDays.first()
    var showHotels by remember { mutableStateOf(true) }
    var showTaxi by remember { mutableStateOf(true) }
    var selectedPin by remember { mutableStateOf("Amber Fort · Stop 1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(
            title = "Route Map · Day ${day.dayNumber}",
            onBack = onBack,
            trailing = {
                TextButton(onClick = onOpenBudget) {
                    Text("Budget", color = Coral, fontWeight = FontWeight.Bold)
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stylized Interactive Route Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFDDEBD8))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Subtle roads
                    drawLine(Color.White.copy(alpha = 0.65f), Offset(0f, h * 0.72f), Offset(w, h * 0.25f), strokeWidth = 18f)
                    drawLine(Color.White.copy(alpha = 0.55f), Offset(w * 0.15f, 0f), Offset(w * 0.65f, h), strokeWidth = 14f)
                    // Dashed route between 1 -> 2 -> 3
                    val p1 = Offset(w * 0.20f, h * 0.25f)
                    val p2 = Offset(w * 0.44f, h * 0.45f)
                    val p3 = Offset(w * 0.72f, h * 0.34f)
                    val dash = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                    drawLine(Coral, p1, p2, strokeWidth = 6f, pathEffect = dash, cap = StrokeCap.Round)
                    drawLine(Coral, p2, p3, strokeWidth = 6f, pathEffect = dash, cap = StrokeCap.Round)
                }

                // Interactive Markers
                MapPinBubble("1", Coral, Modifier.align(Alignment.TopStart).padding(start = 42.dp, top = 46.dp)) {
                    selectedPin = day.stops.firstOrNull()?.title ?: "Stop 1"
                }
                MapPinBubble("2", Coral, Modifier.align(Alignment.Center).offset(x = (-18).dp, y = (-12).dp)) {
                    selectedPin = day.stops.getOrNull(2)?.title ?: "Stop 2"
                }
                MapPinBubble("3", Coral, Modifier.align(Alignment.TopEnd).padding(end = 60.dp, top = 74.dp)) {
                    selectedPin = day.stops.lastOrNull()?.title ?: "Stop 3"
                }
                if (showTaxi) {
                    MapPinBubble("T", Green, Modifier.align(Alignment.BottomStart).padding(start = 66.dp, bottom = 52.dp)) {
                        selectedPin = "Taxi Pickup · C-Scheme"
                    }
                }
                if (showHotels) {
                    MapPinBubble("H", Indigo, Modifier.align(Alignment.BottomEnd).padding(end = 56.dp, bottom = 74.dp)) {
                        selectedPin = "Heritage Haveli Stay"
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    color = SurfaceWhite.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = selectedPin,
                        style = MaterialTheme.typography.labelMedium,
                        color = Navy,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total ${day.distanceKm} km", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Text("Est. journey ${day.estDuration}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterPillChip("Hotels", showHotels, { showHotels = !showHotels })
                        FilterPillChip("Taxi", showTaxi, { showTaxi = !showTaxi })
                    }
                }
            }

            CoralPrimaryButton(
                text = "Directions ↗ Google Maps",
                onClick = {
                    val uri = Uri.parse("geo:26.9124,75.7873?q=${Uri.encode("${day.city} tourist route")}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    runCatching { context.startActivity(intent) }
                },
                testTag = "open_google_maps_btn"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WhiteOutlinedButton(
                    text = "View Places",
                    onClick = onViewPlaces,
                    modifier = Modifier.weight(1f)
                )
                NavyPrimaryButton(
                    text = "Book Taxi 🚕",
                    onClick = onBookTaxi,
                    modifier = Modifier.weight(1f),
                    testTag = "map_book_taxi_btn"
                )
            }
        }
    }
}

@Composable
private fun MapPinBubble(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = color,
        shadowElevation = 4.dp,
        border = BorderStroke(2.dp, SurfaceWhite)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = SurfaceWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BudgetCalculatorScreen(
    trip: PlannedTrip,
    onBack: () -> Unit,
    onUpdateBudget: (BudgetBreakdown) -> Unit,
    onSaveAndShare: () -> Unit
) {
    val context = LocalContext.current
    var editing by remember { mutableStateOf(false) }
    var hotels by remember(trip.budget) { mutableIntStateOf(trip.budget.hotels) }
    var transport by remember(trip.budget) { mutableIntStateOf(trip.budget.transport) }
    var food by remember(trip.budget) { mutableIntStateOf(trip.budget.food) }
    var tickets by remember(trip.budget) { mutableIntStateOf(trip.budget.tickets) }
    var misc by remember(trip.budget) { mutableIntStateOf(trip.budget.miscellaneous) }

    val total = hotels + transport + food + tickets + misc

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Budget Calculator", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(IndigoBannerGradient)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Estimated total · ${trip.adults + trip.children} travellers",
                            style = MaterialTheme.typography.bodySmall,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = formatInr(total),
                            style = MaterialTheme.typography.displaySmall,
                            color = SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Sample estimate. Actual cost varies by dates & services.",
                            style = MaterialTheme.typography.labelSmall,
                            color = SurfaceWhite.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val rows = listOf(
                Triple("🏨 Hotels", hotels, Indigo),
                Triple("🚕 Transport", transport, Coral),
                Triple("🍽 Food", food, Green),
                Triple("🎟 Tickets & Activities", tickets, PlumPurple),
                Triple("🛍 Miscellaneous", misc, Amber)
            )

            items(rows.indices.toList()) { idx ->
                val (label, amount, barColor) = rows[idx]
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                        Text(formatInr(amount), style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (amount.toFloat() / total.coerceAtLeast(1)).coerceIn(0.05f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = barColor,
                        trackColor = Lavender
                    )
                    if (editing) {
                        Slider(
                            value = amount.toFloat(),
                            onValueChange = { v ->
                                val rounded = (v / 500).toInt() * 500
                                when (idx) {
                                    0 -> hotels = rounded
                                    1 -> transport = rounded
                                    2 -> food = rounded
                                    3 -> tickets = rounded
                                    else -> misc = rounded
                                }
                            },
                            valueRange = 500f..50000f,
                            colors = SliderDefaults.colors(thumbColor = barColor, activeTrackColor = barColor)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WhiteOutlinedButton(
                        text = if (editing) "Done" else "Edit",
                        onClick = {
                            if (editing) {
                                onUpdateBudget(BudgetBreakdown(hotels, transport, food, tickets, misc))
                            }
                            editing = !editing
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "budget_edit_button"
                    )
                    Button(
                        onClick = {
                            onUpdateBudget(BudgetBreakdown(hotels, transport, food, tickets, misc))
                            onSaveAndShare()
                            val shareText = "TravelGuide+ Budget for ${trip.title}: ${formatInr(total)} (Hotels ${formatInr(hotels)}, Transport ${formatInr(transport)}, Food ${formatInr(food)})"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            runCatching { context.startActivity(Intent.createChooser(intent, "Share Trip Budget")) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = SurfaceWhite),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp)
                            .testTag("budget_save_share_button")
                    ) {
                        Text("Save & Share")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
