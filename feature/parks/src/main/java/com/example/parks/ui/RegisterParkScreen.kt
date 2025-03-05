package com.example.parks.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.parks.data.ImageUploadManager
import com.example.parks.data.saveParkToFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterParkScreen(navController: NavController, sharedViewModel: SharedViewModel, latitude: String? = null, longitude: String? = null, address: String? = null, parkNameArg: String? = null, descriptionArg: String? = null, statusArg: String? = null, imageUrisArg: List<Uri>? = null, needsArg: List<String>? = null, commentsArg: String? = null) {
fun RegisterParkScreen(
    navController: NavController,
    latitude: String? = null,
    longitude: String? = null,
    address: String? = null
) {
    val verdeBoton = Color(0xFF78B153)
    var showNeedsDialog by remember { mutableStateOf(false) }
    var selectedNeeds by rememberSaveable { mutableStateOf(needsArg ?: emptyList()) }
    val roundedShape = RoundedCornerShape(12.dp)
    // Contenedor de imágenes con scroll
    val scrollState = rememberScrollState()
    // Estados para los campos de texto
    var parkName by rememberSaveable { mutableStateOf(parkNameArg ?: "") }
    var location by remember { mutableStateOf(address ?: "") }
    var description by rememberSaveable { mutableStateOf(descriptionArg ?: "") }
    var comments by rememberSaveable { mutableStateOf(commentsArg ?: "") }
    var selectedOptionText by rememberSaveable { mutableStateOf(statusArg ?: "") }
    // Estado para manejar el mensaje de error con Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    // Estado para manejar el mensaje de error
    var imageError by remember { mutableStateOf<String?>(null) }
    // Obtener las URIs desde el ViewModel
    val imageUrisFromViewModel by sharedViewModel.selectedImageUris.collectAsState()
    // Estado para las URIs de las imágenes
    var selectedImageUris by rememberSaveable { mutableStateOf(imageUrisFromViewModel) }
    // Usar la lógica de subida de imágenes
    val context = LocalContext.current
    // Estado para controlar la visibilidad del diálogo de permisos
    var showPermissionDialog by remember { mutableStateOf(false) }
    // Geopoint del parque
    var parkLatitude by remember { mutableStateOf(latitude) }
    var parkLongitude by remember { mutableStateOf(longitude) }

    val coroutineScope = rememberCoroutineScope()
    val imageUploadManager = remember { ImageUploadManager(context) }
    var isUploading by remember { mutableStateOf(false) }

    // Lanzador para seleccionar imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            if (selectedImageUris.size + uris.size > 5) {
                imageError = "Solo puedes subir un máximo de 5 imágenes."
            } else {
                selectedImageUris = selectedImageUris + uris
                imageError = null
            }
        } else {
            imageError = "Debes seleccionar al menos una imagen."
        }
    }

    // Mostrar el Snackbar si hay un error
    LaunchedEffect(imageError) {
        imageError?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            imageError = null // Limpiar el mensaje de error después de mostrarlo
        }
    }

    // Almacenar las URIs en el ViewModel antes de navegar
    LaunchedEffect(selectedImageUris) {
        sharedViewModel.setImageUris(selectedImageUris)
    }

    LaunchedEffect(Unit) {
        sharedViewModel.setImageUris(emptyList())
    }

    // Función para abrir el selector de imágenes
    fun openImagePicker() {
        if (selectedImageUris.size >= 5) {
            imageError = "Solo puedes subir un máximo de 5 imágenes."
        } else {
            imagePickerLauncher.launch("image/*")
        }
    }

    // Lanzador para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            // Mostrar un mensaje al usuario indicando que el permiso es necesario
            showPermissionDialog = true
        }
    }

    // Función para verificar y solicitar permisos
    fun checkAndRequestPermission() {
        when {
            // Para Android 13 (API 33) y posteriores
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }

                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            }
            // Para versiones anteriores a Android 13
            else -> {
                when {
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }

                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }
    // Define un NestedScrollConnection para manejar los gestos de desplazamiento anidados
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Permite que el desplazamiento vertical tenga prioridad
                return Offset.Zero
            }
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // Maneja el desplazamiento horizontal
                return Offset.Zero
            }
            override suspend fun onPreFling(available: Velocity): Velocity {
                // Permite que el fling vertical tenga prioridad
                return Velocity.Zero
            }
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Maneja el fling horizontal
                return Velocity.Zero
            }
        }
    }

