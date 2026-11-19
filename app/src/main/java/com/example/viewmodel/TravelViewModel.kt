package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlannerDraft(
    val country: String = "India",
    val state: String = "Rajasthan",
    val selectedCities: List<String> = listOf("Jaipur"),
    val fullStateMode: Boolean = false,
    val dateRangeLabel: String = "12–19 Dec",
    val selectedDaysCount: Int = 7,
    val startDayOfMonth: Int = 12,
    val adults: Int = 2,
    val children: Int = 1,
    val travelType: String = "Couple",
    val budgetRange: String = "Standard",
    val preferences: List<String> = listOf("Heritage", "Food")
)

data class TaxiSearchDraft(
    val stateName: String = "Rajasthan",
    val tripMode: String = "Outstation",
    val pickup: String = "Jaipur",
    val drop: String = "Udaipur",
    val dateRangeLabel: String = "14 Dec – 16 Dec (3 days)",
    val daysCount: Int = 3,
    val time: String = "9:00 AM",
    val passengers: Int = 3,
    val luggage: Int = 3
)

class TravelViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesStore(application)
    private val db = TravelGuideDatabase.getInstance(application)
    private val dao = db.dao()
    private val cacheRepository = OfflineTravelCacheRepository(dao)
    private val networkMonitor = NetworkStatusMonitor(application)
    private val aiRepository: TripAiRepository = PlaceholderTripAiRepository()
    private val chatRepository: TravelChatAiRepository = PlaceholderTravelChatAiRepository()

    val smartPlanner = SmartTripController(
        dao = dao,
        cacheRepository = cacheRepository,
        scope = viewModelScope,
        onMessage = ::showMessage
    )
    val activeSmartTrip: StateFlow<SmartTripPlan> = smartPlanner.activeTrip
    val savedSmartTrips: StateFlow<List<SmartTripPlan>> = smartPlanner.savedSmartTrips
    val registeredHotels: StateFlow<List<SmartHotelItem>> = smartPlanner.registeredHotels

    val isOnline: StateFlow<Boolean> = networkMonitor.observeIsOnline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), networkMonitor.currentIsOnline())

    val cachedItineraries: StateFlow<List<CachedItineraryEntity>> = cacheRepository.cachedItineraries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedDestinations: StateFlow<List<CachedDestinationEntity>> = cacheRepository.cachedDestinations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSession: StateFlow<UserSession> = prefs.userSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSession())

    val language: StateFlow<AppLanguage> = prefs.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val notificationsEnabled: StateFlow<Boolean> = prefs.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val savedTrips: StateFlow<List<SavedTripEntity>> = dao.observeTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedItems: StateFlow<List<SavedItemEntity>> = dao.observeSavedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchasedGuides: StateFlow<List<PurchasedGuideEntity>> = dao.observePurchasedGuides()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taxiBookings: StateFlow<List<TaxiBookingEntity>> = dao.observeTaxiBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val managerBookings: StateFlow<List<ManagerBookingEntity>> = dao.observeManagerBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = dao.observeNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _plannerDraft = MutableStateFlow(PlannerDraft())
    val plannerDraft: StateFlow<PlannerDraft> = _plannerDraft.asStateFlow()

    private val _activeTrip = MutableStateFlow(
        PlannedTrip(
            id = "trip_rajasthan_7",
            title = "Rajasthan · 7 Days",
            destinationState = "Rajasthan",
            destinationCities = listOf("Jaipur", "Pushkar", "Jodhpur"),
            dateRange = "12–19 Dec",
            days = 7,
            adults = 2,
            children = 1,
            travelType = "Couple",
            budgetRange = "Standard",
            preferences = listOf("Heritage", "Food"),
            status = "Upcoming",
            isDraft = false,
            isPlaceholderAi = true,
            budget = BudgetBreakdown(21000, 12500, 7000, 5000, 3000),
            itineraryDays = PlaceholderTripAiRepository.buildSampleDays("Rajasthan", listOf("Jaipur", "Pushkar", "Jodhpur"), 7),
            colorKey = "coral"
        )
    )
    val activeTrip: StateFlow<PlannedTrip> = _activeTrip.asStateFlow()

    private val _isGeneratingPlan = MutableStateFlow(false)
    val isGeneratingPlan: StateFlow<Boolean> = _isGeneratingPlan.asStateFlow()

    private val _taxiDraft = MutableStateFlow(TaxiSearchDraft())
    val taxiDraft: StateFlow<TaxiSearchDraft> = _taxiDraft.asStateFlow()

    private val _selectedTaxi = MutableStateFlow(CatalogData.taxis[1])
    val selectedTaxi: StateFlow<TaxiVehicle> = _selectedTaxi.asStateFlow()

    private val _activeBookingCode = MutableStateFlow("TX-7712")
    val activeBookingCode: StateFlow<String> = _activeBookingCode.asStateFlow()

    private val _selectedGuide = MutableStateFlow(CatalogData.guides.first())
    val selectedGuide: StateFlow<TravelGuideProduct> = _selectedGuide.asStateFlow()

    private val _selectedOrderId = MutableStateFlow("TG-20481")
    val selectedOrderId: StateFlow<String> = _selectedOrderId.asStateFlow()

    private val _pendingPhone = MutableStateFlow("+91 98765 43210")
    val pendingPhone: StateFlow<String> = _pendingPhone.asStateFlow()

    val reviews: StateFlow<List<ReviewItem>> = dao.observeReviews()
        .map { persisted ->
            persisted.map { entity ->
                ReviewItem(
                    id = entity.id,
                    author = entity.author,
                    comment = entity.comment,
                    rating = entity.rating,
                    verifiedBooking = entity.verifiedBooking,
                    targetId = entity.targetId,
                    dateLabel = entity.dateLabel
                )
            } + CatalogData.reviews
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogData.reviews)

    private val _chatRole = MutableStateFlow("Trip Concierge")
    val chatRole: StateFlow<String> = _chatRole.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome_1",
                isUser = false,
                text = "Namaste! I'm your TravelGuide+ assistant. Ask me about Indian destinations, routes, taxis, hotels, or local tips.",
                roleLabel = "Trip Concierge"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatSending = MutableStateFlow(false)
    val isChatSending: StateFlow<Boolean> = _isChatSending.asStateFlow()

    private val _guestGateVisible = MutableStateFlow(false)
    val guestGateVisible: StateFlow<Boolean> = _guestGateVisible.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch {
            val alreadySeeded = prefs.hasSeededDefaults.first()
            if (!alreadySeeded) {
                dao.upsertTrip(
                    SavedTripEntity(
                        id = "trip_rajasthan_7",
                        title = "Rajasthan · 7 Days",
                        destinationState = "Rajasthan",
                        citiesCsv = "Jaipur,Pushkar,Jodhpur",
                        dateRange = "12–19 Dec",
                        days = 7,
                        adults = 2,
                        children = 1,
                        travelType = "Couple",
                        budgetRange = "Standard",
                        totalBudget = 48500,
                        hotelsBudget = 21000,
                        transportBudget = 12500,
                        foodBudget = 7000,
                        ticketsBudget = 5000,
                        miscBudget = 3000,
                        status = "Upcoming",
                        isDraft = false,
                        colorKey = "coral"
                    )
                )
                dao.upsertTrip(
                    SavedTripEntity(
                        id = "trip_goa_4",
                        title = "Goa · 4 Days · draft",
                        destinationState = "Goa",
                        citiesCsv = "North Goa,South Goa",
                        dateRange = "22–26 Jan",
                        days = 4,
                        adults = 2,
                        children = 0,
                        travelType = "Couple",
                        budgetRange = "Budget",
                        totalBudget = 24000,
                        hotelsBudget = 10000,
                        transportBudget = 5500,
                        foodBudget = 4500,
                        ticketsBudget = 2500,
                        miscBudget = 1500,
                        status = "Saved",
                        isDraft = true,
                        colorKey = "green"
                    )
                )
                dao.insertSavedItem(
                    SavedItemEntity("city_jaipur", "Jaipur", "Rajasthan · Destination", "Destinations", "coral")
                )
                dao.insertSavedItem(
                    SavedItemEntity("city_udaipur", "Udaipur", "City of Lakes · Destination", "Destinations", "sky")
                )
                dao.insertSavedItem(
                    SavedItemEntity("hotel_haveli", "Heritage Haveli Stay", "Jaipur · ₹2,400/night", "Hotels", "indigo")
                )
                dao.insertSavedItem(
                    SavedItemEntity("rest_peacock", "Peacock Courtyard Diner", "Jaipur · ₹850 for two", "Restaurants", "coral")
                )
                dao.insertSavedItem(
                    SavedItemEntity("trip_rajasthan_7", "Rajasthan · 7 Days", "7 Days · ₹48,500", "Itineraries", "coral")
                )
                dao.insertPurchasedGuide(
                    PurchasedGuideEntity("guide_goa_5", "Goa 5 Days", "TG-19842", 99, "Bought 02 Oct", "sky")
                )
                dao.insertNotification(
                    NotificationEntity("n1", "Purchase complete", "Rajasthan guide ready", "Purchases", "my_purchases")
                )
                dao.insertNotification(
                    NotificationEntity("n2", "Trip in 3 days", "Check your packing list", "Reminders", "trips")
                )
                dao.insertNotification(
                    NotificationEntity("n3", "Taxi confirmed", "Pickup 9:00 AM", "Reminders", "live_tracking")
                )
                dao.insertNotification(
                    NotificationEntity("n4", "20% off Goa guides", "Offer ends Sunday", "Offers", "travel_store")
                )
                cacheRepository.cacheItinerary(_activeTrip.value, pinOffline = true)
                CatalogData.cities.take(2).forEach { city ->
                    val places = CatalogData.touristPlaces.filter { it.cityId == city.id }
                    cacheRepository.cacheDestination(city, places, pinOffline = true)
                }
                prefs.markDefaultsSeeded()
            }
        }
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    fun dismissGuestGate() {
        _guestGateVisible.value = false
    }

    fun requireAuthenticatedAction(onAllowed: () -> Unit) {
        val session = userSession.value
        if (session.isLoggedIn) {
            onAllowed()
        } else {
            _guestGateVisible.value = true
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch { prefs.markOnboardingCompleted() }
    }

    fun setPendingPhone(phone: String) {
        _pendingPhone.value = phone
    }

    fun verifyOtpAndLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            prefs.setAuthenticatedUser("Amit Verma", "amit@email.com", _pendingPhone.value, "Mobile OTP")
            onSuccess()
        }
    }

    fun signInWithProfile(name: String, email: String, provider: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            prefs.setAuthenticatedUser(
                name = name.ifBlank { "Traveler" },
                email = email.ifBlank { "traveler@email.com" },
                phone = _pendingPhone.value,
                provider = provider
            )
            onSuccess()
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            prefs.continueAsGuest()
            onSuccess()
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            prefs.logout()
            onComplete()
        }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            dao.clearTrips()
            dao.clearSavedItems()
            dao.clearPurchases()
            dao.clearBookings()
            dao.clearManagerBookings()
            dao.clearReviews()
            cacheRepository.clearAllCache()
            prefs.resetAllAccountData()
            onComplete()
        }
    }

    fun toggleLanguage(lang: AppLanguage) {
        viewModelScope.launch { prefs.toggleLanguage(lang) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    fun updatePlannerDestination(state: String, city: String, fullState: Boolean) {
        _plannerDraft.update {
            it.copy(
                state = state,
                selectedCities = if (fullState) listOf("Jaipur", "Pushkar", "Jodhpur") else listOf(city),
                fullStateMode = fullState
            )
        }
    }

    fun addPlannerCity(city: String) {
        _plannerDraft.update { draft ->
            draft.copy(selectedCities = (draft.selectedCities + city).distinct())
        }
    }

    fun updatePlannerDates(days: Int, startDay: Int) {
        val endDay = (startDay + days).coerceAtMost(31)
        _plannerDraft.update {
            it.copy(
                selectedDaysCount = days,
                startDayOfMonth = startDay,
                dateRangeLabel = "$startDay–$endDay Dec"
            )
        }
    }

    fun updatePlannerTravellers(
        adults: Int = _plannerDraft.value.adults,
        children: Int = _plannerDraft.value.children,
        travelType: String = _plannerDraft.value.travelType,
        budgetRange: String = _plannerDraft.value.budgetRange,
        prefToggle: String? = null
    ) {
        _plannerDraft.update { draft ->
            val nextPrefs = if (prefToggle != null) {
                if (draft.preferences.contains(prefToggle)) draft.preferences - prefToggle
                else draft.preferences + prefToggle
            } else draft.preferences
            draft.copy(
                adults = adults.coerceIn(1, 12),
                children = children.coerceIn(0, 8),
                travelType = travelType,
                budgetRange = budgetRange,
                preferences = nextPrefs
            )
        }
    }

    fun generatePlannedTrip(onReady: () -> Unit) {
        viewModelScope.launch {
            _isGeneratingPlan.value = true
            val d = _plannerDraft.value
            val plan = aiRepository.generateTripPlan(
                stateName = d.state,
                cities = d.selectedCities,
                dateRange = d.dateRangeLabel,
                days = d.selectedDaysCount,
                adults = d.adults,
                children = d.children,
                travelType = d.travelType,
                budgetRange = d.budgetRange,
                preferences = d.preferences
            )
            _activeTrip.value = plan
            cacheRepository.cacheItinerary(plan, pinOffline = true)
            _isGeneratingPlan.value = false
            onReady()
        }
    }

    fun openTrip(entity: SavedTripEntity) {
        viewModelScope.launch {
            val cached = cacheRepository.loadCachedItinerary(entity.id)
            if (cached != null) {
                _activeTrip.value = cached
            } else {
                val cities = entity.citiesCsv.split(",").filter { it.isNotBlank() }
                val fallbackPlan = PlannedTrip(
                    id = entity.id,
                    title = entity.title,
                    destinationState = entity.destinationState,
                    destinationCities = cities,
                    dateRange = entity.dateRange,
                    days = entity.days,
                    adults = entity.adults,
                    children = entity.children,
                    travelType = entity.travelType,
                    budgetRange = entity.budgetRange,
                    preferences = listOf("Heritage", "Food"),
                    status = entity.status,
                    isDraft = entity.isDraft,
                    isPlaceholderAi = true,
                    budget = BudgetBreakdown(
                        entity.hotelsBudget, entity.transportBudget, entity.foodBudget, entity.ticketsBudget, entity.miscBudget
                    ),
                    itineraryDays = PlaceholderTripAiRepository.buildSampleDays(entity.destinationState, cities, entity.days),
                    colorKey = entity.colorKey
                )
                _activeTrip.value = fallbackPlan
                cacheRepository.cacheItinerary(fallbackPlan, pinOffline = false)
            }
        }
    }

    fun openCachedItinerary(entity: CachedItineraryEntity) {
        _activeTrip.value = cacheRepository.entityToPlannedTrip(entity)
    }

    fun openQuickDestinationItinerary(city: CityItem) {
        viewModelScope.launch {
            val quickId = "quick_${city.id}"
            val cached = cacheRepository.loadCachedItinerary(quickId)
            if (cached != null) {
                _activeTrip.value = cached
            } else {
                val quickPlan = PlannedTrip(
                    id = quickId,
                    title = "${city.name} · 3 Days",
                    destinationState = city.stateName,
                    destinationCities = listOf(city.name),
                    dateRange = "12–15 Dec",
                    days = 3,
                    adults = 2,
                    children = 0,
                    travelType = "Couple",
                    budgetRange = "Standard",
                    preferences = listOf("Heritage", "Food"),
                    status = "Saved",
                    isDraft = false,
                    isPlaceholderAi = true,
                    budget = BudgetBreakdown(9000, 4500, 3500, 2000, 1000),
                    itineraryDays = PlaceholderTripAiRepository.buildSampleDays(city.name, listOf(city.name), 3),
                    colorKey = city.colorKey
                )
                _activeTrip.value = quickPlan
                cacheRepository.cacheItinerary(quickPlan, pinOffline = false)
            }
        }
    }

    fun updateActiveBudget(newBudget: BudgetBreakdown) {
        _activeTrip.update { it.copy(budget = newBudget) }
        viewModelScope.launch {
            cacheRepository.cacheItinerary(_activeTrip.value, pinOffline = true)
        }
    }

    fun downloadActiveTripOffline() {
        viewModelScope.launch {
            val t = _activeTrip.value
            cacheRepository.cacheItinerary(t, pinOffline = true)
            dao.upsertTrip(
                SavedTripEntity(
                    id = t.id,
                    title = t.title.removeSuffix(" · draft"),
                    destinationState = t.destinationState,
                    citiesCsv = t.destinationCities.joinToString(","),
                    dateRange = t.dateRange,
                    days = t.days,
                    adults = t.adults,
                    children = t.children,
                    travelType = t.travelType,
                    budgetRange = t.budgetRange,
                    totalBudget = t.budget.total,
                    hotelsBudget = t.budget.hotels,
                    transportBudget = t.budget.transport,
                    foodBudget = t.budget.food,
                    ticketsBudget = t.budget.tickets,
                    miscBudget = t.budget.miscellaneous,
                    status = "Saved",
                    isDraft = false,
                    colorKey = t.colorKey
                )
            )
            showMessage("Itinerary cached in Room for offline access!")
        }
    }

    fun saveActiveTripToDatabase() {
        requireAuthenticatedAction {
            viewModelScope.launch {
                val t = _activeTrip.value
                cacheRepository.cacheItinerary(t, pinOffline = true)
                dao.upsertTrip(
                    SavedTripEntity(
                        id = t.id,
                        title = t.title.removeSuffix(" · draft"),
                        destinationState = t.destinationState,
                        citiesCsv = t.destinationCities.joinToString(","),
                        dateRange = t.dateRange,
                        days = t.days,
                        adults = t.adults,
                        children = t.children,
                        travelType = t.travelType,
                        budgetRange = t.budgetRange,
                        totalBudget = t.budget.total,
                        hotelsBudget = t.budget.hotels,
                        transportBudget = t.budget.transport,
                        foodBudget = t.budget.food,
                        ticketsBudget = t.budget.tickets,
                        miscBudget = t.budget.miscellaneous,
                        status = "Upcoming",
                        isDraft = false,
                        colorKey = t.colorKey
                    )
                )
                dao.insertSavedItem(
                    SavedItemEntity(t.id, t.title, "${t.days} Days · ₹${t.budget.total}", "Itineraries", t.colorKey)
                )
                showMessage("Trip saved & cached for offline access!")
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            dao.deleteTrip(id)
            showMessage("Trip removed")
        }
    }

    fun openSavedItinerary(itemId: String) {
        viewModelScope.launch {
            val cached = cacheRepository.loadCachedItinerary(itemId)
            if (cached != null) {
                _activeTrip.value = cached
                return@launch
            }
            val tripEntity = savedTrips.value.firstOrNull { it.id == itemId }
            if (tripEntity != null) {
                openTrip(tripEntity)
                return@launch
            }
            val cityId = itemId.removePrefix("quick_")
            val city = CatalogData.cities.firstOrNull { it.id == cityId }
            if (city != null) {
                openQuickDestinationItinerary(city)
            }
        }
    }

    fun ensureDestinationCached(cityId: String) {
        viewModelScope.launch {
            val existing = cacheRepository.getCachedDestination(cityId)
            if (existing == null) {
                val city = CatalogData.cities.firstOrNull { it.id == cityId } ?: return@launch
                val places = CatalogData.touristPlaces.filter { it.cityId == city.id }
                    .ifEmpty { CatalogData.touristPlaces.filter { it.cityId == "jaipur" } }
                cacheRepository.cacheDestination(city, places, pinOffline = false)
            }
        }
    }

    fun toggleDestinationOfflinePin(city: CityItem) {
        viewModelScope.launch {
            val places = CatalogData.touristPlaces.filter { it.cityId == city.id }
                .ifEmpty { CatalogData.touristPlaces.filter { it.cityId == "jaipur" } }
            val pinned = cacheRepository.toggleDestinationPinned(city, places)
            showMessage(
                if (pinned) "${city.name} saved in Room for offline access!"
                else "${city.name} unpinned from offline storage"
            )
        }
    }

    fun removeCachedItinerary(id: String) {
        viewModelScope.launch {
            cacheRepository.removeCachedItinerary(id)
            showMessage("Removed itinerary from offline cache")
        }
    }

    fun removeCachedDestination(cityId: String) {
        viewModelScope.launch {
            cacheRepository.removeCachedDestination(cityId)
            showMessage("Removed destination from offline cache")
        }
    }

    fun clearOfflineCache() {
        viewModelScope.launch {
            cacheRepository.clearAllCache()
            showMessage("Offline cache cleared")
        }
    }

    fun toggleSavedItem(itemId: String, title: String, subtitle: String, type: String, colorKey: String) {
        viewModelScope.launch {
            val exists = savedItems.value.any { it.itemId == itemId }
            if (exists) {
                dao.deleteSavedItem(itemId)
                showMessage("Removed $title from Wishlist")
            } else {
                dao.insertSavedItem(SavedItemEntity(itemId, title, subtitle, type, colorKey))
                showMessage("Added $title to Wishlist ♥")
            }
        }
    }

    fun selectGuide(guide: TravelGuideProduct) {
        _selectedGuide.value = guide
    }

    fun completeGuidePurchase(onDone: () -> Unit) {
        viewModelScope.launch {
            val guide = _selectedGuide.value
            val orderId = "TG-${(20000..29999).random()}"
            _selectedOrderId.value = orderId
            dao.insertPurchasedGuide(
                PurchasedGuideEntity(
                    guideId = guide.id,
                    title = guide.title.replace(" Travel Guide", ""),
                    orderId = orderId,
                    pricePaid = guide.price,
                    boughtLabel = "Bought today",
                    colorKey = guide.colorKey
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_$orderId",
                    title = "Purchase complete",
                    subtitle = "${guide.title} ready",
                    category = "Purchases",
                    targetRoute = "my_purchases"
                )
            )
            onDone()
        }
    }

    fun updateTaxiDraft(draft: TaxiSearchDraft) {
        _taxiDraft.value = draft
    }

    fun selectTaxi(taxi: TaxiVehicle) {
        _selectedTaxi.value = taxi
    }

    fun confirmTaxiBooking(paymentMethod: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val taxi = _selectedTaxi.value
            val draft = _taxiDraft.value
            val code = "TX-${(7000..9999).random()}"
            _activeBookingCode.value = code
            dao.upsertTaxiBooking(
                TaxiBookingEntity(
                    bookingCode = code,
                    routeLabel = "${draft.pickup} → ${draft.drop}",
                    vehicleName = taxi.modelName,
                    driverName = taxi.driverName,
                    vehiclePlate = taxi.vehiclePlate,
                    dateTimeLabel = "${draft.dateRangeLabel} · ${draft.time}",
                    passengers = draft.passengers,
                    totalAmount = taxi.breakdownTotal * draft.daysCount.coerceAtLeast(1),
                    paymentMethod = paymentMethod,
                    status = "Awaiting"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_$code",
                    title = "Taxi booking #$code",
                    subtitle = "Driver ${taxi.driverName} · ${draft.dateRangeLabel}",
                    category = "Reminders",
                    targetRoute = "live_tracking"
                )
            )
            onDone()
        }
    }

    fun markActiveTaxiConfirmed() {
        viewModelScope.launch {
            dao.updateTaxiStatus(_activeBookingCode.value, "Confirmed")
        }
    }

    fun bookLocalManager(manager: LocalManagerItem, dateRange: String, daysCount: Int) {
        requireAuthenticatedAction {
            viewModelScope.launch {
                val total = manager.pricePerDay * daysCount.coerceAtLeast(1)
                val id = "mgr_bk_${manager.id}"
                dao.insertManagerBooking(
                    ManagerBookingEntity(
                        bookingId = id,
                        managerId = manager.id,
                        managerName = manager.name,
                        stateName = manager.stateName,
                        city = manager.city,
                        dateRange = dateRange,
                        daysCount = daysCount,
                        totalPrice = total,
                        phoneNumber = manager.phoneNumber
                    )
                )
                dao.insertNotification(
                    NotificationEntity(
                        id = "notif_$id",
                        title = "Local Manager Booked: ${manager.name}",
                        subtitle = "${manager.city} · $dateRange · Call unlocked!",
                        category = "Reminders",
                        targetRoute = "local_manager"
                    )
                )
                showMessage("Booked ${manager.name} for $dateRange! Call support is now unlocked.")
            }
        }
    }

    fun bookHotelOrActivity(title: String, amount: Int) {
        requireAuthenticatedAction {
            viewModelScope.launch {
                val id = "bk_${System.currentTimeMillis()}"
                dao.insertNotification(
                    NotificationEntity(
                        id = id,
                        title = "Booking Confirmed: $title",
                        subtitle = "Amount ₹$amount · View details",
                        category = "Purchases",
                        targetRoute = "notifications"
                    )
                )
                showMessage("Booked $title (₹$amount)!")
            }
        }
    }

    fun addReview(targetId: String = "global", author: String, comment: String, rating: Int) {
        viewModelScope.launch {
            dao.insertReview(
                ReviewEntity(
                    id = "rev_${System.currentTimeMillis()}",
                    targetId = targetId,
                    author = author.ifBlank { "Verified Traveler" },
                    comment = comment.trim(),
                    rating = rating.coerceIn(1, 5),
                    verifiedBooking = true,
                    dateLabel = "Just now"
                )
            )
            showMessage("Review submitted! Thank you for sharing.")
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch { dao.markNotificationRead(id) }
    }

    fun switchChatRole(role: String) {
        _chatRole.value = role
    }

    fun sendChatMessage(prompt: String) {
        if (prompt.isBlank()) return
        val role = _chatRole.value
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = prompt.trim(),
            roleLabel = "You"
        )
        _chatMessages.update { it + userMsg }

        viewModelScope.launch {
            _isChatSending.value = true
            val systemInstruction = when (role) {
                "Local Food & Heritage Expert" -> "You are a warm Indian heritage & street food travel expert."
                "Taxi & Route Specialist" -> "You are a practical Indian intercity cab, toll, and highway route planner."
                else -> "You are a helpful TravelGuide+ Trip Concierge helping travellers across India."
            }
            val reply = chatRepository.sendMessage(
                role = role,
                systemInstruction = systemInstruction,
                history = _chatMessages.value,
                userPrompt = prompt.trim()
            )
            val botMsg = ChatMessage(
                id = "b_${System.currentTimeMillis()}",
                isUser = false,
                text = reply,
                roleLabel = role
            )
            _chatMessages.update { it + botMsg }
            _isChatSending.value = false
        }
    }
}
