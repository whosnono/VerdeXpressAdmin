package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.home.HomeScreen
//#todo: import com.example.parks.ParksScreen
//#todo: import com.example.donations.DonationsScreen
//#todo: import com.example.notifications.NotificationsScreen
//#todo: import com.example.profile.ProfileScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) { HomeScreen() }
        //#todo:añadir composable(NavigationItem.Parks.route) { ParksScreen() }
        //#todo:añadir composable(NavigationItem.Donations.route) { DonationsScreen() }
        //#todo:añadir composable(NavigationItem.Notifications.route) { NotificationsScreen() }
        //#todo:añadir composable(NavigationItem.Profile.route) { ProfileScreen() }
    }
}