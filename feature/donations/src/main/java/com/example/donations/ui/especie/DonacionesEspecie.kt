package com.example.donations.ui.especie


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.design.MainAppBar
import com.example.donations.ui.DonationItem
import com.example.donations.ui.getEspecieDonationsFromFirebase

//
//var especieDonations by remember { mutableStateOf<List<DonationItem>>(emptyList()) }
//
//LaunchedEffect(Unit) {
//    getEspecieDonationsFromFirebase { donations ->
//        especieDonations = donations
//    }
//}
//
//LaunchedEffect(especieDonations) {
//    if (especieDonations.isEmpty()) {
//        getEspecieDonationsFromFirebase { donations ->
//            if (donations.isEmpty()) {
//                especieDonations = listOf(
//                    DonationItem("Error al cargar datos", "Intente de nuevo más tarde")
//                )
//            }
//        }
//    }
//}

@Composable
fun DonacionesEspecieScreen() {
    Column(modifier = Modifier.fillMaxSize()) {

        MainAppBar()

    }
}


@Preview(showBackground = true)
@Composable
fun VerPantalla() {
    DonacionesEspecieScreen()
}