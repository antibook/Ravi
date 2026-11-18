package com.example.data

object CatalogData {

    val indiaStatesAndCities: Map<String, List<String>> = linkedMapOf(
        "Rajasthan" to listOf("Jaipur", "Udaipur", "Jodhpur", "Jaisalmer", "Pushkar", "Ajmer", "Mount Abu"),
        "Kerala" to listOf("Kochi", "Munnar", "Alleppey", "Thekkady", "Kovalam"),
        "Goa" to listOf("North Goa", "South Goa", "Panaji"),
        "Himachal Pradesh" to listOf("Shimla", "Manali", "Dharamshala", "Kasol", "Dalhousie"),
        "Uttarakhand" to listOf("Rishikesh", "Nainital", "Mussoorie", "Haridwar"),
        "Madhya Pradesh" to listOf("Ujjain", "Indore", "Bhopal", "Khajuraho"),
        "Jammu & Kashmir" to listOf("Srinagar", "Gulmarg", "Pahalgam", "Jammu"),
        "Maharashtra" to listOf("Mumbai", "Pune", "Lonavala", "Mahabaleshwar"),
        "Tamil Nadu" to listOf("Chennai", "Ooty", "Madurai", "Kanyakumari"),
        "Karnataka" to listOf("Bengaluru", "Mysuru", "Coorg", "Hampi"),
        "Uttar Pradesh" to listOf("Agra", "Varanasi", "Lucknow", "Ayodhya"),
        "Gujarat" to listOf("Ahmedabad", "Kutch", "Dwarka", "Somnath")
    )

    val allIndianStates: List<String> = indiaStatesAndCities.keys.toList()

    fun citiesForState(stateName: String): List<String> {
        if (stateName == "All States") {
            return indiaStatesAndCities.values.flatten().distinct()
        }
        return indiaStatesAndCities[stateName] ?: listOf("Jaipur")
    }

    val countries = listOf(
        CountryItem("india", "🇮🇳", "India", "28 states", "coral", isIndia = true),
        CountryItem("thailand", "🇹🇭", "Thailand", "12 cities", "indigo"),
        CountryItem("dubai", "🇦🇪", "Dubai / UAE", "6 cities", "green"),
        CountryItem("japan", "🇯🇵", "Japan", "9 cities", "amber"),
        CountryItem("singapore", "🇸🇬", "Singapore", "3 areas", "sky"),
        CountryItem("vietnam", "🇻🇳", "Vietnam", "8 cities", "plum")
    )

    val states = listOf(
        StateItem("rajasthan", "Rajasthan", "7 cities", 12, "coral", listOf("Heritage")),
        StateItem("kerala", "Kerala", "5 cities", 8, "amber", listOf("Beaches", "Wildlife")),
        StateItem("goa", "Goa", "3 regions", 6, "green", listOf("Beaches")),
        StateItem("himachal", "Himachal Pradesh", "5 cities", 9, "indigo", listOf("Hills")),
        StateItem("uttarakhand", "Uttarakhand", "4 cities", 7, "green", listOf("Hills", "Wildlife")),
        StateItem("madhya_pradesh", "Madhya Pradesh", "4 cities", 5, "plum", listOf("Heritage", "Wildlife")),
        StateItem("jk", "Jammu & Kashmir", "4 cities", 6, "coral", listOf("Hills"))
    )

