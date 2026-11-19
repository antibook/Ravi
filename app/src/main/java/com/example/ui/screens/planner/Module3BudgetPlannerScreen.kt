package com.example.ui.screens.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.SmartTripPlan
import com.example.ui.components.DesignCard
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import com.example.viewmodel.SmartTripController

@Composable
fun Module3BudgetPlannerScreen(
    plan: SmartTripPlan,
    controller: SmartTripController,
    onBack: () -> Unit,
    onStepJump: (Int) -> Unit,
    onSaveAndExit: () -> Unit
) {
    var budgetMode by remember(plan.id) { mutableStateOf(plan.budgetMode) }
    var fixedInput by remember(plan.id, plan.fixedBudget) { mutableStateOf(plan.fixedBudget.toString()) }
    var minInput by remember(plan.id, plan.minBudget) { mutableStateOf(plan.minBudget.toString()) }
    var maxInput by remember(plan.id, plan.maxBudget) { mutableStateOf(plan.maxBudget.toString()) }

    var accommodation by remember(plan.id, plan.accommodationBudget) { mutableIntStateOf(plan.accommodationBudget) }
    var transportation by remember(plan.id, plan.transportationBudget) { mutableIntStateOf(plan.transportationBudget) }
    var food by remember(plan.id, plan.foodBudget) { mutableIntStateOf(plan.foodBudget) }
    var localTravel by remember(plan.id, plan.localTravelBudget) { mutableIntStateOf(plan.localTravelBudget) }
    var sightseeing by remember(plan.id, plan.sightseeingBudget) { mutableIntStateOf(plan.sightseeingBudget) }
    var misc by remember(plan.id, plan.miscBudget) { mutableIntStateOf(plan.miscBudget) }
    var editCategories by remember { mutableStateOf(false) }

    val estTotal = (accommodation + transportation + food + localTravel + sightseeing + misc).coerceAtLeast(1)
    val perPerson = estTotal / plan.totalTravellers
    val dailyCost = estTotal / plan.daysCount.coerceAtLeast(1)

    fun saveCurrent() {
        controller.updateBudgetSettings(
            budgetMode = budgetMode,
            fixedBudget = fixedInput.toIntOrNull() ?: plan.fixedBudget,
            minBudget = minInput.toIntOrNull() ?: plan.minBudget,
            maxBudget = maxInput.toIntOrNull() ?: plan.maxBudget,
            accommodation = accommodation,
            transportation = transportation,
            food = food,
            localTravel = localTravel,
            sightseeing = sightseeing,
            misc = misc
        )
    }

    SmartPlannerScaffold(
        step = 3,
        title = "Plan Your Travel Budget",
        subtitle = "Initial estimate · updates dynamically with hotels & transit",
        onBack = onBack,
        onStepJump = {
            saveCurrent()
            onStepJump(it)
        },
        onSaveAndExit = {
            saveCurrent()
            onSaveAndExit()
        },
        onBottomBack = {
            saveCurrent()
            onStepJump(2)
        },
        bottomNextLabel = "Next: Select Hotels →",
        onBottomNext = {
            saveCurrent()
            onStepJump(4)
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Trip Context Recap
            item {
                DesignCard(containerColor = TileIndigoBg, borderColor = TileIndigoBorder, contentPadding = PaddingValues(14.dp)) {
                    Text("Trip Context Summary", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    val travellersText = if (plan.skippedTravellers) "1 Traveller (Default)" else "${plan.totalTravellers} Travellers (${plan.adults} Adults, ${plan.children} Children)"
                    Text(travellersText, style = MaterialTheme.typography.bodySmall, color = Navy, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${plan.daysCount} Days · ${plan.travelCategory} · ${plan.travelPreference} Style",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Cities: ${plan.cityPlans.joinToString(" → ") { it.cityName }}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Indigo,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Hero Estimate Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(IndigoBannerGradient)
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Estimated Total Trip Cost", style = MaterialTheme.typography.labelMedium, color = SurfaceWhite.copy(alpha = 0.8f))
                        Text(formatInr(estTotal), style = MaterialTheme.typography.displaySmall, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Per Person Cost", style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.75f))
                                Text(formatInr(perPerson), style = MaterialTheme.typography.titleMedium, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Estimated Daily Cost", style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.75f))
                                Text("${formatInr(dailyCost)} / day", style = MaterialTheme.typography.titleMedium, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Option A: Fixed vs Option B: Range
            item {
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Text("Target Budget Mode", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { budgetMode = "Fixed" },
                            color = if (budgetMode == "Fixed") CoralSoft else Lavender,
                            border = BorderStroke(1.4.dp, if (budgetMode == "Fixed") Coral else CardBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Option A: Fixed Budget", style = MaterialTheme.typography.labelLarge, color = Navy, fontWeight = FontWeight.Bold)
                                Text("Target total limit", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { budgetMode = "Range" },
                            color = if (budgetMode == "Range") IndigoSoft else Lavender,
                            border = BorderStroke(1.4.dp, if (budgetMode == "Range") Indigo else CardBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Option B: Budget Range", style = MaterialTheme.typography.labelLarge, color = Navy, fontWeight = FontWeight.Bold)
                                Text("Min – Max corridor", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    if (budgetMode == "Fixed") {
                        OutlinedTextField(
                            value = fixedInput,
                            onValueChange = { fixedInput = it.filter { c -> c.isDigit() } },
                            label = { Text("Fixed Target Budget (INR)") },
                            prefix = { Text("₹ ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = minInput,
                                onValueChange = { minInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Min Budget") },
                                prefix = { Text("₹ ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = maxInput,
                                onValueChange = { maxInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Max Budget") },
                                prefix = { Text("₹ ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 6 Expenditure Categories Visual Breakdown + Sliders
            item {
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category Breakdown (6 Heads)", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Row {
                            TextButton(onClick = { controller.resetBudgetToSmartEstimate() }) {
                                Text("Auto-Calc", color = Indigo, fontWeight = FontWeight.SemiBold)
                            }
                            TextButton(onClick = { editCategories = !editCategories }) {
                                Text(if (editCategories) "Done" else "Edit", color = Coral, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    val cats = listOf(
                        Triple("1. Accommodation", accommodation, Indigo),
                        Triple("2. Transportation (Intercity/Taxi)", transportation, Coral),
                        Triple("3. Food & Dining", food, Green),
                        Triple("4. Local Travel", localTravel, SkyBlue),
                        Triple("5. Sightseeing & Entry Tickets", sightseeing, PlumPurple),
                        Triple("6. Miscellaneous Expenses", misc, Amber)
                    )

                    // Stacked visual distribution bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                    ) {
                        cats.forEach { (_, amt, col) ->
                            val w = (amt.toFloat() / estTotal.toFloat()).coerceAtLeast(0.03f)
                            Box(
                                modifier = Modifier
                                    .weight(w)
                                    .fillMaxHeight()
                                    .background(col)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    cats.forEachIndexed { idx, (label, amt, col) ->
                        val ratio = (amt.toFloat() / estTotal.toFloat()).coerceIn(0.02f, 1f)
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, style = MaterialTheme.typography.bodySmall, color = Navy, fontWeight = FontWeight.SemiBold)
                                Text(formatInr(amt), style = MaterialTheme.typography.bodySmall, color = col, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = col,
                                trackColor = Lavender
                            )
                            if (editCategories) {
                                Slider(
                                    value = amt.toFloat(),
                                    onValueChange = { v ->
                                        val r = ((v / 500f).toInt() * 500).coerceAtLeast(0)
                                        when (idx) {
                                            0 -> accommodation = r
                                            1 -> transportation = r
                                            2 -> food = r
                                            3 -> localTravel = r
                                            4 -> sightseeing = r
                                            else -> misc = r
                                        }
                                    },
                                    valueRange = 0f..60000f,
                                    colors = SliderDefaults.colors(thumbColor = col, activeTrackColor = col)
                                )
                            }
                        }
                    }
                }
            }

            // Dynamic Sync Notice
            item {
                Surface(
                    color = TileAmberBg,
                    border = BorderStroke(1.dp, TileAmberBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Amber, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "This estimate updates automatically as you add hotels in Step 4 and intercity trains, flights, buses, or taxis in Step 5.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Navy
                        )
                    }
                }
            }
        }
    }
}
