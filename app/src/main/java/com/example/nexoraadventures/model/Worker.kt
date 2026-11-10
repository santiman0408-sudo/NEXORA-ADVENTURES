package com.example.nexoraadventures.model

/**
 * Estados laborales de los trabajadores.
 */
enum class EstadoLaboral(
    val descripcion: String
) {
    ACTIVO("Activo"),
    INACTIVO("Inactivo")
}

/**
 * Modelo de trabajador de NEXORA ADVENTURES.
 *
 * Contiene información personal, laboral y salarial.
 */
data class Worker(

    val id: Int = 0,

    // Información personal

    val nombres: String,

    val apellidos: String,

    val documento: String,

    val telefono: String,

    val correo: String,

    // Información laboral

    val cargo: String,

    val area: String,

    val fechaContratacion: String,

    // Remuneración mensual expresada en soles

    val sueldoMensual: Double,

    // Estado del trabajador

    val estadoLaboral: EstadoLaboral = EstadoLaboral.ACTIVO
)
