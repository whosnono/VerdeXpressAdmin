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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ParkImageUploader(
    parkName: String,
    selectedImageUris: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    onImagesUploaded: (List<String>) -> Unit,
    situation: String
) {
    val context = LocalContext.current
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

    // Launcher para selección de múltiples imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        onImagesSelected(uris)
    }

    Column {
        // Mostrar imágenes existentes y nuevas
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Imágenes existentes
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
            if (situation == "En desarrollo") {
                // Imágenes nuevas seleccionadas
                items(selectedImageUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Nueva imagen",
                        modifier = Modifier
                            .width(140.dp)
                            .height(95.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Botón para agregar más imágenes
                item {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(95.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .clickable { imagePickerLauncher.launch("image/*") }
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar imagen",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        }
    }
}