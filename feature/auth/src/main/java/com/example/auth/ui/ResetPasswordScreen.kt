package com.example.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.util.Log
import com.google.firebase.firestore.ktx.firestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)
    val context = LocalContext.current
    val auth: FirebaseAuth = Firebase.auth

    Column {
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.navigate("signIn")
        })

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
                onClick = {
                    if (correoElectronico.isNotEmpty()) {
                        val usersCollection = Firebase.firestore.collection("usuarios") // Asegúrate de usar la colección correcta

                        usersCollection.whereEqualTo("correoElectronico", correoElectronico)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    // El correo está registrado en tu app, ahora sí enviamos el email de recuperación
                                    auth.sendPasswordResetEmail(correoElectronico)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Correo de restablecimiento enviado.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("resetPasswordEmailSent")
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("ResetPassword", "Error: ${exception.javaClass.simpleName} - ${exception.message}")

                                            Toast.makeText(
                                                context,
                                                "Error al enviar el correo de restablecimiento.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    // El correo no existe en nuestra app
                                    Toast.makeText(
                                        context,
                                        "No existe una cuenta con este correo electrónico en nuestra app.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error al verificar la cuenta.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, ingrese su correo electrónico.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ,
                modifier = Modifier
                    .width(175.dp)
                    .height(50.dp),
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