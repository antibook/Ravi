package com.example.data

import kotlinx.coroutines.delay

/**
 * Abstraction for AI-assisted trip planning.
 * Backed by a local sample generator when Firebase AI Logic is not enabled.
 */
interface TripAiRepository {
    suspend fun generateTripPlan(
        stateName: String,
        cities: List<String>,
        dateRange: String,
        days: Int,
        adults: Int,
        children: Int,
        travelType: String,
        budgetRange: String,
        preferences: List<String>
    ): PlannedTrip
}

/**
 * Abstraction for multi-turn Travel Assistant chat.
 * Uses a placeholder implementation when Firebase AI Logic is disabled.
 */
interface TravelChatAiRepository {
    suspend fun sendMessage(
        role: String,
        systemInstruction: String,
        history: List<ChatMessage>,
        userPrompt: String
    ): String
}

class PlaceholderTravelChatAiRepository : TravelChatAiRepository {
    override suspend fun sendMessage(
        role: String,
        systemInstruction: String,
        history: List<ChatMessage>,
        userPrompt: String
    ): String {
        delay(350)
        val q = userPrompt.lowercase()
        return when {
            q.contains("jaipur") || q.contains("fort") || q.contains("amber") ->
                "[$role · Sample Guide] For Jaipur, start at Amber Fort by 8:30 AM before crowds peak, then visit Hawa Mahal, City Palace, and Jantar Mantar in the afternoon. Try Dal Baati Kitchen in C-Scheme for lunch!"
            q.contains("taxi") || q.contains("cab") ->
                "[$role · Sample Guide] You can filter verified taxis by State, Pickup/Drop City, and From-To dates in the Taxi section. Outstation Dzire starts around ₹12/km extra after 250 km."
            q.contains("hotel") || q.contains("stay") ->
                "[$role · Sample Guide] Verified stays like Heritage Haveli Stay (₹2,400/night) and Pink City Palace Hotel (₹3,900/night) can be filtered by State, City, and Check-in/Check-out dates."
            q.contains("manager") || q.contains("help") ->
                "[$role · Sample Guide] In Local Trip Manager, select your State, City, and Date Range to view registered local managers with transparent daily pricing. Once booked, you can call them directly for full day-wise support!"
            else ->
                "[$role · Sample Guide] Based on your question (\"$userPrompt\"), I recommend exploring our State → City → Days Travel Guides or booking a verified Local Manager for on-ground support. (Enable Firebase AI Logic for live Gemini responses.)"
        }
    }
}

class PlaceholderTripAiRepository : TripAiRepository {
    override suspend fun generateTripPlan(
        stateName: String,
        cities: List<String>,
        dateRange: String,
        days: Int,
        adults: Int,
        children: Int,
        travelType: String,
        budgetRange: String,
        preferences: List<String>
    ): PlannedTrip {
        delay(450)
        val primaryCity = cities.firstOrNull() ?: "Jaipur"
        val multiplier = when (budgetRange) {
            "Budget" -> 0.72
            "Premium" -> 1.55
            else -> 1.0
        }
        val peopleFactor = ((adults + children * 0.6) / 2.6).coerceAtLeast(0.8)
        val dayFactor = (days / 7.0).coerceAtLeast(0.45)

        val hotels = ((21000 * multiplier * dayFactor * peopleFactor) / 500).toInt() * 500
        val transport = ((12500 * multiplier * dayFactor) / 500).toInt() * 500
        val food = ((7000 * multiplier * dayFactor * peopleFactor) / 500).toInt() * 500
        val tickets = ((5000 * dayFactor * peopleFactor) / 500).toInt() * 500
        val misc = ((3000 * dayFactor) / 500).toInt() * 500

        val generatedDays = buildSampleDays(stateName, cities, days, preferences)
        return PlannedTrip(
            id = "trip_${stateName.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}",
            title = "$stateName · $days Days",
            destinationState = stateName,
            destinationCities = cities.ifEmpty { listOf(primaryCity) },
            dateRange = dateRange,
            days = days,
            adults = adults,
            children = children,
            travelType = travelType,
            budgetRange = budgetRange,
            preferences = preferences,
            status = "Upcoming",
            isDraft = false,
            isPlaceholderAi = true,
            budget = BudgetBreakdown(hotels, transport, food, tickets, misc),
            itineraryDays = generatedDays,
            colorKey = "coral"
        )
    }

