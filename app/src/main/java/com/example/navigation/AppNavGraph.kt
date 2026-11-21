package com.example.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.*
import com.example.ui.components.GuestGateDialog
import com.example.ui.components.TravelBottomBar
import com.example.ui.screens.*
import com.example.ui.screens.planner.SmartTripPlannerScreen
import com.example.viewmodel.TravelViewModel

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val OTP_VERIFY = "otp_verify"
    const val HOME = "home"
    const val SEARCH = "search"
    const val EXPLORE = "explore"
    const val COUNTRY_SELECT = "country_select"
    const val STATE_SELECT = "state_select"
    const val CITY_SELECT = "city_select/{stateId}"
    const val DEST_DETAIL = "dest_detail/{cityId}"
    const val PLACES_LIST = "places_list/{cityId}"
    const val PLANNER_DEST = "planner_dest"
    const val SMART_PLANNER = "smart_planner"
    const val PLANNER_DATES = "planner_dates"
    const val PLANNER_TRAVELLERS = "planner_travellers"
    const val ITINERARY = "itinerary"
    const val DAY_DETAIL = "day_detail/{dayNum}"
    const val ROUTE_MAP = "route_map/{dayNum}"
    const val BUDGET = "budget"
    const val TRAVEL_STORE = "travel_store"
    const val EXPLORE_TRAVEL_GUIDES = "explore_travel_guides"
    const val PRODUCT_DETAIL = "product_detail"
    const val CHECKOUT_HANDOFF = "checkout_handoff"
    const val SUPERPROFILE_CHECKOUT = "superprofile_checkout"
    const val VERIFYING_PAYMENT = "verifying_payment"
    const val PURCHASE_SUCCESS = "purchase_success"
    const val MY_PURCHASES = "my_purchases"
    const val BOOK_TAXI = "book_taxi"
    const val TAXI_LISTINGS = "taxi_listings"
    const val DRIVER_PROFILE = "driver_profile"
    const val FARE_BREAKDOWN = "fare_breakdown"
    const val CONFIRM_TAXI = "confirm_taxi"
    const val AWAITING_OPERATOR = "awaiting_operator"
    const val LIVE_TRACKING = "live_tracking"
    const val HOTELS = "hotels"
    const val HOTEL_DETAIL = "hotel_detail/{hotelId}"
    const val RESTAURANTS = "restaurants"
    const val RESTAURANT_DETAIL = "restaurant_detail/{restaurantId}"
    const val ACTIVITIES = "activities"
    const val LOCAL_MANAGER = "local_manager"
    const val TRAVEL_CHAT = "travel_chat"
    const val REVIEWS = "reviews"
    const val HELP_SUPPORT = "help_support"
    const val TRIPS = "trips"
    const val SAVED = "saved"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val OFFLINE_CACHE = "offline_cache"
    const val DESIGN_SYSTEM = "design_system"
}

