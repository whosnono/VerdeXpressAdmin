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
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.example.parks.data.getAddressFromCoordinates
import com.example.parks.data.updateParkField
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ParkDetailScreenA(parkName: String?, latitud: String? = null, longitud: String? = null, navController: NavController) {
    val parkState = remember { mutableStateOf<ParkDataA?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

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
                    println("Coordenadas finales: Latitud=${parkState.value?.latitud}, Longitud=${parkState.value?.longitud}")
                },
                onFailure = { exception ->
                    errorState.value = "Error: ${exception.message}"
                }
            )
        }
    }

    when {
        parkState.value != null -> {
            ParkDetailContent(park = parkState.value!!, navController)
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
fun rememberUserFullName(userId: String): String {
    var userFullName by remember { mutableStateOf("Usuario desconocido") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre") ?: ""
                    val apellido = document.getString("apellidos") ?: ""
                    userFullName = if (nombre.isNotEmpty() && apellido.isNotEmpty()) {
                        "$nombre $apellido"
                    } else if (nombre.isNotEmpty()) {
                        nombre
                    } else {
                        "Usuario anónimo"
                    }
                }
                .addOnFailureListener {
                    userFullName = "Error al cargar usuario"
                }
        }
    }
    return userFullName
}

@Composable
fun ParkDetailContent(park: ParkDataA, navController: NavController) {
    // Estados para controlar la expansión de los dropdowns
    var situacionExpanded by remember { mutableStateOf(false) }
    var estadoActualExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Estados para almacenar las selecciones
    var selectedSituacion by remember { mutableStateOf(park.situacion) }
    var selectedEstadoActual by remember { mutableStateOf(park.estado) }

    // Listas de opciones para los dropdowns (puedes obtenerlas de la base de datos)
    val situaciones = listOf("Recibiendo donaciones", "Financiación completada", "En desarrollo", "Mantenimiento requerido", "Inactivo")
    val estadosActuales = listOf("Excelente","Buena", "Regular", "Deficiente", "Muy deficiente")
    val situacionesEditables = listOf("En desarrollo", "Mantenimiento requerido")

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
                        text = "Parques Activos",
                        fontFamily = SFProDisplayBold,
                        fontSize = 20.sp,
                        color = verde,
                        modifier = Modifier.align(Alignment.Center)
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
                    // Columna para texto (ocupa más espacio)
                    Column(
                        modifier = Modifier
                            .weight(0.5f)  // Ocupa la mayor parte del ancho
                    ) {
                        Text(
                            text = park.nombre,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = getAddressFromCoordinates(
                                context,
                                park.latitud.toDouble(),
                                park.longitud.toDouble()
                            ),
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Contenedor para el MapView (ocupa menos espacio)
                    Box(
                        modifier = Modifier
                            .weight(0.6f)  // Ocupa menos espacio
                            .height(150.dp)  // Altura fija más baja
                            .aspectRatio(1.3f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
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

            // Situación del parque (dropdown)
            item {
                Text(
                    text = "Situación del parque",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    OutlinedButton(
                        onClick = { situacionExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedSituacion,
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expandir",
                                tint = Color.DarkGray
                            )
                        }
                    }

                    // DropdownMenu para la situación del parque
                    DropdownMenu(
                        expanded = situacionExpanded,
                        onDismissRequest = { situacionExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(Color.White)
                    ) {
                        situaciones.forEach { situacion ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = situacion,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                },
                                onClick = {
                                    selectedSituacion = situacion
                                    situacionExpanded = false
                                    updateParkField(park.nombre, "situacion_actual", situacion) { success ->
                                        if (success) {
                                            // Opcional: Mostrar mensaje de éxito
                                            println("Situación actualizada correctamente")
                                        } else {
                                            // Opcional: Mostrar mensaje de error
                                            println("Error al actualizar la situación")
                                        }
                                    }
                                }
                            )
                        }
                    }
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

            // Estado actual del parque (dropdown condicional)
            item {
                Text(
                    text = "Estado actual del parque",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (situacionesEditables.contains(selectedSituacion)) {
                    // Mostrar dropdown editable
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopStart)
                    ) {
                        OutlinedButton(
                            onClick = { estadoActualExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.DarkGray
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedEstadoActual,
                                    fontSize = 16.sp,
                                    color = Color.DarkGray
                                )
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Expandir",
                                    tint = Color.DarkGray
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = estadoActualExpanded,
                            onDismissRequest = { estadoActualExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .background(Color.White)
                        ) {
                            estadosActuales.forEach { estado ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = estado,
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )
                                    },
                                    onClick = {
                                        selectedEstadoActual = estado
                                        estadoActualExpanded = false
                                        updateParkField(park.nombre, "estado_actual", estado) { success ->
                                            // Manejar éxito/error
                                        }
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Mostrar solo texto (no editable)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = selectedEstadoActual,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Comentarios
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
                                text = "Por \"${rememberUserFullName(park.usuarioId)}\"",
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
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

fun formatShortFirestoreDate(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    return dateFormat.format(timestamp.toDate())
}

fun getParkDetails(parkName: String, onSuccess: (ParkDataA) -> Unit, onFailure: (Exception) -> Unit) {
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    firestore.collection("parques")
        .whereEqualTo("nombre", parkName)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val document = result.documents[0]
                val nombre = document.getString("nombre") ?: "Desconocido"
                val imagenes = document.get("imagenes") as? List<String> ?: emptyList()
                val primeraImagen = imagenes.firstOrNull() ?: ""
                val necesidades = document.get("necesidades") as? List<String> ?: emptyList()
                val estado = document.getString("estado_actual") ?: "Desconocido"
                val comentarios = document.getString("comentarios") ?: "Sin comentarios"
                val latitud = document.getString("latitud") ?: "0.0" // Obtener latitud
                val longitud = document.getString("longitud") ?: "0.0" // Obtener longitud
                val situacion = document.getString("situacion_actual") ?: "Desconocido"
                val usuarioId = document.getString("registro_usuario") ?: "Desconocido"
                val fecha = document.getTimestamp("created_at") ?: Timestamp.now()

                if(latitud.toDoubleOrNull()== null || longitud.toDoubleOrNull() == null){
                    onFailure(Exception("Coordenadas no válidas"))
                } else {
                    val parkData = ParkDataA(nombre, imagenes, primeraImagen, necesidades, estado, comentarios, latitud, longitud, situacion, usuarioId, fecha)
                    onSuccess(parkData)
                }
            } else {
                onFailure(Exception("Parque no encontrado"))
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

data class ParkDataA(val nombre: String, val imagenes: List<String>, val primeraImagen: String, val necesidades: List<String>, val estado: String, val comentarios: String, val longitud: String, val latitud: String, val situacion: String, val usuarioId: String, val fecha: Timestamp)