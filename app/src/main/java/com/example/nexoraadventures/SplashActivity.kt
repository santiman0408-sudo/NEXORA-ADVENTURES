package com.example.nexoraadventures

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Splash Screen de NEXORA ADVENTURES.
 *
 * Funcionalidades:
 * - Utiliza exclusivamente el Splash nativo de Android.
 * - Conserva el logotipo y el fondo verde.
 * - Mantiene visible el Splash durante cinco segundos.
 * - Evita cargar una segunda pantalla de presentación.
 * - Abre LoginActivity al finalizar.
 */
class SplashActivity : Activity() {

    companion object {

        private const val TAG = "NEXORA_SPLASH"

        private const val SPLASH_DURATION = 5000L

        private const val LOGIN_DELAY = 5200L
    }

    private val handler = Handler(
        Looper.getMainLooper()
    )

    private var navigationStarted = false

    // ==========================================
    // NAVEGACIÓN HACIA LOGIN
    // ==========================================

    private val loginRunnable = Runnable {

        if (!isFinishing && !isDestroyed && !navigationStarted) {

            navigationStarted = true

            Log.d(
                TAG,
                "Finalizando Splash. Abriendo LoginActivity."
            )

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            // Finalizar SplashActivity
            finish()

        }
    }

    // ==========================================
    // CICLO DE VIDA: ONCREATE
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(
            TAG,
            "1. Iniciando Splash nativo de NEXORA."
        )

        // Registrar el inicio del temporizador
        val startTime = SystemClock.uptimeMillis()

        // Calcular el momento de finalización
        val splashEndTime = startTime + SPLASH_DURATION

        // Instalar AndroidX SplashScreen
        val splashScreen = installSplashScreen()

        // Mantener visible el Splash nativo
        // durante cinco segundos.
        splashScreen.setKeepOnScreenCondition {

            SystemClock.uptimeMillis() < splashEndTime

        }

        super.onCreate(savedInstanceState)

        Log.d(
            TAG,
            "2. Splash nativo instalado correctamente."
        )

        // ======================================
        // FONDO CORPORATIVO DE LA VENTANA
        // ======================================

        val primaryColor = getColor(
            R.color.nexora_primary
        )

        val darkColor = getColor(
            R.color.nexora_primary_dark
        )

        window.apply {

            setBackgroundDrawable(
                ColorDrawable(primaryColor)
            )

            statusBarColor = darkColor

            navigationBarColor = darkColor

        }

        // ======================================
        // PROGRAMAR NAVEGACIÓN
        // ======================================

        Log.d(
            TAG,
            "3. Splash nativo visible durante cinco segundos."
        )

        // Abrir Login después de liberar el Splash
        handler.postDelayed(
            loginRunnable,
            LOGIN_DELAY
        )
    }

    // ==========================================
    // CICLO DE VIDA: ONDESTROY
    // ==========================================

    override fun onDestroy() {

        Log.d(
            TAG,
            "4. SplashActivity finalizada."
        )

        // Cancelar tareas pendientes
        handler.removeCallbacks(
            loginRunnable
        )

        super.onDestroy()
    }
}
