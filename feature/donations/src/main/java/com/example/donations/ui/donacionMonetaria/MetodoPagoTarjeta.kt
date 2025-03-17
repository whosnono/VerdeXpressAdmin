package com.example.donations.ui.donacionMonetaria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.donacionMonetaria.DonacionMonetariaViewModel

@Composable
fun MetodoPagoTarjetaScreen(navController: NavController, viewModel: DonacionMonetariaViewModel) {
    // Datos del formulario vinculados al ViewModel
    val nombre = viewModel.nombre
    val correo = viewModel.correo
    val numTel = viewModel.numTel
    val cantidad = viewModel.cantidad
    val metodoPago = viewModel.metodoPago
    val parqueSeleccionado = viewModel.parqueSeleccionado
    val ubicacionSeleccionado = viewModel.ubicacionSeleccionado
    val quiereRecibo = viewModel.quiereRecibo
    val rfc = viewModel.rfc
    val razon = viewModel.razon
    val domFiscal = viewModel.domFiscal

    // Estructura de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.popBackStack()
        })

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            // Título del formulario
            Text(
                text = "Detalles de pago con tarjeta",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(text = "Nombre: $nombre")
            Text(text = "Correo: $correo")
            Text(text = "Cantidad: $cantidad")
        }
    }
}