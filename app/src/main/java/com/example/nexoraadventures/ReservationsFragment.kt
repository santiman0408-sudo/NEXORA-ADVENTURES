package com.example.nexoraadventures

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoraadventures.adapter.ReservationAdapter
import com.example.nexoraadventures.data.ReservationData
import com.example.nexoraadventures.data.ReservationRepository
import com.example.nexoraadventures.model.Reservation
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Gestión de reservas de NEXORA ADVENTURES.
 *
 * Funcionalidades:
 * - Consultar reservas desde Room.
 * - Registrar reservas.
 * - Editar reservas.
 * - Eliminar reservas.
 * - Observar cambios automáticamente.
 */
class ReservationsFragment : Fragment() {

    companion object {
        private const val TAG = "NEXORA_RESERVAS"
    }

    // ==========================================
    // 1. DECLARAR COMPONENTES
    // ==========================================

    private var recyclerView: RecyclerView? = null

    private var txtSinReservas: TextView? = null

    private var fabNuevaReserva: FloatingActionButton? = null

    private var reservationAdapter: ReservationAdapter? = null

    private var reservationRepository: ReservationRepository? = null

    private var contenedorPrincipal: ViewGroup? = null

    private var dialogoEliminacion: View? = null

    // ==========================================
    // 2. CREAR VISTA
    // ==========================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "Creando ReservationsFragment.")

