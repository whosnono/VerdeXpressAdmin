package com.example.donations.ui.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.design.R

@Composable
fun DonationTypeDialog(navController: NavController, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(348.dp)
                .height(400.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 18.dp)),
            contentAlignment = Alignment.Center

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Centra los elementos horizontalmente
                verticalArrangement = Arrangement.Center, // Centra los elementos verticalmente
                modifier = Modifier.fillMaxSize() // Asegura que la columna ocupe todo el espacio disponible
            ) {

                Text(
                    text = "¿Cómo deseas donar?",
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    color = Color(0xFF3F4946)
                )

                Spacer(modifier = Modifier.height(35.dp))

                CustomButton(text = "Monetaria", onClick = {
                    navController.popBackStack("Donaciones", inclusive = false)
                    navController.navigate("donacionMonetaria")
                })

                Spacer(modifier = Modifier.height(20.dp))

                CustomButton(text = "En especie", onClick = {
                    navController.popBackStack("Donaciones", inclusive = false)
                    navController.navigate("donacionEspecie")
                })

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = { navController.navigate("Donaciones") },
                ) {
                    Text(
                        text = "En otra ocasión",
                        textDecoration = TextDecoration.Underline,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_medium)),
                        color = Color(0xFF484C52),
                    )
                }


            }
        }
    }
}

@Composable
fun CustomButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(251.dp)
            .height(58.dp)
            .background(color = Color(0xFF77B254), shape = RoundedCornerShape(size = 10.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF77B254), contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
            textAlign = TextAlign.Center
        )
    }
}
