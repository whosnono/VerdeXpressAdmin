package com.example.donations.data

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun acceptDonation(
    donationId: String,
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
            "registro_estado" to "Aprobada"
        )

        // Primero obtener los datos de la donación para la notificación
        db.collection("donaciones_especie")
            .document(donationId)
            .get()
            .addOnSuccessListener { document ->
                val userId = document.getString("registro_usuario") ?: ""
                val recurso = document.getString("recurso") ?: ""

                // Usar el nombre de campo correcto: parque_donado
                val nombreParque = document.getString("parque_donado") ?: ""

                Log.d("AcceptDonation", "userId: $userId, recurso: $recurso, nombreParque: $nombreParque")

                // Actualizar el estado de la donación
                db.collection("donaciones_especie")
                    .document(donationId)
                    .update(updates)
                    .addOnSuccessListener {
                        // Crear notificación para el usuario
                        if (userId.isNotEmpty()) {
                            createApprovalNotification(recurso, nombreParque, userId) { success, error ->
                                if (!success) {
                                    // Consideramos la operación exitosa aunque falle la notificación
                                    onComplete(true, "Donación aprobada, pero hubo un error al crear la notificación: $error")
                                } else {
                                    onComplete(true, null)
                                }
                            }
                        } else {
                            onComplete(true, null) // Éxito
                        }
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "Error al actualizar: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error al obtener información de la donación: ${e.message}")
            }
    }
}

fun rejectDonation(
    donationId: String,
    password: String,
    reason: String,
    onComplete: (Boolean, String?) -> Unit
) {
    Log.d("RejectDonation", "donationId: $donationId, reason: $reason")
    verifyAdminPassword(password) { isValid ->
        if (!isValid) {
            onComplete(false, "Contraseña de administrador incorrecta")
            return@verifyAdminPassword
        }

        val db = FirebaseFirestore.getInstance()

        // Primero, obtenemos el documento actual
        db.collection("donaciones_especie")
            .document(donationId)
            .get()
            .addOnSuccessListener { document ->
                val fechaEstimada = document.getString("fecha_estimada_donacion")
                    ?: document.getString("estimatedDonationDate")
                    ?: "" // Si no existe, ponemos un valor por defecto (vacío)

                val userId = document.getString("registro_usuario") ?: ""
                val recurso = document.getString("recurso") ?: ""

                // Usar el nombre de campo correcto: parque_donado
                val nombreParque = document.getString("parque_donado") ?: ""

                Log.d("RejectDonation", "userId: $userId, recurso: $recurso, nombreParque: $nombreParque")

                val updates = hashMapOf<String, Any>(
                    "registro_estado" to "Rechazada",
                    "razon_rechazo" to reason,
                    "fecha_estimada_donacion" to fechaEstimada //Hice esto pq al rechazar una donación estaba eliminando este campo
                )
                Log.d("RejectDonation", "updates: $updates")

                db.collection("donaciones_especie")
                    .document(donationId)
                    .update(updates)
                    .addOnSuccessListener {
                        Log.d("RejectDonation", "Donation rejected successfully")

                        // Crear notificación de rechazo para el usuario
                        if (userId.isNotEmpty()) {
                            createRejectionNotification(recurso, nombreParque, reason, userId) { success, error ->
                                if (!success) {
                                    // Consideramos la operación exitosa aunque falle la notificación
                                    onComplete(true, "Donación rechazada, pero hubo un error al crear la notificación: $error")
                                } else {
                                    onComplete(true, null)
                                }
                            }
                        } else {
                            onComplete(true, null) // Éxito
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("RejectDonation", "Error rejecting donation: ${e.message}")
                        onComplete(false, "Error al actualizar: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("RejectDonation", "Error al obtener el documento: ${e.message}")
                onComplete(false, "Error al obtener la información de la donación.")
            }
    }
}

// Nuevas funciones para crear notificaciones
private fun createApprovalNotification(
    recurso: String,
    nombreParque: String,
    userId: String,
    callback: (Boolean, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    val notificationData = hashMapOf(
        "titulo" to "¡Tu donación en especie ha sido aprobada!",
        "mensaje" to "Tu solicitud para donar $recurso al parque $nombreParque ha sido revisada y aprobada por un administrador. Puedes ver los detalles y consultar tu comprobante en la sección Donaciones de la app. Gracias por sumarte al esfuerzo por un Hermosillo más verde.",
        "fecha" to FieldValue.serverTimestamp(),
        "leido" to false,
        "destinatario" to userId
    )

    Log.d("CreateApprovalNotification", "Creating notification: $notificationData")

    db.collection("notificaciones_user")
        .add(notificationData)
        .addOnSuccessListener {
            Log.d("CreateApprovalNotification", "Notification created successfully")
            callback(true, null)
        }
        .addOnFailureListener { e ->
            Log.e("CreateApprovalNotification", "Error creating notification: ${e.message}")
            callback(false, e.message)
        }
}

private fun createRejectionNotification(
    recurso: String,
    nombreParque: String,
    reason: String,
    userId: String,
    callback: (Boolean, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    val notificationData = hashMapOf(
        "titulo" to "Tu donación en especie ha sido rechazada",
        "mensaje" to "Tu solicitud para donar $recurso al parque $nombreParque fue revisada y rechazada por un administrador. Te recomendamos realizar los ajustes necesarios y volver a enviar tu solicitud. Motivo del rechazo: $reason",
        "fecha" to FieldValue.serverTimestamp(),
        "leido" to false,
        "destinatario" to userId
    )

    Log.d("CreateRejectionNotification", "Creating notification: $notificationData")

    db.collection("notificaciones_user")
        .add(notificationData)
        .addOnSuccessListener {
            Log.d("CreateRejectionNotification", "Notification created successfully")
            callback(true, null)
        }
        .addOnFailureListener { e ->
            Log.e("CreateRejectionNotification", "Error creating notification: ${e.message}")
            callback(false, e.message)
        }
}

private fun verifyAdminPassword(
    password: String,
    onResult: (Boolean) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onResult(false)
        return
    }

    val credential = EmailAuthProvider.getCredential(user.email ?: "", password)
    user.reauthenticate(credential).addOnCompleteListener { task ->
        onResult(task.isSuccessful)
    }
}