package com.example.ui.screens.planner

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.example.data.SmartHotelItem
import com.example.data.SmartTripPlan
import com.example.viewmodel.SmartTripController

@Composable
fun SmartTripPlannerScreen(
    plan: SmartTripPlan,
    registeredHotels: List<SmartHotelItem>,
    controller: SmartTripController,
    onExitToHome: () -> Unit,
    onExitToMyTrips: () -> Unit
) {
    val handleBack: () -> Unit = {
        if (plan.currentStep > 1) {
            controller.goToStep(plan.currentStep - 1)
        } else {
            onExitToHome()
        }
    }

    BackHandler(enabled = plan.currentStep > 1) {
        controller.goToStep(plan.currentStep - 1)
    }

    when (plan.currentStep.coerceIn(1, 8)) {
        1 -> Module1TripBasicsScreen(
            plan = plan,
            onBack = onExitToHome,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips,
            onSaveAndNext = { tripName, startState, startCity, destState, destCity, startDateMillis, daysCount, adults, children, skippedTravellers, approxBudget, travelCategory, travelPreference ->
                controller.saveModule1Basics(
                    tripName = tripName,
                    startState = startState,
                    startCity = startCity,
                    destState = destState,
                    destCity = destCity,
                    startDateMillis = startDateMillis,
                    daysCount = daysCount,
                    adults = adults,
                    children = children,
                    skippedTravellers = skippedTravellers,
                    approxBudget = approxBudget,
                    travelCategory = travelCategory,
                    travelPreference = travelPreference
                )
                controller.goToStep(2)
            }
        )

        2 -> Module2DestinationScreen(
            plan = plan,
            controller = controller,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips
        )

        3 -> Module3BudgetPlannerScreen(
            plan = plan,
            controller = controller,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips
        )

        4 -> Module4HotelsScreen(
            plan = plan,
            registeredHotels = registeredHotels,
            controller = controller,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips
        )

        5 -> Module5TransportScreen(
            plan = plan,
            controller = controller,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips
        )

        6 -> Module6InteractiveMapScreen(
            plan = plan,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips
        )

        7 -> Module7ReviewAndBookingScreen(
            plan = plan,
            controller = controller,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onSaveAndExit = onExitToMyTrips,
            onCompletedNavigateDashboard = {
                controller.goToStep(8)
            }
        )

        else -> Module8MyTripDashboardScreen(
            plan = plan,
            controller = controller,
            onBack = handleBack,
            onStepJump = controller::goToStep,
            onExitToMyTrips = onExitToMyTrips
        )
    }
}
