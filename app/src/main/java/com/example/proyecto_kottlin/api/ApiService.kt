package com.example.proyecto_kottlin.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Modelo de datos que enviaremos
data class Report(
    val id: Int? = null,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = "evidencia_foto.jpg" // En un proyecto real aquí iría la URL de la imagen subida
)

interface ApiService {
    // Usamos endpoints de prueba de jsonplaceholder
    @GET("posts")
    suspend fun getReports(): List<Report>

    @POST("posts")
    suspend fun createReport(@Body report: Report): Report
}

object RetrofitClient {
    // API pública para simular conexiones exitosas y que la app no falle
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}