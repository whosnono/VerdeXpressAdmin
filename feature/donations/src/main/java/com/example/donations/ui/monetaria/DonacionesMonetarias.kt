package com.example.donations.ui.monetaria

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.R.font
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DonacionesMonetarias(navController: NavController) {
    var donaciones by remember { mutableStateOf<List<DonacionItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        getDonacionesMonetariaFromFirebase { items ->
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
                text = "Donaciones monetarias",
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
            Text(
                text = "Monto",
                style = TextStyle(
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F4946)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))


        LazyColumn {
            items(donaciones) { donacion ->
                DonacionesCuadro(
                    title = donacion.parqueDonado,
                    date = donacion.fecha,
                    address = donacion.ubicacion,
                    person = donacion.donanteNombre,
                    correo = donacion.donanteContacto,
                    monto = donacion.montoDonado
                )
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
    correo: String,
    monto: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Márgenes laterales
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = Color(0xFF4D4447), shape = RoundedCornerShape(10.dp))
            .padding(15.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Fila con el título y la información de la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(10.dp)) // Espacio entre título y la derecha
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4D4447),
                    textAlign = TextAlign.End
                )
            }

            // Dirección
            Text(text = address, style = MaterialTheme.typography.bodyMedium)

            // Persona y correo
            Text(text = person, style = MaterialTheme.typography.bodyMedium)
            Text(text = correo, style = MaterialTheme.typography.bodyMedium)

            // Monto en una fila a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$$monto mxn",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78B153),
                    fontSize = 20.sp
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
    val montoDonado: String
)

fun getDonacionesMonetariaFromFirebase(onSuccess: (List<DonacionItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_monetaria").get()
        .addOnSuccessListener { result ->
            val donacionesList = result.map { document ->
                val timestamp = document.getTimestamp("created_at")
                val formattedDate = timestamp?.let { formatTimestamp(it) } ?: "Sin fecha"

                DonacionItem(
                    parqueDonado = document.getString("parque_seleccionado") ?: "",
                    fecha = formattedDate,
                    ubicacion = document.getString("ubicacion_seleccionada") ?: "",
                    donanteNombre = document.getString("donante_nombre") ?: "",
                    donanteContacto = document.getString("donante_correo") ?: "",
                    montoDonado = document.getString("cantidad") ?: ""
                )
            }
            onSuccess(donacionesList)
        }
        .addOnFailureListener { exception ->
            Log.e("DonacionesMonetarias", "Error al obtener donaciones de Firebase", exception)
            onSuccess(emptyList())
        }
}

fun formatTimestamp(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(timestamp.toDate())
}
