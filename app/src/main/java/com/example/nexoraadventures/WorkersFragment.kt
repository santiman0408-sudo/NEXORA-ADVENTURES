package com.example.nexoraadventures

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoraadventures.adapter.WorkerAdapter
import com.example.nexoraadventures.data.WorkerRepository
import com.example.nexoraadventures.model.EstadoLaboral
import com.example.nexoraadventures.model.Worker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Gestión de trabajadores de NEXORA ADVENTURES.
 *
 * Funcionalidades:
 * - Consultar trabajadores desde Room.
 * - Registrar nuevos trabajadores.
 * - Editar trabajadores.
 * - Eliminar trabajadores.
 * - Calcular la planilla mensual.
 * - Actualizar automáticamente el listado.
 */
class WorkersFragment : Fragment() {

    companion object {
        private const val TAG = "NEXORA_WORKERS"
    }

    // ==========================================
    // 1. COMPONENTES
    // ==========================================

    private var recyclerView: RecyclerView? = null

    private var txtTotalTrabajadores: TextView? = null

    private var txtPlanillaMensual: TextView? = null

    private var txtSinTrabajadores: TextView? = null

    private var fabNuevoTrabajador: FloatingActionButton? = null

    private var workerAdapter: WorkerAdapter? = null

    private var workerRepository: WorkerRepository? = null

