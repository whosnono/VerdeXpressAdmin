package com.example.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    Column {
        SecondaryAppBar(
            showIcon = true,
            onIconClick = {
                navController.navigate("signIn")
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Restablecer contraseña",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Se enviarán las instrucciones para restablecer su contraseña a su correo electrónico.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_regular)),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            var correoElectronico by remember { mutableStateOf("") }
            OutlinedTextField(
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = {
                    Text(
                        text = "Correo electrónico",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeBoton,
                    focusedLabelColor = verdeBoton,
                    cursorColor = verdeBoton
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Acción al hacer clic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                shape = roundedShape
            ) {
                Text(
                    text = "Enviar",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }
        }
    }
}