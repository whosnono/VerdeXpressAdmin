package com.example.parks.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parks.data.GetParksApproved
import com.example.parks.data.GetParksNew
import com.example.parks.data.ParkData

class ParkViewModel : ViewModel() {

    private val getParksApproved = GetParksApproved()
    private val getParksNew = GetParksNew()

    val parksList = mutableStateOf<List<ParkData>>(emptyList())
    val newParksList = mutableStateOf<List<ParkData>>(emptyList())

    init {
        fetchApprovedParks()
        fetchNewParks()
    }

    fun fetchApprovedParks() {
        getParksApproved.getParks(
            onSuccess = { parks ->
                parksList.value = parks
            },
            onFailure = { exception ->
                println("Error getting approved parks: $exception")
            }
        )
    }

    fun fetchNewParks() {
        getParksNew.getParksN(
            onSuccess = { parks ->
                newParksList.value = parks
            },
            onFailure = { exception ->
                println("Error getting new parks: $exception")
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        getParksApproved.removeListener()
        getParksNew.removeListener()
    }
}