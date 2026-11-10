package com.example.nexoraadventures.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO de trabajadores de NEXORA ADVENTURES.
 *
 * Proporciona acceso a la tabla workers.
 */
@Dao
interface WorkerDao {

    // ==========================================
    // 1. CONSULTAR TODOS LOS TRABAJADORES
    // ==========================================

    @Query(
        """
        SELECT * FROM workers
        ORDER BY apellidos ASC, nombres ASC
        """
    )
    fun obtenerTodos(): Flow<List<WorkerEntity>>

    // ==========================================
    // 2. BUSCAR POR IDENTIFICADOR
    // ==========================================

    @Query(
        "SELECT * FROM workers WHERE id = :id LIMIT 1"
    )
    suspend fun buscarPorId(
        id: Int
    ): WorkerEntity?

    // ==========================================
    // 3. BUSCAR POR DOCUMENTO
    // ==========================================

    @Query(
        "SELECT * FROM workers WHERE documento = :documento LIMIT 1"
    )
    suspend fun buscarPorDocumento(
        documento: String
    ): WorkerEntity?

    // ==========================================
    // 4. INSERTAR TRABAJADOR
    // ==========================================

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertar(
        trabajador: WorkerEntity
    ): Long

    // ==========================================
    // 5. ACTUALIZAR TRABAJADOR
    // ==========================================

    @Update
    suspend fun actualizar(
        trabajador: WorkerEntity
    ): Int

    // ==========================================
    // 6. ELIMINAR TRABAJADOR
    // ==========================================

    @Query(
        "DELETE FROM workers WHERE id = :id"
    )
    suspend fun eliminarPorId(
        id: Int
    ): Int

    // ==========================================
    // 7. CONTAR TRABAJADORES
    // ==========================================

    @Query(
        "SELECT COUNT(*) FROM workers"
    )
    suspend fun contarTrabajadores(): Int

    // ==========================================
    // 8. CALCULAR PLANILLA MENSUAL
    // ==========================================

    @Query(
        """
        SELECT COALESCE(SUM(sueldoMensual), 0.0)
        FROM workers
        WHERE estadoLaboral = 'ACTIVO'
        """
    )
    suspend fun calcularPlanillaMensual(): Double

    // ==========================================
    // 9. CONTAR POR ESTADO
    // ==========================================

    @Query(
        """
        SELECT COUNT(*) FROM workers
        WHERE estadoLaboral = :estado
        """
    )
    suspend fun contarPorEstado(
        estado: String
    ): Int
}


