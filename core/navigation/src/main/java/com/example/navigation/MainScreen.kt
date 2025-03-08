package com.example.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val showBottomBar = remember { mutableStateOf(true) }

    // Observar la ruta actual para determinar cuándo mostrar el FAB
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Crear configuración del FAB basada en la ruta actual
    val fabConfig by remember(currentRoute) {
        derivedStateOf {
            when (currentRoute) {
                "Parques" -> FabConfig(
                    visible = true,
                    route = "registerPark",
                    icon = Icons.Default.Add,
                    contentDescription = "Add Park"
                )
                else -> null
            }
        }
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showBottomBar.value = when (destination.route) {
                "signIn" -> false
                "signUp" -> false
                "signUpSuccess" -> false
                "resetPassword" -> false
                "resetPasswordEmailSent" -> false
                "registerPark" -> false
                "map" -> false
                "registerParkSuccess" -> false
                "map?name={name}&desc={desc}&status={status}&needs={needs}&comments={comments}" -> false
                "registerPark?lat={lat}&lon={lon}&address={address}&name={name}&desc={desc}&status={status}&imageUris={imageUris}&needs={needs}&comments={comments}" -> false
                else -> true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar.value) {
                BottomNavigationBar(
                    navController = navController,
                    items = listOf(
                        NavigationItem.Home,
                        NavigationItem.Parks,
                        NavigationItem.Donations,
                        NavigationItem.Notifications,
                        NavigationItem.Profile
                    ),
                    fabConfig = fabConfig
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}