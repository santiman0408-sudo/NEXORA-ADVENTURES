package com.example.nexoraadventures.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO de reservas de NEXORA ADVENTURES.
 *
 * Controla el acceso a la tabla reservations.
 */
@Dao
interface ReservationDao {

    // ==========================================
    // 1. CONSULTAR TODAS LAS RESERVAS
    // ==========================================

    @Query(
        "SELECT * FROM reservations ORDER BY id ASC"
    )
    fun obtenerTodas(): Flow<List<ReservationEntity>>

    // ==========================================
    // 2. BUSCAR RESERVA POR ID
    // ==========================================

    @Query(
        "SELECT * FROM reservations WHERE id = :id LIMIT 1"
    )
    suspend fun buscarPorId(
        id: Int
    ): ReservationEntity?

    // ==========================================
    // 3. INSERTAR UNA RESERVA
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(
        reserva: ReservationEntity
    ): Long

    // ==========================================
    // 4. INSERTAR VARIAS RESERVAS
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarTodos(
        reservas: List<ReservationEntity>
    ): List<Long>

    // ==========================================
    // 5. ACTUALIZAR RESERVA
    // ==========================================

    @Update
    suspend fun actualizar(
        reserva: ReservationEntity
    ): Int

    // ==========================================
    // 6. ELIMINAR RESERVA
    // ==========================================

    @Query(
        "DELETE FROM reservations WHERE id = :id"
    )
    suspend fun eliminarPorId(
        id: Int
    ): Int

    // ==========================================
    // 7. CONTAR RESERVAS
    // ==========================================

    @Query(
        "SELECT COUNT(*) FROM reservations"
    )
    suspend fun contarReservas(): Int

    // ==========================================
    // 8. CONTAR POR ESTADO
    // ==========================================

    @Query(
        "SELECT COUNT(*) FROM reservations WHERE estadoReserva = :estado"
    )
    suspend fun contarPorEstado(
        estado: String
    ): Int
}
