package com.example.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.ui.ResetPasswordEmailSentScreen
import com.example.auth.ui.ResetPasswordScreen
import com.example.auth.ui.SignInScreen
import com.example.auth.ui.SignUpScreen
import com.example.home.HomeScreen
import com.example.parks.ui.ParksScreen
import com.example.donations.DonationsScreen
import com.example.notifications.NotificationsScreen
import com.example.parks.ui.MapScreen
import com.example.profile.ProfileScreen
import com.example.parks.ui.RegisterParkScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "signIn",
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.Parks.route) { ParksScreen(navController = navController) }
        composable(NavigationItem.Donations.route) { DonationsScreen() }
        composable(NavigationItem.Notifications.route) { NotificationsScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen() }
        composable("signIn") { SignInScreen(navController)}
        composable("signUp") { SignUpScreen(navController)}
        composable("resetPassword") { ResetPasswordScreen(navController)}
        composable("resetPasswordEmailSent") { ResetPasswordEmailSentScreen(navController)}
        composable("registerPark") { RegisterParkScreen(navController) }
        composable("map") { MapScreen(navController) }
    }
}