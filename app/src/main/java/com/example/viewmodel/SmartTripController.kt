package com.example.viewmodel

import com.example.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmartTripController(
    private val dao: TravelGuideDao,
    private val cacheRepository: OfflineTravelCacheRepository,
    private val scope: CoroutineScope,
    private val onMessage: (String) -> Unit
) {
    private val _activeTrip = MutableStateFlow(SmartTripPlan())
    val activeTrip: StateFlow<SmartTripPlan> = _activeTrip.asStateFlow()

    val savedSmartTrips: StateFlow<List<SmartTripPlan>> = dao.observeSmartTrips()
        .map { entities ->
            entities.mapNotNull { SmartTripSerializer.fromJson(it.payloadJson) }
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    val registeredHotels: StateFlow<List<SmartHotelItem>> = dao.observeRegisteredHotels()
        .map { list ->
            list.map { e ->
                SmartHotelItem(
                    id = e.id,
                    stateName = e.stateName,
                    cityName = e.cityName,
                    name = e.name,
                    description = e.description,
                    address = e.address,
                    starCategory = e.starCategory,
                    rating = 4.6,
                    pricePerNight = e.pricePerNight,
                    isAc = e.isAc,
                    hasParking = e.hasParking,
                    hasBreakfast = e.hasBreakfast,
                    familyFriendly = e.familyFriendly,
                    friendsFriendly = e.friendsFriendly,
                    facilities = buildList {
                        add(if (e.isAc) "AC Rooms" else "Non-AC")
                        if (e.hasParking) add("Free Parking")
                        if (e.hasBreakfast) add("Breakfast Included")
                        add("Free Wi-Fi")
                    },
                    roomTypes = listOf(
                        HotelRoomOption("Standard Room", "${if (e.isAc) "AC" else "Non-AC"} · Wi-Fi", e.pricePerNight),
                        HotelRoomOption("Family Deluxe", "Spacious room · Extra bed", e.pricePerNight + 900)
                    ),
                    cancellationPolicy = "Free cancellation up to 24 hrs before check-in",
                    lat = e.lat,
                    lng = e.lng,
                    colorKey = "indigo"
                )
            }
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        scope.launch {
            val latest = dao.observeSmartTrips().first().firstOrNull()
            if (latest != null) {
                SmartTripSerializer.fromJson(latest.payloadJson)?.let { restored ->
                    _activeTrip.value = restored
                }
            } else {
                val seeded = SmartTripCatalog.recalculateBudgets(SmartTripPlan(
                    id = "smart_trip_seed_rajasthan",
                    status = "Planned",
                    currentStep = 1
                )).let { it.copy(daySchedules = SmartTripCatalog.generateDaySchedules(it)) }
                _activeTrip.value = seeded
                persist(seeded)
            }
        }
    }

    private fun persist(plan: SmartTripPlan) {
        val withTime = plan.copy(updatedAt = System.currentTimeMillis())
        _activeTrip.value = withTime
        scope.launch(Dispatchers.IO) {
            dao.upsertSmartTrip(
                SmartTripEntity(
                    id = withTime.id,
                    tripName = withTime.tripName.ifBlank { "${withTime.destCity} Trip" },
                    mainDestination = withTime.cityPlans.firstOrNull()?.cityName ?: withTime.destCity,
                    dateRangeText = withTime.dateRangeText,
                    daysCount = withTime.daysCount,
                    currentStep = withTime.currentStep,
                    status = withTime.status,
                    colorKey = when (withTime.destState.lowercase()) {
                        "rajasthan" -> "coral"
                        "kerala" -> "green"
                        "goa" -> "sky"
                        "himachal pradesh" -> "indigo"
                        else -> "amber"
                    },
                    payloadJson = SmartTripSerializer.toJson(withTime),
                    updatedAt = withTime.updatedAt
                )
            )
        }
    }

    fun startNewTrip() {
        val id = "smart_trip_${System.currentTimeMillis()}"
        val fresh = SmartTripCatalog.recalculateBudgets(
            SmartTripPlan(
                id = id,
                currentStep = 1,
                status = "Draft",
                tripName = "",
                selectedStates = listOf("Rajasthan"),
                cityPlans = SmartTripCatalog.defaultCityPlans()
            )
        )
        persist(fresh)
    }

    fun openTrip(tripId: String) {
        scope.launch {
            val found = savedSmartTrips.value.firstOrNull { it.id == tripId }
                ?: dao.getSmartTripById(tripId)?.let { SmartTripSerializer.fromJson(it.payloadJson) }
            if (found != null) {
                _activeTrip.value = found
            }
        }
    }

    fun goToStep(step: Int) {
        val clamped = step.coerceIn(1, 8)
        var next = _activeTrip.value.copy(currentStep = clamped)
        if (clamped == 3) {
            next = SmartTripCatalog.recalculateBudgets(next)
        }
        if (clamped == 7 && next.daySchedules.isEmpty()) {
            next = next.copy(daySchedules = SmartTripCatalog.generateDaySchedules(next))
        }
        persist(next)
    }

    fun saveModule1Basics(
        tripName: String,
        startState: String,
        startCity: String,
        destState: String,
        destCity: String,
        startDateMillis: Long,
        daysCount: Int,
        adults: Int,
        children: Int,
        skippedTravellers: Boolean,
        approxBudget: Int,
        travelCategory: String,
        travelPreference: String
    ) {
        val endMillis = startDateMillis + (daysCount - 1).coerceAtLeast(0) * 86_400_000L
        val fmt = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        val yearFmt = SimpleDateFormat("yyyy", Locale.ENGLISH)
        val rangeText = "${fmt.format(Date(startDateMillis))} – ${fmt.format(Date(endMillis))} ${yearFmt.format(Date(endMillis))} ($daysCount ${if (daysCount == 1) "Day" else "Days"})"

        val cur = _activeTrip.value
        val nextStates = if (cur.selectedStates.contains(destState)) cur.selectedStates else listOf(destState)
        val hasDestCity = cur.cityPlans.any { it.cityName.equals(destCity, ignoreCase = true) }
        val nextCityPlans = if (hasDestCity) cur.cityPlans else {
            listOf(
                SmartCityPlan(
                    cityName = destCity,
                    stateName = destState,
                    isExpanded = true,
                    attractions = SmartTripCatalog.attractionsForCity(destCity, destState).take(5)
                )
            )
        }

        val updated = SmartTripCatalog.recalculateBudgets(
            cur.copy(
                tripName = tripName.ifBlank { "$destCity $travelCategory Trip" },
                startState = startState,
                startCity = startCity,
                destState = destState,
                destCity = destCity,
                startDateMillis = startDateMillis,
                daysCount = daysCount.coerceIn(1, 30),
                dateRangeText = rangeText,
                adults = adults.coerceIn(1, 20),
                children = children.coerceIn(0, 15),
                skippedTravellers = skippedTravellers,
                approxBudget = approxBudget.coerceAtLeast(2000),
                fixedBudget = approxBudget.coerceAtLeast(2000),
                minBudget = (approxBudget * 0.8).toInt(),
                maxBudget = (approxBudget * 1.25).toInt(),
                travelCategory = travelCategory,
                travelPreference = travelPreference,
                selectedStates = nextStates,
                cityPlans = nextCityPlans
            )
        )
        persist(updated)
    }

    fun setSelectionMode(mode: String) {
        persist(_activeTrip.value.copy(selectionMode = mode))
    }

    fun toggleStateSelection(stateName: String) {
        val cur = _activeTrip.value
        val nextStates = if (cur.selectedStates.contains(stateName)) {
            if (cur.selectedStates.size > 1) cur.selectedStates - stateName else cur.selectedStates
        } else {
            cur.selectedStates + stateName
        }
        val filteredPlans = cur.cityPlans.filter { nextStates.contains(it.stateName) }.ifEmpty {
            val firstState = nextStates.first()
            val firstCity = SmartTripCatalog.allStatesWithCities[firstState]?.firstOrNull() ?: "Jaipur"
            listOf(
                SmartCityPlan(
                    cityName = firstCity,
                    stateName = firstState,
                    isExpanded = true,
                    attractions = SmartTripCatalog.attractionsForCity(firstCity, firstState).take(4)
                )
            )
        }
        persist(cur.copy(selectedStates = nextStates, cityPlans = filteredPlans))
    }

    fun toggleCitySelection(stateName: String, cityName: String) {
        val cur = _activeTrip.value
        val exists = cur.cityPlans.any { it.cityName.equals(cityName, ignoreCase = true) }
        val nextPlans = if (exists) {
            if (cur.cityPlans.size > 1) cur.cityPlans.filterNot { it.cityName.equals(cityName, ignoreCase = true) }
            else {
                onMessage("Keep at least one city in your trip")
                cur.cityPlans
            }
        } else {
            cur.cityPlans + SmartCityPlan(
                cityName = cityName,
                stateName = stateName,
                isExpanded = true,
                attractions = SmartTripCatalog.attractionsForCity(cityName, stateName).take(4)
            )
        }
        val nextStates = (cur.selectedStates + stateName).distinct()
        val recalculated = SmartTripCatalog.recalculateBudgets(
            cur.copy(selectedStates = nextStates, cityPlans = nextPlans)
        )
        persist(recalculated)
    }

    fun applySuggestedDestinationPack(stateName: String, cities: List<String>) {
        val nextPlans = cities.mapIndexed { idx, c ->
            SmartCityPlan(
                cityName = c,
                stateName = stateName,
                isExpanded = idx == 0,
                attractions = SmartTripCatalog.attractionsForCity(c, stateName).take(4)
            )
        }
        val cur = _activeTrip.value
        val next = SmartTripCatalog.recalculateBudgets(
            cur.copy(
                destState = stateName,
                destCity = cities.firstOrNull() ?: cur.destCity,
                selectedStates = listOf(stateName),
                cityPlans = nextPlans,
                selectionMode = "Suggest"
            )
        )
        persist(next)
        onMessage("Applied suggested $stateName route (${cities.joinToString(" → ")})")
    }

    fun toggleCityExpanded(cityName: String) {
        val next = _activeTrip.value.cityPlans.map { cp ->
            if (cp.cityName == cityName) cp.copy(isExpanded = !cp.isExpanded) else cp
        }
        persist(_activeTrip.value.copy(cityPlans = next))
    }

    fun moveCityOrder(fromIdx: Int, toIdx: Int) {
        val list = _activeTrip.value.cityPlans.toMutableList()
        if (fromIdx !in list.indices || toIdx !in list.indices || fromIdx == toIdx) return
        val item = list.removeAt(fromIdx)
        list.add(toIdx, item)
        persist(_activeTrip.value.copy(cityPlans = list))
    }

    fun toggleAttractionInCity(cityName: String, attraction: SmartAttraction) {
        val nextPlans = _activeTrip.value.cityPlans.map { cp ->
            if (cp.cityName != cityName) cp
            else {
                val has = cp.attractions.any { it.id == attraction.id || it.name.equals(attraction.name, ignoreCase = true) }
                val nextAttrs = if (has) cp.attractions.filterNot { it.id == attraction.id || it.name.equals(attraction.name, ignoreCase = true) }
                else cp.attractions + attraction
                cp.copy(attractions = nextAttrs)
            }
        }
        persist(SmartTripCatalog.recalculateBudgets(_activeTrip.value.copy(cityPlans = nextPlans)))
    }

    fun addCustomAttractionToCity(cityName: String, placeName: String, category: String, priorityStars: Int) {
        if (placeName.isBlank()) return
        val coord = SmartTripCatalog.getCoordinate(cityName)
        val cp = _activeTrip.value.cityPlans.firstOrNull { it.cityName == cityName } ?: return
        val custom = SmartAttraction(
            id = "custom_${System.currentTimeMillis()}",
            cityName = cityName,
            stateName = cp.stateName,
            name = placeName.trim(),
            category = category.ifBlank { "Custom Spot" },
            priorityStars = priorityStars.coerceIn(3, 5),
            duration = "1.5 hrs",
            entryFee = "₹50",
            lat = coord.lat + 0.008,
            lng = coord.lng + 0.008,
            description = "Added by you to $cityName",
            isCustom = true
        )
        val nextPlans = _activeTrip.value.cityPlans.map {
            if (it.cityName == cityName) it.copy(attractions = it.attractions + custom) else it
        }
        persist(SmartTripCatalog.recalculateBudgets(_activeTrip.value.copy(cityPlans = nextPlans)))
    }

    fun moveAttractionInCity(cityName: String, fromIdx: Int, toIdx: Int) {
        val nextPlans = _activeTrip.value.cityPlans.map { cp ->
            if (cp.cityName != cityName) cp
            else {
                val attrs = cp.attractions.toMutableList()
                if (fromIdx in attrs.indices && toIdx in attrs.indices && fromIdx != toIdx) {
                    val moved = attrs.removeAt(fromIdx)
                    attrs.add(toIdx, moved)
                }
                cp.copy(attractions = attrs)
            }
        }
        persist(_activeTrip.value.copy(cityPlans = nextPlans))
    }

    fun updateBudgetSettings(
        budgetMode: String,
        fixedBudget: Int,
        minBudget: Int,
        maxBudget: Int,
        accommodation: Int,
        transportation: Int,
        food: Int,
        localTravel: Int,
        sightseeing: Int,
        misc: Int
    ) {
        persist(
            _activeTrip.value.copy(
                budgetMode = budgetMode,
                fixedBudget = fixedBudget.coerceAtLeast(1000),
                minBudget = minBudget.coerceAtLeast(1000),
                maxBudget = maxBudget.coerceAtLeast(minBudget),
                accommodationBudget = accommodation.coerceAtLeast(0),
                transportationBudget = transportation.coerceAtLeast(0),
                foodBudget = food.coerceAtLeast(0),
                localTravelBudget = localTravel.coerceAtLeast(0),
                sightseeingBudget = sightseeing.coerceAtLeast(0),
                miscBudget = misc.coerceAtLeast(0)
            )
        )
    }

    fun resetBudgetToSmartEstimate() {
        persist(SmartTripCatalog.recalculateBudgets(_activeTrip.value))
        onMessage("Budget recalculated from current selections")
    }

    fun selectHotelForCity(selection: SelectedHotelPlan) {
        val cur = _activeTrip.value
        val nextHotels = cur.selectedHotels.filterNot { it.cityName == selection.cityName } + selection
        val nextSkipped = cur.skippedHotelCities - selection.cityName
        val updated = SmartTripCatalog.recalculateBudgets(
            cur.copy(selectedHotels = nextHotels, skippedHotelCities = nextSkipped)
        )
        persist(updated)
        onMessage("${selection.hotelName} added to ${selection.cityName} plan (not booked yet)")
    }

    fun skipHotelForCity(cityName: String) {
        val cur = _activeTrip.value
        val nextHotels = cur.selectedHotels.filterNot { it.cityName == cityName }
        val nextSkipped = (cur.skippedHotelCities + cityName).distinct()
        val updated = SmartTripCatalog.recalculateBudgets(
            cur.copy(selectedHotels = nextHotels, skippedHotelCities = nextSkipped)
        )
        persist(updated)
        onMessage("Skipped hotel selection for $cityName")
    }

    fun registerCustomHotel(
        stateName: String,
        cityName: String,
        name: String,
        description: String,
        address: String,
        pricePerNight: Int,
        starCategory: Int,
        isAc: Boolean,
        hasParking: Boolean,
        hasBreakfast: Boolean,
        familyFriendly: Boolean,
        friendsFriendly: Boolean,
        lat: Double,
        lng: Double
    ) {
        scope.launch(Dispatchers.IO) {
            dao.insertRegisteredHotel(
                RegisteredHotelEntity(
                    id = "reg_hotel_${System.currentTimeMillis()}",
                    stateName = stateName,
                    cityName = cityName,
                    name = name.trim(),
                    description = description.trim(),
                    address = address.trim(),
                    pricePerNight = pricePerNight.coerceAtLeast(500),
                    starCategory = starCategory.coerceIn(1, 5),
                    isAc = isAc,
                    hasParking = hasParking,
                    hasBreakfast = hasBreakfast,
                    familyFriendly = familyFriendly,
                    friendsFriendly = friendsFriendly,
                    lat = lat,
                    lng = lng
                )
            )
        }
        onMessage("$name registered with coordinate distances calculated!")
    }

    fun selectIntercityTransport(leg: SelectedTransportLeg) {
        val cur = _activeTrip.value
        val nextLegs = cur.selectedTransportLegs.filterNot {
            it.fromCity == leg.fromCity && it.toCity == leg.toCity
        } + leg
        val updated = SmartTripCatalog.recalculateBudgets(cur.copy(selectedTransportLegs = nextLegs))
        persist(updated)
        onMessage("Added ${leg.mode} (${leg.operatorName}) for ${leg.fromCity} → ${leg.toCity}")
    }

    fun selectTaxiPlan(taxiPlan: SelectedTaxiPlan) {
        val cur = _activeTrip.value
        val updated = SmartTripCatalog.recalculateBudgets(cur.copy(selectedTaxi = taxiPlan))
        persist(updated)
        onMessage("${taxiPlan.taxiName} added to trip plan (booking happens at confirmation)")
    }

    fun clearTaxiPlan() {
        val cur = _activeTrip.value
        persist(SmartTripCatalog.recalculateBudgets(cur.copy(selectedTaxi = null)))
    }

    fun rebuildDaySchedules() {
        val cur = _activeTrip.value
        persist(cur.copy(daySchedules = SmartTripCatalog.generateDaySchedules(cur)))
        onMessage("Day-wise schedule refreshed from current selections")
    }

    fun addActivityToDay(dayNumber: Int, time: String, title: String, subtitle: String, category: String) {
        if (title.isBlank()) return
        val cur = _activeTrip.value
        val nextDays = cur.daySchedules.map { d ->
            if (d.dayNumber != dayNumber) d
            else d.copy(
                activities = d.activities + SmartDayActivity(
                    id = "custom_act_${System.currentTimeMillis()}",
                    time = time.ifBlank { "05:30 PM" },
                    title = title.trim(),
                    subtitle = subtitle.ifBlank { "Custom schedule item" },
                    category = category
                )
            )
        }
        persist(cur.copy(daySchedules = nextDays))
    }

    fun removeActivityFromDay(dayNumber: Int, activityId: String) {
        val cur = _activeTrip.value
        val nextDays = cur.daySchedules.map { d ->
            if (d.dayNumber != dayNumber) d
            else d.copy(activities = d.activities.filterNot { it.id == activityId })
        }
        persist(cur.copy(daySchedules = nextDays))
    }

    fun confirmHotelBooking(cityName: String) {
        val cur = _activeTrip.value
        val target = cur.selectedHotels.firstOrNull { it.cityName == cityName } ?: return
        val ref = "HTL-${(10000..99999).random()}"
        val nextHotels = cur.selectedHotels.map { h ->
            if (h.cityName == cityName) h.copy(isConfirmedBooking = true, bookingRef = ref) else h
        }
        val next = cur.copy(selectedHotels = nextHotels)
        persist(next)
        scope.launch(Dispatchers.IO) {
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_$ref",
                    title = "Hotel Confirmed: ${target.hotelName}",
                    subtitle = "$cityName · Ref $ref · ${target.checkInDate}",
                    category = "Purchases",
                    targetRoute = "trips"
                )
            )
        }
        onMessage("Hotel booked! Confirmation Ref: $ref")
    }

    fun confirmTaxiBookingInTrip() {
        val cur = _activeTrip.value
        val taxi = cur.selectedTaxi ?: return
        val ref = "TX-${(10000..99999).random()}"
        val nextTaxi = taxi.copy(isConfirmedBooking = true, bookingRef = ref)
        val next = cur.copy(selectedTaxi = nextTaxi)
        persist(next)
        scope.launch(Dispatchers.IO) {
            dao.upsertTaxiBooking(
                TaxiBookingEntity(
                    bookingCode = ref,
                    routeLabel = cur.cityPlans.joinToString(" → ") { it.cityName },
                    vehicleName = taxi.taxiName,
                    driverName = "Verified Partner Driver",
                    vehiclePlate = "RJ14 ··· 9042",
                    dateTimeLabel = taxi.travelDate,
                    passengers = taxi.passengers,
                    totalAmount = taxi.estimatedFare,
                    paymentMethod = "Pay at Pickup / UPI",
                    status = "Confirmed"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_$ref",
                    title = "Taxi Booked: ${taxi.taxiName}",
                    subtitle = "Pickup ${taxi.pickupLocation} · Ref $ref",
                    category = "Reminders",
                    targetRoute = "trips"
                )
            )
        }
        onMessage("Taxi booked! Confirmation Ref: $ref")
    }

    fun completeTripAndSave() {
        val cur = _activeTrip.value
        val schedule = cur.daySchedules.ifEmpty { SmartTripCatalog.generateDaySchedules(cur) }
        val completed = cur.copy(
            status = if (cur.selectedHotels.any { it.isConfirmedBooking } || cur.selectedTaxi?.isConfirmedBooking == true) "Confirmed" else "Planned",
            currentStep = 8,
            daySchedules = schedule
        )
        persist(completed)

        scope.launch(Dispatchers.IO) {
            val citiesList = completed.cityPlans.map { it.cityName }.ifEmpty { listOf(completed.destCity) }
            val savedEntity = SavedTripEntity(
                id = completed.id,
                title = completed.tripName.ifBlank { "${completed.destCity} Trip" },
                destinationState = completed.destState,
                citiesCsv = citiesList.joinToString(","),
                dateRange = completed.dateRangeText,
                days = completed.daysCount,
                adults = completed.adults,
                children = completed.children,
                travelType = completed.travelCategory,
                budgetRange = completed.travelPreference,
                totalBudget = completed.estimatedCategoryTotal,
                hotelsBudget = completed.accommodationBudget,
                transportBudget = completed.transportationBudget,
                foodBudget = completed.foodBudget,
                ticketsBudget = completed.sightseeingBudget,
                miscBudget = completed.miscBudget + completed.localTravelBudget,
                status = "Upcoming",
                isDraft = false,
                colorKey = "coral"
            )
            dao.upsertTrip(savedEntity)

            val plannedDays = schedule.map { d ->
                ItineraryDay(
                    dayNumber = d.dayNumber,
                    title = d.title,
                    city = d.cityName,
                    summary = d.activities.take(3).joinToString(" · ") { it.title },
                    startLocation = d.fromCity ?: d.cityName,
                    distanceKm = d.distanceKm,
                    travelMode = d.transportSummary ?: (completed.selectedTaxi?.taxiName ?: "Cab"),
                    estDuration = d.estDuration,
                    notes = "Planned via 8-Module Smart Trip Planner",
                    stops = d.activities.mapIndexed { idx, act ->
                        ItineraryStop(act.time, act.title, act.subtitle, act.category, idx + 1)
                    }
                )
            }
            cacheRepository.cacheItinerary(
                PlannedTrip(
                    id = completed.id,
                    title = completed.tripName.ifBlank { "${completed.destCity} Trip" },
                    destinationState = completed.destState,
                    destinationCities = citiesList,
                    dateRange = completed.dateRangeText,
                    days = completed.daysCount,
                    adults = completed.adults,
                    children = completed.children,
                    travelType = completed.travelCategory,
                    budgetRange = completed.travelPreference,
                    preferences = listOf(completed.travelCategory, completed.travelPreference),
                    status = completed.status,
                    isDraft = false,
                    isPlaceholderAi = false,
                    budget = BudgetBreakdown(
                        completed.accommodationBudget,
                        completed.transportationBudget,
                        completed.foodBudget,
                        completed.sightseeingBudget,
                        completed.miscBudget + completed.localTravelBudget
                    ),
                    itineraryDays = plannedDays,
                    colorKey = "coral"
                ),
                pinOffline = true
            )
        }
        onMessage("Trip saved to My Trips & cached offline!")
    }

    fun deleteSmartTrip(tripId: String) {
        scope.launch(Dispatchers.IO) {
            dao.deleteSmartTrip(tripId)
            dao.deleteTrip(tripId)
            cacheRepository.removeCachedItinerary(tripId)
        }
        if (_activeTrip.value.id == tripId) {
            startNewTrip()
        }
        onMessage("Trip deleted")
    }
}
