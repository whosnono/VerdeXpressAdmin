package com.example.parks.ui

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.parks.data.ImageUploadManager
import com.example.parks.data.MapView
import com.example.parks.data.ParkDataA
import com.example.parks.data.ParkImageUploader
import com.example.parks.data.formatShortFirestoreDate
import com.example.parks.data.getParkDetails
import com.example.parks.data.rememberUserFullName
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
fun ParkDetailContent(park: ParkDataA, navController: NavController) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    // Estados para controlar la expansión de los dropdowns
    var situacionExpanded by remember { mutableStateOf(false) }
    var estadoActualExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var comNeedState by remember { mutableStateOf(park.comNeed ?: "") }
    var comAdState by remember { mutableStateOf(park.comAd ?: "") }
    var razonCierreState by remember { mutableStateOf(park.razonCierre ?: "") }

    // Estados para almacenar las selecciones e imagenes
    var selectedSituacion by remember { mutableStateOf(park.situacion) }
    var selectedEstadoActual by remember { mutableStateOf(park.estado) }
    var isSaving by remember { mutableStateOf(false) }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var newImageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Listas de opciones para los dropdowns
    val situaciones = listOf("Recibiendo donaciones", "Financiación completada", "En desarrollo", "Mantenimiento requerido", "Inactivo")
    val estadosActuales = listOf("Excelente", "Buena", "Regular", "Deficiente", "Muy deficiente")
    val situacionesEditables = listOf("En desarrollo", "Mantenimiento requerido")

    fun saveAllChanges(
        parkName: String,
        newImageUris: List<Uri>,
        selectedSituacion: String,
        selectedEstadoActual: String,
        currentParkSituacion: String,
        currentParkEstado: String,
        comNeed: String, // Añade este parámetro
        comAd: String,
        razonCierre: String,
        onComplete: (Boolean) -> Unit
    ) {
        // Verificar si hay cambios reales
        val hasChanges = newImageUris.isNotEmpty() ||
                selectedSituacion != currentParkSituacion ||
                (situacionesEditables.contains(selectedSituacion) &&
                        selectedEstadoActual != currentParkEstado) ||
                (selectedSituacion == "Mantenimiento requerido" && comNeed != park.comNeed) ||
                (selectedSituacion == "Inactivo" && razonCierre != park.razonCierre) ||
                (selectedSituacion == "En desarrollo" && comAd != park.comAd) // Añade esta condición

        if (!hasChanges) {
            onComplete(true)
            return
        }

        coroutineScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val parkQuery = firestore.collection("parques")
                    .whereEqualTo("nombre", parkName)
                    .get()
                    .await()
                if (parkQuery.isEmpty) {
                    onComplete(false)
                    return@launch
                }

                val document = parkQuery.documents[0]
                val updates = mutableMapOf<String, Any>()

                // 1. Actualizar campos básicos si cambiaron
                if (selectedSituacion != currentParkSituacion) {
                    updates["situacion_actual"] = selectedSituacion
                }

                if (situacionesEditables.contains(selectedSituacion) &&
                    selectedEstadoActual != currentParkEstado) {
                    updates["estado_actual"] = selectedEstadoActual
                }

                // 2. Procesar imágenes nuevas si las hay
                if (newImageUris.isNotEmpty()) {
                    val imageUploadManager = ImageUploadManager(context)
                    val downloadUrls = imageUploadManager.uploadImages(newImageUris)

                    if (downloadUrls.isNotEmpty()) {
                        val currentImages = document.get("imagenes") as? List<String> ?: emptyList()
                        updates["imagenes"] = currentImages + downloadUrls
                    }
                }

                // 3. Actualizar comentarios de necesidades si es necesario
                if (selectedSituacion == "Mantenimiento requerido" && comNeed != park.comNeed) {
                    updates["necesidades_com"] = comNeed
                }

                if (selectedSituacion == "Inactivo" && razonCierre != park.razonCierre) {
                    updates["motivo_cierre"] = razonCierre
                }

                if (selectedSituacion == "En desarrollo" && comAd != park.comAd) {
                    updates["comentarios_ad"] = comAd
                }

                // 4. Aplicar actualizaciones si hay cambios
                if (updates.isNotEmpty()) {
                    document.reference.update(updates).await()
                    onComplete(true)
                } else {
                    onComplete(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

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
                .weight(1f)
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
                            text = park.ubi,
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

            item {
                // Componente de subida de imágenes modificado
                ParkImageUploader(
                    parkName = park.nombre,
                    selectedImageUris = selectedImageUris,
                    onImagesSelected = { uris ->
                        selectedImageUris = uris
                    },
                    onImagesUploaded = { urls ->
                        newImageUrls = urls
                        selectedImageUris = emptyList() // Limpiar las URIs después de subir
                    },
                    situation = selectedSituacion
                )
                Spacer(modifier = Modifier.height(25.dp))
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
                                }
                            )
                        }
                    }
                }
            }

            if(selectedSituacion == "En desarrollo"){
                item{
                    Text(
                        text = "Comentarios adicionales",
                        fontSize = 20.sp,
                        fontFamily = SFProDisplayBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BasicTextField(
                        value = comAdState,
                        onValueChange = { newValue ->
                            comAdState = newValue
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable() // ¡Importante para que funcione!
                            .background(
                                color = Color.White.copy(alpha = 0.2f)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                            .heightIn(min = 100.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = SFProDisplayM,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if(selectedSituacion == "Inactivo"){
                item{
                    Text(
                        text = "Razon de Inactividad",
                        fontSize = 20.sp,
                        fontFamily = SFProDisplayBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if(park.razonCierre.isEmpty()){
                        BasicTextField(
                            value = razonCierreState,
                            onValueChange = { newValue ->
                                razonCierreState = newValue
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusable() // ¡Importante para que funcione!
                                .background(
                                    color = Color.White.copy(alpha = 0.2f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                                .heightIn(min = 100.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = SFProDisplayM,
                                fontSize = 16.sp
                            )
                        )
                    }else{
                        Text(
                            text = razonCierreState,
                            fontSize = 16.sp,
                            fontFamily = SFProDisplayM,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
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

            if(selectedSituacion == "Mantenimiento requerido"){
                item{
                    Text(
                        text = "Comentarios adicionales de las necesidades",
                        fontSize = 20.sp,
                        fontFamily = SFProDisplayBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BasicTextField(
                        value = comNeedState,
                        onValueChange = { newValue ->
                            comNeedState = newValue
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable() // ¡Importante para que funcione!
                            .background(
                                color = Color.White.copy(alpha = 0.2f)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                            .heightIn(min = 100.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = SFProDisplayM,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopStart)
                    ) {
                        OutlinedButton(
                            onClick = { estadoActualExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth(),
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
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = selectedEstadoActual,
                        fontSize = 16.sp,
                        fontFamily = SFProDisplayM,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                Button(
                    onClick = {
                        if (selectedImageUris.isEmpty() &&
                            selectedSituacion == park.situacion &&
                            selectedEstadoActual == park.estado &&
                            (selectedSituacion != "Mantenimiento requerido" || comNeedState == park.comNeed) &&
                            (selectedSituacion != "Inactivo" || razonCierreState == park.razonCierre)&&
                            (selectedSituacion != "En desarrollo" || comAdState == park.comAd)) {
                            navController.navigate("Parques")
                        } else {
                            isSaving = true
                            coroutineScope.launch {
                                saveAllChanges(
                                    parkName = park.nombre,
                                    newImageUris = selectedImageUris,
                                    selectedSituacion = selectedSituacion,
                                    selectedEstadoActual = selectedEstadoActual,
                                    currentParkSituacion = park.situacion,
                                    currentParkEstado = park.estado,
                                    comNeed = comNeedState,
                                    comAd = comAdState,
                                    razonCierre = razonCierreState,
                                    onComplete = { success ->
                                        isSaving = false
                                        if (success) {
                                            selectedImageUris = emptyList()
                                            showSuccessDialog = true
                                            successMessage = "Los cambios se han guardado correctamente"
                                        }
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verde,
                        contentColor = Color.White
                    )
                ) {
                    if (isSaving) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando...")
                        }
                    } else {
                        Text(
                            "Guardar cambios",
                            fontFamily = SFProDisplayBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

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
}