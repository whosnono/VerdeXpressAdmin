package com.example.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.example.donations.ui.inicio.DonationsScreen
import com.example.notifications.NotificationsScreen
import com.example.parks.ui.MapScreen
import com.example.profile.ProfileScreen
import com.example.parks.ui.RegisterParkScreen
import com.example.parks.ui.RegisterParkSuccessScreen
import com.example.parks.ui.SharedViewModel
import java.net.URLDecoder
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.data.SignUpValidator
import com.google.firebase.auth.FirebaseAuth
import com.example.donations.ui.donacionEspecie.FormScreen as EspecieFormScreen
import com.example.donations.ui.donacionMonetaria.FormScreen as MonetariaFormScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val sharedViewModel: SharedViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    // Lista de rutas públicas que no requieren autenticación
    val publicRoutes = listOf(
        "signIn",
        "signUp",
        "resetPassword",
        "resetPasswordEmailSent",
        "signUpSuccess"
    )

    // Estado de autenticación mantenido en tiempo real
    var isAuthListenerActive by remember { mutableStateOf(true) }
    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

    // Listener para cambios en el estado de autenticación
    DisposableEffect(isAuthListenerActive) {
        if (isAuthListenerActive) {
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                isAuthenticated = firebaseAuth.currentUser != null
            }

            auth.addAuthStateListener(authStateListener)

            onDispose {
                auth.removeAuthStateListener(authStateListener)
            }
        } else {
            onDispose { /* No hacer nada si el listener no está activo */ }
        }
    }

    // Monitoreamos cambios en la navegación para verificar la autenticación
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentRoute = destination.route ?: return@addOnDestinationChangedListener

            // Desactivar el listener en las rutas de registro y éxito de registro
            if (publicRoutes.contains(currentRoute)) {
                isAuthListenerActive = false
            } else {
                isAuthListenerActive = true
            }

            // Verifica si la ruta actual NO está en la lista de públicas
            val routeRequiresAuth = !publicRoutes.any { route ->
                if (route.contains("?")) {
                    // Para rutas con parámetros, verificar solo la parte base
                    val baseRoute = route.split("?")[0]
                    currentRoute.startsWith(baseRoute)
                } else {
                    currentRoute == route
                }
            }

            if (routeRequiresAuth && !isAuthenticated) {
                // Redirigir a signIn si intenta acceder a una ruta protegida sin autenticación
                navController.navigate("signIn") {
                    // Limpiar el stack de navegación
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Efecto para manejar cambios en el estado de autenticación
    LaunchedEffect(isAuthenticated) {
        val currentRoute = navController.currentDestination?.route

        if (!isAuthenticated && currentRoute != null) {
            // Verifica si la ruta actual NO está en la lista de públicas
            val routeRequiresAuth = !publicRoutes.any { route ->
                if (route.contains("?")) {
                    val baseRoute = route.split("?")[0]
                    currentRoute.startsWith(baseRoute)
                } else {
                    currentRoute == route
                }
            }

            if (routeRequiresAuth) {
                // Si el usuario cierra sesión mientras está en una ruta protegida
                navController.navigate("signIn") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Decidir destino inicial basado en el estado de autenticación
    val startDestination = if (isAuthenticated) NavigationItem.Home.route else "signIn"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.Parks.route) { ParksScreen(navController = navController) }
        composable(NavigationItem.Donations.route) { DonationsScreen(navController) }
        composable(NavigationItem.Notifications.route) { NotificationsScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen(navController) }
        composable("signIn") { SignInScreen(navController) }
        composable("signUp") { SignUpScreen(navController, SignUpValidator) }
        composable("resetPassword") { ResetPasswordScreen(navController) }
        composable("resetPasswordEmailSent") { ResetPasswordEmailSentScreen(navController) }
        composable("signUpSuccess") { SignUpSuccessScreen(navController) }
        composable("registerParkSuccess") { RegisterParkSuccessScreen(navController = navController) }

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
                needs = needs,
                comments = comments
            )
        }

        composable("donationsWithDialog") { DonationsScreen(navController = navController, showDialog = true) }

        composable("donacionEspecie") { EspecieFormScreen(navController) }
        composable("donacionMonetaria") { MonetariaFormScreen(navController) }
    }
}