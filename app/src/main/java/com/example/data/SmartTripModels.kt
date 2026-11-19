package com.example.data

import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.*

data class SmartCityCoordinate(
    val cityName: String,
    val stateName: String,
    val lat: Double,
    val lng: Double
)

data class SmartAttraction(
    val id: String,
    val cityName: String,
    val stateName: String,
    val name: String,
    val category: String,
    val priorityStars: Int, // 5 = Must Visit, 4 = Highly Recommended, 3 = Optional Visit
    val duration: String,
    val entryFee: String,
    val lat: Double,
    val lng: Double,
    val description: String = "",
    val isCustom: Boolean = false
)

data class SmartCityPlan(
    val cityName: String,
    val stateName: String,
    val isExpanded: Boolean = true,
    val attractions: List<SmartAttraction> = emptyList()
)

data class SmartHotelItem(
    val id: String,
    val stateName: String,
    val cityName: String,
    val name: String,
    val description: String,
    val address: String,
    val starCategory: Int,
    val rating: Double,
    val pricePerNight: Int,
    val isAc: Boolean,
    val hasParking: Boolean,
    val hasBreakfast: Boolean,
    val familyFriendly: Boolean,
    val friendsFriendly: Boolean,
    val facilities: List<String>,
    val roomTypes: List<HotelRoomOption>,
    val cancellationPolicy: String,
    val lat: Double,
    val lng: Double,
    val colorKey: String
)

data class SelectedHotelPlan(
    val cityName: String,
    val hotelId: String,
    val hotelName: String,
    val roomType: String,
    val pricePerNight: Int,
    val checkInDate: String,
    val checkOutDate: String,
    val nights: Int,
    val roomsCount: Int,
    val guestsCount: Int,
    val cancellationPolicy: String,
    val lat: Double,
    val lng: Double,
    val isConfirmedBooking: Boolean = false,
    val bookingRef: String = ""
) {
    val totalCost: Int get() = pricePerNight * nights.coerceAtLeast(1) * roomsCount.coerceAtLeast(1)
}

data class IntercityTransportOption(
    val id: String,
    val mode: String, // Train, Bus, Flight
    val fromCity: String,
    val toCity: String,
    val operatorName: String,
    val serviceNumber: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val farePerPerson: Int,
    val rating: Double,
    val classInfo: String
)

data class SelectedTransportLeg(
    val fromCity: String,
    val toCity: String,
    val mode: String,
    val operatorName: String,
    val serviceNumber: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val farePerPerson: Int,
    val totalFare: Int
)

data class SelectedTaxiPlan(
    val taxiId: String,
    val taxiName: String,
    val vehicleType: String,
    val seatingCapacity: String,
    val isAc: Boolean,
    val pickupLocation: String,
    val travelDate: String,
    val passengers: Int,
    val estimatedFare: Int,
    val rating: Double,
    val facilities: List<String>,
    val isConfirmedBooking: Boolean = false,
    val bookingRef: String = ""
)

data class SmartDayActivity(
    val id: String,
    val time: String,
    val title: String,
    val subtitle: String,
    val category: String // Travel, Stay, Sightseeing, Food
)

data class SmartDaySchedule(
    val dayNumber: Int,
    val title: String,
    val cityName: String,
    val fromCity: String? = null,
    val toCity: String? = null,
    val hotelName: String? = null,
    val transportSummary: String? = null,
    val distanceKm: Int = 0,
    val estDuration: String = "4 hrs",
    val activities: List<SmartDayActivity> = emptyList()
)

data class SmartTripPlan(
    val id: String = "smart_trip_current",
    val currentStep: Int = 1,
    val status: String = "Draft", // Draft, Planned, Confirmed
    // Module 1: Trip Basics
    val tripName: String = "Rajasthan Royal Vacation",
    val startState: String = "Delhi NCR",
    val startCity: String = "New Delhi",
    val destState: String = "Rajasthan",
    val destCity: String = "Jaipur",
    val startDateMillis: Long = 1797033600000L, // Dec 2026 default
    val daysCount: Int = 5,
    val dateRangeText: String = "12 Dec – 16 Dec 2026 (5 Days)",
    val adults: Int = 2,
    val children: Int = 1,
    val skippedTravellers: Boolean = false,
    val approxBudget: Int = 45000,
    val travelCategory: String = "Family", // Family, Couple, Friends, Solo
    val travelPreference: String = "Standard", // Budget, Standard, Luxury
    // Module 2: Smart Destination Selection
    val selectionMode: String = "Known", // Known, Suggest
    val selectedStates: List<String> = listOf("Rajasthan"),
    val cityPlans: List<SmartCityPlan> = SmartTripCatalog.defaultCityPlans(),
    // Module 3: Smart Budget Planner
    val budgetMode: String = "Fixed", // Fixed, Range
    val fixedBudget: Int = 45000,
    val minBudget: Int = 35000,
    val maxBudget: Int = 60000,
    val accommodationBudget: Int = 16000,
    val transportationBudget: Int = 10000,
    val foodBudget: Int = 7500,
    val localTravelBudget: Int = 4500,
    val sightseeingBudget: Int = 4000,
    val miscBudget: Int = 3000,
    // Module 4: Smart Hotel Selection
    val selectedHotels: List<SelectedHotelPlan> = emptyList(),
    val skippedHotelCities: List<String> = emptyList(),
    // Module 5: Transportation Planner
    val selectedTransportLegs: List<SelectedTransportLeg> = emptyList(),
    val selectedTaxi: SelectedTaxiPlan? = null,
    // Module 7: Review & Editable Schedule
    val daySchedules: List<SmartDaySchedule> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalTravellers: Int get() = if (skippedTravellers) 1 else (adults + children).coerceAtLeast(1)
    val estimatedCategoryTotal: Int
        get() = accommodationBudget + transportationBudget + foodBudget + localTravelBudget + sightseeingBudget + miscBudget
    val perPersonCost: Int get() = estimatedCategoryTotal / totalTravellers
    val dailyCost: Int get() = estimatedCategoryTotal / daysCount.coerceAtLeast(1)
    val confirmedBookingTotal: Int
        get() = selectedHotels.filter { it.isConfirmedBooking }.sumOf { it.totalCost } +
            (selectedTaxi?.takeIf { it.isConfirmedBooking }?.estimatedFare ?: 0)
}

object SmartTripCatalog {

    val allStatesWithCities: Map<String, List<String>> = linkedMapOf(
        "Delhi NCR" to listOf("New Delhi", "Gurugram", "Noida"),
        "Rajasthan" to listOf("Jaipur", "Jodhpur", "Udaipur", "Jaisalmer", "Pushkar", "Ajmer", "Mount Abu"),
        "Madhya Pradesh" to listOf("Ujjain", "Indore", "Bhopal", "Khajuraho"),
        "Himachal Pradesh" to listOf("Shimla", "Manali", "Dharamshala", "Kasol", "Dalhousie"),
        "Uttarakhand" to listOf("Rishikesh", "Nainital", "Mussoorie", "Haridwar"),
        "Kerala" to listOf("Kochi", "Munnar", "Alleppey", "Thekkady", "Kovalam"),
        "Goa" to listOf("North Goa", "South Goa", "Panaji"),
        "Maharashtra" to listOf("Mumbai", "Pune", "Lonavala", "Mahabaleshwar"),
        "Uttar Pradesh" to listOf("Agra", "Varanasi", "Lucknow", "Ayodhya"),
        "Jammu & Kashmir" to listOf("Srinagar", "Gulmarg", "Pahalgam", "Jammu"),
        "Gujarat" to listOf("Ahmedabad", "Kutch", "Dwarka", "Somnath"),
        "Karnataka" to listOf("Bengaluru", "Mysuru", "Coorg", "Hampi"),
        "Tamil Nadu" to listOf("Chennai", "Ooty", "Madurai", "Kanyakumari")
    )

