package com.example.calleme.navigation

import androidx.navigation.NavController

// Global variable to store the current flow
var currentFlow: NavigationFlow? = null


fun navigateByFlow(navController: NavController, flow: NavigationFlow) {
    // Set the global flow variable
    currentFlow = flow

    when (flow) {
        NavigationFlow.EMERGENCY -> {
            navController.navigate("affectedAreaFront")
        }
        NavigationFlow.BOOKING -> {
            navController.navigate("affectedAreaFront")
        }
    }
}

fun navigateFromAffectedAreaFront(navController: NavController) {
    navController.navigate("affectedAreaBack")
}

fun navigateFromAffectedAreaBack(navController: NavController) {
    when (currentFlow) {
        NavigationFlow.EMERGENCY -> {
            navController.navigate("emergencyFormScreen")
        }
        NavigationFlow.BOOKING -> {
            navController.navigate("formScreen") // Assuming you have a "normalBookingForm" route
        }
        null -> {
            // Handle case where flow is not set
            throw IllegalStateException("Navigation flow is not set!")
        }
    }
}

fun navigateFromFormScreen(navController: NavController) {
    when (currentFlow) {
        NavigationFlow.EMERGENCY -> {
            navController.navigate("fetchHospital")
        }
        NavigationFlow.BOOKING -> {
            navController.navigate("fetch")
        }
        null -> {
            // Handle case where flow is not set
            throw IllegalStateException("Navigation flow is not set!")
        }
    }
}