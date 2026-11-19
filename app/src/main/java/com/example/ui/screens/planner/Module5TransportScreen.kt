package com.example.ui.screens.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.DesignCard
import com.example.ui.components.FilterPillChip
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import com.example.viewmodel.SmartTripController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Module5TransportScreen(
    plan: SmartTripPlan,
    controller: SmartTripController,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Train") }
    var taxiDetailDialog by remember { mutableStateOf<TaxiVehicle?>(null) }
    var taxiSelectDialog by remember { mutableStateOf<TaxiVehicle?>(null) }

    // Build intercity legs dynamically from Module 2's ordered cities (+ StartCity -> FirstCity)
    val orderedLegs = remember(plan.startCity, plan.cityPlans) {
        val cities = plan.cityPlans.map { it.cityName }.ifEmpty { listOf(plan.destCity) }
        val allPoints = if (cities.first().equals(plan.startCity, ignoreCase = true)) cities else listOf(plan.startCity) + cities
        allPoints.zipWithNext()
    }

    SmartPlannerScaffold(
        step = 5,
        title = "Choose Your Transportation",
        subtitle = "Intercity Train, Bus, Flight & Local Taxi planning",
        onBack = onBack,
        onStepJump = onStepJump,
        onSaveAndExit = onSaveAndExit,
        onBottomBack = { onStepJump(4) },
        bottomNextLabel = "Next: Interactive Travel Map →",
        onBottomNext = { onStepJump(6) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = TileIndigoBg,
                    border = BorderStroke(1.dp, TileIndigoBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = Indigo, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Planning only: Selected transport and taxis update your estimated budget without initiating a live booking.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy
                        )
                    }
                }
            }

            // 4 Major Transportation Mode Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Train", "Bus", "Flight", "Local Transport / Taxi").forEach { tab ->
                        FilterPillChip(label = tab, selected = activeTab == tab, onClick = { activeTab = tab })
                    }
                }
            }

            if (activeTab != "Local Transport / Taxi") {
                if (orderedLegs.isEmpty()) {
                    item {
                        DesignCard(contentPadding = PaddingValues(16.dp)) {
                            Text("Add at least 2 cities or a different starting city to plan intercity legs.", color = TextSecondary)
                        }
                    }
                } else {
                    orderedLegs.forEach { (fromCity, toCity) ->
                        val options = SmartTripCatalog.intercityOptionsForLeg(fromCity, toCity)
                            .filter { it.mode == activeTab }
                        val chosenLeg = plan.selectedTransportLegs.firstOrNull {
                            it.fromCity == fromCity && it.toCity == toCity
                        }
                        item(key = "leg_header_${fromCity}_${toCity}") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$fromCity → $toCity (${SmartTripCatalog.cityRoadDistanceKm(fromCity, toCity)} km)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Navy,
                                    fontWeight = FontWeight.Bold
                                )
                                if (chosenLeg != null) {
                                    Text(
                                        text = "✓ ${chosenLeg.mode} selected",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Green,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        items(options, key = { it.id }) { opt ->
                            val isSelected = chosenLeg?.serviceNumber == opt.serviceNumber && chosenLeg.mode == opt.mode
                            val totalFare = opt.farePerPerson * plan.totalTravellers
                            DesignCard(
                                borderColor = if (isSelected) Green else CardBorder,
                                containerColor = if (isSelected) TileGreenBg else SurfaceWhite,
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${opt.operatorName} (${opt.serviceNumber})", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                        Text("${opt.fromCity} ${opt.departureTime} → ${opt.toCity} ${opt.arrivalTime}", style = MaterialTheme.typography.bodySmall, color = Indigo, fontWeight = FontWeight.SemiBold)
                                        Text("Duration ${opt.durationText} · ${opt.classInfo} · ★ ${opt.rating}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(formatInr(totalFare), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                                        Text("${formatInr(opt.farePerPerson)} × ${plan.totalTravellers}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        controller.selectIntercityTransport(
                                            SelectedTransportLeg(
                                                fromCity = opt.fromCity,
                                                toCity = opt.toCity,
                                                mode = opt.mode,
                                                operatorName = opt.operatorName,
                                                serviceNumber = opt.serviceNumber,
                                                departureTime = opt.departureTime,
                                                arrivalTime = opt.arrivalTime,
                                                durationText = opt.durationText,
                                                farePerPerson = opt.farePerPerson,
                                                totalFare = totalFare
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Green else Navy)
                                ) {
                                    Text(if (isSelected) "✓ Selected for ${opt.fromCity} → ${opt.toCity}" else "Select ${opt.mode}")
                                }
                            }
                        }
                    }
                }
            } else {
                // Local Transport / Registered Taxi Section
                item {
                    plan.selectedTaxi?.let { sel ->
                        DesignCard(containerColor = TileGreenBg, borderColor = TileGreenBorder, contentPadding = PaddingValues(12.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Selected Taxi: ${sel.taxiName}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                    Text("Pickup: ${sel.pickupLocation} · ${sel.travelDate} · ${sel.passengers} pax · Est ${formatInr(sel.estimatedFare)}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                TextButton(onClick = { controller.clearTaxiPlan() }) { Text("Remove", color = Coral) }
                            }
                        }
                    }
                }

                items(CatalogData.taxis, key = { it.id }) { taxi ->
                    val isSelected = plan.selectedTaxi?.taxiId == taxi.id
                    val estTotal = taxi.breakdownTotal * plan.daysCount.coerceAtLeast(1)
                    DesignCard(
                        borderColor = if (isSelected) Green else CardBorder,
                        containerColor = if (isSelected) TileGreenBg else SurfaceWhite,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalTaxi, contentDescription = null, tint = Coral, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(taxi.modelName, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                Text("${taxi.category} · ${taxi.seatsInfo} · ${taxi.stateName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("★ ${taxi.driverRating} (${taxi.driverTrips} trips) · Driver ${taxi.driverName}", style = MaterialTheme.typography.labelSmall, color = Green, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatInr(estTotal), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                                Text("${plan.daysCount} days est.", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { taxiDetailDialog = taxi },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("View Details", color = Navy)
                            }
                            Button(
                                onClick = { taxiSelectDialog = taxi },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Green else Coral)
                            ) {
                                Text(if (isSelected) "✓ Selected Taxi" else "Select Taxi")
                            }
                        }
                    }
                }
            }
        }
    }

    taxiDetailDialog?.let { t ->
        AlertDialog(
            onDismissRequest = { taxiDetailDialog = null },
            containerColor = SurfaceWhite,
            title = { Text(t.modelName, color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${t.category} · ${t.seatsInfo} · ${t.luggageInfo}", color = Indigo, style = MaterialTheme.typography.labelMedium)
                    Text("Driver: ${t.driverName} (${t.driverExperience}, ★ ${t.driverRating})", color = Navy, style = MaterialTheme.typography.bodySmall)
                    Text("Facilities: AC Climate Control, Verified GPS Tracking, Luggage Carrier, Sanitized Cabin", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text("Estimated Daily Breakdown: Base ₹${t.baseFare} + Allowance ₹${t.driverAllowance} + Toll/Parking ₹${t.toll + t.parking}", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        taxiDetailDialog = null
                        taxiSelectDialog = t
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Select Taxi") }
            },
            dismissButton = { TextButton(onClick = { taxiDetailDialog = null }) { Text("Close") } }
        )
    }

    taxiSelectDialog?.let { t ->
        val fmt = remember { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH) }
        var pickup by remember { mutableStateOf("${plan.cityPlans.firstOrNull()?.cityName ?: plan.destCity} Railway Station / Airport") }
        var travelDate by remember { mutableStateOf(fmt.format(Date(plan.startDateMillis))) }
        var pax by remember { mutableIntStateOf(plan.totalTravellers) }
        val estFare = t.breakdownTotal * plan.daysCount.coerceAtLeast(1)

        AlertDialog(
            onDismissRequest = { taxiSelectDialog = null },
            containerColor = SurfaceWhite,
            title = { Text("Add ${t.modelName} to Trip", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = pickup, onValueChange = { pickup = it }, label = { Text("Pickup Location") }, singleLine = true)
                    OutlinedTextField(value = travelDate, onValueChange = { travelDate = it }, label = { Text("Travel Date") }, singleLine = true)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Passengers: $pax", Modifier.weight(1f), color = Navy, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { if (pax > 1) pax-- }) { Text("-") }
                        TextButton(onClick = { if (pax < 10) pax++ }) { Text("+") }
                    }
                    Text("Estimated Fare (${plan.daysCount} days): ${formatInr(estFare)}", color = Coral, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        controller.selectTaxiPlan(
                            SelectedTaxiPlan(
                                taxiId = t.id,
                                taxiName = t.modelName,
                                vehicleType = t.category,
                                seatingCapacity = t.seatsInfo,
                                isAc = true,
                                pickupLocation = pickup,
                                travelDate = travelDate,
                                passengers = pax,
                                estimatedFare = estFare,
                                rating = t.driverRating,
                                facilities = listOf("AC", "GPS", "Verified Driver")
                            )
                        )
                        taxiSelectDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Add to Trip") }
            },
            dismissButton = { TextButton(onClick = { taxiSelectDialog = null }) { Text("Cancel") } }
        )
    }
}