    val cities = listOf(
        CityItem("jaipur", "rajasthan", "Rajasthan", "Jaipur", "The Pink City · 3-5 days", "3-5 days", "Oct - Mar",
            "Forts, bazaars and royal palaces in the Pink City. Start early at Amber Fort, capture sunrise at Hawa Mahal, and dine on traditional Dal Baati Churma.",
            "Dal Baati Churma at Dal Baati Kitchen, Ghevar & Lassi at Laxmi Misthan Bhandar, Rooftop chai near Hawa Mahal.",
            "Jaipur Int'l Airport (12 km) · Jaipur Junction Railway Station (4 km) · NH-48 highway from Delhi (4.5 hrs).",
            "Johari Bazaar (gems & jewellery), Bapu Bazaar (Mojari footwear & block prints), Tripolia Bazaar (lac bangles).",
            "guide_jaipur_5", 149, "coral"),
        CityItem("jodhpur", "rajasthan", "Rajasthan", "Jodhpur", "The Blue City · 2-3 days", "2-3 days", "Oct - Mar",
            "Towering Mehrangarh Fort overlooking cobalt-blue old city lanes, stepwells, and spice markets around Clock Tower.",
            "Mirchi Vada, Makhaniya Lassi, and Kabuli Pulao near Sardar Market.",
            "Jodhpur Airport (6 km) · Direct trains from Jaipur (5 hrs).",
            "Sardar Market spices, handwoven carpets, antique brassware.",
            "guide_rajasthan_10", 149, "indigo"),
        CityItem("udaipur", "rajasthan", "Rajasthan", "Udaipur", "City of Lakes · 3 days", "3 days", "Sep - Mar",
            "Romantic marble palaces on Lake Pichola, sunset boat rides to Jag Mandir, and Aravalli hill viewpoints.",
            "Lakefront Rajasthani thalis at Ambrai Ghat and hot kachoris in Hathi Pol.",
            "Maharana Pratap Airport (22 km) · Udaipur City Railway Station (3 km).",
            "Miniature Pichwai paintings, silver crafts at Bada Bazaar.",
            "guide_rajasthan_10", 149, "sky"),
        CityItem("jaisalmer", "rajasthan", "Rajasthan", "Jaisalmer", "Golden City · 2-3 days", "2-3 days", "Nov - Feb",
            "Living sandstone Sonar Quila fort, intricately carved Havelis, and starry Thar Desert dune camps.",
            "Ker Sangri, Ghotua Ladoo, and desert camp folk dinners.",
            "Jaisalmer Airport (14 km) · Overnight train from Jaipur.",
            "Mirror-work embroidery, camel leather bags, yellow sandstone crafts.",
            "guide_rajasthan_10", 149, "amber"),
        CityItem("ajmer", "rajasthan", "Rajasthan", "Ajmer", "Dargah Sharif · 1 day", "1 day", "Oct - Mar",
            "Historic spiritual city home to Ajmer Sharif Dargah, Ana Sagar Lake, and Adhai Din Ka Jhonpra.",
            "Sohan Halwa and Kadhi Kachori near Naya Bazaar.",
            "Kishangarh Airport (27 km) · Ajmer Junction (2 hrs from Jaipur).",
            "Attar perfumes, embroidered textiles, rose products.",
            "guide_rajasthan_10", 149, "green"),
        CityItem("pushkar", "rajasthan", "Rajasthan", "Pushkar", "Brahma Temple · 1 day", "1 day", "Oct - Mar",
            "Sacred lake ghats, the ancient Brahma Temple, Savitri Hill ropeway sunset, and bohemian café lanes.",
            "Malpua, falafel wraps, and sunset rooftop cafés.",
            "30 minutes drive from Ajmer across Nag Pahar.",
            "Rose water, leather journals, silver boho jewellery.",
            "guide_rajasthan_10", 149, "plum"),
        CityItem("mount_abu", "rajasthan", "Rajasthan", "Mount Abu", "Hill station · 2 days", "2 days", "Year-round",
            "Rajasthan's cool oasis with Nakki Lake, exquisite marble Dilwara Temples, and Guru Shikhar peak.",
            "Rabri, Gujarati-Rajasthani thalis, and hot bhutta at Sunset Point.",
            "Abu Road Railway Station (28 km uphill drive).",
            "Kota sarees, wooden handicrafts, marble carvings.",
            "guide_rajasthan_10", 149, "green"),
        CityItem("goa", "goa", "Goa", "North Goa", "Beaches & Heritage · 4-5 days", "4-5 days", "Nov - Feb",
            "Sun-kissed North & South beaches, Portuguese Fontainhas lanes, spice plantations, and coastal cafés.",
            "Goan fish curry, bebinca, poi bread, and beach shack grills.",
            "GOI Dabolim & GOX Mopa Airports · Madgaon Railway Station.",
            "Anjuna flea market, cashew feni, azulejo tiles.",
            "guide_goa_5", 99, "green")
    )

    val touristPlaces = listOf(
        TouristPlace("amber_fort", "jaipur", "Amber Fort", "Fort · 11 km", "Amer", "2-3 hrs", "₹100", "Majestic hilltop sandstone & marble palace overlooking Maota Lake with Sheesh Mahal.", "indigo"),
        TouristPlace("hawa_mahal", "jaipur", "Hawa Mahal", "Palace · Old City", "Badi Choupad", "45 min", "₹50", "Iconic five-story honeycomb pink sandstone facade with 953 jharokha windows.", "coral"),
        TouristPlace("city_palace", "jaipur", "City Palace", "Museum · Old City", "Jaleb Chowk", "2 hrs", "₹200", "Royal residence complex featuring Chandra Mahal, Mubarak Mahal, and Peacock Gate.", "amber"),
        TouristPlace("jantar_mantar", "jaipur", "Jantar Mantar", "Observatory", "Old City", "1 hr", "₹50", "UNESCO astronomical observatory housing the world's largest stone sundial.", "green"),
        TouristPlace("nahargarh_fort", "jaipur", "Nahargarh Fort", "Sunset point", "Aravalli Ridge", "2 hrs", "₹50", "Panoramic sunset fortress overlooking the entire illuminated Pink City.", "sky"),
        TouristPlace("jaigarh_fort", "jaipur", "Jaigarh Fort", "Attraction · Jaipur", "Amer Ridge", "1.5 hrs", "₹100", "Victory fort famous for the Jaivana cannon and panoramic views of Amber Fort.", "green"),
        TouristPlace("lake_pichola", "udaipur", "Lake Pichola", "Lake · Old City", "Ambrai", "2 hrs", "₹400", "Scenic freshwater lake famous for sunset boat cruises past Jag Mandir.", "sky"),
        TouristPlace("mehrangarh", "jodhpur", "Mehrangarh Fort", "Fort · 5 km", "Old City", "3 hrs", "₹200", "One of India's largest forts towering 400 feet above the Blue City.", "indigo")
    )

