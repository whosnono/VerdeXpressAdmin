package com.example.donations.data

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun rememberUserFullName(userId: String): String {
    var userFullName by remember { mutableStateOf("Usuario desconocido") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre") ?: ""
                    val apellido = document.getString("apellidos") ?: ""
                    userFullName = if (nombre.isNotEmpty() && apellido.isNotEmpty()) {
                        "$nombre $apellido"
                    } else if (nombre.isNotEmpty()) {
                        nombre
                    } else {
                        "Usuario anónimo"
                    }
                }
                .addOnFailureListener {
                    userFullName = "Error al cargar usuario"
                }
        }
    }
    return userFullName
}

fun getDonacionesEspecieFromFirebase(onSuccess: (List<DonationsEData>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("donaciones_especie").get()
        .addOnSuccessListener { result ->
            val donacionesList = result.map { document ->
                val fechaEstimada = document.getString("fecha_estimada_donacion")
                    ?: document.getString("estimatedDonationDate")

                val fechaConvertida = fechaEstimada?.let { fechaStr ->
                    try {
                        val partes = fechaStr.split("/")
                        if (partes.size == 3) {
                            "${partes[2]}-${partes[1]}-${partes[0]}" // Formato "yyyy-MM-dd"
                        } else {
                            fechaStr // Mantener el original si el formato no es válido
                        }
                    } catch (e: Exception) {
                        Log.e("FechaParse", "Error al convertir la fecha: $fechaStr", e)
                        fechaStr // Mantener el original en caso de error
                    }
                }

                DonationsEData(
                    id = document.id,
                    parqueDonado = document.getString("parque_donado") ?: "",
                    fecha = fechaConvertida ?: "",
                    ubicacion = document.getString("ubicacion") ?: "",
                    imagenes = document.get("imagenes") as? List<String> ?: emptyList(),
                    donanteNombre = document.getString("donante_nombre") ?: "",
                    donanteContacto = document.getString("donante_contacto") ?: "",
                    registroEstado = document.getString("registro_estado") ?: "",
                    cantidad = document.getString("cantidad") ?: "",
                    recurso = document.getString("recurso") ?: "",
                    condicion = document.getString("condicion") ?: ""
                )
            }
            onSuccess(donacionesList)
        }
        .addOnFailureListener { exception ->
            Log.e("DonacionesEspecie", "Error al obtener donaciones de Firebase", exception)
            onSuccess(emptyList())
        }
}

data class DonationsEData(
    val id: String,
    val cantidad: String,
    val condicion: String,
    val parqueDonado: String,
    val imagenes: List<String>,
    val fecha: String,
    val ubicacion: String,
    val donanteNombre: String,
    val donanteContacto: String,
    val registroEstado: String,
    val recurso: String
)