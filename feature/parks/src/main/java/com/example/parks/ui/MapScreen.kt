package com.example.parks.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.design.SecondaryAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.net.URLEncoder
import java.util.*
import kotlin.math.abs

private const val TAG = "MapScreen"

// Constantes para la validación de ubicación en Hermosillo
private const val HERMOSILLO_LATITUDE = 29.0729
private const val HERMOSILLO_LONGITUDE = -110.9559
private const val MAX_DISTANCE_FROM_CENTER = 0.15 // Aproximadamente 15 km en coordenadas

// Extensión para NavController que maneja correctamente los espacios en las direcciones
fun NavController.navigateToRegisterPark(
    latitude: Double?,
    longitude: Double?,
    address: String?,
    name: String?,
    desc: String?,
    status: String?,
    needs: String?,
    comments: String?
) {
    val encodedAddress = address?.let {
        try {
            URLEncoder.encode(it, "UTF-8")
        } catch (e: Exception) {
            Log.e(TAG, "Error al codificar dirección: ${e.message}")
            it
        }
    }
    navigate("registerPark?lat=$latitude&lon=$longitude&address=$encodedAddress&name=$name&desc=$desc&status=$status&needs=$needs&comments=$comments")
}

@Composable
fun MapScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    name: String?,
    desc: String?,
    status: String?,
    needs: String?,
    comments: String?
) {
    val verdeBoton = Color(0xFF78B153)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Coordenadas de Hermosillo, Sonora, México
    val hermosilloCoordsLat = HERMOSILLO_LATITUDE
    val hermosilloCoordsLon = HERMOSILLO_LONGITUDE

    // Estados para manejar la ubicación y dirección
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var addressText by remember { mutableStateOf("") }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var permissionsRequested by remember { mutableStateOf(false) }
    var mapInitialized by remember { mutableStateOf(false) }
    var isLocationInHermosillo by remember { mutableStateOf(true) }

    // Inicializa OSMdroid con el contexto adecuado
    LaunchedEffect(Unit) {
        val ctx = context.applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = ctx.packageName
    }

    // Función para verificar si una ubicación está dentro de Hermosillo
    fun isLocationInHermosillo(geoPoint: GeoPoint): Boolean {
        val latDiff = abs(geoPoint.latitude - HERMOSILLO_LATITUDE)
        val lonDiff = abs(geoPoint.longitude - HERMOSILLO_LONGITUDE)
        return latDiff <= MAX_DISTANCE_FROM_CENTER && lonDiff <= MAX_DISTANCE_FROM_CENTER
    }

    // Función para formatear la dirección (definida correctamente)
    fun formatAddress(address: android.location.Address): String {
        val components = mutableListOf<String>()

        // Añadir componentes individuales si están disponibles
        if (!address.thoroughfare.isNullOrEmpty()) {
            components.add(address.thoroughfare)
        }

        if (!address.subThoroughfare.isNullOrEmpty()) {
            components.add(address.subThoroughfare)
        }

        if (!address.locality.isNullOrEmpty()) {
            components.add(address.locality)
        }

        if (!address.adminArea.isNullOrEmpty()) {
            components.add(address.adminArea)
        }

        if (!address.postalCode.isNullOrEmpty()) {
            components.add(address.postalCode)
        }

        if (!address.countryName.isNullOrEmpty()) {
            components.add(address.countryName)
        }

        // Si hay líneas de dirección completas, usarlas
        val addressLines = mutableListOf<String>()
        for (i in 0..address.maxAddressLineIndex) {
            val line = address.getAddressLine(i)
            if (!line.isNullOrEmpty()) {
                addressLines.add(line)
            }
        }

        // Preferir líneas de dirección completas si están disponibles
        return if (addressLines.isNotEmpty()) {
            addressLines.joinToString(", ")
        } else if (components.isNotEmpty()) {
            components.joinToString(", ")
        } else {
            "Dirección no disponible"
        }
    }

    // Función para obtener la dirección CORREGIDA
    suspend fun getAddressFromLocation(geoPoint: GeoPoint): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                var addressResult = "Buscando dirección..."

                // Verificar si la ubicación está en Hermosillo
                val inHermosillo = isLocationInHermosillo(geoPoint)

                // Actualizar el estado de validación
                withContext(Dispatchers.Main) {
                    isLocationInHermosillo = inHermosillo
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Para Android 13+, usamos el método asíncrono con callback
                    try {
                        val latch = java.util.concurrent.CountDownLatch(1)

                        geocoder.getFromLocation(
                            geoPoint.latitude,
                            geoPoint.longitude,
                            1
                        ) { addresses ->
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                addressResult = formatAddress(address)
                            } else {
                                addressResult = "No se encontró dirección en esta ubicación"
                            }
                            latch.countDown()
                        }

                        // Esperamos a que se complete la operación
                        latch.await(2000, java.util.concurrent.TimeUnit.MILLISECONDS)

                    } catch (e: Exception) {
                        Log.e(TAG, "Error con Geocoder en Android 13+: ${e.message}")
                        addressResult = "Error al obtener dirección: ${e.message}"
                    }
                } else {
                    // Para Android 12 y anteriores, usamos el método síncrono
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(
                            geoPoint.latitude,
                            geoPoint.longitude,
                            1
                        )

                        if (addresses != null && addresses.isNotEmpty()) {
                            addressResult = formatAddress(addresses[0])
                        } else {
                            addressResult = "No se encontró dirección en esta ubicación"
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error con Geocoder en Android <= 12: ${e.message}")
                        addressResult = "Error al obtener dirección: ${e.message}"
                    }
                }

                addressResult
            } catch (e: Exception) {
                Log.e(TAG, "Error general en getAddressFromLocation: ${e.message}")
                "Error al obtener la dirección: ${e.message}"
            }
        }
    }

    // Función para actualizar la dirección
    fun updateAddress(geoPoint: GeoPoint) {
        coroutineScope.launch {
            addressText = "Buscando dirección..."
            addressText = getAddressFromLocation(geoPoint)
        }
    }

    // Solicitud de permiso de ubicación
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        permissionsRequested = true

        // Ahora sí podemos inicializar el mapa
        mapInitialized = true

        if (!isGranted) {
            // Si no hay permiso, establecer Hermosillo como ubicación predeterminada
            val defaultLocation = GeoPoint(hermosilloCoordsLat, hermosilloCoordsLon)
            selectedLocation = defaultLocation
            updateAddress(defaultLocation)
        }
    }

    // Verificar permisos de ubicación al iniciar
    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true
            permissionsRequested = true
            mapInitialized = true
        } else {
            // Esto lanzará el diálogo de permiso
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Usa Scaffold para mantener el AppBar en posición
    Scaffold(
        topBar = {
            SecondaryAppBar(
                showIcon = true,
                onIconClick = { navController.navigate("registerPark") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // MapView con control de ciclo de vida apropiado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Solo mostrar el mapa cuando la inicialización y los permisos están listos
                if (permissionsRequested && mapInitialized) {
                    var mapViewState by remember { mutableStateOf<MapView?>(null) }

                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(15.0)

                                // Configurar el mapa con ubicación predeterminada inicialmente
                                val defaultLocation = GeoPoint(hermosilloCoordsLat, hermosilloCoordsLon)
                                controller.setCenter(defaultLocation)

                                // Crear y configurar el marcador
                                val marker = Marker(this)
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = "Ubicación seleccionada"
                                marker.position = defaultLocation
                                overlays.add(marker)

                                // Configurar eventos de toque en el mapa
                                val mapEventsReceiver = object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                        marker.position = p
                                        selectedLocation = p
                                        updateAddress(p)
                                        invalidate()
                                        return true
                                    }

                                    override fun longPressHelper(p: GeoPoint): Boolean {
                                        return false
                                    }
                                }

                                val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
                                overlays.add(mapEventsOverlay)

                                // Configurar ubicación actual si tenemos permisos
                                if (hasLocationPermission) {
                                    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                                    myLocationOverlay.enableMyLocation()
                                    overlays.add(myLocationOverlay)

                                    myLocationOverlay.runOnFirstFix {
                                        val myLocation = myLocationOverlay.myLocation
                                        if (myLocation != null) {
                                            val geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                                            currentLocation = geoPoint
                                            marker.position = geoPoint
                                            selectedLocation = geoPoint

                                            // Obtiene la dirección inicial
                                            coroutineScope.launch(Dispatchers.Main) {
                                                addressText = getAddressFromLocation(geoPoint)
                                            }

                                            // Actualiza el mapa en el hilo principal
                                            post {
                                                controller.setCenter(geoPoint)
                                                invalidate()
                                            }
                                        }
                                    }
                                } else {
                                    // Establecer la ubicación inicial como Hermosillo
                                    selectedLocation = defaultLocation
                                    coroutineScope.launch {
                                        addressText = getAddressFromLocation(defaultLocation)
                                    }
                                }

                                mapViewState = this
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { mapView ->
                            // Actualizar propiedades si es necesario
                        }
                    )

                    // Gestionar el ciclo de vida del MapView
                    DisposableEffect(lifecycleOwner) {
                        val mapView = mapViewState
                        val observer = LifecycleEventObserver { _, event ->
                            when (event) {
                                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                                else -> {}
                            }
                        }

                        lifecycleOwner.lifecycle.addObserver(observer)

                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                            mapView?.onDetach()
                        }
                    }
                } else {
                    // Mostrar indicador de carga mientras se inicializa
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (!permissionsRequested)
                                "Solicitando permisos..."
                            else
                                "Cargando mapa..."
                        )
                    }
                }

                // Instrucciones superpuestas (solo cuando el mapa está visible)
                if (permissionsRequested && mapInitialized) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Toca en el mapa para seleccionar una ubicación en Hermosillo",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Panel inferior para mostrar la dirección y coordenadas
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    //
                    Text(
                        text = buildAnnotatedString {
                            // Primera parte del texto con estilo titleSmall
                            withStyle(style = MaterialTheme.typography.titleSmall.toSpanStyle()) {
                                append("Ubicación seleccionada: ")
                            }
                            // Segunda parte del texto con estilo bodyMedium
                            withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                                append(addressText.ifEmpty { "Selecciona una ubicación en el mapa" })
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedLocation != null) {
                        Text(
                            text = "GeoPoint: ${selectedLocation?.latitude}, ${selectedLocation?.longitude}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    //
                    // Mensaje de advertencia cuando la ubicación no está en Hermosillo
                    if (!isLocationInHermosillo && selectedLocation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "La ubicación seleccionada está fuera de los límites de Hermosillo. Por favor, selecciona una ubicación dentro de la ciudad.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (selectedLocation != null && isLocationInHermosillo) {
                                // Usar la función de extensión para navegar
                                navController.navigateToRegisterPark(
                                    latitude = selectedLocation?.latitude,
                                    longitude = selectedLocation?.longitude,
                                    address = addressText,
                                    name = name,
                                    desc = desc,
                                    status = status,
                                    needs = needs,
                                    comments = comments
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verdeBoton
                        ),
                        enabled = selectedLocation != null && isLocationInHermosillo
                    ) {
                        Text("Usar esta ubicación")
                    }
                }
            }
        }
    }
}