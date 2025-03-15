package com.example.donations.ui.donacionEspecie

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.GetParkNameAndLocation
import com.example.donations.data.ParkData
import com.example.donations.ui.donacionEspecie.reu.CustomDropdown
import com.example.donations.ui.donacionEspecie.reu.CustomOutlinedTextField
import com.example.parks.ui.ParkImageDisplay

@Composable
fun FormScreen(navController: NavController) {

    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)
    // var showSuccessDialog by remember { mutableStateOf(false) } //dialogo de exito
    // var showFailedDialog by remember { mutableStateOf(false) } //dialogo de error

    var name by rememberSaveable { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var numeroValue by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    // Estado para la opción seleccionada
    var selectedOption by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var contactNumberError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var statusErrorP by remember { mutableStateOf<String?>(null) }
    var statusErrorT by remember { mutableStateOf<String?>(null) }
    var statusErrorR by remember { mutableStateOf<String?>(null) }
    var statusErrorC by remember { mutableStateOf<String?>(null) }
    var statusErrorN by remember { mutableStateOf<String?>(null) }
    var selectedResourceType by remember { mutableStateOf("") }
    var selectedResource by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf("") }
    var optionsPark by remember { mutableStateOf<List<String>>(emptyList()) }
    var parksList by remember { mutableStateOf<List<ParkData>>(emptyList()) }
    val optionsType = remember { listOf("Mobiliario", "Jardinería") }
    val optionsResourceM = remember { listOf("Bancas", "Mesas", "Juegos Infantiles", "Cestos de basura", "Cercas o Delimitaciones") }
    val optionsResourceJ = remember { listOf("Árboles", "Arbustos", "Plantas Ornamentales", "Césped o Pasto en rollo", "Tierra para rehabilitación de suelos" ) }
    val optionsCondition = remember { listOf("Nuevo", "Semi-nuevo", "Usado") }
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    val resourceOptions = remember(selectedResourceType) {
        when (selectedResourceType) {
            "Mobiliario" -> optionsResourceM
            "Jardinería" -> optionsResourceJ
            else -> emptyList() // Si no se ha seleccionado nada
        }
    }
    val validator = rememberDonationFormValidator()

    // Instancia del objeto para obtener los parques
    val getParkNameAndLocation = GetParkNameAndLocation()

    // Obtener los parques
    LaunchedEffect(Unit) {
        getParkNameAndLocation.getParkNameAndLocation(
            onSuccess = { parks ->
                parksList = parks
                optionsPark = parks.map { it.nombre }
            },
            onFailure = { exception ->
                println("Error al obtener parques")
            }
        )
    }

    // Lanzador para seleccionar imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            if (selectedImageUris.size + uris.size > 3) {
                imageError = "Solo puedes subir un máximo de 3 imágenes."
            } else {
                selectedImageUris = selectedImageUris + uris
                imageError = null
            }
        } else {
            imageError = "Debes seleccionar al menos una imagen."
        }
    }

    // Función para abrir el selector de imágenes
    fun openImagePicker() {
        if (selectedImageUris.size >= 3) {
            imageError = "Solo puedes subir un máximo de 3 imágenes."
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
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            permissionLauncher.launch(permission)
        }
    }
    val scrollState = rememberScrollState()

    fun validate(){
        val formState = DonationFormState(
            donorName = name,
            contactNumber = contactNumber,
            location = location,
            parkToDonate = selectedOption,
            resourceType = selectedResourceType,
            resource = selectedResource,
            quantity = numeroValue,
            condition = selectedCondition,
            selectedImageUris = selectedImageUris
        )
        val validationResult = validator(formState)

        nameError = validationResult.donorNameError
        contactNumberError = validationResult.contactNumberError
        locationError = validationResult.locationError
        statusErrorP = validationResult.parkToDonateError
        statusErrorT = validationResult.resourceTypeError
        statusErrorR = validationResult.resourceError
        statusErrorC = validationResult.conditionError
        statusErrorN = validationResult.quantityError
        imageError = validationResult.imageError

        /*//descomentar para probar dialogos
        if (nameError == null && contactNumberError == null && locationError == null &&
            statusErrorP == null && statusErrorT == null && statusErrorR == null &&
            statusErrorC == null && statusErrorN == null && imageError == null) {
            showSuccessDialog = true //descomentar para probar dialogo de exito
            // showFailedDialog = true //descomentar para probar dialogo de error
        }*/

    }

    Box {
        Column {
            //AppBar
            SecondaryAppBar(showIcon = true, onIconClick = {
                navController.popBackStack("Donaciones", inclusive = false)
                navController.navigate("Donaciones")
            })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Donacion en Especie",
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    fontSize = 25.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CustomOutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = when {
                            it.isEmpty() -> "El nombre no puede estar vacío"
                            it.length > 100 -> "El nombre no puede exceder 100 caracteres"
                            else -> null
                        }
                    },
                    label = "Nombre del Donante/Razón Social",
                    isError = nameError != null,
                    errorMessage = nameError
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = contactNumber,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                            contactNumber = newValue
                        }
                        // Validación del número de contacto
                        contactNumberError = when {
                            newValue.isEmpty() -> "El número de contacto no puede estar vacío"
                            newValue.length < 10 -> "El número de contacto debe tener al menos 10 dígitos"
                            else -> null
                        }
                    },
                    label = "Número de contacto",
                    isError = contactNumberError != null,
                    errorMessage = contactNumberError,
                    modifier = Modifier.fillMaxWidth(),
                    isNumberField = true // Indicar que es un campo numérico
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomDropdown(
                    fieldName = "Parque a Donar",
                    options = optionsPark,
                    selectedOption = selectedOption,
                    onOptionSelected = { option ->
                        selectedOption = option
                        statusErrorP = if (option.isEmpty()) "Debes seleccioanr un parque" else null

                        // Buscar la ubicación correspondiente al parque seleccionado
                        val selectedPark = parksList.find { it.nombre == option }
                        location = selectedPark?.ubicacion ?: "Desconocido"
                    },
                    isError = statusErrorP != null,
                    errorMessage = statusErrorP,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = location,
                    onValueChange = { },
                    label = "Ubicación",
                    isError = locationError != null,
                    errorMessage = locationError,
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomDropdown(
                    fieldName = "Tipo de Recurso",
                    options = optionsType,
                    selectedOption = selectedResourceType,
                    onOptionSelected = { option ->
                        selectedResourceType = option
                        selectedResource = ""
                        selectedCondition = ""
                        statusErrorT =
                            if (option.isEmpty()) "Debes seleccioanr un tipo de recurso" else null
                    },
                    isError = statusErrorT != null,
                    errorMessage = statusErrorT,
                    modifier = Modifier.fillMaxWidth()
                )

                if (resourceOptions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDropdown(
                        fieldName = "Recurso",
                        options = resourceOptions,
                        selectedOption = selectedResource,
                        onOptionSelected = { option ->
                            selectedResource = option
                            statusErrorR =
                                if (option.isEmpty()) "Debes seleccionar un recurso" else null
                        },
                        isError = statusErrorR != null,
                        errorMessage = statusErrorR,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CustomOutlinedTextField(
                        value = numeroValue,
                        onValueChange = { newValue ->
                            numeroValue = newValue // Actualiza el estado en el padre
                            val cantidad = newValue.toIntOrNull() ?: 1
                            statusErrorN = when {
                                cantidad < 1 -> "La cantidad mínima es 1"
                                cantidad > 10 -> "La cantidad máxima es 10"
                                else -> null
                            }
                        },
                        label = "Cantidad",
                        isError = statusErrorN != null,
                        errorMessage = statusErrorN,
                        readOnly = true,
                        isNumberField = true,
                        showNumberControls = true
                    )
                }

                if (selectedResourceType == "Mobiliario") {
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDropdown(
                        fieldName = "Condiciones de Mobiliario",
                        options = optionsCondition,
                        selectedOption = selectedCondition,
                        onOptionSelected = { option ->
                            selectedCondition = option
                            statusErrorC =
                                if (option.isEmpty()) "Debes seleccionar una condición" else null
                        },
                        isError = statusErrorC != null,
                        errorMessage = statusErrorC,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = selectedImageUris.joinToString(", ") { it.lastPathSegment ?: "" },
                    onValueChange = { },
                    label = "Imágenes",
                    isError = imageError != null,
                    trailingIcon = {
                        IconButton(onClick = { checkAndRequestPermission() }) {
                            Image(
                                painter = painterResource(id = R.drawable.image_add),
                                contentDescription = "Select location",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    errorMessage = imageError,
                    readOnly = true
                )
                fun removeImage(index: Int) {
                    selectedImageUris = selectedImageUris.toMutableList().apply {
                        removeAt(index)
                    }
                }

                // Mostrar las imágenes seleccionadas con scroll mejorado y indicadores visuales
                ParkImageDisplay(
                    imageUris = selectedImageUris,
                    onImageRemove = { index -> removeImage(index) })

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { validate() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 118.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                    shape = roundedShape
                ) {
                    Text(
                        text = "Validar",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                }
            }
        }

        /*//Borrar si ya no se ocupa
        SuccessDialog(
            visible = showSuccessDialog,
            onDismiss = {
                showSuccessDialog = false
                // Navegar de vuelta a la pantalla de inicio
                navController.popBackStack("Donaciones", inclusive = false)
                navController.navigate("Donaciones")
            }
        )*/

        /* //Borrar si ya no se ocupa
        FailedDialog(
            visible = showFailedDialog,
            onDismiss = {
                showFailedDialog = false
                // Navegar de vuelta a la pantalla de inicio
                navController.popBackStack("Donaciones", inclusive = false)
                navController.navigate("Donaciones")
            }
        )*/
    }
}