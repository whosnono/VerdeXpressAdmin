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
fun DonationsDetails(
    navController: NavController,
    parque: String,
    fecha: String,
    ubicacion: String,
    donante: String,
    telefono: String,
    cantidad: String,
    recurso: String,
    condicion: String
) {
    val fechaFormateada = fecha.replace("-", "/") // Convierte "dd-MM-yyyy" a "dd/MM/yyyy"
    val cantidadFormateado = if (cantidad == "1") "1 pieza" else "$cantidad piezas"

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

        titulos(
            titulo = "Donado por"
        )

        Spacer(modifier = Modifier.height(8.dp))

        detallesDonante(nombre = donante, numero = telefono)

        Spacer(modifier = Modifier.height(7.dp))

        parqueTitulo(Parque = parque)

        Spacer(modifier = Modifier.height(7.dp))

        textos(texto = ubicacion)
        Spacer(modifier = Modifier.height(11.dp))

        titulos(titulo = "Recurso a donar")
        Spacer(modifier = Modifier.height(7.dp))

        textos(texto = recurso)

        Spacer(modifier = Modifier.height(7.dp))

        titulos(titulo = "Cantidad")
        Spacer(modifier = Modifier.height(7.dp))

        textos(texto = cantidadFormateado)

        Spacer(modifier = Modifier.height(7.dp))

        titulos(titulo = "Condicion del recurso")
        Spacer(modifier = Modifier.height(7.dp))

        textos(texto = if (condicion.isNullOrEmpty()) "Desconocida" else condicion)


        Spacer(modifier = Modifier.height(7.dp))

        titulos(titulo = "Fecha estimada de de entrega")
        Spacer(modifier = Modifier.height(7.dp))

        textos(texto = fechaFormateada)



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
fun parqueTitulo(Parque: String) {
    Text(
        text = Parque,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = TextStyle(
            fontSize = 22.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun titulos(titulo: String) {
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
fun textos(texto: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = texto,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
    }
}

@Composable
fun detallesDonante(nombre: String, numero: String) {
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

