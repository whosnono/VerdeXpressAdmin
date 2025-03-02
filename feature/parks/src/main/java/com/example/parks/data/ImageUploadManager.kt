package com.example.parks.data

import android.net.Uri
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.BucketApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import android.content.Context
import io.github.jan.supabase.storage.storage
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Configuración del cliente Supabase para la aplicación Parks
 */
object SupabaseConfig {
    // Reemplaza estos valores con tus credenciales reales de Supabase
    private const val SUPABASE_URL = "https://yeddyrljnqczjxcqirjh.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InllZGR5cmxqbnFjemp4Y3FpcmpoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyNTQyNDYsImV4cCI6MjA1NTgzMDI0Nn0.QLt1enP5CyjmVBW1aTMuMksQIJcfGu9ROeBhr5MY-Sg"
    const val BUCKET_NAME = "parques"

    // Cliente HTTP personalizado para Supabase
    private var httpClient = HttpClient(OkHttp) {
        engine {
            preconfigured = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }
    }

    // Cliente de Supabase inicializado con Storage
    val client: SupabaseClient by lazy {
        createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
            install(Storage)
        }
    }

    // Nombre del bucket para las imágenes de parques
    fun getBucketName(): String = BUCKET_NAME
}

// Clase principal para gestionar la subida de imágenes
class ImageUploadManager(private val context: Context) {

    private val storageBucket: BucketApi = SupabaseConfig.client.storage[SupabaseConfig.BUCKET_NAME]

    // Función para subir múltiples imágenes y obtener sus URLs
    suspend fun uploadImages(imageUris: List<Uri>): List<String> = withContext(Dispatchers.IO) {
        val uploadedUrls = mutableListOf<String>()

        try {
            for (uri in imageUris) {
                val fileName = UUID.randomUUID().toString()
                val file = convertUriToFile(uri, fileName)

                if (file != null) {
                    // Subir el archivo a Supabase Storage
                    val fileBytes = file.readBytes()
                    storageBucket.upload(
                        path = "$fileName.jpg",
                        data = fileBytes
                    )

                    // Obtener y almacenar la URL pública
                    val publicUrl = storageBucket.publicUrl("$fileName.jpg")
                    uploadedUrls.add(publicUrl)

                    // Eliminar el archivo temporal
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e("ImageUpload", "Error subiendo imágenes: ${e.message}")
            throw e
        }

        return@withContext uploadedUrls
    }

    // Función auxiliar para convertir Uri a File
    private fun convertUriToFile(uri: Uri, fileName: String): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "$fileName.jpg")

            FileOutputStream(tempFile).use { outputStream ->
                inputStream.use { input ->
                    val buffer = ByteArray(4 * 1024) // 4k buffer
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                }
            }

            return tempFile
        } catch (e: Exception) {
            Log.e("FileConversion", "Error convirtiendo Uri a File: ${e.message}")
            return null
        }
    }
}