Box{
    Column {
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.navigate("Parques") // Navegar a la pantalla de "Parques"
        })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Registrar Parque",
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                fontSize = 25.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = parkName,
                onValueChange = { parkName = it },
                label = {
                    Text(
                        text = "Nombre del Parque",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeBoton,
                    focusedLabelColor = verdeBoton,
                    cursorColor = verdeBoton
                ),
                modifier = Modifier.fillMaxWidth()
            // Campo: Nombre del Parque
            OutlinedTextField(value = parkName, onValueChange = { parkName = it }, label = {
                Text(
                    text = "Nombre del Parque",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }, shape = roundedShape, colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo: Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = {
                    Text(
                        text = "Ubicación",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { navController.navigate("map?name=${URLEncoder.encode(parkName, "UTF-8")}&desc=${URLEncoder.encode(description, "UTF-8")}&status=${URLEncoder.encode(selectedOptionText, "UTF-8")}"+
                            "&needs=${URLEncoder.encode(selectedNeeds.joinToString(","), "UTF-8")}&comments=${URLEncoder.encode(comments, "UTF-8")}")}) {
                        Image(
                            painter = painterResource(id = R.drawable.map_add),
                            contentDescription = "Select location",
                            modifier = Modifier.size(24.dp) // Tamaño de la imagen
                        )
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeBoton,
                    focusedLabelColor = verdeBoton,
                    cursorColor = verdeBoton
                ),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // Esto hace que el campo sea de solo lectura
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Descripción
            OutlinedTextField(value = description, onValueChange = { description = it }, label = {
                Text(
                    text = "Descripción",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }, shape = roundedShape, colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Estado actual (Dropdown)
            var expanded by remember { mutableStateOf(false) }
            val options = listOf("Excelente", "Bueno", "Regular", "Deficiente", "Muy deficiente")

            ExposedDropdownMenuBox(expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedOptionText,
                    onValueChange = { },
                    label = {
                        Text(
                            text = "Estado actual",
                            color = Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                    },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = roundedShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdeBoton,
                        focusedLabelColor = verdeBoton,
                        cursorColor = verdeBoton
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(text = {
                            Text(
                                text = option,
                                color = if (selectedOptionText == option) Color.White else Color.Black
                            )
                        }, onClick = {
                            selectedOptionText = option
                            expanded = false
                        }, colors = MenuDefaults.itemColors(
                            textColor = Color.Black,
                            leadingIconColor = verdeBoton,
                            trailingIconColor = verdeBoton,
                            disabledTextColor = Color.Gray,
                            disabledLeadingIconColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray,
                        ), modifier = Modifier.background(
                            color = if (selectedOptionText == option) verdeBoton else Color.Transparent
                        )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Imágenes
            OutlinedTextField(
                value = selectedImageUris.joinToString(", ") { it.lastPathSegment ?: "" },
                onValueChange = { },
                label = {
                    Text(
                        text = "Imagenes",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { checkAndRequestPermission() }) {
                        Image(
                            painter = painterResource(id = R.drawable.image_add),
                            contentDescription = "Select location",
                            modifier = Modifier.size(24.dp) // Tamaño de la imagen
                        )
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeBoton,
                    focusedLabelColor = verdeBoton,
                    cursorColor = verdeBoton
                ),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            // Mostrar las imágenes seleccionadas con scroll mejorado y indicadores visuales
            if (selectedImageUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp, max = 110.dp) // Limita la altura del contenedor
                        .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()) // Scroll horizontal para las imágenes
                            .nestedScroll(nestedScrollConnection) // Aplica nestedScroll
                            .padding(horizontal = 4.dp)
                    ) {
                        selectedImageUris.forEachIndexed { index, uri ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = uri, builder = {
                                        crossfade(true)
                                        transformations(
                                            coil.transform.RoundedCornersTransformation(8f)
                                        )
                                    }),
                                    contentDescription = "Imagen ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Contador de imagen (1/5, 2/5, etc.)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(4.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}/${selectedImageUris.size}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                                    )
                                }

                                // Botón para eliminar la imagen
                                IconButton(
                                    onClick = {
                                        selectedImageUris =
                                            selectedImageUris.toMutableList().apply {
                                                removeAt(index)
                                            }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(Color.White, CircleShape)
                                        .size(24.dp)
                                        .padding(1.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Eliminar imagen",
                                        tint = verdeBoton,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Espaciado entre imágenes, excepto después de la última
                            if (index < selectedImageUris.size - 1) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }

                    // Indicadores de sombra para señalar scroll
                    if (selectedImageUris.size > 3) {
                        // Sombra izquierda (visible cuando no estás al inicio)
                        if (scrollState.value > 10) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(24.dp)
                                    .align(Alignment.CenterStart)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.1f), Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        // Sombra derecha (visible cuando no estás al final)
                        if (scrollState.value < scrollState.maxValue - 10) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(24.dp)
                                    .align(Alignment.CenterEnd)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent, Color.Black.copy(alpha = 0.1f)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Necesidades del Parque
            OutlinedTextField(value = selectedNeeds.joinToString(", "),
                onValueChange = { },
                label = {
                    Text(
                        text = "Necesidades del Parque",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeBoton,
                    focusedLabelColor = verdeBoton,
                    cursorColor = verdeBoton
                ),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showNeedsDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Seleccionar necesidades")
                    }
                })

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Comentarios adicionales
            OutlinedTextField(value = comments, onValueChange = { comments = it }, label = {
                Text(
                    text = "Comentarios adicionales",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }, shape = roundedShape, colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Validar antes de guardar los datos del parque
            Button(
                onClick = {
                    if (selectedImageUris.isEmpty()) {
                        imageError = "Debes seleccionar al menos una imagen."
                    } else if (selectedImageUris.size > 5) {
                        imageError = "Solo puedes subir un máximo de 5 imágenes."
                    } else {
                        // Indicar que estamos subiendo las imágenes
                        isUploading = true

                        // Proceso de subida de imágenes y guardado en Firestore
                        coroutineScope.launch {
                            try {
                                // Paso 1: Subir imágenes y obtener URLs
                                val imageUrls = imageUploadManager.uploadImages(selectedImageUris)

                                // Paso 2: Guardar los datos del parque en Firestore con las URLs
                                saveParkToFirestore(
                                    name = parkName,
                                    location = location,
                                    description = description,
                                    status = selectedOptionText,
                                    needs = selectedNeeds.toList(),
                                    comments = comments,
                                    imageUrls = imageUrls,  // Pasamos la lista de URLs
                                    latitude = parkLatitude,
                                    longitude = parkLongitude
                                )

                                // Paso 3: Resetear el formulario y mostrar éxito
                                withContext(Dispatchers.Main) {
                                    isUploading = false
                                    navController.navigate("registerParkSuccess")

                                    // Puedes añadir aquí la navegación a otra pantalla si es necesario
                                    // navController.navigate("ruta_destino")

                                }
                            } catch (e: Exception) {
                                // Manejo de errores
                                withContext(Dispatchers.Main) {
                                    isUploading = false
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message ?: "No se pudo completar la operación"}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("RegisterPark", "Error: ${e.message}", e)
                                }
                            }
                        }
                        imageError = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 118.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                shape = roundedShape,
                enabled = !isUploading // Deshabilitar el botón durante la subida
            ) {
                if (isUploading) {
                    // Mostrar indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Validar",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                }
            }



    // Diálogo: Solicitar permisos
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso necesario") },
            text = {
                Text(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        "Para seleccionar imágenes, necesitas otorgar permiso para acceder a las fotos."
                    else
                        "Para seleccionar una imagen, necesitas otorgar permiso para acceder a la galería."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton)
                ) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPermissionDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("No permitir")
                }
            // SnackbarHost para mostrar mensajes de error fugaces
            SnackbarHost(
                hostState = snackbarHostState, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Diálogo: Solicitar permisos
            if (showPermissionDialog) {
                AlertDialog(onDismissRequest = { showPermissionDialog = false },
                    title = { Text("Permiso necesario") },
                    text = {
                        Text(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) "Para seleccionar imágenes, necesitas otorgar permiso para acceder a las fotos."
                            else "Para seleccionar una imagen, necesitas otorgar permiso para acceder a la galería."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPermissionDialog = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }, colors = ButtonDefaults.buttonColors(containerColor = verdeBoton)
                        ) {
                            Text("Permitir")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showPermissionDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("No permitir")
                        }
                    })
            }

            // Diálogo: Seleccionar necesidades
            if (showNeedsDialog) {
                Dialog(onDismissRequest = { showNeedsDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Seleccione las\nnecesidades del parque",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            val needs = listOf(
                                "Mobiliario", "Iluminación", "Jardineria", "Seguridad", "Limpieza"
                            )

                            needs.chunked(2).forEach { rowNeeds ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    rowNeeds.forEach { need ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Checkbox(
                                                checked = need in selectedNeeds,
                                                onCheckedChange = { checked ->
                                                    selectedNeeds = if (checked) {
                                                        selectedNeeds + need
                                                    } else {
                                                        selectedNeeds - need
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = verdeBoton
                                                )
                                            )
                                            Text(
                                                text = need,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = { showNeedsDialog = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = verdeBoton
                                )
                            ) {
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }
        }
    }
}
    }
    // SnackbarHost para mostrar mensajes de error fugaces
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    }
}
}