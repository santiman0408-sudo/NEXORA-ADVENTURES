package com.example.nexoraadventures.data

import android.content.Context
import com.example.nexoraadventures.model.EstadoReserva
import com.example.nexoraadventures.model.Reservation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio de reservas de NEXORA ADVENTURES.
 *
 * Administra las operaciones entre la aplicación
 * y Room Database.
 */
class ReservationRepository private constructor(
    private val reservationDao: ReservationDao
) {

    // ==========================================
    // 1. OBSERVAR TODAS LAS RESERVAS
    // ==========================================

    fun observarReservas(): Flow<List<Reservation>> {

        return reservationDao.obtenerTodas().map { entidades ->

            entidades.map { entidad ->
                entidad.toReservation()
            }
        }
    }

    // ==========================================
    // 2. BUSCAR RESERVA POR ID
    // ==========================================

    suspend fun buscarReserva(
        id: Int
    ): Reservation? {

        return reservationDao.buscarPorId(id)?.toReservation()
    }

    // ==========================================
    // 3. INSERTAR RESERVA
    // ==========================================

    suspend fun insertarReserva(
        reserva: Reservation
    ): Int {

        val entidad = reserva.toReservationEntity().copy(
            id = 0
        )

        return reservationDao.insertar(entidad).toInt()
    }

    // ==========================================
    // 4. ACTUALIZAR RESERVA
    // ==========================================

    suspend fun actualizarReserva(
        reserva: Reservation
    ): Boolean {

        val entidad = reserva.toReservationEntity()

        return reservationDao.actualizar(entidad) > 0
    }

    // ==========================================
    // 5. ELIMINAR RESERVA
    // ==========================================

    suspend fun eliminarReserva(
        id: Int
    ): Boolean {

        return reservationDao.eliminarPorId(id) > 0
    }

    // ==========================================
    // 6. CONTAR RESERVAS
    // ==========================================

    suspend fun contarReservas(): Int {

        return reservationDao.contarReservas()
    }

    // ==========================================
    // 7. CONTAR POR ESTADO
    // ==========================================

    suspend fun contarPorEstado(
        estado: EstadoReserva
    ): Int {

        return reservationDao.contarPorEstado(
            estado.name
        )
    }

    // ==========================================
    // 8. INICIALIZAR DATOS DE DEMOSTRACIÓN
    // ==========================================

    /**
     * Carga las reservas iniciales una sola vez.
     *
     * No vuelve a cargar los registros cuando
     * el usuario elimina todas las reservas.
     */
    suspend fun inicializarDatosDemoSiEsNecesario(
        context: Context,
        reservasIniciales: List<Reservation>
    ) {

        val preferencias = context.applicationContext
            .getSharedPreferences(
                NOMBRE_PREFERENCIAS,
                Context.MODE_PRIVATE
            )

        val yaInicializado = preferencias.getBoolean(
            CLAVE_DATOS_INICIALIZADOS,
            false
        )

        if (yaInicializado) {
            return
        }

        val cantidadActual = reservationDao.contarReservas()

        if (cantidadActual == 0 && reservasIniciales.isNotEmpty()) {

            val entidades = reservasIniciales.map { reserva ->

                reserva.toReservationEntity().copy(
                    id = 0
                )
            }

            reservationDao.insertarTodos(entidades)
        }

        preferencias.edit()
            .putBoolean(CLAVE_DATOS_INICIALIZADOS, true)
            .apply()
    }

    // ==========================================
    // 9. CONVERTIR ENTITY A MODEL
    // ==========================================

    private fun ReservationEntity.toReservation(): Reservation {

        val estado = runCatching {

            EstadoReserva.valueOf(estadoReserva)

        }.getOrDefault(
            EstadoReserva.PENDIENTE
        )

        return Reservation(

            id = id,

            clienteNombre = clienteNombre,

            telefono = telefono,

            destino = destino,

            fechaViaje = fechaViaje,

            cantidadPersonas = cantidadPersonas,

            estadoReserva = estado,

            puntoEncuentro = puntoEncuentro,

            latitud = latitud,

            longitud = longitud
        )
    }

    // ==========================================
    // 10. CONVERTIR MODEL A ENTITY
    // ==========================================

    private fun Reservation.toReservationEntity(): ReservationEntity {

        return ReservationEntity(

            id = id,

            clienteNombre = clienteNombre,

            telefono = telefono,

            destino = destino,

            fechaViaje = fechaViaje,

            cantidadPersonas = cantidadPersonas,

            estadoReserva = estadoReserva.name,

            puntoEncuentro = puntoEncuentro,

            latitud = latitud,

            longitud = longitud
        )
    }

    // ==========================================
    // 11. OBTENER REPOSITORIO
    // ==========================================

    companion object {

        private const val NOMBRE_PREFERENCIAS =
            "nexora_adventures_preferences"

        private const val CLAVE_DATOS_INICIALIZADOS =
            "reservas_demo_inicializadas"

        fun obtenerRepositorio(
            context: Context
        ): ReservationRepository {

            val database =
                ReservationDatabase.obtenerBaseDeDatos(context)

            return ReservationRepository(
                database.reservationDao()
            )
        }
    }
}


