package com.example.nexoraadventures

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.example.nexoraadventures.data.ReservationRepository
import com.example.nexoraadventures.model.EstadoReserva
import com.example.nexoraadventures.model.Reservation

import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

import kotlinx.coroutines.launch

/**
 * Pantalla independiente para registrar y editar reservas.
 *
 * Funcionalidades:
 * - Registrar reservas.
 * - Editar reservas.
 * - Precargar destinos turísticos.
 * - Recibir coordenadas geográficas.
 * - Guardar coordenadas en Room Database.
 * - Conservar las coordenadas al editar.
 * - Validar la información del formulario.
 */
class ReservationFormActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "NEXORA_FORMULARIO"

        const val EXTRA_RESERVATION_ID =
            "extra_reservation_id"

        const val EXTRA_DESTINATION_NAME =
            "extra_destination_name"

        const val EXTRA_MEETING_POINT =
            "extra_meeting_point"

        const val EXTRA_LATITUDE =
            "extra_reservation_latitude"

        const val EXTRA_LONGITUDE =
            "extra_reservation_longitude"

        private const val ID_NUEVA_RESERVA = -1
    }

    // ==========================================
    // 1. REPOSITORIO
    // ==========================================

    private val reservationRepository by lazy {

        ReservationRepository.obtenerRepositorio(
            applicationContext
        )
    }

    // ==========================================
    // 2. COMPONENTES
    // ==========================================

    private lateinit var toolbar: MaterialToolbar

    private lateinit var etNombreCliente: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDestino: EditText
    private lateinit var etFechaViaje: EditText
    private lateinit var etCantidadPersonas: EditText
    private lateinit var etPuntoEncuentro: EditText

    private lateinit var spinnerEstadoReserva: Spinner

    private lateinit var btnCancelarReserva: MaterialButton
    private lateinit var btnGuardarReserva: MaterialButton

    private val estados = EstadoReserva.values().toList()

    private var reservaExistente: Reservation? = null

    // ==========================================
    // 3. COORDENADAS RECIBIDAS
    // ==========================================

    private var latitudSeleccionada: Double = 0.0

    private var longitudSeleccionada: Double = 0.0

    private var coordenadasRecibidas: Boolean = false

    // ==========================================
    // 4. CREAR ACTIVIDAD
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Log.d(
            TAG,
            "Iniciando ReservationFormActivity."
        )

        setContentView(
            R.layout.activity_reservation_form
        )

        inicializarComponentes()

        configurarToolbar()

        configurarSelectorEstado()

        configurarBotones()

        cargarReservaExistente()

        Log.d(
            TAG,
            "Formulario inicializado correctamente."
        )
    }

    // ==========================================
    // 5. INICIALIZAR COMPONENTES
    // ==========================================

    private fun inicializarComponentes() {

        toolbar = findViewById(
            R.id.toolbarFormulario
        )

        etNombreCliente = findViewById(
            R.id.etNombreCliente
        )

        etTelefono = findViewById(
            R.id.etTelefono
        )

        etDestino = findViewById(
            R.id.etDestino
        )

        etFechaViaje = findViewById(
            R.id.etFechaViaje
        )

        etCantidadPersonas = findViewById(
            R.id.etCantidadPersonas
        )

        etPuntoEncuentro = findViewById(
            R.id.etPuntoEncuentro
        )

        spinnerEstadoReserva = findViewById(
            R.id.spinnerEstadoReserva
        )

        btnCancelarReserva = findViewById(
            R.id.btnCancelarReserva
        )

        btnGuardarReserva = findViewById(
            R.id.btnGuardarReserva
        )

        Log.d(
            TAG,
            "Componentes vinculados correctamente."
        )
    }

    // ==========================================
    // 6. CONFIGURAR TOOLBAR
    // ==========================================

    private fun configurarToolbar() {

        val idReserva = intent.getIntExtra(
            EXTRA_RESERVATION_ID,
            ID_NUEVA_RESERVA
        )

        toolbar.title = if (
            idReserva == ID_NUEVA_RESERVA
        ) {
            "Nueva reserva"
        } else {
            "Editar reserva"
        }

        toolbar.setNavigationIcon(
            android.R.drawable.ic_menu_close_clear_cancel
        )

        toolbar.setNavigationOnClickListener {

            finish()
        }
    }

    // ==========================================
    // 7. CONFIGURAR ESTADOS
    // ==========================================

    private fun configurarSelectorEstado() {

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados.map {
                it.descripcion
            }
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerEstadoReserva.adapter = adaptador

        spinnerEstadoReserva.setSelection(
            estados.indexOf(
                EstadoReserva.PENDIENTE
            ).coerceAtLeast(0)
        )
    }

    // ==========================================
    // 8. CARGAR RESERVA EXISTENTE
    // ==========================================

    private fun cargarReservaExistente() {

        val idReserva = intent.getIntExtra(
            EXTRA_RESERVATION_ID,
            ID_NUEVA_RESERVA
        )

        // ======================================
        // MODO NUEVA RESERVA
        // ======================================

        if (idReserva == ID_NUEVA_RESERVA) {

            btnGuardarReserva.text = "REGISTRAR RESERVA"

            cargarDestinoPreseleccionado()

            Log.d(
                TAG,
                "Modo registro nuevo."
            )

            return
        }

        // ======================================
        // MODO EDICIÓN
        // ======================================

        btnGuardarReserva.isEnabled = false

        btnGuardarReserva.text = "CARGANDO..."

        lifecycleScope.launch {

            try {

                val reserva =
                    reservationRepository.buscarReserva(
                        idReserva
                    )

                if (reserva == null) {

                    Log.e(
                        TAG,
                        "No se encontró la reserva ID: $idReserva"
                    )

                    Toast.makeText(
                        this@ReservationFormActivity,
                        "No se encontró la reserva seleccionada.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@launch
                }

                reservaExistente = reserva

                // Llenar campos.

                etNombreCliente.setText(
                    reserva.clienteNombre
                )

                etTelefono.setText(
                    reserva.telefono
                )

                etDestino.setText(
                    reserva.destino
                )

                etFechaViaje.setText(
                    reserva.fechaViaje
                )

                etCantidadPersonas.setText(
                    reserva.cantidadPersonas.toString()
                )

                etPuntoEncuentro.setText(
                    reserva.puntoEncuentro
                )

                spinnerEstadoReserva.setSelection(
                    estados.indexOf(
                        reserva.estadoReserva
                    ).coerceAtLeast(0)
                )

                // Recuperar coordenadas existentes.

                latitudSeleccionada = reserva.latitud

                longitudSeleccionada = reserva.longitud

                coordenadasRecibidas =
                    coordenadasValidas(
                        latitudSeleccionada,
                        longitudSeleccionada
                    )

                btnGuardarReserva.text = "GUARDAR CAMBIOS"

                btnGuardarReserva.isEnabled = true

                Log.d(
                    TAG,
                    "Reserva cargada desde Room: ${reserva.id}"
                )

                Log.d(
                    TAG,
                    "Coordenadas recuperadas: " +
                            "$latitudSeleccionada, $longitudSeleccionada"
                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error al cargar reserva desde Room.",
                    e
                )

                Toast.makeText(
                    this@ReservationFormActivity,
                    "Error al cargar la reserva.",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
    }

    // ==========================================
    // 9. PRECARGAR DESTINO Y COORDENADAS
    // ==========================================

    private fun cargarDestinoPreseleccionado() {

        val nombreDestino = intent.getStringExtra(
            EXTRA_DESTINATION_NAME
        ).orEmpty().trim()

        val puntoEncuentro = intent.getStringExtra(
            EXTRA_MEETING_POINT
        ).orEmpty().trim()

        val latitud = intent.getDoubleExtra(
            EXTRA_LATITUDE,
            0.0
        )

        val longitud = intent.getDoubleExtra(
            EXTRA_LONGITUDE,
            0.0
        )

        // Precargar nombre del destino.

        if (nombreDestino.isNotEmpty()) {

            etDestino.setText(
                nombreDestino
            )

            Log.d(
                TAG,
                "Destino precargado: $nombreDestino"
            )
        }

        // Precargar punto de encuentro.

        if (puntoEncuentro.isNotEmpty()) {

            etPuntoEncuentro.setText(
                puntoEncuentro
            )

            Log.d(
                TAG,
                "Punto de encuentro precargado: $puntoEncuentro"
            )
        }

        // Validar coordenadas recibidas.

        if (
            coordenadasValidas(
                latitud,
                longitud
            )
        ) {

            latitudSeleccionada = latitud

            longitudSeleccionada = longitud

            coordenadasRecibidas = true

            Log.d(
                TAG,
                "Coordenadas recibidas correctamente: " +
                        "$latitudSeleccionada, $longitudSeleccionada"
            )

        } else {

            latitudSeleccionada = 0.0

            longitudSeleccionada = 0.0

            coordenadasRecibidas = false

            Log.d(
                TAG,
                "La reserva no recibió coordenadas geográficas."
            )
        }

        if (
            nombreDestino.isNotEmpty() ||
            puntoEncuentro.isNotEmpty()
        ) {

            Toast.makeText(
                this,
                "Destino seleccionado correctamente.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ==========================================
    // 10. VALIDAR COORDENADAS
    // ==========================================

    private fun coordenadasValidas(
        latitud: Double,
        longitud: Double
    ): Boolean {

        return latitud.isFinite() &&
                longitud.isFinite() &&
                latitud in -90.0..90.0 &&
                longitud in -180.0..180.0 &&
                (latitud != 0.0 || longitud != 0.0)
    }

    // ==========================================
    // 11. CONFIGURAR BOTONES
    // ==========================================

    private fun configurarBotones() {

        btnCancelarReserva.setOnClickListener {

            Log.d(
                TAG,
                "Se presionó CANCELAR."
            )

            finish()
        }

        btnGuardarReserva.setOnClickListener {

            Log.d(
                TAG,
                "Se presionó REGISTRAR/GUARDAR."
            )

            guardarReserva()
        }
    }

    // ==========================================
    // 12. VALIDAR Y GUARDAR RESERVA
    // ==========================================

    private fun guardarReserva() {

        if (!btnGuardarReserva.isEnabled) return

        try {

            Log.d(
                TAG,
                "Iniciando validación de datos."
            )

            // ==================================
            // OBTENER VALORES
            // ==================================

            val nombre =
                etNombreCliente.text.toString().trim()

            val telefono =
                etTelefono.text.toString().trim()

            val destino =
                etDestino.text.toString().trim()

            val fecha =
                etFechaViaje.text.toString().trim()

            val personas =
                etCantidadPersonas.text.toString()
                    .trim()
                    .toIntOrNull()

            val puntoEncuentro =
                etPuntoEncuentro.text.toString().trim()

            val telefonoNormalizado =
                telefono.filter {
                    it.isDigit()
                }

            // ==================================
            // VALIDAR NOMBRE
            // ==================================

            if (nombre.isEmpty()) {

                mostrarError(
                    etNombreCliente,
                    "Ingresa el nombre del cliente."
                )

                return
            }

            // ==================================
            // VALIDAR TELÉFONO
            // ==================================

            if (telefonoNormalizado.length !in 7..15) {

                mostrarError(
                    etTelefono,
                    "Ingresa un teléfono válido."
                )

                return
            }

            // ==================================
            // VALIDAR DESTINO
            // ==================================

            if (destino.isEmpty()) {

                mostrarError(
                    etDestino,
                    "Ingresa el destino turístico."
                )

                return
            }

            // ==================================
            // VALIDAR FECHA
            // ==================================

            if (!validarFecha(fecha)) {

                mostrarError(
                    etFechaViaje,
                    "Usa una fecha válida con formato DD/MM/AAAA."
                )

                return
            }

            // ==================================
            // VALIDAR PERSONAS
            // ==================================

            if (personas == null || personas <= 0) {

                mostrarError(
                    etCantidadPersonas,
                    "La cantidad de personas debe ser mayor que cero."
                )

                return
            }

            // ==================================
            // VALIDAR PUNTO DE ENCUENTRO
            // ==================================

            if (puntoEncuentro.isEmpty()) {

                mostrarError(
                    etPuntoEncuentro,
                    "Ingresa el punto de encuentro."
                )

                return
            }

            // ==================================
            // OBTENER ESTADO
            // ==================================

            val estadoSeleccionado =
                estados.getOrNull(
                    spinnerEstadoReserva.selectedItemPosition
                ) ?: EstadoReserva.PENDIENTE

            Log.d(
                TAG,
                "Validaciones superadas correctamente."
            )

            // ==================================
            // PREPARAR RESERVA
            // ==================================

            val reservaAnterior = reservaExistente

            val reservaAGuardar = if (
                reservaAnterior == null
            ) {

                Reservation(

                    id = 0,

                    clienteNombre = nombre,

                    telefono = telefonoNormalizado,

                    destino = destino,

                    fechaViaje = fecha,

                    cantidadPersonas = personas,

                    estadoReserva = estadoSeleccionado,

                    puntoEncuentro = puntoEncuentro,

                    latitud = if (coordenadasRecibidas) {
                        latitudSeleccionada
                    } else {
                        0.0
                    },

                    longitud = if (coordenadasRecibidas) {
                        longitudSeleccionada
                    } else {
                        0.0
                    }
                )

            } else {

                reservaAnterior.copy(

                    clienteNombre = nombre,

                    telefono = telefonoNormalizado,

                    destino = destino,

                    fechaViaje = fecha,

                    cantidadPersonas = personas,

                    estadoReserva = estadoSeleccionado,

                    puntoEncuentro = puntoEncuentro,

                    latitud = if (coordenadasRecibidas) {
                        latitudSeleccionada
                    } else {
                        reservaAnterior.latitud
                    },

                    longitud = if (coordenadasRecibidas) {
                        longitudSeleccionada
                    } else {
                        reservaAnterior.longitud
                    }
                )
            }

            // ==================================
            // GUARDAR EN ROOM
            // ==================================

            btnGuardarReserva.isEnabled = false

            btnGuardarReserva.text = "GUARDANDO..."

            lifecycleScope.launch {

                try {

                    if (reservaAnterior == null) {

                        // Insertar nueva reserva.

                        val nuevoId =
                            reservationRepository.insertarReserva(
                                reservaAGuardar
                            )

                        Log.d(
                            TAG,
                            "Reserva insertada en Room. ID=$nuevoId"
                        )

                        Log.d(
                            TAG,
                            "Coordenadas guardadas: " +
                                    "${reservaAGuardar.latitud}, " +
                                    "${reservaAGuardar.longitud}"
                        )

                        Toast.makeText(
                            this@ReservationFormActivity,
                            "Reserva registrada correctamente.",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        // Actualizar reserva existente.

                        val resultado =
                            reservationRepository.actualizarReserva(
                                reservaAGuardar
                            )

                        if (!resultado) {

                            btnGuardarReserva.isEnabled = true

                            btnGuardarReserva.text =
                                "GUARDAR CAMBIOS"

                            Toast.makeText(
                                this@ReservationFormActivity,
                                "No se pudo actualizar la reserva.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@launch
                        }

                        Log.d(
                            TAG,
                            "Reserva actualizada en Room. ID=${reservaAGuardar.id}"
                        )

                        Log.d(
                            TAG,
                            "Coordenadas conservadas o actualizadas: " +
                                    "${reservaAGuardar.latitud}, " +
                                    "${reservaAGuardar.longitud}"
                        )

                        Toast.makeText(
                            this@ReservationFormActivity,
                            "Reserva actualizada correctamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Regresar al listado.

                    finish()

                } catch (e: Exception) {

                    Log.e(
                        TAG,
                        "Error al guardar reserva en Room.",
                        e
                    )

                    btnGuardarReserva.isEnabled = true

                    btnGuardarReserva.text =
                        if (reservaAnterior == null) {
                            "REGISTRAR RESERVA"
                        } else {
                            "GUARDAR CAMBIOS"
                        }

                    Toast.makeText(
                        this@ReservationFormActivity,
                        "Error al guardar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error durante la validación.",
                e
            )

            Toast.makeText(
                this,
                "Error al procesar la reserva.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // 13. VALIDAR FECHA
    // ==========================================

    private fun validarFecha(
        valor: String
    ): Boolean {

        val formato = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).apply {

            isLenient = false
        }

        val posicion = ParsePosition(0)

        val fechaConvertida = formato.parse(
            valor,
            posicion
        )

        val resultado =
            fechaConvertida != null &&
                    posicion.index == valor.length

        Log.d(
            TAG,
            "Validación de fecha '$valor': $resultado"
        )

        return resultado
    }

    // ==========================================
    // 14. MOSTRAR ERRORES
    // ==========================================

    private fun mostrarError(
        campo: EditText,
        mensaje: String
    ) {

        Log.w(
            TAG,
            "Validación rechazada: $mensaje"
        )

        campo.error = mensaje

        campo.requestFocus()

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }
}
