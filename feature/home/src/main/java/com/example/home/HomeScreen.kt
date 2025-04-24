package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.MainAppBar
import com.example.design.R.font


@Composable
fun HomeScreen() {
    Scaffold(
        topBar = { MainAppBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Greeting(
                nombre = "Administrador" //Reemplazar por el nombre del usuario actual, se puede copiar del código de perfil de admin
            )

            Spacer(modifier = Modifier.height(10.dp))

            subtitulo(
                subtitle = "Resumen de notificaciones" //Valor fijo, no cambiar
            )

            Spacer(modifier = Modifier.height(1.dp))

            NPSection()

            Spacer(modifier = Modifier.height(6.dp))

            subtitulo(
                subtitle = "Resumen general" //Valor fijo, no cambiar
            )

            Spacer(modifier = Modifier.height(1.dp))

            ResumenGeneralSection(
                nombreParqueN = "Pitic", //Reemplazar por el nombre del parque que coincida con la notificacion
                nombreParqueD = "Miau", //Reemplazar por el nombre del parque que coincida con la notificacion
                numeroParques = 3, //Aquí se reemplaza por el número de parques que están en estado de revisión
                numeroDonaciones = 3 //Aquí el número de donaciones pendientes por aprobar o rechazar
            )

        }
    }
}

@Composable
fun Greeting(nombre: String) { //Mensaje inicial de bienvenida
    Text(
        text = "Hola, $nombre",
        style = TextStyle(
            fontSize = 25.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF000000),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun subtitulo(subtitle: String) { //Para los subtitulos de color verde
    Text(
        text = subtitle,
        style = TextStyle(
            fontSize = 22.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
            fontWeight = FontWeight(700),
            color = Color(0xFF78B153),
            letterSpacing = 0.25.sp,
        )
    )
}

@Composable
fun NPSection() { //Recuadro principal para mostrar las notificaciones más recientes

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 10.dp))
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 10.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Notificaciones pendientes", //Valor fijo, no cambiar
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Estado actual de parques apoyados", //Valor fijo, no cambiar
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )

        Spacer(modifier = Modifier.height(13.dp))

        NotificationItem( //Reemplazar titulo(o tipoNotificación), usuario, tipo noti y fecha por valores reales
            tipoNotificacion = TipoNotificacion.NUEVO_PARQUE,
            usuario = "Adriana ",
            texto = "ha subido un parque para su ",
            tipoNoti = "revisión",
            fecha = "dd/mm/2025"
        )
        Spacer(modifier = Modifier.height(8.dp))
        NotificationItem(
            tipoNotificacion = TipoNotificacion.DONACION_ESPECIE,
            usuario = "Sofía ",
            texto = "realizó una ",
            tipoNoti = "donación en especie",
            fecha = "dd/mm/2025"
        )
        Spacer(modifier = Modifier.height(8.dp))
        NotificationItem(
            tipoNotificacion = TipoNotificacion.DONACION_MONETARIA,
            usuario = "Elda ",
            texto = "ha realizado una ",
            tipoNoti = "donación monetaria",
            fecha = "dd/mm/2025"
        )
    }
}


@Composable
fun NotificationItem(tipoNotificacion: TipoNotificacion, usuario: String, texto: String, tipoNoti: String, fecha: String) { //Para los recuadros donde se verá la información de las notificaciones

    val titulo = when (tipoNotificacion) { //Lo hice así para que solo se haga una lógica que se lea si es una donacion (y que se vea si es en epsecie o monetaria) o si se trata de un parque nuevo :)
        TipoNotificacion.DONACION_ESPECIE -> "Nueva donación en especie"
        TipoNotificacion.DONACION_MONETARIA -> "Nueva donación monetaria"
        TipoNotificacion.NUEVO_PARQUE -> "Se ha subido un nuevo parque"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 8.dp))
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = titulo, //Lo puse así por que el titulo depende del tipo de notificación, no se como se debería hacer la lógica para esto
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF000000),
                        letterSpacing = 0.15.sp,
                    )
                )
                Row {
                    Text(
                        text = "$usuario",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFF78B153),
                            letterSpacing = 0.15.sp,
                        )
                    )
                    Text(
                        text = texto,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                            letterSpacing = 0.15.sp,
                        )
                    )
                    Text(
                        text = tipoNoti,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFF78B153),
                            letterSpacing = 0.15.sp,
                        )
                    )
                }
            }
        }
        Text(
            text = "$fecha",
            style = TextStyle(
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(400),
                color = Color(0xFF757575),
                letterSpacing = 0.15.sp,
            )
        )
    }
}

@Composable
fun ResumenGeneralSection(nombreParqueN: String, nombreParqueD: String, numeroParques: Int, numeroDonaciones: Int) { //Para los recuadros de la parte de abajo, donde se muestran cuantas donaciones o parques faltan por aprobar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Sección de Parques en revisión
        ResumenItem(
            titulo = "Parques en revisión", //Valor fijo, no reemplazar
            descripcion = "$numeroParques parques en revisión", //Reemplazar numeroParques por la suma de parques pendientes por revisar
            elementos = listOf("Parque \"$nombreParqueN\"", "Parque \"$nombreParqueN\"", "Parque \"$nombreParqueN\""), //Reemplazar nombreParqueN por el nombre del parque nuevo por aprobar
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp)) // Añadimos un Spacer entre las secciones

        // Sección de Donaciones pendientes
        ResumenItem(
            titulo = "Donaciones pendientes",
            descripcion = "$numeroDonaciones donaciones por aprobar", //Reemplazar numeroDonaciones por la suma de donaciones pendientes por revisar
            elementos = listOf("Parque \"$nombreParqueD\"", "Parque \"$nombreParqueD\"", "Parque \"$nombreParqueD\""), //Reemplazar nombreParqueD por el nombre del parque al que se realizo una donacion
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResumenItem(titulo: String, descripcion: String, elementos: List<String>, modifier: Modifier = Modifier) { //Estilo de los recuadros :)
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .border(width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 10.dp))
            .background(color = Color(0xFFF5F6F7), shape = RoundedCornerShape(size = 10.dp))
            .padding(16.dp)
            .width(182.dp)
            .height(255.dp)
    ) {
        Text(
            text = titulo,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_bold)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(font.sf_pro_display_semibold)),
                fontWeight = FontWeight(400),
                color = Color(0xFF000000),
                letterSpacing = 0.25.sp,
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            elementos.forEach { elemento -> //Son los recuadros verdes con los nombres de los parques donde están pendientes de aprobar/ donaciones por aprobar
                Text(
                    text = elemento,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFF78B153), shape = RoundedCornerShape(size = 5.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight(500)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

enum class TipoNotificacion { //Para los titulos que tendrán las notificaciones, si no resulta ser más fácil se pueden eliminar y poner algo nuevo :)
    DONACION_ESPECIE,
    DONACION_MONETARIA,
    NUEVO_PARQUE
}