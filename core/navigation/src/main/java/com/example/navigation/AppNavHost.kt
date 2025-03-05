package com.example.navigation

import android.net.Uri
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
import com.example.home.HomeScreen
import com.example.parks.ui.ParksScreen
import com.example.donations.DonationsScreen
import com.example.notifications.NotificationsScreen
import com.example.parks.ui.MapScreen
import com.example.profile.ProfileScreen
import com.example.parks.ui.RegisterParkScreen
import com.example.parks.ui.SharedViewModel
import java.net.URLDecoder
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val sharedViewModel: SharedViewModel = viewModel()
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

        // Ruta sin parámetros para RegisterPark
        composable("registerPark") {
            RegisterParkScreen(navController = navController,  sharedViewModel = sharedViewModel)
        }

        // Ruta con parámetros para RegisterPark y decodificación automática
        composable(
            route = "registerPark?lat={lat}&lon={lon}&address={address}&name={name}&desc={desc}&status={status}&imageUris={imageUris}&needs={needs}&comments={comments}",
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
                },
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("desc") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("imageUris") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("needs") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("comments") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("lat")
            val longitude = backStackEntry.arguments?.getString("lon")
            val address = backStackEntry.arguments?.getString("address")
            val name = backStackEntry.arguments?.getString("name")?.let { URLDecoder.decode(it, "UTF-8") }
            val desc = backStackEntry.arguments?.getString("desc")?.let { URLDecoder.decode(it, "UTF-8") }
            val status = backStackEntry.arguments?.getString("status")?.let { URLDecoder.decode(it, "UTF-8") }
            val imageUris = backStackEntry.arguments?.getString("imageUris")?.split(",")?.map { Uri.parse(it) } ?: emptyList()
            val needs = backStackEntry.arguments?.getString("needs")?.split(",") ?: emptyList()
            val comments = backStackEntry.arguments?.getString("comments")?.let { URLDecoder.decode(it, "UTF-8") }

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
                sharedViewModel = sharedViewModel,
                latitude = latitude,
                longitude = longitude,
                address = decodedAddress,
                parkNameArg = name,
                descriptionArg = desc,
                statusArg = status,
                imageUrisArg = imageUris,
                needsArg = needs,
                commentsArg = comments
            )
        }

        composable("map?name={name}&desc={desc}&status={status}&needs={needs}&comments={comments}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("desc") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("imageUris") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("needs") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("comments") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")?.let { URLDecoder.decode(it, "UTF-8") }
            val desc = backStackEntry.arguments?.getString("desc")?.let { URLDecoder.decode(it, "UTF-8") }
            val status = backStackEntry.arguments?.getString("status")?.let { URLDecoder.decode(it, "UTF-8") }
            val needs = backStackEntry.arguments?.getString("needs")?.split(",") ?: emptyList()
            val comments = backStackEntry.arguments?.getString("comments")?.let { URLDecoder.decode(it, "UTF-8") }

            MapScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                name = name,
                desc = desc,
                status = status,
                needs = needs.joinToString(","),
                comments = comments
                )
        }
    }
}
