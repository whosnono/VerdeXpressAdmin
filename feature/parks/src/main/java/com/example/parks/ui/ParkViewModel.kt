package com.example.parks.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parks.data.GetParks
import com.example.parks.data.ParkData

class ParkViewModel : ViewModel() {

    private val getParks = GetParks()
    val parksList = mutableStateOf<List<ParkData>>(emptyList())

    fun fetchParks() {
        getParks.getParks(
            onSuccess = { parks ->
                parksList.value = parks
            },
            onFailure = { exception ->
                // Manejar el error, por ejemplo, mostrar un mensaje al usuario
                println("Error getting documents: $exception")
            }
        )
    }
}