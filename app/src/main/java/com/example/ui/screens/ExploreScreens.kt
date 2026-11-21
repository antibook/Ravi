package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    copy: AppStrings.LocalizedCopy,
    onOpenSearch: () -> Unit,
    onOpenCountrySelection: () -> Unit,
    onOpenStateSelection: () -> Unit,
    onSelectState: (String) -> Unit,
    onOpenTravelStore: () -> Unit
) {
    var scopeTab by remember { mutableStateOf("India") }
    var themeFilter by remember { mutableStateOf("All") }
    val themes = listOf("Beaches", "Heritage", "Hills", "Wildlife")

    val displayedStates = remember(themeFilter) {
        if (themeFilter == "All") CatalogData.states.take(4)
        else CatalogData.states.filter { it.themeTags.contains(themeFilter) }
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
            Text(
                text = copy.exploreTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = Navy,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onOpenTravelStore, modifier = Modifier.testTag("explore_store_btn")) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Coral, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Travel Guides", color = Coral, fontWeight = FontWeight.SemiBold)
            }
        }

        // Search box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenSearch)
                .testTag("explore_search_box"),
            color = SurfaceWhite,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Search destination or country", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("India", "International", "Nearby 📍").forEach { tab ->
                FilterPillChip(
                    label = tab,
                    selected = scopeTab == tab,
                    onClick = { scopeTab = tab }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            themes.forEach { t ->
                FilterPillChip(
                    label = t,
                    selected = themeFilter == t,
                    onClick = { themeFilter = if (themeFilter == t) "All" else t }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        if (scopeTab == "International") {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(CatalogData.countries.filter { !it.isIndia }) { country ->
                    GradientStateCard(
                        title = "${country.flag} ${country.name}",
                        subtitle = country.subtitle,
                        colorKey = country.colorKey,
                        onClick = onOpenCountrySelection
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayedStates) { state ->
                    GradientStateCard(
                        title = state.name,
                        subtitle = "${state.guidesCount} guides",
                        colorKey = state.colorKey,
                        onClick = { onSelectState(state.id) }
                    )
                }
                item {
                    Surface(
                        modifier = Modifier
                            .height(132.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(onClick = onOpenStateSelection)
                            .testTag("explore_all_states_card"),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = Indigo)
                            Column {
                                Text("All States", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                Text("28 states →", style = MaterialTheme.typography.bodySmall, color = Coral)
                            }
                        }
                    }
                }
                item {
                    Surface(
                        modifier = Modifier
                            .height(132.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(onClick = onOpenCountrySelection)
                            .testTag("explore_countries_card"),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.Flight, contentDescription = null, tint = Coral)
                            Column {
                                Text("Countries", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                Text("Select country →", style = MaterialTheme.typography.bodySmall, color = Indigo)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GradientStateCard(
    title: String,
    subtitle: String,
    colorKey: String,
    heightDp: Int = 132,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(cardGradientForKey(colorKey))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = SurfaceWhite, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.88f))
        }
    }
}

@Composable
fun CountrySelectionScreen(
    onBack: () -> Unit,
    onSelectIndia: () -> Unit,
    onSelectInternationalGuide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Select Country", onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(CatalogData.countries) { country ->
                GradientStateCard(
                    title = "${country.flag} ${country.name}",
                    subtitle = country.subtitle,
                    colorKey = country.colorKey,
                    heightDp = 118,
                    onClick = {
                        if (country.isIndia) onSelectIndia() else onSelectInternationalGuide()
                    }
                )
            }
        }
    }
}

@Composable
fun StateSelectionScreen(
    onBack: () -> Unit,
    onSelectState: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        CatalogData.states.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "India · Select State", onBack = onBack)
        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search state") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = Coral,
                    unfocusedBorderColor = CardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { state ->
                GradientStateCard(
                    title = state.name,
                    subtitle = state.subtitle,
                    colorKey = state.colorKey,
                    heightDp = 100,
                    onClick = { onSelectState(state.id) }
                )
            }
            item {
                GradientStateCard(
                    title = "More states >",
                    subtitle = "21 more states",
                    colorKey = "indigo",
                    heightDp = 100,
                    onClick = { onSelectState("rajasthan") }
                )
            }
        }
    }
}

