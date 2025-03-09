package com.example.donations.ui.donacionMonetaria

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.design.SecondaryAppBar

@Composable
fun FormScreen(navController: NavController) {

    Box {
        Column {
            //AppBar
            SecondaryAppBar(showIcon = true, onIconClick = {
                navController.popBackStack("Donaciones", inclusive = false)
                navController.navigate("Donaciones")
            })

            //Contenido de la pantalla
            Text("Donación monetaria :)")

        }
    }

}