package com.example.auth.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.design.SecondaryAppBar
import kotlinx.coroutines.delay

@Composable
fun SignUpSuccessScreen(navController: NavController) {
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
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.navigate("signUp")
        })

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texto "Regístrate" siempre visible
            Text(
                text = "Regístrate",
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Animación con crossfade suave
            Crossfade(
                targetState = animationStage, animationSpec = tween(durationMillis = 500)
            ) { stage ->
                when (stage) {
                    AnimationStage.LOADING -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    composition = doneComposition,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )

                            }

                            Text(
                                text = "¡Usuario registrado!",
                                color = Color(0xFF78B153),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )

                            Text(
                                text = "Te hemos enviado un correo electrónico para la confirmación de la creación de tu cuenta",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_regular)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 48.dp)
                            )

                            Button(
                                onClick = { navController.navigate("Inicio") },
                                modifier = Modifier
                                    .width(175.dp)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                shape = roundedShape
                            ) {
                                Text(
                                    text = "Aceptar",
                                    color = Color.White,
                                    fontSize = 16.sp,
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