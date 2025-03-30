package com.example.donations.data


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
            "registro_estado" to "aprobada"
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
    onComplete: (Boolean, String?) -> Unit
) {
    verifyAdminPassword(password) { isValid ->
        if (!isValid) {
            onComplete(false, "Contraseña de administrador incorrecta")
            return@verifyAdminPassword
        }

        val db = FirebaseFirestore.getInstance()
        val updates = hashMapOf<String, Any>(
            "registro_estado" to "rechazada" // Puede ser "Aprobada" o "Rechazada"
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


