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
                1. Comprobar si hay una sesión activa, si sí, redirigir a MainScreen()
                2. Si no hay sesión activa, redirigir a SignInScreen()
            * */

            //SignIpScreen()
            MainScreen()
        }
    }

    private fun initializeFirebase() {
        if (FirebaseApp.getApps(applicationContext).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext)
        }
    }

}