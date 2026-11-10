package com.example.nexoraadventures.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoraadventures.R
import com.example.nexoraadventures.model.Destination
import com.google.android.material.button.MaterialButton
import java.util.Locale

/**
 * Adaptador del catálogo turístico de NEXORA ADVENTURES.
 *
 * Carga las fotografías optimizadas para evitar
 * consumir demasiada memoria del dispositivo.
 */
class DestinationAdapter(
    private var destinations: List<Destination>,
    private val onItemClick: (Destination) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    // Componentes de cada tarjeta
    class DestinationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgDestination: ImageView =
            itemView.findViewById(R.id.imgDestination)

        val txtDestinationName: TextView =
            itemView.findViewById(R.id.txtDestinationName)

        val txtDestinationLocation: TextView =
            itemView.findViewById(R.id.txtDestinationLocation)

        val txtDestinationCategory: TextView =
            itemView.findViewById(R.id.txtDestinationCategory)

        val txtDestinationPrice: TextView =
            itemView.findViewById(R.id.txtDestinationPrice)

        val txtDestinationDuration: TextView =
            itemView.findViewById(R.id.txtDestinationDuration)

        val txtDestinationDifficulty: TextView =
            itemView.findViewById(R.id.txtDestinationDifficulty)

        val btnViewDestination: MaterialButton =
            itemView.findViewById(R.id.btnViewDestination)
    }

    // Crear tarjeta
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_destination,
                parent,
                false
            )

        return DestinationViewHolder(view)
    }

    // Mostrar información del destino
    override fun onBindViewHolder(
        holder: DestinationViewHolder,
        position: Int
    ) {

        val destination = destinations[position]

        holder.txtDestinationName.text = destination.name

        holder.txtDestinationLocation.text =
            destination.location

        holder.txtDestinationCategory.text =
            destination.category

        holder.txtDestinationPrice.text =
            String.format(
                Locale.US,
                "S/ %.2f",
                destination.price
            )

        holder.txtDestinationDuration.text =
            "Duración: ${destination.duration}"

        holder.txtDestinationDifficulty.text =
            "Dificultad: ${destination.difficulty}"

        // Cargar fotografía optimizada
        cargarImagenOptimizada(
            holder.imgDestination,
            destination.imageResId
        )

        holder.imgDestination.contentDescription =
            "Imagen de ${destination.name}"

        // Seleccionar tarjeta
        holder.itemView.setOnClickListener {
            onItemClick(destination)
        }

        // Seleccionar botón de detalle
        holder.btnViewDestination.setOnClickListener {
            onItemClick(destination)
        }
    }

    // Cargar imagen reduciendo su resolución en memoria
    private fun cargarImagenOptimizada(
        imageView: ImageView,
        imageResId: Int?
    ) {

        if (imageResId == null) {

            imageView.setImageResource(
                android.R.drawable.ic_menu_gallery
            )

            return
        }

        // Identificar el recurso de la tarjeta actual
        imageView.tag = imageResId

        // Imagen provisional mientras se carga
        imageView.setImageResource(
            android.R.drawable.ic_menu_gallery
        )

        imageView.post {

            // Evitar mostrar una imagen de una tarjeta reciclada
            if (imageView.tag != imageResId) {
                return@post
            }

            try {

                val boundsOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                    inScaled = false
                }

                // Leer dimensiones sin cargar la imagen completa
                BitmapFactory.decodeResource(
                    imageView.resources,
                    imageResId,
                    boundsOptions
                )

                if (
                    boundsOptions.outWidth <= 0 ||
                    boundsOptions.outHeight <= 0
                ) {
                    return@post
                }

                val requestedWidth =
                    imageView.width.coerceAtLeast(1)

                val requestedHeight =
                    imageView.height.coerceAtLeast(1)

                val options = BitmapFactory.Options().apply {

                    inSampleSize = calcularInSampleSize(
                        boundsOptions.outWidth,
                        boundsOptions.outHeight,
                        requestedWidth,
                        requestedHeight
                    )

                    // Reduce el consumo de memoria de las fotografías
                    inPreferredConfig = Bitmap.Config.RGB_565

                    inScaled = false
                }

                // Decodificar la fotografía a menor resolución
                val bitmap = BitmapFactory.decodeResource(
                    imageView.resources,
                    imageResId,
                    options
                )

                if (imageView.tag == imageResId && bitmap != null) {

                    imageView.setImageBitmap(bitmap)

                } else {

                    bitmap?.recycle()
                }

            } catch (error: OutOfMemoryError) {

                imageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }
        }
    }

    // Calcular cuánto reducir una imagen
    private fun calcularInSampleSize(
        width: Int,
        height: Int,
        requestedWidth: Int,
        requestedHeight: Int
    ): Int {

        var sampleSize = 1

        val halfWidth = width / 2
        val halfHeight = height / 2

        while (
            halfWidth / sampleSize >= requestedWidth &&
            halfHeight / sampleSize >= requestedHeight
        ) {

            sampleSize *= 2
        }

        return sampleSize
    }

    // Cantidad de destinos
    override fun getItemCount(): Int {
        return destinations.size
    }

    // Actualizar lista al realizar una búsqueda
    fun updateData(newDestinations: List<Destination>) {

        destinations = newDestinations.toList()

        notifyDataSetChanged()
    }
}
