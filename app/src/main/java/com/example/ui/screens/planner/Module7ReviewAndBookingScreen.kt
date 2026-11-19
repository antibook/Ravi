package com.example.ui.screens.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SelectedHotelPlan
import com.example.data.SmartTripCatalog
import com.example.data.SmartTripPlan
import com.example.ui.components.DesignCard
import com.example.ui.components.FilterPillChip
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import com.example.viewmodel.SmartTripController

@Composable
fun Module7ReviewAndBookingScreen(
    plan: SmartTripPlan,
    controller: SmartTripController,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit,
    onCompletedNavigateDashboard: () -> Unit
) {
    var viewMode by remember { mutableStateOf("List") } // List vs Map
    var addActivityDay by remember { mutableStateOf<Int?>(null) }
    var confirmHotelPopup by remember { mutableStateOf<SelectedHotelPlan?>(null) }
    var confirmTaxiPopup by remember { mutableStateOf(false) }
    var showCompleteSummary by remember { mutableStateOf(false) }

    val schedules = remember(plan.daySchedules, plan.cityPlans, plan.selectedHotels, plan.selectedTransportLegs, plan.daysCount) {
        plan.daySchedules.ifEmpty { SmartTripCatalog.generateDaySchedules(plan) }
    }

    SmartPlannerScaffold(
        step = 7,
        title = "Review Your Complete Trip",
        subtitle = "Edit schedule, confirm bookings & complete trip",
        onBack = onBack,
        onStepJump = onStepJump,
        onSaveAndExit = onSaveAndExit,
        onBottomBack = { onStepJump(6) },
        bottomNextLabel = "Complete My Trip ✓",
        onBottomNext = { showCompleteSummary = true }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Mode Switcher & Quick Edit Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterPillChip("☰ Day-wise List", viewMode == "List", { viewMode = "List" })
                    FilterPillChip("🗺 Map View", viewMode == "Map", { viewMode = "Map" })
                }
                TextButton(onClick = { controller.rebuildDaySchedules() }) {
                    Text("↻ Sync Plan", color = Indigo, fontWeight = FontWeight.Bold)
                }
            }

            if (viewMode == "Map") {
                InteractiveTripMapCanvas(
                    plan = plan,
                    selectedDay = 0,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Edit Links for All Prior Modules
                    item {
                        DesignCard(contentPadding = PaddingValues(12.dp)) {
                            Text("Quick Edit Sections", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                QuickEditBtn("Dates", { onStepJump(1) }, Modifier.weight(1f))
                                QuickEditBtn("Cities/Spots", { onStepJump(2) }, Modifier.weight(1f))
                                QuickEditBtn("Budget", { onStepJump(3) }, Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                QuickEditBtn("Hotels", { onStepJump(4) }, Modifier.weight(1f))
                                QuickEditBtn("Transport", { onStepJump(5) }, Modifier.weight(1f))
                                QuickEditBtn("Map", { onStepJump(6) }, Modifier.weight(1f))
                            }
                        }
                    }

                    // A. Editable Day-Wise Itinerary
                    item {
                        Text("Suggested Day-Wise Itinerary (Editable)", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                    }

                    items(schedules, key = { it.dayNumber }) { day ->
                        DesignCard(contentPadding = PaddingValues(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(day.title, style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                                TextButton(onClick = { addActivityDay = day.dayNumber }) {
                                    Text("+ Add Stop", color = Indigo, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            day.activities.forEach { act ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (act.category == "Stay") Indigo else if (act.category == "Travel") Green else Coral)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("${act.time} · ${act.title}", style = MaterialTheme.typography.bodySmall, color = Navy, fontWeight = FontWeight.SemiBold)
                                        Text(act.subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                    IconButton(
                                        onClick = { controller.removeActivityFromDay(day.dayNumber, act.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove stop", tint = TextMuted, modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                        }
                    }

                    // B. Explicit Hotel Bookings Section
                    item {
                        Text("Hotel Bookings", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                    }

                    if (plan.selectedHotels.isEmpty()) {
                        item {
                            DesignCard(contentPadding = PaddingValues(14.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text("No hotels selected yet.", Modifier.weight(1f), color = TextSecondary)
                                    TextButton(onClick = { onStepJump(4) }) { Text("Browse Hotels", color = Coral) }
                                }
                            }
                        }
                    } else {
                        items(plan.selectedHotels, key = { it.cityName }) { hotel ->
                            DesignCard(
                                containerColor = if (hotel.isConfirmedBooking) TileGreenBg else SurfaceWhite,
                                borderColor = if (hotel.isConfirmedBooking) TileGreenBorder else CardBorder,
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("${hotel.cityName} · ${hotel.hotelName}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                        Text("${hotel.roomType} · ${hotel.checkInDate} → ${hotel.checkOutDate} · ${hotel.roomsCount} room(s)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                        Text(hotel.cancellationPolicy, style = MaterialTheme.typography.labelSmall, color = Green)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(formatInr(hotel.totalCost), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                                        if (hotel.isConfirmedBooking) {
                                            Text("✓ ${hotel.bookingRef}", style = MaterialTheme.typography.labelSmall, color = Green, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                if (!hotel.isConfirmedBooking) {
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = { confirmHotelPopup = hotel },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Navy)
                                    ) {
                                        Text("Book Hotel (Requires Confirmation)")
                                    }
                                }
                            }
                        }
                    }

                    // C. Explicit Taxi Booking Section
                    item {
                        Text("Taxi Booking", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        val taxi = plan.selectedTaxi
                        if (taxi == null) {
                            DesignCard(contentPadding = PaddingValues(14.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text("No taxi selected for local / outstation travel.", Modifier.weight(1f), color = TextSecondary)
                                    TextButton(onClick = { onStepJump(5) }) { Text("Select Taxi", color = Coral) }
                                }
                            }
                        } else {
                            DesignCard(
                                containerColor = if (taxi.isConfirmedBooking) TileGreenBg else SurfaceWhite,
                                borderColor = if (taxi.isConfirmedBooking) TileGreenBorder else CardBorder,
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(taxi.taxiName, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                        Text("Pickup: ${taxi.pickupLocation} · ${taxi.travelDate} · ${taxi.passengers} pax", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(formatInr(taxi.estimatedFare), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                                        if (taxi.isConfirmedBooking) {
                                            Text("✓ ${taxi.bookingRef}", style = MaterialTheme.typography.labelSmall, color = Green, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                if (!taxi.isConfirmedBooking) {
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = { confirmTaxiPopup = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Indigo)
                                    ) {
                                        Text("Book Taxi (Confirm Charges)")
                                    }
                                }
                            }
                        }
                    }

                    // E. Final Budget Distinction: Estimated vs Confirmed Costs
                    item {
                        DesignCard(containerColor = TileIndigoBg, borderColor = TileIndigoBorder, contentPadding = PaddingValues(14.dp)) {
                            Text("Final Budget Summary", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Estimated Total Trip Cost", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text(formatInr(plan.estimatedCategoryTotal), style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Confirmed Booking Charges", style = MaterialTheme.typography.bodySmall, color = Green)
                                Text(formatInr(plan.confirmedBookingTotal), style = MaterialTheme.typography.titleSmall, color = Green, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Activity to Day Dialog
    addActivityDay?.let { dayNum ->
        var title by remember { mutableStateOf("") }
        var time by remember { mutableStateOf("05:00 PM") }
        var notes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { addActivityDay = null },
            containerColor = SurfaceWhite,
            title = { Text("Add Stop to Day $dayNum", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, singleLine = true)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Activity / Place Name") }, singleLine = true)
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes / Subtitle") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    enabled = title.isNotBlank(),
                    onClick = {
                        controller.addActivityToDay(dayNum, time, title, notes, "Sightseeing")
                        addActivityDay = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { addActivityDay = null }) { Text("Cancel") } }
        )
    }

    // Explicit Hotel Booking Confirmation Dialog
    confirmHotelPopup?.let { h ->
        AlertDialog(
            onDismissRequest = { confirmHotelPopup = null },
            containerColor = SurfaceWhite,
            title = { Text("Confirm Hotel Booking?", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${h.hotelName} (${h.cityName})", style = MaterialTheme.typography.titleSmall, color = Indigo, fontWeight = FontWeight.Bold)
                    Text("Check-in: ${h.checkInDate} · Check-out: ${h.checkOutDate}")
                    Text("Rooms: ${h.roomsCount} · Room Type: ${h.roomType}")
                    Text("Total Price: ${formatInr(h.totalCost)}", color = Coral, fontWeight = FontWeight.Bold)
                    Text("Cancellation Policy: ${h.cancellationPolicy}", style = MaterialTheme.typography.labelSmall, color = Green)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        controller.confirmHotelBooking(h.cityName)
                        confirmHotelPopup = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Confirm Booking") }
            },
            dismissButton = { TextButton(onClick = { confirmHotelPopup = null }) { Text("Cancel") } }
        )
    }

    // Explicit Taxi Booking Confirmation Dialog
    if (confirmTaxiPopup) {
        val taxi = plan.selectedTaxi
        if (taxi != null) {
            AlertDialog(
                onDismissRequest = { confirmTaxiPopup = false },
                containerColor = SurfaceWhite,
                title = { Text("Confirm Taxi Booking?", color = Navy, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(taxi.taxiName, style = MaterialTheme.typography.titleSmall, color = Indigo, fontWeight = FontWeight.Bold)
                        Text("Pickup: ${taxi.pickupLocation}")
                        Text("Date: ${taxi.travelDate} · Passengers: ${taxi.passengers}")
                        Text("Estimated Total Charges: ${formatInr(taxi.estimatedFare)}", color = Coral, fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            controller.confirmTaxiBookingInTrip()
                            confirmTaxiPopup = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Coral)
                    ) { Text("Confirm Taxi") }
                },
                dismissButton = { TextButton(onClick = { confirmTaxiPopup = false }) { Text("Cancel") } }
            )
        }
    }

    // Complete My Trip Final Summary Dialog
    if (showCompleteSummary) {
        AlertDialog(
            onDismissRequest = { showCompleteSummary = false },
            containerColor = SurfaceWhite,
            title = { Text("Complete & Save Your Trip?", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(plan.tripName.ifBlank { "${plan.destCity} Trip" }, style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                    Text("Route: ${plan.startCity} → ${plan.cityPlans.joinToString(" → ") { it.cityName }}")
                    Text("Dates: ${plan.dateRangeText}")
                    Text("Estimated Budget: ${formatInr(plan.estimatedCategoryTotal)}")
                    Text("Confirmed Bookings: ${plan.selectedHotels.count { it.isConfirmedBooking }} hotels, ${if (plan.selectedTaxi?.isConfirmedBooking == true) 1 else 0} taxi")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompleteSummary = false
                        controller.completeTripAndSave()
                        onCompletedNavigateDashboard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) { Text("Save to My Trips") }
            },
            dismissButton = { TextButton(onClick = { showCompleteSummary = false }) { Text("Review Again") } }
        )
    }
}

@Composable
private fun QuickEditBtn(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 6.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.SemiBold)
    }
}
