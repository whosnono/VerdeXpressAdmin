package com.example.parks.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GetParks {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    fun getParks(onSuccess: (List<ParkData>) -> Unit, onFailure: (Exception) -> Unit) {
        listenerRegistration = firestore.collection("parques")
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val parks = mutableListOf<ParkData>()
                if (result != null) {
                    for (document in result) {
                        val nombre = document.getString("nombre") ?: "Desconocido"
                        val imagenes = document.get("imagenes") as? List<String> ?: emptyList()
                        val primeraImagen = imagenes.firstOrNull() ?: ""
                        parks.add(ParkData(nombre, primeraImagen))
                    }
                }
                onSuccess(parks)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}

data class ParkData(val nombre: String, val primeraImagen: String)