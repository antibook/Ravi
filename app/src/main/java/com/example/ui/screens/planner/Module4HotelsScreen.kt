package com.example.ui.screens.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.DesignCard
import com.example.ui.components.FilterPillChip
import com.example.ui.components.GradientBannerThumb
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import com.example.viewmodel.SmartTripController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Module4HotelsScreen(
    plan: SmartTripPlan,
    registeredHotels: List<SmartHotelItem>,
    controller: SmartTripController,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit
) {
    val cityNames = remember(plan.cityPlans) {
        plan.cityPlans.map { it.cityName }.ifEmpty { listOf(plan.destCity) }
    }
    var activeCity by remember(cityNames) { mutableStateOf(cityNames.first()) }
    val activeCityState = plan.cityPlans.firstOrNull { it.cityName == activeCity }?.stateName ?: plan.destState

    // Filters & Sorting State
    var maxPriceFilter by remember { mutableIntStateOf(6000) }
    var minRatingFilter by remember { mutableDoubleStateOf(0.0) }
    var acOnly by remember { mutableStateOf(false) }
    var parkingOnly by remember { mutableStateOf(false) }
    var breakfastOnly by remember { mutableStateOf(false) }
    var familyOnly by remember { mutableStateOf(false) }
    var friendsOnly by remember { mutableStateOf(false) }
    var nearAttractionOnly by remember { mutableStateOf(false) }
    var locationQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("Recommended") }

    var detailHotel by remember { mutableStateOf<SmartHotelItem?>(null) }
    var addHotelDialog by remember { mutableStateOf<SmartHotelItem?>(null) }
    var showRegisterHotelDialog by remember { mutableStateOf(false) }

    val cityAttractions = remember(activeCity, plan.cityPlans) {
        plan.cityPlans.firstOrNull { it.cityName == activeCity }?.attractions
            ?.ifEmpty { null }
            ?: SmartTripCatalog.attractionsForCity(activeCity, activeCityState).take(4)
    }

    val rawHotels = remember(activeCity, activeCityState, registeredHotels) {
        SmartTripCatalog.hotelsForCity(activeCity, activeCityState, registeredHotels)
    }

    fun minAttractionDist(h: SmartHotelItem): Double {
        return cityAttractions.minOfOrNull { a -> SmartTripCatalog.computeDistanceKm(h.lat, h.lng, a.lat, a.lng) } ?: 2.5
    }

    val filteredHotels = remember(
        rawHotels, maxPriceFilter, minRatingFilter, acOnly, parkingOnly, breakfastOnly,
        familyOnly, friendsOnly, nearAttractionOnly, locationQuery, sortBy, cityAttractions
    ) {
        rawHotels.filter { h ->
            h.pricePerNight <= maxPriceFilter &&
                h.rating >= minRatingFilter &&
                (!acOnly || h.isAc) &&
                (!parkingOnly || h.hasParking) &&
                (!breakfastOnly || h.hasBreakfast) &&
                (!familyOnly || h.familyFriendly) &&
                (!friendsOnly || h.friendsFriendly) &&
                (!nearAttractionOnly || minAttractionDist(h) <= 4.0) &&
                (locationQuery.isBlank() || h.address.contains(locationQuery, true) || h.name.contains(locationQuery, true))
        }.let { list ->
            when (sortBy) {
                "Lowest Price" -> list.sortedBy { it.pricePerNight }
                "Highest Rating" -> list.sortedByDescending { it.rating }
                "Nearest to Attractions" -> list.sortedBy { minAttractionDist(it) }
                else -> list
            }
        }
    }

    val currentSelection = plan.selectedHotels.firstOrNull { it.cityName == activeCity }
    val isSkipped = plan.skippedHotelCities.contains(activeCity)

    SmartPlannerScaffold(
        step = 4,
        title = "Find Your Perfect Stay",
        subtitle = "Plan hotels per city · no automatic booking sent",
        onBack = onBack,
        onStepJump = onStepJump,
        onSaveAndExit = onSaveAndExit,
        onBottomBack = { onStepJump(3) },
        bottomNextLabel = "Next: Transportation (${plan.selectedHotels.size}/${cityNames.size}) →",
        onBottomNext = { onStepJump(5) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Planning-Only Notice + Admin Hotel Registration trigger
            item {
                Surface(
                    color = TileAmberBg,
                    border = BorderStroke(1.2.dp, TileAmberBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Amber, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Planning only: Selecting a hotel adds it to your trip budget. Booking happens only on final confirmation.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { showRegisterHotelDialog = true }) {
                            Text("+ Register", color = Indigo, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // City Sections Tabs (JAIPUR HOTELS, JODHPUR HOTELS, ...)
            item {
                Text("Select City Section (${cityNames.size} cities)", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cityNames.forEach { c ->
                        val picked = plan.selectedHotels.any { it.cityName == c }
                        val skipped = plan.skippedHotelCities.contains(c)
                        val label = when {
                            picked -> "✓ ${c.uppercase()} HOTELS"
                            skipped -> "↷ ${c.uppercase()} (Skipped)"
                            else -> "${c.uppercase()} HOTELS"
                        }
                        FilterPillChip(label = label, selected = activeCity == c, onClick = { activeCity = c })
                    }
                }
            }

            // City Selection Summary & Skip Button
            item {
                DesignCard(
                    containerColor = if (currentSelection != null) TileGreenBg else SurfaceWhite,
                    borderColor = if (currentSelection != null) TileGreenBorder else CardBorder,
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSelection?.let { "Selected: ${it.hotelName}" }
                                    ?: if (isSkipped) "Hotel skipped for $activeCity"
                                    else "$activeCity · ${filteredHotels.size} hotels available",
                                style = MaterialTheme.typography.titleSmall,
                                color = Navy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentSelection?.let { "${it.checkInDate} → ${it.checkOutDate} · ${it.roomsCount} room(s) · ${formatInr(it.totalCost)}" }
                                    ?: "Choose a stay below or skip for this city",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        TextButton(onClick = { controller.skipHotelForCity(activeCity) }) {
                            Text(if (isSkipped) "Skipped" else "Skip City", color = Coral, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Comprehensive Filters & Sort
            item {
                DesignCard(contentPadding = PaddingValues(12.dp)) {
                    OutlinedTextField(
                        value = locationQuery,
                        onValueChange = { locationQuery = it },
                        placeholder = { Text("Filter by location or hotel name in $activeCity...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Indigo) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Sort by:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Recommended", "Lowest Price", "Highest Rating", "Nearest to Attractions").forEach { s ->
                            FilterPillChip(s, sortBy == s, { sortBy = s })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Filters:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterPillChip("≤ ₹2,500", maxPriceFilter == 2500, { maxPriceFilter = if (maxPriceFilter == 2500) 6000 else 2500 })
                        FilterPillChip("4.5★+", minRatingFilter == 4.5, { minRatingFilter = if (minRatingFilter == 4.5) 0.0 else 4.5 })
                        FilterPillChip("AC", acOnly, { acOnly = !acOnly })
                        FilterPillChip("Parking", parkingOnly, { parkingOnly = !parkingOnly })
                        FilterPillChip("Breakfast", breakfastOnly, { breakfastOnly = !breakfastOnly })
                        FilterPillChip("Family", familyOnly, { familyOnly = !familyOnly })
                        FilterPillChip("Friends", friendsOnly, { friendsOnly = !friendsOnly })
                        FilterPillChip("≤4 km to Spots", nearAttractionOnly, { nearAttractionOnly = !nearAttractionOnly })
                    }
                }
            }

            // Hotel Cards (10-14 listings)
            items(filteredHotels, key = { it.id }) { hotel ->
                var showDistances by remember { mutableStateOf(false) }
                val isSelected = currentSelection?.hotelId == hotel.id

                DesignCard(
                    borderColor = if (isSelected) Green else CardBorder,
                    containerColor = if (isSelected) TileGreenBg else SurfaceWhite,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GradientBannerThumb(colorKey = hotel.colorKey, modifier = Modifier.size(64.dp), cornerRadius = 14.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(hotel.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text("${hotel.starCategory}★ Hotel · ${hotel.address}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = AmberSoft, shape = RoundedCornerShape(6.dp)) {
                                    Text("★ ${hotel.rating}", style = MaterialTheme.typography.labelSmall, color = Navy, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Text(
                                    text = "${if (hotel.isAc) "AC" else "Non-AC"}${if (hotel.hasParking) " · Parking" else ""}${if (hotel.hasBreakfast) " · Breakfast" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Indigo
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatInr(hotel.pricePerNight), style = MaterialTheme.typography.titleMedium, color = Coral, fontWeight = FontWeight.Bold)
                            Text("per night", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    // Suitability & Facilities tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (hotel.familyFriendly) TagChip("Family Friendly", TileGreenBg, Green)
                        if (hotel.friendsFriendly) TagChip("Friends Friendly", TileSkyBg, SkyBlue)
                        hotel.facilities.take(3).forEach { f -> TagChip(f, Lavender, Navy) }
                    }

                    // Expandable Distances from Tourist Attractions
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDistances = !showDistances }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val nearest = minAttractionDist(hotel)
                        Text(
                            text = "Distance to attractions (Nearest: $nearest km)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Indigo,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            if (showDistances) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle distances",
                            tint = Indigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (showDistances) {
                        Surface(
                            color = Lavender,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                cityAttractions.forEach { spot ->
                                    val km = SmartTripCatalog.computeDistanceKm(hotel.lat, hotel.lng, spot.lat, spot.lng)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(spot.name, style = MaterialTheme.typography.labelSmall, color = Navy)
                                        Text("$km KM", style = MaterialTheme.typography.labelSmall, color = Coral, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { detailHotel = hotel },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Details", color = Navy, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { addHotelDialog = hotel },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Green else Coral,
                                contentColor = SurfaceWhite
                            )
                        ) {
                            Text(if (isSelected) "✓ Added to Trip" else "Add to My Trip", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // View Hotel Details Modal
    detailHotel?.let { h ->
        AlertDialog(
            onDismissRequest = { detailHotel = null },
            containerColor = SurfaceWhite,
            title = { Text(h.name, color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${h.starCategory}★ · Rating ${h.rating} · ${h.address}", style = MaterialTheme.typography.labelMedium, color = Indigo)
                    Text(h.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("Facilities: ${h.facilities.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = Navy)
                    Text("Policy: ${h.cancellationPolicy}", style = MaterialTheme.typography.labelSmall, color = Green)
                    Spacer(Modifier.height(4.dp))
                    Text("Attractions Nearby:", style = MaterialTheme.typography.labelMedium, color = Navy, fontWeight = FontWeight.Bold)
                    cityAttractions.forEach { spot ->
                        val km = SmartTripCatalog.computeDistanceKm(h.lat, h.lng, spot.lat, spot.lng)
                        Text("• ${spot.name}: $km KM", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        detailHotel = null
                        addHotelDialog = h
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Add to My Trip") }
            },
            dismissButton = {
                TextButton(onClick = { detailHotel = null }) { Text("Close", color = Navy) }
            }
        )
    }

    // Add Hotel to Trip Modal (Auto-populated Check-in/out & Guests from Module 1)
    addHotelDialog?.let { h ->
        val fmt = remember { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH) }
        val defaultCheckIn = remember(plan.startDateMillis) { fmt.format(Date(plan.startDateMillis)) }
        val defaultNights = (plan.daysCount / cityNames.size.coerceAtLeast(1)).coerceAtLeast(1)
        val defaultCheckOut = remember(plan.startDateMillis, defaultNights) {
            fmt.format(Date(plan.startDateMillis + defaultNights * 86_400_000L))
        }

        var checkIn by remember { mutableStateOf(defaultCheckIn) }
        var checkOut by remember { mutableStateOf(defaultCheckOut) }
        var nights by remember { mutableIntStateOf(defaultNights) }
        var rooms by remember { mutableIntStateOf(((plan.totalTravellers + 1) / 2).coerceAtLeast(1)) }
        var guests by remember { mutableIntStateOf(plan.totalTravellers) }
        var selectedRoom by remember { mutableStateOf(h.roomTypes.first()) }

        AlertDialog(
            onDismissRequest = { addHotelDialog = null },
            containerColor = SurfaceWhite,
            title = { Text("Add ${h.name}", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Room Type:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    h.roomTypes.forEach { r ->
                        FilterPillChip(
                            label = "${r.name} · ${formatInr(r.pricePerNight)}/nt",
                            selected = selectedRoom.name == r.name,
                            onClick = { selectedRoom = r }
                        )
                    }
                    OutlinedTextField(value = checkIn, onValueChange = { checkIn = it }, label = { Text("Check-in Date") }, singleLine = true)
                    OutlinedTextField(value = checkOut, onValueChange = { checkOut = it }, label = { Text("Check-out Date") }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Nights: $nights", Modifier.weight(1f), color = Navy, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { if (nights > 1) nights-- }) { Text("-") }
                        TextButton(onClick = { nights++ }) { Text("+") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Rooms: $rooms · Guests: $guests", Modifier.weight(1f), color = Navy, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { if (rooms > 1) rooms-- }) { Text("-") }
                        TextButton(onClick = { rooms++ }) { Text("+") }
                    }
                    Text(
                        text = "Est. Stay Total: ${formatInr(selectedRoom.pricePerNight * nights * rooms)} (planning only)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Coral,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        controller.selectHotelForCity(
                            SelectedHotelPlan(
                                cityName = h.cityName,
                                hotelId = h.id,
                                hotelName = h.name,
                                roomType = selectedRoom.name,
                                pricePerNight = selectedRoom.pricePerNight,
                                checkInDate = checkIn,
                                checkOutDate = checkOut,
                                nights = nights,
                                roomsCount = rooms,
                                guestsCount = guests,
                                cancellationPolicy = h.cancellationPolicy,
                                lat = h.lat,
                                lng = h.lng
                            )
                        )
                        addHotelDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Save to Trip") }
            },
            dismissButton = {
                TextButton(onClick = { addHotelDialog = null }) { Text("Cancel", color = Navy) }
            }
        )
    }

    // Admin / Hotel Owner Property Registration Dialog (Module 4.E)
    if (showRegisterHotelDialog) {
        var hName by remember { mutableStateOf("") }
        var hAddress by remember { mutableStateOf("Central Avenue, $activeCity") }
        var hDesc by remember { mutableStateOf("Comfortable property near popular attractions.") }
        var hPrice by remember { mutableStateOf("2800") }
        var hAc by remember { mutableStateOf(true) }
        var hParking by remember { mutableStateOf(true) }
        var hBreakfast by remember { mutableStateOf(true) }
        val baseCoord = SmartTripCatalog.getCoordinate(activeCity)

        AlertDialog(
            onDismissRequest = { showRegisterHotelDialog = false },
            containerColor = SurfaceWhite,
            title = { Text("Register Hotel ($activeCity)", color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = hName, onValueChange = { hName = it }, label = { Text("Hotel Name") }, singleLine = true)
                    OutlinedTextField(value = hAddress, onValueChange = { hAddress = it }, label = { Text("Address") }, singleLine = true)
                    OutlinedTextField(
                        value = hPrice,
                        onValueChange = { hPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Room Price / Night (INR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(value = hDesc, onValueChange = { hDesc = it }, label = { Text("Description") }, maxLines = 2)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterPillChip("AC", hAc, { hAc = !hAc })
                        FilterPillChip("Parking", hParking, { hParking = !hParking })
                        FilterPillChip("Breakfast", hBreakfast, { hBreakfast = !hBreakfast })
                    }
                    Text(
                        text = "Coordinates: ${baseCoord.lat}, ${baseCoord.lng} · Distances to attractions auto-calculated.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = hName.isNotBlank(),
                    onClick = {
                        controller.registerCustomHotel(
                            stateName = activeCityState,
                            cityName = activeCity,
                            name = hName,
                            description = hDesc,
                            address = hAddress,
                            pricePerNight = hPrice.toIntOrNull() ?: 2800,
                            starCategory = 4,
                            isAc = hAc,
                            hasParking = hParking,
                            hasBreakfast = hBreakfast,
                            familyFriendly = true,
                            friendsFriendly = true,
                            lat = baseCoord.lat + 0.005,
                            lng = baseCoord.lng + 0.005
                        )
                        showRegisterHotelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo)
                ) { Text("Submit Property") }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterHotelDialog = false }) { Text("Cancel", color = Navy) }
            }
        )
    }
}

@Composable
private fun TagChip(label: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
