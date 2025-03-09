package com.example.parks.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.design.R

private val verdeBoton = Color(0xFF78B153)

@Composable
fun ParkImageDisplay(
    imageUris: List<Uri>,
    onImageRemove: (Int) -> Unit
) {
    if (imageUris.isEmpty()) return

    val scrollState = rememberScrollState()

    // Define un NestedScrollConnection para manejar los gestos de desplazamiento anidados
    val nestedScrollConnection = androidx.compose.runtime.remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp, max = 110.dp)
            .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .nestedScroll(nestedScrollConnection)
                .padding(horizontal = 4.dp)
        ) {
            imageUris.forEachIndexed { index, uri ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = uri,
                            builder = {
                                crossfade(true)
                                transformations(
                                    coil.transform.RoundedCornersTransformation(8f)
                                )
                            }
                        ),
                        contentDescription = "Imagen ${index + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Contador de imagen
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${index + 1}/5",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                        )
                    }

                    // Botón para eliminar la imagen
                    IconButton(
                        onClick = { onImageRemove(index) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.White, CircleShape)
                            .size(24.dp)
                            .padding(1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen",
                            tint = verdeBoton,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (index < imageUris.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        // Indicadores de sombra para señalar scroll
        if (imageUris.size > 3) {
            // Sombra izquierda
            if (scrollState.value > 10) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(24.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Sombra derecha
            if (scrollState.value < scrollState.maxValue - 10) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(24.dp)
                        .align(Alignment.CenterEnd)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f)
                                )
                            )
                        )
                )
            }
        }
    }
}