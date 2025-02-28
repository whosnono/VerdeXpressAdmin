package com.example.parks.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

fun saveParkToFirestore(
    name: String,
    location: String,
    description: String,
    status: String,
    needs: List<String>,
    comments: String,
    imageUrl: String?
) {
    val db = Firebase.firestore
    val parkData = hashMapOf(
        "name" to name,
        "location" to location,
        "description" to description,
        "status" to status,
        "imageUrl" to imageUrl,
        "needs" to needs,
        "comments" to comments
    )

    db.collection("parques")
        .add(parkData)
        .addOnSuccessListener {
            // Éxito al guardar los datos
            println("Parque guardado correctamente")
        }
        .addOnFailureListener { e ->
            // Error al guardar los datos
            println("Error al guardar el parque: ${e.message}")
        }
}