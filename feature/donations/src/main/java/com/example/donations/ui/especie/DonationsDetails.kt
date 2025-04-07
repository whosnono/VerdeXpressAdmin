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
import androidx.compose.ui.draw.clip
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
import com.example.donations.data.DonationImageShow
import com.example.donations.data.DonationsEData
import com.example.donations.data.acceptDonation
import com.example.donations.data.rejectDonation
import com.example.parks.data.MapView
import com.example.parks.data.ParkDataA
import com.example.parks.data.getParkDetails
import com.example.parks.ui.verde
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.io.println


@Composable
fun DonationsDetails(
    navController: NavController,
    donationId: String
) {
    var donationsEData by remember { mutableStateOf<DonationsEData?>(null) } //Para recibir los datos necesarios de la base de datos (getDonationsFromBD)
    var isLoading by remember { mutableStateOf(true) }
    var parkData by remember { mutableStateOf<ParkDataA?>(null) }
    var mapLoading by remember { mutableStateOf(true) }

    LaunchedEffect(donationId) {
        donationsEData = null
        FirebaseFirestore.getInstance()
            .collection("donaciones_especie")
            .document(donationId)
            .get()
            .addOnSuccessListener { document ->
                val fechaEstimada = document.getString("fecha_estimada_donacion")
                    ?: document.getString("estimatedDonationDate")

                val fechaConvertida = fechaEstimada?.let { fechaStr -> //Para evitar crasheos o que no se muestre la fecha debido a como está escrita, se dividen las tres partes de la fecha (año, mes y día) y después se reescriben con el formato normal
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

                donationsEData = DonationsEData( //Los datos que se deben mostrar en la ventana de detalles (Falta integrar las imagenes)
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
                    condicion = document.getString("condicion") ?: "",
                    razonRechazo = document.getString("razon_rechazo") //No todas las donaciones lo tienen por eso es diferente a las demas, no es un campo que a fuerza se deba mostrar
                )

                //En esta seccion se obtiene la informacion del parque (para obtener la latitud y la longitud)
                val parqueDonado = document.getString("parque_donado") ?: ""
                if (parqueDonado.isNotEmpty()) {
                    getParkDetails(
                        parkName = parqueDonado,
                        onSuccess = { parkDetails ->
                            parkData = parkDetails
                            mapLoading = false
                            isLoading = false
                        },
                        onFailure = { exception ->
                            Log.e("ParkDetails", "Error al obtener detalles del parque: $exception")
                            mapLoading = false
                            isLoading = false
                        }
                    )
                } else {
                    mapLoading = false
                    isLoading = false
                }
            }
            .addOnFailureListener {
                isLoading = false
                mapLoading = false
            }
    }

    if (isLoading || mapLoading) {
        Text("Cargando...")
    } else if (donationsEData != null) {
        val fechaConvertida = donationsEData!!.fecha.replace("-", "/") //Para que la fecha tenga un formato "año/mes/día"
        val cantidadFormateado = if (donationsEData!!.cantidad == "1") "1 pieza" else "${donationsEData!!.cantidad} piezas" //Para que detecte si se escribira en plural o no (basado en la cantidad de piezas)

        var showAcceptDialog by remember { mutableStateOf(false) } //Para mostrar el popup de Aprobar donaciones
        var showRejectDialog by remember { mutableStateOf(false) } //Para mostrar el popup de Rechazar donaciones
        var showSuccessDialog by remember { mutableStateOf(false) }
        var successMessage by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Column(modifier = Modifier //Columna principal
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) //Para poder scrollear hacia abajo de ser necesario
        ) {
            MainAppBar()

            //Barra principal para donaciones en especie
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

            //Detalles de la donación
            Row(
                modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Column (
                    modifier = Modifier
                        .weight(0.5f)
                ){
                    titulos(titulo = "Donado por")
                    Spacer(modifier = Modifier.height(8.dp))
                    detallesDonante(nombre = donationsEData!!.donanteNombre, numero = donationsEData!!.donanteContacto)
                    Spacer(modifier = Modifier.height(7.dp))
                    parqueTitulo(Parque = donationsEData!!.parqueDonado)
                }
                // Contenedor para MapView (aqui se visualiza el mapa)
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(150.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                ){
                  if (parkData != null) {
                      MapView(
                          latitud = parkData!!.latitud,
                          longitud = parkData!!.longitud,
                          modifier = Modifier.fillMaxSize()
                      )
                  } else{
                      Text("No se encontraron coordenadas para el parque.")
                  }
                }
            }
            textos(texto = donationsEData!!.ubicacion)
            Spacer(modifier = Modifier.height(11.dp))
            // Con esta funcion se despliegan las imagenes de la donación
            DonationImageShow(images = donationsEData!!.imagenes)
            Spacer(modifier = Modifier.height(15.dp))
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
            condicion(condition = if (donationsEData!!.condicion.isNullOrEmpty()) "Desconocida" else donationsEData!!.condicion) //En algunas donaciones este campo está vacío, cuando sea así, que muestre que la condición del recurso a donar es desconocida
            Spacer(modifier = Modifier.height(7.dp))
            titulos(titulo = "Fecha estimada de de entrega")
            Spacer(modifier = Modifier.height(7.dp))
            textos(texto = fechaConvertida)
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.weight(1f))

            //Cuando se detecte que el estado de la donación es "Pendiente" o "En revisión", mostrará la ventana de
            //detalles con los botones para aprobar o rechazar la donación
            when (donationsEData!!.registroEstado.lowercase()) {
                "pendiente" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(bottom = 23.dp),
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

                            Spacer(modifier = Modifier.width(15.dp))

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

                //Cuando se detecta que el estado de la donación es "Aprobada", mostrará
                //en vez de los botones el texto siguiente:
                "aprobada" -> {
                    textoEstado(
                        EstadoDonacion = "Donación aprobada"
                    )
                }

                //Cuando se detecta que el estado de la donación es "Rechazada", muestra un
                //texto de que fue rechazada, y un recuadro que contiene la razón que se dio para rechazar la donación
                "rechazada" -> {
                    textoEstado(
                        EstadoDonacion = "Donación rechazada"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    textRazones(
                        tr = "Razón"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!donationsEData!!.razonRechazo.isNullOrEmpty()) { //Si la razón no está vacia, muestra lo que hay en el campo "razon_rechazo"
                        razonCuadro(
                            reason = donationsEData!!.razonRechazo!!
                        )
                    } else { //Si no encuentra nada (lo cuál no debería de pasar), muestra que no hubo una razón específica
                        Text(
                            text = "Razón no especificada",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.Gray
                        )
                    }

                }
                else -> { //Si la aplicación no reconoce el estado registrado (tiene una palabra diferente a pendiente/aprobada/rechazada,
                    // muestra lo siguiente
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

        if (showRejectDialog) { //Abrir popup "DonationRejectDialog" y pasa el nombre del parque y recurso que se quieren rechazar
            DonationRejectDialog(
                parque = donationsEData!!.parqueDonado,
                recurso = donationsEData!!.recurso,
                onReject = { reason, password ->
                    Log.d("DonationsDetails", "Rejecting donation: donationId=${donationsEData!!.id}, reason=$reason, password=$password")
                    rejectDonation(donationId = donationsEData!!.id, password, reason) { success, message ->
                        Log.d("DonationsDetails", "Reject donation result: success=$success, message=$message")
                        if (success) { //Mensaje después que se rechazo exitosamente una donación
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

        if (showAcceptDialog) { //Abrir popup "DonationAcceptDialog" y pasa el nombre del parque y recurso que se quieren aprobar
            DonationAcceptDialog(
                parque = donationsEData!!.parqueDonado,
                recurso = donationsEData!!.recurso,
                onAccept = { password ->
                    Log.d("DonationsDetails", "Accepting donation: donationId=${donationsEData!!.id}, password=$password")
                    acceptDonation(donationId = donationsEData!!.id, password) { success, message ->
                        Log.d("DonationsDetails", "Accept donation result: success=$success, message=$message")
                        if (success) { //Mensaje después que se aprobo exitosamente una donación
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

        if (showSuccessDialog) { //Al aprobar o rechazar una donación, se cierra el popup y se abre un mensaje de alerta que
            //se realizaron los cambios
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
            fontSize = 25.sp,
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
fun condicion(condition: String) {
    Text(
        text = condition,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF78B153),
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
                fontFamily = FontFamily(Font(font.sf_pro_display_medium)),
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
                fontSize = 14.sp,
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
                fontFamily = FontFamily(Font(font.sf_pro_display_medium)),
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
                fontFamily = FontFamily(Font(font.sf_pro_display_medium)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}