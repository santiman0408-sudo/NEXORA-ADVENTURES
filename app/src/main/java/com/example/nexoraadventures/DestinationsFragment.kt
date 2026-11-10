package com.example.nexoraadventures

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoraadventures.adapter.DestinationAdapter
import com.example.nexoraadventures.data.DestinationData
import com.example.nexoraadventures.model.Destination
import com.google.android.material.textfield.TextInputEditText

/**
 * Fragmento del catálogo turístico de NEXORA ADVENTURES.
 *
 * Muestra destinos, permite buscarlos y abrir
 * la información detallada de cada uno.
 */
class DestinationsFragment :
    Fragment(R.layout.fragment_destinations) {

    private lateinit var destinationAdapter: DestinationAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recuperar componentes del XML
        val recyclerView = view.findViewById<RecyclerView>(
            R.id.rvDestinations
        )

        val searchInput = view.findViewById<TextInputEditText>(
            R.id.etSearchDestination
        )

        // 2. Configurar lista vertical
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        // 3. Inicializar adaptador
        destinationAdapter = DestinationAdapter(
            DestinationData.destinations
        ) { destination ->

            abrirDetalleDestino(destination)
        }

        // 4. Conectar adaptador con RecyclerView
        recyclerView.adapter = destinationAdapter

        // 5. Configurar búsqueda
        configurarBuscador(searchInput)
    }

    /**
     * Abre el detalle del destino seleccionado
     * y transfiere todos sus datos mediante Intent.
     */
    private fun abrirDetalleDestino(destination: Destination) {

        val intent = Intent(
            requireContext(),
            DestinationDetailActivity::class.java
        ).apply {

            putExtra(
                DestinationDetailActivity.EXTRA_ID,
                destination.id
            )

            putExtra(
                DestinationDetailActivity.EXTRA_NAME,
                destination.name
            )

            putExtra(
                DestinationDetailActivity.EXTRA_LOCATION,
                destination.location
            )

            putExtra(
                DestinationDetailActivity.EXTRA_CATEGORY,
                destination.category
            )

            putExtra(
                DestinationDetailActivity.EXTRA_DESCRIPTION,
                destination.description
            )

            putExtra(
                DestinationDetailActivity.EXTRA_PRICE,
                destination.price
            )

            putExtra(
                DestinationDetailActivity.EXTRA_DURATION,
                destination.duration
            )

            putExtra(
                DestinationDetailActivity.EXTRA_DIFFICULTY,
                destination.difficulty
            )

            putExtra(
                DestinationDetailActivity.EXTRA_MEETING_POINT,
                destination.meetingPoint
            )

            putExtra(
                DestinationDetailActivity.EXTRA_ACTIVITIES,
                destination.activities
            )

            putExtra(
                DestinationDetailActivity.EXTRA_IMAGE_RES_ID,
                destination.imageResId ?: 0
            )
        }

        startActivity(intent)
    }

    /**
     * Configura el buscador por nombre,
     * ubicación y categoría turística.
     */
    private fun configurarBuscador(
        searchInput: TextInputEditText
    ) {

        searchInput.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Sin acciones previas.
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val query = s
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                    val filteredDestinations =
                        DestinationData.destinations.filter { destination ->

                            destination.name.contains(
                                query,
                                ignoreCase = true
                            ) ||
                                    destination.location.contains(
                                        query,
                                        ignoreCase = true
                                    ) ||
                                    destination.category.contains(
                                        query,
                                        ignoreCase = true
                                    )
                        }

                    destinationAdapter.updateData(
                        filteredDestinations
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                    // Sin acciones posteriores.
                }
            }
        )
    }
}