    val cityCoordinates: Map<String, SmartCityCoordinate> = listOf(
        SmartCityCoordinate("New Delhi", "Delhi NCR", 28.6139, 77.2090),
        SmartCityCoordinate("Gurugram", "Delhi NCR", 28.4595, 77.0266),
        SmartCityCoordinate("Noida", "Delhi NCR", 28.5355, 77.3910),
        SmartCityCoordinate("Jaipur", "Rajasthan", 26.9124, 75.7873),
        SmartCityCoordinate("Jodhpur", "Rajasthan", 26.2389, 73.0243),
        SmartCityCoordinate("Udaipur", "Rajasthan", 24.5854, 73.7125),
        SmartCityCoordinate("Jaisalmer", "Rajasthan", 26.9157, 70.9083),
        SmartCityCoordinate("Pushkar", "Rajasthan", 26.4897, 74.5511),
        SmartCityCoordinate("Ajmer", "Rajasthan", 26.4499, 74.6399),
        SmartCityCoordinate("Mount Abu", "Rajasthan", 24.5926, 72.7156),
        SmartCityCoordinate("Ujjain", "Madhya Pradesh", 23.1765, 75.7885),
        SmartCityCoordinate("Indore", "Madhya Pradesh", 22.7196, 75.8577),
        SmartCityCoordinate("Bhopal", "Madhya Pradesh", 23.2599, 77.4126),
        SmartCityCoordinate("Khajuraho", "Madhya Pradesh", 24.8318, 79.9199),
        SmartCityCoordinate("Shimla", "Himachal Pradesh", 31.1048, 77.1734),
        SmartCityCoordinate("Manali", "Himachal Pradesh", 32.2432, 77.1892),
        SmartCityCoordinate("Dharamshala", "Himachal Pradesh", 32.2190, 76.3234),
        SmartCityCoordinate("Kasol", "Himachal Pradesh", 32.0100, 77.3150),
        SmartCityCoordinate("Dalhousie", "Himachal Pradesh", 32.5387, 75.9710),
        SmartCityCoordinate("Rishikesh", "Uttarakhand", 30.0869, 78.2676),
        SmartCityCoordinate("Nainital", "Uttarakhand", 29.3919, 79.4542),
        SmartCityCoordinate("Mussoorie", "Uttarakhand", 30.4598, 78.0644),
        SmartCityCoordinate("Haridwar", "Uttarakhand", 29.9457, 78.1642),
        SmartCityCoordinate("Kochi", "Kerala", 9.9312, 76.2673),
        SmartCityCoordinate("Munnar", "Kerala", 10.0889, 77.0595),
        SmartCityCoordinate("Alleppey", "Kerala", 9.4981, 76.3388),
        SmartCityCoordinate("Thekkady", "Kerala", 9.6031, 77.1615),
        SmartCityCoordinate("Kovalam", "Kerala", 8.3988, 76.9820),
        SmartCityCoordinate("North Goa", "Goa", 15.5494, 73.7535),
        SmartCityCoordinate("South Goa", "Goa", 15.0100, 74.0200),
        SmartCityCoordinate("Panaji", "Goa", 15.4909, 73.8278),
        SmartCityCoordinate("Mumbai", "Maharashtra", 19.0760, 72.8777),
        SmartCityCoordinate("Pune", "Maharashtra", 18.5204, 73.8567),
        SmartCityCoordinate("Lonavala", "Maharashtra", 18.7557, 73.4091),
        SmartCityCoordinate("Mahabaleshwar", "Maharashtra", 17.9307, 73.6477),
        SmartCityCoordinate("Agra", "Uttar Pradesh", 27.1767, 78.0081),
        SmartCityCoordinate("Varanasi", "Uttar Pradesh", 25.3176, 82.9739),
        SmartCityCoordinate("Lucknow", "Uttar Pradesh", 26.8467, 80.9462),
        SmartCityCoordinate("Ayodhya", "Uttar Pradesh", 26.7922, 82.1998),
        SmartCityCoordinate("Srinagar", "Jammu & Kashmir", 34.0837, 74.7973),
        SmartCityCoordinate("Gulmarg", "Jammu & Kashmir", 34.0484, 74.3805),
        SmartCityCoordinate("Pahalgam", "Jammu & Kashmir", 34.0161, 75.3150),
        SmartCityCoordinate("Jammu", "Jammu & Kashmir", 32.7266, 74.8570),
        SmartCityCoordinate("Ahmedabad", "Gujarat", 23.0225, 72.5714),
        SmartCityCoordinate("Kutch", "Gujarat", 23.7337, 69.8597),
        SmartCityCoordinate("Dwarka", "Gujarat", 22.2442, 68.9685),
        SmartCityCoordinate("Somnath", "Gujarat", 20.8880, 70.4012),
        SmartCityCoordinate("Bengaluru", "Karnataka", 12.9716, 77.5946),
        SmartCityCoordinate("Mysuru", "Karnataka", 12.2958, 76.6394),
        SmartCityCoordinate("Coorg", "Karnataka", 12.3375, 75.8069),
        SmartCityCoordinate("Hampi", "Karnataka", 15.3350, 76.4600),
        SmartCityCoordinate("Chennai", "Tamil Nadu", 13.0827, 80.2707),
        SmartCityCoordinate("Ooty", "Tamil Nadu", 11.4102, 76.6950),
        SmartCityCoordinate("Madurai", "Tamil Nadu", 9.9252, 78.1198),
        SmartCityCoordinate("Kanyakumari", "Tamil Nadu", 8.0883, 77.5385)
    ).associateBy { it.cityName }

    fun getCoordinate(cityName: String): SmartCityCoordinate {
        return cityCoordinates[cityName] ?: SmartCityCoordinate(cityName, "India", 26.9124, 75.7873)
    }

