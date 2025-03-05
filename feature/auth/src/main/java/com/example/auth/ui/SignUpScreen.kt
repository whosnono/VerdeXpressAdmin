package com.example.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.auth.data.SignUpValidator
import com.example.design.R
import com.example.design.SecondaryAppBar

@Composable
fun SignUpScreen(
    navController: NavController,
    signUpValidator: SignUpValidator
) {
    val verdeBoton = Color(0xFF78B153)
    val rojoError = Color.Red
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

            // Estados de los campos
            var nombre by remember { mutableStateOf("") }
            var apellidos by remember { mutableStateOf("") }
            var numeroContacto by remember { mutableStateOf("") }
            var correoElectronico by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }

            // Estados de errores
            var errorMessages by remember { mutableStateOf(mapOf<String, List<String>>()) }

            // Campos de texto
            SignUpTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre",
                errors = errorMessages["nombre"] ?: emptyList(),
                roundedShape = roundedShape,
                verdeBoton = verdeBoton,
                rojoError = rojoError
            )
            Spacer(modifier = Modifier.height(8.dp))

            SignUpTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = "Apellidos",
                errors = errorMessages["apellidos"] ?: emptyList(),
                roundedShape = roundedShape,
                verdeBoton = verdeBoton,
                rojoError = rojoError
            )
            Spacer(modifier = Modifier.height(8.dp))

            SignUpTextField(
                value = numeroContacto,
                onValueChange = { numeroContacto = it },
                label = "Número de contacto",
                errors = errorMessages["numeroContacto"] ?: emptyList(),
                roundedShape = roundedShape,
                verdeBoton = verdeBoton,
                rojoError = rojoError,
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(8.dp))

            SignUpTextField(
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = "Correo electrónico",
                errors = errorMessages["correoElectronico"] ?: emptyList(),
                roundedShape = roundedShape,
                verdeBoton = verdeBoton,
                rojoError = rojoError,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(8.dp))

            SignUpTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                errors = errorMessages["password"] ?: emptyList(),
                roundedShape = roundedShape,
                verdeBoton = verdeBoton,
                rojoError = rojoError,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            SignUpTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar contraseña",
                errors = errorMessages["confirmPassword"] ?: emptyList(),
                roundedShape = roundedShape,
                verdeBoton = verdeBoton,
                rojoError = rojoError,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de registro
            Button(
                onClick = {
                    // Validar campos
                    errorMessages = signUpValidator.validate(
                        nombre,
                        apellidos,
                        numeroContacto,
                        correoElectronico,
                        password,
                        confirmPassword
                    )

                    // Si no hay errores, navegar
                    if (errorMessages.isEmpty()) {
                        navController.navigate("signUpSuccess")
                    }
                },
                modifier = Modifier
                    .padding(start = 100.dp)
                    .width(175.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                shape = roundedShape
            ) {
                Text(
                    text = "Registrarse",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }

            // Enlace a inicio de sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                )
                TextButton(
                    onClick = { navController.navigate("signIn") },
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        textDecoration = TextDecoration.Underline,
                        color = verdeBoton,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                }
            }
        }
    }
}

// Componente reutilizable para campos de texto
@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errors: List<String>,
    roundedShape: RoundedCornerShape,
    verdeBoton: Color,
    rojoError: Color,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Row {
                    Text(
                        text = label,
                        color = if (errors.isNotEmpty()) rojoError else Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                    if (errors.isNotEmpty()) {
                        Text(text = "*", color = rojoError)
                    }
                }
            },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errors.isNotEmpty()) rojoError else verdeBoton,
                focusedLabelColor = if (errors.isNotEmpty()) rojoError else verdeBoton,
                cursorColor = if (errors.isNotEmpty()) rojoError else verdeBoton
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )

        // Mostrar mensajes de error
        errors.forEach { errorMessage ->
            Text(
                text = errorMessage,
                color = rojoError,
                fontSize = 12.sp
            )
        }
    }
}