package com.example.parks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class ParkFormState(
    val parkName: String = "",
    val location: String = "",
    val description: String = "",
    val status: String = "",
    val comments: String = "",
    val selectedNeeds: List<String> = emptyList(),
    val selectedImageUris: List<android.net.Uri> = emptyList()
)

data class ValidationResult(
    val parkNameError: String? = null,
    val locationError: String? = null,
    val descriptionError: String? = null,
    val statusError: String? = null,
    val needsError: String? = null,
    val commentsError: String? = null,
    val imageError: String? = null
)

@Composable
fun rememberParkFormValidator() = remember {
    { state: ParkFormState ->
        ValidationResult(
            parkNameError = when {
                state.parkName.isEmpty() -> "El nombre no puede estar vacío"
                state.parkName.length > 100 -> "El nombre no puede exceder 100 caracteres"
                else -> null
            },
            locationError = when {
                state.location.isEmpty() -> "La ubicación no puede estar vacía"
                else -> null
            },
            descriptionError = when {
                state.description.isEmpty() -> "La descripción no puede estar vacía"
                state.description.length > 500 -> "La descripción no puede exceder 500 caracteres"
                else -> null
            },
            statusError = when {
                state.status.isEmpty() -> "Selecciona el estado actual del parque"
                else -> null
            },
            needsError = when {
                state.selectedNeeds.isEmpty() -> "Selecciona al menos una necesidad"
                else -> null
            },
            commentsError = when {
                state.comments.length > 300 -> "Los comentarios no pueden exceder 300 caracteres"
                else -> null
            },
            imageError = when {
                state.selectedImageUris.isEmpty() -> "Debes seleccionar al menos una imagen."
                state.selectedImageUris.size > 5 -> "Solo puedes subir un máximo de 5 imágenes."
                else -> null
            }
        )
    }
}

fun ValidationResult.isValid(): Boolean {
    return parkNameError == null &&
            locationError == null &&
            descriptionError == null &&
            statusError == null &&
            needsError == null &&
            commentsError == null &&
            imageError == null
}

