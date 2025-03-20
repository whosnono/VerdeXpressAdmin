package com.example.donations.ui.donacionMonetaria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.donacionMonetaria.DonacionMonetariaViewModel
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun MetodoPagoTarjetaScreen(navController: NavController, viewModel: DonacionMonetariaViewModel) {
    // Get payment data from viewModel
    val nombre = viewModel.nombre
    val correo = viewModel.correo
    val cantidad = viewModel.cantidad
    val parqueSeleccionado = viewModel.parqueSeleccionado

    // Stripe Payment Sheet
    val context = LocalContext.current
    val isPaymentSheetReady = viewModel.isPaymentSheetReady
    val isLoading = viewModel.isLoading
    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                // Payment complete
                viewModel.paymentStatus = result
                // Navigate to success screen or show success message
                //navController.navigate("")
            }
            is PaymentSheetResult.Canceled -> {
                // Payment canceled
                viewModel.paymentStatus = result
            }
            is PaymentSheetResult.Failed -> {
                // Payment failed
                viewModel.paymentStatus = result
                // Show error message
            }
        }
    }

    // Colors and styles
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    // Effect to prepare Payment Sheet when entering this screen
    LaunchedEffect(Unit) {
        if (viewModel.paymentIntentClientSecret == null) {
            viewModel.preparePaymentSheet()
        }
    }

    // Effect to present Payment Sheet when it's ready
    LaunchedEffect(isPaymentSheetReady) {
        if (isPaymentSheetReady && viewModel.paymentIntentClientSecret != null) {
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = viewModel.paymentIntentClientSecret!!,
                configuration = viewModel.paymentSheetConfig
            )
        }
    }

    // Screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.popBackStack()
        })

        Spacer(modifier = Modifier.height(16.dp))

        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Detalles de pago",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Donation summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = roundedShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Resumen de la donación",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                    Divider()
                    Text(text = "Nombre: $nombre")
                    Text(text = "Correo: $correo")
                    Text(text = "Parque: $parqueSeleccionado")
                    Text(
                        text = "Monto: $${cantidad} MXN",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show loading or payment button
            if (isLoading) {
                CircularProgressIndicator(color = verdeBoton)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Preparando pago...")
            } else {
                when (val result = viewModel.paymentStatus) {
                    is PaymentSheetResult.Completed -> {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                            contentDescription = "Pago completado",
                            tint = verdeBoton,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "¡Pago completado!",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                    }
                    is PaymentSheetResult.Failed -> {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                            contentDescription = "Error en el pago",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Error en el pago: ${result.error.localizedMessage}",
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.preparePaymentSheet() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                            shape = roundedShape
                        ) {
                            Text(
                                text = "Reintentar",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                            )
                        }
                    }
                    is PaymentSheetResult.Canceled -> {
                        Button(
                            onClick = {
                                if (viewModel.paymentIntentClientSecret != null) {
                                    paymentSheet.presentWithPaymentIntent(
                                        paymentIntentClientSecret = viewModel.paymentIntentClientSecret!!,
                                        configuration = viewModel.paymentSheetConfig
                                    )
                                } else {
                                    viewModel.preparePaymentSheet()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                            shape = roundedShape
                        ) {
                            Text(
                                text = "Continuar con el pago",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                            )
                        }
                    }
                    null -> {
                        if (isPaymentSheetReady) {
                            Button(
                                onClick = {
                                    paymentSheet.presentWithPaymentIntent(
                                        paymentIntentClientSecret = viewModel.paymentIntentClientSecret!!,
                                        configuration = viewModel.paymentSheetConfig
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                shape = roundedShape
                            ) {
                                Text(
                                    text = "Pagar ahora",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.preparePaymentSheet() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                shape = roundedShape
                            ) {
                                Text(
                                    text = "Preparar pago",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}