    val guides = listOf(
        TravelGuideProduct(
            id = "guide_rajasthan_10",
            title = "Rajasthan 10 Days Royal Circuit",
            subtitle = "Jaipur · Pushkar · Jodhpur · Jaisalmer · Udaipur",
            stateName = "Rajasthan",
            coveredCities = listOf("Jaipur", "Pushkar", "Jodhpur", "Jaisalmer", "Udaipur"),
            category = "State",
            days = 10,
            audience = "Family / Couple",
            pages = 45,
            price = 149,
            rating = 4.7,
            languages = "PDF · Hindi + English",
            colorKey = "coral",
            highlights = listOf("Day-wise plan", "Maps & routes", "Budget tips", "Hotel & food picks"),
            samplePages = listOf(
                "Page 1 · Welcome to Royal Rajasthan" to "Cover Jaipur (3D), Pushkar (1D), Jodhpur (2D), Jaisalmer (2D), and Udaipur (2D) in a smooth clockwise loop.",
                "Page 2 · Jaipur Morning Loop" to "Leave C-Scheme at 8:15 AM to reach Amber Fort before tour buses arrive at 10:30 AM.",
                "Page 3 · Verified Food & Budget Sheet" to "Daily meal budget ₹700-₹1,200/person. Try Dal Baati Kitchen & Laxmi Misthan Bhandar."
            )
        ),
        TravelGuideProduct(
            id = "guide_jaipur_5",
            title = "Jaipur & Pushkar 5 Days Guide",
            subtitle = "Jaipur · Ajmer · Pushkar",
            stateName = "Rajasthan",
            coveredCities = listOf("Jaipur", "Ajmer", "Pushkar"),
            category = "City",
            days = 5,
            audience = "Couple / Solo",
            pages = 28,
            price = 149,
            rating = 4.8,
            languages = "PDF · Hindi + English",
            colorKey = "indigo",
            highlights = listOf("Heritage forts map", "Bazaar walking trail", "Local street food", "Cab fare guide"),
            samplePages = listOf(
                "Page 1 · Pink City Architecture Trail" to "Sunrise photo spot across Hawa Mahal at Wind View Café, followed by Jantar Mantar and City Palace.",
                "Page 2 · Pushkar Day Excursion" to "Morning train or cab to Ajmer Dargah & Brahma Temple ghat sunset."
            )
        ),
        TravelGuideProduct(
            id = "guide_udaipur_jodhpur_6",
            title = "Udaipur & Jodhpur 6 Days Lakes & Forts",
            subtitle = "Udaipur · Jodhpur · Mount Abu",
            stateName = "Rajasthan",
            coveredCities = listOf("Udaipur", "Jodhpur", "Mount Abu"),
            category = "State",
            days = 6,
            audience = "Family / Couple",
            pages = 34,
            price = 139,
            rating = 4.7,
            languages = "PDF · Hindi + English",
            colorKey = "sky",
            highlights = listOf("Lake Pichola timings", "Mehrangarh audio route", "Kumbhalgarh stop", "Sunset viewpoints"),
            samplePages = listOf(
                "Page 1 · Udaipur to Jodhpur Scenic Route" to "Stop at Ranakpur Jain Temple en route between Udaipur and Jodhpur."
            )
        ),
        TravelGuideProduct(
            id = "guide_jaipur_3",
            title = "Jaipur Weekend 3 Days Express",
            subtitle = "Jaipur",
            stateName = "Rajasthan",
            coveredCities = listOf("Jaipur"),
            category = "City",
            days = 3,
            audience = "Friends / Solo",
            pages = 20,
            price = 99,
            rating = 4.6,
            languages = "PDF · Hindi + English",
            colorKey = "amber",
            highlights = listOf("72-hr tight schedule", "Best photo spots", "Metro & cab tips", "Evening bazaars"),
            samplePages = listOf(
                "Page 1 · Day 1 Old City & Nahargarh" to "Cover Hawa Mahal, Jantar Mantar, City Palace and sunset at Nahargarh Fort."
            )
        ),
        TravelGuideProduct(
            id = "guide_kerala_7",
            title = "Kerala 7 Days Backwaters & Tea Hills",
            subtitle = "Kochi · Munnar · Alleppey · Thekkady",
            stateName = "Kerala",
            coveredCities = listOf("Kochi", "Munnar", "Alleppey", "Thekkady"),
            category = "State",
            days = 7,
            audience = "Family",
            pages = 38,
            price = 129,
            rating = 4.6,
            languages = "PDF · Hindi + English",
            colorKey = "green",
            highlights = listOf("Munnar tea routes", "Alleppey houseboats", "Kochi heritage walk", "Ayurveda tips"),
            samplePages = listOf(
                "Page 1 · Kochi to Munnar" to "Scenic waterfall stops at Cheeyappara and Valara en route to Munnar tea estates."
            )
        ),
        TravelGuideProduct(
            id = "guide_goa_5",
            title = "Goa 5 Days Coast & Heritage",
            subtitle = "North Goa · South Goa · Panaji",
            stateName = "Goa",
            coveredCities = listOf("North Goa", "South Goa", "Panaji"),
            category = "Budget",
            days = 5,
            audience = "Friends / Couple",
            pages = 26,
            price = 99,
            rating = 4.5,
            languages = "PDF · Hindi + English",
            colorKey = "sky",
            highlights = listOf("North & South beaches", "Scooter & taxi rates", "Fontainhas walk", "Hidden coves"),
            samplePages = listOf(
                "Page 1 · South Goa Serenity" to "Palolem, Agonda, and Cabo de Rama fort sunset loop with beach shack price tips."
            )
        ),
        TravelGuideProduct(
            id = "guide_himachal_8",
            title = "Himachal 8 Days Mountain Loop",
            subtitle = "Shimla · Manali · Kasol · Dharamshala",
            stateName = "Himachal Pradesh",
            coveredCities = listOf("Shimla", "Manali", "Kasol", "Dharamshala"),
            category = "Family",
            days = 8,
            audience = "Family / Friends",
            pages = 40,
            price = 149,
            rating = 4.8,
            languages = "PDF · Hindi + English",
            colorKey = "indigo",
            highlights = listOf("Shimla-Manali route", "Solang & Kasol tips", "Snow gear checklist", "Verified cab rates"),
            samplePages = listOf(
                "Page 1 · Toy Train & Ridge Walk" to "Kalka-Shimla morning Vistadome timings and Mall Road heritage stops."
            )
        ),
        TravelGuideProduct(
            id = "guide_uttarakhand_4",
            title = "Uttarakhand 4 Days Ganga & Lakes",
            subtitle = "Rishikesh · Haridwar · Mussoorie · Nainital",
            stateName = "Uttarakhand",
            coveredCities = listOf("Rishikesh", "Haridwar", "Mussoorie", "Nainital"),
            category = "Budget",
            days = 4,
            audience = "Family / Friends",
            pages = 24,
            price = 119,
            rating = 4.6,
            languages = "PDF · Hindi + English",
            colorKey = "green",
            highlights = listOf("Ganga Aarti slots", "Rafting safety picks", "Mall Road cafés", "Best season guide"),
            samplePages = listOf(
                "Page 1 · Rishikesh & Haridwar" to "Triveni Ghat evening aarti and Laxman Jhula morning café trail."
            )
        ),
        TravelGuideProduct(
            id = "guide_up_3",
            title = "Agra & Varanasi 3-5 Days Heritage",
            subtitle = "Agra · Varanasi · Ayodhya",
            stateName = "Uttar Pradesh",
            coveredCities = listOf("Agra", "Varanasi", "Ayodhya"),
            category = "State",
            days = 5,
            audience = "Family",
            pages = 30,
            price = 129,
            rating = 4.7,
            languages = "PDF · Hindi + English",
            colorKey = "plum",
            highlights = listOf("Taj sunrise entry", "Kashi Vishwanath tips", "Ghat boat rides", "Food street map"),
            samplePages = listOf(
                "Page 1 · Taj Mahal Sunrise" to "East Gate entry at 6:00 AM with Mehtab Bagh sunset reflection spot."
            )
        ),
        TravelGuideProduct(
            id = "guide_kashmir_6",
            title = "Kashmir 6 Days Paradise Valley",
            subtitle = "Srinagar · Gulmarg · Pahalgam",
            stateName = "Jammu & Kashmir",
            coveredCities = listOf("Srinagar", "Gulmarg", "Pahalgam"),
            category = "Family",
            days = 6,
            audience = "Family / Couple",
            pages = 36,
            price = 159,
            rating = 4.9,
            languages = "PDF · Hindi + English",
            colorKey = "coral",
            highlights = listOf("Dal Lake Shikara rates", "Gulmarg Gondola booking", "Betaab Valley cab rules", "Winter packing"),
            samplePages = listOf(
                "Page 1 · Dal Lake & Mughal Gardens" to "Morning floating market tour and Nishat Bagh terrace walk."
            )
        )
    )

