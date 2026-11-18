package com.example.data

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी")
}

data class UserSession(
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val hasCompletedOnboarding: Boolean = false,
    val name: String = "Amit Verma",
    val email: String = "amit@email.com",
    val phone: String = "+91 98765 43210",
    val authProvider: String = "Guest"
)

data class CountryItem(
    val id: String,
    val flag: String,
    val name: String,
    val subtitle: String,
    val colorKey: String,
    val isIndia: Boolean = false
)

data class StateItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val guidesCount: Int,
    val colorKey: String,
    val themeTags: List<String>
)

data class CityItem(
    val id: String,
    val stateId: String,
    val stateName: String,
    val name: String,
    val tagline: String,
    val recommendedDays: String,
    val bestTime: String,
    val overview: String,
    val foodHighlights: String,
    val reachInfo: String,
    val shoppingHighlights: String,
    val guideId: String,
    val guidePrice: Int,
    val colorKey: String
)

data class TouristPlace(
    val id: String,
    val cityId: String,
    val name: String,
    val category: String,
    val area: String,
    val duration: String,
    val entryFee: String,
    val description: String,
    val colorKey: String
)

data class TravelGuideProduct(
    val id: String,
    val title: String,
    val subtitle: String,
    val stateName: String,
    val coveredCities: List<String>,
    val category: String,
    val days: Int,
    val audience: String,
    val pages: Int,
    val price: Int,
    val rating: Double,
    val languages: String,
    val colorKey: String,
    val highlights: List<String>,
    val samplePages: List<Pair<String, String>>
)

data class ItineraryStop(
    val time: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val pinNumber: Int? = null
)

data class ItineraryDay(
    val dayNumber: Int,
    val title: String,
    val city: String,
    val summary: String,
    val startLocation: String,
    val distanceKm: Int,
    val travelMode: String,
    val estDuration: String,
    val notes: String,
    val stops: List<ItineraryStop>
)

data class BudgetBreakdown(
    val hotels: Int,
    val transport: Int,
    val food: Int,
    val tickets: Int,
    val miscellaneous: Int
) {
    val total: Int get() = hotels + transport + food + tickets + miscellaneous
}

data class PlannedTrip(
    val id: String,
    val title: String,
    val destinationState: String,
    val destinationCities: List<String>,
    val dateRange: String,
    val days: Int,
    val adults: Int,
    val children: Int,
    val travelType: String,
    val budgetRange: String,
    val preferences: List<String>,
    val status: String,
    val isDraft: Boolean = false,
    val isPlaceholderAi: Boolean = true,
    val budget: BudgetBreakdown,
    val itineraryDays: List<ItineraryDay>,
    val colorKey: String = "coral"
)

data class TaxiVehicle(
    val id: String,
    val stateName: String,
    val baseCity: String,
    val category: String,
    val modelName: String,
    val seatsInfo: String,
    val luggageInfo: String,
    val totalTripPrice: Int,
    val extraPerKm: Int,
    val isVerified: Boolean,
    val driverName: String,
    val driverExperience: String,
    val driverTrips: Int,
    val driverRating: Double,
    val vehiclePlate: String,
    val lastChecked: String,
    val baseFare: Int,
    val driverAllowance: Int,
    val toll: Int,
    val parking: Int
) {
    val breakdownTotal: Int get() = baseFare + driverAllowance + toll + parking
}

data class HotelRoomOption(
    val name: String,
    val details: String,
    val pricePerNight: Int
)

data class HotelItem(
    val id: String,
    val stateName: String,
    val city: String,
    val name: String,
    val areaInfo: String,
    val pricePerNight: Int,
    val rating: Double,
    val category: String,
    val isVerified: Boolean,
    val colorKey: String,
    val checkInOut: String,
    val facilities: List<String>,
    val policies: List<String>,
    val rooms: List<HotelRoomOption>
)

data class RestaurantItem(
    val id: String,
    val stateName: String,
    val city: String,
    val name: String,
    val tags: String,
    val filterCategory: String,
    val rating: Double,
    val priceForTwo: Int,
    val isVerified: Boolean,
    val colorKey: String,
    val areaInfo: String = "",
    val timings: String = "11:00 AM – 11:00 PM",
    val mustTryDishes: List<String> = emptyList(),
    val description: String = ""
)

data class LocalManagerItem(
    val id: String,
    val name: String,
    val stateName: String,
    val city: String,
    val experienceYears: Int,
    val tripsManaged: Int,
    val rating: Double,
    val languages: String,
    val pricePerDay: Int,
    val phoneNumber: String,
    val specialty: String,
    val isVerified: Boolean,
    val colorKey: String
)

data class ActivityItem(
    val id: String,
    val title: String,
    val city: String,
    val duration: String,
    val price: Int,
    val category: String,
    val colorKey: String,
    val description: String
)

data class ReviewItem(
    val id: String,
    val author: String,
    val comment: String,
    val rating: Int,
    val verifiedBooking: Boolean,
    val targetId: String = "global",
    val dateLabel: String = "Nov 2026"
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val roleLabel: String = "TravelGuide Assistant"
)
