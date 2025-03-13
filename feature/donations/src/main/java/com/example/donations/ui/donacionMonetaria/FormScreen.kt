package com.example.donations.ui.donacionMonetaria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.design.SecondaryAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

@Composable
fun MonetariaFormScreen(navController: NavController) {
    var cantidad by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }
    var parqueSeleccionado by remember { mutableStateOf("") }
    val metodosPago = listOf("Tarjeta", "PayPal", "Transferencia")
    val parques = listOf("Parque 1", "Parque 2", "Parque 3")
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        // AppBar
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.popBackStack("Donaciones", inclusive = false)
            navController.navigate("Donaciones")
        })

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para cantidad
        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad a donar") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        // Dropdown de Metodo de pago
        var expandedMetodo by remember { mutableStateOf(false) }
        Box(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = metodoPago,
                onValueChange = {}, // No permite escritura manual
                label = { Text("Método de pago") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedMetodo, onDismissRequest = { expandedMetodo = false }) {
                metodosPago.forEach { metodo ->
                    DropdownMenuItem(text = { Text(metodo) }, onClick = {
                        metodoPago = metodo
                        expandedMetodo = false
                    })
                }
            }
        }

        // Dropdown de Parque a donar
        var expandedParque by remember { mutableStateOf(false) }
        Box(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = parqueSeleccionado,
                onValueChange = {}, // No permite escritura manual
                label = { Text("Parque a donar") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedParque, onDismissRequest = { expandedParque = false }) {
                parques.forEach { parque ->
                    DropdownMenuItem(text = { Text(parque) }, onClick = {
                        parqueSeleccionado = parque
                        expandedParque = false
                    })
                }
            }
        }

        // Botón Validar
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Validar")
        }
    }
}

fun NavGraphBuilder.addDonacionMonetariaScreen(navController: NavController) {
    composable("donacionMonetaria") { MonetariaFormScreen(navController) }
}
