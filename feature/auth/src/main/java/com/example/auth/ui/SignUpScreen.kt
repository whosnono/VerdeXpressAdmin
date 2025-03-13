package com.example.auth.ui

import SignUpViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.auth.data.SignUpValidator
import com.example.design.R
import com.example.design.SecondaryAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    signUpValidator: SignUpValidator,
    signUpViewModel: SignUpViewModel = viewModel()
) {
    val verdeBoton = Color(0xFF78B153)
    val rojoError = Color.Red
    val roundedShape = RoundedCornerShape(12.dp)

    // Estados del ViewModel
    val signUpState by signUpViewModel.signUpState.collectAsState()

    // Estados locales de los campos
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var numeroContacto by remember { mutableStateOf("") }
    var correoElectronico by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados de errores
    var errorMessages by remember { mutableStateOf(mapOf<String, List<String>>()) }

    // Efecto para manejar estados de registro
    LaunchedEffect(signUpState) {
        when (val state = signUpState) {
            is SignUpState.Success -> {
                navController.navigate("signUpSuccess")
            }
            is SignUpState.Error -> {
                // Actualizar errores con el mensaje de Firebase
                errorMessages = errorMessages + ("general" to listOf(state.message))
            }
            else -> {} // Otros estados
        }
    }

    Scaffold(
        topBar = {
            SecondaryAppBar(
                showIcon = true,
                onIconClick = { navController.navigate("signIn") }
            )
        },
        containerColor = Color.White // Set the background color of the Scaffold
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White),
        ) {
            Text(
                text = "Regístrate",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar error general de Firebase si existe
            errorMessages["general"]?.let { generalErrors ->
                generalErrors.forEach { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = rojoError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }

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
                    // Limpiar errores generales
                    errorMessages = errorMessages - "general"

                    // Validar campos localmente
                    val localErrors = signUpValidator.validate(
                        nombre,
                        apellidos,
                        numeroContacto,
                        correoElectronico,
                        password,
                        confirmPassword
                    )

                    // Actualizar errores locales
                    errorMessages = localErrors

                    // Si no hay errores locales, intentar registro
                    if (localErrors.isEmpty()) {
                        signUpViewModel.signUp(
                            nombre,
                            apellidos,
                            numeroContacto,
                            correoElectronico,
                            password
                        )
                    }
                },
                modifier = Modifier
                    .width(175.dp)
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                shape = roundedShape,
                enabled = signUpState !is SignUpState.Loading
            ) {
                if (signUpState is SignUpState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Registrarse",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                }
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
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType
            )
        )

        // Mostrar errores debajo del campo de texto
        errors.forEach { error ->
            Text(
                text = error,
                color = rojoError,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}