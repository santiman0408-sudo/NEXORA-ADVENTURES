package com.example.nexoraadventures.data

import com.example.nexoraadventures.model.EstadoReserva
import com.example.nexoraadventures.model.Reservation

/**
 * Repositorio local de reservas turísticas.
 *
 * Contiene datos de demostración y permite:
 * - Consultar reservas.
 * - Buscar una reserva por ID.
 * - Registrar nuevas reservas.
 * - Actualizar reservas existentes.
 * - Eliminar reservas.
 *
 * Esta versión utiliza memoria temporal.
 * Posteriormente integraremos Room Database.
 */
object ReservationData {

    // ==========================================
    // 1. LISTA DE RESERVAS DE DEMOSTRACIÓN
    // ==========================================

    private val reservations = mutableListOf(

        Reservation(
            id = 1,
            clienteNombre = "Carlos Mendoza",
            telefono = "999111222",
            destino = "Machu Picchu",
            fechaViaje = "20/12/2026",
            cantidadPersonas = 2,
            estadoReserva = EstadoReserva.CONFIRMADO,
            puntoEncuentro = "Aguas Calientes, Cusco"
        ),

        Reservation(
            id = 2,
            clienteNombre = "María Fernández",
            telefono = "988222333",
            destino = "Laguna Humantay",
            fechaViaje = "27/12/2026",
            cantidadPersonas = 3,
            estadoReserva = EstadoReserva.PENDIENTE,
            puntoEncuentro = "Mollepata, Cusco"
        ),

        Reservation(
            id = 3,
            clienteNombre = "José Ramírez",
            telefono = "977333444",
            destino = "Montaña de Siete Colores",
            fechaViaje = "30/12/2026",
            cantidadPersonas = 4,
            estadoReserva = EstadoReserva.FINALIZADO,
            puntoEncuentro = "Cusipata, Cusco"
        )
    )

    // ==========================================
    // 2. GENERADOR DE IDENTIFICADORES
    // ==========================================

    private var siguienteId: Int =
        (reservations.maxOfOrNull { it.id } ?: 0) + 1

    // ==========================================
    // 3. OBTENER TODAS LAS RESERVAS
    // ==========================================

    fun obtenerReservas(): List<Reservation> {
        return reservations.toList()
    }

    // ==========================================
    // 4. BUSCAR RESERVA POR IDENTIFICADOR
    // ==========================================

    fun buscarReserva(id: Int): Reservation? {

        return reservations.find {
            it.id == id
        }
    }

    // ==========================================
    // 5. REGISTRAR NUEVA RESERVA
    // ==========================================

    fun agregarReserva(
        reserva: Reservation
    ): Reservation {

        val nuevaReserva = reserva.copy(
            id = siguienteId++
        )

        reservations.add(nuevaReserva)

        return nuevaReserva
    }

    // ==========================================
    // 6. ACTUALIZAR RESERVA
    // ==========================================

    fun actualizarReserva(
        reservaActualizada: Reservation
    ): Boolean {

        val posicion = reservations.indexOfFirst {
            it.id == reservaActualizada.id
        }

        if (posicion == -1) {
            return false
        }

        reservations[posicion] = reservaActualizada

        return true
    }

    // ==========================================
    // 7. ELIMINAR RESERVA
    // ==========================================

    fun eliminarReserva(id: Int): Boolean {

        return reservations.removeAll {
            it.id == id
        }
    }

    // ==========================================
    // 8. CONTAR RESERVAS
    // ==========================================

    fun contarReservas(): Int {
        return reservations.size
    }

    // ==========================================
    // 9. CONTAR POR ESTADO
    // ==========================================

    fun contarPorEstado(
        estado: EstadoReserva
    ): Int {

        return reservations.count {
            it.estadoReserva == estado
        }
    }
}