    companion object {
        fun buildSampleDays(
            stateName: String,
            cities: List<String>,
            days: Int,
            preferences: List<String> = listOf("Heritage", "Food")
        ): List<ItineraryDay> {
            val c1 = cities.getOrNull(0) ?: if (stateName == "Goa") "North Goa" else "Jaipur"
            val c2 = cities.getOrNull(1) ?: if (stateName == "Goa") "South Goa" else "Pushkar"
            val c3 = cities.getOrNull(2) ?: if (stateName == "Goa") "Panaji" else "Jodhpur"

            val templates = listOf(
                ItineraryDay(
                    dayNumber = 1,
                    title = "Arrive $c1",
                    city = c1,
                    summary = "Hawa Mahal · Bazaars",
                    startLocation = "Jaipur Airport / Hotel C-Scheme",
                    distanceKm = 18,
                    travelMode = "Taxi",
                    estDuration = "55m",
                    notes = "Settle into your hotel and explore Bapu Bazaar in the cool evening breeze.",
                    stops = listOf(
                        ItineraryStop("11:00", "Hotel Check-in", "C-Scheme Heritage Stay", "Stay"),
                        ItineraryStop("15:30", "Hawa Mahal", "Old City · 45 min · ticket ₹50", "Attraction", 1),
                        ItineraryStop("17:00", "Johari & Bapu Bazaar", "Evening heritage shopping walk", "Attraction", 2),
                        ItineraryStop("19:30", "Rooftop Rajasthani Café", "Dinner with Old City lights · ₹600 for two", "Food", 3)
                    )
                ),
                ItineraryDay(
                    dayNumber = 2,
                    title = "Amber Fort & City Palace",
                    city = c1,
                    summary = "3 places · 28 km",
                    startLocation = "Hotel, C-Scheme",
                    distanceKm = 28,
                    travelMode = "Taxi",
                    estDuration = "1h 20m",
                    notes = "Notes: Carry ID proof. Amber Fort is crowded after 11 AM.",
                    stops = listOf(
                        ItineraryStop("9:00", "Amber Fort", "2.5 hrs · ticket ₹100", "Attraction", 1),
                        ItineraryStop("12:30", "Lunch - Dal Baati Kitchen", "Food break · ₹450 for two", "Food"),
                        ItineraryStop("14:00", "City Palace", "2 hrs · ticket ₹200", "Attraction", 2),
                        ItineraryStop("16:30", "Jantar Mantar", "1 hr · UNESCO observatory", "Attraction", 3),
                        ItineraryStop("19:00", "Chokhi Dhani / Rooftop dinner", "Overnight: Heritage Haveli", "Food")
                    )
                ),
                ItineraryDay(
                    dayNumber = 3,
                    title = "$c1 → $c2",
                    city = c2,
                    summary = "145 km · 3 hr",
                    startLocation = "Hotel, C-Scheme",
                    distanceKm = 145,
                    travelMode = "Taxi",
                    estDuration = "3h 00m",
                    notes = "Start at 8 AM for smooth NH-48 highway drive; visit Brahma Temple before sunset.",
                    stops = listOf(
                        ItineraryStop("8:30", "Highway Departure", "Scenic drive toward $c2", "Attraction", 1),
                        ItineraryStop("12:00", "Brahma Temple & Ghat", "Sacred lakeside walk", "Attraction", 2),
                        ItineraryStop("13:30", "Lakefront Café Lunch", "Fresh thali & falafel · ₹500", "Food"),
                        ItineraryStop("17:30", "Savitri Hill Ropeway", "Panoramic desert valley sunset", "Attraction", 3)
                    )
                ),
                ItineraryDay(
                    dayNumber = 4,
                    title = "$c2 → $c3",
                    city = c3,
                    summary = "Mehrangarh Fort",
                    startLocation = "$c2 Center",
                    distanceKm = 185,
                    travelMode = "Taxi",
                    estDuration = "3h 40m",
                    notes = "Audio guide at Mehrangarh Fort is highly recommended for family history.",
                    stops = listOf(
                        ItineraryStop("9:00", "Morning Drive to $c3", "Check-in near Old City", "Stay"),
                        ItineraryStop("14:00", "Mehrangarh Fort", "2.5 hrs · Blue City views", "Attraction", 1),
                        ItineraryStop("17:00", "Jaswant Thada", "Marble cenotaph garden", "Attraction", 2),
                        ItineraryStop("19:00", "Clock Tower Spice Market", "Mirchi Vada & Lassi tasting", "Food", 3)
                    )
                ),
                ItineraryDay(
                    dayNumber = 5,
                    title = "$c3 Heritage & Lakes",
                    city = c3,
                    summary = "Stepwells & Local Crafts",
                    startLocation = "Heritage Hotel",
                    distanceKm = 24,
                    travelMode = "Taxi",
                    estDuration = "1h 10m",
                    notes = "Preferred focus: ${preferences.joinToString(", ").ifEmpty { "Heritage & Food" }}.",
                    stops = listOf(
                        ItineraryStop("9:30", "Toorji Ka Jhalra", "Historic stepwell architecture", "Attraction", 1),
                        ItineraryStop("11:30", "Umaid Bhawan Museum", "Vintage car & palace gallery", "Attraction", 2),
                        ItineraryStop("16:30", "Local Artisan Workshop", "Block print & handicraft stop", "Attraction", 3),
                        ItineraryStop("20:00", "Courtyard Folk Dinner", "Traditional thali & live music", "Food")
                    )
                )
            )

            return (1..days).map { dayIdx ->
                val base = templates[(dayIdx - 1) % templates.size]
                if (dayIdx <= templates.size) {
                    base
                } else {
                    base.copy(
                        dayNumber = dayIdx,
                        title = "Day $dayIdx · Explore ${cities.getOrElse(dayIdx % cities.size.coerceAtLeast(1)) { c1 }}",
                        summary = "${base.distanceKm} km · Curated highlights"
                    )
                }
            }
        }
    }
}
