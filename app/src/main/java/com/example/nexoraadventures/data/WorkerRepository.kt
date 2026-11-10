package com.example.nexoraadventures.data

import android.content.Context
import com.example.nexoraadventures.model.EstadoLaboral
import com.example.nexoraadventures.model.Worker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio de trabajadores de NEXORA ADVENTURES.
 *
 * Coordina las operaciones entre la aplicación
 * y Room Database.
 */
class WorkerRepository private constructor(
    private val workerDao: WorkerDao
) {

    // ==========================================
    // 1. OBSERVAR TRABAJADORES
    // ==========================================

    fun observarTrabajadores(): Flow<List<Worker>> {

        return workerDao.obtenerTodos().map { entidades ->

            entidades.map { entidad ->
                entidad.toWorker()
            }
        }
    }

    // ==========================================
    // 2. BUSCAR POR IDENTIFICADOR
    // ==========================================

    suspend fun buscarPorId(
        id: Int
    ): Worker? {

        return workerDao.buscarPorId(id)?.toWorker()
    }

    // ==========================================
    // 3. VERIFICAR DOCUMENTO
    // ==========================================

    suspend fun documentoRegistrado(
        documento: String,
        excluirId: Int? = null
    ): Boolean {

        val existente = workerDao.buscarPorDocumento(
            documento.trim()
        )

        return existente != null &&
                existente.id != excluirId
    }

    // ==========================================
    // 4. REGISTRAR TRABAJADOR
    // ==========================================

    suspend fun registrarTrabajador(
        trabajador: Worker
    ): Int {

        val documento = trabajador.documento.trim()

        if (documentoRegistrado(documento)) {

            throw IllegalArgumentException(
                "Ya existe un trabajador con ese documento."
            )
        }

        val entidad = trabajador.toWorkerEntity().copy(
            id = 0,
            documento = documento
        )

        return workerDao.insertar(entidad).toInt()
    }

    // ==========================================
    // 5. ACTUALIZAR TRABAJADOR
    // ==========================================

    suspend fun actualizarTrabajador(
        trabajador: Worker
    ): Boolean {

        val documento = trabajador.documento.trim()

        if (
            documentoRegistrado(
                documento,
                excluirId = trabajador.id
            )
        ) {

            throw IllegalArgumentException(
                "El documento ya pertenece a otro trabajador."
            )
        }

        val entidad = trabajador.toWorkerEntity().copy(
            documento = documento
        )

        return workerDao.actualizar(entidad) > 0
    }

    // ==========================================
    // 6. ELIMINAR TRABAJADOR
    // ==========================================

    suspend fun eliminarTrabajador(
        id: Int
    ): Boolean {

        return workerDao.eliminarPorId(id) > 0
    }

    // ==========================================
    // 7. CONTAR TRABAJADORES
    // ==========================================

    suspend fun contarTrabajadores(): Int {

        return workerDao.contarTrabajadores()
    }

    // ==========================================
    // 8. CALCULAR PLANILLA MENSUAL
    // ==========================================

    suspend fun calcularPlanillaMensual(): Double {

        return workerDao.calcularPlanillaMensual()
    }

    // ==========================================
    // 9. CONTAR POR ESTADO
    // ==========================================

    suspend fun contarPorEstado(
        estado: EstadoLaboral
    ): Int {

        return workerDao.contarPorEstado(
            estado.name
        )
    }

    // ==========================================
    // 10. ENTITY A MODEL
    // ==========================================

    private fun WorkerEntity.toWorker(): Worker {

        val estado = runCatching {

            EstadoLaboral.valueOf(estadoLaboral)

        }.getOrDefault(
            EstadoLaboral.ACTIVO
        )

        return Worker(

            id = id,

            nombres = nombres,

            apellidos = apellidos,

            documento = documento,

            telefono = telefono,

            correo = correo,

            cargo = cargo,

            area = area,

            fechaContratacion = fechaContratacion,

            sueldoMensual = sueldoMensual,

            estadoLaboral = estado
        )
    }

    // ==========================================
    // 11. MODEL A ENTITY
    // ==========================================

    private fun Worker.toWorkerEntity(): WorkerEntity {

        return WorkerEntity(

            id = id,

            nombres = nombres,

            apellidos = apellidos,

            documento = documento,

            telefono = telefono,

            correo = correo,

            cargo = cargo,

            area = area,

            fechaContratacion = fechaContratacion,

            sueldoMensual = sueldoMensual,

            estadoLaboral = estadoLaboral.name
        )
    }

    // ==========================================
    // 12. OBTENER REPOSITORIO
    // ==========================================

    companion object {

        fun obtenerRepositorio(
            context: Context
        ): WorkerRepository {

            val database =
                ReservationDatabase.obtenerBaseDeDatos(
                    context.applicationContext
                )

            return WorkerRepository(
                database.workerDao()
            )
        }
    }
}


