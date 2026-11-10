package com.example.nexoraadventures

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.google.android.material.navigation.NavigationView

/**
 * Actividad principal de NEXORA ADVENTURES.
 *
 * Gestiona:
 * - Toolbar.
 * - Navigation Drawer.
 * - Navegación entre módulos.
 * - Presentación institucional.
 * - Gestión de reservas.
 * - Gestión de trabajadores.
 * - Mapa turístico.
 * - Contactos.
 * - Cierre de sesión.
 */
class MainActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "NEXORA_MAIN"
    }

    // ==========================================
    // 1. DECLARACIÓN DE COMPONENTES
    // ==========================================

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var toolbar: Toolbar

    private lateinit var fragmentContainer: FrameLayout

    // ==========================================
    // 2. MÉTODO PRINCIPAL
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Cargar diseño principal.
        setContentView(
            R.layout.activity_main
        )

        // Inicializar componentes.
        inicializarComponentes()

        // Configurar Toolbar.
        configurarToolbar()

        // Configurar menú lateral.
        configurarNavigationDrawer()

        // Mostrar pantalla inicial.
        if (savedInstanceState == null) {

            mostrarInicio()

        } else {

            Log.d(
                TAG,
                "Actividad restaurada."
            )
        }
    }

    // ==========================================
    // 3. INICIALIZAR COMPONENTES
    // ==========================================

    private fun inicializarComponentes() {

        drawerLayout = findViewById(
            R.id.drawerLayout
        )

        navigationView = findViewById(
            R.id.navigationView
        )

        toolbar = findViewById(
            R.id.toolbar
        )

        fragmentContainer = findViewById(
            R.id.fragmentContainer
        )

        Log.d(
            TAG,
            "Componentes principales inicializados."
        )
    }

    // ==========================================
    // 4. CONFIGURAR TOOLBAR
    // ==========================================

    private fun configurarToolbar() {

        setSupportActionBar(toolbar)

        supportActionBar?.title = "Inicio"

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    // ==========================================
    // 5. CONFIGURAR NAVIGATION DRAWER
    // ==========================================

    private fun configurarNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener { item ->

            Log.d(
                TAG,
                "Opción seleccionada: ${item.title}"
            )

            when (item.itemId) {

                // ==================================
                // INICIO
                // ==================================

                R.id.nav_home -> {

                    mostrarInicio()
                }

                // ==================================
                // DESTINOS TURÍSTICOS
                // ==================================

                R.id.nav_destinations -> {

                    mostrarFragmento(
                        DestinationsFragment(),
                        "Destinos turísticos"
                    )
                }

                // ==================================
                // GESTIÓN DE RESERVAS
                // ==================================

                R.id.nav_reservations -> {

                    Log.d(
                        TAG,
                        "Solicitando ReservationsFragment."
                    )

                    mostrarFragmento(
                        ReservationsFragment(),
                        "Gestión de reservas"
                    )
                }

                // ==================================
                // GESTIÓN DE TRABAJADORES
                // ==================================

                R.id.nav_workers -> {

                    Log.d(
                        TAG,
                        "Solicitando WorkersFragment."
                    )

                    mostrarFragmento(
                        WorkersFragment(),
                        "Gestión de trabajadores"
                    )
                }

                // ==================================
                // MAPA Y UBICACIÓN
                // ==================================

                R.id.nav_map -> {

                    mostrarFragmento(
                        MapFragment(),
                        "Mapa y ubicación"
                    )
                }

                // ==================================
                // NOSOTROS
                // ==================================

                R.id.nav_about -> {

                    Log.d(
                        TAG,
                        "Solicitando NosotrosFragment."
                    )

                    mostrarFragmento(
                        NosotrosFragment(),
                        "Nosotros"
                    )
                }

                // ==================================
                // CONTACTOS
                // ==================================

                R.id.nav_contacts -> {

                    Log.d(
                        TAG,
                        "Solicitando ContactsFragment."
                    )

                    mostrarFragmento(
                        ContactsFragment(),
                        "Contactos"
                    )
                }

                // ==================================
                // CERRAR SESIÓN
                // ==================================

                R.id.nav_logout -> {

                    confirmarCerrarSesion()
                }
            }

            // Marcar opción seleccionada.

            if (item.itemId != R.id.nav_logout) {

                navigationView.setCheckedItem(
                    item.itemId
                )
            }

            // Cerrar menú lateral.

            drawerLayout.closeDrawer(
                GravityCompat.START
            )

            true
        }
    }

    // ==========================================
    // 6. MOSTRAR FRAGMENTO
    // ==========================================

    private fun mostrarFragmento(
        fragment: Fragment,
        titulo: String
    ) {

        // Evitar cambios después de guardar el estado.

        if (supportFragmentManager.isStateSaved) {

            Log.w(
                TAG,
                "No se puede cambiar de fragmento: estado guardado."
            )

            return
        }

        try {

            Log.d(
                TAG,
                "Iniciando fragmento: ${fragment.javaClass.simpleName}"
            )

            // Buscar fragmento actual.

            val fragmentActual =
                supportFragmentManager.findFragmentById(
                    R.id.fragmentContainer
                )

            // Preparar transacción.

            val transaccion =
                supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)

            if (fragmentActual != null) {

                // Reemplazar fragmento existente.

                transaccion.replace(
                    R.id.fragmentContainer,
                    fragment
                )

            } else {

                // Limpiar contenido provisional.

                fragmentContainer.removeAllViews()

                // Agregar nuevo fragmento.

                transaccion.add(
                    R.id.fragmentContainer,
                    fragment
                )
            }

            // Ejecutar transacción.

            transaccion.commitNow()

            // Actualizar título de Toolbar.

            supportActionBar?.title = titulo

            Log.d(
                TAG,
                "Fragmento cargado correctamente: ${fragment.javaClass.simpleName}"
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error al cargar fragmento: ${fragment.javaClass.simpleName}",
                e
            )
        }
    }

    // ==========================================
    // 7. MOSTRAR PANTALLA INICIO
    // ==========================================

    private fun mostrarInicio() {

        mostrarFragmento(
            HomeFragment(),
            "Inicio"
        )

        navigationView.setCheckedItem(
            R.id.nav_home
        )
    }

    // ==========================================
    // 8. MOSTRAR SECCIONES PROVISIONALES
    // ==========================================

    private fun mostrarSeccion(
        titulo: String,
        descripcion: String
    ) {

        if (supportFragmentManager.isStateSaved) {
            return
        }

        try {

            val fragmentActual =
                supportFragmentManager.findFragmentById(
                    R.id.fragmentContainer
                )

            if (fragmentActual != null) {

                supportFragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(fragmentActual)
                    .commitNow()
            }

            fragmentContainer.removeAllViews()

            val texto = TextView(this).apply {

                text = "$titulo\n\n$descripcion"

                textSize = 20f

                setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.nexora_primary
                    )
                )

                gravity = Gravity.CENTER

                setPadding(
                    32,
                    32,
                    32,
                    32
                )
            }

            fragmentContainer.addView(
                texto,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            supportActionBar?.title = titulo

            Log.d(
                TAG,
                "Sección provisional mostrada: $titulo"
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error al mostrar sección: $titulo",
                e
            )
        }
    }

    // ==========================================
    // 9. CONFIRMAR CIERRE DE SESIÓN
    // ==========================================

    private fun confirmarCerrarSesion() {

        AlertDialog.Builder(this)

            .setTitle(
                R.string.logout_confirmation_title
            )

            .setMessage(
                R.string.logout_confirmation_message
            )

            .setNegativeButton(
                R.string.logout_cancel
            ) { dialog, _ ->

                dialog.dismiss()
            }

            .setPositiveButton(
                R.string.logout_confirm
            ) { _, _ ->

                val intent = Intent(
                    this,
                    LoginActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                finish()
            }

            .show()
    }

    // ==========================================
    // 10. CONTROLAR BOTÓN ATRÁS
    // ==========================================

    @Deprecated(
        "Se conserva por compatibilidad con esta implementación."
    )
    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(
                GravityCompat.START
            )

        } else {

            super.onBackPressed()
        }
    }
}
