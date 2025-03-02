package com.example.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.design.R

@Composable
fun IntroScreen(onTimeout: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF78B153)), // Fondo verde
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text( //El texto
                text = "VerdeXpress",
                fontSize = 30.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                fontWeight = FontWeight(700),
                color = Color(0xFFFFFFFF),
                letterSpacing = 0.25.sp,
            )
        }
    }

    LaunchedEffect(true) {
        delay(2000) // Espera 4 segundos antes de mostrar la app
        onTimeout()
    }
}