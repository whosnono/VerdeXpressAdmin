package com.example.parks.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun acceptPark(
    parkName: String,
    password: String,
    onComplete: (Boolean, String?) -> Unit
) {
    verifyAdminPassword(password) { isValid ->
        if (!isValid) {
            onComplete(false, "Contraseña de administrador incorrecta")
            return@verifyAdminPassword
        }

        val db = FirebaseFirestore.getInstance()
        val updates = hashMapOf<String, Any>(
            "registro_estado" to "aprobado"
        )

        db.collection("parques")
            .whereEqualTo("nombre", parkName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    onComplete(false, "Parque no encontrado")
                    return@addOnSuccessListener
                }

                val parkDoc = querySnapshot.documents.first()
                val userId = parkDoc.getString("registro_usuario") ?: ""

                parkDoc.reference.update(updates)
                    .addOnSuccessListener {
                        // Create notification for the user
                        if (userId.isNotEmpty()) {
                            createApprovalNotification(parkName, userId) { success, error ->
                                if (!success) {
                                    // Still consider the operation successful even if notification fails
                                    onComplete(true, "Parque aprobado, pero hubo un error al crear la notificación: $error")
                                } else {
                                    onComplete(true, null)
                                }
                            }
                        } else {
                            onComplete(true, null)
                        }
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "Error al actualizar: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error al buscar parque: ${e.message}")
            }
    }
}

fun rejectPark(
    parkName: String,
    reason: String,
    password: String,
    onComplete: (Boolean, String?) -> Unit
) {
    verifyAdminPassword(password) { isValid ->
        if (!isValid) {
            onComplete(false, "Contraseña de administrador incorrecta")
            return@verifyAdminPassword
        }

        val db = FirebaseFirestore.getInstance()
        val updates = hashMapOf<String, Any>(
            "registro_estado" to "rechazado",
            "razon_rechazo" to reason
        )

        db.collection("parques")
            .whereEqualTo("nombre", parkName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    onComplete(false, "Parque no encontrado")
                    return@addOnSuccessListener
                }

                val parkDoc = querySnapshot.documents.first()
                val userId = parkDoc.getString("registro_usuario") ?: ""

                parkDoc.reference.update(updates)
                    .addOnSuccessListener {
                        // Create rejection notification for the user
                        if (userId.isNotEmpty()) {
                            createRejectionNotification(parkName, reason, userId) { success, error ->
                                if (!success) {
                                    // Still consider the operation successful even if notification fails
                                    onComplete(true, "Parque rechazado, pero hubo un error al crear la notificación: $error")
                                } else {
                                    onComplete(true, null)
                                }
                            }
                        } else {
                            onComplete(true, null)
                        }
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "Error al actualizar: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error al buscar parque: ${e.message}")
            }
    }
}

fun deletePark(
    parkName: String,
    confirmationText: String,
    password: String,
    onComplete: (Boolean, String?) -> Unit
) {
    // Verificar que el texto de confirmación sea correcto
    if (confirmationText.lowercase() != "eliminar") {
        onComplete(false, "Texto de confirmación incorrecto")
        return
    }

    verifyAdminPassword(password) { isValid ->
        if (!isValid) {
            onComplete(false, "Contraseña de administrador incorrecta")
            return@verifyAdminPassword
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("parques")
            .whereEqualTo("nombre", parkName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    onComplete(false, "Parque no encontrado")
                    return@addOnSuccessListener
                }

                querySnapshot.documents.first().reference.delete()
                    .addOnSuccessListener { onComplete(true, null) }
                    .addOnFailureListener { e ->
                        onComplete(false, "Error al eliminar: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error al buscar parque: ${e.message}")
            }
    }
}

// Nuevas funciones para crear notificaciones
private fun createApprovalNotification(
    parkName: String,
    userId: String,
    callback: (Boolean, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    val notificationData = hashMapOf(
        "titulo" to "¡Tu parque ha sido aprobado!",
        "mensaje" to "Tu solicitud para registrar el parque $parkName ha sido revisada y aprobada por un administrador. A partir de ahora puedes visualizar este cambio en la sección de Parques de la app. Tu acción hace la diferencia. ¡Gracias!",
        "fecha" to FieldValue.serverTimestamp(),
        "leido" to false,
        "destinatario" to userId
    )

    db.collection("notificaciones_user")
        .add(notificationData)
        .addOnSuccessListener {
            callback(true, null)
        }
        .addOnFailureListener { e ->
            callback(false, e.message)
        }
}

private fun createRejectionNotification(
    parkName: String,
    reason: String,
    userId: String,
    callback: (Boolean, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    val notificationData = hashMapOf(
        "titulo" to "Tu parque ha sido rechazado",
        "mensaje" to "La solicitud para registrar el parque $parkName fue revisada y rechazada por un administrador. Te recomendamos realizar los ajustes necesarios y volver a enviar tu solicitud. Motivo del rechazo: $reason",
        "fecha" to FieldValue.serverTimestamp(),
        "leido" to false,
        "destinatario" to userId
    )

    db.collection("notificaciones_user")
        .add(notificationData)
        .addOnSuccessListener {
            callback(true, null)
        }
        .addOnFailureListener { e ->
            callback(false, e.message)
        }
}

// Función para verificar contraseña de administrador (simplificada)
private fun verifyAdminPassword(
    password: String,
    callback: (Boolean) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        callback(false)
        return
    }

    // Crear credenciales con email y contraseña
    val credential = EmailAuthProvider.getCredential(user.email ?: "", password)

    // Reautenticar al usuario
    user.reauthenticate(credential)
        .addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
}