package com.example.donations.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.R.font
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun DonationsScreen(navController: NavController) {
    var especieDonations by remember { mutableStateOf<List<DonationItem>>(emptyList()) }
    var monetariaDonations by remember { mutableStateOf<List<DonationItem>>(emptyList()) }
    var isLoadingEspecie by remember { mutableStateOf(true) }
    var isLoadingMonetaria by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        getEspecieDonationsFromFirebase { donations ->
            especieDonations = donations
            isLoadingEspecie = false
        }
        getMonetariaDonationsFromFirebase { donations ->
            monetariaDonations = donations
            isLoadingMonetaria = false
        }
    }

    LaunchedEffect(especieDonations) {
        if (especieDonations.isEmpty() && !isLoadingEspecie) {
            getEspecieDonationsFromFirebase { donations ->
                if (donations.isEmpty()) {
                    especieDonations = listOf(
                        DonationItem("Error al cargar datos", "Intente de nuevo más tarde")
                    )
                }
                isLoadingEspecie = false
            }
        }
    }

    LaunchedEffect(monetariaDonations) {
        if (monetariaDonations.isEmpty() && !isLoadingMonetaria) {
            getMonetariaDonationsFromFirebase { donations ->
                if (donations.isEmpty()) {
                    monetariaDonations = listOf(
                        DonationItem("Error al cargar datos", "Intente de nuevo más tarde")
                    )
                }
                isLoadingMonetaria = false
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
                .verticalScroll(rememberScrollState())
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

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        navController.navigate("DonacionesMonetarias")
                    }
                    .height(280.dp) // Altura fija para el contenedor
                    .fillMaxWidth()
            ) {
                DonationSection(
                    title = "Últimas donaciones monetarias",
                    donations = monetariaDonations,
                    isLoading = isLoadingMonetaria
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        navController.navigate("DonacionesEspecie")
                    }
                    .height(280.dp) // Altura fija para el contenedor
                    .fillMaxWidth()
            ) {
                DonationSection(
                    title = "Últimas donaciones en especie",
                    donations = especieDonations,
                    isLoading = isLoadingEspecie
                )
            }
        }
    }
}

fun getEspecieDonationsFromFirebase(onSuccess: (List<DonationItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_especie").get()
        .addOnSuccessListener { result ->
            val donationsList = result.map { document ->
                val registroEstado = document.getString("registro_estado")
                var fechaEstimada = document.getString("fecha_estimada_donacion")

                // Verificar si el campo "estimatedDonationDate" existe y usarlo si es necesario
                if (fechaEstimada == null) {
                    fechaEstimada = document.getString("estimatedDonationDate")
                }

                val detalles = if (registroEstado != null && fechaEstimada != null) {
                    "$registroEstado - $fechaEstimada"
                } else {
                    "Información de estado o fecha no disponible"
                }

                val recursoCompleto = document.getString("recurso") ?: ""
                val primerRecurso = recursoCompleto.split(" ").firstOrNull() ?: ""

                DonationItem(
                    description = "El parque \"${document.getString("parque_donado")}\" recibió ${document.getString("cantidad")} $primerRecurso",
                    details = detalles
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
fun DonationSection(title: String, donations: List<DonationItem>, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 10.dp))
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 10.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF78B153),
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(donations) { donation ->
                        DonationItemRow(donation = donation)
                    }
                }
            }
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
    val description = if (amountIndex > 0) {
        parts.subList(0, amountIndex).joinToString(" ")
    } else {
        // Si no se encuentra el índice del monto, usa toda la lista como descripción
        // o proporciona un valor predeterminado
        parts.joinToString(" ")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Parte izquierda con descripción y monto
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$description ",
                style = TextStyle(
                    fontSize = 11.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF000000),
                    letterSpacing = 0.15.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = amount,
                style = TextStyle(
                    fontSize = 11.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                    fontWeight = FontWeight(500),
                    color = verde,
                    letterSpacing = 0.15.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Espaciador flexible
        Spacer(modifier = Modifier.width(8.dp))

        // Parte derecha con la fecha, alineada a la derecha
        Text(
            text = donation.details,
            style = TextStyle(
                fontSize = 8.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(400),
                color = Color(0xFF484C52),
                letterSpacing = 0.15.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.wrapContentWidth(Alignment.End)
        )
    }
}

data class DonationItem(val description: String, val details: String)

// Función auxiliar para formatear el timestamp
fun formatTimestamp(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(timestamp.toDate())
}