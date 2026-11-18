package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class OfflineTravelCacheRepository(private val dao: TravelGuideDao) {

    val cachedItineraries: Flow<List<CachedItineraryEntity>> = dao.observeCachedItineraries()
    val cachedDestinations: Flow<List<CachedDestinationEntity>> = dao.observeCachedDestinations()

    suspend fun cacheItinerary(trip: PlannedTrip, pinOffline: Boolean = true) = withContext(Dispatchers.IO) {
        val existing = dao.getCachedItineraryById(trip.id)
        val entity = CachedItineraryEntity(
            id = trip.id,
            title = trip.title,
            destinationState = trip.destinationState,
            citiesCsv = trip.destinationCities.joinToString(","),
            dateRange = trip.dateRange,
            days = trip.days,
            adults = trip.adults,
            children = trip.children,
            travelType = trip.travelType,
            budgetRange = trip.budgetRange,
            preferencesCsv = trip.preferences.joinToString(","),
            hotelsBudget = trip.budget.hotels,
            transportBudget = trip.budget.transport,
            foodBudget = trip.budget.food,
            ticketsBudget = trip.budget.tickets,
            miscBudget = trip.budget.miscellaneous,
            daysJson = serializeDays(trip.itineraryDays),
            isPlaceholderAi = trip.isPlaceholderAi,
            colorKey = trip.colorKey,
            isPinnedOffline = pinOffline || (existing?.isPinnedOffline == true),
            cachedAt = System.currentTimeMillis()
        )
        dao.upsertCachedItinerary(entity)
    }

    suspend fun setItineraryPinned(id: String, pinned: Boolean) = withContext(Dispatchers.IO) {
        val existing = dao.getCachedItineraryById(id) ?: return@withContext
        dao.upsertCachedItinerary(existing.copy(isPinnedOffline = pinned, cachedAt = System.currentTimeMillis()))
    }

    suspend fun loadCachedItinerary(id: String): PlannedTrip? = withContext(Dispatchers.IO) {
        val entity = dao.getCachedItineraryById(id) ?: return@withContext null
        entityToPlannedTrip(entity)
    }

    fun entityToPlannedTrip(entity: CachedItineraryEntity): PlannedTrip {
        val cities = entity.citiesCsv.split(",").filter { it.isNotBlank() }
        val prefs = entity.preferencesCsv.split(",").filter { it.isNotBlank() }.ifEmpty { listOf("Heritage", "Food") }
        val days = deserializeDays(entity.daysJson).ifEmpty {
            PlaceholderTripAiRepository.buildSampleDays(entity.destinationState, cities, entity.days)
        }
        return PlannedTrip(
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
            preferences = prefs,
            status = if (entity.isPinnedOffline) "Offline Cached" else "Saved",
            isDraft = false,
            isPlaceholderAi = entity.isPlaceholderAi,
            budget = BudgetBreakdown(
                hotels = entity.hotelsBudget,
                transport = entity.transportBudget,
                food = entity.foodBudget,
                tickets = entity.ticketsBudget,
                miscellaneous = entity.miscBudget
            ),
            itineraryDays = days,
            colorKey = entity.colorKey
        )
    }

    suspend fun cacheDestination(
        city: CityItem,
        places: List<TouristPlace>,
        pinOffline: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val existing = dao.getCachedDestinationById(city.id)
        val entity = CachedDestinationEntity(
            cityId = city.id,
            stateId = city.stateId,
            stateName = city.stateName,
            name = city.name,
            tagline = city.tagline,
            recommendedDays = city.recommendedDays,
            bestTime = city.bestTime,
            overview = city.overview,
            foodHighlights = city.foodHighlights,
            reachInfo = city.reachInfo,
            shoppingHighlights = city.shoppingHighlights,
            guideId = city.guideId,
            guidePrice = city.guidePrice,
            colorKey = city.colorKey,
            placesJson = serializePlaces(places),
            isPinnedOffline = pinOffline || (existing?.isPinnedOffline == true),
            cachedAt = System.currentTimeMillis()
        )
        dao.upsertCachedDestination(entity)
    }

    suspend fun toggleDestinationPinned(city: CityItem, places: List<TouristPlace>): Boolean = withContext(Dispatchers.IO) {
        val existing = dao.getCachedDestinationById(city.id)
        val nextPinned = !(existing?.isPinnedOffline ?: false)
        val entity = CachedDestinationEntity(
            cityId = city.id,
            stateId = city.stateId,
            stateName = city.stateName,
            name = city.name,
            tagline = city.tagline,
            recommendedDays = city.recommendedDays,
            bestTime = city.bestTime,
            overview = city.overview,
            foodHighlights = city.foodHighlights,
            reachInfo = city.reachInfo,
            shoppingHighlights = city.shoppingHighlights,
            guideId = city.guideId,
            guidePrice = city.guidePrice,
            colorKey = city.colorKey,
            placesJson = serializePlaces(places),
            isPinnedOffline = nextPinned,
            cachedAt = System.currentTimeMillis()
        )
        dao.upsertCachedDestination(entity)
        nextPinned
    }

    suspend fun getCachedDestination(cityId: String): Pair<CityItem, List<TouristPlace>>? = withContext(Dispatchers.IO) {
        val entity = dao.getCachedDestinationById(cityId) ?: return@withContext null
        entityToDestinationPair(entity)
    }

    fun entityToDestinationPair(entity: CachedDestinationEntity): Pair<CityItem, List<TouristPlace>> {
        return entity.toCityAndPlaces()
    }

    suspend fun removeCachedItinerary(id: String) = withContext(Dispatchers.IO) {
        dao.deleteCachedItinerary(id)
    }

    suspend fun removeCachedDestination(cityId: String) = withContext(Dispatchers.IO) {
        dao.deleteCachedDestination(cityId)
    }

    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        dao.clearCachedItineraries()
        dao.clearCachedDestinations()
    }

    private fun serializeDays(days: List<ItineraryDay>): String {
        val arr = JSONArray()
        days.forEach { d ->
            val obj = JSONObject().apply {
                put("dayNumber", d.dayNumber)
                put("title", d.title)
                put("city", d.city)
                put("summary", d.summary)
                put("startLocation", d.startLocation)
                put("distanceKm", d.distanceKm)
                put("travelMode", d.travelMode)
                put("estDuration", d.estDuration)
                put("notes", d.notes)
                val stopsArr = JSONArray()
                d.stops.forEach { s ->
                    stopsArr.put(
                        JSONObject().apply {
                            put("time", s.time)
                            put("title", s.title)
                            put("subtitle", s.subtitle)
                            put("category", s.category)
                            if (s.pinNumber != null) put("pinNumber", s.pinNumber)
                        }
                    )
                }
                put("stops", stopsArr)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserializeDays(json: String): List<ItineraryDay> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val stopsArr = obj.optJSONArray("stops") ?: JSONArray()
                    val stops = buildList {
                        for (j in 0 until stopsArr.length()) {
                            val s = stopsArr.getJSONObject(j)
                            add(
                                ItineraryStop(
                                    time = s.optString("time"),
                                    title = s.optString("title"),
                                    subtitle = s.optString("subtitle"),
                                    category = s.optString("category"),
                                    pinNumber = if (s.has("pinNumber") && !s.isNull("pinNumber")) s.optInt("pinNumber") else null
                                )
                            )
                        }
                    }
                    add(
                        ItineraryDay(
                            dayNumber = obj.optInt("dayNumber", i + 1),
                            title = obj.optString("title"),
                            city = obj.optString("city"),
                            summary = obj.optString("summary"),
                            startLocation = obj.optString("startLocation"),
                            distanceKm = obj.optInt("distanceKm", 20),
                            travelMode = obj.optString("travelMode", "Cab"),
                            estDuration = obj.optString("estDuration", "8 hrs"),
                            notes = obj.optString("notes"),
                            stops = stops
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun serializePlaces(places: List<TouristPlace>): String {
        val arr = JSONArray()
        places.forEach { p ->
            arr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("cityId", p.cityId)
                    put("name", p.name)
                    put("category", p.category)
                    put("area", p.area)
                    put("duration", p.duration)
                    put("entryFee", p.entryFee)
                    put("description", p.description)
                    put("colorKey", p.colorKey)
                }
            )
        }
        return arr.toString()
    }

    private fun deserializePlaces(json: String): List<TouristPlace> = deserializeTouristPlacesJson(json)
}

fun CachedDestinationEntity.toCityAndPlaces(): Pair<CityItem, List<TouristPlace>> {
    val city = CityItem(
        id = cityId,
        stateId = stateId,
        stateName = stateName,
        name = name,
        tagline = tagline,
        recommendedDays = recommendedDays,
        bestTime = bestTime,
        overview = overview,
        foodHighlights = foodHighlights,
        reachInfo = reachInfo,
        shoppingHighlights = shoppingHighlights,
        guideId = guideId,
        guidePrice = guidePrice,
        colorKey = colorKey
    )
    return city to deserializeTouristPlacesJson(placesJson)
}

fun deserializeTouristPlacesJson(json: String): List<TouristPlace> {
    if (json.isBlank()) return emptyList()
    return runCatching {
        val arr = JSONArray(json)
        buildList {
            for (i in 0 until arr.length()) {
                val p = arr.getJSONObject(i)
                add(
                    TouristPlace(
                        id = p.optString("id"),
                        cityId = p.optString("cityId"),
                        name = p.optString("name"),
                        category = p.optString("category"),
                        area = p.optString("area"),
                        duration = p.optString("duration"),
                        entryFee = p.optString("entryFee"),
                        description = p.optString("description"),
                        colorKey = p.optString("colorKey", "indigo")
                    )
                )
            }
        }
    }.getOrDefault(emptyList())
}

class NetworkStatusMonitor(context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun currentIsOnline(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun observeIsOnline(): Flow<Boolean> = callbackFlow {
        trySend(currentIsOnline())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(currentIsOnline())
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        runCatching { connectivityManager.registerNetworkCallback(request, callback) }

        awaitClose {
            runCatching { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }.distinctUntilChanged()
}
