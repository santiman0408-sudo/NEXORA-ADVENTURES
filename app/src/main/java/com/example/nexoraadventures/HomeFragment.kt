package com.example.nexoraadventures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Fragmento principal de NEXORA ADVENTURES.
 *
 * Se encarga de mostrar la pantalla de inicio
 * dentro del contenedor de MainActivity.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // El diseño de la pantalla se carga desde fragment_home.xml.
        // Aquí agregaremos posteriormente las acciones de las tarjetas.
    }
}
