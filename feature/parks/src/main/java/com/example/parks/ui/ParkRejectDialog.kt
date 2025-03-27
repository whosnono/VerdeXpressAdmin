package com.example.parks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.design.R

@Composable
fun ParkRejectDialog(
    parkName: String,
    onReject: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var rejectionReason by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                            text = "Rechazar parque",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = SFProDisplayBold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
                    Text(
                        text = "¿Desea rechazar el parque \"$parkName\"?",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Razon",
                        color = Color.Black,
                        fontFamily = SFProDisplayBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    // Rejection Reason Input
                    Box(modifier = Modifier.fillMaxWidth()) {
                        BasicTextField(
                            value = rejectionReason,
                            onValueChange = { rejectionReason = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            decorationBox = { innerTextField ->
                                if (rejectionReason.isEmpty()) {
                                    Text(
                                        text = "Razón del rechazo",
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            },
                            maxLines = 4
                        )
                    }

                    // Password Input
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ingrese su contraseña",
                        color = Color.Black,
                        fontFamily = SFProDisplayBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        BasicTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 48.dp)
                                .padding(12.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (password.isEmpty()) {
                                        Text(
                                            text = "Ingrese su contraseña",
                                            color = Color.Gray
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            singleLine = true
                        )

                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility_on),
                            contentDescription = "Toggle password visibility",
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .clickable { passwordVisible = !passwordVisible },
                            tint = Color.Gray
                        )
                    }

                    // Error Message
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Reject Button
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            when {
                                rejectionReason.isEmpty() -> {
                                    errorMessage = "Por favor ingrese una razón"
                                }
                                password.isEmpty() -> {
                                    errorMessage = "Por favor ingrese su contraseña"
                                }
                                else -> {
                                    onReject(rejectionReason, password)
                                }
                            }
                        },
                        modifier = Modifier
                            .width(175.dp)
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verde
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Aceptar",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = SFProDisplayBold
                        )
                    }
                }
            }
        }
    }
}