    // ==========================================
    // 2. CREAR VISTA
    // ==========================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(
            TAG,
            "Creando WorkersFragment."
        )

        return inflater.inflate(
            R.layout.fragment_workers,
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

        // ======================================
        // VINCULAR COMPONENTES
        // ======================================

        recyclerView = view.findViewById(
            R.id.rvWorkers
        )

        txtTotalTrabajadores = view.findViewById(
            R.id.txtTotalTrabajadores
        )

        txtPlanillaMensual = view.findViewById(
            R.id.txtPlanillaMensual
        )

        txtSinTrabajadores = view.findViewById(
            R.id.txtSinTrabajadores
        )

        fabNuevoTrabajador = view.findViewById(
            R.id.fabNuevoTrabajador
        )

        // ======================================
        // INICIALIZAR REPOSITORIO
        // ======================================

        workerRepository =
            WorkerRepository.obtenerRepositorio(
                requireContext().applicationContext
            )

        // ======================================
        // CONFIGURAR ADAPTADOR
        // ======================================

        workerAdapter = WorkerAdapter(

            onEditar = { trabajador ->

                Log.d(
                    TAG,
                    "Editar trabajador ID=${trabajador.id}"
                )

                abrirFormulario(trabajador)
            },

            onEliminar = { trabajador ->

                Log.d(
                    TAG,
                    "Eliminar trabajador ID=${trabajador.id}"
                )

                confirmarEliminacion(trabajador)
            }
        )

        // ======================================
        // CONFIGURAR RECYCLERVIEW
        // ======================================

        recyclerView?.apply {

            layoutManager = LinearLayoutManager(
                requireContext()
            )

            adapter = workerAdapter
        }

        // ======================================
        // CONFIGURAR BOTÓN NUEVO
        // ======================================

        fabNuevoTrabajador?.setOnClickListener {

            Log.d(
                TAG,
                "Botón nuevo trabajador presionado."
            )

            abrirFormulario()
        }

        // ======================================
        // OBSERVAR ROOM
        // ======================================

        observarTrabajadores()

        Log.d(
            TAG,
            "Módulo de trabajadores inicializado."
        )
    }

    // ==========================================
    // 4. OBSERVAR TRABAJADORES
    // ==========================================

    private fun observarTrabajadores() {

        val repositorio = workerRepository ?: return

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                repositorio.observarTrabajadores().collect { trabajadores ->

                    mostrarTrabajadores(trabajadores)
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error al consultar trabajadores.",
                    e
                )

                if (isAdded) {

                    Toast.makeText(
                        requireContext(),
                        "No se pudieron cargar los trabajadores.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // 5. MOSTRAR LISTADO E INDICADORES
    // ==========================================

    private fun mostrarTrabajadores(
        trabajadores: List<Worker>
    ) {

        if (!isAdded) return

        // Actualizar tarjetas.

        workerAdapter?.actualizarLista(
            trabajadores
        )

        // ======================================
        // TOTAL DE TRABAJADORES
        // ======================================

        txtTotalTrabajadores?.text =
            trabajadores.size.toString()

        // ======================================
        // CALCULAR PLANILLA DE ACTIVOS
        // ======================================

        val planillaMensual = trabajadores
            .filter {
                it.estadoLaboral == EstadoLaboral.ACTIVO
            }
            .sumOf {
                it.sueldoMensual
            }

        val formatoMoneda =
            NumberFormat.getCurrencyInstance(
                Locale("es", "PE")
            )

        txtPlanillaMensual?.text =
            formatoMoneda.format(planillaMensual)

        // ======================================
        // COMPROBAR LISTA VACÍA
        // ======================================

        if (trabajadores.isEmpty()) {

            recyclerView?.visibility = View.GONE

            txtSinTrabajadores?.visibility = View.VISIBLE

        } else {

            recyclerView?.visibility = View.VISIBLE

            txtSinTrabajadores?.visibility = View.GONE
        }

        Log.d(
            TAG,
            "Trabajadores: ${trabajadores.size}. Planilla activa: $planillaMensual"
        )
    }

    // ==========================================
    // 6. ABRIR FORMULARIO
    // ==========================================

    private fun abrirFormulario(
        trabajador: Worker? = null
    ) {

        if (!isAdded) return

        try {

            val intent = Intent(
                requireContext(),
                WorkerFormActivity::class.java
            )

            if (trabajador != null) {

                intent.putExtra(
                    WorkerFormActivity.EXTRA_WORKER_ID,
                    trabajador.id
                )

                Log.d(
                    TAG,
                    "Abriendo edición de trabajador ${trabajador.id}."
                )

            } else {

                Log.d(
                    TAG,
                    "Abriendo registro de trabajador."
                )
            }

            startActivity(intent)

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error al abrir formulario de trabajadores.",
                e
            )

            Toast.makeText(
                requireContext(),
                "No se pudo abrir el formulario.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // 7. CONFIRMAR ELIMINACIÓN
    // ==========================================

    private fun confirmarEliminacion(
        trabajador: Worker
    ) {

        if (!isAdded) return

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle("Eliminar trabajador")
            .setMessage(
                "¿Deseas eliminar a ${trabajador.nombres} ${trabajador.apellidos}?"
            )
            .setNegativeButton(
                "CANCELAR",
                null
            )
            .setPositiveButton(
                "ELIMINAR"
            ) { _, _ ->

                eliminarTrabajador(trabajador)
            }
            .show()

        Log.d(
            TAG,
            "Confirmación de eliminación mostrada para ID=${trabajador.id}."
        )
    }

    // ==========================================
    // 8. ELIMINAR TRABAJADOR EN ROOM
    // ==========================================

    private fun eliminarTrabajador(
        trabajador: Worker
    ) {

        val repositorio = workerRepository ?: return

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val eliminado =
                    repositorio.eliminarTrabajador(
                        trabajador.id
                    )

                if (eliminado) {

                    Log.d(
                        TAG,
                        "Trabajador eliminado de Room: ${trabajador.id}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "Trabajador eliminado correctamente.",
                        Toast.LENGTH_LONG
                    ).show()

                    // El Flow de Room actualizará
                    // las tarjetas y los indicadores.

                } else {

                    Toast.makeText(
                        requireContext(),
                        "No se encontró el trabajador.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error al eliminar trabajador.",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    "No se pudo eliminar el trabajador.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 9. LIBERAR COMPONENTES
    // ==========================================

    override fun onDestroyView() {

        recyclerView?.adapter = null

        recyclerView = null

        txtTotalTrabajadores = null

        txtPlanillaMensual = null

        txtSinTrabajadores = null

        fabNuevoTrabajador = null

        workerAdapter = null

        workerRepository = null

        super.onDestroyView()
    }
}
