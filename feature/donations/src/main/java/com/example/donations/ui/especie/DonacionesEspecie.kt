package com.example.donations.ui.especie

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.R.font
import com.example.donations.data.DonationsEData
import com.example.donations.data.getDonacionesEspecieFromFirebase
import com.example.donations.ui.monetaria.DonacionItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DonacionesEspecie(navController: NavController) {
    var donacionesOriginal by remember { mutableStateOf<List<DonationsEData>>(emptyList()) }
    var donacionesFiltradas by remember { mutableStateOf<List<DonationsEData>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var ordenReciente by remember { mutableStateOf(true) } // Estado para el ordenamiento
    var filtroActivo by remember { mutableStateOf("") } //Detecta que tipo de sorteo se escogío, empieza vació para que todos los textos en la barra de filtros estén 'apagados' (en su color original)
    var recienteTextStyle by remember {
        mutableStateOf(
            TextStyle(
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (filtroActivo == "Reciente") Color(0xFF78B153) else Color(0xFF3F4946),
                textDecoration = if (filtroActivo == "Reciente") TextDecoration.Underline else TextDecoration.None
            )
        )
    }
    var antiguasTextStyle by remember {
        mutableStateOf(
            TextStyle(
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (filtroActivo == "Más antiguas") Color(0xFF78B153) else Color(0xFF3F4946),
                textDecoration = if (filtroActivo == "Más antiguas") TextDecoration.Underline else TextDecoration.None
            )
        )
    }

    LaunchedEffect(Unit) {
        getDonacionesEspecieFromFirebase { items ->
            donacionesOriginal = items
            donacionesFiltradas = items // Inicialmente, mostrar todas las donaciones
        }
    }

    LaunchedEffect(searchText) {
        donacionesFiltradas = filterDonacionesEspecie(searchText, donacionesOriginal)
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textStyle = TextStyle(color = Color.Black),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text(
                                "Buscar donación...",
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Gray)
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
                text = "Recientes",
                style = recienteTextStyle,
                modifier = Modifier.clickable {
                    filtroActivo = "Reciente"
                    ordenReciente = true
                    val listaOrdenada = ordenarDonacionesInternoEspecie(donacionesFiltradas, ordenReciente)
                    donacionesFiltradas = listaOrdenada
                    recienteTextStyle = recienteTextStyle.copy(
                        color = Color(0xFF78B153),
                        textDecoration = TextDecoration.Underline
                    )
                    antiguasTextStyle = antiguasTextStyle.copy(
                        color = Color(0xFF3F4946),
                        textDecoration = TextDecoration.None
                    )
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Más antiguas",
                style = antiguasTextStyle,
                modifier = Modifier.clickable {
                    filtroActivo = "Más antiguas"
                    ordenReciente = false
                    val listaOrdenada = ordenarDonacionesInternoEspecie(donacionesFiltradas, ordenReciente)
                    donacionesFiltradas = listaOrdenada
                    antiguasTextStyle = antiguasTextStyle.copy(
                        color = Color(0xFF78B153),
                        textDecoration = TextDecoration.Underline
                    )
                    recienteTextStyle = recienteTextStyle.copy(
                        color = Color(0xFF3F4946),
                        textDecoration = TextDecoration.None
                    )
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(5.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(donacionesFiltradas) { donacion -> // Usamos la lista filtrada aquí
                DonacionesCuadro(
                    title = donacion.parqueDonado,
                    date = donacion.created_at,
                    address = donacion.ubicacion,
                    person = donacion.donanteNombre,
                    telefono = donacion.donanteContacto,
                    status = donacion.registroEstado,
                    amount = donacion.cantidad,
                    resource = donacion.recurso,
                    condition = donacion.condicion,
                    onClick = {
                        navController.navigate("DonationsDetails/${donacion.id}")
                    }
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
    val statusText = if (isPendiente) "Pendiente" else status


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
                modifier = Modifier.weight(1f),
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

fun filterDonacionesEspecie(searchText: String, donaciones: List<DonationsEData>): List<DonationsEData> {
    if (searchText.isBlank()) {
        return donaciones
    }
    val lowerCaseSearchText = searchText.lowercase()

    return donaciones.filter { donacion ->
        donacion.parqueDonado.lowercase().contains(lowerCaseSearchText) ||
                donacion.ubicacion.lowercase().contains(lowerCaseSearchText) ||
                donacion.donanteNombre.lowercase().contains(lowerCaseSearchText) ||
                donacion.donanteContacto.contains(lowerCaseSearchText) ||
                donacion.registroEstado.lowercase().contains(lowerCaseSearchText)
    }
}

fun ordenarDonacionesInternoEspecie(
    donaciones: List<DonationsEData>,
    ordenReciente: Boolean
): List<DonationsEData> {
    // Usamos la fecha formateada de created_at para ordenar
    val listaOrdenada = donaciones.sortedWith(compareBy { donacion ->
        try {
            // Ajustamos el formato para que coincida con "17/03/2025 15:37"
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            dateFormat.parse(donacion.created_at)
        } catch (e: Exception) {
            // Si hay un error con el formato principal, intentamos con formatos alternativos
            try {
                // Intentamos con otro formato posible
                val alternateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                alternateFormat.parse(donacion.created_at)
            } catch (e2: Exception) {
                Log.e("OrdenarDonaciones", "Error al parsear fecha: ${donacion.created_at}", e)
                null // Si todos los intentos fallan, retornamos null
            }
        }
    })

    // Si queremos ordenar de más reciente a más antiguo, invertimos el resultado
    return if (ordenReciente) {
        listaOrdenada.reversed() // Lo más reciente primero
    } else {
        listaOrdenada // Lo más antiguo primero
    }
}