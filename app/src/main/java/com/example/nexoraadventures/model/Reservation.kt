package com.example.nexoraadventures.model

/**
 * Estados disponibles para las reservas turísticas.
 */
enum class EstadoReserva(
    val descripcion: String
) {

    PENDIENTE("Pendiente"),

    CONFIRMADO("Confirmado"),

    FINALIZADO("Finalizado")
}

/**
 * Modelo de datos de una reserva turística.
 *
 * Almacena la información del cliente,
 * destino turístico, fecha y ubicación.
 */
data class Reservation(

    // Identificador de la reserva
    val id: Int,

    // Información del cliente
    val clienteNombre: String,

    val telefono: String,

    // Información del viaje
    val destino: String,

    val fechaViaje: String,

    val cantidadPersonas: Int,

    // Estado de la reserva
    val estadoReserva: EstadoReserva =
        EstadoReserva.PENDIENTE,

    // Punto de encuentro
    val puntoEncuentro: String,

    // Coordenadas geográficas
    val latitud: Double = 0.0,

    val longitud: Double = 0.0

)