@Composable
fun CitySelectionScreen(
    stateId: String,
    onBack: () -> Unit,
    onSelectCity: (String) -> Unit
) {
    val stateName = CatalogData.states.firstOrNull { it.id == stateId }?.name ?: "Rajasthan"
    val cities = CatalogData.cities.filter { it.stateId == stateId }
        .ifEmpty { CatalogData.cities.filter { it.stateId == "rajasthan" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "$stateName · Cities", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cities) { city ->
                DesignCard(
                    onClick = { onSelectCity(city.id) },
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GradientBannerThumb(
                            colorKey = city.colorKey,
                            modifier = Modifier.size(46.dp),
                            cornerRadius = 12.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(city.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text(city.tagline, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Coral)
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationDetailScreen(
    cityId: String,
    isSaved: Boolean,
    isOnline: Boolean = true,
    cachedDestination: CachedDestinationEntity? = null,
    onEnsureCached: (String) -> Unit = {},
    onToggleOfflinePin: (CityItem) -> Unit = {},
    onBack: () -> Unit,
    onToggleSave: (CityItem) -> Unit,
    onOpenPlacesList: (String) -> Unit,
    onViewMap: () -> Unit,
    onOpenItinerary: (CityItem) -> Unit,
    onBuyGuide: (String) -> Unit
) {
    LaunchedEffect(cityId) {
        onEnsureCached(cityId)
    }

    val cachedPair = remember(cachedDestination) { cachedDestination?.toCityAndPlaces() }
    val city = remember(cityId, cachedPair) {
        cachedPair?.first ?: CatalogData.cities.firstOrNull { it.id == cityId } ?: CatalogData.cities.first()
    }
    val places = remember(city.id, cachedPair) {
        val fromCache = cachedPair?.second.orEmpty()
        if (fromCache.isNotEmpty()) fromCache
        else CatalogData.touristPlaces.filter { it.cityId == city.id }
            .ifEmpty { CatalogData.touristPlaces.filter { it.cityId == "jaipur" } }
    }
    var activeTab by remember { mutableStateOf("Overview") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp)
            ) {
                if (city.id == "jaipur") {
                    Image(
                        painter = painterResource(id = R.drawable.img_jaipur_hero),
                        contentDescription = city.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.65f))
                                )
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(cardGradientForKey(city.colorKey))
                    )
                }

                // Back button overlay
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Navy)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurfaceWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${city.stateId.replaceFirstChar { it.uppercase() }}, India · ",
                            style = MaterialTheme.typography.bodySmall,
                            color = SurfaceWhite.copy(alpha = 0.9f)
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleSave(city) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("dest_save_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) Coral else SurfaceWhite,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(if (isSaved) "Saved" else "Save", style = MaterialTheme.typography.bodySmall, color = SurfaceWhite)
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                OfflineCacheStatusBanner(
                    isOnline = isOnline,
                    isCached = cachedDestination != null,
                    cachedAtMillis = cachedDestination?.cachedAt,
                    actionLabel = if (cachedDestination?.isPinnedOffline == true) "Pinned" else "Save Offline",
                    onActionClick = { onToggleOfflinePin(city) },
                    modifier = Modifier.testTag("dest_offline_banner")
                )

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Overview", "Attractions", "Food", "Reach").forEach { tab ->
                        FilterPillChip(
                            label = tab,
                            selected = activeTab == tab,
                            onClick = {
                                if (tab == "Attractions") onOpenPlacesList(city.id)
                                else activeTab = tab
                            }
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    when (activeTab) {
                        "Overview" -> {
                            Text("Best time: ${city.bestTime}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(city.overview, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text("Shopping: ${city.shoppingHighlights}", style = MaterialTheme.typography.bodySmall, color = Indigo)
                        }
                        "Food" -> {
                            Text("Must-Try Food & Cafés", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(city.foodHighlights, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        else -> {
                            Text("How to Reach ${city.name}", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(city.reachInfo, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Famous attractions", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
                    Text(
                        text = "See all →",
                        style = MaterialTheme.typography.labelMedium,
                        color = Coral,
                        modifier = Modifier
                            .clickable { onOpenPlacesList(city.id) }
                            .padding(4.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    places.take(3).forEach { place ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(86.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(cardGradientForKey(place.colorKey))
                                .clickable { onOpenPlacesList(city.id) }
                                .padding(10.dp)
                        ) {
                            Text(
                                text = place.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.BottomStart)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WhiteOutlinedButton(
                        text = "View Map",
                        icon = Icons.Default.Map,
                        onClick = onViewMap,
                        modifier = Modifier.weight(1f),
                        testTag = "dest_view_map_btn"
                    )
                    WhiteOutlinedButton(
                        text = "Itinerary",
                        icon = Icons.Default.EventNote,
                        onClick = { onOpenItinerary(city) },
                        modifier = Modifier.weight(1f),
                        testTag = "dest_itinerary_btn"
                    )
                }

                Spacer(Modifier.height(12.dp))
                CoralPrimaryButton(
                    text = "Buy ${city.name} Guide · ${formatInr(city.guidePrice)}",
                    onClick = { onBuyGuide(city.guideId) },
                    testTag = "dest_buy_guide_btn"
                )
            }
        }
    }
}

@Composable
fun TouristPlacesScreen(
    cityId: String,
    savedIds: Set<String>,
    isOnline: Boolean = true,
    cachedDestination: CachedDestinationEntity? = null,
    onEnsureCached: (String) -> Unit = {},
    onBack: () -> Unit,
    onToggleSavePlace: (TouristPlace) -> Unit
) {
    LaunchedEffect(cityId) {
        onEnsureCached(cityId)
    }

    val cachedPair = remember(cachedDestination) { cachedDestination?.toCityAndPlaces() }
    val cityName = cachedPair?.first?.name
        ?: CatalogData.cities.firstOrNull { it.id == cityId }?.name
        ?: "Jaipur"
    val places = remember(cityId, cachedPair) {
        val fromCache = cachedPair?.second.orEmpty()
        if (fromCache.isNotEmpty()) fromCache
        else CatalogData.touristPlaces.filter { it.cityId == cityId }
            .ifEmpty { CatalogData.touristPlaces.filter { it.cityId == "jaipur" } }
    }
    var selectedPlace by remember { mutableStateOf<TouristPlace?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "$cityName · Places", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OfflineCacheStatusBanner(
                    isOnline = isOnline,
                    isCached = cachedDestination != null,
                    cachedAtMillis = cachedDestination?.cachedAt
                )
            }
            items(places) { place ->
                val isFav = savedIds.contains(place.id)
                DesignCard(
                    onClick = { selectedPlace = place },
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GradientBannerThumb(
                            colorKey = place.colorKey,
                            modifier = Modifier.size(56.dp),
                            cornerRadius = 14.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(place.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text(place.category, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("⏱ ${place.duration} · Entry ${place.entryFee}", style = MaterialTheme.typography.labelSmall, color = Indigo)
                        }
                        IconButton(onClick = { onToggleSavePlace(place) }) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Save Place",
                                tint = if (isFav) Coral else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }

    selectedPlace?.let { place ->
        AlertDialog(
            onDismissRequest = { selectedPlace = null },
            containerColor = SurfaceWhite,
            title = { Text(place.name, color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${place.category} · ${place.area}", style = MaterialTheme.typography.labelMedium, color = Coral)
                    Text(place.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("Recommended time: ${place.duration} · Entry: ${place.entryFee}", style = MaterialTheme.typography.bodySmall, color = Navy)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPlace = null }) { Text("Close", color = Coral) }
            }
        )
    }
}
