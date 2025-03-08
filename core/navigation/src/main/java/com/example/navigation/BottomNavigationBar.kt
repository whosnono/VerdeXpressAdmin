package com.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip

val SFProDisplayMedium = FontFamily(Font(R.font.sf_pro_display_medium))

@Composable
fun BottomNavigationBar(navController: NavController, items: List<NavigationItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column {
            // Rounded top part with background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(color = Color(0xFFF5F6F7))
            )

            // Navigation menu with items
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFFFFFF))
            ) {
                // Row for the navigation items with proper padding
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 25.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    items.forEach { item ->
                        val isSelected = currentRoute == item.route

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            launchSingleTop = true
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            restoreState = true
                                        }
                                    }
                                }
                        ) {
                            // Green indicator line at the very top for selected items
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(2.dp)
                                        .background(color = Color(0xFF78B153))
                                        .align(Alignment.TopCenter)
                                )
                            }

                            // Column for icon and text, centered in the box
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.route,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) Color(0xFF78B153) else Color(0xFF3F4946)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.route,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = SFProDisplayMedium,
                                    color = if (isSelected) Color(0xFF78B153) else Color(0xFF484C52)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}