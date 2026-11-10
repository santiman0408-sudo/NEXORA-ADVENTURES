package com.example.nexoraadventures.model

/**
 * Modelo de datos de un destino turístico.
 *
 * Representa la información que utilizará
 * el catálogo de NEXORA ADVENTURES.
 */
data class Destination(

    // Identificador único del destino
    val id: Int,

    // Nombre del destino turístico
    val name: String,

    // Ubicación geográfica
    val location: String,

    // Categoría turística
    val category: String,

    // Descripción del destino
    val description: String,

    // Precio referencial en soles
    val price: Double,

    // Duración de la experiencia
    val duration: String,

    // Nivel de dificultad
    val difficulty: String,

    // Punto de encuentro
    val meetingPoint: String,

    // Actividades disponibles
    val activities: String,

    // Identificador de la fotografía local
    val imageResId: Int? = null

)
