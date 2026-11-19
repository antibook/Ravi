package com.example.ui.screens.planner

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SmartTripCatalog
import com.example.data.SmartTripPlan
import com.example.ui.components.DesignCard
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import com.example.viewmodel.SmartTripController

@Composable
fun Module8MyTripDashboardScreen(
    plan: SmartTripPlan,
    controller: SmartTripController,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onExitToMyTrips: () -> Unit
) {
    val context = LocalContext.current
    var showFullItinerary by remember { mutableStateOf(false) }
    var showFullMap by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val schedules = remember(plan.daySchedules, plan.cityPlans) {
        plan.daySchedules.ifEmpty { SmartTripCatalog.generateDaySchedules(plan) }
    }
    val totalAttractions = plan.cityPlans.sumOf { it.attractions.size }

    SmartPlannerScaffold(
        step = 8,
        title = "Your Trip Overview",
        subtitle = "Complete Trip Dashboard · ${plan.status}",
        onBack = onBack,
        onStepJump = onStepJump,
        onSaveAndExit = onExitToMyTrips,
        bottomBackLabel = "← Edit Plan",
        onBottomBack = { onStepJump(1) },
        bottomNextLabel = "Open My Trips",
        onBottomNext = onExitToMyTrips
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1–8. Hero Overview Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(IndigoBannerGradient)
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = SurfaceWhite.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = plan.status.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SurfaceWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(plan.travelCategory, style = MaterialTheme.typography.labelMedium, color = SurfaceWhite.copy(alpha = 0.9f))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(plan.tripName.ifBlank { "${plan.destCity} Trip" }, style = MaterialTheme.typography.headlineSmall, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${plan.startCity} → ${plan.cityPlans.joinToString(" → ") { it.cityName }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SurfaceWhite.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "📅 ${plan.dateRangeText}  ·  👥 ${plan.totalTravellers} Travellers",
                            style = MaterialTheme.typography.labelMedium,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "🏙 ${plan.cityPlans.size} Cities  ·  📍 $totalAttractions Attractions",
                            style = MaterialTheme.typography.labelMedium,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Dashboard Actions Grid
            item {
                DesignCard(contentPadding = PaddingValues(12.dp)) {
                    Text("Trip Actions", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DashActionBtn("Edit Trip", Icons.Default.Edit, { onStepJump(1) }, Modifier.weight(1f))
                        DashActionBtn("Itinerary", Icons.Default.EventNote, { showFullItinerary = !showFullItinerary }, Modifier.weight(1f))
                        DashActionBtn("View Map", Icons.Default.Map, { showFullMap = !showFullMap }, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DashActionBtn("Share", Icons.Default.Share, {
                            val summary = buildString {
                                appendLine("Trip: ${plan.tripName}")
                                appendLine("Route: ${plan.startCity} -> ${plan.cityPlans.joinToString(" -> ") { it.cityName }}")
                                appendLine("Dates: ${plan.dateRangeText}")
                                appendLine("Estimated Budget: ${formatInr(plan.estimatedCategoryTotal)}")
                                appendLine("Days: ${schedules.size}")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            runCatching { context.startActivity(Intent.createChooser(intent, "Share Trip Summary")) }
                        }, Modifier.weight(1f))
                        DashActionBtn("Download", Icons.Default.Download, { controller.completeTripAndSave() }, Modifier.weight(1f))
                        DashActionBtn("Delete", Icons.Default.DeleteOutline, { showDeleteConfirm = true }, Modifier.weight(1f), isDanger = true)
                    }
                }
            }

            // Interactive Map Preview / Expand
            if (showFullMap) {
                item {
                    InteractiveTripMapCanvas(
                        plan = plan,
                        selectedDay = 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    )
                }
            } else {
                item {
                    DesignCard(onClick = { showFullMap = true }, containerColor = TileGreenBg, borderColor = TileGreenBorder, contentPadding = PaddingValues(14.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Green)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Interactive Route Map", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                Text("Tap to preview all ${plan.cityPlans.size} cities, hotels & attraction pins", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Text("Open →", color = Coral, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 9. Day-wise Itinerary Preview
            item {
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Day-Wise Itinerary (${schedules.size} Days)", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { showFullItinerary = !showFullItinerary }) {
                            Text(if (showFullItinerary) "Collapse" else "View All", color = Coral, fontWeight = FontWeight.Bold)
                        }
                    }
                    val visibleDays = if (showFullItinerary) schedules else schedules.take(2)
                    visibleDays.forEach { d ->
                        Spacer(Modifier.height(6.dp))
                        Text(d.title, style = MaterialTheme.typography.labelLarge, color = Indigo, fontWeight = FontWeight.Bold)
                        d.activities.forEach { act ->
                            Text("  • ${act.time}: ${act.title}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }

            // 10 & 14. Hotel Bookings & Details
            item {
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Hotels & Booking Details", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { onStepJump(7) }) { Text("Manage", color = Coral) }
                    }
                    if (plan.selectedHotels.isEmpty()) {
                        Text("No hotels attached to this trip yet.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    } else {
                        plan.selectedHotels.forEach { h ->
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text("${h.cityName}: ${h.hotelName}", style = MaterialTheme.typography.bodySmall, color = Navy, fontWeight = FontWeight.Bold)
                                    Text("${h.checkInDate} → ${h.checkOutDate} · ${h.roomsCount} room(s)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatInr(h.totalCost), style = MaterialTheme.typography.labelLarge, color = Coral, fontWeight = FontWeight.Bold)
                                    Text(if (h.isConfirmedBooking) "Confirmed (${h.bookingRef})" else "Planned", style = MaterialTheme.typography.labelSmall, color = if (h.isConfirmedBooking) Green else Indigo)
                                }
                            }
                        }
                    }
                }
            }

            // 11. Transportation Details
            item {
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Transportation & Taxi", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { onStepJump(5) }) { Text("Edit", color = Coral) }
                    }
                    if (plan.selectedTransportLegs.isEmpty() && plan.selectedTaxi == null) {
                        Text("No intercity transit or taxi selected yet.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    plan.selectedTransportLegs.forEach { leg ->
                        Text("• ${leg.mode}: ${leg.fromCity} → ${leg.toCity} (${leg.operatorName}, ${formatInr(leg.totalFare)})", style = MaterialTheme.typography.bodySmall, color = Navy)
                    }
                    plan.selectedTaxi?.let { t ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "🚕 Taxi: ${t.taxiName} · ${t.pickupLocation} · ${if (t.isConfirmedBooking) "Booked (${t.bookingRef})" else "Planned"} (${formatInr(t.estimatedFare)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (t.isConfirmedBooking) Green else Navy,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 12. Budget Summary
            item {
                DesignCard(containerColor = TileIndigoBg, borderColor = TileIndigoBorder, contentPadding = PaddingValues(14.dp)) {
                    Text("Budget Summary", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    BudgetRow("Accommodation", plan.accommodationBudget)
                    BudgetRow("Transportation", plan.transportationBudget)
                    BudgetRow("Food & Dining", plan.foodBudget)
                    BudgetRow("Local Travel", plan.localTravelBudget)
                    BudgetRow("Sightseeing & Tickets", plan.sightseeingBudget)
                    BudgetRow("Miscellaneous", plan.miscBudget)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = DividerLight)
                    BudgetRow("Estimated Total", plan.estimatedCategoryTotal, highlight = true)
                    BudgetRow("Confirmed Booking Spend", plan.confirmedBookingTotal, highlight = true)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SurfaceWhite,
            title = { Text("Delete This Trip?", color = DangerRed, fontWeight = FontWeight.Bold) },
            text = { Text("This removes ${plan.tripName.ifBlank { "this trip" }} from My Trips and offline storage.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        controller.deleteSmartTrip(plan.id)
                        onExitToMyTrips()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun DashActionBtn(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        contentPadding = PaddingValues(horizontal = 6.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.2.dp, if (isDanger) DangerRed.copy(alpha = 0.5f) else CardBorder)
    ) {
        Icon(icon, contentDescription = label, tint = if (isDanger) DangerRed else Indigo, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isDanger) DangerRed else Navy, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BudgetRow(label: String, amount: Int, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            color = Navy,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = formatInr(amount),
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            color = if (highlight) Coral else Navy,
            fontWeight = FontWeight.Bold
        )
    }
}
