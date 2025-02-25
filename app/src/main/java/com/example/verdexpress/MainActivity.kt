package com.example.verdexpress

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.example.navigation.MainScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeFirebase()

        setContent {
            /*
            Flujo propuesto:
                1. Comprobar si hay una sesión activa, si sí, startDestination = "Inicio"
                2. Si no hay sesión activa, startDestination = "signIn"

                Nota: Para que esto funcione, se debe establecer startDestination como
                parametro en AppNavHost o MainScreen o algo así
            * */

            MainScreen()
        }
    }

    private fun initializeFirebase() {
        if (FirebaseApp.getApps(applicationContext).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext)
        }
    }

}