package com.example.parks.data

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.design.SFProDisplayBold
import com.example.parks.ui.verde
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ParkImageShow(
    parkName: String
) {
    var existingImages by remember { mutableStateOf<List<String>>(emptyList()) }

    // Obtener imágenes existentes al iniciar
    LaunchedEffect(parkName) {
        val parkDoc = FirebaseFirestore.getInstance()
            .collection("parques")
            .whereEqualTo("nombre", parkName)
            .get()
            .await()

        if (!parkDoc.isEmpty) {
            existingImages = parkDoc.documents[0].get("imagenes") as? List<String> ?: emptyList()
        }
    }

    Column {
        Text(
            text = "Imágenes actuales",
            fontSize = 20.sp,
            fontFamily = SFProDisplayBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(existingImages) { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Imagen existente",
                    modifier = Modifier
                        .width(140.dp)
                        .height(95.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun ParkProgressImagesUploader(
    parkName: String,
    selectedImageUris: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    onImageRemoved: (Uri) -> Unit,
    onProgressImageRemoved: (String) -> Unit,
    // Nuevo parámetro para imágenes marcadas para eliminación
    onMarkImageForRemoval: (String) -> Unit,
    // Estado de imágenes marcadas para eliminar
    imagesMarkedForRemoval: List<String>
) {
    val context = LocalContext.current
    var progressImages by remember { mutableStateOf<List<String>>(emptyList()) }

    // Launcher para selección de múltiples imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        // Combina las imágenes nuevas con las existentes
        val updatedUris = selectedImageUris + uris
        onImagesSelected(updatedUris)
    }

    // Obtener imágenes de avance al iniciar
    LaunchedEffect(parkName) {
        val parkDoc = FirebaseFirestore.getInstance()
            .collection("parques")
            .whereEqualTo("nombre", parkName)
            .get()
            .await()

        if (!parkDoc.isEmpty) {
            progressImages = parkDoc.documents[0].get("imagenes_avance") as? List<String> ?: emptyList()
        }
    }

    Column {
        Text(
            text = "Imágenes de avance",
            fontSize = 20.sp,
            fontFamily = SFProDisplayBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mostrar primero las imágenes existentes
            items(progressImages) { imageUrl ->
                // Solo mostrar imágenes no marcadas para eliminación
                if(imageUrl !in imagesMarkedForRemoval){
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(95.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Imagen de avance",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(3.dp)),
                            contentScale = ContentScale.Crop
                        )
                        //Boton para eliminar imagen
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen",
                            tint = verde,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.White.copy(alpha = 0.8f), CircleShape)
                                .clickable {
                                    onMarkImageForRemoval(imageUrl)
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Mostrar las imágenes recién seleccionadas
            items(selectedImageUris) { uri ->
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(95.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Nueva imagen",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(3.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar imagen",
                        tint = verde,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                            .clickable {
                                onImageRemoved(uri)
                            }
                            .padding(4.dp)
                    )
                }
            }

            // Botón para agregar más imágenes
            item {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(95.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable { imagePickerLauncher.launch("image/*") }
                        .background(Color.LightGray)
                        .drawBehind {
                            drawRoundRect(
                                color = verde,
                                size = size,
                                cornerRadius = CornerRadius(3.dp.toPx()),
                                style = Stroke(
                                    width = 4.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar imagen",
                        tint = verde,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}
