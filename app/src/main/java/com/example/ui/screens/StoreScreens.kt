package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TravelStoreScreen(
    copy: AppStrings.LocalizedCopy,
    savedItemIds: Set<String> = emptySet(),
    onSelectGuide: (TravelGuideProduct) -> Unit,
    onToggleSaveGuide: (TravelGuideProduct) -> Unit = {},
    onOpenPurchases: () -> Unit
) {
    val stateOptions = remember { listOf("All States") + CatalogData.allIndianStates }
    val daysOptions = remember { listOf("All Days", "2–3 Days", "4–5 Days", "6–7 Days", "8–10 Days", "12+ Days") }

    var selectedState by remember { mutableStateOf("All States") }
    var selectedCities by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedDaysRange by remember { mutableStateOf("All Days") }

    val availableCities = remember(selectedState) { CatalogData.citiesForState(selectedState) }

    val rankedGuides = remember(selectedState, selectedCities, selectedDaysRange) {
        fun matchesDays(days: Int, range: String): Boolean = when (range) {
            "2–3 Days" -> days in 2..3
            "4–5 Days" -> days in 4..5
            "6–7 Days" -> days in 6..7
            "8–10 Days" -> days in 8..10
            "12+ Days" -> days >= 12
            else -> true
        }

        val stateFiltered = if (selectedState == "All States") CatalogData.guides
        else CatalogData.guides.filter { it.stateName.equals(selectedState, ignoreCase = true) }

        val cityFiltered = if (selectedCities.isEmpty()) stateFiltered
        else stateFiltered.filter { g -> selectedCities.any { c -> g.coveredCities.contains(c) } }

        val daysFiltered = cityFiltered.filter { matchesDays(it.days, selectedDaysRange) }

        // If strict filter returns matches, show them ranked by city coverage + days closeness;
        // otherwise keep state-level suggestions so user always sees closest fitting options.
        val result = daysFiltered.ifEmpty { cityFiltered.ifEmpty { stateFiltered } }
        result.sortedByDescending { g ->
            val cityHits = selectedCities.count { c -> g.coveredCities.contains(c) }
            val dayFit = if (matchesDays(g.days, selectedDaysRange)) 2 else 0
            cityHits * 3 + dayFit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding()
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(copy.travelStoreTitle, style = MaterialTheme.typography.headlineMedium, color = Navy, fontWeight = FontWeight.Bold)
                Text("🇮🇳 India · Curated PDF Itineraries", style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = onOpenPurchases) {
                Text("My PDFs", color = Coral, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(10.dp))
        // Cascading Non-Editable Dropdown Filters: State -> Cities -> Days
        DesignCard(
            borderColor = TileIndigoBorder,
            containerColor = TileIndigoBg.copy(alpha = 0.55f),
            contentPadding = PaddingValues(12.dp)
        ) {
            StrictDropdownSelector(
                label = "1. Select State (India)",
                selectedValue = selectedState,
                options = stateOptions,
                onSelect = { newState ->
                    selectedState = newState
                    selectedCities = emptyList()
                },
                testTag = "store_state_dropdown"
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MultiCityDropdownSelector(
                    label = "2. Select Cities",
                    selectedCities = selectedCities,
                    availableCities = availableCities,
                    onToggleCity = { city ->
                        selectedCities = if (selectedCities.contains(city)) {
                            selectedCities - city
                        } else {
                            selectedCities + city
                        }
                    },
                    onClearAll = { selectedCities = emptyList() },
                    modifier = Modifier.weight(1.15f)
                )
                StrictDropdownSelector(
                    label = "3. Days",
                    selectedValue = selectedDaysRange,
                    options = daysOptions,
                    onSelect = { selectedDaysRange = it },
                    modifier = Modifier.weight(0.85f),
                    testTag = "store_days_dropdown"
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "${rankedGuides.size} Best-Fit Guide${if (rankedGuides.size == 1) "" else "s"}",
            style = MaterialTheme.typography.titleSmall,
            color = Navy,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(rankedGuides) { guide ->
                val isBestFit = rankedGuides.firstOrNull()?.id == guide.id &&
                    (selectedState != "All States" || selectedCities.isNotEmpty() || selectedDaysRange != "All Days")
                DesignCard(
                    onClick = { onSelectGuide(guide) },
                    borderColor = if (isBestFit) Coral else CardBorder,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    GradientBannerThumb(
                        colorKey = guide.colorKey,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        cornerRadius = 12.dp
                    ) {
                        Surface(
                            color = if (isBestFit) Coral else SurfaceWhite.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = if (isBestFit) "★ Best Fit" else "${guide.days} Days",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isBestFit) SurfaceWhite else Navy,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        WishlistHeartButton(
                            isSaved = savedItemIds.contains(guide.id),
                            onToggle = { onToggleSaveGuide(guide) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(34.dp),
                            testTag = "wishlist_guide_${guide.id}"
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Navy,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${guide.stateName} · ${guide.coveredCities.take(2).joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatInr(guide.price), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                        Text("★ ${guide.rating}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailsScreen(
    guide: TravelGuideProduct,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    onBuyNow: () -> Unit
) {
    var showPreview by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(cardGradientForKey(guide.colorKey))
                ) {
                    Surface(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBack),
                        shape = CircleShape,
                        color = SurfaceWhite.copy(alpha = 0.9f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(guide.title, style = MaterialTheme.typography.headlineSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Text("${guide.stateName} · ${guide.coveredCities.joinToString(" → ")}", style = MaterialTheme.typography.bodySmall, color = Indigo, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(formatInr(guide.price), style = MaterialTheme.typography.headlineSmall, color = Coral, fontWeight = FontWeight.Bold)
                        Text(guide.languages, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }

                    DesignCard(contentPadding = PaddingValues(14.dp)) {
                        Text("What's included", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        guide.highlights.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                pair.forEach { item ->
                                    Text(
                                        text = "✓ $item",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Green,
                                        modifier = Modifier.weight(1f).padding(vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecBox("${guide.days}", "Days", Modifier.weight(1f))
                        SpecBox(guide.audience.substringBefore("/").trim(), guide.audience, Modifier.weight(1f))
                        SpecBox("${guide.pages}", "Pages", Modifier.weight(1f))
                    }

                    WhiteOutlinedButton(
                        text = "📄 Preview sample PDF",
                        onClick = { showPreview = true },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "preview_sample_pdf_btn"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onToggleSave),
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceWhite,
                            border = BorderStroke(1.5.dp, CardBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Save Guide",
                                    tint = if (isSaved) Coral else Navy
                                )
                            }
                        }
                        CoralPrimaryButton(
                            text = "Buy Now",
                            onClick = onBuyNow,
                            modifier = Modifier.weight(1f),
                            testTag = "guide_buy_now_btn"
                        )
                    }
                }
            }
        }
    }

    if (showPreview) {
        GuideReaderDialog(guide = guide, isSample = true, onDismiss = { showPreview = false })
    }
}

@Composable
private fun SpecBox(value: String, label: String, modifier: Modifier = Modifier) {
    DesignCard(modifier = modifier, contentPadding = PaddingValues(10.dp)) {
        Text(value, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun GuideReaderDialog(
    guide: TravelGuideProduct,
    isSample: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        title = {
            Column {
                Text(if (isSample) "Sample PDF Preview" else guide.title, color = Navy, fontWeight = FontWeight.Bold)
                Text(guide.languages, style = MaterialTheme.typography.labelSmall, color = Coral)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(guide.samplePages) { (heading, body) ->
                    Surface(
                        color = Lavender,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(heading, style = MaterialTheme.typography.labelLarge, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(body, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Coral)
            ) { Text("Close Reader") }
        }
    )
}

@Composable
fun CheckoutHandoffScreen(
    guide: TravelGuideProduct,
    userEmail: String,
    onBack: () -> Unit,
    onProceedToGateway: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Checkout", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GradientBannerThumb(colorKey = guide.colorKey, modifier = Modifier.size(48.dp), cornerRadius = 12.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(guide.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Text("Digital PDF", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Text(formatInr(guide.price), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                }
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("EMAIL FOR DELIVERY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(Modifier.height(4.dp))
                Text(userEmail, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
            }

            DesignCard(contentPadding = PaddingValues(14.dp)) {
                Text("Secure payment on SuperProfile checkout. You will return here after payment.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text("Digital product · Refund policy applies", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }

            CoralPrimaryButton(
                text = "Pay ${formatInr(guide.price)} securely →",
                onClick = onProceedToGateway,
                testTag = "checkout_pay_securely_btn"
            )
        }
    }
}

@Composable
fun SuperProfileCheckoutScreen(
    guide: TravelGuideProduct,
    onBack: () -> Unit,
    onPayNow: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI · GPay · PhonePe") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Text("SuperProfile Checkout", style = MaterialTheme.typography.headlineSmall, color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))

        DesignCard(containerColor = Lavender, contentPadding = PaddingValues(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(guide.title.replace(" Travel Guide", ""), style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                Text(formatInr(guide.price), style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(14.dp))
        listOf("UPI · GPay · PhonePe", "Card · Netbanking").forEach { method ->
            DesignCard(
                onClick = { selectedMethod = method },
                containerColor = if (selectedMethod == method) IndigoSoft else SurfaceWhite,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(method, style = MaterialTheme.typography.bodyMedium, color = Navy, fontWeight = FontWeight.Medium)
                    RadioButton(selected = selectedMethod == method, onClick = { selectedMethod = method })
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(14.dp))
        NavyPrimaryButton(
            text = "Pay Now",
            onClick = onPayNow,
            modifier = Modifier.fillMaxWidth(),
            testTag = "superprofile_pay_now_btn"
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) { Text("Cancel", color = TextSecondary) }
        Spacer(Modifier.height(6.dp))
        Text("Simulated checkout preview", style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
fun VerifyingPaymentScreen(
    onVerified: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500)
        onVerified()
    }

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
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = AmberSoft
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Amber, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Verifying your payment", style = MaterialTheme.typography.headlineSmall, color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Download unlocks only after payment is verified.", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(22.dp))
        WhiteOutlinedButton(
            text = "Check status",
            onClick = onVerified,
            modifier = Modifier.fillMaxWidth(),
            testTag = "check_payment_status_btn"
        )
    }
}

@Composable
fun PurchaseSuccessScreen(
    guide: TravelGuideProduct,
    orderId: String,
    onOpenPurchases: () -> Unit,
    onBackHome: () -> Unit
) {
    var showReader by remember { mutableStateOf(false) }

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
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = GreenSoft
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Green, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Payment successful", style = MaterialTheme.typography.headlineSmall, color = Navy, fontWeight = FontWeight.Bold)
        Text("Order #$orderId", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(18.dp))

        DesignCard(contentPadding = PaddingValues(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GradientBannerThumb(colorKey = guide.colorKey, modifier = Modifier.size(44.dp), cornerRadius = 10.dp)
                Spacer(Modifier.width(12.dp))
                Text(guide.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(18.dp))
        CoralPrimaryButton(
            text = "⬇ Download Guide",
            onClick = { showReader = true },
            testTag = "success_download_guide_btn"
        )
        Spacer(Modifier.height(10.dp))
        WhiteOutlinedButton(
            text = "Open My Purchases",
            onClick = onOpenPurchases,
            modifier = Modifier.fillMaxWidth(),
            testTag = "open_my_purchases_btn"
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBackHome) { Text("Back to Home", color = TextSecondary) }
    }

    if (showReader) {
        GuideReaderDialog(guide = guide, isSample = false, onDismiss = { showReader = false })
    }
}

@Composable
fun MyPurchasesScreen(
    purchases: List<PurchasedGuideEntity>,
    onBack: () -> Unit
) {
    var openedGuide by remember { mutableStateOf<TravelGuideProduct?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "My Purchases", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(purchases) { item ->
                val product = CatalogData.guides.firstOrNull { it.id == item.guideId } ?: CatalogData.guides.first()
                DesignCard(
                    onClick = { openedGuide = product },
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GradientBannerThumb(colorKey = item.colorKey, modifier = Modifier.size(48.dp), cornerRadius = 12.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text("${item.boughtLabel} · #${item.orderId}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Surface(color = CoralSoft, shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "⬇ PDF",
                                style = MaterialTheme.typography.labelSmall,
                                color = Coral,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            item {
                DesignCard(containerColor = SurfaceWhite.copy(alpha = 0.7f), contentPadding = PaddingValues(14.dp)) {
                    Text(
                        text = "🔒 Files are stored offline on your device for instant reading during travel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    openedGuide?.let { g ->
        GuideReaderDialog(guide = g, isSample = false, onDismiss = { openedGuide = null })
    }
}
