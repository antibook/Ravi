package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun MyTripsScreen(
    trips: List<SavedTripEntity>,
    copy: AppStrings.LocalizedCopy,
    savedItemIds: Set<String> = emptySet(),
    onPlanNewTrip: () -> Unit,
    onOpenItinerary: (SavedTripEntity) -> Unit,
    onToggleFavoriteTrip: (SavedTripEntity) -> Unit = {},
    onEditTrip: () -> Unit,
    onDeleteTrip: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Upcoming") }
    val filtered = remember(trips, selectedTab) {
        trips.filter { it.status.equals(selectedTab, ignoreCase = true) }
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
            Text(copy.myTripsTitle, style = MaterialTheme.typography.headlineMedium, color = Navy, fontWeight = FontWeight.Bold)
            TextButton(onClick = onPlanNewTrip, modifier = Modifier.testTag("trips_plan_new_btn")) {
                Text("+ Plan Trip", color = Coral, fontWeight = FontWeight.Bold)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Upcoming", "Saved", "Completed").forEach { tab ->
                FilterPillChip(label = tab, selected = selectedTab == tab, onClick = { selectedTab = tab })
            }
        }

        Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) {
            DesignCard(contentPadding = PaddingValues(24.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = Indigo, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("No $selectedTab trips yet", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Create a day-wise trip itinerary in seconds.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(14.dp))
                    CoralPrimaryButton(text = "Plan a Trip", onClick = onPlanNewTrip)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered) { trip ->
                    DesignCard(contentPadding = PaddingValues(12.dp)) {
                        GradientBannerThumb(
                            colorKey = trip.colorKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp),
                            cornerRadius = 12.dp
                        ) {
                            Text(
                                text = trip.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            )
                            WishlistHeartButton(
                                isSaved = savedItemIds.contains(trip.id),
                                onToggle = { onToggleFavoriteTrip(trip) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp),
                                testTag = "wishlist_trip_${trip.id}"
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${trip.dateRange} · ${trip.adults + trip.children} travellers", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(formatInr(trip.totalBudget), style = MaterialTheme.typography.titleSmall, color = Coral, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WhiteOutlinedButton(
                                text = "Itinerary",
                                onClick = { onOpenItinerary(trip) },
                                modifier = Modifier.weight(1f)
                            )
                            NavyPrimaryButton(
                                text = if (trip.isDraft) "Continue" else "Edit Trip",
                                onClick = onEditTrip,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onDeleteTrip(trip.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedItemsScreen(
    items: List<SavedItemEntity>,
    copy: AppStrings.LocalizedCopy,
    onOpenSavedItem: (SavedItemEntity) -> Unit,
    onRemoveSaved: (SavedItemEntity) -> Unit,
    onExploreHotels: () -> Unit = {},
    onExploreRestaurants: () -> Unit = {},
    onPlanTrip: () -> Unit = {}
) {
    val tabs = listOf("All", "Hotels", "Restaurants", "Itineraries", "Destinations", "Guides")
    var selectedTab by remember { mutableStateOf("All") }
    val filtered = remember(items, selectedTab) {
        if (selectedTab == "All") items
        else items.filter { it.type.equals(selectedTab, ignoreCase = true) }
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
            Text(copy.savedItemsTitle, style = MaterialTheme.typography.headlineMedium, color = Navy, fontWeight = FontWeight.Bold)
            Surface(
                color = CoralSoft,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Coral.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = Coral, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${items.size}", style = MaterialTheme.typography.labelMedium, color = Coral, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                val count = if (tab == "All") items.size else items.count { it.type.equals(tab, ignoreCase = true) }
                val label = if (count > 0 && tab != "All") "$tab ($count)" else tab
                FilterPillChip(
                    label = label,
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) {
            DesignCard(contentPadding = PaddingValues(20.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Coral, modifier = Modifier.size(38.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (selectedTab == "All") "Your Wishlist is empty" else "No saved $selectedTab yet",
                        style = MaterialTheme.typography.titleSmall,
                        color = Navy,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tap the heart icon on any hotel, restaurant, or itinerary to save it here for quick access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WhiteOutlinedButton(text = "Hotels", onClick = onExploreHotels, modifier = Modifier.weight(1f))
                        WhiteOutlinedButton(text = "Restaurants", onClick = onExploreRestaurants, modifier = Modifier.weight(1f))
                        WhiteOutlinedButton(text = "Trips", onClick = onPlanTrip, modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered, key = { it.itemId }) { item ->
                    val badgeColors = when (item.type) {
                        "Hotels" -> TileCoralBg to Coral
                        "Restaurants" -> TileGreenBg to Green
                        "Itineraries" -> TileAmberBg to Amber
                        "Guides" -> TileIndigoBg to Indigo
                        else -> TileSkyBg to SkyBlue
                    }
                    DesignCard(
                        onClick = { onOpenSavedItem(item) },
                        borderColor = CardBorder,
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.testTag("saved_card_${item.itemId}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GradientBannerThumb(colorKey = item.colorKey, modifier = Modifier.size(52.dp), cornerRadius = 12.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = badgeColors.first,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.type.removeSuffix("s").let {
                                            if (item.type == "Itineraries") "Itinerary" else it
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = badgeColors.second,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(Modifier.height(3.dp))
                                Text(item.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            IconButton(
                                onClick = { onRemoveSaved(item) },
                                modifier = Modifier.testTag("remove_saved_${item.itemId}")
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = "Remove from Wishlist", tint = Coral)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onBack: () -> Unit,
    onNotificationClick: (NotificationEntity) -> Unit
) {
    val filters = listOf("All", "Purchases", "Reminders", "Offers")
    var selectedFilter by remember { mutableStateOf("All") }
    val list = remember(notifications, selectedFilter) {
        if (selectedFilter == "All") notifications
        else notifications.filter { it.category.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "Notifications", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { f ->
                    FilterPillChip(label = f, selected = selectedFilter == f, onClick = { selectedFilter = f })
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(list) { notif ->
                DesignCard(
                    onClick = { onNotificationClick(notif) },
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (icon, bg, tint) = when (notif.category) {
                            "Purchases" -> Triple(Icons.Default.Check, GreenSoft, Green)
                            "Offers" -> Triple(Icons.Default.LocalOffer, CoralSoft, Coral)
                            else -> Triple(Icons.Default.Schedule, AmberSoft, Amber)
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(bg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                            Text(notif.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        if (!notif.isRead) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Coral))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    session: UserSession,
    language: AppLanguage,
    notificationsEnabled: Boolean,
    copy: AppStrings.LocalizedCopy,
    onOpenPurchases: () -> Unit,
    onOpenTrips: () -> Unit,
    onOpenSaved: () -> Unit,
    onToggleLanguage: (AppLanguage) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onOpenHelp: () -> Unit,
    onOpenDesignSystem: () -> Unit,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showPrivacy by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            DesignCard(contentPadding = PaddingValues(18.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Lavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = "Avatar", tint = Navy, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (session.isLoggedIn) session.name else "Guest Explorer",
                        style = MaterialTheme.typography.titleMedium,
                        color = Navy,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (session.isLoggedIn) "${session.email} · ${session.phone}" else "Tap Sign In to sync purchases & saved trips",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    if (!session.isLoggedIn) {
                        Spacer(Modifier.height(10.dp))
                        CoralPrimaryButton(text = "Sign In / Create Account", onClick = onLoginClick)
                    }
                }
            }
        }

        item {
            ProfileMenuRow("🛍", "My Purchases", onOpenPurchases)
        }
        item {
            ProfileMenuRow("✈", "My Trips", onOpenTrips)
        }
        item {
            ProfileMenuRow("♡", "Saved Items", onOpenSaved)
        }
        item {
            DesignCard(
                onClick = {
                    val next = if (language == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
                    onToggleLanguage(next)
                },
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_language_toggle"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌐", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Language: ${language.label} (Tap to switch)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Navy,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (language == AppLanguage.HINDI) "EN" else "हिं",
                        style = MaterialTheme.typography.labelLarge,
                        color = Coral,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item {
            DesignCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔔", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(12.dp))
                    Text("Notification Settings", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = onToggleNotifications,
                        colors = SwitchDefaults.colors(checkedThumbColor = SurfaceWhite, checkedTrackColor = Coral)
                    )
                }
            }
        }
        item {
            ProfileMenuRow("❓", "Help & Support", onOpenHelp)
        }
        item {
            ProfileMenuRow("📄", "Privacy & Terms") { showPrivacy = true }
        }
        item {
            ProfileMenuRow("🎨", "UI States & Design System", onOpenDesignSystem)
        }
        item {
            DesignCard(onClick = { showDeleteConfirm = true }, contentPadding = PaddingValues(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Delete Account", style = MaterialTheme.typography.titleSmall, color = DangerRed, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (session.isLoggedIn) {
            item {
                Spacer(Modifier.height(4.dp))
                WhiteOutlinedButton(
                    text = "Logout",
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "profile_logout_btn"
                )
            }
        }
    }

    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            containerColor = SurfaceWhite,
            title = { Text("Privacy & Terms", color = Navy) },
            text = { Text("Your saved trips, bookmarks, and downloaded guides are stored locally on your device. Emergency disclaimers apply to local manager and support services.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { showPrivacy = false }) { Text("Close", color = Coral) } }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SurfaceWhite,
            title = { Text("Delete Local Account Data?", color = DangerRed) },
            text = { Text("This clears all saved trips, purchases, and bookings stored on this device.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = { showDeleteConfirm = false; onDeleteAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel", color = Navy) }
            }
        )
    }
}

@Composable
private fun ProfileMenuRow(icon: String, label: String, onClick: () -> Unit) {
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
fun DesignSystemStatesScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Lavender)
    ) {
        TravelTopBar(title = "UI States & Design System", onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("UI States", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
            }
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Lavender)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Loading · skeleton cards", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = TextSecondary)
                        Text("No internet", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Text("Showing saved content", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        CoralPrimaryButton(text = "Retry", onClick = {})
                    }
                }
            }
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = Indigo)
                        Text("No results", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Text("Try nearby popular places", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
            item {
                DesignCard(contentPadding = PaddingValues(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Amber)
                        Text("Payment failed", style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.Bold)
                        Text("We check the old order before charging again.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            item {
                Spacer(Modifier.height(6.dp))
                Text("Design System Palette", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorSwatch("Navy\n#282A6B", Navy, Modifier.weight(1f))
                    ColorSwatch("Coral\n#F4634F", Coral, Modifier.weight(1f))
                    ColorSwatch("Indigo\n#4F46C8", Indigo, Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorSwatch("Green\n#17A673", Green, Modifier.weight(1f))
                    ColorSwatch("Lavender\n#EEF0F8", Lavender, Modifier.weight(1f), darkText = true)
                    ColorSwatch("Amber\n#F7B13B", Amber, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(label: String, color: Color, modifier: Modifier = Modifier, darkText: Boolean = false) {
    Surface(
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(14.dp),
        color = color,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (darkText) Navy else SurfaceWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
