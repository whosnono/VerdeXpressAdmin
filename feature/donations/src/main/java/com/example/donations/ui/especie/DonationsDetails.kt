package com.example.donations.ui.especie

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.donations.data.DonationsEData
import com.example.donations.data.acceptDonation
import com.example.donations.data.rejectDonation
import com.example.parks.ui.verde
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun DonationsDetails(
    navController: NavController,
    donationId: String
) {
    var donationsEData by remember { mutableStateOf<DonationsEData?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(donationId) {
        FirebaseFirestore.getInstance()
            .collection("donaciones_especie")
            .document(donationId)
            .get()
            .addOnSuccessListener { document ->
                val fechaEstimada = document.getString("fecha_estimada_donacion")
                    ?: document.getString("estimatedDonationDate")

                val fechaConvertida = fechaEstimada?.let { fechaStr ->
                    try {
                        val partes = fechaStr.split("/")
                        if (partes.size == 3) {
                            "${partes[2]}-${partes[1]}-${partes[0]}"
                        } else {
                            fechaStr
                        }
                    } catch (e: Exception) {
                        Log.e("FechaParse", "Error al convertir la fecha: $fechaStr", e)
                        fechaStr
                    }
                }

                donationsEData = DonationsEData(
                    id = document.id,
                    parqueDonado = document.getString("parque_donado") ?: "",
                    fecha = fechaConvertida ?: "",
                    ubicacion = document.getString("ubicacion") ?: "",
                    imagenes = document.get("imagenes") as? List<String> ?: emptyList(),
                    donanteNombre = document.getString("donante_nombre") ?: "",
                    donanteContacto = document.getString("donante_contacto") ?: "",
                    registroEstado = document.getString("registro_estado") ?: "",
                    cantidad = document.getString("cantidad") ?: "",
                    recurso = document.getString("recurso") ?: "",
                    condicion = document.getString("condicion") ?: ""
                )
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    if (isLoading) {
        Text("Cargando...")
    } else if (donationsEData != null) {
        val fechaFormateada = donationsEData!!.fecha.replace("-", "/")
        val cantidadFormateado = if (donationsEData!!.cantidad == "1") "1 pieza" else "${donationsEData!!.cantidad} piezas"

        var showAcceptDialog by remember { mutableStateOf(false) }
        var showRejectDialog by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }
        var successMessage by remember { mutableStateOf("") }

        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var rejectionReason by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        var selectedEstadoActual by remember { mutableStateOf(donationsEData!!.registroEstado) }

        fun resetDialogStates() {
            password = ""
            confirmPassword = ""
            rejectionReason = ""
            errorMessage = null
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            MainAppBar()

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

            titulos(titulo = "Donado por")
            Spacer(modifier = Modifier.height(8.dp))
            detallesDonante(nombre = donationsEData!!.donanteNombre, numero = donationsEData!!.donanteContacto)
            Spacer(modifier = Modifier.height(7.dp))
            parqueTitulo(Parque = donationsEData!!.parqueDonado)
            Spacer(modifier = Modifier.height(7.dp))
            textos(texto = donationsEData!!.ubicacion)
            Spacer(modifier = Modifier.height(11.dp))
            titulos(titulo = "Recurso a donar")
            Spacer(modifier = Modifier.height(7.dp))
            textos(texto = donationsEData!!.recurso)
            Spacer(modifier = Modifier.height(7.dp))
            titulos(titulo = "Cantidad")
            Spacer(modifier = Modifier.height(7.dp))
            textos(texto = cantidadFormateado)
            Spacer(modifier = Modifier.height(7.dp))
            titulos(titulo = "Condicion del recurso")
            Spacer(modifier = Modifier.height(7.dp))
            textos(texto = if (donationsEData!!.condicion.isNullOrEmpty()) "Desconocida" else donationsEData!!.condicion)
            Spacer(modifier = Modifier.height(7.dp))
            titulos(titulo = "Fecha estimada de de entrega")
            Spacer(modifier = Modifier.height(7.dp))
            textos(texto = fechaFormateada)

            Spacer(modifier = Modifier.weight(1f))

            when (donationsEData!!.registroEstado.lowercase()) {
                "pendiente" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { showRejectDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = verde
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, verde)
                            ) {
                                Text("Rechazar donación")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = { showAcceptDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = verde,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Aceptar donación")
                            }
                        }
                    }
                }
                "aprobada" -> {
                    textoEstado(
                        EstadoDonacion = "Donación aprobada"
                    )
                }
                "rechazada" -> {
                    textoEstado(
                        EstadoDonacion = "Donación rechazada"
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    textRazones(
                        tr = "Razón"
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    razonCuadro(
                        reason = ""
                    )


                }
                else -> {
                    Text(
                        text = "Estado desconocido",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showAcceptDialog) {
            DonationAcceptDialog(
                parque = donationsEData!!.parqueDonado,
                recurso = donationsEData!!.recurso,
                onAccept = { password ->
                    acceptDonation(donationId = donationsEData!!.id, password) { success, message ->
                        if (success) {
                            successMessage = "Donación aprobada exitosamente"
                            showSuccessDialog = true
                            showAcceptDialog = false
                        } else {
                            errorMessage = message
                        }
                    }
                },
                onDismiss = { showAcceptDialog = false }
            )
        }

        if (showRejectDialog) {
            DonationRejectDialog(
                parque = donationsEData!!.parqueDonado,
                recurso = donationsEData!!.recurso,
                onReject = { reason, password ->
                    rejectDonation(donationId = donationsEData!!.id, password) { success, message ->
                        if (success) {
                            successMessage = "Donación rechazada exitosamente"
                            showSuccessDialog = true
                            showRejectDialog = false
                        } else {
                            errorMessage = message
                        }
                    }
                },
                onDismiss = { showRejectDialog = false }
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    navController.popBackStack()
                },
                containerColor = Color.White,
                title = { Text("Éxito") },
                text = { Text(successMessage) },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verde,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Aceptar"
                        )
                    }
                }
            )
        }
    } else {
        Text("Error al cargar los datos de la donación.")
    }
}


@Composable
fun parqueTitulo(Parque: String) {
    Text(
        text = Parque,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = TextStyle(
            fontSize = 22.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun titulos(titulo: String) {
    Text(
        text = titulo,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun textos(texto: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = texto,
            style = TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
    }
}

@Composable
fun textRazones(tr: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = tr,
            style = TextStyle(
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF565656),
                letterSpacing = 0.25.sp,
            )
        )
    }
}

@Composable
fun textoEstado(EstadoDonacion: String) {
    val isAprobada = EstadoDonacion.equals("Donación aprobada")
    val colorEstado = if (isAprobada) Color(0xFF78B153) else Color(0xFF852221)

    Text(
        text = EstadoDonacion,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = TextStyle(
            fontSize = 28.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = colorEstado,
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun razonCuadro(
    reason: String
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "[Admin]: \"$reason\"",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun detallesDonante(nombre: String, numero: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = nombre,
            style = TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = numero,
            style = TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}