    val taxis = listOf(
        TaxiVehicle("taxi_dzire", "Rajasthan", "Jaipur", "Sedan", "Sedan · Dzire", "4 seats · AC · 2 bags", "2 large bags", 5800, 12, true,
            "Mahesh Gurjar", "7 yrs", 310, 4.7, "RJ14 ··· 4108", "04 Nov", 1800, 300, 100, 50),
        TaxiVehicle("taxi_innova", "Rajasthan", "Jaipur", "SUV", "SUV · Innova Crysta", "6-7 seats · AC", "4 large bags", 8200, 18, true,
            "Rakesh Sharma", "9 yrs", 420, 4.8, "RJ14 ··· 2231", "02 Nov", 2000, 300, 100, 50),
        TaxiVehicle("taxi_i10", "Rajasthan", "Udaipur", "Hatchback", "Hatchback · i10", "4 seats · AC", "2 cabin bags", 4900, 10, true,
            "Vikram Rathore", "5 yrs", 195, 4.6, "RJ27 ··· 8892", "05 Nov", 1500, 250, 100, 50),
        TaxiVehicle("taxi_ertiga_kerala", "Kerala", "Kochi", "SUV", "SUV · Ertiga", "6 seats · AC", "3 large bags", 6400, 15, true,
            "Suresh Nair", "8 yrs", 280, 4.8, "KL07 ··· 5510", "06 Nov", 1900, 300, 120, 60),
        TaxiVehicle("taxi_etios_goa", "Goa", "North Goa", "Sedan", "Sedan · Etios", "4 seats · AC", "2 bags", 3600, 13, true,
            "Nikhil Naik", "6 yrs", 240, 4.7, "GA03 ··· 1904", "07 Nov", 1400, 200, 50, 50),
        TaxiVehicle("taxi_innova_hp", "Himachal Pradesh", "Shimla", "SUV", "SUV · Innova Hill", "6 seats · AC", "4 bags", 9200, 20, true,
            "Deepak Thakur", "11 yrs", 510, 4.9, "HP01 ··· 7743", "01 Nov", 2400, 400, 150, 80)
    )

