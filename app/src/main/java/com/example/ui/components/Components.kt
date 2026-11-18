package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.AppStrings
import com.example.data.ReviewItem
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatInr(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${formatter.format(amount)}"
}

@Composable
fun TravelGuideLogoMark(size: Dp = 56.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(SurfaceWhite),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.56f)
                .border(width = 3.dp, color = Coral, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(size * 0.30f)
                    .border(width = 2.5.dp, color = Coral, shape = CircleShape)
            )
        }
    }
}

@Composable
fun CoralPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "coral_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Coral,
            contentColor = SurfaceWhite,
            disabledContainerColor = Coral.copy(alpha = 0.45f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun NavyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "navy_button"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Navy,
            contentColor = SurfaceWhite
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun WhiteOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "outlined_button"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, CardBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceWhite,
            contentColor = TextPrimary
        )
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VerifiedBadgeChip(
    label: String = "Verified",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = GreenSoft,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Green.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Verified",
                tint = Green,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Green,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FilterPillChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 36.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (selected) Navy else SurfaceWhite,
        shape = RoundedCornerShape(20.dp),
        border = if (selected) null else BorderStroke(1.5.dp, CardBorder)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) SurfaceWhite else TextPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun DesignCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = SurfaceWhite,
    borderColor: Color = CardBorder,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun StrictDropdownSelector(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Indigo,
    testTag: String = "dropdown_$label"
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .testTag(testTag),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceWhite,
            border = BorderStroke(1.5.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = selectedValue,
                        style = MaterialTheme.typography.titleSmall,
                        color = Navy,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose $label", tint = accentColor)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(SurfaceWhite)
                .heightIn(max = 320.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = option,
                                fontWeight = if (option == selectedValue) FontWeight.Bold else FontWeight.Normal,
                                color = if (option == selectedValue) Coral else Navy,
                                modifier = Modifier.weight(1f)
                            )
                            if (option == selectedValue) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Coral, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MultiCityDropdownSelector(
    label: String,
    selectedCities: List<String>,
    availableCities: List<String>,
    onToggleCity: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = if (selectedCities.isEmpty()) "All Cities" else selectedCities.joinToString(", ")

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .testTag("dropdown_multi_city"),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceWhite,
            border = BorderStroke(1.5.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.titleSmall,
                        color = Navy,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Cities", tint = Indigo)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(SurfaceWhite)
                .heightIn(max = 320.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "All Cities",
                        fontWeight = if (selectedCities.isEmpty()) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedCities.isEmpty()) Coral else Navy
                    )
                },
                onClick = {
                    onClearAll()
                    expanded = false
                }
            )
            HorizontalDivider(color = DividerLight)
            availableCities.forEach { city ->
                val checked = selectedCities.contains(city)
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(checkedColor = Coral)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(city, color = Navy, fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    onClick = { onToggleCity(city) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelectorField(
    label: String,
    dateRangeText: String,
    onDateRangePicked: (formattedRange: String, daysCount: Int) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "date_range_selector"
) {
    var showPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { showPicker = true }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.5.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Coral, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(Modifier.height(2.dp))
                Text(dateRangeText, style = MaterialTheme.typography.titleSmall, color = Navy, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Default.EditCalendar, contentDescription = "Pick Dates", tint = Indigo, modifier = Modifier.size(18.dp))
        }
    }

    if (showPicker) {
        val state = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = state.selectedStartDateMillis
                        val end = state.selectedEndDateMillis
                        if (start != null) {
                            val fmt = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                            val endSafe = end ?: (start + 2 * 86_400_000L)
                            val days = (((endSafe - start) / 86_400_000L).toInt() + 1).coerceAtLeast(1)
                            val text = "${fmt.format(Date(start))} – ${fmt.format(Date(endSafe))} ($days ${if (days == 1) "day" else "days"})"
                            onDateRangePicked(text, days)
                        }
                        showPicker = false
                    }
                ) { Text("Apply", color = Coral, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel", color = Navy) }
            }
        ) {
            DateRangePicker(
                state = state,
                modifier = Modifier.height(420.dp),
                title = {
                    Text("Select Dates", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, color = Navy)
                }
            )
        }
    }
}

@Composable
fun GradientBannerThumb(
    colorKey: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(cardGradientForKey(colorKey)),
        content = content
    )
}

@Composable
fun TravelTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .testTag("top_back_button"),
                shape = CircleShape,
                color = SurfaceWhite,
                border = BorderStroke(1.5.dp, CardBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Navy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Navy,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun TravelBottomBar(
    currentRoute: String,
    copy: AppStrings.LocalizedCopy,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, DividerLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab(
                label = copy.navHome,
                selected = currentRoute == "home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_home",
                onClick = { onNavigate("home") }
            )
            BottomNavTab(
                label = copy.navExplore,
                selected = currentRoute == "explore" || currentRoute == "travel_store",
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                testTag = "nav_explore",
                onClick = { onNavigate("explore") }
            )
            BottomNavTab(
                label = copy.navTrips,
                selected = currentRoute == "trips",
                selectedIcon = Icons.Filled.FlightTakeoff,
                unselectedIcon = Icons.Outlined.FlightTakeoff,
                testTag = "nav_trips",
                onClick = { onNavigate("trips") }
            )
            BottomNavTab(
                label = copy.navSaved,
                selected = currentRoute == "saved",
                selectedIcon = Icons.Filled.Favorite,
                unselectedIcon = Icons.Outlined.FavoriteBorder,
                testTag = "nav_saved",
                onClick = { onNavigate("saved") }
            )
            BottomNavTab(
                label = copy.navProfile,
                selected = currentRoute == "profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                testTag = "nav_profile",
                onClick = { onNavigate("profile") }
            )
        }
    }
}

