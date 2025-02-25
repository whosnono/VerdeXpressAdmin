package com.example.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    Column( //Aquí es para que todos los elementos se encuentren en una sola fila
        modifier = modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {  },
                modifier = Modifier
                    .padding(0.dp) // Quita el espacio que puse para centrar los elementos de la ventana y puedo colocarlo en la esquina
                    .offset(x = (-8).dp) // La posición exacta de la flecha
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "VerdeXpress",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78B153)
            )
        }

        Spacer(modifier = Modifier.height(24.dp)) //Estos son solo para hacer espacio entre las cosas

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Regístrate",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f)) // Spacer para alinear a la izquierda
        }

        Spacer(modifier = Modifier.height(20.dp))

        var nombre by remember { mutableStateOf("") } //textfield de nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        var apellidos by remember { mutableStateOf("") } //textfield de appellidos
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text(text = "Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        var numeroContacto by remember { mutableStateOf("") }
        OutlinedTextField(
            value = numeroContacto,
            onValueChange = { numeroContacto = it },
            label = { Text(text = "Número de contacto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        var correoElectronico by remember { mutableStateOf("") }
        OutlinedTextField(
            value = correoElectronico,
            onValueChange = { correoElectronico = it },
            label = { Text(text = "Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        var confirmPassword by remember { mutableStateOf("") }
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(45.dp))

        Button(
            onClick = {  },
            modifier = Modifier.widthIn(max = 250.dp).align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78B153))
        ) {
            Text("Regístrarse", color = Color.White,
                fontSize = 17.sp)
        }

        Spacer(modifier = Modifier.height(1.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "¿Ya tienes cuenta?", fontSize = 14.sp)
            TextButton(onClick = {  }) {
                Text(text = "Iniciar sesión", color = Color(0xFF78B153),
                    fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen()
}