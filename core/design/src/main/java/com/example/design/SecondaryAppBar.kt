package com.example.design

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryAppBar(showIcon: Boolean = false, onIconClick: () -> Unit = {}) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "VerdeXpress",
                    fontSize = 30.sp,
                    fontFamily = SFProDisplayBold
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF78B153)
        ),
        navigationIcon = {
            if (showIcon) {
                IconButton(onClick = onIconClick) { // Usa onIconClick aquí
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            }
        }
    )
}

