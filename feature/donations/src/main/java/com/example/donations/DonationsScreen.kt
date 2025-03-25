package com.example.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import com.example.design.MainAppBar
import com.example.design.R.font
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DonationsScreen() {
    var especieDonations by remember { mutableStateOf<List<DonationItem>>(emptyList()) }
    var monetariaDonations by remember { mutableStateOf<List<DonationItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        getEspecieDonationsFromFirebase { donations ->
            especieDonations = donations
        }
        getMonetariaDonationsFromFirebase { donations ->
            monetariaDonations = donations
        }
    }

    LaunchedEffect(especieDonations) {
        if (especieDonations.isEmpty()) {
            getEspecieDonationsFromFirebase { donations ->
                if (donations.isEmpty()) {
                    especieDonations = listOf(
                        DonationItem("Error al cargar datos", "Intente de nuevo más tarde")
                    )
                }
            }
        }
    }

    LaunchedEffect(monetariaDonations) {
        if (monetariaDonations.isEmpty()) {
            getMonetariaDonationsFromFirebase { donations ->
                if (donations.isEmpty()) {
                    monetariaDonations = listOf(
                        DonationItem("Error al cargar datos", "Intente de nuevo más tarde")
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = { MainAppBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Donaciones",
                style = TextStyle(
                    fontSize = 25.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    letterSpacing = 0.25.sp,
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            DonationSection(
                title = "Últimas donaciones monetarias",
                donations = monetariaDonations
            )

            Spacer(modifier = Modifier.height(16.dp))

            DonationSection(
                title = "Últimas donaciones en especie",
                donations = especieDonations
            )
        }
    }
}

fun getEspecieDonationsFromFirebase(onSuccess: (List<DonationItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_especie").get()
        .addOnSuccessListener { result ->
            val donationsList = result.map { document ->
                DonationItem(
                    description = "El parque \"${document.getString("parque_donado")}\" recibió ${document.getString("cantidad")} ${document.getString("tipo_recurso")}",
                    details = document.getString("condicion")
                        ?: ("registro_estado" + " - " + (document.getString("fecha_estimada_donacion")
                            ?: "Sin fecha"))
                )
            }
            onSuccess(donationsList)
        }
        .addOnFailureListener { exception ->
            Log.e("DonationsScreen", "Error al obtener donaciones de Firebase", exception)
            onSuccess(emptyList())
        }
}


fun getMonetariaDonationsFromFirebase(onSuccess: (List<DonationItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_monetaria").get()
        .addOnSuccessListener { result ->
            val donationsList = result.map { document ->
                val timestamp = document.getTimestamp("created_at")
                val formattedDate = timestamp?.let { formatTimestamp(it) } ?: "Sin fecha"
                DonationItem(
                    description = "El parque \"${document.getString("parque_seleccionado")}\" recibió ${document.getString("cantidad")} mxn",
                    details = formattedDate
                )
            }
            onSuccess(donationsList)
        }
        .addOnFailureListener { exception ->
            Log.e("DonationsScreen", "Error al obtener donaciones monetarias de Firebase", exception)
            onSuccess(emptyList())
        }
}

@Composable
fun DonationSection(title: String, donations: List<DonationItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 10.dp))
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 10.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        donations.forEach { donation ->
            DonationItemRow(donation = donation)
        }
    }
}

@Composable
fun DonationItemRow(donation: DonationItem) {
    val verde = Color(0xFF78B153)
    val parts = donation.description.split(" ")
    val amountIndex = parts.indexOfLast { it.contains(Regex("\\d+")) }
    val amount = if (amountIndex >= 0) {
        parts.subList(amountIndex, parts.size).joinToString(" ")
    } else {
        ""
    }
    val description = parts.subList(0, amountIndex).joinToString(" ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = "$description ",
                style = TextStyle(
                    fontSize = 11.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF000000),
                    letterSpacing = 0.25.sp,
                )
            )
            Text(
                text = amount,
                style = TextStyle(
                    fontSize = 11.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontWeight = FontWeight(500),
                    color = verde,
                    letterSpacing = 0.25.sp,
                )
            )
        }
        Text(
            text = donation.details,
            style = TextStyle(
                fontSize = 10.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(400),
                color = Color(0xFF484C52),
                letterSpacing = 0.25.sp,
            )
        )
    }
}

data class DonationItem(val description: String, val details: String)

// Función auxiliar para formatear el timestamp
fun formatTimestamp(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(timestamp.toDate())
}
