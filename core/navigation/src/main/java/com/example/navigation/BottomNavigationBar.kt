package com.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.design.R

val SFProDisplayMedium = FontFamily(Font(R.font.sf_pro_display_medium))

@Composable
fun BottomNavigationBar(navController: NavController, items: List<NavigationItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
    ) {
        // Barra gris
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp) // Ajusta la altura de la barra gris aquí
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFFEEEEEE)) // Gris claro, puedes ajustar el color
        )

        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.route,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.route,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = SFProDisplayMedium
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF78B153),
                        unselectedIconColor = Color(0xFF3F4946),
                        selectedTextColor = Color(0xFF78B153),
                        unselectedTextColor = Color(0xFF484C52),
                        indicatorColor = Color.Transparent
                    ),
                    alwaysShowLabel = true
                )
            }
        }
    }
}