    fun computeDistanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c * 10.0).roundToInt() / 10.0
    }

    fun cityRoadDistanceKm(fromCity: String, toCity: String): Int {
        val c1 = getCoordinate(fromCity)
        val c2 = getCoordinate(toCity)
        val air = computeDistanceKm(c1.lat, c1.lng, c2.lat, c2.lng)
        return (air * 1.22).roundToInt().coerceAtLeast(18)
    }

    val attractionsByCity: Map<String, List<SmartAttraction>> = mapOf(
        "Jaipur" to listOf(
            SmartAttraction("jpr_amer", "Jaipur", "Rajasthan", "Amer Fort", "Heritage Fort", 5, "3 hrs", "₹100", 26.9855, 75.8513, "Hilltop sandstone & marble citadel with Sheesh Mahal."),
            SmartAttraction("jpr_hawa", "Jaipur", "Rajasthan", "Hawa Mahal", "Royal Palace", 5, "1 hr", "₹50", 26.9239, 75.8267, "Iconic five-story pink honeycomb facade in Old City."),
            SmartAttraction("jpr_city", "Jaipur", "Rajasthan", "City Palace", "Royal Museum", 4, "2 hrs", "₹200", 26.9258, 75.8237, "Royal courtyards, Peacock Gate and museum galleries."),
            SmartAttraction("jpr_jantar", "Jaipur", "Rajasthan", "Jantar Mantar", "UNESCO Observatory", 4, "1 hr", "₹50", 26.9248, 75.8246, "Historic stone astronomical instruments & giant sundial."),
            SmartAttraction("jpr_nahar", "Jaipur", "Rajasthan", "Nahargarh Fort", "Sunset Viewpoint", 4, "2 hrs", "₹50", 26.9373, 75.8155, "Sweeping Aravalli ridge view overlooking Pink City."),
            SmartAttraction("jpr_jaigarh", "Jaipur", "Rajasthan", "Jaigarh Fort", "Fortress", 3, "1.5 hrs", "₹100", 26.9851, 75.8456, "Victory fort housing Jaivana cannon above Amer."),
            SmartAttraction("jpr_albert", "Jaipur", "Rajasthan", "Albert Hall Museum", "Museum", 3, "1.5 hrs", "₹50", 26.9116, 75.8194, "Indo-Saracenic museum lit up beautifully after dusk.")
        ),
        "Jodhpur" to listOf(
            SmartAttraction("jdh_mehran", "Jodhpur", "Rajasthan", "Mehrangarh Fort", "Heritage Fort", 5, "3 hrs", "₹200", 26.2981, 73.0185, "Towering fortress rising 400 ft above the Blue City."),
            SmartAttraction("jdh_jaswant", "Jodhpur", "Rajasthan", "Jaswant Thada", "Marble Cenotaph", 5, "1 hr", "₹50", 26.3040, 73.0253, "Peaceful white marble memorial beside a serene pond."),
            SmartAttraction("jdh_umaid", "Jodhpur", "Rajasthan", "Umaid Bhawan Palace", "Royal Palace", 4, "1.5 hrs", "₹100", 26.2809, 73.0472, "Art-deco royal palace & vintage car museum."),
            SmartAttraction("jdh_clock", "Jodhpur", "Rajasthan", "Clock Tower & Sardar Market", "Local Bazaar", 4, "1.5 hrs", "Free", 26.2947, 73.0241, "Bustling spice market famous for Mirchi Vada & textiles."),
            SmartAttraction("jdh_mandore", "Jodhpur", "Rajasthan", "Mandore Garden", "Historic Garden", 3, "1.5 hrs", "₹50", 26.3519, 73.0359, "Ancient Marwar cenotaphs surrounded by lush gardens.")
        ),
        "Udaipur" to listOf(
            SmartAttraction("udr_city", "Udaipur", "Rajasthan", "City Palace", "Royal Palace", 5, "2.5 hrs", "₹300", 24.5764, 73.6835, "Rajasthan's largest lakeside palace complex."),
            SmartAttraction("udr_pichola", "Udaipur", "Rajasthan", "Lake Pichola", "Scenic Lake", 5, "2 hrs", "₹400", 24.5720, 73.6790, "Iconic sunset boat rides framing Taj Lake Palace."),
            SmartAttraction("udr_jag", "Udaipur", "Rajasthan", "Jag Mandir", "Island Palace", 4, "1.5 hrs", "₹350", 24.5674, 73.6778, "Marble island pavilion accessible via Pichola ferry."),
            SmartAttraction("udr_saheli", "Udaipur", "Rajasthan", "Saheliyon Ki Bari", "Garden", 4, "1 hr", "₹30", 24.6029, 73.6852, "Lotus pools, marble fountains, and royal pavilions."),
            SmartAttraction("udr_fateh", "Udaipur", "Rajasthan", "Fateh Sagar Lake", "Lakeside Promenade", 3, "1.5 hrs", "Free", 24.6006, 73.6744, "Breezy evening promenade with Nehru Garden views.")
        ),
        "Jaisalmer" to listOf(
            SmartAttraction("jsm_fort", "Jaisalmer", "Rajasthan", "Sonar Quila (Jaisalmer Fort)", "Living Fort", 5, "2.5 hrs", "Free", 26.9126, 70.9126, "Golden sandstone UNESCO living fort."),
            SmartAttraction("jsm_sam", "Jaisalmer", "Rajasthan", "Sam Sand Dunes", "Thar Desert", 5, "4 hrs", "₹500", 26.8278, 70.5112, "Golden desert dunes with camel safari & folk dinner."),
            SmartAttraction("jsm_patwon", "Jaisalmer", "Rajasthan", "Patwon Ki Haveli", "Heritage Haveli", 4, "1.5 hrs", "₹100", 26.9163, 70.9143, "Intricately carved cluster of five historic havelis."),
            SmartAttraction("jsm_gadisar", "Jaisalmer", "Rajasthan", "Gadisar Lake", "Historic Lake", 3, "1 hr", "Free", 26.9065, 70.9225, "Tranquil 14th-century lake flanked by sandstone chhatris.")
        ),
        "Pushkar" to listOf(
            SmartAttraction("psk_brahma", "Pushkar", "Rajasthan", "Brahma Temple", "Sacred Temple", 5, "1 hr", "Free", 26.4872, 74.5515, "Rare sacred temple beside Pushkar Lake."),
            SmartAttraction("psk_lake", "Pushkar", "Rajasthan", "Pushkar Lake Ghats", "Spiritual Ghat", 5, "1.5 hrs", "Free", 26.4875, 74.5542, "52 bathing ghats glowing during evening aarti."),
            SmartAttraction("psk_savitri", "Pushkar", "Rajasthan", "Savitri Mata Ropeway", "Hill Viewpoint", 4, "1.5 hrs", "₹130", 26.4810, 74.5446, "Panoramic cable car ride for desert & lake sunsets.")
        ),
        "Ajmer" to listOf(
            SmartAttraction("ajm_dargah", "Ajmer", "Rajasthan", "Ajmer Sharif Dargah", "Shrine", 5, "1.5 hrs", "Free", 26.4561, 74.6279, "Revered Sufi shrine in the old bazaar quarter."),
            SmartAttraction("ajm_anasagar", "Ajmer", "Rajasthan", "Ana Sagar Lake", "Lakeside Park", 4, "1 hr", "Free", 26.4730, 74.6194, "Marble Baradari pavilions overlooking calm waters.")
        ),
        "Mount Abu" to listOf(
            SmartAttraction("abu_dilwara", "Mount Abu", "Rajasthan", "Dilwara Jain Temples", "Marble Temple", 5, "2 hrs", "Free", 24.6093, 72.7226, "Exquisite hand-carved white marble temple architecture."),
            SmartAttraction("abu_nakki", "Mount Abu", "Rajasthan", "Nakki Lake", "Hill Lake", 4, "1.5 hrs", "₹100", 24.5955, 72.7077, "Boating lake bordered by cafes and Toad Rock."),
            SmartAttraction("abu_sunset", "Mount Abu", "Rajasthan", "Sunset Point", "Scenic Ridge", 4, "1 hr", "Free", 24.5858, 72.6952, "Golden-hour valley view across Aravalli peaks.")
        ),
        "Kochi" to listOf(
            SmartAttraction("koc_nets", "Kochi", "Kerala", "Fort Kochi & Chinese Nets", "Coastal Heritage", 5, "2 hrs", "Free", 9.9675, 76.2416, "Iconic sunset fishing nets & colonial heritage lanes."),
            SmartAttraction("koc_mattancherry", "Kochi", "Kerala", "Mattancherry Palace & Jew Town", "Heritage", 4, "2 hrs", "₹25", 9.9583, 76.2594, "Murals, antique spice shops, and Paradesi Synagogue.")
        ),
        "Munnar" to listOf(
            SmartAttraction("mnr_tea", "Munnar", "Kerala", "Eravikulam & Tea Plantations", "Hills & Nature", 5, "3 hrs", "₹200", 10.1972, 77.0568, "Rolling emerald tea hills & Nilgiri Tahr reserve."),
            SmartAttraction("mnr_mattupetty", "Munnar", "Kerala", "Mattupetty Dam & Echo Point", "Scenic Valley", 4, "2 hrs", "₹50", 10.1066, 77.1235, "Misty reservoir and lakeside pine forests.")
        ),
        "Alleppey" to listOf(
            SmartAttraction("alp_backwater", "Alleppey", "Kerala", "Vembanad Backwaters Cruise", "Houseboat", 5, "4 hrs", "₹800", 9.4981, 76.3388, "Calm palm-lined canals via traditional kettuvallam boat."),
            SmartAttraction("alp_beach", "Alleppey", "Kerala", "Marari & Alappuzha Beach", "Coast", 4, "1.5 hrs", "Free", 9.4926, 76.3177, "Golden sands and historic pier sunset views.")
        ),
        "North Goa" to listOf(
            SmartAttraction("goa_baga", "North Goa", "Goa", "Calangute & Baga Coast", "Beach", 5, "3 hrs", "Free", 15.5527, 73.7517, "Lively water sports, shacks, and sunset shore."),
            SmartAttraction("goa_aguada", "North Goa", "Goa", "Fort Aguada", "Coastal Fort", 4, "1.5 hrs", "₹25", 15.4920, 73.7737, "17th-century Portuguese fort overlooking the Arabian Sea."),
            SmartAttraction("goa_chapora", "North Goa", "Goa", "Chapora Fort & Vagator", "Viewpoint", 4, "1.5 hrs", "Free", 15.6054, 73.7366, "Clifftop panorama over Vagator palms and sea.")
        ),
        "South Goa" to listOf(
            SmartAttraction("goa_palolem", "South Goa", "Goa", "Palolem Crescent Bay", "Beach", 5, "3 hrs", "Free", 15.0100, 74.0232, "Calm palm-fringed bay ideal for kayaking."),
            SmartAttraction("goa_caboderama", "South Goa", "Goa", "Cabo de Rama Fort", "Coastal Fort", 4, "1.5 hrs", "Free", 15.0884, 73.9212, "Dramatic cliffside fort above pristine waters.")
        ),
        "Shimla" to listOf(
            SmartAttraction("shl_ridge", "Shimla", "Himachal Pradesh", "The Ridge & Mall Road", "Heritage Walk", 5, "2.5 hrs", "Free", 31.1049, 77.1754, "Colonial promenade, Christ Church, and mountain cafes."),
            SmartAttraction("shl_jakhoo", "Shimla", "Himachal Pradesh", "Jakhoo Hill & Ropeway", "Hilltop Temple", 4, "1.5 hrs", "₹250", 31.1008, 77.1845, "Highest peak in Shimla framed by cedar forests.")
        ),
        "Manali" to listOf(
            SmartAttraction("mnl_solang", "Manali", "Himachal Pradesh", "Solang Valley & Atal Tunnel", "Snow & Adventure", 5, "4 hrs", "Free", 32.3168, 77.1574, "Alpine valley views, cable car, and snow activities."),
            SmartAttraction("mnl_hadimba", "Manali", "Himachal Pradesh", "Hadimba Devi Temple", "Cedar Forest Temple", 5, "1.5 hrs", "Free", 32.2484, 77.1806, "16th-century pagoda temple amidst giant deodar trees."),
            SmartAttraction("mnl_old", "Manali", "Himachal Pradesh", "Old Manali & Vashisht", "Village & Springs", 4, "2 hrs", "Free", 32.2571, 77.1763, "Riverside cafes, wooden houses, and hot springs.")
        ),
        "Rishikesh" to listOf(
            SmartAttraction("rsk_triveni", "Rishikesh", "Uttarakhand", "Triveni Ghat Evening Aarti", "Spiritual Ghat", 5, "1.5 hrs", "Free", 30.1043, 78.2974, "Soulful Ganga Aarti with lamps along the river."),
            SmartAttraction("rsk_jhula", "Rishikesh", "Uttarakhand", "Ram Jhula & Beatles Ashram", "Riverside Trail", 4, "2 hrs", "₹150", 30.1235, 78.3137, "Iconic suspension bridges and heritage forest ashram.")
        ),
        "Agra" to listOf(
            SmartAttraction("agr_taj", "Agra", "Uttar Pradesh", "Taj Mahal", "Wonder of the World", 5, "3 hrs", "₹50", 27.1751, 78.0421, "Iconic ivory-white marble mausoleum on the Yamuna."),
            SmartAttraction("agr_fort", "Agra", "Uttar Pradesh", "Agra Fort", "UNESCO Fort", 5, "2 hrs", "₹50", 27.1795, 78.0211, "Red sandstone imperial palace overlooking the Taj.")
        ),
        "Varanasi" to listOf(
            SmartAttraction("vns_dasha", "Varanasi", "Uttar Pradesh", "Dashashwamedh Ghat Aarti", "River Ghat", 5, "2 hrs", "Free", 25.3069, 83.0104, "Grand evening Ganga Aarti from ghats or wooden boat."),
            SmartAttraction("vns_kashi", "Varanasi", "Uttar Pradesh", "Kashi Vishwanath Corridor", "Sacred Temple", 5, "2 hrs", "Free", 25.3109, 83.0107, "Historic temple complex connected directly to the ghats.")
        )
    )

    fun attractionsForCity(cityName: String, stateName: String): List<SmartAttraction> {
        val exact = attractionsByCity[cityName]
        if (exact != null) return exact
        val coord = getCoordinate(cityName)
        return listOf(
            SmartAttraction("${cityName.lowercase()}_1", cityName, stateName, "$cityName Heritage Landmark", "Must-See Landmark", 5, "2 hrs", "₹50", coord.lat + 0.012, coord.lng + 0.009, "Top iconic cultural monument in $cityName."),
            SmartAttraction("${cityName.lowercase()}_2", cityName, stateName, "$cityName Scenic Viewpoint", "Nature & Views", 4, "1.5 hrs", "Free", coord.lat - 0.014, coord.lng + 0.015, "Popular sunset and panoramic sightseeing spot."),
            SmartAttraction("${cityName.lowercase()}_3", cityName, stateName, "$cityName Old Bazaar & Food Street", "Local Culture", 4, "2 hrs", "Free", coord.lat + 0.006, coord.lng - 0.011, "Famous local crafts, street snacks, and evening stroll."),
            SmartAttraction("${cityName.lowercase()}_4", cityName, stateName, "$cityName City Museum & Park", "Museum", 3, "1.5 hrs", "₹40", coord.lat - 0.008, coord.lng - 0.014, "Curated regional history and landscaped gardens.")
        )
    }

    fun defaultCityPlans(): List<SmartCityPlan> {
        return listOf(
            SmartCityPlan(
                cityName = "Jaipur",
                stateName = "Rajasthan",
                isExpanded = true,
                attractions = attractionsForCity("Jaipur", "Rajasthan").take(5)
            ),
            SmartCityPlan(
                cityName = "Jodhpur",
                stateName = "Rajasthan",
                isExpanded = false,
                attractions = attractionsForCity("Jodhpur", "Rajasthan").take(3)
            )
        )
    }

    fun hotelsForCity(cityName: String, stateName: String, registered: List<SmartHotelItem> = emptyList()): List<SmartHotelItem> {
        val customMatches = registered.filter { it.cityName.equals(cityName, ignoreCase = true) }
        val coord = getCoordinate(cityName)
        val fromCatalog = CatalogData.hotels
            .filter { it.city.equals(cityName, ignoreCase = true) }
            .mapIndexed { idx, h ->
                SmartHotelItem(
                    id = h.id,
                    stateName = h.stateName,
                    cityName = h.city,
                    name = h.name,
                    description = h.areaInfo,
                    address = "${h.areaInfo}, ${h.city}",
                    starCategory = if (h.pricePerNight >= 3500) 5 else if (h.pricePerNight >= 2200) 4 else 3,
                    rating = h.rating,
                    pricePerNight = h.pricePerNight,
                    isAc = true,
                    hasParking = idx % 2 == 0 || h.facilities.any { it.contains("Parking", true) },
                    hasBreakfast = h.facilities.any { it.contains("Breakfast", true) } || h.rooms.any { it.details.contains("Breakfast", true) },
                    familyFriendly = h.category != "Budget" || idx % 2 == 0,
                    friendsFriendly = true,
                    facilities = h.facilities,
                    roomTypes = h.rooms,
                    cancellationPolicy = h.policies.firstOrNull() ?: "Free cancellation up to 24 hrs",
                    lat = coord.lat + (idx - 1) * 0.011,
                    lng = coord.lng + (idx % 3 - 1) * 0.013,
                    colorKey = h.colorKey
                )
            }

        val templates = listOf(
            Triple("Royal Courtyard Haveli", "Heritage Old City", 3400) to listOf(true, true, true, true, true, 5, 4.7, "amber"),
            Triple("Grand Heritage Suites", "Central Avenue", 4200) to listOf(true, true, true, true, true, 5, 4.8, "indigo"),
            Triple("Palm View Boutique Stay", "Civil Lines", 2650) to listOf(true, true, true, true, true, 4, 4.6, "coral"),
            Triple("Sunview Urban Inn", "Station Road", 1850) to listOf(true, false, true, true, true, 3, 4.3, "green"),
            Triple("Saffron Terrace Residency", "Lake & Fort District", 2950) to listOf(true, true, true, true, false, 4, 4.5, "sky"),
            Triple("Aravalli Bliss Resort", "Quiet Garden Enclave", 4800) to listOf(true, true, true, true, true, 5, 4.9, "plum"),
            Triple("Backpacker & Friends Loft", "Café Street", 1250) to listOf(false, false, false, false, true, 3, 4.2, "green"),
            Triple("Lotus Mahal Regency", "Heritage Promenade", 3150) to listOf(true, true, true, true, false, 4, 4.5, "amber"),
            Triple("Cityscape Comfort Express", "Metro & Hub Link", 1950) to listOf(true, true, false, true, true, 3, 4.1, "indigo"),
            Triple("Marigold Family Resort", "Park Avenue", 2500) to listOf(true, true, true, true, false, 4, 4.4, "coral"),
            Triple("Imperial Pearl Palace", "Palace Gate Road", 5600) to listOf(true, true, true, true, true, 5, 4.9, "plum"),
            Triple("Silver Oak Homestay", "Quiet Residential Lane", 1600) to listOf(false, true, true, true, true, 3, 4.3, "sky")
        )

        val generated = templates.mapIndexed { idx, (info, meta) ->
            val (suffixName, area, price) = info
            val ac = meta[0] as Boolean
            val parking = meta[1] as Boolean
            val breakfast = meta[2] as Boolean
            val family = meta[3] as Boolean
            val friends = meta[4] as Boolean
            val stars = meta[5] as Int
            val rating = meta[6] as Double
            val colorKey = meta[7] as String
            val latOffset = ((idx % 4) - 1.5) * 0.014
            val lngOffset = (((idx / 4) % 4) - 1.5) * 0.015
            SmartHotelItem(
                id = "gen_${cityName.lowercase().replace(" ", "_")}_$idx",
                stateName = stateName,
                cityName = cityName,
                name = "$cityName $suffixName",
                description = "$area · Convenient stay near key attractions in $cityName.",
                address = "$area, $cityName, $stateName",
                starCategory = stars,
                rating = rating,
                pricePerNight = price,
                isAc = ac,
                hasParking = parking,
                hasBreakfast = breakfast,
                familyFriendly = family,
                friendsFriendly = friends,
                facilities = buildList {
                    add(if (ac) "AC Rooms" else "Air Cooled")
                    if (parking) add("Free Parking")
                    if (breakfast) add("Breakfast Included")
                    add("Free Wi-Fi")
                    add("24x7 Support")
                },
                roomTypes = listOf(
                    HotelRoomOption("Deluxe Room", "${if (ac) "AC" else "Non-AC"} · Wi-Fi", price),
                    HotelRoomOption("Family Suite", "Spacious 4-guest suite${if (breakfast) " · Breakfast" else ""}", price + 1200)
                ),
                cancellationPolicy = "Free cancellation up to 24 hrs before check-in. No prepayment until booking confirmation.",
                lat = coord.lat + latOffset,
                lng = coord.lng + lngOffset,
                colorKey = colorKey
            )
        }

        val combined = customMatches + fromCatalog + generated
        return combined.distinctBy { it.name }.take(14)
    }

    fun intercityOptionsForLeg(fromCity: String, toCity: String): List<IntercityTransportOption> {
        val dist = cityRoadDistanceKm(fromCity, toCity)
        val trainMins = (dist / 62.0 * 60).roundToInt().coerceAtLeast(70)
        val busMins = (dist / 50.0 * 60).roundToInt().coerceAtLeast(90)
        val flightMins = (dist / 550.0 * 60 + 55).roundToInt().coerceAtLeast(65)

        fun fmtDur(mins: Int): String = "${mins / 60}h ${mins % 60}m"

        return listOf(
            // Trains
            IntercityTransportOption(
                id = "trn_${fromCity}_${toCity}_1", mode = "Train",
                fromCity = fromCity, toCity = toCity,
                operatorName = "Vande Bharat / Shatabdi Express", serviceNumber = "12462-VB",
                departureTime = "07:15 AM", arrivalTime = "11:45 AM",
                durationText = fmtDur(trainMins), farePerPerson = (dist * 2.6).roundToInt().coerceIn(450, 2600),
                rating = 4.7, classInfo = "AC Chair Car / Executive"
            ),
            IntercityTransportOption(
                id = "trn_${fromCity}_${toCity}_2", mode = "Train",
                fromCity = fromCity, toCity = toCity,
                operatorName = "Superfast Intercity Express", serviceNumber = "14814-SF",
                departureTime = "02:30 PM", arrivalTime = "08:10 PM",
                durationText = fmtDur((trainMins * 1.18).roundToInt()), farePerPerson = (dist * 1.6).roundToInt().coerceIn(280, 1750),
                rating = 4.4, classInfo = "3AC / Sleeper"
            ),
            // Buses
            IntercityTransportOption(
                id = "bus_${fromCity}_${toCity}_1", mode = "Bus",
                fromCity = fromCity, toCity = toCity,
                operatorName = "Volvo AC Multi-Axle Sleeper", serviceNumber = "RJ-VOL-18",
                departureTime = "08:30 AM", arrivalTime = "02:15 PM",
                durationText = fmtDur(busMins), farePerPerson = (dist * 2.1).roundToInt().coerceIn(380, 1900),
                rating = 4.5, classInfo = "AC Seater / Wi-Fi"
            ),
            IntercityTransportOption(
                id = "bus_${fromCity}_${toCity}_2", mode = "Bus",
                fromCity = fromCity, toCity = toCity,
                operatorName = "State Tourism Goldline AC", serviceNumber = "ST-GL-42",
                departureTime = "10:00 PM", arrivalTime = "05:30 AM",
                durationText = fmtDur((busMins * 1.1).roundToInt()), farePerPerson = (dist * 1.5).roundToInt().coerceIn(300, 1450),
                rating = 4.2, classInfo = "AC Sleeper Berth"
            ),
            // Flights
            IntercityTransportOption(
                id = "flt_${fromCity}_${toCity}_1", mode = "Flight",
                fromCity = fromCity, toCity = toCity,
                operatorName = "IndiGo Regional Direct", serviceNumber = "6E-7214",
                departureTime = "09:40 AM", arrivalTime = "10:55 AM",
                durationText = fmtDur(flightMins), farePerPerson = (dist * 8.5 + 2400).roundToInt().coerceIn(3200, 7800),
                rating = 4.6, classInfo = "Economy · Cabin 7kg + 15kg"
            ),
            IntercityTransportOption(
                id = "flt_${fromCity}_${toCity}_2", mode = "Flight",
                fromCity = fromCity, toCity = toCity,
                operatorName = "Air India Express", serviceNumber = "IX-1182",
                departureTime = "05:15 PM", arrivalTime = "06:35 PM",
                durationText = fmtDur(flightMins + 10), farePerPerson = (dist * 9.2 + 2700).roundToInt().coerceIn(3600, 8600),
                rating = 4.5, classInfo = "Economy Flex · Meal Included"
            )
        )
    }

    fun generateDaySchedules(plan: SmartTripPlan): List<SmartDaySchedule> {
        val cities = plan.cityPlans.ifEmpty { defaultCityPlans() }
        val totalDays = plan.daysCount.coerceAtLeast(cities.size)
        val hotelMap = plan.selectedHotels.associateBy { it.cityName }
        val legMap = plan.selectedTransportLegs.associateBy { "${it.fromCity}->${it.toCity}" }

        // Allocate days across ordered cities
        val cityForDay = mutableListOf<Int>()
        for (d in 0 until totalDays) {
            val idx = ((d.toDouble() / totalDays) * cities.size).toInt().coerceIn(0, cities.lastIndex)
            cityForDay.add(idx)
        }

        // Track remaining attractions per city
        val remainingAttractions = cities.map { it.attractions.toMutableList() }

        return (0 until totalDays).map { dayIdx ->
            val dayNum = dayIdx + 1
            val cIdx = cityForDay[dayIdx]
            val currentCity = cities[cIdx]
            val prevCity = if (dayIdx > 0) cities[cityForDay[dayIdx - 1]] else null
            val isTransition = prevCity != null && prevCity.cityName != currentCity.cityName
            val isArrivalDay = dayIdx == 0
            val hotel = hotelMap[currentCity.cityName]

            val activities = mutableListOf<SmartDayActivity>()
            var distKm = 18
            var estDur = "5 hrs"
            var transportSummary: String? = null

            if (isArrivalDay) {
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_arr",
                        time = "09:00 AM",
                        title = "Arrival at ${currentCity.cityName}",
                        subtitle = "From ${plan.startCity} · ${plan.travelCategory} trip begins",
                        category = "Travel"
                    )
                )
            } else if (isTransition && prevCity != null) {
                val leg = legMap["${prevCity.cityName}->${currentCity.cityName}"]
                distKm = cityRoadDistanceKm(prevCity.cityName, currentCity.cityName)
                estDur = leg?.durationText ?: "${(distKm / 55).coerceAtLeast(2)} hrs"
                transportSummary = if (leg != null) "${leg.mode} · ${leg.operatorName} (${leg.departureTime})" else "Intercity Route ($distKm km)"
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_chkout",
                        time = "08:30 AM",
                        title = "Hotel Check-out · ${prevCity.cityName}",
                        subtitle = hotelMap[prevCity.cityName]?.hotelName ?: "Morning checkout",
                        category = "Stay"
                    )
                )
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_transit",
                        time = "09:30 AM",
                        title = "Travel ${prevCity.cityName} → ${currentCity.cityName}",
                        subtitle = transportSummary,
                        category = "Travel"
                    )
                )
            }

            if ((isArrivalDay || isTransition) && hotel != null) {
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_chkin",
                        time = if (isTransition) "01:30 PM" else "11:00 AM",
                        title = "Check-in: ${hotel.hotelName}",
                        subtitle = "${hotel.roomType} · ${hotel.roomsCount} room(s)",
                        category = "Stay"
                    )
                )
            }

            // Pick attractions for this day
            val cityDaysTotal = cityForDay.count { it == cIdx }.coerceAtLeast(1)
            val remainingList = remainingAttractions[cIdx]
            val cityDayPos = cityForDay.subList(0, dayIdx + 1).count { it == cIdx }
            val countToPick = if (cityDayPos == cityDaysTotal) remainingList.size
            else (remainingList.size / (cityDaysTotal - cityDayPos + 1).coerceAtLeast(1)).coerceAtLeast(1)

            val slots = listOf("10:30 AM", "01:30 PM", "04:00 PM", "06:30 PM")
            for (k in 0 until countToPick.coerceAtMost(remainingList.size)) {
                val spot = remainingList.removeAt(0)
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_${spot.id}",
                        time = slots.getOrElse(k) { "05:00 PM" },
                        title = spot.name,
                        subtitle = "${spot.category} · ${spot.duration} · Entry ${spot.entryFee}",
                        category = "Sightseeing"
                    )
                )
            }

            if (activities.none { it.category == "Sightseeing" }) {
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_explore",
                        time = "04:30 PM",
                        title = "${currentCity.cityName} Local Sightseeing & Bazaar",
                        subtitle = "Evening heritage walk and local food tasting",
                        category = "Food"
                    )
                )
            } else {
                activities.add(
                    SmartDayActivity(
                        id = "act_${dayNum}_dinner",
                        time = "08:00 PM",
                        title = "Local Dining in ${currentCity.cityName}",
                        subtitle = "${plan.travelPreference} preference · Regional specialties",
                        category = "Food"
                    )
                )
            }

            val dayTitle = if (isTransition && prevCity != null) {
                "DAY $dayNum – ${prevCity.cityName} to ${currentCity.cityName}"
            } else {
                "DAY $dayNum – ${currentCity.cityName}"
            }

            SmartDaySchedule(
                dayNumber = dayNum,
                title = dayTitle,
                cityName = currentCity.cityName,
                fromCity = if (isTransition) prevCity?.cityName else null,
                toCity = if (isTransition) currentCity.cityName else null,
                hotelName = hotel?.hotelName,
                transportSummary = transportSummary,
                distanceKm = distKm,
                estDuration = estDur,
                activities = activities
            )
        }
    }

    fun recalculateBudgets(plan: SmartTripPlan): SmartTripPlan {
        val prefMultiplier = when (plan.travelPreference) {
            "Luxury" -> 1.65
            "Budget" -> 0.72
            else -> 1.0
        }
        val people = plan.totalTravellers
        val days = plan.daysCount.coerceAtLeast(1)

        val hotelSum = if (plan.selectedHotels.isNotEmpty()) {
            plan.selectedHotels.sumOf { it.totalCost }
        } else {
            ((days - 1).coerceAtLeast(1) * 2400 * prefMultiplier * ceil(people / 2.0)).roundToInt()
        }

        val intercitySum = plan.selectedTransportLegs.sumOf { it.totalFare }
        val taxiSum = plan.selectedTaxi?.estimatedFare ?: 0
        val transportCalc = if (intercitySum + taxiSum > 0) {
            intercitySum + taxiSum
        } else {
            (plan.cityPlans.size.coerceAtLeast(1) * 2200 * people * prefMultiplier).roundToInt()
        }

        val foodCalc = (days * 700 * people * prefMultiplier).roundToInt()
        val localCalc = (days * 550 * prefMultiplier).roundToInt()
        val ticketsCalc = plan.cityPlans.sumOf { it.attractions.size }.coerceAtLeast(3) * 180 * people
        val miscCalc = (days * 450 * prefMultiplier).roundToInt()

        return plan.copy(
            accommodationBudget = hotelSum,
            transportationBudget = transportCalc,
            foodBudget = foodCalc,
            localTravelBudget = localCalc,
            sightseeingBudget = ticketsCalc,
            miscBudget = miscCalc
        )
    }
}

