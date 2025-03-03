package com.example.parks.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parks.data.GetParks
import com.example.parks.data.ParkData

class ParkViewModel : ViewModel() {

    private val getParks = GetParks()
    val parksList = mutableStateOf<List<ParkData>>(emptyList())

    init {
        fetchParks()
    }

    fun fetchParks() {
        getParks.getParks(
            onSuccess = { parks ->
                parksList.value = parks
            },
            onFailure = { exception ->
                println("Error getting documents: $exception")
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        getParks.removeListener()
    }
}