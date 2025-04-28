package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.MainAppBar
import com.example.design.R
import com.example.design.R.font
import com.example.notifications.GetAdminNotifications
import com.example.notifications.NotificationItem
import com.example.notifications.formatTimestamp
import com.example.parks.data.rememberUserFullName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


@Composable
fun HomeScreen() {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""
    val userName = rememberUserFullName(userId)
    val scrollState = rememberScrollState()

    // Park service instance for dynamically getting parks
    val parkManager = remember { ParkManager() }
    var parksInReview by remember { mutableStateOf<List<ParkData>>(emptyList()) }

    // Donations manager instance
    val donationsManager = remember { DonationsManager() }
    var pendingDonations by remember { mutableStateOf<List<DonationData>>(emptyList()) }

    // Load parks and donations when the composable is displayed
    LaunchedEffect(Unit) {
        // Load parks in review
        parkManager.getParks(
            onSuccess = { parks ->
                parksInReview = parks
            },
            onFailure = { exception ->
                // Handle error if needed
            }
        )

        // Load pending donations
        donationsManager.getPendingDonations(
            onSuccess = { donations ->
                pendingDonations = donations
            },
            onFailure = { exception ->
                // Handle error if needed
            }
        )
    }

    // Remember to clean up listeners when not needed
    DisposableEffect(Unit) {
        onDispose {
            parkManager.cleanup()
            donationsManager.cleanup()
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
            Greeting(
                nombre = userName
            )

            Spacer(modifier = Modifier.height(10.dp))

            subtitulo(
                subtitle = "Resumen de notificaciones" //Valor fijo, no cambiar
            )

            Spacer(modifier = Modifier.height(1.dp))

            NPSection()

            Spacer(modifier = Modifier.height(6.dp))

            subtitulo(
                subtitle = "Resumen general" //Valor fijo, no cambiar
            )

            Spacer(modifier = Modifier.height(1.dp))

            ResumenGeneralSection(
                parksInReview = parksInReview,
                pendingDonations = pendingDonations
            )
        }
    }
}

@Composable
fun Greeting(nombre: String) { //Mensaje inicial de bienvenida
    Text(
        text = "Hola, $nombre",
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
fun subtitulo(subtitle: String) { //Para los subtitulos de color verde
    Text(
        text = subtitle,
        style = TextStyle(
            fontSize = 22.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF78B153),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun NPSection() { //Recuadro principal para mostrar las notificaciones más recientes

    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    // Estados para gestionar las notificaciones
    var isLoading by remember { mutableStateOf(true) }
    var notificationsList by remember { mutableStateOf<List<GetAdminNotifications.AdminNotification>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Instancia del gestor de notificaciones
    val userNotificationManager = remember { GetAdminNotifications() }

    // Add this LaunchedEffect to trigger the notification loading when the composable is displayed
    LaunchedEffect(Unit) {
        userNotificationManager.getAdminNotifications(
            onSuccess = { allNotifications ->
                // Filter notifications to only include those not read by current user
                val unreadNotifications = allNotifications.filter { notification ->
                    !notification.leido_por.contains(currentUserUid)
                }
                notificationsList = unreadNotifications
                isLoading = false
            },
            onFailure = { exception ->
                errorMessage = "Error al cargar notificaciones: ${exception.message}"
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 10.dp))
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 10.dp))
            .padding(16.dp)
            .height(200.dp)
    ) {
        Text(
            text = "Notificaciones pendientes", //Valor fijo, no cambiar
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Estado actual de parques apoyados", //Valor fijo, no cambiar
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )

        Spacer(modifier = Modifier.height(13.dp))

        // Mostrar el contenido según el estado dentro de un Box con altura fija
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f) // This will make the Box take the remaining space
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF78B153))
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage ?: "Error desconocido",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Red
                                ),
                                fontFamily = FontFamily(Font(com.example.design.R.font.sf_pro_display_medium))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Intentar de nuevo",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF78B153),
                                    textDecoration = TextDecoration.Underline
                                ),
                                fontFamily = FontFamily(Font(com.example.design.R.font.sf_pro_display_medium)),
                                modifier = Modifier.clickable {
                                    isLoading = true
                                    errorMessage = null
                                    userNotificationManager.getAdminNotifications(
                                        onSuccess = { allNotifications ->
                                            // Apply the same filter when retrying
                                            val unreadNotifications = allNotifications.filter { notification ->
                                                !notification.leido_por.contains(currentUserUid)
                                            }
                                            notificationsList = unreadNotifications
                                            isLoading = false
                                        },
                                        onFailure = { exception ->
                                            errorMessage = "Error al cargar notificaciones: ${exception.message}"
                                            isLoading = false
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
                notificationsList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes notificaciones pendientes.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray
                            ),
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                        )
                    }
                }
                else -> {
                    // Lista deslizable de notificaciones
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(notificationsList) { notification ->
                            NotificationItem(
                                notification = notification,
                                currentUserUid = currentUserUid,
                                onMarkAsRead = {
                                    // Implementar marcado como leído
                                    userNotificationManager.markAsRead(notification.id)
                                    // Remove this notification from the list after marking as read
                                    notificationsList = notificationsList.filter { it.id != notification.id }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationItem(notification: GetAdminNotifications.AdminNotification,
                     currentUserUid: String?) { //Para los recuadros donde se verá la información de las notificaciones

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 8.dp))
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = notification.titulo, //Lo puse así por que el titulo depende del tipo de notificación, no se como se debería hacer la lógica para esto
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF000000),
                        letterSpacing = 0.15.sp,
                    )
                )
                Row {

                    Text(
                        text = notification.mensaje,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                            letterSpacing = 0.15.sp,
                        )
                    )
                }
            }
        }
        Text(
            text = formatTimestamp(notification.fecha),
            style = TextStyle(
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(400),
                color = Color(0xFF757575),
                letterSpacing = 0.15.sp,
            )
        )
    }
}