        return inflater.inflate(
            R.layout.fragment_reservations,
            container,
            false
        )
    }

    // ==========================================
    // 3. INICIALIZAR COMPONENTES
    // ==========================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        contenedorPrincipal = view as? ViewGroup

        recyclerView = view.findViewById(
            R.id.rvReservations
        )

        txtSinReservas = view.findViewById(
            R.id.txtSinReservas
        )

        fabNuevaReserva = view.findViewById(
            R.id.fabNuevaReserva
        )

        // Crear repositorio Room.

        reservationRepository =
            ReservationRepository.obtenerRepositorio(
                requireContext().applicationContext
            )

        // Configurar adaptador.

        reservationAdapter = ReservationAdapter(

            onEditar = { reserva ->

                Log.d(
                    TAG,
                    "Solicitud de edición: ${reserva.id}"
                )

                abrirFormulario(reserva)
            },

            onEliminar = { reserva ->

                Log.d(
                    TAG,
                    "Solicitud de eliminación: ${reserva.id}"
                )

                confirmarEliminacion(reserva)
            }
        )

        // Configurar RecyclerView.

        recyclerView?.apply {

            layoutManager = LinearLayoutManager(
                requireContext()
            )

            adapter = reservationAdapter
        }

        // Configurar botón nueva reserva.

        fabNuevaReserva?.setOnClickListener {

            Log.d(TAG, "Botón nueva reserva presionado.")

            abrirFormulario()
        }

        // Iniciar carga y observación de Room.

        iniciarObservacionReservas()
    }

    // ==========================================
    // 4. INICIALIZAR Y OBSERVAR ROOM
    // ==========================================

    private fun iniciarObservacionReservas() {

        val repositorio = reservationRepository ?: return

        val contextoAplicacion =
            requireContext().applicationContext

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                // Cargar datos iniciales solo una vez.

                repositorio.inicializarDatosDemoSiEsNecesario(
                    contextoAplicacion,
                    ReservationData.obtenerReservas()
                )

                Log.d(
                    TAG,
                    "Inicialización de datos de demostración completada."
                )

                // Observar cambios de Room.

                repositorio.observarReservas().collect { reservas ->

                    mostrarReservas(reservas)
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error al consultar las reservas de Room.",
                    e
                )

                if (isAdded) {

                    Toast.makeText(
                        requireContext(),
                        "No se pudieron cargar las reservas.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // 5. MOSTRAR RESERVAS
    // ==========================================

    private fun mostrarReservas(
        reservas: List<Reservation>
    ) {

        if (!isAdded) return

        reservationAdapter?.actualizarLista(reservas)

        if (reservas.isEmpty()) {

            recyclerView?.visibility = View.GONE

            txtSinReservas?.visibility = View.VISIBLE

        } else {

            recyclerView?.visibility = View.VISIBLE

            txtSinReservas?.visibility = View.GONE
        }

        Log.d(
            TAG,
            "Room notificó ${reservas.size} reservas."
        )
    }

    // ==========================================
    // 6. ABRIR FORMULARIO
    // ==========================================

    private fun abrirFormulario(
        reserva: Reservation? = null
    ) {

        if (!isAdded) return

        try {

            val intent = Intent(
                requireContext(),
                ReservationFormActivity::class.java
            )

            if (reserva != null) {

                intent.putExtra(
                    ReservationFormActivity.EXTRA_RESERVATION_ID,
                    reserva.id
                )

                Log.d(
                    TAG,
                    "Abriendo edición de reserva ${reserva.id}."
                )

            } else {

                Log.d(
                    TAG,
                    "Abriendo nuevo formulario."
                )
            }

            startActivity(intent)

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error al abrir formulario.",
                e
            )

            Toast.makeText(
                requireContext(),
                "No se pudo abrir el formulario.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ==========================================
    // 7. MOSTRAR CONFIRMACIÓN DE ELIMINACIÓN
    // ==========================================

    private fun confirmarEliminacion(
        reserva: Reservation
    ) {

        if (!isAdded) return

        if (dialogoEliminacion?.parent != null) {

            Log.w(
                TAG,
                "Ya existe una confirmación abierta."
            )

            return
        }

        try {

            Log.d(
                TAG,
                "Creando confirmación para ${reserva.clienteNombre}."
            )

            // Crear capa oscura.

            val overlay = FrameLayout(
                requireContext()
            ).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setBackgroundColor(
                    Color.parseColor("#99000000")
                )

                isClickable = true

                isFocusable = true
            }

            // Cargar XML personalizado.

            val contenido = LayoutInflater.from(
                requireContext()
            ).inflate(
                R.layout.dialog_confirm_delete,
                overlay,
                false
            )

            // Aplicar estilo visual.

            val radio = 18f *
                    resources.displayMetrics.density

            contenido.background = GradientDrawable().apply {

                shape = GradientDrawable.RECTANGLE

                cornerRadius = radio

                setColor(Color.WHITE)
            }

            contenido.elevation =
                16f * resources.displayMetrics.density

            contenido.isClickable = true

            // ==================================
            // CONECTAR COMPONENTES
            // ==================================

            val txtTitulo =
                contenido.findViewById<TextView>(
                    R.id.txtTituloEliminar
                )

            val txtMensaje =
                contenido.findViewById<TextView>(
                    R.id.txtMensajeEliminar
                )

            val btnCancelar =
                contenido.findViewById<MaterialButton>(
                    R.id.btnCancelarEliminar
                )

            val btnConfirmar =
                contenido.findViewById<MaterialButton>(
                    R.id.btnConfirmarEliminar
                )

            checkNotNull(txtTitulo) {
                "No se encontró txtTituloEliminar."
            }

            checkNotNull(txtMensaje) {
                "No se encontró txtMensajeEliminar."
            }

            checkNotNull(btnCancelar) {
                "No se encontró btnCancelarEliminar."
            }

            checkNotNull(btnConfirmar) {
                "No se encontró btnConfirmarEliminar."
            }

            // ==================================
            // CONFIGURAR INFORMACIÓN
            // ==================================

            txtTitulo.text = "Eliminar reserva"

            txtMensaje.text =
                "¿Deseas eliminar la reserva de ${reserva.clienteNombre}?"

            // ==================================
            // BOTÓN CANCELAR
            // ==================================

            btnCancelar.isEnabled = true

            btnCancelar.setOnClickListener {

                Log.d(
                    TAG,
                    "CANCELAR presionado. ID=${reserva.id}"
                )

                cerrarConfirmacion(overlay)

                Toast.makeText(
                    requireContext(),
                    "Eliminación cancelada.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // ==================================
            // BOTÓN ELIMINAR
            // ==================================

            btnConfirmar.isEnabled = true

            btnConfirmar.setOnClickListener {

                Log.d(
                    TAG,
                    "ELIMINAR presionado. ID=${reserva.id}"
                )

                btnConfirmar.isEnabled = false

                // Ejecutar eliminación en Room.

                viewLifecycleOwner.lifecycleScope.launch {

                    try {

                        val eliminada =
                            reservationRepository?.eliminarReserva(
                                reserva.id
                            ) ?: false

                        if (eliminada) {

                            cerrarConfirmacion(overlay)

                            Toast.makeText(
                                requireContext(),
                                "Reserva eliminada correctamente.",
                                Toast.LENGTH_LONG
                            ).show()

                            Log.d(
                                TAG,
                                "Reserva ${reserva.id} eliminada de Room."
                            )

                            // No necesitamos actualizar manualmente.
                            // Flow notificará el cambio.

                        } else {

                            btnConfirmar.isEnabled = true

                            Toast.makeText(
                                requireContext(),
                                "No se encontró la reserva.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } catch (e: Exception) {

                        btnConfirmar.isEnabled = true

                        Log.e(
                            TAG,
                            "Error al eliminar reserva de Room.",
                            e
                        )

                        Toast.makeText(
                            requireContext(),
                            "Error al eliminar la reserva.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // ==================================
            // AGREGAR CAPA A LA PANTALLA
            // ==================================

            val anchoPantalla =
                resources.displayMetrics.widthPixels

            val parametrosContenido =
                FrameLayout.LayoutParams(
                    (anchoPantalla * 0.88f).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )

            overlay.addView(
                contenido,
                parametrosContenido
            )

            val padre = contenedorPrincipal

            checkNotNull(padre) {
                "No existe el contenedor principal."
            }

            dialogoEliminacion = overlay

            padre.addView(
                overlay,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            overlay.bringToFront()

            Log.d(
                TAG,
                "Confirmación visible con botones conectados."
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error al mostrar confirmación.",
                e
            )

            Toast.makeText(
                requireContext(),
                "No se pudo mostrar la confirmación.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // 8. CERRAR CONFIRMACIÓN
    // ==========================================

    private fun cerrarConfirmacion(
        overlay: View
    ) {

        val padre = overlay.parent as? ViewGroup

        padre?.removeView(overlay)

        if (dialogoEliminacion === overlay) {

            dialogoEliminacion = null
        }

        Log.d(
            TAG,
            "Confirmación cerrada."
        )
    }

    // ==========================================
    // 9. LIBERAR COMPONENTES
    // ==========================================

    override fun onDestroyView() {

        dialogoEliminacion?.let {
            cerrarConfirmacion(it)
        }

        dialogoEliminacion = null

        fabNuevaReserva = null

        recyclerView?.adapter = null

        recyclerView = null

        txtSinReservas = null

        reservationAdapter = null

        reservationRepository = null

        contenedorPrincipal = null

        super.onDestroyView()
    }
}
