package com.example.parks.ui

import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun SlideInFilterPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onApply: (Map<String, String>) -> Unit,
    initialSort: String = "",
    initialStatus: String = "",
    modifier: Modifier = Modifier
) {
    var selectedSort by remember { mutableStateOf(initialSort) }
    var selectedStatus by remember { mutableStateOf(initialStatus) }

    // Definir colores
    val gris = Color(0xFFEAEAEA)

    // Estado para la animación
    val visibleState = remember { MutableTransitionState(false) }

    // Actualizar el estado visible basado en el prop isVisible
    LaunchedEffect(isVisible) {
        visibleState.targetState = isVisible
    }

    LaunchedEffect(initialSort, initialStatus) {
        selectedSort = initialSort
        selectedStatus = initialStatus
    }

    // Un Box para cubrir toda la pantalla cuando el panel está abierto
    if (visibleState.currentState || visibleState.targetState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
        ) {
            // Fondo semi-transparente que cubre el resto de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .zIndex(10f)
                    .clickable(onClick = onDismiss)
            )

            // Panel deslizante de filtros
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it }, // Comienza fuera de la pantalla (derecha)
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it }, // Sale de la pantalla (derecha)
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(306.dp)
                    .align(Alignment.CenterEnd)
                    .zIndex(100f)
            ) {
                Surface(
                    modifier = Modifier.fillMaxHeight(),
                    shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Header verde
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(verde)
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                "Filtro de parques",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontFamily = SFProDisplayBold
                            )
                        }

                        // Contenido del filtro
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Ordenamiento
                            Text(
                                "Nombre del parque",
                                fontFamily = SFProDisplayM,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                FilterButton(
                                    text = "A-Z",
                                    selected = selectedSort == "A-Z",
                                    onClick = { selectedSort = "A-Z" },
                                    activeColor = verde,
                                    inactiveColor = gris
                                )

                                FilterButton(
                                    text = "Z-A",
                                    selected = selectedSort == "Z-A",
                                    onClick = { selectedSort = "Z-A" },
                                    activeColor = verde,
                                    inactiveColor = gris
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 16.dp))

                            // Estado
                            Text(
                                "Situación del parque",
                                fontFamily = SFProDisplayM,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                FilterButton(
                                    text = "Recibiendo donaciones",
                                    selected = selectedStatus == "Recibiendo donaciones",
                                    onClick = { selectedStatus = if (selectedStatus == "Recibiendo donaciones") "" else "Recibiendo donaciones" },
                                    activeColor = verde,
                                    inactiveColor = gris,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                FilterButton(
                                    text = "Financiación completada",
                                    selected = selectedStatus == "Financiación completada",
                                    onClick = { selectedStatus = if (selectedStatus == "Financiación completada") "" else "Financiación completada" },
                                    activeColor = verde,
                                    inactiveColor = gris,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                FilterButton(
                                    text = "En desarrollo",
                                    selected = selectedStatus == "En desarrollo",
                                    onClick = { selectedStatus = if (selectedStatus == "En desarrollo") "" else "En desarrollo" },
                                    activeColor = verde,
                                    inactiveColor = gris,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                FilterButton(
                                    text = "Mantenimiento requerido",
                                    selected = selectedStatus == "Mantenimiento requerido",
                                    onClick = { selectedStatus = if (selectedStatus == "Mantenimiento requerido") "" else "Mantenimiento requerido" },
                                    activeColor = verde,
                                    inactiveColor = gris,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                FilterButton(
                                    text = "Inactivo",
                                    selected = selectedStatus == "Inactivo",
                                    onClick = { selectedStatus = if (selectedStatus == "Inactivo") "" else "Inactivo" },
                                    activeColor = verde,
                                    inactiveColor = gris,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 16.dp))

                            Spacer(modifier = Modifier.weight(1f))

                            // Botones de acción en la parte inferior
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        selectedSort = ""
                                        selectedStatus = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, verde),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = verde
                                    )
                                ) {
                                    Text(text = "Reestablecer",
                                        fontFamily = SFProDisplayM,
                                        fontSize = 14.sp)
                                }

                                Button(
                                    onClick = {
                                        val filters = mapOf(
                                            "sort" to selectedSort,
                                            "status" to selectedStatus
                                        )
                                        onApply(filters)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = verde
                                    )
                                ) {
                                    Text(text = "Aplicar",
                                        fontFamily = SFProDisplayM,
                                        fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) activeColor else inactiveColor,
            contentColor = if (selected) Color.White else Color.Black
        )
    ) {
        Text(text = text,
            fontFamily = SFProDisplayM,
            fontSize = 14.sp)
    }
}

