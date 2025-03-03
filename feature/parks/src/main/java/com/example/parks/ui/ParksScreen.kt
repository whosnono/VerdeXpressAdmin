package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

    Column(modifier = Modifier.fillMaxSize()) {

        MainAppBar()

        Text(
            text = "Parques",
            fontFamily = SFProDisplayBold,
            fontSize = 25.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(parks) { park ->
                ParkItem(imageUrl = park.primeraImagen, parkName = park.nombre)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                navController.navigate("registerPark") // Navegar a la pantalla de registro
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