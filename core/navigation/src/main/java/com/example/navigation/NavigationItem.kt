package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector) {
    object Home : NavigationItem("Inicio", Icons.Outlined.Home)
    object Parks : NavigationItem("Parques", Icons.Outlined.Place)
    object Donations : NavigationItem("Donaciones", Icons.Outlined.FavoriteBorder)
    object Notifications : NavigationItem("Notificaciones", Icons.Outlined.Notifications)
    object Profile : NavigationItem("Perfil", Icons.Outlined.Person)
}