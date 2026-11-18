package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.TaxiSearchDraft

@Composable
fun BookTaxiScreen(
    draft: TaxiSearchDraft,
    copy: AppStrings.LocalizedCopy,
    onBack: () -> Unit,
    onUpdateDraft: (TaxiSearchDraft) -> Unit,
    onSearchTaxi: () -> Unit
) {
    val modes = listOf("Local City", "Outstation", "Round Trip", "Airport")
    val stateOptions = CatalogData.allIndianStates
    val cityOptions = remember(draft.stateName) {
        val base = CatalogData.citiesForState(draft.stateName)
        base + listOf("${base.firstOrNull() ?: "City"} Airport")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = copy.bookTaxiTitle, onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modes.forEach { mode ->
                        FilterPillChip(
                            label = mode,
                            selected = draft.tripMode == mode,
                            onClick = { onUpdateDraft(draft.copy(tripMode = mode)) }
                        )
                    }
                }
            }

            item {
                DesignCard(
                    borderColor = TileSkyBorder,
                    containerColor = TileSkyBg.copy(alpha = 0.55f),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    StrictDropdownSelector(
                        label = "1. Select State",
                        selectedValue = draft.stateName,
                        options = stateOptions,
                        onSelect = { newState ->
                            val newCities = CatalogData.citiesForState(newState)
                            onUpdateDraft(
                                draft.copy(
                                    stateName = newState,
                                    pickup = newCities.firstOrNull() ?: "Jaipur",
                                    drop = newCities.getOrElse(1) { newCities.firstOrNull() ?: "Udaipur" }
                                )
                            )
                        },
                        testTag = "taxi_state_dropdown"
                    )
                    Spacer(Modifier.height(10.dp))
                    StrictDropdownSelector(
                        label = "2. Pickup City (From)",
                        selectedValue = draft.pickup,
                        options = cityOptions,
                        onSelect = { onUpdateDraft(draft.copy(pickup = it)) },
                        testTag = "taxi_pickup_dropdown"
                    )
                    Spacer(Modifier.height(10.dp))
                    StrictDropdownSelector(
                        label = "3. Drop City (To)",
                        selectedValue = draft.drop,
                        options = cityOptions,
                        onSelect = { onUpdateDraft(draft.copy(drop = it)) },
                        testTag = "taxi_drop_dropdown"
                    )
                    Spacer(Modifier.height(10.dp))
                    DateRangeSelectorField(
                        label = "4. Booking From – To Dates",
                        dateRangeText = draft.dateRangeLabel,
                        onDateRangePicked = { text, days ->
                            onUpdateDraft(draft.copy(dateRangeLabel = text, daysCount = days))
                        },
                        testTag = "taxi_date_range_selector"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DesignCard(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(12.dp),
                        onClick = { onUpdateDraft(draft.copy(passengers = (draft.passengers % 6) + 1)) }
                    ) {
                        Text("PASSENGERS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${draft.passengers} Adults", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    }
                    DesignCard(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(12.dp),
                        onClick = { onUpdateDraft(draft.copy(luggage = (draft.luggage % 5) + 1)) }
                    ) {
                        Text("LUGGAGE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${draft.luggage} bags", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                CoralPrimaryButton(
                    text = "Search Verified Taxis",
                    onClick = onSearchTaxi,
                    testTag = "search_taxi_button"
                )
            }
        }
    }
}

@Composable
fun TaxiListingsScreen(
    draft: TaxiSearchDraft,
    onBack: () -> Unit,
    onSelectTaxi: (TaxiVehicle) -> Unit
) {
    val filters = listOf("All", "Sedan", "SUV", "Hatchback", "6-7 seater")
    var selectedFilter by remember { mutableStateOf("All") }

    val stateTaxis = remember(draft.stateName) {
        val matched = CatalogData.taxis.filter { it.stateName.equals(draft.stateName, ignoreCase = true) }
        matched.ifEmpty { CatalogData.taxis }
    }

    val list = remember(stateTaxis, selectedFilter) {
        when (selectedFilter) {
            "All" -> stateTaxis
            "6-7 seater" -> stateTaxis.filter { it.category == "SUV" }
            else -> stateTaxis.filter { it.category == selectedFilter }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(
            title = "Verified Taxis · ${draft.pickup} → ${draft.drop}",
            onBack = onBack
        )

        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TileSkyBg,
                border = BorderStroke(1.dp, TileSkyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${draft.stateName} · ${draft.dateRangeLabel}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Navy,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { f ->
                    FilterPillChip(label = f, selected = selectedFilter == f, onClick = { selectedFilter = f })
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list) { taxi ->
                val multiDayPrice = taxi.breakdownTotal * draft.daysCount.coerceAtLeast(1)
                DesignCard(
                    borderColor = TileSkyBorder,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(GreenSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalTaxi, contentDescription = taxi.modelName, tint = Amber, modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(taxi.modelName, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text("${taxi.seatsInfo} · ${taxi.baseCity}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            if (taxi.isVerified) VerifiedBadgeChip("Verified Driver")
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatInr(multiDayPrice), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                            Text("${draft.daysCount}d · ₹${taxi.extraPerKm}/km extra", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    NavyPrimaryButton(
                        text = "View Driver & Fare",
                        onClick = { onSelectTaxi(taxi) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "taxi_view_details_${taxi.id}"
                    )
                }
            }
        }
    }
}

@Composable
fun DriverProfileScreen(
    taxi: TaxiVehicle,
    onBack: () -> Unit,
    onSeeFareBreakdown: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Driver & Vehicle", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DesignCard(contentPadding = PaddingValues(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(IndigoSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = taxi.driverName, tint = Indigo, modifier = Modifier.size(30.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(taxi.driverName, style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                        Text("${taxi.driverExperience} · ${taxi.driverTrips} trips · ★ ${taxi.driverRating}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                        VerifiedBadgeChip("Licence verified")
                    }
                }
            }

            DesignCard(contentPadding = PaddingValues(16.dp)) {
                Text("Vehicle · ${taxi.modelName}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                listOf("RC" to "Verified", "Permit" to "Verified", "Insurance" to "Valid", "Fitness / PUC" to "Valid").forEach { (doc, status) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(doc, style = MaterialTheme.typography.bodyMedium, color = Navy)
                        Text("✓ $status", style = MaterialTheme.typography.bodySmall, color = Green, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text("${taxi.vehiclePlate} · Last checked ${taxi.lastChecked}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }

            CoralPrimaryButton(
                text = "See Fare Breakdown",
                onClick = onSeeFareBreakdown,
                testTag = "see_fare_breakdown_btn"
            )
        }
    }
}

@Composable
fun FareBreakdownScreen(
    taxi: TaxiVehicle,
    daysCount: Int,
    onBack: () -> Unit,
    onBookNow: () -> Unit
) {
    val context = LocalContext.current
    var showPolicy by remember { mutableStateOf(false) }
    val totalFare = taxi.breakdownTotal * daysCount.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Fare Breakdown", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DesignCard(contentPadding = PaddingValues(16.dp)) {
                FareRow("Base fare ($daysCount days)", formatInr(taxi.baseFare * daysCount.coerceAtLeast(1)))
                FareRow("Driver allowance", formatInr(taxi.driverAllowance * daysCount.coerceAtLeast(1)))
                FareRow("Toll", formatInr(taxi.toll * daysCount.coerceAtLeast(1)))
                FareRow("Parking", formatInr(taxi.parking * daysCount.coerceAtLeast(1)))
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DividerLight)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                    Text(formatInr(totalFare), style = MaterialTheme.typography.titleMedium, color = Coral, fontWeight = FontWeight.Bold)
                }
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("Included ${250 * daysCount.coerceAtLeast(1)} km · Extra ₹${taxi.extraPerKm}/km", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("Free cancellation up to 24 hrs before", style = MaterialTheme.typography.bodySmall, color = Green)
            }

            CoralPrimaryButton(
                text = "Book Now",
                onClick = onBookNow,
                testTag = "fare_book_now_btn"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WhiteOutlinedButton(
                    text = "Contact Operator",
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                        runCatching { context.startActivity(intent) }
                    },
                    modifier = Modifier.weight(1f)
                )
                WhiteOutlinedButton(
                    text = "Cancellation Policy",
                    onClick = { showPolicy = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showPolicy) {
        AlertDialog(
            onDismissRequest = { showPolicy = false },
            containerColor = SurfaceWhite,
            title = { Text("Cancellation Policy", color = Navy) },
            text = { Text("100% refund if cancelled 24 hours before pickup. 50% fee within 6-24 hours.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { showPolicy = false }) { Text("OK", color = Coral) } }
        )
    }
}

@Composable
private fun FareRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Navy, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ConfirmTaxiBookingScreen(
    taxi: TaxiVehicle,
    draft: TaxiSearchDraft,
    userName: String,
    userPhone: String,
    onBack: () -> Unit,
    onPayAndConfirm: (String) -> Unit
) {
    var passengerName by remember { mutableStateOf(userName) }
    var contact by remember { mutableStateOf(userPhone) }
    var paymentMethod by remember { mutableStateOf("UPI") }
    val total = taxi.breakdownTotal * draft.daysCount.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Confirm Booking", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("${taxi.modelName} · ${draft.pickup} → ${draft.drop}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                Text("${draft.dateRangeLabel} · ${draft.time} · ${draft.passengers} passengers", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("PASSENGER NAME", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                OutlinedTextField(
                    value = passengerName,
                    onValueChange = { passengerName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Text("CONTACT", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                    Text(formatInr(total), style = MaterialTheme.typography.titleMedium, color = Coral, fontWeight = FontWeight.Bold)
                }
            }

            Text("Payment", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("UPI", "Card", "Pay driver").forEach { method ->
                    FilterPillChip(label = method, selected = paymentMethod == method, onClick = { paymentMethod = method })
                }
            }

            Spacer(Modifier.height(8.dp))
            CoralPrimaryButton(
                text = "Pay & Confirm",
                onClick = { onPayAndConfirm(paymentMethod) },
                testTag = "confirm_taxi_pay_btn"
            )
        }
    }
}

@Composable
fun AwaitingOperatorScreen(
    bookingCode: String,
    onViewLiveTracking: () -> Unit,
    onFindAnotherTaxi: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = AmberSoft) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Amber, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Awaiting operator", style = MaterialTheme.typography.headlineSmall, color = Navy, fontWeight = FontWeight.Bold)
        Text("Confirmation within 30 min", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(18.dp))

        DesignCard(contentPadding = PaddingValues(14.dp)) {
            Text("Booking #$bookingCode", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("If rejected, you get a full refund and alternatives.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        Spacer(Modifier.height(18.dp))
        CoralPrimaryButton(
            text = "View Booking & Track Driver",
            onClick = onViewLiveTracking,
            testTag = "view_live_tracking_btn"
        )
        Spacer(Modifier.height(10.dp))
        WhiteOutlinedButton(
            text = "Find another taxi",
            onClick = onFindAnotherTaxi,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LiveTripTrackingScreen(
    taxi: TaxiVehicle,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showSosDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Live Trip Tracking", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFDDEBD8))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawLine(Color.White.copy(alpha = 0.65f), Offset(0f, h * 0.78f), Offset(w, h * 0.20f), strokeWidth = 18f)
                    val pCar = Offset(w * 0.25f, h * 0.65f)
                    val pDest = Offset(w * 0.74f, h * 0.31f)
                    drawLine(
                        color = Indigo,
                        start = pCar,
                        end = pDest,
                        strokeWidth = 7f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f),
                        cap = StrokeCap.Round
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 58.dp, bottom = 86.dp)
                        .size(34.dp),
                    shape = CircleShape,
                    color = Indigo,
                    border = BorderStroke(2.dp, SurfaceWhite)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = "Driver", tint = SurfaceWhite, modifier = Modifier.size(18.dp))
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 62.dp, top = 74.dp)
                        .size(32.dp),
                    shape = CircleShape,
                    color = Coral,
                    border = BorderStroke(2.dp, SurfaceWhite)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = "Pickup", tint = SurfaceWhite, modifier = Modifier.size(16.dp))
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val uri = Uri.parse("geo:26.9124,75.7873?q=${Uri.encode("${taxi.baseCity} pickup")}")
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                        },
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "Open Google Maps ↗",
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            DesignCard(contentPadding = PaddingValues(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Arriving in 8 min", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                        Text("${taxi.driverName.substringBefore(" ")} · ${taxi.vehiclePlate}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                                runCatching { context.startActivity(intent) }
                            },
                        shape = CircleShape,
                        color = Coral
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Driver", tint = SurfaceWhite)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WhiteOutlinedButton(
                        text = "Share Trip",
                        onClick = {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Tracking my TravelGuide+ taxi with ${taxi.driverName} (${taxi.vehiclePlate}) arriving in 8 min.")
                            }
                            runCatching { context.startActivity(Intent.createChooser(share, "Share Live Trip")) }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { showSosDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = SurfaceWhite),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("live_sos_button")
                    ) {
                        Text("SOS")
                    }
                }
            }
        }
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            containerColor = SurfaceWhite,
            title = { Text("Emergency Services", color = DangerRed, fontWeight = FontWeight.Bold) },
            text = { Text("For immediate emergency help, call national emergency number 112, Police 100, or Ambulance 108.", color = Navy) },
            confirmButton = {
                Button(
                    onClick = {
                        showSosDialog = false
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        runCatching { context.startActivity(intent) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) { Text("Dial 112") }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) { Text("Close", color = Navy) }
            }
        )
    }
}
