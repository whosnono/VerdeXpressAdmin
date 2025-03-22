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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DonationsScreen() {
    // Variable para poder almacenar las donaciones en especie obtenidas desde Firebase
    var especieDonations by remember { mutableStateOf<List<DonationItem>>(emptyList()) }

    // Obtener las donaciones de Firebase al inicio
    LaunchedEffect(Unit) {
        getEspecieDonationsFromFirebase { donations ->
            especieDonations = donations
        }
    }

    // Manejar el error y mostrar datos predeterminados si la lista está vacía
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

    Scaffold(
        topBar = { MainAppBar() } // Colocamos MainAppBar en la topBar del Scaffold
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Usamos paddingValues para evitar que el contenido se superponga con la topBar
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
                donations = listOf(
                    DonationItem("El parque \"nombre\" recibió 50 mxn", "12:20 am"),
                    DonationItem("El parque \"nombre\" recibió 50 mxn", "14/03/2025"),
                    DonationItem("El parque \"nombre\" recibió 50 mxn", "12/03/2025"),
                    DonationItem("El parque \"nombre\" recibió 50 mxn", "12/03/2025")
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            DonationSection( // Sección de donaciones en especie (datos de Firebase)
                title = "Últimas donaciones en especie",
                donations = especieDonations // Usamos los datos obtenidos de Firebase aquí
            )
        }
    }
}

// Función para obtener las donaciones de Firebase
fun getEspecieDonationsFromFirebase(onSuccess: (List<DonationItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_especie").get() // Obtener documentos de la colección "donaciones_especie"
        .addOnSuccessListener { result ->
            // Mapear los documentos a objetos DonationItem
            val donationsList = result.map { document ->
                DonationItem(
                    description = "El parque \"${document.getString("parque_donado")}\" recibió ${document.getString("cantidad")} ${document.getString("recurso")}",
                    details = document.getString("condicion")
                        ?: ("Pendiente" + " - " + (document.getString("estimatedDonationDate")
                            ?: "Sin fecha"))
                )
            }
            onSuccess(donationsList) // Llamar a onSuccess con la lista de donaciones
        }
        .addOnFailureListener { exception ->
            // Registrar el error
            Log.e("DonationsScreen", "Error al obtener donaciones de Firebase", exception)
            onSuccess(emptyList()) // Se devuelve una lista vacía para indicar que se hallo un error
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
    val amountIndex = parts.indexOfLast { it.contains(Regex("\\d+")) } // Encuentra el índice de la parte que contiene el número (para que se vea como en el prototipo c:)
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