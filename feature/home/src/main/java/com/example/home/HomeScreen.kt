package com.example.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.design.MainAppBar


@Composable
fun HomeScreen() {
    Column {
        MainAppBar()

        Text("Home Screen!!!!")
    }
}

