package com.example.nexoraadventures

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import com.example.nexoraadventures.data.ContactDbHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Módulo de contactos de NEXORA ADVENTURES.
 *
 * Funcionalidades:
 * - Capturar datos del cliente.
 * - Validar nombre y correo electrónico.
 * - Validar teléfono, asunto y mensaje.
 * - Registrar consultas en SQLite local.
 * - Limpiar el formulario.
 */
class ContactsFragment : Fragment() {

    companion object {

        private const val TAG = "NEXORA_CONTACTOS"
    }

    // ==========================================
    // 1. COMPONENTES DEL FORMULARIO
    // ==========================================

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout
    private lateinit var tilAsunto: TextInputLayout
    private lateinit var tilMensaje: TextInputLayout

    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etAsunto: TextInputEditText
    private lateinit var etMensaje: TextInputEditText

    private lateinit var btnEnviar: MaterialButton
    private lateinit var btnLimpiar: MaterialButton

    // ==========================================
    // 2. BASE DE DATOS LOCAL
    // ==========================================

    private var contactDbHelper: ContactDbHelper? = null

    // ==========================================
    // 3. CREAR VISTA DEL FRAGMENT
    // ==========================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(
            TAG,
            "Creando ContactsFragment."
        )

        return inflater.inflate(
            R.layout.fragment_contacts,
            container,
            false
        )
    }

    // ==========================================
    // 4. INICIALIZAR COMPONENTES
    // ==========================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        inicializarComponentes(view)

        contactDbHelper = ContactDbHelper(
            requireContext().applicationContext
        )

        configurarValidacionesVisuales()

        configurarBotones()

        Log.d(
            TAG,
            "Formulario de contactos inicializado."
        )
    }

    // ==========================================
    // 5. VINCULAR COMPONENTES XML
    // ==========================================

    private fun inicializarComponentes(
        view: View
    ) {

        tilNombre = view.findViewById(
            R.id.tilNombreContacto
        )

        tilCorreo = view.findViewById(
            R.id.tilCorreoContacto
        )

        tilTelefono = view.findViewById(
            R.id.tilTelefonoContacto
        )

        tilAsunto = view.findViewById(
            R.id.tilAsuntoContacto
        )

        tilMensaje = view.findViewById(
            R.id.tilMensajeContacto
        )

        etNombre = view.findViewById(
            R.id.etNombreContacto
        )

        etCorreo = view.findViewById(
            R.id.etCorreoContacto
        )

        etTelefono = view.findViewById(
            R.id.etTelefonoContacto
        )

        etAsunto = view.findViewById(
            R.id.etAsuntoContacto
        )

        etMensaje = view.findViewById(
            R.id.etMensajeContacto
        )

        btnEnviar = view.findViewById(
            R.id.btnEnviarConsulta
        )

        btnLimpiar = view.findViewById(
            R.id.btnLimpiarFormulario
        )
    }

    // ==========================================
    // 6. VALIDACIONES VISUALES
    // ==========================================

    private fun configurarValidacionesVisuales() {

        // Limpiar error al modificar el nombre.
        etNombre.doAfterTextChanged {
            tilNombre.error = null
        }

        // Limpiar error al modificar el correo.
        etCorreo.doAfterTextChanged {
            tilCorreo.error = null
        }

        // Limpiar error al modificar el teléfono.
        etTelefono.doAfterTextChanged {
            tilTelefono.error = null
        }

        // Limpiar error al modificar el asunto.
        etAsunto.doAfterTextChanged {
            tilAsunto.error = null
        }

        // Limpiar error al modificar el mensaje.
        etMensaje.doAfterTextChanged {
            tilMensaje.error = null
        }
    }

    // ==========================================
    // 7. CONFIGURAR BOTONES
    // ==========================================

    private fun configurarBotones() {

        btnEnviar.setOnClickListener {

            Log.d(
                TAG,
                "Solicitud de registro de consulta."
            )

            enviarConsulta()
        }

        btnLimpiar.setOnClickListener {

            limpiarFormulario()

            Toast.makeText(
                requireContext(),
                "Formulario limpiado.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ==========================================
    // 8. VALIDAR Y REGISTRAR CONSULTA
    // ==========================================

    private fun enviarConsulta() {

        if (!isAdded) return

        // Obtener los valores.
        val nombre = etNombre.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val correo = etCorreo.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val telefono = etTelefono.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val asunto = etAsunto.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val mensaje = etMensaje.text
            ?.toString()
            ?.trim()
            .orEmpty()

        // Ejecutar todas las validaciones.
        if (!validarFormulario(
                nombre,
                correo,
                telefono,
                asunto,
                mensaje
            )
        ) {

            Toast.makeText(
                requireContext(),
                "Revisa los campos indicados.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Evitar envíos duplicados.
        btnEnviar.isEnabled = false
        btnEnviar.text = "REGISTRANDO..."

        val helper = contactDbHelper

        if (helper == null) {

            btnEnviar.isEnabled = true
            btnEnviar.text = "ENVIAR CONSULTA"

            Toast.makeText(
                requireContext(),
                "No se pudo acceder al almacenamiento.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // Guardar consulta fuera del hilo principal.
        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val nuevoId = withContext(
                    Dispatchers.IO
                ) {

                    helper.insertarConsulta(
                        nombre = nombre,
                        correo = correo,
                        telefono = telefono,
                        asunto = asunto,
                        mensaje = mensaje
                    )
                }

                if (!isAdded) return@launch

                if (nuevoId > 0) {

                    Log.d(
                        TAG,
                        "Consulta registrada. ID=$nuevoId"
                    )

                    Toast.makeText(
                        requireContext(),
                        "Consulta registrada correctamente.",
                        Toast.LENGTH_LONG
                    ).show()

                    limpiarFormulario()

                } else {

                    Toast.makeText(
                        requireContext(),
                        "No se pudo registrar la consulta.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (error: Exception) {

                Log.e(
                    TAG,
                    "Error al registrar consulta.",
                    error
                )

                if (isAdded) {

                    Toast.makeText(
                        requireContext(),
                        "Ocurrió un error al guardar la consulta.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } finally {

                if (
                    isAdded &&
                    ::btnEnviar.isInitialized
                ) {

                    btnEnviar.isEnabled = true
                    btnEnviar.text = "ENVIAR CONSULTA"
                }
            }
        }
    }

    // ==========================================
    // 9. VALIDAR CAMPOS DEL FORMULARIO
    // ==========================================

    private fun validarFormulario(
        nombre: String,
        correo: String,
        telefono: String,
        asunto: String,
        mensaje: String
    ): Boolean {

        var formularioValido = true

        tilNombre.error = null
        tilCorreo.error = null
        tilTelefono.error = null
        tilAsunto.error = null
        tilMensaje.error = null

        // VALIDAR NOMBRE.
        if (nombre.length < 3) {

            tilNombre.error =
                "Ingresa un nombre de al menos 3 caracteres."

            formularioValido = false
        }

        // VALIDAR CORREO.
        if (
            correo.isBlank() ||
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches()
        ) {

            tilCorreo.error =
                "Ingresa un correo electrónico válido."

            formularioValido = false
        }

        // VALIDAR TELÉFONO.
        val telefonoNormalizado = telefono.filter {
            it.isDigit()
        }

        if (telefonoNormalizado.length !in 7..15) {

            tilTelefono.error =
                "Ingresa un teléfono válido."

            formularioValido = false
        }

        // VALIDAR ASUNTO.
        if (asunto.length < 3) {

            tilAsunto.error =
                "El asunto debe tener al menos 3 caracteres."

            formularioValido = false
        }

        // VALIDAR MENSAJE.
        if (mensaje.length < 10) {

            tilMensaje.error =
                "El mensaje debe contener al menos 10 caracteres."

            formularioValido = false
        }

        return formularioValido
    }

    // ==========================================
    // 10. LIMPIAR FORMULARIO
    // ==========================================

    private fun limpiarFormulario() {

        etNombre.setText("")
        etCorreo.setText("")
        etTelefono.setText("")
        etAsunto.setText("")
        etMensaje.setText("")

        tilNombre.error = null
        tilCorreo.error = null
        tilTelefono.error = null
        tilAsunto.error = null
        tilMensaje.error = null

        etNombre.requestFocus()
    }

    // ==========================================
    // 11. LIBERAR RECURSOS
    // ==========================================

    override fun onDestroyView() {

        contactDbHelper?.close()

        contactDbHelper = null

        Log.d(
            TAG,
            "ContactsFragment liberado."
        )

        super.onDestroyView()
    }
}
