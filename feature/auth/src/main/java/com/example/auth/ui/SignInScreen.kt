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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

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
            onValueChange = { usuario = it },
            label = { Text(text = "Correo electrónico", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))) },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeBoton, focusedLabelColor = verdeBoton, cursorColor = verdeBoton),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)) )},
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeBoton, focusedLabelColor = verdeBoton, cursorColor = verdeBoton),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
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
        Button(
            onClick = {
                scope.launch {
                    try {
                        auth.signInWithEmailAndPassword(usuario, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("Inicio")
                                } else {
                                    errorMessage = task.exception?.message ?: "Error al iniciar sesión"
                                }
                            }
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error al iniciar sesión"
                    }
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