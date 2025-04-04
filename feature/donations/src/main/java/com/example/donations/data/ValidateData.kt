package com.example.donations.data


import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
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

        db.collection("donaciones_especie")
            .document(donationId)
            .update(updates)
            .addOnSuccessListener {
                onComplete(true, null) // Éxito
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error al actualizar: ${e.message}")
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

        // Primero, obtenemos el documento actual para no perder la fecha estimada
        db.collection("donaciones_especie")
            .document(donationId)
            .get()
            .addOnSuccessListener { document ->
                val fechaEstimada = document.getString("fecha_estimada_donacion")
                    ?: document.getString("estimatedDonationDate")
                    ?: "" // Si no existe, ponemos un valor por defecto (vacío)

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
                        onComplete(true, null) // Éxito
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
