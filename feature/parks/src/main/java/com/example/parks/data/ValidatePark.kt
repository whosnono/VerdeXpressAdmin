package com.example.parks.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
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

                querySnapshot.documents.first().reference.update(updates)
                    .addOnSuccessListener { onComplete(true, null) }
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

                querySnapshot.documents.first().reference.update(updates)
                    .addOnSuccessListener { onComplete(true, null) }
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