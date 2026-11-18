package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppStrings
import com.example.data.CatalogData
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    copy: AppStrings.LocalizedCopy,
    unreadNotificationCount: Int,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenChatAssistant: () -> Unit,
    onPlanMyTrip: () -> Unit,
    onSelectCity: (String) -> Unit,
    onOpenTravelGuides: () -> Unit,
    onOpenTripPlanner: () -> Unit,
    onOpenHotels: () -> Unit,
    onOpenTaxi: () -> Unit,
    onOpenRestaurants: () -> Unit,
    onOpenLocalManager: () -> Unit,
    onOpenActivities: () -> Unit
) {
    val quickCities = listOf("Jaipur", "Udaipur", "Jodhpur", "Jaisalmer", "Goa")
    var selectedQuickCity by remember { mutableStateOf("Jaipur") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TravelGuideLogoMark(size = 30.dp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = copy.appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onOpenChatAssistant)
                        .testTag("home_chat_button"),
                    shape = RoundedCornerShape(20.dp),
                    color = IndigoSoft,
                    border = BorderStroke(1.2.dp, TileIndigoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Travel Assistant", tint = Indigo, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ask Guide", style = MaterialTheme.typography.labelSmall, color = Indigo, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(8.dp))
                BadgedBox(
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge(containerColor = Coral) {
                                Text("$unreadNotificationCount", color = SurfaceWhite, fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onOpenNotifications)
                            .testTag("home_notifications_button"),
                        shape = CircleShape,
                        color = AmberSoft,
                        border = BorderStroke(1.2.dp, TileAmberBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Amber, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onOpenProfile)
                        .testTag("home_profile_button"),
                    shape = CircleShape,
                    color = SurfaceWhite,
                    border = BorderStroke(1.5.dp, CardBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Navy, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Headline
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Navy, fontWeight = FontWeight.Bold)) {
                        append(copy.wherePrefix)
                    }
                    withStyle(SpanStyle(color = Coral, fontWeight = FontWeight.Bold)) {
                        append(copy.whereHighlight)
                    }
                },
                style = MaterialTheme.typography.displaySmall,
                lineHeight = 34.sp
            )
        }

        // Trip Quick Card with crisp visible border & colorful inner chips
        item {
            DesignCard(
                borderColor = Indigo.copy(alpha = 0.28f),
                contentPadding = PaddingValues(14.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenSearch)
                        .testTag("home_search_box"),
                    color = TileIndigoBg,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, TileIndigoBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("DESTINATION", style = MaterialTheme.typography.labelSmall, color = Indigo)
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Coral, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "$selectedQuickCity, Rajasthan",
                                style = MaterialTheme.typography.titleSmall,
                                color = Navy,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Indigo, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenTripPlanner),
                        color = TileAmberBg,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, TileAmberBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("DATES", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("12 – 19 Dec", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenTripPlanner),
                        color = TileGreenBg,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, TileGreenBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("TRAVELLERS", style = MaterialTheme.typography.labelSmall, color = Green)
                            Text("2 Adults", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                CoralPrimaryButton(
                    text = copy.planMyTrip,
                    onClick = onPlanMyTrip,
                    testTag = "home_plan_trip_button"
                )
            }
        }

        // Quick City Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickCities.forEach { city ->
                    FilterPillChip(
                        label = city,
                        selected = city == selectedQuickCity,
                        onClick = {
                            selectedQuickCity = city
                            onSelectCity(city.lowercase())
                        }
                    )
                }
            }
        }

        // Everything for your trip (Colorful bordered 6 Service Tiles)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = copy.everythingForTrip,
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ColorfulServiceTile(
                        title = "Travel Guides",
                        subtitle = "PDF itineraries",
                        icon = Icons.Default.MenuBook,
                        tileBg = TileIndigoBg,
                        tileBorder = TileIndigoBorder,
                        iconBg = Indigo,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_travel_guides",
                        onClick = onOpenTravelGuides
                    )
                    ColorfulServiceTile(
                        title = "Trip Planner",
                        subtitle = "Budget & route",
                        icon = Icons.Default.Explore,
                        tileBg = TileAmberBg,
                        tileBorder = TileAmberBorder,
                        iconBg = Amber,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_trip_planner",
                        onClick = onOpenTripPlanner
                    )
                    ColorfulServiceTile(
                        title = "Hotels",
                        subtitle = "City-wise stays",
                        icon = Icons.Default.Hotel,
                        tileBg = TileCoralBg,
                        tileBorder = TileCoralBorder,
                        iconBg = Coral,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_hotels",
                        onClick = onOpenHotels
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ColorfulServiceTile(
                        title = "Taxi",
                        subtitle = "Verified drivers",
                        icon = Icons.Default.LocalTaxi,
                        tileBg = TileSkyBg,
                        tileBorder = TileSkyBorder,
                        iconBg = SkyBlue,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_taxi",
                        onClick = onOpenTaxi
                    )
                    ColorfulServiceTile(
                        title = "Restaurants",
                        subtitle = "Top rated",
                        icon = Icons.Default.Restaurant,
                        tileBg = TileGreenBg,
                        tileBorder = TileGreenBorder,
                        iconBg = Green,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_restaurants",
                        onClick = onOpenRestaurants
                    )
                    // Coral Gradient Local Manager Tile with visible border & NEW badge
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(114.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(onClick = onOpenLocalManager)
                            .testTag("tile_local_manager"),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFD94A38)),
                        shadowElevation = 3.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CoralCardGradient)
                                .padding(10.dp)
                        ) {
                            Surface(
                                color = Amber,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = "NEW",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Navy,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    text = "Local Manager",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SurfaceWhite,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "On-call expert",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SurfaceWhite.copy(alpha = 0.9f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Verified in Jaipur Section + Activities quick entry
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = copy.verifiedInJaipur,
                        style = MaterialTheme.typography.titleMedium,
                        color = Navy,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Activities →",
                        style = MaterialTheme.typography.labelMedium,
                        color = Coral,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable(onClick = onOpenActivities)
                            .padding(4.dp)
                            .testTag("home_activities_link")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val featuredHotel = CatalogData.hotels.first()
                    DesignCard(
                        modifier = Modifier.weight(1f),
                        borderColor = TileIndigoBorder,
                        contentPadding = PaddingValues(10.dp),
                        onClick = onOpenHotels
                    ) {
                        GradientBannerThumb(
                            colorKey = featuredHotel.colorKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                        ) {
                            VerifiedBadgeChip(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(featuredHotel.name, style = MaterialTheme.typography.labelLarge, color = Navy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${formatInr(featuredHotel.pricePerNight)}/night · ★ ${featuredHotel.rating}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }

                    val featuredGuide = CatalogData.guides.first()
                    DesignCard(
                        modifier = Modifier.weight(1f),
                        borderColor = TileCoralBorder,
                        contentPadding = PaddingValues(10.dp),
                        onClick = onOpenTravelGuides
                    ) {
                        GradientBannerThumb(
                            colorKey = featuredGuide.colorKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                        ) {
                            VerifiedBadgeChip(
                                label = "PDF",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Rajasthan Guide", style = MaterialTheme.typography.labelLarge, color = Navy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${formatInr(featuredGuide.price)} · ${featuredGuide.days} Days", style = MaterialTheme.typography.bodySmall, color = Coral, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorfulServiceTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tileBg: Color,
    tileBorder: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(114.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = tileBg,
        border = BorderStroke(1.5.dp, tileBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = SurfaceWhite, modifier = Modifier.size(19.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Navy,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onSelectCity: (String) -> Unit,
    onSelectGuide: (String) -> Unit,
    onOpenHotels: () -> Unit
) {
    var query by remember { mutableStateOf("Jai") }
    var activeFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Destinations", "Guides", "Hotels")

    val matchedCities = remember(query, activeFilter) {
        if (activeFilter != "All" && activeFilter != "Destinations") emptyList()
        else CatalogData.cities.filter {
            query.isBlank() || it.name.contains(query, ignoreCase = true) || it.tagline.contains(query, ignoreCase = true)
        }
    }
    val matchedGuides = remember(query, activeFilter) {
        if (activeFilter != "All" && activeFilter != "Guides") emptyList()
        else CatalogData.guides.filter {
            query.isBlank() || it.title.contains(query, ignoreCase = true)
        }
    }
    val matchedPlaces = remember(query, activeFilter) {
        if (activeFilter != "All" && activeFilter != "Destinations") emptyList()
        else CatalogData.touristPlaces.filter {
            query.isBlank() || it.name.contains(query, ignoreCase = true)
        }
    }
    val matchedHotels = remember(query, activeFilter) {
        if (activeFilter != "All" && activeFilter != "Hotels") emptyList()
        else CatalogData.hotels.filter {
            query.isBlank() || it.name.contains(query, ignoreCase = true) || it.city.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Search", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search destinations, guides, hotels...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Coral) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = Coral,
                    unfocusedBorderColor = CardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input")
            )

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { f ->
                    FilterPillChip(
                        label = f,
                        selected = activeFilter == f,
                        onClick = { activeFilter = f }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(matchedCities) { city ->
                    SearchResultRow(
                        title = city.name,
                        subtitle = "${city.stateName} · Destination",
                        colorKey = city.colorKey,
                        onClick = { onSelectCity(city.id) }
                    )
                }
                items(matchedGuides) { guide ->
                    SearchResultRow(
                        title = guide.title,
                        subtitle = "PDF · ${formatInr(guide.price)}",
                        colorKey = guide.colorKey,
                        onClick = { onSelectGuide(guide.id) }
                    )
                }
                items(matchedPlaces) { place ->
                    SearchResultRow(
                        title = place.name,
                        subtitle = place.category,
                        colorKey = place.colorKey,
                        onClick = { onSelectCity(place.cityId) }
                    )
                }
                items(matchedHotels) { hotel ->
                    SearchResultRow(
                        title = hotel.name,
                        subtitle = "${hotel.city} · ${formatInr(hotel.pricePerNight)}/night",
                        colorKey = hotel.colorKey,
                        onClick = onOpenHotels
                    )
                }

                item {
                    Spacer(Modifier.height(6.dp))
                    DesignCard(contentPadding = PaddingValues(14.dp)) {
                        Text(
                            text = "No match? Popular nearby",
                            style = MaterialTheme.typography.titleSmall,
                            color = Navy,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Udaipur" to "udaipur", "Pushkar" to "pushkar", "Mount Abu" to "mount_abu").forEach { (name, id) ->
                                FilterPillChip(
                                    label = name,
                                    selected = false,
                                    onClick = { onSelectCity(id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    title: String,
    subtitle: String,
    colorKey: String,
    onClick: () -> Unit
) {
    DesignCard(
        onClick = onClick,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientBannerThumb(
                colorKey = colorKey,
                modifier = Modifier.size(44.dp),
                cornerRadius = 12.dp
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted)
        }
    }
}