@Composable
fun TravelGuideApp(viewModel: TravelViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.SPLASH

    val session by viewModel.userSession.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val trips by viewModel.savedTrips.collectAsStateWithLifecycle()
    val savedItems by viewModel.savedItems.collectAsStateWithLifecycle()
    val savedItemIds = remember(savedItems) { savedItems.map { it.itemId }.toSet() }
    val purchases by viewModel.purchasedGuides.collectAsStateWithLifecycle()
    val bookedManagers by viewModel.managerBookings.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val plannerDraft by viewModel.plannerDraft.collectAsStateWithLifecycle()
    val activeTrip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingPlan.collectAsStateWithLifecycle()
    val taxiDraft by viewModel.taxiDraft.collectAsStateWithLifecycle()
    val selectedTaxi by viewModel.selectedTaxi.collectAsStateWithLifecycle()
    val activeBookingCode by viewModel.activeBookingCode.collectAsStateWithLifecycle()
    val selectedGuide by viewModel.selectedGuide.collectAsStateWithLifecycle()
    val selectedOrderId by viewModel.selectedOrderId.collectAsStateWithLifecycle()
    val pendingPhone by viewModel.pendingPhone.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatRole by viewModel.chatRole.collectAsStateWithLifecycle()
    val isChatSending by viewModel.isChatSending.collectAsStateWithLifecycle()
    val guestGateVisible by viewModel.guestGateVisible.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val cachedItineraries by viewModel.cachedItineraries.collectAsStateWithLifecycle()
    val cachedDestinations by viewModel.cachedDestinations.collectAsStateWithLifecycle()
    val activeSmartTrip by viewModel.activeSmartTrip.collectAsStateWithLifecycle()
    val savedSmartTrips by viewModel.savedSmartTrips.collectAsStateWithLifecycle()
    val registeredHotels by viewModel.registeredHotels.collectAsStateWithLifecycle()

    val copy = remember(language) { AppStrings.get(language) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val routesWithBottomBar = remember {
        setOf(
            Routes.HOME, Routes.EXPLORE, Routes.TRIPS, Routes.SAVED, Routes.PROFILE,
            Routes.TRAVEL_STORE, Routes.HOTELS, Routes.RESTAURANTS, Routes.ACTIVITIES,
            Routes.LOCAL_MANAGER, Routes.REVIEWS, Routes.HELP_SUPPORT,
            Routes.BOOK_TAXI, Routes.TAXI_LISTINGS, Routes.DRIVER_PROFILE,
            Routes.FARE_BREAKDOWN, Routes.CONFIRM_TAXI, Routes.AWAITING_OPERATOR,
            Routes.LIVE_TRACKING, Routes.PLANNER_DEST, Routes.PLANNER_DATES,
            Routes.PLANNER_TRAVELLERS, Routes.ITINERARY, Routes.DAY_DETAIL,
            Routes.ROUTE_MAP, Routes.BUDGET, Routes.MY_PURCHASES
        )
    }
    val showBottomBar = currentRoute in routesWithBottomBar

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                TravelBottomBar(
                    currentRoute = currentRoute,
                    copy = copy,
                    onNavigate = { target ->
                        navController.navigate(target) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH
            ) {
                composable(Routes.SPLASH) {
                    SplashScreen(
                        session = session,
                        onNavigateToOnboarding = {
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.ONBOARDING) {
                    OnboardingScreen(
                        onFinished = {
                            viewModel.completeOnboarding()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.LOGIN) {
                    LoginScreen(
                        onSendOtp = { phone ->
                            viewModel.setPendingPhone(phone)
                            navController.navigate(Routes.OTP_VERIFY)
                        },
                        onGoogleProfileSignIn = { name, email ->
                            viewModel.signInWithProfile(name, email, "Google") {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        },
                        onEmailSignIn = { name, email ->
                            viewModel.signInWithProfile(name, email, "Email") {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        },
                        onContinueAsGuest = {
                            viewModel.continueAsGuest {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Routes.OTP_VERIFY) {
                    OtpVerifyScreen(
                        phone = pendingPhone,
                        onBack = { navController.popBackStack() },
                        onVerified = {
                            viewModel.verifyOtpAndLogin {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Routes.HOME) {
                    HomeScreen(
                        copy = copy,
                        unreadNotificationCount = notifications.count { !it.isRead },
                        onOpenSearch = { navController.navigate(Routes.SEARCH) },
                        onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                        onOpenProfile = { navController.navigate(Routes.PROFILE) },
                        onOpenChatAssistant = { navController.navigate(Routes.TRAVEL_CHAT) },
                        onPlanMyTrip = {
                            viewModel.smartPlanner.goToStep(1)
                            navController.navigate(Routes.SMART_PLANNER)
                        },
                        onSelectCity = { cityId -> navController.navigate("dest_detail/$cityId") },
                        onOpenTravelGuides = { navController.navigate(Routes.EXPLORE_TRAVEL_GUIDES) },
                        onOpenTripPlanner = {
                            navController.navigate(Routes.SMART_PLANNER)
                        },
                        onOpenHotels = { navController.navigate(Routes.HOTELS) },
                        onOpenTaxi = { navController.navigate(Routes.BOOK_TAXI) },
                        onOpenRestaurants = { navController.navigate(Routes.RESTAURANTS) },
                        onOpenLocalManager = { navController.navigate(Routes.LOCAL_MANAGER) },
                        onOpenActivities = { navController.navigate(Routes.ACTIVITIES) }
                    )
                }

                composable(Routes.SEARCH) {
                    SearchScreen(
                        onBack = { navController.popBackStack() },
                        onSelectCity = { cityId -> navController.navigate("dest_detail/$cityId") },
                        onSelectGuide = { guideId ->
                            val guide = CatalogData.guides.firstOrNull { it.id == guideId } ?: CatalogData.guides.first()
                            viewModel.selectGuide(guide)
                            navController.navigate(Routes.PRODUCT_DETAIL)
                        },
                        onOpenHotels = { navController.navigate(Routes.HOTELS) }
                    )
                }

                composable(Routes.EXPLORE) {
                    ExploreScreen(
                        copy = copy,
                        onOpenSearch = { navController.navigate(Routes.SEARCH) },
                        onOpenCountrySelection = { navController.navigate(Routes.COUNTRY_SELECT) },
                        onOpenStateSelection = { navController.navigate(Routes.STATE_SELECT) },
                        onSelectState = { stateId -> navController.navigate("city_select/$stateId") },
                        onOpenTravelStore = { navController.navigate(Routes.EXPLORE_TRAVEL_GUIDES) }
                    )
                }

                composable(Routes.COUNTRY_SELECT) {
                    CountrySelectionScreen(
                        onBack = { navController.popBackStack() },
                        onSelectIndia = { navController.navigate(Routes.STATE_SELECT) },
                        onSelectInternationalGuide = { navController.navigate(Routes.TRAVEL_STORE) }
                    )
                }

                composable(Routes.STATE_SELECT) {
                    StateSelectionScreen(
                        onBack = { navController.popBackStack() },
                        onSelectState = { stateId -> navController.navigate("city_select/$stateId") }
                    )
                }

                composable(
                    route = Routes.CITY_SELECT,
                    arguments = listOf(navArgument("stateId") { type = NavType.StringType })
                ) { entry ->
                    val stateId = entry.arguments?.getString("stateId") ?: "rajasthan"
                    CitySelectionScreen(
                        stateId = stateId,
                        onBack = { navController.popBackStack() },
                        onSelectCity = { cityId -> navController.navigate("dest_detail/$cityId") }
                    )
                }

                composable(
                    route = Routes.DEST_DETAIL,
                    arguments = listOf(navArgument("cityId") { type = NavType.StringType })
                ) { entry ->
                    val cityId = entry.arguments?.getString("cityId") ?: "jaipur"
                    val isSaved = savedItems.any { it.itemId == "city_$cityId" }
                    val cachedDest = remember(cachedDestinations, cityId) {
                        cachedDestinations.firstOrNull { it.cityId == cityId }
                    }
                    DestinationDetailScreen(
                        cityId = cityId,
                        isSaved = isSaved,
                        isOnline = isOnline,
                        cachedDestination = cachedDest,
                        onEnsureCached = viewModel::ensureDestinationCached,
                        onToggleOfflinePin = viewModel::toggleDestinationOfflinePin,
                        onBack = { navController.popBackStack() },
                        onToggleSave = { city ->
                            viewModel.toggleSavedItem(
                                itemId = "city_${city.id}",
                                title = city.name,
                                subtitle = "${city.stateName} · Destination",
                                type = "Destinations",
                                colorKey = city.colorKey
                            )
                        },
                        onOpenPlacesList = { cid -> navController.navigate("places_list/$cid") },
                        onViewMap = { navController.navigate("route_map/1") },
                        onOpenItinerary = { city ->
                            viewModel.openQuickDestinationItinerary(city)
                            navController.navigate(Routes.ITINERARY)
                        },
                        onBuyGuide = { guideId ->
                            val guide = CatalogData.guides.firstOrNull { it.id == guideId } ?: CatalogData.guides.first()
                            viewModel.selectGuide(guide)
                            navController.navigate(Routes.PRODUCT_DETAIL)
                        }
                    )
                }

                composable(
                    route = Routes.PLACES_LIST,
                    arguments = listOf(navArgument("cityId") { type = NavType.StringType })
                ) { entry ->
                    val cityId = entry.arguments?.getString("cityId") ?: "jaipur"
                    val savedPlaceIds = savedItems.map { it.itemId.removePrefix("place_") }.toSet()
                    val cachedDest = remember(cachedDestinations, cityId) {
                        cachedDestinations.firstOrNull { it.cityId == cityId }
                    }
                    TouristPlacesScreen(
                        cityId = cityId,
                        savedIds = savedPlaceIds,
                        isOnline = isOnline,
                        cachedDestination = cachedDest,
                        onEnsureCached = viewModel::ensureDestinationCached,
                        onBack = { navController.popBackStack() },
                        onToggleSavePlace = { place ->
                            viewModel.toggleSavedItem(
                                itemId = "place_${place.id}",
                                title = place.name,
                                subtitle = place.category,
                                type = "Destinations",
                                colorKey = place.colorKey
                            )
                        }
                    )
                }

                // Part 3: 8-Module Smart Trip Planner + Quick Itinerary
                composable(Routes.SMART_PLANNER) {
                    SmartTripPlannerScreen(
                        plan = activeSmartTrip,
                        registeredHotels = registeredHotels,
                        controller = viewModel.smartPlanner,
                        onExitToHome = {
                            navController.popBackStack(Routes.HOME, inclusive = false)
                        },
                        onExitToMyTrips = {
                            navController.navigate(Routes.TRIPS) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Routes.PLANNER_DEST) {
                    PlannerDestinationScreen(
                        draft = plannerDraft,
                        onBack = { navController.popBackStack() },
                        onUpdateDestination = viewModel::updatePlannerDestination,
                        onAddCity = viewModel::addPlannerCity,
                        onNext = { navController.navigate(Routes.PLANNER_DATES) }
                    )
                }

                composable(Routes.PLANNER_DATES) {
                    PlannerDatesScreen(
                        draft = plannerDraft,
                        onBack = { navController.popBackStack() },
                        onUpdateDates = viewModel::updatePlannerDates,
                        onNext = { navController.navigate(Routes.PLANNER_TRAVELLERS) }
                    )
                }

                composable(Routes.PLANNER_TRAVELLERS) {
                    PlannerTravellersScreen(
                        draft = plannerDraft,
                        isGenerating = isGenerating,
                        onBack = { navController.popBackStack() },
                        onUpdateTravellers = viewModel::updatePlannerTravellers,
                        onGeneratePlan = {
                            viewModel.generatePlannedTrip {
                                navController.navigate(Routes.ITINERARY)
                            }
                        }
                    )
                }

                composable(Routes.ITINERARY) {
                    val isFav = savedItemIds.contains(activeTrip.id)
                    val cachedEntity = remember(cachedItineraries, activeTrip.id) {
                        cachedItineraries.firstOrNull { it.id == activeTrip.id }
                    }
                    SuggestedItineraryScreen(
                        trip = activeTrip,
                        copy = copy,
                        isFavorited = isFav,
                        isOnline = isOnline,
                        isCachedOffline = cachedEntity != null,
                        cachedAtMillis = cachedEntity?.cachedAt,
                        onBack = { navController.popBackStack() },
                        onToggleFavorite = {
                            viewModel.toggleSavedItem(
                                itemId = activeTrip.id,
                                title = activeTrip.title,
                                subtitle = "${activeTrip.days} Days · ₹${activeTrip.budget.total}",
                                type = "Itineraries",
                                colorKey = activeTrip.colorKey
                            )
                        },
                        onOpenDayDetail = { d -> navController.navigate("day_detail/$d") },
                        onOpenRouteMap = { d -> navController.navigate("route_map/$d") },
                        onOpenBudget = { navController.navigate(Routes.BUDGET) },
                        onSaveTrip = viewModel::saveActiveTripToDatabase,
                        onDownloadOffline = viewModel::downloadActiveTripOffline
                    )
                }

                composable(
                    route = Routes.DAY_DETAIL,
                    arguments = listOf(navArgument("dayNum") { type = NavType.IntType })
                ) { entry ->
                    val dayNum = entry.arguments?.getInt("dayNum") ?: 1
                    DayDetailScreen(
                        trip = activeTrip,
                        dayNumber = dayNum,
                        onBack = { navController.popBackStack() },
                        onSelectDay = { nextDay ->
                            navController.navigate("day_detail/$nextDay") {
                                popUpTo("day_detail/$dayNum") { inclusive = true }
                            }
                        },
                        onOpenMap = { d -> navController.navigate("route_map/$d") }
                    )
                }

                composable(
                    route = Routes.ROUTE_MAP,
                    arguments = listOf(navArgument("dayNum") { type = NavType.IntType })
                ) { entry ->
                    val dayNum = entry.arguments?.getInt("dayNum") ?: 1
                    RouteMapScreen(
                        trip = activeTrip,
                        dayNumber = dayNum,
                        onBack = { navController.popBackStack() },
                        onOpenBudget = { navController.navigate(Routes.BUDGET) },
                        onViewPlaces = { navController.navigate("places_list/jaipur") },
                        onBookTaxi = { navController.navigate(Routes.BOOK_TAXI) }
                    )
                }

                composable(Routes.BUDGET) {
                    BudgetCalculatorScreen(
                        trip = activeTrip,
                        onBack = { navController.popBackStack() },
                        onUpdateBudget = viewModel::updateActiveBudget,
                        onSaveAndShare = viewModel::saveActiveTripToDatabase
                    )
                }

                // Part 4: Travel Guide Discovery & Store
                composable(Routes.EXPLORE_TRAVEL_GUIDES) {
                    ExploreTravelGuidesScreen(
                        savedItemIds = savedItemIds,
                        onBack = { navController.popBackStack() },
                        onOpenSavedShortlist = { navController.navigate(Routes.SAVED) }
                    )
                }

                composable(Routes.TRAVEL_STORE) {
                    TravelStoreScreen(
                        copy = copy,
                        savedItemIds = savedItemIds,
                        onSelectGuide = { guide ->
                            viewModel.selectGuide(guide)
                            navController.navigate(Routes.PRODUCT_DETAIL)
                        },
                        onToggleSaveGuide = { guide ->
                            viewModel.toggleSavedItem(
                                itemId = guide.id,
                                title = guide.title,
                                subtitle = "PDF · ${guide.days} Days",
                                type = "Guides",
                                colorKey = guide.colorKey
                            )
                        },
                        onOpenPurchases = { navController.navigate(Routes.MY_PURCHASES) }
                    )
                }

                composable(Routes.PRODUCT_DETAIL) {
                    val isSaved = savedItems.any { it.itemId == selectedGuide.id }
                    ProductDetailsScreen(
                        guide = selectedGuide,
                        isSaved = isSaved,
                        onBack = { navController.popBackStack() },
                        onToggleSave = {
                            viewModel.toggleSavedItem(
                                itemId = selectedGuide.id,
                                title = selectedGuide.title,
                                subtitle = "PDF · ${selectedGuide.days} Days",
                                type = "Guides",
                                colorKey = selectedGuide.colorKey
                            )
                        },
                        onBuyNow = {
                            viewModel.requireAuthenticatedAction {
                                navController.navigate(Routes.CHECKOUT_HANDOFF)
                            }
                        }
                    )
                }

                composable(Routes.CHECKOUT_HANDOFF) {
                    CheckoutHandoffScreen(
                        guide = selectedGuide,
                        userEmail = session.email,
                        onBack = { navController.popBackStack() },
                        onProceedToGateway = { navController.navigate(Routes.SUPERPROFILE_CHECKOUT) }
                    )
                }

                composable(Routes.SUPERPROFILE_CHECKOUT) {
                    SuperProfileCheckoutScreen(
                        guide = selectedGuide,
                        onBack = { navController.popBackStack() },
                        onPayNow = { navController.navigate(Routes.VERIFYING_PAYMENT) }
                    )
                }

                composable(Routes.VERIFYING_PAYMENT) {
                    VerifyingPaymentScreen(
                        onVerified = {
                            viewModel.completeGuidePurchase {
                                navController.navigate(Routes.PURCHASE_SUCCESS) {
                                    popUpTo(Routes.PRODUCT_DETAIL)
                                }
                            }
                        }
                    )
                }

                composable(Routes.PURCHASE_SUCCESS) {
                    PurchaseSuccessScreen(
                        guide = selectedGuide,
                        orderId = selectedOrderId,
                        onOpenPurchases = { navController.navigate(Routes.MY_PURCHASES) },
                        onBackHome = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.MY_PURCHASES) {
                    MyPurchasesScreen(
                        purchases = purchases,
                        onBack = { navController.popBackStack() }
                    )
                }

                // Part 5: Taxi Marketplace
                composable(Routes.BOOK_TAXI) {
                    BookTaxiScreen(
                        draft = taxiDraft,
                        copy = copy,
                        onBack = { navController.popBackStack() },
                        onUpdateDraft = viewModel::updateTaxiDraft,
                        onSearchTaxi = { navController.navigate(Routes.TAXI_LISTINGS) }
                    )
                }

                composable(Routes.TAXI_LISTINGS) {
                    TaxiListingsScreen(
                        draft = taxiDraft,
                        onBack = { navController.popBackStack() },
                        onSelectTaxi = { taxi ->
                            viewModel.selectTaxi(taxi)
                            navController.navigate(Routes.DRIVER_PROFILE)
                        }
                    )
                }

                composable(Routes.DRIVER_PROFILE) {
                    DriverProfileScreen(
                        taxi = selectedTaxi,
                        onBack = { navController.popBackStack() },
                        onSeeFareBreakdown = { navController.navigate(Routes.FARE_BREAKDOWN) }
                    )
                }

                composable(Routes.FARE_BREAKDOWN) {
                    FareBreakdownScreen(
                        taxi = selectedTaxi,
                        daysCount = taxiDraft.daysCount,
                        onBack = { navController.popBackStack() },
                        onBookNow = {
                            viewModel.requireAuthenticatedAction {
                                navController.navigate(Routes.CONFIRM_TAXI)
                            }
                        }
                    )
                }

                composable(Routes.CONFIRM_TAXI) {
                    ConfirmTaxiBookingScreen(
                        taxi = selectedTaxi,
                        draft = taxiDraft,
                        userName = session.name,
                        userPhone = session.phone,
                        onBack = { navController.popBackStack() },
                        onPayAndConfirm = { method ->
                            viewModel.confirmTaxiBooking(method) {
                                navController.navigate(Routes.AWAITING_OPERATOR)
                            }
                        }
                    )
                }

                composable(Routes.AWAITING_OPERATOR) {
                    AwaitingOperatorScreen(
                        bookingCode = activeBookingCode,
                        onViewLiveTracking = {
                            viewModel.markActiveTaxiConfirmed()
                            navController.navigate(Routes.LIVE_TRACKING)
                        },
                        onFindAnotherTaxi = {
                            navController.popBackStack(Routes.TAXI_LISTINGS, inclusive = false)
                        }
                    )
                }

                composable(Routes.LIVE_TRACKING) {
                    LiveTripTrackingScreen(
                        taxi = selectedTaxi,
                        onBack = { navController.popBackStack() }
                    )
                }

                // Part 6: Marketplace & Support
                composable(Routes.HOTELS) {
                    HotelListingsScreen(
                        savedItemIds = savedItemIds,
                        onBack = { navController.popBackStack() },
                        onSelectHotel = { hotel -> navController.navigate("hotel_detail/${hotel.id}") },
                        onToggleSaveHotel = { hotel ->
                            viewModel.toggleSavedItem(
                                itemId = hotel.id,
                                title = hotel.name,
                                subtitle = "${hotel.city} · ₹${hotel.pricePerNight}/night",
                                type = "Hotels",
                                colorKey = hotel.colorKey
                            )
                        }
                    )
                }

                composable(
                    route = Routes.HOTEL_DETAIL,
                    arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
                ) { entry ->
                    val hotelId = entry.arguments?.getString("hotelId") ?: "hotel_haveli"
                    val isHotelSaved = savedItemIds.contains(hotelId)
                    HotelDetailsScreen(
                        hotelId = hotelId,
                        reviews = reviews,
                        isSaved = isHotelSaved,
                        onBack = { navController.popBackStack() },
                        onToggleSave = { hotel ->
                            viewModel.toggleSavedItem(
                                itemId = hotel.id,
                                title = hotel.name,
                                subtitle = "${hotel.city} · ₹${hotel.pricePerNight}/night",
                                type = "Hotels",
                                colorKey = hotel.colorKey
                            )
                        },
                        onAddReview = viewModel::addReview,
                        onBookHotel = { hotel, room ->
                            viewModel.bookHotelOrActivity("${hotel.name} (${room.name})", room.pricePerNight)
                        }
                    )
                }

                composable(Routes.RESTAURANTS) {
                    RestaurantsScreen(
                        reviews = reviews,
                        savedItemIds = savedItemIds,
                        onBack = { navController.popBackStack() },
                        onSelectRestaurant = { rest -> navController.navigate("restaurant_detail/${rest.id}") },
                        onToggleSaveRestaurant = { rest ->
                            viewModel.toggleSavedItem(
                                itemId = rest.id,
                                title = rest.name,
                                subtitle = "${rest.city} · ₹${rest.priceForTwo} for two",
                                type = "Restaurants",
                                colorKey = rest.colorKey
                            )
                        }
                    )
                }

                composable(
                    route = Routes.RESTAURANT_DETAIL,
                    arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
                ) { entry ->
                    val restaurantId = entry.arguments?.getString("restaurantId") ?: "rest_rooftop"
                    val isSaved = savedItemIds.contains(restaurantId)
                    RestaurantDetailsScreen(
                        restaurantId = restaurantId,
                        reviews = reviews,
                        isSaved = isSaved,
                        onBack = { navController.popBackStack() },
                        onToggleSave = { rest ->
                            viewModel.toggleSavedItem(
                                itemId = rest.id,
                                title = rest.name,
                                subtitle = "${rest.city} · ₹${rest.priceForTwo} for two",
                                type = "Restaurants",
                                colorKey = rest.colorKey
                            )
                        },
                        onAddReview = viewModel::addReview
                    )
                }

                composable(Routes.ACTIVITIES) {
                    ActivitiesScreen(
                        onBack = { navController.popBackStack() },
                        onBookActivity = { act ->
                            viewModel.bookHotelOrActivity(act.title, act.price)
                        }
                    )
                }

                composable(Routes.LOCAL_MANAGER) {
                    LocalManagerScreen(
                        copy = copy,
                        bookedManagers = bookedManagers,
                        onBack = { navController.popBackStack() },
                        onBookManager = viewModel::bookLocalManager,
                        onChangeTaxi = { navController.navigate(Routes.BOOK_TAXI) },
                        onChangeHotel = { navController.navigate(Routes.HOTELS) },
                        onReplanRoute = { navController.navigate(Routes.ITINERARY) },
                        onOpenChatAssistant = { navController.navigate(Routes.TRAVEL_CHAT) }
                    )
                }

                composable(Routes.TRAVEL_CHAT) {
                    TravelChatScreen(
                        messages = chatMessages,
                        activeRole = chatRole,
                        isSending = isChatSending,
                        onBack = { navController.popBackStack() },
                        onSwitchRole = viewModel::switchChatRole,
                        onSendMessage = viewModel::sendChatMessage
                    )
                }

                composable(Routes.REVIEWS) {
                    ReviewsScreen(
                        reviews = reviews,
                        onBack = { navController.popBackStack() },
                        onAddReview = { author, comment, rating ->
                            viewModel.addReview("global", author, comment, rating)
                        }
                    )
                }

                composable(Routes.HELP_SUPPORT) {
                    HelpSupportScreen(
                        onBack = { navController.popBackStack() },
                        onTicketSubmitted = { msg -> viewModel.showMessage(msg) }
                    )
                }

                // Part 7: Account, Saved, Trips, Notifications, Profile, Design System
                composable(Routes.TRIPS) {
                    MyTripsScreen(
                        trips = trips,
                        smartTrips = savedSmartTrips,
                        copy = copy,
                        savedItemIds = savedItemIds,
                        isOnline = isOnline,
                        cachedItinerariesCount = cachedItineraries.size,
                        cachedDestinationsCount = cachedDestinations.size,
                        onOpenOfflineCache = { navController.navigate(Routes.OFFLINE_CACHE) },
                        onPlanNewTrip = {
                            viewModel.smartPlanner.startNewTrip()
                            navController.navigate(Routes.SMART_PLANNER)
                        },
                        onViewSmartTrip = { id ->
                            viewModel.smartPlanner.openTrip(id)
                            viewModel.smartPlanner.goToStep(8)
                            navController.navigate(Routes.SMART_PLANNER)
                        },
                        onEditSmartTrip = { id ->
                            viewModel.smartPlanner.openTrip(id)
                            navController.navigate(Routes.SMART_PLANNER)
                        },
                        onDeleteSmartTrip = viewModel.smartPlanner::deleteSmartTrip,
                        onOpenItinerary = { entity ->
                            viewModel.openTrip(entity)
                            navController.navigate(Routes.ITINERARY)
                        },
                        onToggleFavoriteTrip = { entity ->
                            viewModel.toggleSavedItem(
                                itemId = entity.id,
                                title = entity.title,
                                subtitle = "${entity.days} Days · ₹${entity.totalBudget}",
                                type = "Itineraries",
                                colorKey = entity.colorKey
                            )
                        },
                        onEditTrip = { navController.navigate(Routes.SMART_PLANNER) },
                        onDeleteTrip = viewModel::deleteTrip
                    )
                }

                composable(Routes.SAVED) {
                    SavedItemsScreen(
                        items = savedItems,
                        copy = copy,
                        onOpenSavedItem = { item ->
                            when (item.type) {
                                "Hotels" -> navController.navigate("hotel_detail/${item.itemId}")
                                "Restaurants" -> navController.navigate("restaurant_detail/${item.itemId}")
                                "Itineraries" -> {
                                    viewModel.openSavedItinerary(item.itemId)
                                    navController.navigate(Routes.ITINERARY)
                                }
                                "Guides" -> {
                                    val guide = CatalogData.guides.firstOrNull { it.id == item.itemId } ?: CatalogData.guides.first()
                                    viewModel.selectGuide(guide)
                                    navController.navigate(Routes.PRODUCT_DETAIL)
                                }
                                else -> {
                                    val cityId = item.itemId.removePrefix("city_")
                                    val cleanId = if (CatalogData.cities.any { it.id == cityId }) cityId else "jaipur"
                                    navController.navigate("dest_detail/$cleanId")
                                }
                            }
                        },
                        onRemoveSaved = { item ->
                            viewModel.toggleSavedItem(item.itemId, item.title, item.subtitle, item.type, item.colorKey)
                        },
                        onExploreHotels = { navController.navigate(Routes.HOTELS) },
                        onExploreRestaurants = { navController.navigate(Routes.RESTAURANTS) },
                        onPlanTrip = {
                            viewModel.smartPlanner.startNewTrip()
                            navController.navigate(Routes.SMART_PLANNER)
                        }
                    )
                }

                composable(Routes.NOTIFICATIONS) {
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = { navController.popBackStack() },
                        onNotificationClick = { notif ->
                            viewModel.markNotificationRead(notif.id)
                            when (notif.targetRoute) {
                                "my_purchases" -> navController.navigate(Routes.MY_PURCHASES)
                                "trips" -> navController.navigate(Routes.TRIPS)
                                "live_tracking" -> navController.navigate(Routes.LIVE_TRACKING)
                                "travel_store" -> navController.navigate(Routes.TRAVEL_STORE)
                                "local_manager" -> navController.navigate(Routes.LOCAL_MANAGER)
                            }
                        }
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        session = session,
                        language = language,
                        notificationsEnabled = notificationsEnabled,
                        copy = copy,
                        cachedCount = cachedItineraries.size + cachedDestinations.size,
                        onOpenOfflineCache = { navController.navigate(Routes.OFFLINE_CACHE) },
                        onOpenPurchases = { navController.navigate(Routes.MY_PURCHASES) },
                        onOpenTrips = { navController.navigate(Routes.TRIPS) },
                        onOpenSaved = { navController.navigate(Routes.SAVED) },
                        onToggleLanguage = viewModel::toggleLanguage,
                        onToggleNotifications = viewModel::setNotificationsEnabled,
                        onOpenHelp = { navController.navigate(Routes.HELP_SUPPORT) },
                        onOpenDesignSystem = { navController.navigate(Routes.DESIGN_SYSTEM) },
                        onLoginClick = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        },
                        onLogout = {
                            viewModel.logout {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            }
                        },
                        onDeleteAccount = {
                            viewModel.deleteAccount {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Routes.OFFLINE_CACHE) {
                    OfflineCacheScreen(
                        isOnline = isOnline,
                        cachedItineraries = cachedItineraries,
                        cachedDestinations = cachedDestinations,
                        onBack = { navController.popBackStack() },
                        onOpenItinerary = { cached ->
                            viewModel.openCachedItinerary(cached)
                            navController.navigate(Routes.ITINERARY)
                        },
                        onOpenDestination = { dest ->
                            navController.navigate("dest_detail/${dest.cityId}")
                        },
                        onDeleteItinerary = viewModel::removeCachedItinerary,
                        onDeleteDestination = viewModel::removeCachedDestination,
                        onClearAll = viewModel::clearOfflineCache,
                        onExploreDestinations = { navController.navigate(Routes.EXPLORE) },
                        onPlanTrip = {
                            viewModel.smartPlanner.startNewTrip()
                            navController.navigate(Routes.SMART_PLANNER)
                        }
                    )
                }

                composable(Routes.DESIGN_SYSTEM) {
                    DesignSystemStatesScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }

    if (guestGateVisible) {
        GuestGateDialog(
            copy = copy,
            onSignIn = {
                viewModel.dismissGuestGate()
                navController.navigate(Routes.LOGIN)
            },
            onDismiss = viewModel::dismissGuestGate
        )
    }
}
