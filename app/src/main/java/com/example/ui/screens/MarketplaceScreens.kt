package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import java.util.Locale
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HotelListingsScreen(
    savedItemIds: Set<String>,
    onBack: () -> Unit,
    onSelectHotel: (HotelItem) -> Unit,
    onToggleSaveHotel: (HotelItem) -> Unit
) {
    val stateOptions = remember { listOf("All States") + CatalogData.allIndianStates }
    var selectedState by remember { mutableStateOf("Rajasthan") }
    val cityOptions = remember(selectedState) { listOf("All Cities") + CatalogData.citiesForState(selectedState) }
    var selectedCity by remember { mutableStateOf("Jaipur") }
    var dateRangeText by remember { mutableStateOf("14 Dec – 16 Dec (3 days)") }
    var nightsCount by remember { mutableIntStateOf(2) }

    val filters = listOf("All", "Budget", "Family", "Luxury", "4.5+")
    var selectedFilter by remember { mutableStateOf("All") }

    val hotels = remember(selectedState, selectedCity, selectedFilter) {
        val byLocation = CatalogData.hotels.filter { h ->
            val stateOk = selectedState == "All States" || h.stateName.equals(selectedState, ignoreCase = true)
            val cityOk = selectedCity == "All Cities" || h.city.equals(selectedCity, ignoreCase = true)
            stateOk && cityOk
        }
        val base = byLocation.ifEmpty {
            CatalogData.hotels.filter { selectedState == "All States" || it.stateName.equals(selectedState, ignoreCase = true) }
        }
        when (selectedFilter) {
            "All" -> base
            "4.5+" -> base.filter { it.rating >= 4.5 }
            else -> base.filter { it.category == selectedFilter }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Verified Hotels", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            DesignCard(
                borderColor = TileCoralBorder,
                containerColor = TileCoralBg.copy(alpha = 0.55f),
                contentPadding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StrictDropdownSelector(
                        label = "State",
                        selectedValue = selectedState,
                        options = stateOptions,
                        onSelect = { newState ->
                            selectedState = newState
                            selectedCity = "All Cities"
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "hotel_state_dropdown"
                    )
                    StrictDropdownSelector(
                        label = "City",
                        selectedValue = selectedCity,
                        options = cityOptions,
                        onSelect = { selectedCity = it },
                        modifier = Modifier.weight(1f),
                        testTag = "hotel_city_dropdown"
                    )
                }
                Spacer(Modifier.height(8.dp))
                DateRangeSelectorField(
                    label = "Check-in & Check-out Dates",
                    dateRangeText = dateRangeText,
                    onDateRangePicked = { text, days ->
                        dateRangeText = text
                        nightsCount = (days - 1).coerceAtLeast(1)
                    },
                    testTag = "hotel_date_selector"
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

        if (hotels.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopCenter) {
                DesignCard(contentPadding = PaddingValues(20.dp)) {
                    Text("No hotels match this exact category in $selectedCity. Try switching to All.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hotels) { hotel ->
                    DesignCard(
                        onClick = { onSelectHotel(hotel) },
                        borderColor = TileCoralBorder,
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        GradientBannerThumb(
                            colorKey = hotel.colorKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            cornerRadius = 14.dp
                        ) {
                            Surface(
                                color = SurfaceWhite.copy(alpha = 0.92f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${hotel.city}, ${hotel.stateName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Navy,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            WishlistHeartButton(
                                isSaved = savedItemIds.contains(hotel.id),
                                onToggle = { onToggleSaveHotel(hotel) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp),
                                testTag = "wishlist_hotel_${hotel.id}"
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(hotel.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text("★ ${hotel.rating}", style = MaterialTheme.typography.labelMedium, color = Navy, fontWeight = FontWeight.SemiBold)
                        }
                        Text(hotel.areaInfo, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${formatInr(hotel.pricePerNight)}/night · ${formatInr(hotel.pricePerNight * nightsCount)} ($nightsCount nights)",
                                style = MaterialTheme.typography.labelLarge,
                                color = Coral,
                                fontWeight = FontWeight.Bold
                            )
                            if (hotel.isVerified) VerifiedBadgeChip()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HotelDetailsScreen(
    hotelId: String,
    reviews: List<ReviewItem>,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: (HotelItem) -> Unit,
    onAddReview: (targetId: String, author: String, comment: String, rating: Int) -> Unit,
    onBookHotel: (HotelItem, HotelRoomOption) -> Unit
) {
    val hotel = CatalogData.hotels.firstOrNull { it.id == hotelId } ?: CatalogData.hotels.first()
    var activeTab by remember { mutableStateOf("Rooms") }
    var selectedRoom by remember { mutableStateOf(hotel.rooms.first()) }
    val hotelReviews = remember(reviews, hotel.id) { reviews.filter { it.targetId == hotel.id } }
    val avgRating = remember(hotelReviews, hotel.rating) { computeAverageRating(hotelReviews, hotel.rating) }
    val avgText = remember(avgRating) { String.format(Locale.ENGLISH, "%.1f", avgRating) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(cardGradientForKey(hotel.colorKey))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onBack)
                        .testTag("hotel_detail_back"),
                    color = SurfaceWhite.copy(alpha = 0.92f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                    }
                }
                WishlistHeartButton(
                    isSaved = isSaved,
                    onToggle = { onToggleSave(hotel) },
                    modifier = Modifier.size(44.dp),
                    testTag = "hotel_save_button"
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Surface(
                    color = SurfaceWhite.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Amber, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$avgText · ${hotelReviews.size} reviews · ${hotel.city}, ${hotel.stateName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = SurfaceWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Rooms", "Facilities", "Policies", "Reviews (${hotelReviews.size})").forEach { tab ->
                        val tabKey = if (tab.startsWith("Reviews")) "Reviews" else tab
                        FilterPillChip(
                            label = tab,
                            selected = activeTab == tabKey,
                            onClick = { activeTab = tabKey }
                        )
                    }
                }
            }

            when (activeTab) {
                "Rooms" -> {
                    items(hotel.rooms) { room ->
                        val isChosen = selectedRoom == room
                        DesignCard(
                            onClick = { selectedRoom = room },
                            containerColor = if (isChosen) CoralSoft else SurfaceWhite,
                            borderColor = if (isChosen) Coral else CardBorder,
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(room.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                    Text(room.details, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Text(formatInr(room.pricePerNight), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    item {
                        DesignCard(contentPadding = PaddingValues(12.dp)) {
                            Text("${hotel.areaInfo} · ${hotel.checkInOut}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                "Facilities" -> {
                    item {
                        DesignCard(contentPadding = PaddingValues(14.dp)) {
                            hotel.facilities.forEach { f ->
                                Text("✓ $f", style = MaterialTheme.typography.bodyMedium, color = Navy, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
                "Policies" -> {
                    item {
                        DesignCard(contentPadding = PaddingValues(14.dp)) {
                            hotel.policies.forEach { p ->
                                Text("• $p", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(vertical = 4.dp))
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(hotel.checkInOut, style = MaterialTheme.typography.bodySmall, color = Indigo, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                else -> {
                    // Reviews tab focused directly on UserReviewsAndRatingsSection below
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                UserReviewsAndRatingsSection(
                    targetId = hotel.id,
                    targetTitle = hotel.name,
                    fallbackRating = hotel.rating,
                    reviews = reviews,
                    onAddReview = onAddReview
                )
            }
        }

        Surface(
            color = SurfaceWhite,
            border = BorderStroke(1.dp, DividerLight),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                CoralPrimaryButton(
                    text = "Book Hotel · ${formatInr(selectedRoom.pricePerNight)}",
                    onClick = { onBookHotel(hotel, selectedRoom) },
                    testTag = "book_hotel_button"
                )
            }
        }
    }
}

@Composable
fun RestaurantsScreen(
    reviews: List<ReviewItem>,
    savedItemIds: Set<String>,
    onBack: () -> Unit,
    onSelectRestaurant: (RestaurantItem) -> Unit,
    onToggleSaveRestaurant: (RestaurantItem) -> Unit
) {
    val stateOptions = remember { listOf("All States") + CatalogData.allIndianStates }
    var selectedState by remember { mutableStateOf("Rajasthan") }
    val cityOptions = remember(selectedState) { listOf("All Cities") + CatalogData.citiesForState(selectedState) }
    var selectedCity by remember { mutableStateOf("Jaipur") }

    val list = remember(selectedState, selectedCity) {
        val matches = CatalogData.restaurants.filter { r ->
            val stateOk = selectedState == "All States" || r.stateName.equals(selectedState, ignoreCase = true)
            val cityOk = selectedCity == "All Cities" || r.city.equals(selectedCity, ignoreCase = true)
            stateOk && cityOk
        }
        matches.ifEmpty {
            CatalogData.restaurants.filter { selectedState == "All States" || it.stateName.equals(selectedState, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Restaurants", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            // Strictly two filters: State -> City as requested
            DesignCard(
                borderColor = TileGreenBorder,
                containerColor = TileGreenBg.copy(alpha = 0.55f),
                contentPadding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StrictDropdownSelector(
                        label = "1. State",
                        selectedValue = selectedState,
                        options = stateOptions,
                        onSelect = { newState ->
                            selectedState = newState
                            selectedCity = "All Cities"
                        },
                        modifier = Modifier.weight(1f),
                        accentColor = Green,
                        testTag = "restaurant_state_dropdown"
                    )
                    StrictDropdownSelector(
                        label = "2. City",
                        selectedValue = selectedCity,
                        options = cityOptions,
                        onSelect = { selectedCity = it },
                        modifier = Modifier.weight(1f),
                        accentColor = Green,
                        testTag = "restaurant_city_dropdown"
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopCenter) {
                DesignCard(contentPadding = PaddingValues(20.dp)) {
                    Text("No verified restaurants listed yet for $selectedState. Try Rajasthan, Kerala, Goa, or Himachal Pradesh.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list) { rest ->
                    val restReviews = reviews.filter { it.targetId == rest.id }
                    val avg = computeAverageRating(restReviews, rest.rating)
                    val avgFormatted = String.format(Locale.ENGLISH, "%.1f", avg)
                    DesignCard(
                        onClick = { onSelectRestaurant(rest) },
                        borderColor = TileGreenBorder,
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.testTag("restaurant_card_${rest.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GradientBannerThumb(colorKey = rest.colorKey, modifier = Modifier.size(62.dp), cornerRadius = 14.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rest.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                Text("${rest.city}, ${rest.stateName}", style = MaterialTheme.typography.labelSmall, color = Indigo)
                                Text("${rest.tags} · ★ $avgFormatted (${restReviews.size})", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (rest.isVerified) VerifiedBadgeChip()
                                    Text("${formatInr(rest.priceForTwo)}/2 · Details →", style = MaterialTheme.typography.labelSmall, color = Coral, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            WishlistHeartButton(
                                isSaved = savedItemIds.contains(rest.id),
                                onToggle = { onToggleSaveRestaurant(rest) },
                                testTag = "wishlist_restaurant_${rest.id}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantDetailsScreen(
    restaurantId: String,
    reviews: List<ReviewItem>,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: (RestaurantItem) -> Unit,
    onAddReview: (targetId: String, author: String, comment: String, rating: Int) -> Unit
) {
    val context = LocalContext.current
    val rest = CatalogData.restaurants.firstOrNull { it.id == restaurantId } ?: CatalogData.restaurants.first()
    val restReviews = remember(reviews, rest.id) { reviews.filter { it.targetId == rest.id } }
    val avgRating = remember(restReviews, rest.rating) { computeAverageRating(restReviews, rest.rating) }
    val avgText = remember(avgRating) { String.format(Locale.ENGLISH, "%.1f", avgRating) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(cardGradientForKey(rest.colorKey))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack)
                        .testTag("restaurant_detail_back"),
                    color = SurfaceWhite.copy(alpha = 0.92f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Navy, modifier = Modifier.size(18.dp))
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onToggleSave(rest) }
                        .testTag("restaurant_save_button"),
                    color = SurfaceWhite.copy(alpha = 0.92f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isSaved) "Remove from Saved" else "Save Restaurant",
                            tint = if (isSaved) Coral else Navy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Surface(
                    color = SurfaceWhite.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Amber, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$avgText · ${restReviews.size} reviews · ${rest.city}, ${rest.stateName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = rest.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = SurfaceWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                DesignCard(
                    borderColor = TileGreenBorder,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(rest.tags, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            if (rest.areaInfo.isNotBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(rest.areaInfo, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                        if (rest.isVerified) VerifiedBadgeChip("Verified")
                    }

                    if (rest.description.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(rest.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = DividerLight)
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("COST FOR TWO", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(formatInr(rest.priceForTwo), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TIMINGS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(rest.timings, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (rest.mustTryDishes.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("MUST-TRY DISHES", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rest.mustTryDishes.forEach { dish ->
                                Surface(
                                    color = TileGreenBg,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, TileGreenBorder)
                                ) {
                                    Text(
                                        text = dish,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Navy,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                WhiteOutlinedButton(
                    text = "Open in Google Maps",
                    onClick = {
                        val query = Uri.encode("${rest.name}, ${rest.city}, ${rest.stateName}")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
                        runCatching { context.startActivity(intent) }
                    },
                    testTag = "restaurant_open_maps"
                )
            }

            item {
                UserReviewsAndRatingsSection(
                    targetId = rest.id,
                    targetTitle = rest.name,
                    fallbackRating = rest.rating,
                    reviews = reviews,
                    onAddReview = onAddReview
                )
            }
        }
    }
}

@Composable
fun ActivitiesScreen(
    onBack: () -> Unit,
    onBookActivity: (ActivityItem) -> Unit
) {
    val filters = listOf("All", "Heritage", "Food", "Adventure")
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedActivity by remember { mutableStateOf(CatalogData.activities.first()) }

    val list = remember(selectedFilter) {
        if (selectedFilter == "All") CatalogData.activities
        else CatalogData.activities.filter { it.category == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Activities", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { f ->
                    FilterPillChip(label = f, selected = selectedFilter == f, onClick = { selectedFilter = f })
                }
            }
            Spacer(Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(list) { item ->
                    val isSelected = selectedActivity.id == item.id
                    DesignCard(
                        onClick = { selectedActivity = item },
                        containerColor = if (isSelected) CoralSoft else SurfaceWhite,
                        borderColor = if (isSelected) Coral else CardBorder,
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        GradientBannerThumb(
                            colorKey = item.colorKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(86.dp),
                            cornerRadius = 12.dp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(item.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${item.city} · ${item.duration}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Text(formatInr(item.price), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            CoralPrimaryButton(
                text = "Book Activity · ${selectedActivity.title}",
                onClick = { onBookActivity(selectedActivity) },
                testTag = "book_activity_button"
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun LocalManagerScreen(
    copy: AppStrings.LocalizedCopy,
    bookedManagers: List<ManagerBookingEntity>,
    onBack: () -> Unit,
    onBookManager: (LocalManagerItem, String, Int) -> Unit,
    onChangeTaxi: () -> Unit,
    onChangeHotel: () -> Unit,
    onReplanRoute: () -> Unit,
    onOpenChatAssistant: () -> Unit
) {
    val context = LocalContext.current
    val stateOptions = remember { listOf("All States") + CatalogData.allIndianStates }
    var selectedState by remember { mutableStateOf("Rajasthan") }
    val cityOptions = remember(selectedState) { listOf("All Cities") + CatalogData.citiesForState(selectedState) }
    var selectedCity by remember { mutableStateOf("Jaipur") }
    var dateRangeText by remember { mutableStateOf("14 Dec – 16 Dec (3 days)") }
    var daysCount by remember { mutableIntStateOf(3) }

    val managers = remember(selectedState, selectedCity) {
        val exact = CatalogData.localManagers.filter { m ->
            val stateOk = selectedState == "All States" || m.stateName.equals(selectedState, ignoreCase = true)
            val cityOk = selectedCity == "All Cities" || m.city.equals(selectedCity, ignoreCase = true)
            stateOk && cityOk
        }
        exact.ifEmpty {
            CatalogData.localManagers.filter { selectedState == "All States" || it.stateName.equals(selectedState, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(
            title = copy.localManagerTitle,
            onBack = onBack,
            trailing = {
                Surface(color = Amber, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "NEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = Navy,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(IndigoBannerGradient)
                        .padding(16.dp)
                ) {
                    Column {
                        Text("On-call expert for your trip", style = MaterialTheme.typography.titleMedium, color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("Select state, city & dates to book a verified manager who plans your whole day & handles local support.", style = MaterialTheme.typography.bodySmall, color = SurfaceWhite.copy(alpha = 0.88f))
                    }
                }
            }

            // State, City & Date Selector Card
            item {
                DesignCard(
                    borderColor = TileCoralBorder,
                    containerColor = TileCoralBg.copy(alpha = 0.55f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StrictDropdownSelector(
                            label = "1. State",
                            selectedValue = selectedState,
                            options = stateOptions,
                            onSelect = { newState ->
                                selectedState = newState
                                selectedCity = "All Cities"
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "manager_state_dropdown"
                        )
                        StrictDropdownSelector(
                            label = "2. City",
                            selectedValue = selectedCity,
                            options = cityOptions,
                            onSelect = { selectedCity = it },
                            modifier = Modifier.weight(1f),
                            testTag = "manager_city_dropdown"
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    DateRangeSelectorField(
                        label = "3. Booking Dates",
                        dateRangeText = dateRangeText,
                        onDateRangePicked = { text, days ->
                            dateRangeText = text
                            daysCount = days
                        },
                        testTag = "manager_date_selector"
                    )
                }
            }

            if (managers.isEmpty()) {
                item {
                    DesignCard(contentPadding = PaddingValues(18.dp)) {
                        Text("No registered managers found for $selectedState yet. Try Rajasthan, Kerala, Goa, or Himachal Pradesh.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            } else {
                items(managers) { mgr ->
                    val booking = bookedManagers.firstOrNull { it.managerId == mgr.id }
                    val totalPrice = mgr.pricePerDay * daysCount.coerceAtLeast(1)

                    DesignCard(
                        borderColor = if (booking != null) Green else TileIndigoBorder,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(IndigoSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = mgr.name, tint = Indigo, modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(mgr.name, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(6.dp))
                                    if (mgr.isVerified) VerifiedBadgeChip("Verified")
                                }
                                Text("${mgr.city}, ${mgr.stateName} · ${mgr.experienceYears} yrs · ★ ${mgr.rating}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Speaks: ${mgr.languages}", style = MaterialTheme.typography.labelSmall, color = Indigo)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatInr(totalPrice), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                                Text("${formatInr(mgr.pricePerDay)}/day", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(mgr.specialty, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.height(12.dp))

                        if (booking != null) {
                            Surface(
                                color = GreenSoft,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Green.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ Booked for ${booking.dateRange} · Direct support unlocked",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Green,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mgr.phoneNumber}"))
                                        runCatching { context.startActivity(intent) }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = SurfaceWhite),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).heightIn(min = 46.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Call Manager")
                                }
                                WhiteOutlinedButton(
                                    text = "WhatsApp",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${mgr.phoneNumber.removePrefix("+")}"))
                                        runCatching { context.startActivity(intent) }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            CoralPrimaryButton(
                                text = "Book ${mgr.name.substringBefore(" ")} · ${formatInr(totalPrice)}",
                                onClick = { onBookManager(mgr, dateRangeText, daysCount) },
                                testTag = "book_manager_${mgr.id}"
                            )
                        }
                    }
                }
            }

            item {
                Text("Quick Support Tools", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
            }
            item { ManagerActionRow("✨", "Ask Travel Assistant Chat", onOpenChatAssistant) }
            item { ManagerActionRow("🚕", "Change taxi / driver", onChangeTaxi) }
            item { ManagerActionRow("🏨", "Change hotel", onChangeHotel) }
            item { ManagerActionRow("🗺", "Re-plan today's route", onReplanRoute) }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CoralSoft,
                    border = BorderStroke(1.dp, Coral.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Emergency? Call local services: 112 / 100 / 108. We are not an emergency service.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Coral,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ManagerActionRow(icon: String, label: String, onClick: () -> Unit) {
    DesignCard(onClick = onClick, contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted)
        }
    }
}

@Composable
fun TravelChatScreen(
    messages: List<ChatMessage>,
    activeRole: String,
    isSending: Boolean,
    onBack: () -> Unit,
    onSwitchRole: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val roles = listOf("Trip Concierge", "Local Food & Heritage Expert", "Taxi & Route Specialist")
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "TravelGuide+ Assistant", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AiNoticeBanner(text = "Sample assistant active · Enable Firebase AI Logic for live Gemini chat.")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                roles.forEach { role ->
                    FilterPillChip(label = role, selected = activeRole == role, onClick = { onSwitchRole(role) })
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (msg.isUser) Navy else SurfaceWhite,
                        border = if (msg.isUser) null else BorderStroke(1.5.dp, CardBorder),
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.roleLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.isUser) Amber else Coral,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (msg.isUser) SurfaceWhite else Navy
                            )
                        }
                    }
                }
            }
        }

        Surface(
            color = SurfaceWhite,
            border = BorderStroke(1.dp, DividerLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask about routes, hotels, food...") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isSending && inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                        .testTag("chat_send_button"),
                    shape = CircleShape,
                    color = Coral
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSending) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SurfaceWhite, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = SurfaceWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewsScreen(
    reviews: List<ReviewItem>,
    onBack: () -> Unit,
    onAddReview: (String, String, Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val avgRating = remember(reviews) { computeAverageRating(reviews, 4.6) }
    val avgFormatted = remember(avgRating) { String.format(Locale.ENGLISH, "%.1f", avgRating) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(
            title = "Reviews & Ratings",
            onBack = onBack,
            trailing = {
                TextButton(onClick = { showAddDialog = true }) {
                    Text("+ Write", color = Coral, fontWeight = FontWeight.Bold)
                }
            }
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DesignCard(
                    containerColor = TileAmberBg.copy(alpha = 0.65f),
                    borderColor = TileAmberBorder,
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(avgFormatted, style = MaterialTheme.typography.displaySmall, color = Navy, fontWeight = FontWeight.Bold)
                        StarRatingRow(rating = avgRating, starSize = 20.dp)
                        Spacer(Modifier.height(4.dp))
                        Text("${reviews.size} verified traveler reviews", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            items(reviews) { rev ->
                DesignCard(contentPadding = PaddingValues(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(rev.author, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            StarRatingRow(rating = rev.rating.toDouble(), starSize = 14.dp)
                        }
                        if (rev.verifiedBooking) VerifiedBadgeChip("Verified booking")
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(rev.comment, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }

    if (showAddDialog) {
        var author by remember { mutableStateOf("Amit Verma") }
        var comment by remember { mutableStateOf("") }
        var stars by remember { mutableIntStateOf(5) }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SurfaceWhite,
            title = { Text("Write a Review", color = Navy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (s in 1..5) {
                            IconButton(onClick = { stars = s }) {
                                Icon(
                                    imageVector = if (s <= stars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$s stars",
                                    tint = if (s <= stars) Amber else TextMuted
                                )
                            }
                        }
                    }
                    OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Your Name") })
                    OutlinedTextField(value = comment, onValueChange = { comment = it }, label = { Text("Experience") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (comment.isNotBlank()) onAddReview(author, comment, stars)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = Navy) }
            }
        )
    }
}

@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    onTicketSubmitted: (String) -> Unit
) {
    val topics = listOf(
        "FAQ" to "Digital PDF guides are available offline in My Purchases immediately after checkout.",
        "Booking help" to "Taxi and hotel bookings include instant operator & host contact details.",
        "Payment help" to "If a payment deducts and stays pending, we verify the transaction before retrying.",
        "Refund request" to "Full taxi refunds apply 24+ hrs before pickup. Contact support with your Order/Booking ID.",
        "Report a partner" to "Share partner name and booking ID so our quality team can audit.",
        "Raise a complaint ticket" to "Submit an issue below and receive a ticket reference.",
        "Contact support" to "Support hours: 7 AM – 11 PM IST daily."
    )
    var activeTopic by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Help & Support", onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(topics) { pair ->
                DesignCard(
                    onClick = { activeTopic = pair },
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(pair.first, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CoralSoft,
                    border = BorderStroke(1.dp, Coral.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "Emergency? Dial 112. Support is not an emergency response service.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Coral,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }

    activeTopic?.let { (title, info) ->
        var ticketNote by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { activeTopic = null },
            containerColor = SurfaceWhite,
            title = { Text(title, color = Navy, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(info, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    OutlinedTextField(
                        value = ticketNote,
                        onValueChange = { ticketNote = it },
                        label = { Text("Optional message or Booking ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeTopic = null
                        onTicketSubmitted("Support ticket submitted for $title")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral)
                ) { Text("Submit Ticket") }
            },
            dismissButton = {
                TextButton(onClick = { activeTopic = null }) { Text("Close", color = Navy) }
            }
        )
    }
}