    val hotels = listOf(
        HotelItem(
            id = "hotel_haveli", stateName = "Rajasthan", city = "Jaipur", name = "Heritage Haveli Stay",
            areaInfo = "1.2 km to City Palace", pricePerNight = 2400, rating = 4.6, category = "Family",
            isVerified = true, colorKey = "indigo", checkInOut = "Check-in 12 PM · Check-out 11 AM",
            facilities = listOf("Free Wi-Fi", "Complimentary Breakfast", "Rooftop Courtyard", "24x7 Front Desk", "Airport Pickup", "Air Conditioning"),
            policies = listOf("Free cancellation up to 24 hrs before check-in", "Valid Govt ID required", "Couple & family friendly"),
            rooms = listOf(HotelRoomOption("Deluxe Room", "Breakfast · AC · Free Wi-Fi", 2400), HotelRoomOption("Family Suite", "2 beds · 4 guests", 3800))
        ),
        HotelItem(
            id = "hotel_pink_city", stateName = "Rajasthan", city = "Jaipur", name = "Pink City Palace Hotel",
            areaInfo = "C-Scheme · Near Hawa Mahal", pricePerNight = 3900, rating = 4.8, category = "Luxury",
            isVerified = true, colorKey = "amber", checkInOut = "Check-in 1 PM · Check-out 11 AM",
            facilities = listOf("Swimming Pool", "Spa & Courtyard", "Free Wi-Fi", "Valet Parking", "Rajasthani Fine Dining"),
            policies = listOf("Free cancellation up to 48 hrs before check-in", "Early check-in subject to availability"),
            rooms = listOf(HotelRoomOption("Heritage King Room", "Breakfast · Pool view · AC", 3900), HotelRoomOption("Royal Jharokha Suite", "Balcony · Lounge access", 5400))
        ),
        HotelItem(
            id = "hotel_bapu_inn", stateName = "Rajasthan", city = "Jaipur", name = "C-Scheme Comfort Inn",
            areaInfo = "Central Jaipur · Metro 400m", pricePerNight = 1650, rating = 4.4, category = "Budget",
            isVerified = true, colorKey = "green", checkInOut = "Check-in 12 PM · Check-out 10 AM",
            facilities = listOf("Free Wi-Fi", "AC Rooms", "Tea/Coffee Maker", "Travel Desk"),
            policies = listOf("No hidden charges", "Free cancellation up to 24 hrs"),
            rooms = listOf(HotelRoomOption("Standard Queen", "AC · Wi-Fi", 1650), HotelRoomOption("Twin Deluxe", "2 single beds · Breakfast", 2100))
        ),
        HotelItem(
            id = "hotel_pichola", stateName = "Rajasthan", city = "Udaipur", name = "Lake Pichola Haveli",
            areaInfo = "Ambrai Ghat · Lakefront", pricePerNight = 3500, rating = 4.8, category = "Luxury",
            isVerified = true, colorKey = "sky", checkInOut = "Check-in 12 PM · Check-out 11 AM",
            facilities = listOf("Lake View Rooftop", "Free Wi-Fi", "Breakfast Included", "Boat Ghat Access"),
            policies = listOf("Free cancellation up to 24 hrs"),
            rooms = listOf(HotelRoomOption("Lake View Room", "Balcony · Breakfast", 3500))
        ),
        HotelItem(
            id = "hotel_blue_city", stateName = "Rajasthan", city = "Jodhpur", name = "Mehrangarh View Courtyard",
            areaInfo = "Old City · 800m to Fort", pricePerNight = 2200, rating = 4.5, category = "Family",
            isVerified = true, colorKey = "indigo", checkInOut = "Check-in 12 PM · Check-out 11 AM",
            facilities = listOf("Fort View Terrace", "Free Wi-Fi", "AC"),
            policies = listOf("Valid ID required"),
            rooms = listOf(HotelRoomOption("Courtyard Room", "Breakfast · AC", 2200))
        ),
        HotelItem(
            id = "hotel_munnar_mist", stateName = "Kerala", city = "Munnar", name = "Tea Valley Mist Resort",
            areaInfo = "Chithirapuram · Hill View", pricePerNight = 3200, rating = 4.7, category = "Family",
            isVerified = true, colorKey = "green", checkInOut = "Check-in 1 PM · Check-out 11 AM",
            facilities = listOf("Tea Plantation Walk", "Bonfire", "Free Breakfast"),
            policies = listOf("Free cancellation 48 hrs prior"),
            rooms = listOf(HotelRoomOption("Valley Cottage", "Balcony · Breakfast", 3200))
        ),
        HotelItem(
            id = "hotel_goa_palm", stateName = "Goa", city = "North Goa", name = "Calangute Palm Retreat",
            areaInfo = "500m to Calangute Beach", pricePerNight = 2800, rating = 4.5, category = "Family",
            isVerified = true, colorKey = "coral", checkInOut = "Check-in 1 PM · Check-out 11 AM",
            facilities = listOf("Pool", "Beach Shuttle", "Free Wi-Fi"),
            policies = listOf("Free cancellation 24 hrs prior"),
            rooms = listOf(HotelRoomOption("Poolside Deluxe", "Breakfast · AC", 2800))
        ),
        HotelItem(
            id = "hotel_manali_pine", stateName = "Himachal Pradesh", city = "Manali", name = "Old Manali Pine Lodge",
            areaInfo = "Near Manu Temple Road", pricePerNight = 2600, rating = 4.6, category = "Budget",
            isVerified = true, colorKey = "amber", checkInOut = "Check-in 12 PM · Check-out 10 AM",
            facilities = listOf("Heated Rooms", "Mountain Café", "Wi-Fi"),
            policies = listOf("Flexible winter rescheduling"),
            rooms = listOf(HotelRoomOption("Cedar Balcony Room", "Heater · Breakfast", 2600))
        )
    )

