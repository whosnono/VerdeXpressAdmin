package com.example.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.parks.data.LocalBottomBarState


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val showBottomBar = remember { mutableStateOf(true) }

     LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showBottomBar.value = when (destination.route) {
                "Inicio" -> true
                "Parques" -> true
                "Donaciones" -> true
                "Notificaciones" -> true
                "Perfil" -> true
                "parkDetailA/{parkName}?latitud={latitud}&longitud={longitud}" -> true
                "parkDetailN/{parkName}?latitud={latitud}&longitud={longitud}" -> true
                "DonacionesEspecie" -> true
                "DonacionesMonetarias" -> true
                "DonationsDetails/{donationId}" -> true
                else -> false
            }
        }
    }

    CompositionLocalProvider(LocalBottomBarState provides showBottomBar) {
        Scaffold(
            containerColor = Color(0xFFFFFFFF),
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
                        )
                    )
                }
            }
        ) { innerPadding ->
            AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }
}