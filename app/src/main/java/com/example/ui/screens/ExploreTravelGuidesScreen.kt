package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Elderly
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.TempleHindu
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CatalogData
import com.example.data.SmartTripCatalog
import com.example.data.TravelGuideProduct
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Reference UI Palette
private val ScreenBg = Color(0xFFF4F7FB)
private val CardWhite = Color(0xFFFFFFFF)
private val DeepNavyText = Color(0xFF0F1E46)
private val SubtitleSlate = Color(0xFF53637E)
private val MutedHint = Color(0xFF94A3B8)
private val CardOutline = Color(0xFFE5ECF5)
private val FieldOutline = Color(0xFFD5DFEC)

private val VibrantOrange = Color(0xFFF35D06)
private val VibrantOrangeLight = Color(0xFFFF7A18)
private val PeachBadgeBg = Color(0xFFFFEFE3)
private val PeachChipBg = Color(0xFFFFF5EC)

private val ChipIceBlueBg = Color(0xFFE8F2FF)
private val ChipBlueAccent = Color(0xFF2563EB)
private val ReservedBoxBg = Color(0xFFEBF1F7)

private data class GuideFilterState(
    val selectedState: String = "Rajasthan",
    val selectedCities: Set<String> = setOf("Jaipur", "Udaipur"),
    val selectedDuration: String? = "3 – 5 Days",
    val startDateMillis: Long? = 1765497600000L, // 12 Dec 2025
    val selectedCategories: Set<String> = setOf("Solo", "Budget")
)

private val durationOptions = listOf(
    "All Durations",
    "1–2 days",
    "3 – 5 Days",
    "6–7 days",
    "8–10 days",
    "10+ days"
)

private data class CategoryOptionItem(
    val name: String,
    val icon: ImageVector
)

private val categoryOptionItems = listOf(
    CategoryOptionItem("Family", Icons.Outlined.Groups),
    CategoryOptionItem("Couple", Icons.Outlined.FavoriteBorder),
    CategoryOptionItem("Honeymoon", Icons.Outlined.AllInclusive),
    CategoryOptionItem("Friends", Icons.Outlined.Group),
    CategoryOptionItem("Solo", Icons.Outlined.PersonOutline),
    CategoryOptionItem("Senior Citizens", Icons.Outlined.Elderly),
    CategoryOptionItem("Adventure", Icons.Outlined.Landscape),
    CategoryOptionItem("Religious", Icons.Outlined.TempleHindu),
    CategoryOptionItem("Luxury", Icons.Outlined.Diamond),
    CategoryOptionItem("Budget", Icons.Outlined.AccountBalanceWallet)
)

