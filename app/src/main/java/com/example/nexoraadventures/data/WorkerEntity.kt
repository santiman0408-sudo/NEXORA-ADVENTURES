package com.example.nexoraadventures.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room de trabajadores.
 *
 * Representa la tabla workers de la base de datos.
 *
 * El documento del trabajador debe ser único.
 */
@Entity(
    tableName = "workers",
    indices = [
        Index(
            value = ["documento"],
            unique = true
        )
    ]
)
data class WorkerEntity(

    // ==========================================
    // 1. IDENTIFICADOR
    // ==========================================

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ==========================================
    // 2. INFORMACIÓN PERSONAL
    // ==========================================

    val nombres: String,

    val apellidos: String,

    val documento: String,

    val telefono: String,

    val correo: String,

    // ==========================================
    // 3. INFORMACIÓN LABORAL
    // ==========================================

    val cargo: String,

    val area: String,

    val fechaContratacion: String,

    // ==========================================
    // 4. INFORMACIÓN SALARIAL
    // ==========================================

    val sueldoMensual: Double,

    // ==========================================
    // 5. ESTADO LABORAL
    // ==========================================

    val estadoLaboral: String
)


