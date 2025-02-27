package com.example.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun SignInScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    val rojoError = Color.Red
    val roundedShape = RoundedCornerShape(12.dp)

    //Estos los cree para la lógica de detectar errores al tratar de ingresar a la app :)
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usuarioError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var usuarioErrorMessage by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo y nombre de la app
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo VerdeXpress",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = "VerdeXpress",
            fontSize = 30.sp,
            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
            color = verdeBoton,
            modifier = Modifier.padding(bottom = 42.dp)
        )

        // Login
        Text(
            text = "Iniciar sesión",
            fontSize = 25.sp,
            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Username field
        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                usuarioError = false
                usuarioErrorMessage = ""
            },
            label = {
                Row {
                    Text(
                        text = "Usuario",
                        fontSize = 14.sp,
                        color = if (usuarioError) rojoError else Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                    if (usuarioError) {
                        Text(
                            text = "*",
                            color = rojoError,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors( //Esto es para cambiar el color del borde a rojo si hay un error en los datos
                focusedBorderColor = if (usuarioError) rojoError else verdeBoton,
                focusedLabelColor = if (usuarioError) rojoError else verdeBoton,
                cursorColor = if (usuarioError) rojoError else verdeBoton
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (usuarioError) 4.dp else 4.dp)
        )
        if (usuarioError) {
            Text(
                text = usuarioErrorMessage,
                color = rojoError,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Password field
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
                        fontSize = 14.sp,
                        color = if (passwordError) rojoError else Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                    if (passwordError) {
                        Text(
                            text = "*",
                            color = rojoError,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors( //Esto es para cambiar el color del borde a rojo si hay un error en los datos
                focusedBorderColor = if (passwordError) rojoError else verdeBoton,
                focusedLabelColor = if (passwordError) rojoError else verdeBoton,
                cursorColor = if (passwordError) rojoError else verdeBoton
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (passwordError) {
            Text(
                text = passwordErrorMessage,
                color = rojoError,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextButton(
                onClick = { navController.navigate("resetPassword") },
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = "Recuperar contraseña",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp,
                    color = verdeBoton,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button( //Aquí solo es la lógica que va a seguir para identificar errores, si el correo no es válido, si dejaron un campo vacio, etc.
            onClick = {
                if (usuario.isEmpty()) {
                    usuarioError = true
                    usuarioErrorMessage = "Ingresa tu correo"
                } else if (!usuario.contains("@") || (!usuario.contains(".com") && !usuario.contains(".es"))) {
                    usuarioError = true
                    usuarioErrorMessage = "Ingresa un correo válido"
                } else {
                    usuarioError = false
                    usuarioErrorMessage = ""
                }

                if (password.isEmpty()) {
                    passwordError = true
                    passwordErrorMessage = "Ingresa tu contraseña"
                } else {
                    passwordError = false
                    passwordErrorMessage = ""
                }

                if (!usuarioError && !passwordError) {
                    navController.navigate("Inicio")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
            shape = roundedShape
        ) {
            Text(
                text = "Ingresar",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
            )
        }

        // Registration link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta?",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
            )
            TextButton(
                onClick = { navController.navigate("signUp") },
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = "Regístrate",
                    textDecoration = TextDecoration.Underline,
                    color = verdeBoton,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    fontSize = 14.sp
                )
            }
        }
    }
}