package com.example.donations.ui.inicio

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.design.MainAppBar

@Composable
fun DonationsScreen(navController: NavController, showDialog: Boolean = false) {
    // Estado para controlar la visibilidad del diálogo
    var isDialogVisible by rememberSaveable { mutableStateOf(showDialog) }

    // Observa si se debe mostrar el diálogo desde el savedStateHandle
    LaunchedEffect(navController) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("showDonationDialog")
            ?.observeForever { shouldShow ->
                isDialogVisible = shouldShow == true
            }
    }

    // Mostrar el Dialog cuando `isDialogVisible` sea `true`
    if (isDialogVisible) {
        DonationTypeDialog(
            onDismiss = {
                isDialogVisible = false
                // Guardar en savedStateHandle para persistencia
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "showDonationDialog",
                    false
                )
            },
            navController = navController
        )
    }

    // Contenido de la pantalla
    Column{
        MainAppBar()

        Text("Donations Screen!!!!")
    }

}