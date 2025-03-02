package com.example.parks.data

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun saveParkToFirestore(
    name: String,
    location: String,
    description: String,
    status: String,
    needs: List<String>,
    comments: String,
    imageUrls: List<String>,
    latitude: String? = null,
    longitude: String? = null
) {
    val db = Firebase.firestore
    val parkData = hashMapOf(
        "nombre" to name,
        "ubicacion" to location,
        "descripcion" to description,
        "estado_actual" to status,
        "imagenes" to imageUrls,
        "necesidades" to needs,
        "comentarios" to comments
    )

    // Añadir las coordenadas si están disponibles
    if (latitude != null && longitude != null) {
        parkData["latitud"] = latitude
        parkData["longitud"] = longitude
    }

    db.collection("parques")
        .add(parkData)
        .addOnSuccessListener {
            // Éxito al guardar los datos
            Log.d("Firestore", "Parque guardado correctamente con id: ${it.id}")
        }
        .addOnFailureListener { e ->
            // Error al guardar los datos
            Log.e("Firestore", "Error al guardar el parque: ${e.message}")
        }
}