package com.example.proyecto_kottlin.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyecto_kottlin.viewmodel.ReportViewModel
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(viewModel: ReportViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Estados de los campos
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    // Estado para guardar la foto capturada
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 1. Lanzador para obtener permisos y extraer el GPS
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        Toast.makeText(context, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No se pudo obtener ubicación. Activa el GPS.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // 2. Lanzador para la Cámara (Captura la foto y la guarda en imageBitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            if (bitmap != null) {
                imageBitmap = bitmap
                Toast.makeText(context, "Foto capturada con éxito", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // 3. NUEVO: Lanzador para pedir permiso de la cámara ANTES de abrirla
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Si el usuario nos da permiso, ahora sí abrimos la cámara
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(context, "Necesitas dar permiso para usar la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Interfaz de la aplicación
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título del reporte") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Coordenadas: Lat: $latitude | Lon: $longitude")

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            // Botón GPS
            Button(onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                Text("Obtener GPS")
            }
            // Botón Cámara (Ahora pide el permiso primero)
            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("Tomar Foto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra la imagen si el usuario ya tomó la foto
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Evidencia Fotográfica",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón Enviar
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    viewModel.createReport(title, description, latitude, longitude)
                    Toast.makeText(context, "Enviando reporte a la API...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor llena título y descripción", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Reporte a API")
        }
    }
}