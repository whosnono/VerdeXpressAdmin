package com.example.parks.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.design.R
import kotlinx.coroutines.delay

@Composable
fun RegisterParkSuccessScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    // Estados para controlar la secuencia de animaciones
    var animationStage by remember { mutableStateOf(AnimationStage.LOADING) }

    // Composiciones de Lottie para las animaciones
    val loadingComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
    val doneComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.done_animation))

    // Efecto de lanzamiento para controlar la secuencia de animaciones
    LaunchedEffect(Unit) {
        // Secuencia de animaciones
        delay(2000)
        animationStage = AnimationStage.DONE
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animación con crossfade suave
            Crossfade(
                targetState = animationStage, animationSpec = tween(durationMillis = 500)
            ) { stage ->
                when (stage) {
                    AnimationStage.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = loadingComposition,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                    }

                    AnimationStage.DONE -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    composition = doneComposition,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )

                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "¡Parque registrado!",
                                color = Color(0xFF78B153),
                                fontSize = 25.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                                modifier = Modifier.fillMaxWidth()

                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    navController.navigate("Parques") {
                                        // Eliminar todas las instancias anteriores de RegisterParkSuccess y RegisterPark de la pila
                                        popUpTo("registerPark") {
                                            inclusive = true
                                        }

                                    }
                                },
                                modifier = Modifier
                                    .width(175.dp)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                shape = roundedShape
                            ) {
                                Text(
                                    text = "Aceptar",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Enum para manejar los estados de animación
enum class AnimationStage {
    LOADING, DONE
}