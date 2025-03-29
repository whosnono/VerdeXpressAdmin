package com.example.donations.ui.especie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.R.font

@Composable
fun DonationsDetails(navController: NavController,
                     parque: String,
                     fecha: String,
                     ubicacion: String,
                     donante: String,
                     telefono: String,
                     estado: String) {
    Column(modifier = Modifier.fillMaxSize()) {

        MainAppBar()

        // Barra inferior "Donaciones en Especie"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Donaciones en Especie",
                style = TextStyle(
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78B153)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        donacion(
            titulo = "Donado por"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Donante: $donante")
        Text(text = "Teléfono: $telefono")
        Text(text = "Parque: $parque")
        Text(text = "Fecha: $fecha")
        Text(text = "Ubicación: $ubicacion")
        Text(text = "Estado: $estado")


        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BotonRechazar()

            BotonAceptar()
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
fun donacion(titulo: String) {
    Text(
        text = titulo,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun detalles(nombre: String, numero: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = nombre,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = numero,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
fun BotonRechazar() {
    Button(
        onClick = { /* Lógica para rechazar */ },
        modifier = Modifier
            .width(177.dp)
            .height(45.dp),
        border = BorderStroke(2.dp, Color(0xFF78B153)), // Agrega el borde
        shape = RoundedCornerShape(5.dp), // Agrega la forma
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Color de fondo del botón
            contentColor = Color(0xFF78B153) // Color del texto del botón
        )
    ) {
        Text(
            text = "Rechazar Donación",
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(700),
                letterSpacing = 0.25.sp,
            )
        )
    }
}


@Composable
fun BotonAceptar() {
    Button(onClick = { /* Lógica para aceptar */ },
        modifier = Modifier
            .width(177.dp)
            .height(45.dp),
        shape = RoundedCornerShape(5.dp), // Agrega la forma
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF78B153), // Color de fondo del botón
            contentColor = Color.White // Color del texto del botón
        )
    ) {
        Text(
            text = "Aceptar Donación",
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(700),
                letterSpacing = 0.25.sp,
            )
        )
    }
}