object SmartTripSerializer {

    fun toJson(plan: SmartTripPlan): String {
        val obj = JSONObject()
        obj.put("id", plan.id)
        obj.put("currentStep", plan.currentStep)
        obj.put("status", plan.status)
        obj.put("tripName", plan.tripName)
        obj.put("startState", plan.startState)
        obj.put("startCity", plan.startCity)
        obj.put("destState", plan.destState)
        obj.put("destCity", plan.destCity)
        obj.put("startDateMillis", plan.startDateMillis)
        obj.put("daysCount", plan.daysCount)
        obj.put("dateRangeText", plan.dateRangeText)
        obj.put("adults", plan.adults)
        obj.put("children", plan.children)
        obj.put("skippedTravellers", plan.skippedTravellers)
        obj.put("approxBudget", plan.approxBudget)
        obj.put("travelCategory", plan.travelCategory)
        obj.put("travelPreference", plan.travelPreference)
        obj.put("selectionMode", plan.selectionMode)
        obj.put("selectedStates", JSONArray(plan.selectedStates))
        obj.put("budgetMode", plan.budgetMode)
        obj.put("fixedBudget", plan.fixedBudget)
        obj.put("minBudget", plan.minBudget)
        obj.put("maxBudget", plan.maxBudget)
        obj.put("accommodationBudget", plan.accommodationBudget)
        obj.put("transportationBudget", plan.transportationBudget)
        obj.put("foodBudget", plan.foodBudget)
        obj.put("localTravelBudget", plan.localTravelBudget)
        obj.put("sightseeingBudget", plan.sightseeingBudget)
        obj.put("miscBudget", plan.miscBudget)
        obj.put("skippedHotelCities", JSONArray(plan.skippedHotelCities))
        obj.put("updatedAt", plan.updatedAt)

        val citiesArr = JSONArray()
        plan.cityPlans.forEach { cp ->
            val cpObj = JSONObject().apply {
                put("cityName", cp.cityName)
                put("stateName", cp.stateName)
                put("isExpanded", cp.isExpanded)
                val attrArr = JSONArray()
                cp.attractions.forEach { a ->
                    attrArr.put(JSONObject().apply {
                        put("id", a.id)
                        put("cityName", a.cityName)
                        put("stateName", a.stateName)
                        put("name", a.name)
                        put("category", a.category)
                        put("priorityStars", a.priorityStars)
                        put("duration", a.duration)
                        put("entryFee", a.entryFee)
                        put("lat", a.lat)
                        put("lng", a.lng)
                        put("description", a.description)
                        put("isCustom", a.isCustom)
                    })
                }
                put("attractions", attrArr)
            }
            citiesArr.put(cpObj)
        }
        obj.put("cityPlans", citiesArr)

        val hotelsArr = JSONArray()
        plan.selectedHotels.forEach { h ->
            hotelsArr.put(JSONObject().apply {
                put("cityName", h.cityName)
                put("hotelId", h.hotelId)
                put("hotelName", h.hotelName)
                put("roomType", h.roomType)
                put("pricePerNight", h.pricePerNight)
                put("checkInDate", h.checkInDate)
                put("checkOutDate", h.checkOutDate)
                put("nights", h.nights)
                put("roomsCount", h.roomsCount)
                put("guestsCount", h.guestsCount)
                put("cancellationPolicy", h.cancellationPolicy)
                put("lat", h.lat)
                put("lng", h.lng)
                put("isConfirmedBooking", h.isConfirmedBooking)
                put("bookingRef", h.bookingRef)
            })
        }
        obj.put("selectedHotels", hotelsArr)

        val legsArr = JSONArray()
        plan.selectedTransportLegs.forEach { l ->
            legsArr.put(JSONObject().apply {
                put("fromCity", l.fromCity)
                put("toCity", l.toCity)
                put("mode", l.mode)
                put("operatorName", l.operatorName)
                put("serviceNumber", l.serviceNumber)
                put("departureTime", l.departureTime)
                put("arrivalTime", l.arrivalTime)
                put("durationText", l.durationText)
                put("farePerPerson", l.farePerPerson)
                put("totalFare", l.totalFare)
            })
        }
        obj.put("selectedTransportLegs", legsArr)

        plan.selectedTaxi?.let { t ->
            obj.put("selectedTaxi", JSONObject().apply {
                put("taxiId", t.taxiId)
                put("taxiName", t.taxiName)
                put("vehicleType", t.vehicleType)
                put("seatingCapacity", t.seatingCapacity)
                put("isAc", t.isAc)
                put("pickupLocation", t.pickupLocation)
                put("travelDate", t.travelDate)
                put("passengers", t.passengers)
                put("estimatedFare", t.estimatedFare)
                put("rating", t.rating)
                put("facilities", JSONArray(t.facilities))
                put("isConfirmedBooking", t.isConfirmedBooking)
                put("bookingRef", t.bookingRef)
            })
        }

        val daysArr = JSONArray()
        plan.daySchedules.forEach { d ->
            daysArr.put(JSONObject().apply {
                put("dayNumber", d.dayNumber)
                put("title", d.title)
                put("cityName", d.cityName)
                if (d.fromCity != null) put("fromCity", d.fromCity)
                if (d.toCity != null) put("toCity", d.toCity)
                if (d.hotelName != null) put("hotelName", d.hotelName)
                if (d.transportSummary != null) put("transportSummary", d.transportSummary)
                put("distanceKm", d.distanceKm)
                put("estDuration", d.estDuration)
                val actsArr = JSONArray()
                d.activities.forEach { act ->
                    actsArr.put(JSONObject().apply {
                        put("id", act.id)
                        put("time", act.time)
                        put("title", act.title)
                        put("subtitle", act.subtitle)
                        put("category", act.category)
                    })
                }
                put("activities", actsArr)
            })
        }
        obj.put("daySchedules", daysArr)

        return obj.toString()
    }