    val restaurants = listOf(
        RestaurantItem(
            id = "rest_rooftop", stateName = "Rajasthan", city = "Jaipur",
            name = "Rooftop Rajasthani Café", tags = "Veg & Non-veg · Rooftop",
            filterCategory = "Rooftop", rating = 4.5, priceForTwo = 600,
            isVerified = true, colorKey = "plum",
            areaInfo = "MI Road · Overlooking Old City",
            timings = "11:00 AM – 11:30 PM",
            mustTryDishes = listOf("Laal Maas", "Ker Sangri", "Masala Chaas"),
            description = "Open-air heritage terrace serving slow-cooked Rajasthani classics with illuminated evening views."
        ),
        RestaurantItem(
            id = "rest_dalbaati", stateName = "Rajasthan", city = "Jaipur",
            name = "Dal Baati Kitchen", tags = "Family friendly · Local thali",
            filterCategory = "Local thali", rating = 4.4, priceForTwo = 450,
            isVerified = true, colorKey = "green",
            areaInfo = "Bani Park · 1.4 km from Station",
            timings = "12:00 PM – 10:30 PM",
            mustTryDishes = listOf("Dal Baati Churma", "Gatte ki Sabzi", "Kesar Jalebi"),
            description = "Warm family dining hall famous for wood-fired baatis, pure desi ghee churma, and unlimited thalis."
        ),
        RestaurantItem(
            id = "rest_laxmi", stateName = "Rajasthan", city = "Jaipur",
            name = "Laxmi Misthan", tags = "Sweets & snacks · Pure Veg",
            filterCategory = "Veg", rating = 4.3, priceForTwo = 300,
            isVerified = true, colorKey = "amber",
            areaInfo = "Johari Bazaar · Old City",
            timings = "8:30 AM – 10:30 PM",
            mustTryDishes = listOf("Pyaaz Kachori", "Paneer Ghewar", "Raj Kachori"),
            description = "Iconic heritage sweet shop and vegetarian eatery right in the heart of Johari Bazaar."
        ),
        RestaurantItem(
            id = "rest_peacock", stateName = "Rajasthan", city = "Jaipur",
            name = "Peacock Courtyard Diner", tags = "Live folk music · Rooftop",
            filterCategory = "Rooftop", rating = 4.7, priceForTwo = 850,
            isVerified = true, colorKey = "coral",
            areaInfo = "Hathroi Fort Road · Central Jaipur",
            timings = "12:00 PM – 11:00 PM",
            mustTryDishes = listOf("Rajasthani Platter", "Tandoori Paneer", "Kulhad Chai"),
            description = "Breezy courtyard and multi-level terrace with live acoustic folk music every evening."
        ),
        RestaurantItem(
            id = "rest_ambrai", stateName = "Rajasthan", city = "Udaipur",
            name = "Ambrai Lakefront Kitchen", tags = "Lake Pichola View · Thali",
            filterCategory = "Rooftop", rating = 4.8, priceForTwo = 1100,
            isVerified = true, colorKey = "sky",
            areaInfo = "Ambrai Ghat · Lake Pichola",
            timings = "12:30 PM – 10:45 PM",
            mustTryDishes = listOf("Mewari Thali", "Corn Palak", "Rose Kulfi"),
            description = "Waterfront dining right along Lake Pichola with panoramic views of City Palace and Jag Mandir."
        ),
        RestaurantItem(
            id = "rest_gypsy", stateName = "Rajasthan", city = "Jodhpur",
            name = "Gypsy Heritage Thali", tags = "Authentic Marwari Thali",
            filterCategory = "Local thali", rating = 4.6, priceForTwo = 550,
            isVerified = true, colorKey = "indigo",
            areaInfo = "Sardarpura · Near Clock Tower",
            timings = "12:00 PM – 10:30 PM",
            mustTryDishes = listOf("Marwari Thali", "Mirchi Vada", "Makhaniya Lassi"),
            description = "Generous 22-item traditional Marwari thali served with attentive local hospitality."
        ),
        RestaurantItem(
            id = "rest_kochi_spice", stateName = "Kerala", city = "Kochi",
            name = "Fort Kochi Spice Table", tags = "Malabar Coastal · Veg & Non-veg",
            filterCategory = "Local thali", rating = 4.6, priceForTwo = 700,
            isVerified = true, colorKey = "green",
            areaInfo = "Princess Street · Fort Kochi",
            timings = "11:30 AM – 10:30 PM",
            mustTryDishes = listOf("Appam & Veg Stew", "Malabar Parotta", "Coconut Payasam"),
            description = "Colonial-era courtyard restaurant celebrating coastal Kerala spices and fresh coconut recipes."
        ),
        RestaurantItem(
            id = "rest_goa_shack", stateName = "Goa", city = "North Goa",
            name = "Baga Sunset Bistro", tags = "Goan Curry & Grill",
            filterCategory = "Rooftop", rating = 4.5, priceForTwo = 900,
            isVerified = true, colorKey = "coral",
            areaInfo = "Baga Creek Road · North Goa",
            timings = "11:00 AM – 12:00 AM",
            mustTryDishes = listOf("Goan Xacuti", "Bebinca Dessert", "Kokum Cooler"),
            description = "Relaxed coastal bistro offering Goan curries, wood-fired grills, and sunset lounge seating."
        ),
        RestaurantItem(
            id = "rest_manali_cafe", stateName = "Himachal Pradesh", city = "Manali",
            name = "Apple Orchard Café", tags = "Himachali Siddu & Coffee",
            filterCategory = "Veg", rating = 4.7, priceForTwo = 500,
            isVerified = true, colorKey = "amber",
            areaInfo = "Old Manali · Near Manu Temple",
            timings = "9:00 AM – 10:00 PM",
            mustTryDishes = listOf("Steamed Siddu", "Apple Cinnamon Pie", "Kullu Herbal Tea"),
            description = "Cozy timber café tucked inside apple orchards with mountain views and freshly baked desserts."
        )
    )

