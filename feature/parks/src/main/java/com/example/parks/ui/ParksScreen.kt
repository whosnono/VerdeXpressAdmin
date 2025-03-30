package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
val SFProDisplayM = FontFamily(Font(R.font.sf_pro_display_medium))
val RobotoBold = FontFamily(Font(R.font.roboto_bold))
val verde = Color(0xFF78B153)

@Composable
fun ParksScreen(viewModel: ParkViewModel = viewModel(), navController: NavController, showContent: Boolean = true) {

    var showFilterDialog by remember { mutableStateOf(false) }

    val filters by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Map<String, String>?>("filters", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchApprovedParks()
        viewModel.fetchNewParks()
    }

    LaunchedEffect(filters) {
        filters?.let { viewModel.applyFilters(it) }
    }

    val parksApproved = viewModel.parksList.value
    val parksNew = viewModel.newParksList.value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainAppBar()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp)
            ) {
                // Título principal
                item {
                    Box(modifier = Modifier.fillMaxWidth()){
                        Text(
                            text = "Parques",
                            fontFamily = SFProDisplayBold,
                            fontSize = 25.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        if (showContent) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = "Filter",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                                    .clickable {
                                        showFilterDialog = true
                                    }
                            )
                        }
                    }
                }

                // Sección Parques Activos
                item {
                    Text(
                        text = "Parques Activos",
                        fontFamily = SFProDisplayBold,
                        fontSize = 20.sp,
                        color = verde,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                // Grid de parques aprobados
                items(parksApproved.chunked(2)) { parksPair ->
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
                                    park = park.nombre,
                                    latitud = park.latitud,
                                    longitud = park.longitud,
                                    navController = navController,
                                    isApproved = true
                                )
                            }
                        }
                        // Añadir un Box vacío si hay un número impar de parques
                        if (parksPair.size < 2) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Sección Administración de Parques
                item {
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = "Administración de Parques",
                        fontFamily = SFProDisplayBold,
                        fontSize = 20.sp,
                        color = verde,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                // Subsección Nuevos parques
                item {
                    Text(
                        text = "Nuevos parques",
                        fontFamily = SFProDisplayBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                // Grid de nuevos parques
                items(parksNew.chunked(2)) { parksPair ->
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
                                    park = park.nombre,
                                    latitud = park.latitud,
                                    longitud = park.longitud,
                                    navController = navController,
                                    isApproved = false
                                )
                            }
                        }
                        // Añadir un Box vacío si hay un número impar de parques
                        if (parksPair.size < 2) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Espacio para el bottom navigation
                item {
                    Spacer(modifier = Modifier.height(56.dp))
                }
            }
        }
    }
    if (!showContent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )
    }

    if (showFilterDialog) {
        SlideInFilterPanel(
            isVisible = showFilterDialog,
            onDismiss = { showFilterDialog = false },
            onApply = { filters ->
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("filters", filters)
            }
        )
    }
}

@Composable
fun ParkItem(imageUrl: String, park: String, navController: NavController, latitud: String? = null, longitud: String? = null, isApproved: Boolean) {
    Box(
        modifier = Modifier
            .width(183.dp)
            .height(122.dp)
            .clickable {
                val route = if (isApproved) {
                    "parkDetailA/${park}?latitud=${latitud}&longitud=${longitud}"
                } else {
                    "parkDetailN/${park}?latitud=${latitud}&longitud=${longitud}"
                }
                navController.navigate(route)
            }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Imagen del parque",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(verde.copy(alpha = 0.7f))
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = park,
                fontFamily = RobotoBold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}