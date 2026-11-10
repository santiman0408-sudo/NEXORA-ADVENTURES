package com.example.nexoraadventures.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoraadventures.R
import com.example.nexoraadventures.model.EstadoLaboral
import com.example.nexoraadventures.model.Worker
import com.google.android.material.button.MaterialButton
import java.util.Locale

/**
 * Adaptador RecyclerView de trabajadores.
 *
 * Responsabilidades:
 * - Mostrar información personal.
 * - Mostrar información laboral.
 * - Mostrar sueldo mensual.
 * - Diferenciar trabajadores activos e inactivos.
 * - Ejecutar edición y eliminación.
 */
class WorkerAdapter(

    private val onEditar: (Worker) -> Unit,

    private val onEliminar: (Worker) -> Unit

) : RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder>() {

    // ==========================================
    // 1. LISTA DE TRABAJADORES
    // ==========================================

    private var listaTrabajadores: List<Worker> = emptyList()

    // ==========================================
    // 2. ACTUALIZAR LISTADO
    // ==========================================

    fun actualizarLista(
        nuevosTrabajadores: List<Worker>
    ) {

        listaTrabajadores = nuevosTrabajadores.toList()

        notifyDataSetChanged()
    }

    // ==========================================
    // 3. CREAR VIEW HOLDER
    // ==========================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkerViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_worker,
                parent,
                false
            )

        return WorkerViewHolder(vista)
    }

    // ==========================================
    // 4. VINCULAR INFORMACIÓN
    // ==========================================

    override fun onBindViewHolder(
        holder: WorkerViewHolder,
        position: Int
    ) {

        val trabajador = listaTrabajadores[position]

        holder.vincular(
            trabajador,
            onEditar,
            onEliminar
        )
    }

    // ==========================================
    // 5. CANTIDAD DE TRABAJADORES
    // ==========================================

    override fun getItemCount(): Int {

        return listaTrabajadores.size
    }

    // ==========================================
    // 6. VIEW HOLDER
    // ==========================================

    class WorkerViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        // ======================================
        // COMPONENTES DEL XML
        // ======================================

        private val txtNombre =
            itemView.findViewById<TextView>(
                R.id.txtWorkerNombre
            )

        private val txtEstado =
            itemView.findViewById<TextView>(
                R.id.txtWorkerEstado
            )

        private val txtCargo =
            itemView.findViewById<TextView>(
                R.id.txtWorkerCargo
            )

        private val txtArea =
            itemView.findViewById<TextView>(
                R.id.txtWorkerArea
            )

        private val txtDocumento =
            itemView.findViewById<TextView>(
                R.id.txtWorkerDocumento
            )

        private val txtTelefono =
            itemView.findViewById<TextView>(
                R.id.txtWorkerTelefono
            )

        private val txtCorreo =
            itemView.findViewById<TextView>(
                R.id.txtWorkerCorreo
            )

        private val txtFecha =
            itemView.findViewById<TextView>(
                R.id.txtWorkerFecha
            )

        private val txtSueldo =
            itemView.findViewById<TextView>(
                R.id.txtWorkerSueldo
            )

        private val btnEditar =
            itemView.findViewById<MaterialButton>(
                R.id.btnWorkerEditar
            )

        private val btnEliminar =
            itemView.findViewById<MaterialButton>(
                R.id.btnWorkerEliminar
            )

        // ======================================
        // 7. VINCULAR DATOS
        // ======================================

        fun vincular(
            trabajador: Worker,
            onEditar: (Worker) -> Unit,
            onEliminar: (Worker) -> Unit
        ) {

            // Nombre completo.

            txtNombre.text =
                "${trabajador.nombres} ${trabajador.apellidos}"

            // Cargo.

            txtCargo.text = trabajador.cargo

            // Área.

            txtArea.text =
                "Área: ${trabajador.area}"

            // Documento.

            txtDocumento.text =
                "DNI: ${trabajador.documento}"

            // Teléfono.

            txtTelefono.text =
                "Teléfono: ${trabajador.telefono}"

            // Correo.

            txtCorreo.text =
                "Correo: ${trabajador.correo}"

            // Fecha de contratación.

            txtFecha.text =
                "Ingreso: ${trabajador.fechaContratacion}"

            // Sueldo mensual.

            txtSueldo.text = String.format(
                Locale.US,
                "Sueldo mensual: S/ %,.2f",
                trabajador.sueldoMensual
            )

            // ==================================
            // 8. ESTADO LABORAL
            // ==================================

            txtEstado.text =
                trabajador.estadoLaboral.descripcion.uppercase(
                    Locale.getDefault()
                )

            val colorEstado = when (
                trabajador.estadoLaboral
            ) {

                EstadoLaboral.ACTIVO ->
                    R.color.nexora_success

                EstadoLaboral.INACTIVO ->
                    R.color.nexora_primary_dark
            }

            val color = ContextCompat.getColor(
                itemView.context,
                colorEstado
            )

            txtEstado.background =
                GradientDrawable().apply {

                    shape = GradientDrawable.RECTANGLE

                    cornerRadius =
                        30f * itemView.resources.displayMetrics.density

                    setColor(color)
                }

            // ==================================
            // 9. BOTÓN EDITAR
            // ==================================

            btnEditar.isEnabled = true

            btnEditar.setOnClickListener {

                onEditar(trabajador)
            }

            // ==================================
            // 10. BOTÓN ELIMINAR
            // ==================================

            btnEliminar.isEnabled = true

            btnEliminar.setOnClickListener {

                onEliminar(trabajador)
            }
        }
    }
}