    val localManagers = listOf(
        LocalManagerItem(
            id = "mgr_rajesh", name = "Rajesh Rathore", stateName = "Rajasthan", city = "Jaipur",
            experienceYears = 8, tripsManaged = 185, rating = 4.9, languages = "Hindi, English",
            pricePerDay = 999, phoneNumber = "+919876543210",
            specialty = "Full Jaipur fort route, priority entry tips, taxi & hotel coordination",
            isVerified = true, colorKey = "coral"
        ),
        LocalManagerItem(
            id = "mgr_kunal", name = "Kunal Shekhawat", stateName = "Rajasthan", city = "Jaipur",
            experienceYears = 6, tripsManaged = 120, rating = 4.7, languages = "Hindi, English",
            pricePerDay = 799, phoneNumber = "+919876501122",
            specialty = "Bazaar shopping, heritage walks, family restaurant reservations",
            isVerified = true, colorKey = "indigo"
        ),
        LocalManagerItem(
            id = "mgr_ananya", name = "Ananya Sisodia", stateName = "Rajasthan", city = "Udaipur",
            experienceYears = 7, tripsManaged = 140, rating = 4.8, languages = "Hindi, English",
            pricePerDay = 949, phoneNumber = "+919876502233",
            specialty = "Lake Pichola boat timings, Kumbhalgarh day trips, haveli stays",
            isVerified = true, colorKey = "sky"
        ),
        LocalManagerItem(
            id = "mgr_devendra", name = "Devendra Bhati", stateName = "Rajasthan", city = "Jaisalmer",
            experienceYears = 9, tripsManaged = 210, rating = 4.8, languages = "Hindi, English",
            pricePerDay = 899, phoneNumber = "+919876503344",
            specialty = "Verified Sam desert camps, jeep safaris, Golden Fort heritage tours",
            isVerified = true, colorKey = "amber"
        ),
        LocalManagerItem(
            id = "mgr_arjun", name = "Arjun Menon", stateName = "Kerala", city = "Kochi",
            experienceYears = 7, tripsManaged = 160, rating = 4.8, languages = "English, Hindi, Malayalam",
            pricePerDay = 999, phoneNumber = "+919876504455",
            specialty = "Munnar cab routing, verified Alleppey houseboats & spice tours",
            isVerified = true, colorKey = "green"
        ),
        LocalManagerItem(
            id = "mgr_rohan", name = "Rohan Fernandes", stateName = "Goa", city = "North Goa",
            experienceYears = 5, tripsManaged = 130, rating = 4.6, languages = "English, Hindi, Konkani",
            pricePerDay = 849, phoneNumber = "+919876505566",
            specialty = "North-South beach itinerary, verified scooters & airport transfers",
            isVerified = true, colorKey = "coral"
        ),
        LocalManagerItem(
            id = "mgr_tarun", name = "Tarun Verma", stateName = "Himachal Pradesh", city = "Manali",
            experienceYears = 10, tripsManaged = 245, rating = 4.9, languages = "Hindi, English",
            pricePerDay = 1099, phoneNumber = "+919876506677",
            specialty = "Atal Tunnel & Solang weather routing, mountain cabs & heated stays",
            isVerified = true, colorKey = "indigo"
        )
    )

    val activities = listOf(
        ActivityItem("act_safari", "Desert Safari", "Jaisalmer", "4 hrs", 1800, "Adventure", "amber", "Golden dune jeep + camel ride with sunset chai and folk performance."),
        ActivityItem("act_boat", "Boat Ride", "Udaipur", "1 hr", 500, "Heritage", "sky", "Sunset cruise on Lake Pichola past City Palace and Jag Mandir."),
        ActivityItem("act_walk", "Heritage Walk", "Jaipur", "2 hrs", 700, "Heritage", "coral", "Guided morning walk through Old City temples, havelis, and bazaars."),
        ActivityItem("act_food", "Food Tour", "Old City", "3 hrs", 900, "Food", "green", "Curated tasting of 7 iconic local snacks from verified heritage stalls.")
    )

