package com.example.nexoraadventures

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Actividad de inicio de sesión de NEXORA ADVENTURES.
 *
 * Funcionalidades:
 * - Validación del correo electrónico.
 * - Validación de contraseña.
 * - Acceso de demostración.
 * - Navegación hacia MainActivity.
 * - Prevención de clics y navegaciones duplicadas.
 */
class LoginActivity : AppCompatActivity() {

    // ==========================================
    // 1. DECLARACIÓN DE COMPONENTES
    // ==========================================

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var btnLogin: MaterialButton

    // ==========================================
    // 2. CREDENCIALES DE DEMOSTRACIÓN
    // ==========================================

    private val demoEmail = "admin@nexora.com"
    private val demoPassword = "123456"

    // Evita iniciar MainActivity más de una vez
    private var isNavigating = false

    // ==========================================
    // 3. MÉTODO ONCREATE
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el diseño de Login
        setContentView(R.layout.activity_login)

        // Inicializar los componentes
        inicializarComponentes()

        // Configurar los eventos
        configurarEventos()
    }

    // ==========================================
    // 4. INICIALIZAR COMPONENTES
    // ==========================================

    private fun inicializarComponentes() {

        tilEmail = findViewById(R.id.tilEmail)

        tilPassword = findViewById(R.id.tilPassword)

        etEmail = findViewById(R.id.etEmail)

        etPassword = findViewById(R.id.etPassword)

        btnLogin = findViewById(R.id.btnLogin)
    }

    // ==========================================
    // 5. CONFIGURAR EVENTOS
    // ==========================================

    private fun configurarEventos() {

        btnLogin.setOnClickListener {

            if (!isNavigating) {
                validarCredenciales()
            }
        }

        // Limpiar error del correo al escribir
        etEmail.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                tilEmail.error = null
            }
        }

        // Limpiar error de contraseña al escribir
        etPassword.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                tilPassword.error = null
            }
        }
    }

    // ==========================================
    // 6. VALIDAR CREDENCIALES
    // ==========================================

    private fun validarCredenciales() {

        val email = etEmail.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val password = etPassword.text
            ?.toString()
            .orEmpty()

        // Limpiar errores anteriores
        tilEmail.error = null
        tilPassword.error = null

        // Validar correo vacío
        if (email.isEmpty()) {

            tilEmail.error = "Ingresa tu correo electrónico"

            etEmail.requestFocus()

            return
        }

        // Validar formato de correo
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            tilEmail.error = "Ingresa un correo electrónico válido"

            etEmail.requestFocus()

            return
        }

        // Validar contraseña vacía
        if (password.isEmpty()) {

            tilPassword.error = "Ingresa tu contraseña"

            etPassword.requestFocus()

            return
        }

        // Validar longitud mínima
        if (password.length < 6) {

            tilPassword.error =
                "La contraseña debe tener al menos 6 caracteres"

            etPassword.requestFocus()

            return
        }

        // Comprobar credenciales de demostración
        val credentialsAreValid =
            email.equals(demoEmail, ignoreCase = true) &&
                    password == demoPassword

        if (credentialsAreValid) {

            iniciarSesion()

        } else {

            tilPassword.error = "Correo o contraseña incorrectos"

            Toast.makeText(
                this,
                "Verifica tus credenciales e inténtalo nuevamente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // 7. OCULTAR TECLADO
    // ==========================================

    private fun ocultarTeclado() {

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager

        currentFocus?.let { view ->
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }

    // ==========================================
    // 8. INICIAR SESIÓN
    // ==========================================

    private fun iniciarSesion() {

        // Evitar navegaciones duplicadas
        if (isNavigating || isFinishing) {
            return
        }

        isNavigating = true

        btnLogin.isEnabled = false

        try {

            // Ocultar teclado antes de cambiar de pantalla
            ocultarTeclado()

            // Crear Intent hacia MainActivity
            val intent = Intent(
                this,
                MainActivity::class.java
            )

            // Navegación normal, sin NEW_TASK
            // ni CLEAR_TASK
            startActivity(intent)

            // Finalizar Login una sola vez
            finish()

        } catch (exception: Exception) {

            // Recuperar botón si ocurre un error
            isNavigating = false

            btnLogin.isEnabled = true

            Toast.makeText(
                this,
                "No se pudo abrir la pantalla principal.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
