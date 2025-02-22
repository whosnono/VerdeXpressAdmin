package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Parks,
        NavigationItem.Donations,
        NavigationItem.Notifications,
        NavigationItem.Profile
    )
    BottomNavigationBar(navController = navController, items = items)
}