package com.example.donations.ui.donacionMonetaria

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.GetParkNameAndLocation
import com.example.donations.data.ParkData
import com.example.donations.ui.donacionEspecie.reu.CustomDropdown
import com.example.donations.ui.donacionEspecie.reu.CustomOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetariaFormScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var numTel by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }
    var parqueSeleccionado by remember { mutableStateOf("") }
    var ubicacionSeleccionado by remember { mutableStateOf("") }
    var quiereRecibo by remember { mutableStateOf<Boolean?>(null) }
    val metodosPago = listOf("Tarjeta de crédito/débito", "PayPal")
    val parques = remember { mutableStateListOf<String>() } // Lista vacía inicialmente
    val ubicaciones = remember { mutableStateListOf<String>() } // Lista vacía inicialmente
    val scrollState = rememberScrollState()
    var rfc by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var domFiscal by remember { mutableStateOf("") }

    var validationResult by remember { mutableStateOf<MonetariaValidationResult?>(null) }

    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    // Añade una función para obtener los datos de los parques
    var parksList by remember { mutableStateOf<List<ParkData>>(emptyList()) }
    val getParkNameAndLocation = GetParkNameAndLocation()

    // Usa LaunchedEffect para cargar los datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        getParkNameAndLocation.getParkNameAndLocation(
            onSuccess = { parks ->
                // Actualiza la lista de parques y ubicaciones
                parksList = parks
                parques.clear()
                ubicaciones.clear()
                parks.forEach { park ->
                    parques.add(park.nombre)
                    ubicaciones.add(park.ubicacion)
                }
            },
            onFailure = { exception ->
                // Maneja el error (puedes mostrar un Toast o un mensaje de error)
                Log.e("MonetariaFormScreen", "Error al obtener parques: ${exception.message}")
            }
        )
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = verdeBoton,
        focusedLabelColor = verdeBoton,
        cursorColor = verdeBoton,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red,
        errorCursorColor = Color.Red
    )

    val validator = rememberMonetariaFormValidator()

    fun validateForm() {
        val formState = MonetariaFormState(
            nombre = nombre,
            correo = correo,
            numTel = numTel,
            cantidad = cantidad,
            metodoPago = metodoPago,
            parqueSeleccionado = parqueSeleccionado,
            ubicacionSeleccionado = ubicacionSeleccionado,
            quiereRecibo = quiereRecibo,
            rfc = rfc,
            razon = razon,
            domFiscal = domFiscal
        )
        validationResult = validator(formState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // AppBar
        SecondaryAppBar(
            showIcon = true,
            onIconClick = {
                navController.popBackStack("Donaciones", inclusive = false)
                navController.navigate("Donaciones")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Donación monetaria",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo de texto para nombre del donante
            CustomOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre del Donante",
                isError = validationResult?.nombreError != null,
                errorMessage = validationResult?.nombreError
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de texto para correo electrónico
            CustomOutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo electrónico",
                isError = validationResult?.correoError != null,
                errorMessage = validationResult?.correoError
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de texto para número de teléfono
            CustomOutlinedTextField(
                value = numTel,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        numTel = it
                    }
                },
                label = "Teléfono de contacto",
                isError = validationResult?.numTelError != null,
                errorMessage = validationResult?.numTelError,
                isNumberField = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dropdown de Parque a donar
            CustomDropdown(
                fieldName = "Parque a donar",
                options = parques,
                selectedOption = parqueSeleccionado,
                onOptionSelected = { option ->
                    parqueSeleccionado = option
                    // Actualiza la ubicación automáticamente
                    val parqueSeleccionado = parksList.find { it.nombre == option }
                    ubicacionSeleccionado = parqueSeleccionado?.ubicacion ?: "Desconocido"
                },
                isError = validationResult?.parqueSeleccionadoError != null,
                errorMessage = validationResult?.parqueSeleccionadoError
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de texto para la ubicación (solo lectura)
            CustomOutlinedTextField(
                value = ubicacionSeleccionado,
                onValueChange = { },
                label = "Ubicación",
                readOnly = true,
                isError = false,
                errorMessage = null
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dropdown de Método de pago
            CustomDropdown(
                fieldName = "Método de pago",
                options = metodosPago,
                selectedOption = metodoPago,
                onOptionSelected = { option ->
                    metodoPago = option
                },
                isError = validationResult?.metodoPagoError != null,
                errorMessage = validationResult?.metodoPagoError
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de texto para cantidad con botones + y -
            CustomOutlinedTextField(
                value = cantidad,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        cantidad = it
                    }
                },
                label = "Cantidad monetaria a donar",
                isError = validationResult?.cantidadError != null,
                errorMessage = validationResult?.cantidadError,
                isNumberField = true,
                showNumberControls = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de "¿Desea recibo de donación?"
            Text(
                text = "¿Desea recibo de donación?",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = quiereRecibo == true,
                        onClick = { quiereRecibo = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = verdeBoton,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text(text = "Sí", color = Color.Black)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = quiereRecibo == false,
                        onClick = { quiereRecibo = false },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = verdeBoton,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text(text = "No", color = Color.Black)
                }
            }

            // Campos adicionales para el recibo
            AnimatedVisibility(visible = quiereRecibo == true) {
                Column {
                    CustomOutlinedTextField(
                        value = rfc,
                        onValueChange = {
                            if (it.length <= 13) {
                                rfc = it
                            }
                        },
                        label = "RFC",
                        isError = validationResult?.rfcError != null,
                        errorMessage = validationResult?.rfcError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CustomOutlinedTextField(
                        value = razon,
                        onValueChange = { razon = it },
                        label = "Razón social (para empresas)",
                        isError = validationResult?.razonError != null,
                        errorMessage = validationResult?.razonError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CustomOutlinedTextField(
                        value = domFiscal,
                        onValueChange = { domFiscal = it },
                        label = "Domicilio Fiscal",
                        isError = validationResult?.domFiscalError != null,
                        errorMessage = validationResult?.domFiscalError
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón Validar
            Button(
                onClick = { validateForm() },
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
}