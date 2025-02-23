package com.example.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.home.HomeScreen
import com.example.parks.ParksScreen
import com.example.donations.DonationsScreen
import com.example.notifications.NotificationsScreen
import com.example.profile.ProfileScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.Parks.route) { ParksScreen() }
        composable(NavigationItem.Donations.route) { DonationsScreen() }
        composable(NavigationItem.Notifications.route) { NotificationsScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen() }
    }
}