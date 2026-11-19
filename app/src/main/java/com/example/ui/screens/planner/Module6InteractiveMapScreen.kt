package com.example.ui.screens.planner

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.data.SmartTripCatalog
import com.example.data.SmartTripPlan
import com.example.ui.components.DesignCard
import com.example.ui.components.FilterPillChip
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import kotlin.math.roundToInt

data class MapMarkerNode(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // Start, Destination, City, Hotel, Attraction, Transport
    val lat: Double,
    val lng: Double,
    val dayTag: Int = 0
)

@Composable
fun Module6InteractiveMapScreen(
    plan: SmartTripPlan,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit
) {
    var mapOrList by remember { mutableStateOf("Map") }
    var selectedDayFilter by remember { mutableIntStateOf(0) } // 0 = All Route

    val schedules = remember(plan) {
        plan.daySchedules.ifEmpty { SmartTripCatalog.generateDaySchedules(plan) }
    }

    SmartPlannerScaffold(
        step = 6,
        title = "Explore Your Complete Travel Route",
        subtitle = "Interactive geo-route, pins & day-wise distances",
        onBack = onBack,
        onStepJump = onStepJump,
        onSaveAndExit = onSaveAndExit,
        onBottomBack = { onStepJump(5) },
        bottomNextLabel = "Next: Review & Book →",
        onBottomNext = { onStepJump(7) }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp)) {
            // Map vs List Mode Toggle + Day-wise Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterPillChip("🗺 Map View", mapOrList == "Map", { mapOrList = "Map" })
                    FilterPillChip("☰ List View", mapOrList == "List", { mapOrList = "List" })
                }
                Text(
                    text = "${plan.cityPlans.size} Cities",
                    style = MaterialTheme.typography.labelMedium,
                    color = Indigo,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterPillChip("All Days", selectedDayFilter == 0, { selectedDayFilter = 0 })
                schedules.forEach { d ->
                    val lbl = if (d.fromCity != null) "Day ${d.dayNumber} · ${d.fromCity}→${d.cityName}" else "Day ${d.dayNumber} · ${d.cityName}"
                    FilterPillChip(lbl, selectedDayFilter == d.dayNumber, { selectedDayFilter = d.dayNumber })
                }
            }

            Spacer(Modifier.height(10.dp))
            if (mapOrList == "Map") {
                InteractiveTripMapCanvas(
                    plan = plan,
                    selectedDay = selectedDayFilter,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // List View of Routes, Distances, Hotels, and Attractions
                val allCities = remember(plan.startCity, plan.cityPlans) {
                    val ordered = plan.cityPlans.map { it.cityName }
                    if (ordered.firstOrNull().equals(plan.startCity, true)) ordered else listOf(plan.startCity) + ordered
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(allCities.zipWithNext()) { (fromC, toC) ->
                        val km = SmartTripCatalog.cityRoadDistanceKm(fromC, toC)
                        val leg = plan.selectedTransportLegs.firstOrNull { it.fromCity == fromC && it.toCity == toC }
                        DesignCard(contentPadding = PaddingValues(12.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Route, contentDescription = null, tint = Coral)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("$fromC → $toC", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = leg?.let { "${it.mode}: ${it.operatorName} (${it.durationText})" }
                                            ?: "Est. road travel ~${(km / 52).coerceAtLeast(1)}h ${(km % 52)}m",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Surface(color = IndigoSoft, shape = RoundedCornerShape(8.dp)) {
                                    Text("$km km", style = MaterialTheme.typography.labelMedium, color = Indigo, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }

                    items(plan.cityPlans) { cp ->
                        val hotel = plan.selectedHotels.firstOrNull { it.cityName == cp.cityName }
                        DesignCard(contentPadding = PaddingValues(12.dp)) {
                            Text("${cp.cityName} (${cp.stateName})", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            if (hotel != null) {
                                Text("🏨 Stay: ${hotel.hotelName} · ${hotel.checkInDate}", style = MaterialTheme.typography.bodySmall, color = Green, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(4.dp))
                            cp.attractions.forEach { a ->
                                Text("• ${a.name} (${a.duration})", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTripMapCanvas(
    plan: SmartTripPlan,
    selectedDay: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var zoom by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedNode by remember { mutableStateOf<MapMarkerNode?>(null) }

    val activeDayCity = remember(selectedDay, plan) {
        if (selectedDay == 0) null
        else plan.daySchedules.ifEmpty { SmartTripCatalog.generateDaySchedules(plan) }
            .firstOrNull { it.dayNumber == selectedDay }?.cityName
    }

    val nodes = remember(plan, activeDayCity) {
        buildList {
            val startCoord = SmartTripCatalog.getCoordinate(plan.startCity)
            add(
                MapMarkerNode(
                    id = "start_${plan.startCity}",
                    title = "Start: ${plan.startCity}",
                    subtitle = "${plan.startState} · Origin Point",
                    category = "Start",
                    lat = startCoord.lat,
                    lng = startCoord.lng
                )
            )
            plan.cityPlans.forEachIndexed { idx, cp ->
                val cCoord = SmartTripCatalog.getCoordinate(cp.cityName)
                val isLast = idx == plan.cityPlans.lastIndex
                add(
                    MapMarkerNode(
                        id = "city_${cp.cityName}",
                        title = cp.cityName,
                        subtitle = "${cp.stateName} · ${cp.attractions.size} attractions",
                        category = if (isLast) "Destination" else "City",
                        lat = cCoord.lat,
                        lng = cCoord.lng
                    )
                )
                if (activeDayCity == null || activeDayCity == cp.cityName) {
                    plan.selectedHotels.firstOrNull { it.cityName == cp.cityName }?.let { h ->
                        add(
                            MapMarkerNode(
                                id = "hotel_${h.hotelId}",
                                title = h.hotelName,
                                subtitle = "Hotel · ${h.roomType} · ${formatInr(h.totalCost)}",
                                category = "Hotel",
                                lat = h.lat,
                                lng = h.lng
                            )
                        )
                    }
                    cp.attractions.forEach { a ->
                        add(
                            MapMarkerNode(
                                id = "attr_${a.id}",
                                title = a.name,
                                subtitle = "${a.category} · ${a.duration} · Entry ${a.entryFee}",
                                category = "Attraction",
                                lat = a.lat,
                                lng = a.lng
                            )
                        )
                    }
                }
            }
            plan.selectedTransportLegs.forEach { leg ->
                val fCoord = SmartTripCatalog.getCoordinate(leg.fromCity)
                val tCoord = SmartTripCatalog.getCoordinate(leg.toCity)
                add(
                    MapMarkerNode(
                        id = "leg_${leg.fromCity}_${leg.toCity}",
                        title = "${leg.mode}: ${leg.operatorName}",
                        subtitle = "${leg.fromCity} ${leg.departureTime} → ${leg.toCity} ${leg.arrivalTime}",
                        category = "Transport",
                        lat = (fCoord.lat + tCoord.lat) / 2.0,
                        lng = (fCoord.lng + tCoord.lng) / 2.0
                    )
                )
            }
        }
    }

    val routeSequence = remember(plan.startCity, plan.cityPlans, activeDayCity) {
        val list = mutableListOf<Pair<String, RouteLatLng>>()
        if (activeDayCity == null) {
            val s = SmartTripCatalog.getCoordinate(plan.startCity)
            list.add(plan.startCity to RouteLatLng(s.lat, s.lng))
            plan.cityPlans.forEach { cp ->
                val c = SmartTripCatalog.getCoordinate(cp.cityName)
                list.add(cp.cityName to RouteLatLng(c.lat, c.lng))
            }
        } else {
            val c = SmartTripCatalog.getCoordinate(activeDayCity)
            list.add(activeDayCity to RouteLatLng(c.lat, c.lng))
            plan.cityPlans.firstOrNull { it.cityName == activeDayCity }?.attractions?.forEach { a ->
                list.add(a.name to RouteLatLng(a.lat, a.lng))
            }
        }
        list
    }

    val minLat = (nodes.minOfOrNull { it.lat } ?: 24.5) - 0.15
    val maxLat = (nodes.maxOfOrNull { it.lat } ?: 28.7) + 0.15
    val minLng = (nodes.minOfOrNull { it.lng } ?: 72.5) - 0.15
    val maxLng = (nodes.maxOfOrNull { it.lng } ?: 77.5) + 0.15

    fun project(lat: Double, lng: Double, width: Float, height: Float): Offset {
        val pad = 64f
        val xNorm = ((lng - minLng) / (maxLng - minLng).coerceAtLeast(0.05)).toFloat()
        val yNorm = (1.0 - (lat - minLat) / (maxLat - minLat).coerceAtLeast(0.05)).toFloat()
        val baseX = pad + xNorm * (width - pad * 2).coerceAtLeast(40f)
        val baseY = pad + yNorm * (height - pad * 2).coerceAtLeast(40f)
        val cx = width / 2f
        val cy = height / 2f
        return Offset(
            x = cx + (baseX - cx) * zoom + panOffset.x,
            y = cy + (baseY - cy) * zoom + panOffset.y
        )
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFE3EFE2))
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        zoom = (zoom * gestureZoom).coerceIn(0.8f, 4f)
                        panOffset = Offset(
                            x = (panOffset.x + pan.x).coerceIn(-600f, 600f),
                            y = (panOffset.y + pan.y).coerceIn(-600f, 600f)
                        )
                    }
                }
        ) {
            val mapW = constraints.maxWidth.toFloat()
            val mapH = constraints.maxHeight.toFloat()

            // Topographic Grid & Connecting Route Lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color.White.copy(alpha = 0.45f)
                for (i in 1..6) {
                    drawLine(gridColor, Offset(0f, size.height * i / 7f), Offset(size.width, size.height * i / 7f), strokeWidth = 2f)
                    drawLine(gridColor, Offset(size.width * i / 7f, 0f), Offset(size.width * i / 7f, size.height), strokeWidth = 2f)
                }

                val dash = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                for (i in 0 until routeSequence.lastIndex) {
                    val p1 = project(routeSequence[i].second.lat, routeSequence[i].second.lng, size.width, size.height)
                    val p2 = project(routeSequence[i + 1].second.lat, routeSequence[i + 1].second.lng, size.width, size.height)
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = p1,
                        end = p2,
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Coral,
                        start = p1,
                        end = p2,
                        strokeWidth = 5f,
                        pathEffect = dash,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Interactive Category Markers
            nodes.forEach { node ->
                val pos = project(node.lat, node.lng, mapW, mapH)
                val markerMeta: Triple<Color, ImageVector, String> = when (node.category) {
                    "Start" -> Triple(Green, Icons.Default.TripOrigin, "Start")
                    "Destination" -> Triple(DangerRed, Icons.Default.Flag, "End")
                    "Hotel" -> Triple(Indigo, Icons.Default.Hotel, "Hotel")
                    "Attraction" -> Triple(Amber, Icons.Default.PhotoCamera, "Spot")
                    "Transport" -> Triple(SkyBlue, Icons.Default.DirectionsTransit, "Transit")
                    else -> Triple(Coral, Icons.Default.LocationCity, "City")
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset((pos.x - 36).roundToInt(), (pos.y - 36).roundToInt()) }
                        .clickable { selectedNode = node }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = markerMeta.first,
                            border = BorderStroke(2.dp, SurfaceWhite),
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(markerMeta.second, contentDescription = node.title, tint = SurfaceWhite, modifier = Modifier.size(16.dp))
                            }
                        }
                        Surface(
                            color = SurfaceWhite.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = node.title.take(16),
                                style = MaterialTheme.typography.labelSmall,
                                color = Navy,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Legend Overlay (Top-Left)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                color = SurfaceWhite.copy(alpha = 0.94f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendDot("Start", Green)
                    LegendDot("End", DangerRed)
                    LegendDot("Hotel", Indigo)
                    LegendDot("Spot", Amber)
                    LegendDot("Transit", SkyBlue)
                }
            }

            // Zoom Controls (Top-Right)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SmallMapBtn(Icons.Default.Add, "Zoom In") { zoom = (zoom + 0.4f).coerceAtMost(4f) }
                SmallMapBtn(Icons.Default.Remove, "Zoom Out") { zoom = (zoom - 0.4f).coerceAtLeast(0.8f) }
                SmallMapBtn(Icons.Default.MyLocation, "Reset Camera") { zoom = 1f; panOffset = Offset.Zero }
            }

            // External Google Maps Navigation Button (Bottom-Right)
            Button(
                onClick = {
                    val destQuery = plan.cityPlans.joinToString(" to ") { it.cityName }
                    val coord = SmartTripCatalog.getCoordinate(plan.cityPlans.firstOrNull()?.cityName ?: plan.destCity)
                    val uri = Uri.parse("geo:${coord.lat},${coord.lng}?q=${Uri.encode(destQuery)}")
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy, contentColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Google Maps ↗", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Clicked Marker Details Popup Card
        selectedNode?.let { node ->
            DesignCard(containerColor = SurfaceWhite, borderColor = Indigo, contentPadding = PaddingValues(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${node.category} Pin · ${node.title}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Text(node.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("GPS: ${"%.4f".format(node.lat)}, ${"%.4f".format(node.lng)}", style = MaterialTheme.typography.labelSmall, color = Indigo)
                    }
                    IconButton(onClick = { selectedNode = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close pin card")
                    }
                }
            }
        }
    }
}

private data class RouteLatLng(val lat: Double, val lng: Double)

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(3.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Navy)
    }
}

@Composable
private fun SmallMapBtn(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = SurfaceWhite,
        shape = CircleShape,
        shadowElevation = 3.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = desc, tint = Navy, modifier = Modifier.size(18.dp))
        }
    }
}
