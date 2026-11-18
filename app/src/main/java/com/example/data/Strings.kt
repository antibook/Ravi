package com.example.data

object AppStrings {
    fun get(lang: AppLanguage): LocalizedCopy = if (lang == AppLanguage.HINDI) HindiCopy else EnglishCopy

    data class LocalizedCopy(
        val appName: String,
        val tagline: String,
        val navHome: String,
        val navExplore: String,
        val navTrips: String,
        val navSaved: String,
        val navProfile: String,
        val wherePrefix: String,
        val whereHighlight: String,
        val planMyTrip: String,
        val everythingForTrip: String,
        val verifiedInJaipur: String,
        val exploreTitle: String,
        val myTripsTitle: String,
        val savedItemsTitle: String,
        val profileTitle: String,
        val travelStoreTitle: String,
        val bookTaxiTitle: String,
        val localManagerTitle: String,
        val aiNoticeText: String,
        val guestGateTitle: String,
        val guestGateBody: String,
        val signInNow: String,
        val cancel: String
    )

    private val EnglishCopy = LocalizedCopy(
        appName = "TravelGuide+",
        tagline = "Explore More. Worry Less.",
        navHome = "Home",
        navExplore = "Explore",
        navTrips = "Trips",
        navSaved = "Saved",
        navProfile = "Profile",
        wherePrefix = "Where do you\n",
        whereHighlight = "want to go?",
        planMyTrip = "Plan My Trip →",
        everythingForTrip = "Everything for your trip",
        verifiedInJaipur = "✓ Verified in Jaipur",
        exploreTitle = "Explore",
        myTripsTitle = "My Trips",
        savedItemsTitle = "Favorites & Wishlist",
        profileTitle = "Profile",
        travelStoreTitle = "Travel Store",
        bookTaxiTitle = "Book a Taxi",
        localManagerTitle = "Local Trip Manager",
        aiNoticeText = "Sample planner active · Cloud Gemini AI requires enabling Firebase AI Logic.",
        guestGateTitle = "Account Required",
        guestGateBody = "Guests can browse freely. Sign in to save trips, bookmark items, or purchase PDF guides.",
        signInNow = "Sign In",
        cancel = "Continue Browsing"
    )

    private val HindiCopy = LocalizedCopy(
        appName = "TravelGuide+",
        tagline = "ज्यादा घूमो, चिंता कम करो।",
        navHome = "होम",
        navExplore = "एक्सप्लोर",
        navTrips = "ट्रिप्स",
        navSaved = "सेव्ड",
        navProfile = "प्रोफ़ाइल",
        wherePrefix = "आप कहाँ\n",
        whereHighlight = "जाना चाहते हैं?",
        planMyTrip = "मेरी ट्रिप प्लान करें →",
        everythingForTrip = "आपकी यात्रा के लिए सब कुछ",
        verifiedInJaipur = "✓ जयपुर में सत्यापित",
        exploreTitle = "एक्सप्लोर करें",
        myTripsTitle = "मेरी ट्रिप्स",
        savedItemsTitle = "पसंदीदा और विशलिस्ट",
        profileTitle = "प्रोफ़ाइल",
        travelStoreTitle = "ट्रैवल स्टोर",
        bookTaxiTitle = "टैक्सी बुक करें",
        localManagerTitle = "लोकल ट्रिप मैनेजर",
        aiNoticeText = "सैंपल प्लानर सक्रिय है · Gemini AI के लिए Firebase सेटअप आवश्यक है।",
        guestGateTitle = "अकाउंट आवश्यक है",
        guestGateBody = "गेस्ट ब्राउज़ कर सकते हैं। ट्रिप सेव करने या गाइड खरीदने के लिए साइन इन करें।",
        signInNow = "साइन इन करें",
        cancel = "ब्राउज़ जारी रखें"
    )
}
