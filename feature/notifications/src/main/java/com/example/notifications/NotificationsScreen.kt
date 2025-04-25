package com.example.notifications

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import com.example.donations.R as RD

@Composable
fun NotificationsScreen(
    navController: NavController,
    showFilter: Boolean = false
) {
    // Get Firebase user UID once at the top level
    val currentUserUid = remember { FirebaseAuth.getInstance().currentUser?.uid }

    NotificationFilterManager(
        navController = navController,
        initialShowFilter = false
    )

    // Estado para controlar la visibilidad del filtro
    var isFilterVisible by rememberSaveable { mutableStateOf(false) }

    // Estados para gestionar las notificaciones
    var isLoading by remember { mutableStateOf(true) }
    var notificationsList by remember { mutableStateOf<List<GetAdminNotifications.AdminNotification>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Instancia del gestor de notificaciones
    val userNotificationManager = remember { GetAdminNotifications() }

    // Cargar las notificaciones al iniciar la pantalla
    LaunchedEffect(Unit) {
        userNotificationManager.getAdminNotifications(
            onSuccess = { notifications ->
                notificationsList = notifications.map { notification ->
                    notification.copy(
                        leido_por = notification.leido_por,
                        titulo = notification.titulo,
                        mensaje = notification.mensaje,
                        fecha = notification.fecha
                    ).apply {
                        val isRead = currentUserUid?.let { uid ->
                            leido_por.contains(uid)
                        } ?: false
                    }
                }
                isLoading = false
            },
            onFailure = { exception ->
                errorMessage = "Error al cargar notificaciones: ${exception.message}"
                isLoading = false
            }
        )
    }

    // Limpiar listeners al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            userNotificationManager.removeListeners()
        }
    }

    // Estados para los filtros aplicados
    var appliedSortFilter by remember { mutableStateOf("") }
    var appliedTimeFilter by remember { mutableStateOf("") }
    var appliedTypeFilter by remember { mutableStateOf("") }

    // Observar cambios en los filtros aplicados
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, String>>("appliedNotificationFilters")
            ?.observeForever { filters ->
                if (filters != null) {
                    appliedSortFilter = filters["sort"] ?: ""
                    appliedTimeFilter = filters["time"] ?: ""
                    appliedTypeFilter = filters["type"] ?: ""

                    // Aplicar los filtros a la lista de notificaciones
                    if (isLoading || errorMessage != null) return@observeForever

                    var filteredList = notificationsList

                    // Aplicar filtro de ordenamiento por nombre (A-Z o Z-A)
                    filteredList = when (appliedSortFilter) {
                        "A-Z" -> filteredList.sortedBy { it.titulo }
                        "Z-A" -> filteredList.sortedByDescending { it.titulo }
                        else -> filteredList
                    }

                    // Aplicar filtro de tiempo
                    filteredList = when (appliedTimeFilter) {
                        "Más recientes" -> filteredList.sortedByDescending { it.fecha }
                        "Más antiguas" -> filteredList.sortedBy { it.fecha }
                        else -> filteredList
                    }

                    notificationsList = filteredList
                }
            }
    }

    // Estructura principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
    ) {
        MainAppBar()

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 26.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Título
                Text(
                    text = "Historial de notificaciones",
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )

                // Botón de filtro
                Image(
                    painter = painterResource(id = RD.drawable.filter_list),
                    contentDescription = "Filtrar notificaciones",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "showFilterNotification",
                                true
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo con contador de notificaciones no leídas
            val unreadCount = notificationsList.count { !it.leido_por.contains(currentUserUid ?: "") }
            if (unreadCount > 0) {
                Text(
                    text = "Tienes $unreadCount notificaciones no leídas.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar el contenido según el estado
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
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Intentar de nuevo",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF78B153),
                                    textDecoration = TextDecoration.Underline
                                ),
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium)),
                                modifier = Modifier.clickable {
                                    isLoading = true
                                    errorMessage = null
                                    userNotificationManager.getAdminNotifications(
                                        onSuccess = { notifications ->
                                            notificationsList = notifications
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
                            text = "No tienes notificaciones.",
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
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(notificationsList) { notification ->
                            NotificationItem(
                                notification = notification,
                                currentUserUid = currentUserUid,
                                onMarkAsRead = {
                                    // Implementar marcado como leído
                                    userNotificationManager.markAsRead(notification.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente individual para mostrar cada notificación
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun NotificationItem(
    notification: GetAdminNotifications.AdminNotification,
    currentUserUid: String?, // Recibe el UID como parámetro
    onMarkAsRead: () -> Unit = {}
) {
    val verde = Color(0xFF78B153)

    // Usamos BoxWithConstraints para poder acceder al ancho máximo disponible
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Box principal de la notificación con sombra y bordes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .border(
                    width = 1.dp,
                    color = verde,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .clickable {
                    if (!notification.leido_por.contains(currentUserUid ?: "")) {
                        onMarkAsRead()
                    }
                }
        ) {
            // Contenido de la notificación
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Título de la notificación
                Text(
                    text = notification.titulo,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Mensaje de la notificación
                Text(
                    text = notification.mensaje,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha formateada
                val formattedDate = formatTimestamp(notification.fecha)
                Text(
                    text = formattedDate,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                )
            }
        }

        // Indicador verde de no leído
        if (!notification.leido_por.contains(currentUserUid ?: "")) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .offset(x = maxWidth - 10.dp, y = (-5).dp)
                    .background(color = verde, shape = RoundedCornerShape(8.dp))
                    .zIndex(1f)
            )
        }
    }
}

// Función para formatear timestamp a formato legible
fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("es", "ES"))
    return format.format(date)
}