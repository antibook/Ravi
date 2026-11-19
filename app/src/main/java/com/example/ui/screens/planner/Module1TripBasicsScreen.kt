package com.example.ui.screens.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.SmartTripCatalog
import com.example.data.SmartTripPlan
import com.example.ui.components.DesignCard
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Module1TripBasicsScreen(
    plan: SmartTripPlan,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit,
    onSaveAndNext: (
        tripName: String,
        startState: String,
        startCity: String,
        destState: String,
        destCity: String,
        startDateMillis: Long,
        daysCount: Int,
        adults: Int,
        children: Int,
        skippedTravellers: Boolean,
        approxBudget: Int,
        travelCategory: String,
        travelPreference: String
    ) -> Unit
) {
    var tripName by remember(plan.id) { mutableStateOf(plan.tripName) }
    var startState by remember(plan.id) { mutableStateOf(plan.startState) }
    var startCity by remember(plan.id) { mutableStateOf(plan.startCity) }
    var destState by remember(plan.id) { mutableStateOf(plan.destState) }
    var destCity by remember(plan.id) { mutableStateOf(plan.destCity) }
    var startDateMillis by remember(plan.id) { mutableLongStateOf(plan.startDateMillis) }
    var daysCount by remember(plan.id) { mutableIntStateOf(plan.daysCount) }
    var adults by remember(plan.id) { mutableIntStateOf(plan.adults) }
    var children by remember(plan.id) { mutableIntStateOf(plan.children) }
    var skippedTravellers by remember(plan.id) { mutableStateOf(plan.skippedTravellers) }
    var budgetInput by remember(plan.id) { mutableStateOf(plan.approxBudget.toString()) }
    var travelCategory by remember(plan.id) { mutableStateOf(plan.travelCategory) }
    var travelPreference by remember(plan.id) { mutableStateOf(plan.travelPreference) }
    var showDatePicker by remember { mutableStateOf(false) }

    val statesList = remember { SmartTripCatalog.allStatesWithCities.keys.toList() }
    val startCities = remember(startState) { SmartTripCatalog.allStatesWithCities[startState] ?: listOf("New Delhi") }
    val destCities = remember(destState) { SmartTripCatalog.allStatesWithCities[destState] ?: listOf("Jaipur") }

    val formattedDates = remember(startDateMillis, daysCount) {
        val endMillis = startDateMillis + (daysCount - 1).coerceAtLeast(0) * 86_400_000L
        val fmt = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        val yr = SimpleDateFormat("yyyy", Locale.ENGLISH)
        Triple(fmt.format(Date(startDateMillis)), fmt.format(Date(endMillis)), "${yr.format(Date(endMillis))}")
    }

    fun persistCurrent() {
        val parsedBudget = budgetInput.toIntOrNull() ?: plan.approxBudget
        onSaveAndNext(
            tripName,
            startState,
            startCity,
            destState,
            destCity,
            startDateMillis,
            daysCount,
            adults,
            children,
            skippedTravellers,
            parsedBudget,
            travelCategory,
            travelPreference
        )
    }

    SmartPlannerScaffold(
        step = 1,
        title = "Create Your Trip",
        subtitle = "Start with basic route, dates, group & style",
        onBack = onBack,
        onStepJump = { target ->
            persistCurrent()
            onStepJump(target)
        },
        onSaveAndExit = {
            persistCurrent()
            onSaveAndExit()
        },
        showBottomBack = false,
        bottomNextLabel = "Save & Continue to Places →",
        onBottomNext = {
            persistCurrent()
            onStepJump(2)
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Trip Name
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Text("1. Trip Name", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = tripName,
                        onValueChange = { tripName = it },
                        placeholder = { Text("e.g., Rajasthan Family Vacation") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("m1_trip_name"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 2. Starting Location & 3. Destination Location
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Text("2. Starting Location", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SearchableDropdownField(
                            label = "Start State",
                            selectedValue = startState,
                            options = statesList,
                            onSelect = { st ->
                                startState = st
                                startCity = SmartTripCatalog.allStatesWithCities[st]?.firstOrNull() ?: startCity
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "m1_start_state"
                        )
                        SearchableDropdownField(
                            label = "Start City",
                            selectedValue = startCity,
                            options = startCities,
                            onSelect = { startCity = it },
                            modifier = Modifier.weight(1f),
                            testTag = "m1_start_city"
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = DividerLight)
                    Spacer(Modifier.height(14.dp))

                    Text("3. Main Destination Location", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SearchableDropdownField(
                            label = "Dest State",
                            selectedValue = destState,
                            options = statesList,
                            onSelect = { st ->
                                destState = st
                                destCity = SmartTripCatalog.allStatesWithCities[st]?.firstOrNull() ?: destCity
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "m1_dest_state"
                        )
                        SearchableDropdownField(
                            label = "Dest City",
                            selectedValue = destCity,
                            options = destCities,
                            onSelect = { destCity = it },
                            modifier = Modifier.weight(1f),
                            testTag = "m1_dest_city"
                        )
                    }
                }
            }

            // 4. Travel Dates & Duration
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Text("4. Travel Dates & Duration", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showDatePicker = true },
                            color = TileIndigoBg,
                            border = BorderStroke(1.2.dp, TileIndigoBorder),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Indigo, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("START DATE", style = MaterialTheme.typography.labelSmall, color = Indigo)
                                    Text(formattedDates.first, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Lavender,
                            border = BorderStroke(1.2.dp, CardBorder),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { if (daysCount > 1) daysCount-- },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Fewer days", tint = Navy)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$daysCount", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                                    Text("Days", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                IconButton(
                                    onClick = { if (daysCount < 30) daysCount++ },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "More days", tint = Coral)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Surface(
                        color = TileGreenBg,
                        border = BorderStroke(1.dp, TileGreenBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Green, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Trip Range: ${formattedDates.first} → ${formattedDates.second} ${formattedDates.third} ($daysCount Days)",
                                style = MaterialTheme.typography.labelMedium,
                                color = Navy,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 5. Travellers (Optional)
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("5. Travellers (Optional)", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        FilterChip(
                            selected = skippedTravellers,
                            onClick = { skippedTravellers = !skippedTravellers },
                            label = { Text(if (skippedTravellers) "Skipped" else "Skip") }
                        )
                    }
                    if (!skippedTravellers) {
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TravellerStepper("Adults", adults, { if (adults > 1) adults-- }, { if (adults < 20) adults++ }, Modifier.weight(1f))
                            TravellerStepper("Children", children, { if (children > 0) children-- }, { if (children < 15) children++ }, Modifier.weight(1f))
                        }
                    } else {
                        Text("Travellers skipped — defaults to 1 traveller for estimates.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            // 6. Approximate Total Budget
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Text("6. Approximate Total Budget (INR)", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { txt -> budgetInput = txt.filter { it.isDigit() } },
                        prefix = { Text("₹ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        supportingText = {
                            val amt = budgetInput.toIntOrNull() ?: 0
                            if (amt > 0) Text("Approx ${formatInr(amt)} total")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 7. Travel Category
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Text("7. Travel Category", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    val cats = listOf(
                        Triple("Family", Icons.Default.FamilyRestroom, "Spacious stays & relaxed pace"),
                        Triple("Couple", Icons.Default.Favorite, "Scenic spots & boutique stays"),
                        Triple("Friends", Icons.Default.Groups, "Adventure & group savings"),
                        Triple("Solo", Icons.Default.Person, "Flexible & safe routes")
                    )
                    cats.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { (label, icon, desc) ->
                                val selected = travelCategory == label
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { travelCategory = label },
                                    color = if (selected) CoralSoft else Lavender,
                                    border = BorderStroke(1.5.dp, if (selected) Coral else CardBorder),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Icon(icon, contentDescription = null, tint = if (selected) Coral else Navy, modifier = Modifier.size(22.dp))
                                        Spacer(Modifier.height(6.dp))
                                        Text(label, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                        Text(desc, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            // 8. Travel Preference
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Text("8. Travel Preference", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    val prefs = listOf(
                        Triple("Budget", "Value-focused guesthouses, local transit & street food", Green),
                        Triple("Standard", "Comfortable 3-4★ hotels, AC cabs & balanced sightseeing", Indigo),
                        Triple("Luxury", "5★ heritage resorts, private SUVs & curated fine dining", Coral)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        prefs.forEach { (pref, expl, tint) ->
                            val selected = travelPreference == pref
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { travelPreference = pref },
                                color = if (selected) tint.copy(alpha = 0.1f) else SurfaceWhite,
                                border = BorderStroke(1.5.dp, if (selected) tint else CardBorder),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selected, onClick = { travelPreference = pref })
                                    Spacer(Modifier.width(6.dp))
                                    Column {
                                        Text(pref, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                        Text(expl, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { startDateMillis = it }
                        showDatePicker = false
                    }
                ) { Text("Select", color = Coral, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Navy) }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun TravellerStepper(
    label: String,
    count: Int,
    onDec: () -> Unit,
    onInc: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Lavender,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = Navy, fontWeight = FontWeight.SemiBold)
                Text("$count", style = MaterialTheme.typography.titleMedium, color = Coral, fontWeight = FontWeight.Bold)
            }
            Row {
                IconButton(onClick = onDec, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease $label", tint = Navy, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onInc, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase $label", tint = Coral, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