    fun fromJson(json: String): SmartTripPlan? {
        if (json.isBlank()) return null
        return runCatching {
            val obj = JSONObject(json)
            val statesArr = obj.optJSONArray("selectedStates") ?: JSONArray()
            val states = List(statesArr.length()) { statesArr.optString(it) }.filter { it.isNotBlank() }

            val skippedHotelsArr = obj.optJSONArray("skippedHotelCities") ?: JSONArray()
            val skippedHotels = List(skippedHotelsArr.length()) { skippedHotelsArr.optString(it) }.filter { it.isNotBlank() }

            val cpArr = obj.optJSONArray("cityPlans") ?: JSONArray()
            val cityPlans = List(cpArr.length()) { i ->
                val cp = cpArr.getJSONObject(i)
                val attrArr = cp.optJSONArray("attractions") ?: JSONArray()
                val attractions = List(attrArr.length()) { j ->
                    val a = attrArr.getJSONObject(j)
                    SmartAttraction(
                        id = a.optString("id"),
                        cityName = a.optString("cityName"),
                        stateName = a.optString("stateName"),
                        name = a.optString("name"),
                        category = a.optString("category"),
                        priorityStars = a.optInt("priorityStars", 4),
                        duration = a.optString("duration", "2 hrs"),
                        entryFee = a.optString("entryFee", "Free"),
                        lat = a.optDouble("lat", 26.9124),
                        lng = a.optDouble("lng", 75.7873),
                        description = a.optString("description"),
                        isCustom = a.optBoolean("isCustom", false)
                    )
                }
                SmartCityPlan(
                    cityName = cp.optString("cityName"),
                    stateName = cp.optString("stateName"),
                    isExpanded = cp.optBoolean("isExpanded", true),
                    attractions = attractions
                )
            }

            val hArr = obj.optJSONArray("selectedHotels") ?: JSONArray()
            val selectedHotels = List(hArr.length()) { i ->
                val h = hArr.getJSONObject(i)
                SelectedHotelPlan(
                    cityName = h.optString("cityName"),
                    hotelId = h.optString("hotelId"),
                    hotelName = h.optString("hotelName"),
                    roomType = h.optString("roomType"),
                    pricePerNight = h.optInt("pricePerNight", 2500),
                    checkInDate = h.optString("checkInDate"),
                    checkOutDate = h.optString("checkOutDate"),
                    nights = h.optInt("nights", 1),
                    roomsCount = h.optInt("roomsCount", 1),
                    guestsCount = h.optInt("guestsCount", 2),
                    cancellationPolicy = h.optString("cancellationPolicy"),
                    lat = h.optDouble("lat", 26.9124),
                    lng = h.optDouble("lng", 75.7873),
                    isConfirmedBooking = h.optBoolean("isConfirmedBooking", false),
                    bookingRef = h.optString("bookingRef")
                )
            }

            val lArr = obj.optJSONArray("selectedTransportLegs") ?: JSONArray()
            val legs = List(lArr.length()) { i ->
                val l = lArr.getJSONObject(i)
                SelectedTransportLeg(
                    fromCity = l.optString("fromCity"),
                    toCity = l.optString("toCity"),
                    mode = l.optString("mode"),
                    operatorName = l.optString("operatorName"),
                    serviceNumber = l.optString("serviceNumber"),
                    departureTime = l.optString("departureTime"),
                    arrivalTime = l.optString("arrivalTime"),
                    durationText = l.optString("durationText"),
                    farePerPerson = l.optInt("farePerPerson"),
                    totalFare = l.optInt("totalFare")
                )
            }

            val taxi = obj.optJSONObject("selectedTaxi")?.let { t ->
                val fArr = t.optJSONArray("facilities") ?: JSONArray()
                SelectedTaxiPlan(
                    taxiId = t.optString("taxiId"),
                    taxiName = t.optString("taxiName"),
                    vehicleType = t.optString("vehicleType"),
                    seatingCapacity = t.optString("seatingCapacity"),
                    isAc = t.optBoolean("isAc", true),
                    pickupLocation = t.optString("pickupLocation"),
                    travelDate = t.optString("travelDate"),
                    passengers = t.optInt("passengers", 2),
                    estimatedFare = t.optInt("estimatedFare"),
                    rating = t.optDouble("rating", 4.7),
                    facilities = List(fArr.length()) { fArr.optString(it) },
                    isConfirmedBooking = t.optBoolean("isConfirmedBooking", false),
                    bookingRef = t.optString("bookingRef")
                )
            }

            val dArr = obj.optJSONArray("daySchedules") ?: JSONArray()
            val schedules = List(dArr.length()) { i ->
                val d = dArr.getJSONObject(i)
                val aArr = d.optJSONArray("activities") ?: JSONArray()
                val acts = List(aArr.length()) { j ->
                    val act = aArr.getJSONObject(j)
                    SmartDayActivity(
                        id = act.optString("id"),
                        time = act.optString("time"),
                        title = act.optString("title"),
                        subtitle = act.optString("subtitle"),
                        category = act.optString("category")
                    )
                }
                SmartDaySchedule(
                    dayNumber = d.optInt("dayNumber", i + 1),
                    title = d.optString("title"),
                    cityName = d.optString("cityName"),
                    fromCity = d.optString("fromCity").takeIf { it.isNotBlank() },
                    toCity = d.optString("toCity").takeIf { it.isNotBlank() },
                    hotelName = d.optString("hotelName").takeIf { it.isNotBlank() },
                    transportSummary = d.optString("transportSummary").takeIf { it.isNotBlank() },
                    distanceKm = d.optInt("distanceKm"),
                    estDuration = d.optString("estDuration", "4 hrs"),
                    activities = acts
                )
            }

            SmartTripPlan(
                id = obj.optString("id", "smart_trip_current"),
                currentStep = obj.optInt("currentStep", 1),
                status = obj.optString("status", "Draft"),
                tripName = obj.optString("tripName", "Rajasthan Royal Vacation"),
                startState = obj.optString("startState", "Delhi NCR"),
                startCity = obj.optString("startCity", "New Delhi"),
                destState = obj.optString("destState", "Rajasthan"),
                destCity = obj.optString("destCity", "Jaipur"),
                startDateMillis = obj.optLong("startDateMillis", 1797033600000L),
                daysCount = obj.optInt("daysCount", 5),
                dateRangeText = obj.optString("dateRangeText", "12 Dec – 16 Dec 2026 (5 Days)"),
                adults = obj.optInt("adults", 2),
                children = obj.optInt("children", 1),
                skippedTravellers = obj.optBoolean("skippedTravellers", false),
                approxBudget = obj.optInt("approxBudget", 45000),
                travelCategory = obj.optString("travelCategory", "Family"),
                travelPreference = obj.optString("travelPreference", "Standard"),
                selectionMode = obj.optString("selectionMode", "Known"),
                selectedStates = states.ifEmpty { listOf("Rajasthan") },
                cityPlans = cityPlans.ifEmpty { SmartTripCatalog.defaultCityPlans() },
                budgetMode = obj.optString("budgetMode", "Fixed"),
                fixedBudget = obj.optInt("fixedBudget", 45000),
                minBudget = obj.optInt("minBudget", 35000),
                maxBudget = obj.optInt("maxBudget", 60000),
                accommodationBudget = obj.optInt("accommodationBudget", 16000),
                transportationBudget = obj.optInt("transportationBudget", 10000),
                foodBudget = obj.optInt("foodBudget", 7500),
                localTravelBudget = obj.optInt("localTravelBudget", 4500),
                sightseeingBudget = obj.optInt("sightseeingBudget", 4000),
                miscBudget = obj.optInt("miscBudget", 3000),
                selectedHotels = selectedHotels,
                skippedHotelCities = skippedHotels,
                selectedTransportLegs = legs,
                selectedTaxi = taxi,
                daySchedules = schedules,
                updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
            )
        }.getOrNull()
    }
}
