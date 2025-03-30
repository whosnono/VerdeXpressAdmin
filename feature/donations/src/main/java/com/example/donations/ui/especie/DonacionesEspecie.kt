package com.example.donations.ui.especie

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.R.font
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.clickable

@Composable
fun DonacionesEspecie(navController: NavController) {
    var donaciones by remember { mutableStateOf<List<DonacionItem>>(emptyList()) }


    LaunchedEffect(Unit) {
        getDonacionesEspecieFromFirebase { items ->
            donaciones = items
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MainAppBar()

        // Barra inferior "Donaciones en Especie"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Donaciones en Especie",
                style = TextStyle(
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78B153)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Row(
            Modifier
                .width(412.dp)
                .height(12.dp)
                .background(color = Color(0xFFF5F6F7))
        ) {}

        Row(
            Modifier
                .width(412.dp)
                .height(29.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Reciente",
                style = TextStyle(
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F4946)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Más antiguas",
                style = TextStyle(
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F4946)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(5.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            donaciones.forEach { donacion ->
                DonacionesCuadro(

                    title = donacion.parqueDonado,
                    date = donacion.fecha,
                    address = donacion.ubicacion,
                    person = donacion.donanteNombre,
                    telefono = donacion.donanteContacto,
                    status = donacion.registroEstado,
                    amount = donacion.cantidad,
                    resource = donacion.recurso,
                    condition = donacion.condicion,
                    onClick = {
                        navController.navigate(
                            "DonationsDetails/${donacion.parqueDonado}/${donacion.fecha.replace("/", "-")}/${donacion.ubicacion}/${donacion.donanteNombre}/${donacion.donanteContacto}/${donacion.cantidad}/${donacion.recurso}/${donacion.condicion}"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DonacionesCuadro(
    title: String,
    date: String,
    address: String,
    person: String,
    telefono: String,
    status: String,
    amount: String,
    resource: String,
    condition: String,
    onClick: () -> Unit
) {
    val isPendiente = status.equals("Pendiente", ignoreCase = true)
    val statusColor = if (isPendiente) Color(0xFF78B153) else Color.DarkGray
    val borderColor = if (isPendiente) Color(0xFF78B153) else Color.DarkGray
    val statusText = if (isPendiente) "En revisión" else status

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Parque \"$title\"",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = address, fontSize = 14.sp)
                Text(text = person, fontSize = 14.sp)
                Text(text = telefono, fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = date,
                    fontSize = 13.sp,
                    color = Color(0xFF4D4447),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = statusText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = statusColor
                )
            }
        }
    }
}


data class DonacionItem(
    val parqueDonado: String,
    val fecha: String,
    val ubicacion: String,
    val donanteNombre: String,
    val donanteContacto: String,
    val registroEstado: String,
    val cantidad: String,
    val recurso: String,
    val condicion: String
)

fun getDonacionesEspecieFromFirebase(onSuccess: (List<DonacionItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_especie").get()
        .addOnSuccessListener { result ->
            val donacionesList = result.map { document ->

                var fechaEstimada = document.getString("fecha_estimada_donacion")

// Verificar si el campo "estimatedDonationDate" existe y usarlo si es necesario
                if (fechaEstimada == null) {
                    fechaEstimada = document.getString("estimatedDonationDate")
                }


                val fechaConvertida = fechaEstimada?.let { fechaStr ->
                    try {
                        val partes = fechaStr.split("/")
                        if (partes.size == 3) {
                            "${partes[2]}-${partes[1]}-${partes[0]}" // Convertir a formato "yyyy-MM-dd"
                        } else {
                            fechaStr // Mantener el original si el formato no es válido
                        }
                    } catch (e: Exception) {
                        Log.e("FechaParse", "Error al convertir la fecha: $fechaStr", e)
                        fechaStr // Mantener el original en caso de error
                    }
                }

                DonacionItem(
                    parqueDonado = document.getString("parque_donado") ?: "",
                    fecha = fechaConvertida ?: "",
                    ubicacion = document.getString("ubicacion") ?: "",
                    donanteNombre = document.getString("donante_nombre") ?: "",
                    donanteContacto = document.getString("donante_contacto") ?: "",
                    registroEstado = document.getString("registro_estado") ?: "",
                    cantidad = document.getString("cantidad") ?: "",
                    recurso = document.getString("recurso") ?: "",
                    condicion = document.getString("condicion") ?: ""
                )

            }
            onSuccess(donacionesList)
        }
        .addOnFailureListener { exception ->
            Log.e("DonacionesEspecie", "Error al obtener donaciones de Firebase", exception)
            onSuccess(emptyList())
        }
}
