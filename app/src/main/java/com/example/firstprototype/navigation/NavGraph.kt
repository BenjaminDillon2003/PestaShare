package com.example.firstprototype.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Estos dos imports son vitales:
import com.example.firstprototype.ui.screens.HomeScreen
import com.example.firstprototype.ui.screens.LoginScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(onLoginClick = {
                navController.navigate("home")
            })
        }
        composable("home") {
            HomeScreen()
        }
    }
}