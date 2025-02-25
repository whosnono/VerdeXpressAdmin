package com.example.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
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
                .padding(16.dp)
        ) {
            Text(
                text = "Regístrate",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            var nombre by remember { mutableStateOf("") }
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text(
                        text = "Nombre",
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

            Spacer(modifier = Modifier.height(8.dp))

            var apellidos by remember { mutableStateOf("") }
            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = {
                    Text(
                        text = "Apellidos",
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

            Spacer(modifier = Modifier.height(8.dp))

            var numeroContacto by remember { mutableStateOf("") }
            OutlinedTextField(
                value = numeroContacto,
                onValueChange = { numeroContacto = it },
                label = {
                    Text(
                        text = "Número de contacto",
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

            Spacer(modifier = Modifier.height(8.dp))

            var correoElectronico by remember { mutableStateOf("") }
            OutlinedTextField(
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = {
                    Text(
                        text = "Correo electrónico",
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

            Spacer(modifier = Modifier.height(8.dp))

            var password by remember { mutableStateOf("") }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Contraseña",
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

            Spacer(modifier = Modifier.height(8.dp))

            var confirmPassword by remember { mutableStateOf("") }
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = {
                    Text(
                        text = "Confirmar contraseña",
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("Home") },
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 100.dp)
                    .width(175.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                shape = roundedShape
            ) {
                Text(
                    text = "Regístrarse",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
                TextButton(onClick = { navController.navigate("signIn") }) {
                    Text(
                        text = "Iniciar sesión",
                        color = verdeBoton,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                }
            }
        }
    }
}