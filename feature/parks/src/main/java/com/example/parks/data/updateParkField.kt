package com.example.parks.data

import com.google.firebase.firestore.FirebaseFirestore

fun updateParkField(parkName: String, fieldName: String, newValue: String, onComplete: (Boolean) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("parques")
        .whereEqualTo("nombre", parkName)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]

                // Actualizar el campo específico
                document.reference.update(fieldName, newValue)
                    .addOnSuccessListener {
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        println("Error al actualizar $fieldName: ${e.message}")
                        onComplete(false)
                    }
            } else {
                println("No se encontró el parque")
                onComplete(false)
            }
        }
        .addOnFailureListener { e ->
            println("Error al buscar el parque: ${e.message}")
            onComplete(false)
        }
}