package com.example.nexoraadventures.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoraadventures.R
import com.example.nexoraadventures.model.EstadoReserva
import com.example.nexoraadventures.model.Reservation
import com.google.android.material.button.MaterialButton

/**
 * Adaptador RecyclerView de reservas turísticas.
 */
class ReservationAdapter(

    private val onEditar: (Reservation) -> Unit,

    private val onEliminar: (Reservation) -> Unit

) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    companion object {
        private const val TAG = "NEXORA_ADAPTER"
    }

    private var listaReservas: List<Reservation> = emptyList()

    // ==========================================
    // ACTUALIZAR LISTADO
    // ==========================================

    fun actualizarLista(nuevasReservas: List<Reservation>) {

        listaReservas = nuevasReservas.toList()

        Log.d(TAG, "Reservas actualizadas: ${listaReservas.size}")

        notifyDataSetChanged()
    }

    // ==========================================
    // CREAR VIEW HOLDER
    // ==========================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)

        return ReservationViewHolder(view)
    }

    // ==========================================
    // VINCULAR INFORMACIÓN
    // ==========================================

    override fun onBindViewHolder(
        holder: ReservationViewHolder,
        position: Int
    ) {

        holder.vincular(
            listaReservas[position],
            onEditar,
            onEliminar
        )
    }

    override fun getItemCount(): Int {
        return listaReservas.size
    }

    // ==========================================
    // VIEW HOLDER
    // ==========================================

    class ReservationViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtCliente =
            itemView.findViewById<TextView>(R.id.txtCliente)

        private val txtEstado =
            itemView.findViewById<TextView>(R.id.txtEstado)

        private val txtDestino =
            itemView.findViewById<TextView>(R.id.txtDestino)

        private val txtFecha =
            itemView.findViewById<TextView>(R.id.txtFecha)

        private val txtPersonas =
            itemView.findViewById<TextView>(R.id.txtPersonas)

        private val txtTelefono =
            itemView.findViewById<TextView>(R.id.txtTelefono)

        private val txtPuntoEncuentro =
            itemView.findViewById<TextView>(R.id.txtPuntoEncuentro)

        private val btnEditar =
            itemView.findViewById<MaterialButton>(R.id.btnEditar)

        private val btnEliminar =
            itemView.findViewById<MaterialButton>(R.id.btnEliminar)

        // ======================================
        // VINCULAR DATOS
        // ======================================

        fun vincular(
            reserva: Reservation,
            onEditar: (Reservation) -> Unit,
            onEliminar: (Reservation) -> Unit
        ) {

            txtCliente.text = reserva.clienteNombre

            txtDestino.text = reserva.destino

            txtFecha.text = "Fecha: ${reserva.fechaViaje}"

            txtPersonas.text =
                "Personas: ${reserva.cantidadPersonas}"

            txtTelefono.text =
                "Teléfono: ${reserva.telefono}"

            txtPuntoEncuentro.text =
                "Encuentro: ${reserva.puntoEncuentro}"

            txtEstado.text =
                reserva.estadoReserva.descripcion.uppercase()

            // ==================================
            // COLORES SEGÚN ESTADO
            // ==================================

            val colorEstado = when (reserva.estadoReserva) {

                EstadoReserva.PENDIENTE ->
                    R.color.nexora_secondary

                EstadoReserva.CONFIRMADO ->
                    R.color.nexora_success

                EstadoReserva.FINALIZADO ->
                    R.color.nexora_primary_dark
            }

            val color = ContextCompat.getColor(
                itemView.context,
                colorEstado
            )

            txtEstado.background = GradientDrawable().apply {

                shape = GradientDrawable.RECTANGLE

                cornerRadius = 30f

                setColor(color)
            }

            // ==================================
            // BOTÓN EDITAR
            // ==================================

            btnEditar.setOnClickListener {

                Log.d(
                    TAG,
                    "Clic EDITAR: ${reserva.clienteNombre}, ID=${reserva.id}"
                )

                onEditar(reserva)
            }

            // ==================================
            // BOTÓN ELIMINAR
            // ==================================

            btnEliminar.isEnabled = true
            btnEliminar.isClickable = true

            btnEliminar.setOnClickListener {

                Log.d(
                    TAG,
                    "BOTÓN ELIMINAR PRESIONADO: ${reserva.clienteNombre}, ID=${reserva.id}"
                )

                Toast.makeText(
                    itemView.context,
                    "Clic detectado: ${reserva.clienteNombre}",
                    Toast.LENGTH_SHORT
                ).show()

                onEliminar(reserva)
            }
        }
    }
}
