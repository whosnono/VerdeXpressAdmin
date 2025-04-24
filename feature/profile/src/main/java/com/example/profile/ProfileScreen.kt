package com.example.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfileScreen(navController: NavController) {
    Column {
        MainAppBar()
        val auth = FirebaseAuth.getInstance()

        // Botón de Cerrar sesión
        Button(
            onClick = {
                // Cerrar sesión en Firebase
                auth.signOut()

                // Limpiar la pila de navegación y redirigir a la pantalla de inicio de sesión
                navController.navigate("signIn") {
                    // Limpiar el stack de navegación
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    // Hacer que no se pueda regresar a ninguna pantalla previa
                    launchSingleTop = true
                    // Limpiar la pila de navegación
                    restoreState = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF78B153)) // Usar el mismo color del login
        ) {
            Text("Cerrar sesión")
        }
    }

}
