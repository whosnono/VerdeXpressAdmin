package com.example.parks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.design.R
import com.example.design.SecondaryAppBar
import androidx.navigation.NavController
import com.example.design.SecondaryAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterParkScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    var showNeedsDialog by remember { mutableStateOf(false) }
    var selectedNeeds by remember { mutableStateOf(setOf<String>()) }
    val roundedShape = RoundedCornerShape(12.dp)

    Column {
        SecondaryAppBar(
            showIcon = true,
            onIconClick = {
                navController.navigate("Parques") // Navegar a la pantalla de "Parques"
            }
        )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registrar Parque",
            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
            fontSize = 25.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = {
                Text(
                    text = "Nombre del Parque",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                ) },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = {
                Text(
                    text = "Ubicación",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            },
            trailingIcon = {
                IconButton(onClick = { /* Acción al hacer clic */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.map_add),
                        contentDescription = "Select location",
                        modifier = Modifier.size(24.dp) // Tamaño de la imagen
                    )
                }
            },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = {
                Text(
                    text = "Descripción",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                ) },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf("") }
        // Lista de opciones para el dropdown
        val options = listOf("Excelente", "Bueno", "Regular", "Deficiente", "Muy deficiente")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOptionText,
                onValueChange = { },
                label = {
                    Text(
                        text = "Estado actual",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = roundedShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeBoton,
                    focusedLabelColor = verdeBoton,
                    cursorColor = verdeBoton
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (selectedOptionText == option) Color.White else Color.Black
                            )
                        },
                        onClick = {
                            selectedOptionText = option
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.Black,
                            leadingIconColor = verdeBoton,
                            trailingIconColor = verdeBoton,
                            disabledTextColor = Color.Gray,
                            disabledLeadingIconColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray,
                        ),
                        modifier = Modifier.background(
                            color = if (selectedOptionText == option) verdeBoton else Color.Transparent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = {
                Text(
                    text = "Imagenes",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            },
            trailingIcon = {
                IconButton(onClick = { /* Acción al hacer clic */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.image_add),
                        contentDescription = "Select location",
                        modifier = Modifier.size(24.dp) // Tamaño de la imagen
                    )
                }
            },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = selectedNeeds.joinToString(", "),
            onValueChange = { },
            label = {
                Text(
                    text = "Necesidades del Parque",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                ) },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showNeedsDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Seleccionar necesidades")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = {
                Text(
                    text = "Comentarios adicionales",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                ) },
            shape = roundedShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeBoton,
                focusedLabelColor = verdeBoton,
                cursorColor = verdeBoton
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { },
            modifier = Modifier
                .padding(bottom = 100.dp, start = 100.dp)
                .width(175.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
            shape = roundedShape
        ) {
            Text(
                text ="Validar",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
        }
    }
}
    if (showNeedsDialog) {
        Dialog(
            onDismissRequest = { showNeedsDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Seleccione las\nnecesidades del parque",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    val needs = listOf(
                        "Mobiliario",
                        "Iluminación",
                        "Jardineria",
                        "Seguridad",
                        "Limpieza"
                    )

                    needs.chunked(2).forEach { rowNeeds ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowNeeds.forEach { need ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = need in selectedNeeds,
                                        onCheckedChange = { checked ->
                                            selectedNeeds = if (checked) {
                                                selectedNeeds + need
                                            } else {
                                                selectedNeeds - need
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = verdeBoton
                                        )
                                    )
                                    Text(
                                        text = need,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { showNeedsDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verdeBoton,
                        )
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}