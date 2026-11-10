package com.example.nexoraadventures.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room de NEXORA ADVENTURES.
 *
 * Representa una reserva turística almacenada
 * en la base de datos local SQLite.
 *
 * Cada objeto corresponde a un registro
 * de la tabla reservations.
 */
@Entity(tableName = "reservations")
data class ReservationEntity(

    // ==========================================
    // 1. IDENTIFICADOR
    // ==========================================

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ==========================================
    // 2. INFORMACIÓN DEL CLIENTE
    // ==========================================

    val clienteNombre: String,

    val telefono: String,

    // ==========================================
    // 3. INFORMACIÓN DEL DESTINO
    // ==========================================

    val destino: String,

    val fechaViaje: String,

    val cantidadPersonas: Int,

    // ==========================================
    // 4. ESTADO DE LA RESERVA
    // ==========================================

    // Se almacena como texto:
    // PENDIENTE, CONFIRMADO o FINALIZADO.

    val estadoReserva: String,

    // ==========================================
    // 5. PUNTO DE ENCUENTRO
    // ==========================================

    val puntoEncuentro: String,

    // ==========================================
    // 6. COORDENADAS GEOGRÁFICAS
    // ==========================================

    val latitud: Double = 0.0,

    val longitud: Double = 0.0
)
