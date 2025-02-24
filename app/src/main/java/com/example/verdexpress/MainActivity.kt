package com.example.verdexpress

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.example.navigation.MainScreen
//import com.example.auth.ui.SignUpScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeFirebase()

        setContent {
            // Aquí hay que implementar una lógica tipo, si no hay una sesión inicidiada, entonces muestra la pantalla de inicio de sesión, y si sí, que ejecute MainScreen
            //SignUpScreen()
            MainScreen()
        }
    }

    private fun initializeFirebase() {
        if (FirebaseApp.getApps(applicationContext).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext)
        }
    }

}