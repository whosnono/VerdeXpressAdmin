package com.example.donations.ui.donacionMonetaria

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.design.SecondaryAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

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
    var quiereRecibo by remember { mutableStateOf<Boolean?>(null) } // null=no seleccionado, true=sí, false=no
    val metodosPago = listOf("Tarjeta", "PayPal", "Transferencia")
    // Lista vacía para los parques y ubicaciones, que se llenará posteriormente con datos de la BD
    val parques = remember { mutableStateListOf<String>() }
    val ubicaciones = remember { mutableStateListOf<String>() }
    val scrollState = rememberScrollState()
    var expandedMetodo by remember { mutableStateOf(false) }
    var expandedParque by remember { mutableStateOf(false) }
    var expandedUbicacion by remember { mutableStateOf(false) }
    var rfc by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var domFiscal by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        // AppBar
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.popBackStack("Donaciones", inclusive = false)
            navController.navigate("Donaciones")
        })

        Spacer(modifier = Modifier.height(16.dp))

        val verdeBoton = Color(0xFF78B153)
        val grisBoton = Color(0x4D000000)
        val botonColor = Color(0x4D000000)

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = verdeBoton,
            focusedLabelColor = verdeBoton,
            cursorColor = verdeBoton,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            errorCursorColor = Color.Red
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Donación monetaria",
                fontSize = 25.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        }

        // Campo de texto para nombre del donante (auto-expandible)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Donante") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(size = 10.dp),
                minLines = 1,
                singleLine = false // Permitir múltiples líneas
            )
        }

        // Campo de texto para correo electronico (auto-expandible)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(size = 10.dp),
                minLines = 1,
                singleLine = false // Permitir múltiples líneas
            )
        }

        // Campo de texto para numero de telefono (limitado a 10 dígitos)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = numTel,
                onValueChange = {
                    // Limitar a 10 dígitos y solo permitir números
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        numTel = it
                    }
                },
                label = { Text("Teléfono de contacto") },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                shape = RoundedCornerShape(size = 10.dp),
                singleLine = true
            )
        }

        // Dropdown de Parque a donar (inicialmente vacío)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedParque,
                onExpandedChange = { expandedParque = !expandedParque }
            ) {
                OutlinedTextField(
                    value = parqueSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Parque a donar") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand dropdown",
                            tint = if (expandedParque) verdeBoton else Color.Gray
                        )
                    },
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Añadido menuAnchor para vincular el campo con el menú
                    shape = RoundedCornerShape(size = 10.dp),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expandedParque,
                    onDismissRequest = { expandedParque = false }
                ) {
                    // Solo muestra las opciones si hay parques en la lista
                    if (parques.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay parques disponibles") },
                            onClick = { expandedParque = false },
                            enabled = false
                        )
                    } else {
                        parques.forEach { parque ->
                            DropdownMenuItem(
                                text = { Text(parque) },
                                onClick = {
                                    parqueSeleccionado = parque
                                    expandedParque = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dropdown de ubicacion del parque a donar (inicialmente vacío)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedUbicacion,
                onExpandedChange = { expandedUbicacion = !expandedUbicacion }
            ) {
                OutlinedTextField(
                    value = ubicacionSeleccionado, // Corregido: ahora usa ubicacionSeleccionado
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ubicación del parque") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand dropdown",
                            tint = if (expandedUbicacion) verdeBoton else Color.Gray
                        )
                    },
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Añadido menuAnchor para vincular el campo con el menú
                    shape = RoundedCornerShape(size = 10.dp),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expandedUbicacion,
                    onDismissRequest = { expandedUbicacion = false }
                ) {
                    // Solo muestra las opciones si hay ubicaciones en la lista
                    if (ubicaciones.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay ubicaciones disponibles") },
                            onClick = { expandedUbicacion = false },
                            enabled = false
                        )
                    } else {
                        ubicaciones.forEach { ubicacion ->
                            DropdownMenuItem(
                                text = { Text(ubicacion) },
                                onClick = {
                                    ubicacionSeleccionado = ubicacion
                                    expandedUbicacion = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Campo de texto para cantidad con botones + y -
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            // Variable para manejar la cantidad como número
            var cantidadNum by remember { mutableStateOf(0) }

            // Actualizar el estado de texto cuando cambia el número
            LaunchedEffect(cantidadNum) {
                cantidad = cantidadNum.toString()
            }

            // Actualizar el número cuando cambia el texto manualmente
            LaunchedEffect(cantidad) {
                if (cantidad.isNotEmpty()) {
                    try {
                        cantidadNum = cantidad.toInt()
                    } catch (e: NumberFormatException) {
                        // Manejar el caso de texto no válido
                    }
                }
            }

            // Row contenedor para agrupar el campo y los botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TextField para la cantidad
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = {
                        // Validar que solo contenga números
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            cantidad = it
                        }
                    },
                    label = { Text("Cantidad monetaria a donar") },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(size = 10.dp),
                    singleLine = true
                )

                // Botón para disminuir
                IconButton(
                    onClick = {
                        if (cantidadNum > 0) cantidadNum--
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = botonColor,
                            shape = RoundedCornerShape(size = 10.dp)
                        )
                ) {
                    Text(
                        text = "−",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botón para aumentar
                IconButton(
                    onClick = { cantidadNum++ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = botonColor,
                            shape = RoundedCornerShape(size = 10.dp)
                        )
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Dropdown de Metodo de pago
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedMetodo,
                onExpandedChange = { expandedMetodo = !expandedMetodo }
            ) {
                OutlinedTextField(
                    value = metodoPago,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Método de pago") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand dropdown",
                            tint = if (expandedMetodo) verdeBoton else Color.Gray
                        )
                    },
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Añadido menuAnchor para vincular el campo con el menú
                    shape = RoundedCornerShape(size = 10.dp),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expandedMetodo,
                    onDismissRequest = { expandedMetodo = false }
                ) {
                    metodosPago.forEach { metodo ->
                        DropdownMenuItem(
                            text = { Text(metodo) },
                            onClick = {
                                metodoPago = metodo
                                expandedMetodo = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.Black,
                                leadingIconColor = verdeBoton,
                                trailingIconColor = verdeBoton,
                                disabledTextColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            ),
                            modifier = Modifier.background(
                                color = if (metodoPago == metodo) verdeBoton else Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        // Sección de "¿Desea recibo de donación?"
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Desea recibo de donación?",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(700),
                color = Color(0xFF171D1B),
                letterSpacing = 0.25.sp
            )

            // Botones de opción
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
                            selectedColor = grisBoton,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text(text = "No", color = Color.Black)
                }
            }

            // Campo RFC (Aparece solo si selecciona "Sí")
            AnimatedVisibility(visible = quiereRecibo == true) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rfc,
                        onValueChange = {
                            if (it.length <= 13) {
                                rfc = it
                            }
                        },
                        label = { Text("RFC") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(size = 10.dp),
                        minLines = 1,
                        singleLine = false // Permitir múltiples líneas
                    )
                }
            }

            // Campo razón social (Aparece solo si selecciona "Sí")
            AnimatedVisibility(visible = quiereRecibo == true) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = razon,
                        onValueChange = { razon = it },
                        label = { Text("Razón social (para empresas)") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(size = 10.dp),
                        minLines = 1,
                        singleLine = false // Permitir múltiples líneas
                    )
                }
            }

            // Campo domicilio fiscal (Aparece solo si selecciona "Sí")
            AnimatedVisibility(visible = quiereRecibo == true) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = domFiscal,
                        onValueChange = { domFiscal = it },
                        label = { Text("Domicilio Fiscal") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(size = 10.dp),
                        minLines = 1,
                        singleLine = false // Permitir múltiples líneas
                    )
                }
            }
        }

        // Botón Validar
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeBoton)
        ) {
            Text("Validar")
        }
    }
}
