package com.example.donations.data.donacionMonetaria

import androidx.lifecycle.ViewModel

class DonacionMonetariaViewModel : ViewModel() {
    var nombre: String = ""
    var correo: String = ""
    var numTel: String = ""
    var cantidad: String = ""
    var metodoPago: String = ""
    var parqueSeleccionado: String = ""
    var ubicacionSeleccionado: String = ""
    var quiereRecibo: Boolean? = null
    var rfc: String = ""
    var razon: String = ""
    var domFiscal: String = ""

    fun clear() {
        nombre = ""
        correo = ""
        numTel = ""
        cantidad = ""
        metodoPago = ""
        parqueSeleccionado = ""
        ubicacionSeleccionado = ""
        quiereRecibo = null
        rfc = ""
        razon = ""
        domFiscal = ""
    }
}