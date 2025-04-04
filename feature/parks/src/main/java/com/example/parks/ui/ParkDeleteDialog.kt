package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.parks.data.deletePark

@Composable
fun ParkDeleteDialog(
    parkName: String,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmationText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(verde)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Eliminar parque",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = SFProDisplayBold,
                                color = Color.White
                            )
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar el parque \"$parkName\"? Esta acción no puede deshacerse.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Campo de confirmación de texto
                    Text(
                        text = "Escriba \"Eliminar\" para confirmar:",
                        color = Color.Black,
                        fontFamily = SFProDisplayBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    BasicTextField(
                        value = confirmationText,
                        onValueChange = { confirmationText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de contraseña
                    Text(
                        text = "Ingrese su contraseña de administrador:",
                        color = Color.Black,
                        fontFamily = SFProDisplayBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    BasicTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        singleLine = true
                    )

                    // Mensaje de error
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Botones
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                if (confirmationText.lowercase() != "eliminar") {
                                    errorMessage = "Debe escribir \"Eliminar\" para confirmar"
                                    return@Button
                                }

                                if (password.isEmpty()) {
                                    errorMessage = "Debe ingresar su contraseña"
                                    return@Button
                                }

                                isDeleting = true
                                errorMessage = null

                                deletePark(
                                    parkName = parkName,
                                    confirmationText = confirmationText,
                                    password = password
                                ) { success, message ->
                                    isDeleting = false
                                    if (success) {
                                        onDelete()
                                    } else {
                                        errorMessage = message ?: "Error desconocido"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = verde
                            ),
                            shape = RoundedCornerShape(5.dp),
                            enabled = !isDeleting
                        ) {
                            if (isDeleting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}