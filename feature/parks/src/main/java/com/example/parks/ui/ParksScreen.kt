package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.design.MainAppBar
import com.example.design.R

val SFProDisplayBold = FontFamily(Font(R.font.sf_pro_display_bold))
val RobotoBold = FontFamily(Font(R.font.roboto_bold))

@Composable
fun ParksScreen(viewModel: ParkViewModel = viewModel(), navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.fetchParks()
    }

    val parks = viewModel.parksList.value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainAppBar()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = "Parques",
                        fontFamily = SFProDisplayBold,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)
                    )
                }

                items(parks.chunked(2)) { parksPair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        parksPair.forEach { park ->
                            Box(modifier = Modifier.weight(1f)) {
                                ParkItem(
                                    imageUrl = park.primeraImagen,
                                    parkName = park.nombre
                                )
                            }
                        }
                        // Add an empty Box if there's an odd number of parks
                        if (parksPair.size < 2) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp)) // Añadir padding inferior al final de cada fila

                }
            }
        }
    }
}

@Composable
fun ParkItem(imageUrl: String, parkName: String) {
    Box(
        modifier = Modifier
            .width(183.dp)
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