private val sortOptions = listOf(
    "Recommended",
    "Duration: Short to Long",
    "Duration: Long to Short",
    "Top Rated"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExploreTravelGuidesScreen(
    savedItemIds: Set<String> = emptySet(),
    onBack: () -> Unit,
    onOpenSavedShortlist: () -> Unit = {}
) {
    val stateCityMap = remember { SmartTripCatalog.allStatesWithCities }
    val allStates = remember(stateCityMap) { listOf("All States") + stateCityMap.keys.toList() }

    var draftFilter by remember { mutableStateOf(GuideFilterState()) }
    var appliedFilter by remember { mutableStateOf(GuideFilterState()) }

    var destinationExpanded by remember { mutableStateOf(true) }
    var durationExpanded by remember { mutableStateOf(true) }
    var categoryExpanded by remember { mutableStateOf(true) }

    var showStateDropdownDialog by remember { mutableStateOf(false) }
    var showCityDropdownDialog by remember { mutableStateOf(false) }
    var showDurationDropdownDialog by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }

    var categorySearchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("Recommended") }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var shortlistOnly by remember { mutableStateOf(false) }

    val savedGuideIds = remember(savedItemIds) {
        CatalogData.guides.map { it.id }.filter { savedItemIds.contains(it) }.toSet()
    }

    val matchingGuidesCount = remember(appliedFilter, shortlistOnly, savedGuideIds) {
        CatalogData.guides.count { guide ->
            val matchesSaved = !shortlistOnly || savedGuideIds.contains(guide.id)
            val matchesState = appliedFilter.selectedState == "All States" ||
                guide.stateName.equals(appliedFilter.selectedState, ignoreCase = true)
            val matchesCities = appliedFilter.selectedCities.isEmpty() ||
                appliedFilter.selectedCities.any { city ->
                    guide.coveredCities.any { it.equals(city, ignoreCase = true) }
                }
            val matchesDuration = when (appliedFilter.selectedDuration) {
                "1–2 days" -> guide.days in 1..2
                "3 – 5 Days" -> guide.days in 3..5
                "6–7 days" -> guide.days in 6..7
                "8–10 days" -> guide.days in 8..10
                "10+ days" -> guide.days >= 10
                else -> true
            }
            val matchesCategories = appliedFilter.selectedCategories.isEmpty() ||
                appliedFilter.selectedCategories.any { cat -> guideMatchesCategory(guide, cat) }

            matchesSaved && matchesState && matchesCities && matchesDuration && matchesCategories
        }
    }

    val hasUnappliedChanges = draftFilter != appliedFilter
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // Atmospheric top-right Taj Mahal & Airplane header illustration
        TajHeaderArtwork(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("explore_guides_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepNavyText
                        )
                    }
                    Text(
                        text = "Explore Travel Guides",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = DeepNavyText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = "Find ready itineraries & travel guides for your next trip",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = SubtitleSlate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 48.dp, end = 16.dp)
                )
            }

            // Scrollable Cards + Apply/Reset + Results Bar + Reserved Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. DESTINATION CARD
                item {
                    PlannerFilterSectionCard(
                        icon = Icons.Outlined.LocationOn,
                        title = "Destination",
                        subtitle = "Select State and one or more cities",
                        expanded = destinationExpanded,
                        onToggleExpand = { destinationExpanded = !destinationExpanded },
                        testTag = "dropdown_destination"
                    ) {
                        Text(
                            text = "Select State",
                            style = MaterialTheme.typography.labelMedium,
                            color = DeepNavyText,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showStateDropdownDialog = true }
                                .testTag("destination_state_field"),
                            shape = RoundedCornerShape(12.dp),
                            color = CardWhite,
                            border = BorderStroke(1.dp, FieldOutline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = draftFilter.selectedState,
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                                    color = DeepNavyText,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Choose State",
                                    tint = SubtitleSlate,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Select City (Multiple)",
                            style = MaterialTheme.typography.labelMedium,
                            color = DeepNavyText,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showCityDropdownDialog = true }
                                .testTag("destination_cities_field"),
                            shape = RoundedCornerShape(12.dp),
                            color = CardWhite,
                            border = BorderStroke(1.dp, FieldOutline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (draftFilter.selectedCities.isEmpty()) {
                                    Text(
                                        text = "Select one or more cities",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MutedHint,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 4.dp, vertical = 4.dp)
                                    )
                                } else {
                                    FlowRow(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        draftFilter.selectedCities.forEach { cityName ->
                                            Surface(
                                                color = ChipIceBlueBg,
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier.clip(RoundedCornerShape(50))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        start = 11.dp,
                                                        end = 8.dp,
                                                        top = 5.dp,
                                                        bottom = 5.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = cityName,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = DeepNavyText,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Spacer(Modifier.width(5.dp))
                                                    Icon(
                                                        imageVector = Icons.Outlined.Close,
                                                        contentDescription = "Remove $cityName",
                                                        tint = ChipBlueAccent,
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clickable {
                                                                draftFilter = draftFilter.copy(
                                                                    selectedCities = draftFilter.selectedCities - cityName
                                                                )
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Select Cities",
                                    tint = SubtitleSlate,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // 2. DURATION & TRAVEL DATE CARD
                item {
                    PlannerFilterSectionCard(
                        icon = Icons.Outlined.DateRange,
                        title = "Duration & Travel Date",
                        subtitle = "Choose trip duration and starting date",
                        expanded = durationExpanded,
                        onToggleExpand = { durationExpanded = !durationExpanded },
                        testTag = "dropdown_duration_date"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(0.95f)) {
                                Text(
                                    text = "Trip Duration",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DeepNavyText,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(6.dp))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showDurationDropdownDialog = true }
                                        .testTag("duration_selector_field"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = CardWhite,
                                    border = BorderStroke(1.dp, FieldOutline)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 11.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = draftFilter.selectedDuration ?: "Any",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (draftFilter.selectedDuration != null) DeepNavyText else MutedHint,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = "Select duration",
                                            tint = SubtitleSlate,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1.05f)) {
                                Text(
                                    text = "Starting Date",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DeepNavyText,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(6.dp))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showStartDatePicker = true }
                                        .testTag("duration_start_date_picker"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = CardWhite,
                                    border = BorderStroke(1.dp, FieldOutline)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 11.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DateRange,
                                            contentDescription = null,
                                            tint = VibrantOrange,
                                            modifier = Modifier.size(17.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = draftFilter.startDateMillis?.let { dateFormatter.format(Date(it)) }
                                                ?: "Select date",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (draftFilter.startDateMillis != null) DeepNavyText else MutedHint,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = "Pick starting date",
                                            tint = SubtitleSlate,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. TRAVEL CATEGORY CARD (Search bar + 3-column grid with orange check badges)
                item {
                    PlannerFilterSectionCard(
                        icon = Icons.Outlined.Layers,
                        title = "Travel Category",
                        subtitle = "Select one or more travel categories",
                        expanded = categoryExpanded,
                        onToggleExpand = { categoryExpanded = !categoryExpanded },
                        testTag = "dropdown_travel_category"
                    ) {
                        DropdownSearchBox(
                            query = categorySearchQuery,
                            onQueryChange = { categorySearchQuery = it },
                            placeholder = "Search categories",
                            testTag = "search_category_input"
                        )

                        Spacer(Modifier.height(12.dp))

                        val filteredCategories = remember(categorySearchQuery) {
                            val q = categorySearchQuery.trim()
                            if (q.isBlank()) categoryOptionItems
                            else categoryOptionItems.filter { it.name.contains(q, ignoreCase = true) }
                        }

                        if (filteredCategories.isEmpty()) {
                            Text(
                                text = "No matching travel categories",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedHint,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            val rows = filteredCategories.chunked(3)
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                rows.forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowItems.forEach { item ->
                                            val isSelected = draftFilter.selectedCategories.contains(item.name)
                                            CategoryGridTile(
                                                label = item.name,
                                                icon = item.icon,
                                                selected = isSelected,
                                                onClick = {
                                                    val next = if (isSelected) {
                                                        draftFilter.selectedCategories - item.name
                                                    } else {
                                                        draftFilter.selectedCategories + item.name
                                                    }
                                                    draftFilter = draftFilter.copy(selectedCategories = next)
                                                },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        repeat(3 - rowItems.size) {
                                            Spacer(Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // PROMINENT ORANGE "Apply Filters" BUTTON + "Reset All"
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { appliedFilter = draftFilter },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = CardWhite
                            ),
                            contentPadding = PaddingValues(0.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(VibrantOrangeLight, VibrantOrange)
                                    )
                                )
                                .testTag("apply_filters_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = null,
                                    tint = CardWhite,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (hasUnappliedChanges) "Apply Filters •" else "Apply Filters",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = CardWhite
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val cleared = GuideFilterState(
                                        selectedState = "All States",
                                        selectedCities = emptySet(),
                                        selectedDuration = null,
                                        startDateMillis = null,
                                        selectedCategories = emptySet()
                                    )
                                    draftFilter = cleared
                                    appliedFilter = cleared
                                    categorySearchQuery = ""
                                    shortlistOnly = false
                                    selectedSort = "Recommended"
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("reset_all_filters_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Reset All",
                                tint = VibrantOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Reset All",
                                style = MaterialTheme.typography.labelLarge,
                                color = VibrantOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // RESULTS COUNT, SORTING & SAVED CONTROLS + RESERVED PLACEHOLDER SECTION
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                    ) {
                        HorizontalDivider(thickness = 1.dp, color = CardOutline)
                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$matchingGuidesCount ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                                    color = DeepNavyText,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("guides_results_count")
                                )
                                Text(
                                    text = if (shortlistOnly) "Saved Guides" else "Travel Guides Found",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                    color = SubtitleSlate
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box {
                                    Surface(
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { sortMenuExpanded = true }
                                            .testTag("guides_sort_button"),
                                        color = CardWhite,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, FieldOutline)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.SwapVert,
                                                contentDescription = "Sort by",
                                                tint = DeepNavyText,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            val sortText = when (selectedSort) {
                                                "Duration: Short to Long" -> "Short"
                                                "Duration: Long to Short" -> "Long"
                                                "Top Rated" -> "Top"
                                                else -> "Sort by"
                                            }
                                            Text(
                                                text = sortText,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = DeepNavyText,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Icon(
                                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = SubtitleSlate,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = sortMenuExpanded,
                                        onDismissRequest = { sortMenuExpanded = false },
                                        modifier = Modifier.background(CardWhite)
                                    ) {
                                        sortOptions.forEach { option ->
                                            val active = selectedSort == option
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = option,
                                                            color = if (active) VibrantOrange else DeepNavyText,
                                                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        if (active) {
                                                            Spacer(Modifier.width(8.dp))
                                                            Icon(
                                                                imageVector = Icons.Filled.Check,
                                                                contentDescription = null,
                                                                tint = VibrantOrange,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedSort = option
                                                    sortMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { shortlistOnly = !shortlistOnly }
                                        .testTag("guides_shortlist_toggle"),
                                    color = if (shortlistOnly) PeachChipBg else CardWhite,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (shortlistOnly) VibrantOrange else FieldOutline
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (shortlistOnly) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Saved shortlist",
                                            tint = if (shortlistOnly) VibrantOrange else DeepNavyText,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = if (savedGuideIds.isEmpty()) "Saved" else "Saved (${savedGuideIds.size})",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (shortlistOnly) VibrantOrange else DeepNavyText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Clean Reserved Lower Area for Future Travel Guide Listings (NO Product Cards)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 108.dp)
                                .testTag("reserved_listings_container"),
                            shape = RoundedCornerShape(14.dp),
                            color = ReservedBoxBg,
                            border = BorderStroke(1.dp, CardOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Reserved for Travel Guide Listings",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = DeepNavyText,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                val summaryText = buildString {
                                    append("$matchingGuidesCount matching guides")
                                    if (appliedFilter.selectedState != "All States") append(" · ${appliedFilter.selectedState}")
                                    if (appliedFilter.selectedCities.isNotEmpty()) append(" (${appliedFilter.selectedCities.joinToString(", ")})")
                                    appliedFilter.selectedDuration?.let { append(" · $it") }
                                    append(" · Sorted by $selectedSort")
                                }
                                Text(
                                    text = summaryText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SubtitleSlate
                                )
                                if (savedGuideIds.isNotEmpty()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Open Shortlist (${savedGuideIds.size}) →",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = VibrantOrange,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable(onClick = onOpenSavedShortlist)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Searchable State Selector Modal
    if (showStateDropdownDialog) {
        var stateQuery by remember { mutableStateOf("") }
        val filteredStates = remember(allStates, stateCityMap, stateQuery) {
            val q = stateQuery.trim()
            if (q.isBlank()) allStates
            else allStates.filter { st ->
                st.contains(q, ignoreCase = true) ||
                    (stateCityMap[st]?.any { it.contains(q, ignoreCase = true) } == true)
            }
        }
        AlertDialog(
            onDismissRequest = { showStateDropdownDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "Select State",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepNavyText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropdownSearchBox(
                        query = stateQuery,
                        onQueryChange = { stateQuery = it },
                        placeholder = "Search state or city...",
                        testTag = "search_state_dialog_input"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 290.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        filteredStates.forEach { stateName ->
                            val isSelected = draftFilter.selectedState == stateName
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PeachChipBg else Color.Transparent)
                                    .clickable {
                                        val nextCities = if (stateName == "All States") {
                                            emptySet()
                                        } else {
                                            val validSet = stateCityMap[stateName].orEmpty().toSet()
                                            draftFilter.selectedCities.filter { validSet.contains(it) }.toSet()
                                        }
                                        draftFilter = draftFilter.copy(
                                            selectedState = stateName,
                                            selectedCities = nextCities
                                        )
                                        showStateDropdownDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stateName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) VibrantOrange else DeepNavyText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = VibrantOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStateDropdownDialog = false }) {
                    Text("Close", color = DeepNavyText)
                }
            }
        )
    }

    // Searchable Multi-City Selector Modal (relevant to Selected State)
    if (showCityDropdownDialog) {
        var cityQuery by remember { mutableStateOf("") }
        val cityPairs = remember(draftFilter.selectedState, stateCityMap, cityQuery) {
            val raw = if (draftFilter.selectedState == "All States") {
                stateCityMap.entries.flatMap { (st, cities) -> cities.map { c -> c to st } }
            } else {
                stateCityMap[draftFilter.selectedState].orEmpty().map { it to draftFilter.selectedState }
            }
            val q = cityQuery.trim()
            if (q.isBlank()) raw else raw.filter { (city, st) ->
                city.contains(q, ignoreCase = true) || st.contains(q, ignoreCase = true)
            }
        }
        AlertDialog(
            onDismissRequest = { showCityDropdownDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = if (draftFilter.selectedState == "All States") "Select Cities"
                    else "Cities in ${draftFilter.selectedState}",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepNavyText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropdownSearchBox(
                        query = cityQuery,
                        onQueryChange = { cityQuery = it },
                        placeholder = "Search cities...",
                        testTag = "search_city_dialog_input"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 290.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (cityPairs.isEmpty()) {
                            Text(
                                text = "No matching cities found",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedHint,
                                modifier = Modifier.padding(10.dp)
                            )
                        } else {
                            cityPairs.forEach { (cityName, parentState) ->
                                val isChecked = draftFilter.selectedCities.contains(cityName)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isChecked) ChipIceBlueBg else Color.Transparent)
                                        .clickable {
                                            val updatedState = if (draftFilter.selectedState == "All States") {
                                                parentState
                                            } else {
                                                draftFilter.selectedState
                                            }
                                            val nextCities = if (isChecked) {
                                                draftFilter.selectedCities - cityName
                                            } else {
                                                draftFilter.selectedCities + cityName
                                            }
                                            draftFilter = draftFilter.copy(
                                                selectedState = updatedState,
                                                selectedCities = nextCities
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isChecked) Icons.Filled.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                                        contentDescription = null,
                                        tint = if (isChecked) ChipBlueAccent else MutedHint,
                                        modifier = Modifier.size(19.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = cityName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DeepNavyText,
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (draftFilter.selectedState == "All States") {
                                        Text(
                                            text = parentState,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SubtitleSlate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDropdownDialog = false }) {
                    Text("Done", color = VibrantOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (draftFilter.selectedCities.isNotEmpty()) {
                    TextButton(
                        onClick = { draftFilter = draftFilter.copy(selectedCities = emptySet()) }
                    ) {
                        Text("Clear", color = SubtitleSlate)
                    }
                }
            }
        )
    }

    // Searchable Duration Selector Modal
    if (showDurationDropdownDialog) {
        var durationQuery by remember { mutableStateOf("") }
        val filteredDurations = remember(durationQuery) {
            val q = durationQuery.trim()
            if (q.isBlank()) durationOptions
            else durationOptions.filter { it.contains(q, ignoreCase = true) }
        }
        AlertDialog(
            onDismissRequest = { showDurationDropdownDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "Select Trip Duration",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepNavyText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropdownSearchBox(
                        query = durationQuery,
                        onQueryChange = { durationQuery = it },
                        placeholder = "Search duration (e.g. 3–5, 10+)...",
                        testTag = "search_duration_dialog_input"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        filteredDurations.forEach { option ->
                            val isSelected = (option == "All Durations" && draftFilter.selectedDuration == null) ||
                                draftFilter.selectedDuration == option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PeachChipBg else Color.Transparent)
                                    .clickable {
                                        draftFilter = draftFilter.copy(
                                            selectedDuration = if (option == "All Durations") null else option
                                        )
                                        showDurationDropdownDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) VibrantOrange else DeepNavyText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = VibrantOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDurationDropdownDialog = false }) {
                    Text("Close", color = DeepNavyText)
                }
            }
        )
    }

    // Calendar Starting Date Picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = draftFilter.startDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        draftFilter = draftFilter.copy(startDateMillis = datePickerState.selectedDateMillis)
                        showStartDatePicker = false
                    }
                ) {
                    Text("Confirm", color = VibrantOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    if (draftFilter.startDateMillis != null) {
                        TextButton(
                            onClick = {
                                draftFilter = draftFilter.copy(startDateMillis = null)
                                showStartDatePicker = false
                            }
                        ) {
                            Text("Clear", color = SubtitleSlate)
                        }
                    }
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("Cancel", color = DeepNavyText)
                    }
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PlannerFilterSectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, CardOutline),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PeachBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = VibrantOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        color = DeepNavyText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = SubtitleSlate,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse $title" else "Expand $title",
                    tint = DeepNavyText,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun DropdownSearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, CardOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = SubtitleSlate,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = MutedHint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = DeepNavyText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(VibrantOrange),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Clear search",
                    tint = MutedHint,
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .clickable { onQueryChange("") }
                )
            }
        }
    }
}

@Composable
private fun CategoryGridTile(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 42.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(10.dp),
            color = if (selected) PeachChipBg else CardWhite,
            border = BorderStroke(
                width = if (selected) 1.3.dp else 1.dp,
                color = if (selected) VibrantOrange else FieldOutline
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) VibrantOrange else DeepNavyText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = DeepNavyText,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-5).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(VibrantOrange)
                    .border(1.5.dp, CardWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = CardWhite,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
private fun TajHeaderArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Soft sky gradient wash concentrated top-right
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFD6EAFC), Color(0xFFEAF3FB), ScreenBg),
                center = Offset(w * 0.85f, h * 0.25f),
                radius = w * 0.78f
            )
        )

        // Subtle cloud puffs
        drawCircle(Color.White.copy(alpha = 0.65f), radius = w * 0.08f, center = Offset(w * 0.72f, h * 0.28f))
        drawCircle(Color.White.copy(alpha = 0.55f), radius = w * 0.06f, center = Offset(w * 0.80f, h * 0.25f))
        drawCircle(Color.White.copy(alpha = 0.55f), radius = w * 0.07f, center = Offset(w * 0.92f, h * 0.30f))

        // Taj Mahal stylized silhouette on right side
        val tajCx = w * 0.82f
        val baseY = h * 0.68f
        val ivoryMain = Color(0xFFF3EAE0).copy(alpha = 0.92f)
        val ivoryShadow = Color(0xFFDFD3C5).copy(alpha = 0.9f)
        val archDark = Color(0xFFB5A798).copy(alpha = 0.75f)

        // Plinth
        drawRect(ivoryShadow, Offset(tajCx - w * 0.16f, baseY), Size(w * 0.32f, h * 0.06f))

        // Side Minarets
        listOf(-0.15f, -0.105f, 0.105f, 0.15f).forEach { dx ->
            val mx = tajCx + w * dx
            val mw = w * 0.012f
            val mh = if (kotlin.math.abs(dx) > 0.12f) h * 0.18f else h * 0.15f
            drawRect(ivoryMain, Offset(mx - mw / 2f, baseY - mh), Size(mw, mh))
            drawCircle(ivoryShadow, radius = mw * 0.75f, center = Offset(mx, baseY - mh))
        }

        // Main Facade Block
        val blockW = w * 0.17f
        val blockH = h * 0.17f
        drawRect(ivoryMain, Offset(tajCx - blockW / 2f, baseY - blockH), Size(blockW, blockH))

        // Central Onion Dome
        val domePath = Path().apply {
            val topY = baseY - blockH - h * 0.12f
            val neckY = baseY - blockH
            moveTo(tajCx, topY)
            cubicTo(
                tajCx + w * 0.055f, neckY - h * 0.06f,
                tajCx + w * 0.042f, neckY,
                tajCx + w * 0.036f, neckY
            )
            lineTo(tajCx - w * 0.036f, neckY)
            cubicTo(
                tajCx - w * 0.042f, neckY,
                tajCx - w * 0.055f, neckY - h * 0.06f,
                tajCx, topY
            )
            close()
        }
        drawPath(domePath, ivoryMain)

        // Finial spire
        drawLine(
            color = archDark,
            start = Offset(tajCx, baseY - blockH - h * 0.12f),
            end = Offset(tajCx, baseY - blockH - h * 0.15f),
            strokeWidth = 2f
        )

        // Side small domes
        listOf(-0.055f, 0.055f).forEach { dx ->
            drawCircle(ivoryShadow, radius = w * 0.018f, center = Offset(tajCx + w * dx, baseY - blockH - h * 0.01f))
        }

        // Central Arch & Side Niches
        drawRoundRect(
            color = archDark,
            topLeft = Offset(tajCx - w * 0.024f, baseY - blockH * 0.72f),
            size = Size(w * 0.048f, blockH * 0.72f),
            cornerRadius = CornerRadius(24f, 24f)
        )
        listOf(-0.058f, 0.044f).forEach { dx ->
            drawRoundRect(
                color = archDark.copy(alpha = 0.55f),
                topLeft = Offset(tajCx + w * dx, baseY - blockH * 0.55f),
                size = Size(w * 0.016f, blockH * 0.55f),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }

        // Garden Green Strips + Reflection Pool
        drawRect(Color(0xFF7FA96B).copy(alpha = 0.55f), Offset(tajCx - w * 0.18f, baseY + h * 0.05f), Size(w * 0.36f, h * 0.08f))
        val poolPath = Path().apply {
            moveTo(tajCx - w * 0.015f, baseY + h * 0.04f)
            lineTo(tajCx + w * 0.015f, baseY + h * 0.04f)
            lineTo(tajCx + w * 0.045f, baseY + h * 0.16f)
            lineTo(tajCx - w * 0.045f, baseY + h * 0.16f)
            close()
        }
        drawPath(poolPath, Color(0xFF8AB8D9).copy(alpha = 0.65f))

        // Stylized Jet Airplane flying diagonally up-right
        val px = w * 0.87f
        val py = h * 0.22f
        val planePath = Path().apply {
            moveTo(px - 28f, py + 12f)
            lineTo(px + 26f, py - 14f)
            lineTo(px + 30f, py - 8f)
            lineTo(px - 18f, py + 18f)
            close()
        }
        drawPath(planePath, Color(0xFF64748B).copy(alpha = 0.65f))
        val wingPath = Path().apply {
            moveTo(px - 2f, py)
            lineTo(px - 20f, py - 18f)
            lineTo(px - 12f, py - 20f)
            lineTo(px + 10f, py - 4f)
            close()
        }
        drawPath(wingPath, Color(0xFF475569).copy(alpha = 0.55f))

        // Soft white/background fade-out on left & bottom so header text stays ultra crisp
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(ScreenBg, ScreenBg.copy(alpha = 0.88f), Color.Transparent),
                startX = 0f,
                endX = w * 0.66f
            )
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, ScreenBg),
                startY = h * 0.58f,
                endY = h
            )
        )
    }
}

private fun guideMatchesCategory(guide: TravelGuideProduct, category: String): Boolean {
    val haystack = "${guide.audience} ${guide.category} ${guide.title} ${guide.highlights.joinToString(" ")}".lowercase()
    return when (category) {
        "Family" -> haystack.contains("family")
        "Couple" -> haystack.contains("couple")
        "Honeymoon" -> haystack.contains("couple") || haystack.contains("lake") || haystack.contains("paradise") || haystack.contains("beach")
        "Friends" -> haystack.contains("friends")
        "Solo" -> haystack.contains("solo")
        "Senior Citizens" -> haystack.contains("family") || haystack.contains("heritage") || haystack.contains("aarti") || haystack.contains("temple")
        "Adventure" -> haystack.contains("rafting") || haystack.contains("mountain") || haystack.contains("snow") || haystack.contains("dune") || haystack.contains("fort")
        "Religious" -> haystack.contains("aarti") || haystack.contains("temple") || haystack.contains("varanasi") || haystack.contains("pushkar") || haystack.contains("ayodhya")
        "Luxury" -> guide.price >= 149 || haystack.contains("royal") || haystack.contains("palace") || haystack.contains("houseboat")
        "Budget" -> haystack.contains("budget") || guide.price <= 119
        else -> true
    }
}
