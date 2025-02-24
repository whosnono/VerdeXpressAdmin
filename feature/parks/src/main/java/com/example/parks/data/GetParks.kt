package com.example.parks.data

import com.google.firebase.firestore.FirebaseFirestore

class GetParks {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getParks(onSuccess: (List<ParkData>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("parques")
            .get()
            .addOnSuccessListener { result ->
                val parks = mutableListOf<ParkData>()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: "Desconocido"
                    val imagenes = document.get("imagenes") as? List<String> ?: emptyList()
                    val primeraImagen = imagenes.firstOrNull() ?: ""
                    parks.add(ParkData(nombre, primeraImagen))
                }
                onSuccess(parks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

data class ParkData(val nombre: String, val primeraImagen: String)