package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.design.MainAppBar
import com.example.design.R
import androidx.navigation.NavController

val SFProDisplayBold = FontFamily(Font(R.font.sf_pro_display_bold))
val RobotoBold = FontFamily(Font(R.font.roboto_bold))

@Composable
fun ParksScreen(viewModel: ParkViewModel = viewModel(), navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.fetchParks()
    }

    val parks = viewModel.parksList.value
    // Reorganizamos la lista para mostrarla como cuadrícula en un LazyColumn
    val rows = parks.chunked(2)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barra de navegación (fija)
            MainAppBar()

            // Usamos un único LazyColumn para el contenido scrollable
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Elemento 1: El título "Parques"
                item {
                    Text(
                        text = "Parques",
                        fontFamily = SFProDisplayBold,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)
                    )
                }

                // Elementos para las filas de parques (2 parques por fila)
                items(rows) { rowParks ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowParks.forEach { park ->
                            Box(modifier = Modifier.weight(1f)) {
                                ParkItem(imageUrl = park.primeraImagen, parkName = park.nombre)
                            }
                        }

                        // Si hay un solo elemento en la fila, añadimos un espacio vacío
                        if (rowParks.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // FAB en posición absoluta
        FloatingActionButton(
            onClick = {
                navController.navigate("registerPark")
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            containerColor = Color(0xFF78B153),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Park", tint = Color.White)
        }
    }
}

@Composable
fun ParkItem(imageUrl: String, parkName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Imagen del parque",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.65f))
        ) {
            Text(
                text = parkName,
                fontFamily = RobotoBold,
                fontSize = 14.sp,
                color = Color(0xFF484C52),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}