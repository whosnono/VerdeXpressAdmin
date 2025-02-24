package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.design.MainAppBar
import com.example.parks.data.ParkData

@Composable
fun ParksScreen(viewModel: ParkViewModel = viewModel()) {
    // Lanzar la carga de datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.fetchParks()
    }

    // Obtener la lista de parques desde el ViewModel
    val parks = viewModel.parksList.value

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de aplicación
        MainAppBar()

        // Lista de parques
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(parks) { park ->
                ParkItem(imageUrl = park.primeraImagen, parkName = park.nombre)
            }
        }
    }
}

@Composable
fun ParkItem(imageUrl: String, parkName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray) // Fondo del contenedor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen del parque
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del parque",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            // Nombre del parque
            Text(
                text = parkName,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}