@Composable
private fun BottomNavTab(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    val tint = if (selected) Coral else TextMuted
    Column(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) CoralSoft else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun AiNoticeBanner(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = IndigoSoft,
        border = BorderStroke(1.dp, Indigo.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Indigo,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Navy
            )
        }
    }
}

@Composable
fun GuestGateDialog(
    copy: AppStrings.LocalizedCopy,
    onSignIn: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        title = {
            Text(copy.guestGateTitle, style = MaterialTheme.typography.headlineSmall, color = Navy)
        },
        text = {
            Text(copy.guestGateBody, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        },
        confirmButton = {
            Button(
                onClick = onSignIn,
                colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = SurfaceWhite)
            ) {
                Text(copy.signInNow)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(copy.cancel, color = Navy)
            }
        }
    )
}

fun computeAverageRating(reviews: List<ReviewItem>, fallbackRating: Double): Double {
    if (reviews.isEmpty()) return fallbackRating
    return reviews.map { it.rating.toDouble() }.average()
}

@Composable
fun StarRatingRow(
    rating: Double,
    starSize: Dp = 16.dp,
    tint: Color = Amber
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            val icon = when {
                rating >= i - 0.25 -> Icons.Filled.Star
                rating >= i - 0.75 -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
fun UserReviewsAndRatingsSection(
    targetId: String,
    targetTitle: String,
    fallbackRating: Double,
    reviews: List<ReviewItem>,
    onAddReview: (targetId: String, author: String, comment: String, rating: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val targetReviews = remember(reviews, targetId) {
        reviews.filter { it.targetId == targetId }
    }
    val avgRating = remember(targetReviews, fallbackRating) {
        computeAverageRating(targetReviews, fallbackRating)
    }
    val formattedAvg = remember(avgRating) {
        String.format(Locale.ENGLISH, "%.1f", avgRating)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "User Reviews and Ratings",
                style = MaterialTheme.typography.titleMedium,
                color = Navy,
                fontWeight = FontWeight.Bold
            )
            Surface(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(50))
                    .clickable { showAddDialog = true }
                    .testTag("write_review_button_$targetId"),
                shape = RoundedCornerShape(50),
                color = CoralSoft,
                border = BorderStroke(1.dp, Coral.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = "Write Review", tint = Coral, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Write Review", style = MaterialTheme.typography.labelMedium, color = Coral, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Average rating summary card with distribution bars
        DesignCard(
            containerColor = TileAmberBg.copy(alpha = 0.65f),
            borderColor = TileAmberBorder,
            contentPadding = PaddingValues(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.widthIn(min = 84.dp)
                ) {
                    Text(
                        text = formattedAvg,
                        style = MaterialTheme.typography.displaySmall,
                        color = Navy,
                        fontWeight = FontWeight.Bold
                    )
                    StarRatingRow(rating = avgRating, starSize = 15.dp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${targetReviews.size} ${if (targetReviews.size == 1) "review" else "reviews"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (star in 5 downTo 1) {
                        val count = targetReviews.count { it.rating == star }
                        val ratio = if (targetReviews.isEmpty()) 0f else count.toFloat() / targetReviews.size.toFloat()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$star★",
                                style = MaterialTheme.typography.labelSmall,
                                color = Navy,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(22.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceWhite.copy(alpha = 0.85f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Amber)
                                )
                            }
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.width(16.dp)
                            )
                        }
                    }
                }
            }
        }

        if (targetReviews.isEmpty()) {
            DesignCard(contentPadding = PaddingValues(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Reviews, contentDescription = null, tint = Indigo, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "No traveler feedback yet for $targetTitle. Be the first to share your experience!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        } else {
            targetReviews.forEach { rev ->
                DesignCard(
                    containerColor = SurfaceWhite,
                    borderColor = CardBorder,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(IndigoSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = rev.author.trim().take(1).uppercase().ifEmpty { "T" },
                                style = MaterialTheme.typography.titleSmall,
                                color = Indigo,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = rev.author,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Navy,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (rev.verifiedBooking) {
                                    VerifiedBadgeChip("Verified")
                                }
                            }
                            Text(
                                text = rev.dateLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Surface(
                            color = AmberSoft,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Amber.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Star, contentDescription = "${rev.rating} stars", tint = Amber, modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = "${rev.rating}.0",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Navy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = rev.comment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var author by remember { mutableStateOf("Traveler") }
        var comment by remember { mutableStateOf("") }
        var selectedStars by remember { mutableIntStateOf(5) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SurfaceWhite,
            title = {
                Text(
                    text = "Rate $targetTitle",
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tap a star rating", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (star in 1..5) {
                            IconButton(
                                onClick = { selectedStars = star },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = if (star <= selectedStars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$star stars",
                                    tint = if (star <= selectedStars) Amber else TextMuted,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Short Feedback") },
                        placeholder = { Text("Share what you liked...") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (comment.isNotBlank()) {
                            onAddReview(targetId, author, comment, selectedStars)
                            showAddDialog = false
                        }
                    },
                    enabled = comment.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = SurfaceWhite)
                ) {
                    Text("Post Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Navy)
                }
            }
        )
    }
}

@Composable
fun WishlistHeartButton(
    isSaved: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = if (isSaved) "Remove from Wishlist" else "Add to Wishlist",
    testTag: String = "wishlist_heart_button"
) {
    Surface(
        modifier = modifier
            .size(40.dp)
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(onClick = onToggle)
            .testTag(testTag),
        shape = CircleShape,
        color = SurfaceWhite.copy(alpha = 0.94f),
        border = BorderStroke(1.2.dp, if (isSaved) Coral.copy(alpha = 0.5f) else CardBorder),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = contentDescription,
                tint = if (isSaved) Coral else Navy,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}


