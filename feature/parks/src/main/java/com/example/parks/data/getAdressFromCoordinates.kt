package com.example.parks.data

import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String {
    try {
        val geocoder = Geocoder(context, Locale("es", "MX"))
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressParts = mutableListOf<String>()

            // Construir la dirección de manera más completa
            address.thoroughfare?.let { addressParts.add(it) }
            address.subLocality?.let { addressParts.add(it) }
            address.locality?.let { addressParts.add(it) }
            address.adminArea?.let { addressParts.add(it) }
            address.postalCode?.let { addressParts.add(it) }

            return addressParts.joinToString(", ")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return "Dirección no disponible"
}
