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
import com.example.design.R
import com.example.design.SecondaryAppBar

@Composable
fun SignUpScreen(navController: NavController) {
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

            // Variables de estado para los campos y errores
            var nombre by remember { mutableStateOf("") }
            var apellidos by remember { mutableStateOf("") }
            var numeroContacto by remember { mutableStateOf("") }
            var correoElectronico by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }

            var nombreError by remember { mutableStateOf(false) }
            var apellidosError by remember { mutableStateOf(false) }
            var numeroContactoError by remember { mutableStateOf(false) }
            var correoElectronicoError by remember { mutableStateOf(false) }
            var passwordError by remember { mutableStateOf(false) }
            var confirmPasswordError by remember { mutableStateOf(false) }

            var nombreErrorMessage by remember { mutableStateOf("") }
            var apellidosErrorMessage by remember { mutableStateOf("") }
            var numeroContactoErrorMessage by remember { mutableStateOf("") }
            var correoElectronicoErrorMessage by remember { mutableStateOf("") }
            var passwordErrorMessage by remember { mutableStateOf("") }
            var confirmPasswordErrorMessage by remember { mutableStateOf("") }

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                    nombreErrorMessage = ""
                },
                label = {
                    Row {
                        Text(
                            text = "Nombre",
                            color = if (nombreError) rojoError else Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        if (nombreError) {
                            Text(text = "*", color = rojoError) //Un * para resaltar donde hay un error
                        }
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors( //Cambio de color del field si el usuario se equivoca
                    focusedBorderColor = if (nombreError) rojoError else verdeBoton,
                    focusedLabelColor = if (nombreError) rojoError else verdeBoton,
                    cursorColor = if (nombreError) rojoError else verdeBoton
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError) {
                Text(text = nombreErrorMessage, color = rojoError, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Apellidos
            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    apellidos = it
                    apellidosError = false
                    apellidosErrorMessage = ""
                },
                label = {
                    Row {
                        Text(
                            text = "Apellidos",
                            color = if (apellidosError) rojoError else Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        if (apellidosError) {
                            Text(text = "*", color = rojoError) //Un * para resaltar donde hay un error
                        }
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors( //Cambio de color del field si el usuario se equivoca
                    focusedBorderColor = if (apellidosError) rojoError else verdeBoton,
                    focusedLabelColor = if (apellidosError) rojoError else verdeBoton,
                    cursorColor = if (apellidosError) rojoError else verdeBoton
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (apellidosError) {
                Text(text = apellidosErrorMessage, color = rojoError, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Número de contacto
            OutlinedTextField(
                value = numeroContacto,
                onValueChange = {
                    numeroContacto = it
                    numeroContactoError = false
                    numeroContactoErrorMessage = ""
                },
                label = {
                    Row {
                        Text(
                            text = "Número de contacto",
                            color = if (numeroContactoError) rojoError else Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        if (numeroContactoError) {
                            Text(text = "*", color = rojoError) //Un * para resaltar donde hay un error
                        }
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors( //Cambio de color del field si el usuario se equivoca
                    focusedBorderColor = if (numeroContactoError) rojoError else verdeBoton,
                    focusedLabelColor = if (numeroContactoError) rojoError else verdeBoton,
                    cursorColor = if (numeroContactoError) rojoError else verdeBoton
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (numeroContactoError) {
                Text(text = numeroContactoErrorMessage, color = rojoError, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Correo electrónico
            OutlinedTextField(
                value = correoElectronico,
                onValueChange = {
                    correoElectronico = it
                    correoElectronicoError = false
                    correoElectronicoErrorMessage = ""
                },
                label = {
                    Row {
                        Text(
                            text = "Correo electrónico",
                            color = if (correoElectronicoError) rojoError else Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        if (correoElectronicoError) {
                            Text(text = "*", color = rojoError) //Un * para resaltar donde hay un error
                        }
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors( //Cambio de color del field si el usuario se equivoca
                    focusedBorderColor = if (correoElectronicoError) rojoError else verdeBoton,
                    focusedLabelColor = if (correoElectronicoError) rojoError else verdeBoton,
                    cursorColor = if (correoElectronicoError) rojoError else verdeBoton
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (correoElectronicoError) {
                Text(text = correoElectronicoErrorMessage, color = rojoError, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                    passwordErrorMessage = ""
                },
                label = {
                    Row {
                        Text(
                            text = "Contraseña",
                            color = if (passwordError) rojoError else Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        if (passwordError) {
                            Text(text = "*", color = rojoError) //Un * para resaltar donde hay un error
                        }
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors( //Cambio de color del field si el usuario se equivoca
                    focusedBorderColor = if (passwordError) rojoError else verdeBoton,
                    focusedLabelColor = if (passwordError) rojoError else verdeBoton,
                    cursorColor = if (passwordError) rojoError else verdeBoton
                ),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (passwordError) {
                Text(text = passwordErrorMessage, color = rojoError, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = false
                    confirmPasswordErrorMessage = ""
                },
                label = {
                    Row {
                        Text(
                            text = "Confirmar contraseña",
                            color = if (confirmPasswordError) rojoError else Color.Gray,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        if (confirmPasswordError) {
                            Text(text = "*", color = rojoError) //Un * para resaltar donde hay un error
                        }
                    }
                },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors( //Cambio de color del field si el usuario se equivoca
                    focusedBorderColor = if (confirmPasswordError) rojoError else verdeBoton,
                    focusedLabelColor = if (confirmPasswordError) rojoError else verdeBoton,
                    cursorColor = if (confirmPasswordError) rojoError else verdeBoton
                ),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (confirmPasswordError) {
                Text(text = confirmPasswordErrorMessage, color = rojoError, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de registro
            Button(
                onClick = {
                    // Validación de campos (Cuando se encuentran vacios todos)
                    nombreError = nombre.isEmpty().also { if (it) nombreErrorMessage = "Ingrese su nombre" }
                    apellidosError = apellidos.isEmpty().also { if (it) apellidosErrorMessage = "Ingrese al menos un apellido" }
                    numeroContactoError = numeroContacto.isEmpty().also { if (it) numeroContactoErrorMessage = "Ingrese su número de contacto" }
                    correoElectronicoError = correoElectronico.isEmpty().also { if (it) correoElectronicoErrorMessage = "Ingrese su correo electrónico" }
                    passwordError = password.isEmpty().also { if (it) passwordErrorMessage = "Ingrese su contraseña" }
                    confirmPasswordError = confirmPassword.isEmpty().also { if (it) confirmPasswordErrorMessage = "Confirme su contraseña" }

                    if (!nombreError && !apellidosError && !numeroContactoError && !correoElectronicoError && !passwordError && !confirmPasswordError) {
                        if (!correoElectronico.contains("@") || (!correoElectronico.contains(".com") && !correoElectronico.contains(".es"))) { //Que tenga los parámetros correctos de un correo electronico
                            correoElectronicoError = true
                            correoElectronicoErrorMessage = "Ingrese un correo válido"
                        } else if (password.length < 8 || !password.any { it.isDigit() } || !password.any { it.isUpperCase() }) { //La contraseña debe ser mayor a 8 caracteres y debe tener al menos un numero y una mayúscula
                            passwordError = true
                            passwordErrorMessage = "La contraseña debe tener al menos 8 caracteres, 1 número y 1 mayúscula"
                        } else if (password != confirmPassword) { //Que la contraseña en la confirmación sea la misma
                            confirmPasswordError = true
                            confirmPasswordErrorMessage = "Las contraseñas no coinciden"
                        } else if (numeroContacto.toIntOrNull() == null) { // Verifica que sean solo números en el field de contacto
                            numeroContactoError = true
                            numeroContactoErrorMessage = "Ingrese solo números"
                        } else {
                            navController.navigate("signUpSuccess")
                        }
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