@Composable
fun ResumenGeneralSection(
    parksInReview: List<ParkData>,
    pendingDonations: List<DonationData>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Construimos la lista de elementos de parques a mostrar
        val parkElements = mutableListOf<String>()
        parksInReview.take(3).forEach { park ->
            parkElements.add("Parque \"${park.nombre}\"")
        }

        // Si hay más de 3 parques, agregamos un indicador
        if (parksInReview.size > 3) {
            parkElements.add("+ ${parksInReview.size - 3} más")
        }

        // Parques en revisión section
        ResumenItem(
            titulo = "Parques en revisión",
            descripcion = "${parksInReview.size} parques en revisión",
            elementos = parkElements,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Construimos la lista de elementos de donaciones a mostrar
        val donationElements = mutableListOf<String>()
        val uniqueParkNames = pendingDonations.map { it.parkName }.distinct().take(3)

        uniqueParkNames.forEach { parkName ->
            donationElements.add("Parque \"$parkName\"")
        }

        // Si hay más de 3 parques con donaciones pendientes, agregamos un indicador
        if (pendingDonations.map { it.parkName }.distinct().size > 3) {
            donationElements.add("+ ${pendingDonations.map { it.parkName }.distinct().size - 3} más")
        }

        // Donaciones pendientes section
        ResumenItem(
            titulo = "Donaciones pendientes",
            descripcion = "${pendingDonations.size} donaciones por aprobar",
            elementos = donationElements,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResumenItem(titulo: String, descripcion: String, elementos: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 10.dp))
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 10.dp))
            .padding(16.dp)
            .width(182.dp)
            .height(255.dp)
    ) {
        Text(
            text = titulo,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(400),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column {
            if (elementos.isEmpty()) {
                Text(
                    text = "No hay elementos pendientes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight(500)
                    )
                )
            } else {
                elementos.forEachIndexed { index, elemento ->
                    // El último elemento (si es "+X más") se muestra diferente
                    if (index == elementos.size - 1 && elemento.startsWith("+")) {
                        Text(
                            text = elemento,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color(0xFFAED581), shape = RoundedCornerShape(size = 5.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight(500),
                            )
                        )
                    } else {
                        Text(
                            text = elemento,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color(0xFF78B153), shape = RoundedCornerShape(size = 5.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight(500)
                            )
                        )
                    }
                    if (index < elementos.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

// ParkData class definition
data class ParkData(
    val id: String,
    val nombre: String,
    val createdAt: Long // Timestamp for sorting
)

// Updated Park Manager class with sorting
class ParkManager {
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    fun getParks(onSuccess: (List<ParkData>) -> Unit, onFailure: (Exception) -> Unit) {
        listenerRegistration = firestore.collection("parques")
            .whereEqualTo("registro_estado", "pendiente")
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by most recent
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val parks = mutableListOf<ParkData>()
                if (result != null) {
                    for (document in result) {
                        val parkId = document.id
                        val nombre = document.getString("nombre") ?: "Desconocido"
                        // Get timestamp, default to current time if not available
                        val timestamp = document.getTimestamp("created_at")?.seconds ?: System.currentTimeMillis() / 1000
                        parks.add(ParkData(parkId, nombre, timestamp))
                    }
                }
                onSuccess(parks)
            }
    }

    // Don't forget to cancel the listener when not needed
    fun cleanup() {
        listenerRegistration?.remove()
    }
}

// New data class for donations
data class DonationData(
    val id: String,
    val parkName: String,
    val type: String, // "monetaria" or "especie"
    val createdAt: Long // Timestamp for sorting
)


// Updated Donations Manager class with sorting
class DonationsManager {
    private val firestore = FirebaseFirestore.getInstance()
    private var monetaryListenerRegistration: ListenerRegistration? = null
    private var speciesListenerRegistration: ListenerRegistration? = null

    fun getPendingDonations(onSuccess: (List<DonationData>) -> Unit, onFailure: (Exception) -> Unit) {
        val allDonations = mutableListOf<DonationData>()
        var completedQueries = 0
        var errorOccurred = false

        // Query for monetary donations
        monetaryListenerRegistration = firestore.collection("donaciones_monetaria")
            .whereEqualTo("registro_estado", "pendiente")
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by most recent
            .addSnapshotListener { monetaryResult, monetaryException ->
                if (monetaryException != null && !errorOccurred) {
                    errorOccurred = true
                    onFailure(monetaryException)
                    return@addSnapshotListener
                }

                if (monetaryResult != null) {
                    for (document in monetaryResult) {
                        val donationId = document.id
                        val parkName = document.getString("parque_seleccionado") ?: "Desconocido"
                        // Get timestamp, default to current time if not available
                        val timestamp = document.getTimestamp("created_at")?.seconds ?: System.currentTimeMillis() / 1000
                        allDonations.add(DonationData(donationId, parkName, "monetaria", timestamp))
                    }
                }

                completedQueries++
                if (completedQueries == 2 && !errorOccurred) {
                    // Sort combined results by timestamp (most recent first)
                    val sortedDonations = allDonations.sortedByDescending { it.createdAt }
                    onSuccess(sortedDonations)
                }
            }

        // Query for species donations
        speciesListenerRegistration = firestore.collection("donaciones_especie")
            .whereEqualTo("registro_estado", "pendiente")
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by most recent
            .addSnapshotListener { speciesResult, speciesException ->
                if (speciesException != null && !errorOccurred) {
                    errorOccurred = true
                    onFailure(speciesException)
                    return@addSnapshotListener
                }

                if (speciesResult != null) {
                    for (document in speciesResult) {
                        val donationId = document.id
                        val parkName = document.getString("parque_donado") ?: "Desconocido"
                        // Get timestamp, default to current time if not available
                        val timestamp = document.getTimestamp("created_at")?.seconds ?: System.currentTimeMillis() / 1000
                        allDonations.add(DonationData(donationId, parkName, "especie", timestamp))
                    }
                }

                completedQueries++
                if (completedQueries == 2 && !errorOccurred) {
                    // Sort combined results by timestamp (most recent first)
                    val sortedDonations = allDonations.sortedByDescending { it.createdAt }
                    onSuccess(sortedDonations)
                }
            }
    }

    // Don't forget to cancel listeners when not needed
    fun cleanup() {
        monetaryListenerRegistration?.remove()
        speciesListenerRegistration?.remove()
    }
}
