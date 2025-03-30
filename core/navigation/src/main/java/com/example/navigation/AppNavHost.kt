package com.example.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.notifications.NotificationsScreen
import com.example.profile.ProfileScreen
import com.example.auth.data.SignUpValidator
import com.example.donations.ui.DonationsScreen
import com.example.donations.ui.especie.DonacionesEspecie
import com.example.donations.ui.especie.DonationsDetails
import com.example.donations.ui.monetaria.DonacionesMonetarias
import com.example.parks.ui.ParkDetailScreenA
import com.example.parks.ui.ParkDetailScreenN
import com.example.parks.ui.SlideInFilterPanel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    // Get an instance of FirebaseAuth
    val auth = FirebaseAuth.getInstance()

    // List of routes that do not require authentication
    val publicRoutes = listOf("signIn", "signUp", "resetPassword", "resetPasswordEmailSent", "signUpSuccess")

    // State to track if the authentication listener is active
    var isAuthListenerActive by remember { mutableStateOf(true) }

    // State to track if the user is authenticated
    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

    // Effect to manage the authentication state listener
    DisposableEffect(isAuthListenerActive) {
        if (isAuthListenerActive) {
            // Create and add the authentication state listener
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                isAuthenticated = firebaseAuth.currentUser != null
            }
            auth.addAuthStateListener(authStateListener)
            // Remove the listener when the effect is disposed
            onDispose { auth.removeAuthStateListener(authStateListener) }
        } else {
            onDispose { }
        }
    }

    // Effect to handle navigation changes
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentRoute = destination.route ?: return@addOnDestinationChangedListener
            // Update the authentication listener state based on the current route
            isAuthListenerActive = currentRoute !in listOf("signUp", "signUpSuccess")

            // Check if the current route requires authentication
            val routeRequiresAuth = !publicRoutes.any { route ->
                if (route.contains("?")) currentRoute.startsWith(route.split("?")[0]) else currentRoute == route
            }

            // Navigate to the sign-in screen if the route requires authentication and the user is not authenticated
            if (routeRequiresAuth && !isAuthenticated) {
                navController.navigate("signIn") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Effect to handle changes in authentication state
    LaunchedEffect(isAuthenticated) {
        val currentRoute = navController.currentDestination?.route
        if (!isAuthenticated && currentRoute != null) {
            // Check if the current route requires authentication
            val routeRequiresAuth = !publicRoutes.any { route ->
                if (route.contains("?")) currentRoute.startsWith(route.split("?")[0]) else currentRoute == route
            }

            // Navigate to the sign-in screen if the route requires authentication and the user is not authenticated
            if (routeRequiresAuth) {
                navController.navigate("signIn") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Decidir destino inicial basado en el estado de autenticación
    val startDestination = if (isAuthenticated) NavigationItem.Home.route else "signIn"

    // AQUÍ SE DEFINEN LAS RUTAS DE LA APLICACIÓN ⬇️

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        // ----------------------------------------------------------------

        // RUTAS PRINCIPALES

        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.Parks.route) { ParksScreen(navController = navController) }
        composable(NavigationItem.Donations.route) { DonationsScreen(navController = navController) }
        composable(NavigationItem.Notifications.route) { NotificationsScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen(navController) }

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "AUTH"

        composable("signIn") { SignInScreen(navController) }
        composable("signUp") { SignUpScreen(navController, SignUpValidator) }
        composable("resetPassword") { ResetPasswordScreen(navController) }
        composable("resetPasswordEmailSent") { ResetPasswordEmailSentScreen(navController) }
        composable("signUpSuccess") { SignUpSuccessScreen(navController) }

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "PARKS"
        composable(
            route = "parkDetailA/{parkName}?latitud={latitud}&longitud={longitud}",
            arguments = listOf(
                navArgument("parkName") { type = NavType.StringType },
                navArgument("latitud") { type = NavType.StringType },
                navArgument("longitud") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ParkDetailScreenA(
                parkName = backStackEntry.arguments?.getString("parkName"),
                latitud = backStackEntry.arguments?.getString("latitud"),
                longitud = backStackEntry.arguments?.getString("longitud"),
                navController = navController
            )
        }

        composable(
            route = "parkDetailN/{parkName}?latitud={latitud}&longitud={longitud}",
            arguments = listOf(
                navArgument("parkName") { type = NavType.StringType },
                navArgument("latitud") { type = NavType.StringType },
                navArgument("longitud") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ParkDetailScreenN(
                parkName = backStackEntry.arguments?.getString("parkName"),
                latitud = backStackEntry.arguments?.getString("latitud"),
                longitud = backStackEntry.arguments?.getString("longitud"),
                navController = navController
            )
        }

        composable("FiltrosParques") {
            // Esta es la clave: renderiza ParksScreen debajo y luego el panel encima
            Box(modifier = Modifier.fillMaxSize()) {
                // Primero renderizamos la pantalla de parques como fondo
                ParksScreen(navController = navController, showContent = false)

                // Luego añadimos el panel de filtros superpuesto
                SlideInFilterPanel(
                    isVisible = true,
                    onDismiss = { navController.navigate("Parques") },
                    onApply = { filters ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("filters", filters)
                        navController.navigate("Parques")
                    }
                )
            }
        }


        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "DONATIONS"

        composable("DonacionesEspecie") {
            DonacionesEspecie(navController = navController)
        }

        composable("DonacionesMonetarias") {
            DonacionesMonetarias(navController = navController)
        }

        composable(
            "DonationsDetails/{donationId}",
            arguments = listOf(
                navArgument("donationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            DonationsDetails(
                navController = navController,
                donationId = backStackEntry.arguments?.getString("donationId") ?: ""
            )
        }


        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "HOME"

        // ...

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "NOTIFICATIONS"

        // ...

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "PROFILE"

        // ...

        // ----------------------------------------------------------------
    }
}
