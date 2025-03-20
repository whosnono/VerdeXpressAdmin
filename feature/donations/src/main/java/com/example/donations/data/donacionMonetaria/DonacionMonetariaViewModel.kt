package com.example.donations.data.donacionMonetaria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

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

    // Stripe-related fields
    var paymentIntentClientSecret by mutableStateOf<String?>(null)
    var customerConfig by mutableStateOf<PaymentSheet.CustomerConfiguration?>(null)
    var paymentSheetConfig by mutableStateOf<PaymentSheet.Configuration?>(null)
    var paymentStatus by mutableStateOf<PaymentSheetResult?>(null)
    var isPaymentSheetReady by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

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

        // Clear Stripe-related fields
        paymentIntentClientSecret = null
        customerConfig = null
        paymentSheetConfig = null
        paymentStatus = null
        isPaymentSheetReady = false
    }
    // This function would typically call your backend to create a PaymentIntent
    fun preparePaymentSheet() {
        if (cantidad.isEmpty()) return

        isLoading = true
        viewModelScope.launch {
            try {
                // Simulating a backend call to create a payment intent
                val paymentIntentData = fetchPaymentIntent()
                paymentIntentClientSecret = paymentIntentData.first
                val customerId = paymentIntentData.second

                if (customerId != null) {
                    customerConfig = PaymentSheet.CustomerConfiguration(
                        customerId,
                        paymentIntentData.third ?: ""
                    )
                }

                // Configure payment sheet
                paymentSheetConfig = PaymentSheet.Configuration(
                    merchantDisplayName = "VerdExpress Donaciones",
                    customer = customerConfig,
                    allowsDelayedPaymentMethods = true
                )

                isPaymentSheetReady = true
            } catch (e: Exception) {
                // Handle error
                isPaymentSheetReady = false
            } finally {
                isLoading = false
            }
        }
    }

    /*
    // In a real app, this would be a call to your backend that communicates with Stripe API
    private suspend fun fetchPaymentIntent(): Triple<String, String?, String?> = withContext(
        Dispatchers.IO) {
        // This is where you would call your backend endpoint
        // For testing, you can simulate a response

        // This is just for example - in a real app you need a real backend endpoint
        val url = URL("https://yeddyrljnqczjxcqirjh.supabase.co/functions/v1/create-payment-intent")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        // Send the amount and other needed info
        val requestBody = """
            {
                "amount": ${cantidad.toIntOrNull() ?: 0},
                "currency": "mxn",
                "description": "Donación a $parqueSeleccionado",
                "email": "$correo"
            }
        """.trimIndent()

        connection.outputStream.use { os ->
            os.write(requestBody.toByteArray(Charsets.UTF_8))
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(response)

        val clientSecret = jsonObject.getString("clientSecret")
        val customerId = if (jsonObject.has("customerId")) jsonObject.getString("customerId") else null
        val ephemeralKey = if (jsonObject.has("ephemeralKey")) jsonObject.getString("ephemeralKey") else null

        Triple(clientSecret, customerId, ephemeralKey)
    }
    */

    // Simulated backend call
    private suspend fun fetchPaymentIntent(): Triple<String, String?, String?> = withContext(
        Dispatchers.IO) {
        delay(2000) // Simulate network delay

        // Simulated response
        Triple("pi_1J2e3F2eZvKYlo2C5eZvKYlo", "cus_1J2e3F2eZvKYlo2C5eZvKYlo", "ek_test_1J2e3F2eZvKYlo2C5eZvKYlo")
    }


}