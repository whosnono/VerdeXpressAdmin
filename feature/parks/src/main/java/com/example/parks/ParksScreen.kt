package com.example.parks

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.design.MainAppBar

@Composable
fun ParksScreen() {
    Column {
        MainAppBar()

        Text("Parks Screen!!!!")
    }
}