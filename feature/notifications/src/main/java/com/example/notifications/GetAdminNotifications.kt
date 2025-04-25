package com.example.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

/**
 * Clase para gestionar la obtención de notificaciones del usuario desde Firestore.
 */
class GetAdminNotifications {

    private val db = FirebaseFirestore.getInstance()
    internal val auth = FirebaseAuth.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    /**
     * Clase de datos para representar una notificación de usuario.
     */
    data class AdminNotification(
        val id: String = "",
        val titulo: String = "",
        val mensaje: String = "",
        val fecha: Long = 0,
        val leido_por: List<String> = emptyList(),
    )

    /**
     * Obtiene las notificaciones del usuario actual.
     *
     * @param onSuccess Callback que se ejecuta cuando se obtienen las notificaciones correctamente.
     * @param onFailure Callback que se ejecuta cuando ocurre un error al obtener las notificaciones.
     */
    fun getAdminNotifications(
        onSuccess: (List<AdminNotification>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }

        val uid = currentUser.uid

        // Consulta las notificaciones en Firestore
        listenerRegistration = db.collection("notificaciones_admin")
            .orderBy("fecha", Query.Direction.DESCENDING) // Ordenar por fecha, más recientes primero
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notificationsList = mutableListOf<AdminNotification>()

                    for (document in snapshot.documents) {
                        try {
                            // Manejar correctamente el campo fecha que es un Timestamp
                            val fechaValue = document.get("fecha")
                            val fechaLong = when (fechaValue) {
                                is Timestamp -> fechaValue.seconds * 1000 // Convertir segundos a milisegundos
                                is Long -> fechaValue
                                is Double -> fechaValue.toLong()
                                null -> 0L
                                else -> 0L
                            }

                            val notification = AdminNotification(
                                id = document.id,
                                titulo = document.getString("titulo") ?: "",
                                mensaje = document.getString("mensaje") ?: "",
                                fecha = fechaLong,
                                leido_por = document.get("leido_por") as? List<String> ?: emptyList())
                            notificationsList.add(notification)
                        } catch (ex: Exception) {
                            // Log the error but continue processing other notifications
                            println("Error procesando notificación ${document.id}: ${ex.message}")
                        }
                    }

                    onSuccess(notificationsList)
                } else {
                    onSuccess(emptyList())
                }
            }
    }

    /**
     * Marca una notificación como leída en Firestore.
     *
     * @param notificationId ID de la notificación a marcar como leída.
     */
    fun markAsRead(notificationId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("notificaciones_admin")
                .document(notificationId)
                .update("leido_por", FieldValue.arrayUnion(uid))
        }
    }

    /**
     * Elimina los listeners para evitar fugas de memoria.
     */
    fun removeListeners() {
        listenerRegistration?.remove()
    }
}