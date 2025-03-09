package com.example.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
        DonationTypeDialog(onDismiss = {
            isDialogVisible = false
            // Guardar en savedStateHandle para persistencia
            navController.currentBackStackEntry?.savedStateHandle?.set("showDonationDialog", false)
        })
    }

    // Contenido de la pantalla
    Column{
        MainAppBar()

        Text("Donations Screen!!!!")
    }

}