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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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
fun RegisterParkScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    latitude: String? = null,
    longitude: String? = null,
    address: String? = null,
    parkNameArg: String? = null,
    descriptionArg: String? = null,
    statusArg: String? = null,
    imageUrisArg: List<Uri>? = null,
    needsArg: List<String>? = null,
    commentsArg: String? = null
) {
    val verdeBoton = Color(0xFF78B153)
    var showNeedsDialog by remember { mutableStateOf(false) }
    var selectedNeeds by rememberSaveable { mutableStateOf(needsArg?.filter { it.isNotEmpty() } ?: emptyList()) }
    val roundedShape = RoundedCornerShape(12.dp)
    // Contenedor de imágenes con scroll
    val scrollState = rememberScrollState()
    // Estados para los campos de texto
    var parkName by rememberSaveable { mutableStateOf(parkNameArg ?: "") }
    var location by remember { mutableStateOf(address ?: "") }
    var description by rememberSaveable { mutableStateOf(descriptionArg ?: "") }
    var comments by rememberSaveable { mutableStateOf(commentsArg ?: "") }
    var selectedOptionText by rememberSaveable { mutableStateOf(statusArg ?: "") }
    //Estados para mensajes de error
    var parkNameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var commentsError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var statusError by remember { mutableStateOf<String?>(null) }
    var needsError by remember { mutableStateOf<String?>(null) }
    // Estado para manejar el mensaje de error
    var imageError by remember { mutableStateOf<String?>(null) }
    // Estado para almacenar las URIs de las imágenes seleccionadas
    var selectedImageUris by rememberSaveable {
        mutableStateOf(imageUrisArg ?: emptyList())
    }
    // Añadir este bloque para recuperar las imágenes del ViewModel cuando regreses del mapa
    val viewModelUris by sharedViewModel.selectedImageUris.collectAsState()
    LaunchedEffect(viewModelUris) {
        if (viewModelUris.isNotEmpty() && selectedImageUris.isEmpty()) {
            selectedImageUris = viewModelUris
        }
    }
    // Add this DisposableEffect to clear images when navigating away
    DisposableEffect(key1 = Unit) {
        onDispose {
            // This will run when the composable is removed from composition
            // (i.e., when the user navigates away without submitting)
            if (!navController.currentBackStackEntry?.destination?.route?.contains("registerParkSuccess")!! &&
                !navController.currentBackStackEntry?.destination?.route?.contains("map")!!) {
                // Only clear if not navigating to success screen
                sharedViewModel.setImageUris(emptyList())
            }
        }
    }
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

    // Validador de formulario
    val validator = rememberParkFormValidator()

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

    // Actualizar el ViewModel con las URIs de las imágenes seleccionadas
    LaunchedEffect(selectedImageUris) {
        sharedViewModel.setImageUris(selectedImageUris)
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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = verdeBoton,
        focusedLabelColor = verdeBoton,
        cursorColor = verdeBoton,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red,
        errorCursorColor = Color.Red
    )

    @Composable
    fun ErrorText(errorMessage: String) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }

    // Función de validación y guardado
    fun validateAndSavePark() {
        val formState = ParkFormState(
            parkName = parkName,
            location = location,
            description = description,
            status = selectedOptionText,
            comments = comments,
            selectedNeeds = selectedNeeds,
            selectedImageUris = selectedImageUris
        )

        val validationResult = validator(formState)

        // Actualizar estados de error
        parkNameError = validationResult.parkNameError
        locationError = validationResult.locationError
        descriptionError = validationResult.descriptionError
        statusError = validationResult.statusError
        needsError = validationResult.needsError
        commentsError = validationResult.commentsError
        imageError = validationResult.imageError

        if (validationResult.isValid()) {
            isUploading = true

            coroutineScope.launch {
                try {
                    val imageUrls = imageUploadManager.uploadImages(selectedImageUris)

                    saveParkToFirestore(
                        name = parkName,
                        location = location,
                        description = description,
                        status = selectedOptionText,
                        needs = selectedNeeds.toList(),
                        comments = comments,
                        imageUrls = imageUrls,
                        latitude = parkLatitude,
                        longitude = parkLongitude
                    )

                    withContext(Dispatchers.Main) {
                        isUploading = false

                        // Clear the image URIs in the ViewModel
                        sharedViewModel.setImageUris(emptyList())

                        navController.navigate("registerParkSuccess")
                    }
                } catch (e: Exception) {
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
    }

    @Composable
    fun CustomOutlinedTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        isError: Boolean,
        errorMessage: String?,
        modifier: Modifier = Modifier,
        trailingIcon: @Composable (() -> Unit)? = null,
        readOnly: Boolean = false
    ) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = label, color = Color.Gray, fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))) },
                isError = isError,
                trailingIcon = trailingIcon,
                shape = roundedShape,
                colors = textFieldColors,
                modifier = modifier.fillMaxWidth(),
                readOnly = readOnly
            )
            errorMessage?.let { ErrorText(it) }
        }
    }
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

                CustomOutlinedTextField(
                    value = parkName,
                    onValueChange = { parkName = it
                        parkNameError = when {
                            it.isEmpty() -> "El nombre no puede estar vacío"
                            it.length > 100 -> "El nombre no puede exceder 100 caracteres"
                            else -> null
                        }},
                    label = "Nombre del Parque",
                    isError = parkNameError != null,
                    errorMessage = parkNameError
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = location,
                    onValueChange = { location = it
                        locationError = when {
                            it.isEmpty() -> "La ubicación no puede estar vacía"
                            else -> null
                        }},
                    label = "Ubicación",
                    isError = locationError != null,
                    trailingIcon = { IconButton(onClick = {
                        // Guardar imágenes en el ViewModel antes de navegar
                        sharedViewModel.setImageUris(selectedImageUris)


                        val needsParam = if (selectedNeeds.isEmpty()) {
                            ""
                        } else {
                            URLEncoder.encode(selectedNeeds.joinToString(","), "UTF-8")
                        }

                        navController.navigate(
                            "map?name=${
                                URLEncoder.encode(
                                    parkName,
                                    "UTF-8"
                                )
                            }&desc=${
                                URLEncoder.encode(
                                    description,
                                    "UTF-8"
                                )
                            }&status=${URLEncoder.encode(selectedOptionText, "UTF-8")}" +
                                    "&needs=${needsParam}&comments=${URLEncoder.encode(comments, "UTF-8")}"
                        )
                    }) {
                            Image(
                                painter = painterResource(id = R.drawable.map_add),
                                contentDescription = "Select location",
                                modifier = Modifier.size(24.dp) // Tamaño de la imagen
                            )
                        }
                    },
                    errorMessage = locationError
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomOutlinedTextField(
                    value = description,
                    onValueChange = { description = it
                        descriptionError = when {
                            it.isEmpty() -> "La descripción no puede estar vacía"
                            it.length > 500 -> "La descripción no puede exceder 500 caracteres"
                            else -> null
                        } },
                    label = "Descripción",
                    isError = descriptionError != null,
                    errorMessage = descriptionError
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo: Estado actual (Dropdown)
                var expanded by remember { mutableStateOf(false) }
                val options =
                    listOf("Excelente", "Bueno", "Regular", "Deficiente", "Muy deficiente")

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
                        isError = statusError != null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = roundedShape,
                        colors = textFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(text = {
                                Text(
                                    text = option,
                                    color = if (selectedOptionText == option) Color.White else Color.Black
                                )
                            }, onClick = {
                                selectedOptionText = option
                                expanded = false
                                statusError = null
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
                statusError?.let { ErrorText(it) }

                Spacer(modifier = Modifier.height(8.dp))

                CustomOutlinedTextField(
                    value = selectedImageUris.joinToString(", ") { it.lastPathSegment ?: "" },
                    onValueChange = { },
                    label = "Imagenes",
                    isError = imageError != null,
                    trailingIcon = {
                        IconButton(onClick = { checkAndRequestPermission() }) {
                            Image(
                                painter = painterResource(id = R.drawable.image_add),
                                contentDescription = "Select location",
                                modifier = Modifier.size(24.dp) // Tamaño de la imagen
                            )
                        }
                    },
                    errorMessage = imageError
                )
                fun removeImage(index: Int) {
                    selectedImageUris = selectedImageUris.toMutableList().apply {
                        removeAt(index)
                    }
                }
                // Mostrar las imágenes seleccionadas con scroll mejorado y indicadores visuales
                ParkImageDisplay(imageUris = selectedImageUris, onImageRemove = { index -> removeImage(index) })

                Spacer(modifier = Modifier.height(8.dp))

                CustomOutlinedTextField(
                    value = selectedNeeds.joinToString(", ") { it },
                    onValueChange = { },
                    label = "Necesidades del Parque",
                    isError = needsError != null,
                    trailingIcon = {
                        IconButton(onClick = {
                            showNeedsDialog = true
                            if (selectedNeeds.isEmpty()) {
                                needsError = "Selecciona al menos una necesidad"
                            } else {
                                needsError = null
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Seleccionar necesidades")
                        }
                    },
                    errorMessage = needsError
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomOutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it
                        commentsError = when {
                            it.length > 300 -> "Los comentarios no pueden exceder 300 caracteres"
                            else -> null
                        }},
                    label = "Comentarios adicionales",
                    isError = commentsError != null,
                    errorMessage = commentsError
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Validar antes de guardar los datos del parque
                Button(
                    onClick = { validateAndSavePark() },
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
                        }
                    )
                }

                // Diálogo: Seleccionar necesidades
                if (showNeedsDialog) {
                    Dialog(onDismissRequest = { showNeedsDialog = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                val needs = listOf("Mobiliario", "Iluminación", "Jardineria", "Seguridad", "Limpieza")
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
                                                    colors = CheckboxDefaults.colors(checkedColor = verdeBoton)
                                                )
                                                Text(text = need, modifier = Modifier.padding(start = 8.dp))
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        showNeedsDialog = false
                                        needsError = if (selectedNeeds.isEmpty()) {
                                            "Selecciona al menos una necesidad"
                                        } else {
                                            null
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton)
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