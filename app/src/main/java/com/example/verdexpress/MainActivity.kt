package com.example.verdexpress

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.FirebaseApp
import com.example.navigation.MainScreen
import com.example.auth.ui.IntroScreen


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeFirebase()

        setContent {
            val showIntro = remember { mutableStateOf(true) }

            if (showIntro.value) { //Para mostrar la página de Intro
                IntroScreen {
                    showIntro.value = false
                }
            } else {
                MainScreen()
            }
        }
    }

    private fun initializeFirebase() {
        if (FirebaseApp.getApps(applicationContext).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext)
        }
    }

}