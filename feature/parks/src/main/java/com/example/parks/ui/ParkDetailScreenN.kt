package com.example.parks.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.design.MainAppBar
import com.example.parks.data.MapView
import com.example.parks.data.ParkDataA
import com.example.parks.data.acceptPark
import com.example.parks.data.formatShortFirestoreDate
import com.example.parks.data.getParkDetails
import com.example.parks.data.rejectPark
import com.example.parks.data.rememberUserFullName

@Composable
fun ParkDetailScreenN(parkName: String?, latitud: String? = null, longitud: String? = null, navController: NavController) {
    // Estados para almacenar los datos del parque y posibles errores
    val parkState = remember { mutableStateOf<ParkDataA?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

    // Efecto lanzado para obtener los detalles del parque desde Firestore
    LaunchedEffect(parkName) {
        if (parkName != null) {
            getParkDetails(
                parkName = parkName,
                onSuccess = { park ->
                    // Usamos los datos de Firestore pero sobrescribimos las coordenadas si vienen por navegación
                    parkState.value = park.copy(
                        latitud = latitud ?: park.latitud,  // Usa la latitud de navegación o la de Firestore
                        longitud = longitud ?: park.longitud  // Usa la longitud de navegación o la de Firestore
                    )
                },
                onFailure = { exception ->
                    errorState.value = "Error: ${exception.message}"
                }
            )
        }
    }

    // Renderiza el contenido según el estado actual
    when {
        parkState.value != null -> {
            ParkDetailContentN(park = parkState.value!!, navController)
        }
        errorState.value != null -> {
            Text(text = "Error: ${errorState.value}")
        }
        else -> {
            Text(text = "Cargando...")
        }
    }
}

@Composable
fun ParkDetailContentN(park: ParkDataA, navController: NavController) {
    // Estados para manejar dialogos, mensaje de exito y de error
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Estados para almacenar el estado actual
    var selectedEstadoActual by remember { mutableStateOf(park.estado) }

    // Contenido principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        MainAppBar()

        // Contenido del parque
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // A partir de aqui se definen los elementos que apareceran de la pantalla,
            // como el nombre del parque, ubicación, etc.
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 23.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        modifier = Modifier
                            .clickable {
                                // Lógica para manejar el clic en el ícono de retroceso
                                navController.navigate("Parques")
                            }
                    )

                    Text(
                        text = "Administracion de parques",
                        fontFamily = SFProDisplayBold,
                        fontSize = 20.sp,
                        color = verde,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            item{
                Column(modifier = Modifier.fillMaxWidth()){
                    Text(
                        text = "Solicitado por",
                        fontSize = 20.sp,
                        fontFamily = SFProDisplayBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = rememberUserFullName(park.usuarioId),
                        fontSize = 12.sp,
                        fontFamily = SFProDisplayM
                    )
                }
            }

            // Nombre del parque y ubicación
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna para texto
                    Column(
                        modifier = Modifier
                            .weight(0.5f)  // Ocupa la mayor parte del ancho
                    ) {
                        Text(
                            text = park.nombre,
                            fontSize = 25.sp,
                            fontFamily = SFProDisplayBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = park.ubi,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Contenedor para el MapView
                    Box(
                        modifier = Modifier
                            .weight(0.6f)  // Ocupa menos espacio
                            .height(150.dp)
                            .aspectRatio(1.3f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        // Con esta funcion se puede visualizar el mapa
                        MapView(
                            latitud = park.latitud,
                            longitud = park.longitud
                        )
                    }
                }
            }

            // Imágenes del parque
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp)
                ) {
                    items(park.imagenes) { imagenUrl ->
                        Box(
                            modifier = Modifier
                                .size(width = 140.dp, height = 100.dp)
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = imagenUrl, // URL de la imagen
                                contentDescription = "Imagen del parque",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Indicador de página de imágenes (línea verde)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(4.dp)
                            .background(verde)
                    )
                }
            }

            // Necesidades del parque (lista con bullets)
            item {
                Text(
                    text = "Necesidades del parque",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                park.necesidades.forEach { necesidad ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(verde, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = necesidad, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Estado actual del parque
            item {
                Text(
                    text = "Estado actual del parque",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = selectedEstadoActual,
                    fontSize = 14.sp,
                    fontFamily = SFProDisplayBold,
                    color = verde,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }

            // Aqui se despliegan los comentarios que hizo el usuario sobre el parque
            item {
                Text(
                    text = "Comentarios",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(1.dp, verde),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Por ${rememberUserFullName(park.usuarioId)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = formatShortFirestoreDate(park.fecha),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = park.comentarios,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón para rechazar el parque
                    Button(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = verde
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, verde)
                    ) {
                        Text("Rechazar parque")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botón para aceptar el parque
                    Button(
                        onClick = { showAcceptDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verde,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar parque")
                    }
                }
            }
        }
    }
    // Mostrar el dialogo para aceptar el parque
    if (showAcceptDialog) {
        ParkAcceptDialog(
            parkName = park.nombre,
            onAccept = { password ->
                acceptPark(park.nombre, password) { success, message ->
                    if (success) {
                        successMessage = "Parque aprobado exitosamente"
                        showSuccessDialog = true
                        showAcceptDialog = false
                    } else {
                        errorMessage = message
                    }
                }
            },
            onDismiss = { showAcceptDialog = false }
        )
    }
    // Mostar el dialogo de rechazo del parque
    if (showRejectDialog) {
        ParkRejectDialog(
            parkName = park.nombre,
            onReject = { reason, password ->
                rejectPark(park.nombre, reason, password) { success, message ->
                    if (success) {
                        successMessage = "Parque rechazado exitosamente"
                        showSuccessDialog = true
                        showRejectDialog = false
                    } else {
                        errorMessage = message
                    }
                }
            },
            onDismiss = { showRejectDialog = false }
        )
    }
    //Al aprobar o rechazar un parque, se cierra el popup y se abre un mensaje de alerta que
    //avisa que se a realizado con exito la accion
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController.popBackStack()
            },
            containerColor = Color.White,
            title = { Text("Éxito") },
            text = { Text(successMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verde,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Aceptar"
                    )
                }
            }
        )
    }
    //Al aprobar o rechazar un parque, se cierra el popup y se abre un mensaje de alerta que
    //avisa cualquier error que haya ocurrido
    if(showErrorDialog){
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                navController.popBackStack()
            },
            containerColor = Color.White,
            title = { Text("Error") },
            text = { Text(errorMessage.toString()) },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verde,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Aceptar"
                    )
                }
            }
        )
    }
}