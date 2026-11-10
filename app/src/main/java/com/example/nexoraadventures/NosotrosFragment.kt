package com.example.nexoraadventures

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

/**
 * Fragmento institucional de NEXORA ADVENTURES.
 *
 * Presenta:
 * - Quiénes somos.
 * - Misión.
 * - Visión.
 * - Valores institucionales.
 * - Servicios turísticos.
 */
class NosotrosFragment : Fragment() {

    companion object {

        private const val TAG = "NEXORA_NOSOTROS"
    }

    // ==========================================
    // 1. CREAR LA VISTA DEL FRAGMENT
    // ==========================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(
            TAG,
            "Creando NosotrosFragment."
        )

        // Cargar el diseño XML institucional.
        return inflater.inflate(
            R.layout.fragment_nosotros,
            container,
            false
        )
    }

    // ==========================================
    // 2. INICIALIZAR EL FRAGMENT
    // ==========================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        Log.d(
            TAG,
            "Presentación institucional cargada correctamente."
        )
    }

    // ==========================================
    // 3. LIBERAR RECURSOS
    // ==========================================

    override fun onDestroyView() {

        Log.d(
            TAG,
            "Vista de NosotrosFragment liberada."
        )

        super.onDestroyView()
    }
}