    val reviews = listOf(
        // Hotels
        ReviewItem("rev_h1_1", "Neha S.", "Clean heritage rooms, helpful front desk, and an easy walk to City Palace.", 5, true, "hotel_haveli", "Nov 2026"),
        ReviewItem("rev_h1_2", "Arjun T.", "Quiet courtyard breakfast was lovely. Family suite easily fit 4 of us.", 4, true, "hotel_haveli", "Oct 2026"),
        ReviewItem("rev_h1_3", "Meera P.", "Great value in Jaipur. Quick auto availability right outside the gate.", 5, true, "hotel_haveli", "Oct 2026"),

        ReviewItem("rev_h2_1", "Siddharth R.", "Poolside courtyard feels royal and rooms are spotless.", 5, true, "hotel_pink_city", "Nov 2026"),
        ReviewItem("rev_h2_2", "Kavya M.", "Breakfast spread had fresh poha and kachoris. Valet parking was smooth.", 5, true, "hotel_pink_city", "Oct 2026"),

        ReviewItem("rev_h3_1", "Rohan D.", "Very close to the metro and clean AC rooms for a budget stay.", 4, true, "hotel_bapu_inn", "Nov 2026"),
        ReviewItem("rev_h3_2", "Anjali K.", "Good Wi-Fi for remote work and friendly staff.", 5, true, "hotel_bapu_inn", "Oct 2026"),

        ReviewItem("rev_h4_1", "Vikram J.", "Waking up to Lake Pichola from the balcony is unforgettable!", 5, true, "hotel_pichola", "Nov 2026"),
        ReviewItem("rev_h4_2", "Pooja N.", "Sunset tea on the rooftop overlooking Ambrai Ghat was the trip highlight.", 5, true, "hotel_pichola", "Oct 2026"),

        ReviewItem("rev_h5_1", "Harsh V.", "Unbeatable view of Mehrangarh Fort lit up at night.", 5, true, "hotel_blue_city", "Nov 2026"),
        ReviewItem("rev_h5_2", "Divya B.", "Cozy rooms and staff helped book our fort audio tour.", 4, true, "hotel_blue_city", "Sep 2026"),

        ReviewItem("rev_h6_1", "Karthik S.", "Misty tea estate mornings and a warm evening bonfire.", 5, true, "hotel_munnar_mist", "Nov 2026"),
        ReviewItem("rev_h6_2", "Sneha L.", "Clean cottages and delicious hot appam for breakfast.", 4, true, "hotel_munnar_mist", "Oct 2026"),

        ReviewItem("rev_h7_1", "Nikhil G.", "5-minute walk to Calangute Beach and a super clean pool.", 5, true, "hotel_goa_palm", "Nov 2026"),
        ReviewItem("rev_h7_2", "Riya A.", "Peaceful retreat despite being close to the main road.", 4, true, "hotel_goa_palm", "Oct 2026"),

        ReviewItem("rev_h8_1", "Aman C.", "Room heaters worked great in the chill and mountain café food was fresh.", 5, true, "hotel_manali_pine", "Nov 2026"),
        ReviewItem("rev_h8_2", "Tanya K.", "Wooden balcony views of the pines are gorgeous.", 4, true, "hotel_manali_pine", "Oct 2026"),

        // Restaurants
        ReviewItem("rev_r1_1", "Neha S.", "Amazing rooftop sunset and flavorful Ker Sangri.", 5, true, "rest_rooftop", "Nov 2026"),
        ReviewItem("rev_r1_2", "Gaurav P.", "Slightly busy at 8 PM, so arrive early for corner terrace seats.", 4, true, "rest_rooftop", "Oct 2026"),

        ReviewItem("rev_r2_1", "Manish B.", "Authentic wood-fired Dal Baati with generous ghee and warm service.", 5, true, "rest_dalbaati", "Nov 2026"),
        ReviewItem("rev_r2_2", "Shruti V.", "Very hygienic family place and pocket-friendly thali.", 4, true, "rest_dalbaati", "Oct 2026"),

        ReviewItem("rev_r3_1", "Devika R.", "Crisp Pyaaz Kachori and fresh Ghewar — must stop after Hawa Mahal.", 5, true, "rest_laxmi", "Nov 2026"),
        ReviewItem("rev_r3_2", "Pranav M.", "Quick service even during morning rush.", 4, true, "rest_laxmi", "Oct 2026"),

        ReviewItem("rev_r4_1", "Aarav K.", "Live folk music made dinner magical. Great ambience for couples and families.", 5, true, "rest_peacock", "Nov 2026"),
        ReviewItem("rev_r4_2", "Isha N.", "Loved the fairy-lit courtyard and fresh tandoori platter.", 5, true, "rest_peacock", "Oct 2026"),

        ReviewItem("rev_r5_1", "Meghna S.", "Stunning Lake Pichola reflection at night; Mewari thali was delicious.", 5, true, "rest_ambrai", "Nov 2026"),
        ReviewItem("rev_r5_2", "Kabir J.", "Worth reserving a lakeside table ahead of sunset.", 5, true, "rest_ambrai", "Oct 2026"),

        ReviewItem("rev_r6_1", "Yash R.", "22-item Marwari thali was incredible value and super fresh.", 5, true, "rest_gypsy", "Nov 2026"),
        ReviewItem("rev_r6_2", "Nandini T.", "Don't skip the Makhaniya Lassi at the end!", 4, true, "rest_gypsy", "Oct 2026"),

        ReviewItem("rev_r7_1", "Surya K.", "Soft appams and fragrant vegetable stew — reminded us of home cooking.", 5, true, "rest_kochi_spice", "Nov 2026"),
        ReviewItem("rev_r7_2", "Elena W.", "Calm courtyard right in Fort Kochi with friendly hosts.", 4, true, "rest_kochi_spice", "Oct 2026"),

        ReviewItem("rev_r8_1", "Varun S.", "Loved the Goan curry and chilled kokum cooler after the beach.", 5, true, "rest_goa_shack", "Nov 2026"),
        ReviewItem("rev_r8_2", "Simran D.", "Great music and prompt service even on weekends.", 4, true, "rest_goa_shack", "Oct 2026"),

        ReviewItem("rev_r9_1", "Karan V.", "Warm steamed siddu and hot apple cinnamon pie under the apple trees.", 5, true, "rest_manali_cafe", "Nov 2026"),
        ReviewItem("rev_r9_2", "Aditi P.", "Best morning coffee spot in Old Manali!", 5, true, "rest_manali_cafe", "Oct 2026"),

        // General app / trip reviews
        ReviewItem("rev_2", "Rohit K.", "Driver was on time and polite.", 5, true, "global", "Nov 2026"),
        ReviewItem("rev_3", "Priya M.", "The Jaipur 5-day PDF route saved us so much time at Amber Fort!", 5, true, "global", "Oct 2026"),
        ReviewItem("rev_4", "Karan V.", "Clear fare breakdown with zero surprise toll demands.", 4, true, "global", "Oct 2026")
    )
}
