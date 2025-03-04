package com.example.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.auth.ui.ResetPasswordEmailSentScreen
import com.example.auth.ui.ResetPasswordScreen
import com.example.auth.ui.SignInScreen
import com.example.auth.ui.SignUpScreen
import com.example.auth.ui.SignUpSuccessScreen
import com.example.home.HomeScreen
import com.example.parks.ui.ParksScreen
import com.example.donations.DonationsScreen
import com.example.notifications.NotificationsScreen
import com.example.parks.ui.MapScreen
import com.example.profile.ProfileScreen
import com.example.parks.ui.RegisterParkScreen
import java.net.URLDecoder
import java.net.URLEncoder

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
        composable("signIn") { SignInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("resetPassword") { ResetPasswordScreen(navController) }
        composable("resetPasswordEmailSent") { ResetPasswordEmailSentScreen(navController) }
        composable("signUpSuccess") { SignUpSuccessScreen(navController) }

        // Ruta sin parámetros para RegisterPark
        composable("registerPark") {
            RegisterParkScreen(navController = navController)
        }

        // Ruta con parámetros para RegisterPark y decodificación automática
        composable(
            route = "registerPark?lat={lat}&lon={lon}&address={address}",
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("lon") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("address") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("lat")
            val longitude = backStackEntry.arguments?.getString("lon")
            val address = backStackEntry.arguments?.getString("address")

            // Decodificar la dirección automáticamente
            val decodedAddress = address?.let {
                try {
                    URLDecoder.decode(it, "UTF-8")
                } catch (e: Exception) {
                    it
                }
            }

            RegisterParkScreen(
                navController = navController,
                latitude = latitude,
                longitude = longitude,
                address = decodedAddress
            )
        }

        composable("map") { MapScreen(navController = navController) }
    }
}

fun NavHostController.navigateToRegisterPark(
    latitude: String?,
    longitude: String?,
    address: String?
) {
    val encodedAddress = address?.let {
        URLEncoder.encode(it, "UTF-8")
    }

    navigate("registerPark?lat=$latitude&lon=$longitude&address=$encodedAddress")
}