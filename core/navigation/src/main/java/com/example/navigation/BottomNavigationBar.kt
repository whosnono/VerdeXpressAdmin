package com.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconColor = if (isSelected) Color(0xFF78B153) else Color(0xFF3F4946)
                val textColor = if (isSelected) Color(0xFF78B153) else Color(0xFF484C52)

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.route,
                            modifier = Modifier.size(24.dp),
                            tint = iconColor
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
                            color = textColor,
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
                    }
                )
            }
        }
    }
}