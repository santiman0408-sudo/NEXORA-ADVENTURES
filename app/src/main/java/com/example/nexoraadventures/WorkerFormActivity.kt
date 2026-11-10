package com.example.nexoraadventures

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nexoraadventures.data.WorkerRepository
import com.example.nexoraadventures.model.EstadoLaboral
import com.example.nexoraadventures.model.Worker
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Formulario de trabajadores de NEXORA ADVENTURES.
 *
 * Permite:
 * - Registrar trabajadores.
 * - Editar trabajadores existentes.
 * - Validar información personal.
 * - Validar información laboral.
 * - Guardar datos en Room Database.
 */
class WorkerFormActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "NEXORA_WORKER_FORM"

        const val EXTRA_WORKER_ID =
            "extra_worker_id"

        private const val ID_NUEVO_TRABAJADOR = -1
    }

    // ==========================================
    // 1. REPOSITORIO
    // ==========================================

    private val workerRepository by lazy {

        WorkerRepository.obtenerRepositorio(
            applicationContext
        )
    }

    // ==========================================
    // 2. DECLARAR COMPONENTES
    // ==========================================

    private lateinit var toolbar: MaterialToolbar

    private lateinit var etNombres: EditText

    private lateinit var etApellidos: EditText

    private lateinit var etDocumento: EditText

    private lateinit var etTelefono: EditText

    private lateinit var etCorreo: EditText

    private lateinit var etCargo: EditText

    private lateinit var etArea: EditText

    private lateinit var etFecha: EditText

    private lateinit var etSueldo: EditText

    private lateinit var spinnerEstado: Spinner

    private lateinit var btnCancelar: MaterialButton

    private lateinit var btnGuardar: MaterialButton

    private val estados = EstadoLaboral.values().toList()

    private var trabajadorExistente: Worker? = null

    // ==========================================
    // 3. CREAR ACTIVIDAD
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Log.d(
            TAG,
            "Iniciando formulario de trabajadores."
        )

        setContentView(
            R.layout.activity_worker_form
        )

        inicializarComponentes()

        configurarToolbar()

        configurarSelectorEstado()

        configurarBotones()

        cargarTrabajadorExistente()

        Log.d(
            TAG,
            "Formulario inicializado correctamente."
        )
    }

    // ==========================================
    // 4. INICIALIZAR COMPONENTES
    // ==========================================

    private fun inicializarComponentes() {

        toolbar = findViewById(
            R.id.toolbarTrabajador
        )

        etNombres = findViewById(
            R.id.etWorkerNombres
        )

        etApellidos = findViewById(
            R.id.etWorkerApellidos
        )

        etDocumento = findViewById(
            R.id.etWorkerDocumento
        )

        etTelefono = findViewById(
            R.id.etWorkerTelefono
        )

        etCorreo = findViewById(
            R.id.etWorkerCorreo
        )

        etCargo = findViewById(
            R.id.etWorkerCargo
        )

        etArea = findViewById(
            R.id.etWorkerArea
        )

        etFecha = findViewById(
            R.id.etWorkerFecha
        )

        etSueldo = findViewById(
            R.id.etWorkerSueldo
        )

        spinnerEstado = findViewById(
            R.id.spinnerWorkerEstado
        )

        btnCancelar = findViewById(
            R.id.btnWorkerCancelar
        )

        btnGuardar = findViewById(
            R.id.btnWorkerGuardar
        )

        Log.d(
            TAG,
            "Componentes vinculados correctamente."
        )
    }

    // ==========================================
    // 5. CONFIGURAR TOOLBAR
    // ==========================================

    private fun configurarToolbar() {

        toolbar.title = if (
            esEdicion()
        ) {
            "Editar trabajador"
        } else {
            "Nuevo trabajador"
        }

        toolbar.setNavigationIcon(
            android.R.drawable.ic_menu_close_clear_cancel
        )

        toolbar.setNavigationOnClickListener {

            finish()
        }
    }

    // ==========================================
    // 6. CONFIGURAR SELECTOR DE ESTADO
    // ==========================================

    private fun configurarSelectorEstado() {

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados.map {
                it.descripcion
            }
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerEstado.adapter = adaptador

        spinnerEstado.setSelection(
            estados.indexOf(
                EstadoLaboral.ACTIVO
            ).coerceAtLeast(0)
        )
    }

    // ==========================================
    // 7. IDENTIFICAR MODO DE OPERACIÓN
    // ==========================================

    private fun esEdicion(): Boolean {

        return intent.getIntExtra(
            EXTRA_WORKER_ID,
            ID_NUEVO_TRABAJADOR
        ) != ID_NUEVO_TRABAJADOR
    }

    // ==========================================
    // 8. CARGAR TRABAJADOR EXISTENTE
    // ==========================================

    private fun cargarTrabajadorExistente() {

        val idTrabajador = intent.getIntExtra(
            EXTRA_WORKER_ID,
            ID_NUEVO_TRABAJADOR
        )

        // MODO REGISTRO.

        if (idTrabajador == ID_NUEVO_TRABAJADOR) {

            btnGuardar.text = "REGISTRAR"

            Log.d(
                TAG,
                "Modo registro de trabajador nuevo."
            )

            return
        }

        // MODO EDICIÓN.

        btnGuardar.isEnabled = false

        btnGuardar.text = "CARGANDO..."

        lifecycleScope.launch {

            try {

                val trabajador =
                    workerRepository.buscarPorId(
                        idTrabajador
                    )

                if (trabajador == null) {

                    Log.e(
                        TAG,
                        "No se encontró el trabajador ID=$idTrabajador"
                    )

                    Toast.makeText(
                        this@WorkerFormActivity,
                        "No se encontró el trabajador.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                    return@launch
                }

                trabajadorExistente = trabajador

                // ==================================
                // LLENAR CAMPOS
                // ==================================

                etNombres.setText(
                    trabajador.nombres
                )

                etApellidos.setText(
                    trabajador.apellidos
                )

                etDocumento.setText(
                    trabajador.documento
                )

                etTelefono.setText(
                    trabajador.telefono
                )

                etCorreo.setText(
                    trabajador.correo
                )

                etCargo.setText(
                    trabajador.cargo
                )

                etArea.setText(
                    trabajador.area
                )

                etFecha.setText(
                    trabajador.fechaContratacion
                )

                etSueldo.setText(
                    trabajador.sueldoMensual.toString()
                )

                spinnerEstado.setSelection(
                    estados.indexOf(
                        trabajador.estadoLaboral
                    ).coerceAtLeast(0)
                )

                btnGuardar.text = "GUARDAR CAMBIOS"

                btnGuardar.isEnabled = true

                Log.d(
                    TAG,
                    "Trabajador cargado para edición: ${trabajador.id}"
                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error al cargar trabajador.",
                    e
                )

                Toast.makeText(
                    this@WorkerFormActivity,
                    "Error al cargar la información.",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
    }

    // ==========================================
    // 9. CONFIGURAR BOTONES
    // ==========================================

    private fun configurarBotones() {

        btnCancelar.setOnClickListener {

            Log.d(
                TAG,
                "Operación de trabajador cancelada."
            )

            finish()
        }

        btnGuardar.setOnClickListener {

            Log.d(
                TAG,
                "Se presionó REGISTRAR/GUARDAR."
            )

            guardarTrabajador()
        }
    }

    // ==========================================
    // 10. VALIDAR Y GUARDAR TRABAJADOR
    // ==========================================

    private fun guardarTrabajador() {

        if (!btnGuardar.isEnabled) return

        // ======================================
        // OBTENER VALORES
        // ======================================

        val nombres =
            etNombres.text.toString().trim()

        val apellidos =
            etApellidos.text.toString().trim()

        val documento =
            etDocumento.text.toString().filter {
                it.isDigit()
            }

        val telefono =
            etTelefono.text.toString().filter {
                it.isDigit()
            }

        val correo =
            etCorreo.text.toString().trim()

        val cargo =
            etCargo.text.toString().trim()

        val area =
            etArea.text.toString().trim()

        val fecha =
            etFecha.text.toString().trim()

        val sueldoTexto =
            etSueldo.text.toString()
                .trim()
                .replace(",", ".")

        val sueldo =
            sueldoTexto.toDoubleOrNull()

        // ======================================
        // VALIDAR NOMBRES
        // ======================================

        if (nombres.length < 2) {

            mostrarError(
                etNombres,
                "Ingresa los nombres del trabajador."
            )

            return
        }

        // ======================================
        // VALIDAR APELLIDOS
        // ======================================

        if (apellidos.length < 2) {

            mostrarError(
                etApellidos,
                "Ingresa los apellidos del trabajador."
            )

            return
        }

        // ======================================
        // VALIDAR DOCUMENTO
        // ======================================

        if (documento.length !in 8..15) {

            mostrarError(
                etDocumento,
                "Ingresa un documento válido de 8 a 15 dígitos."
            )

            return
        }

        // ======================================
        // VALIDAR TELÉFONO
        // ======================================

        if (telefono.length !in 7..15) {

            mostrarError(
                etTelefono,
                "Ingresa un teléfono válido."
            )

            return
        }

        // ======================================
        // VALIDAR CORREO
        // ======================================

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(correo)
                .matches()
        ) {

            mostrarError(
                etCorreo,
                "Ingresa un correo electrónico válido."
            )

            return
        }

        // ======================================
        // VALIDAR CARGO
        // ======================================

        if (cargo.isEmpty()) {

            mostrarError(
                etCargo,
                "Ingresa el cargo del trabajador."
            )

            return
        }

        // ======================================
        // VALIDAR ÁREA
        // ======================================

        if (area.isEmpty()) {

            mostrarError(
                etArea,
                "Ingresa el área de trabajo."
            )

            return
        }

        // ======================================
        // VALIDAR FECHA
        // ======================================

        if (!validarFecha(fecha)) {

            mostrarError(
                etFecha,
                "Utiliza una fecha válida DD/MM/AAAA."
            )

            return
        }

        // ======================================
        // VALIDAR SUELDO
        // ======================================

        if (
            sueldo == null ||
            !sueldo.isFinite() ||
            sueldo <= 0.0
        ) {

            mostrarError(
                etSueldo,
                "Ingresa un sueldo mensual mayor que cero."
            )

            return
        }

        // ======================================
        // OBTENER ESTADO
        // ======================================

        val estadoSeleccionado =
            estados.getOrNull(
                spinnerEstado.selectedItemPosition
            ) ?: EstadoLaboral.ACTIVO

        // ======================================
        // PREPARAR TRABAJADOR
        // ======================================

        val trabajadorAnterior = trabajadorExistente

        val trabajadorGuardar = Worker(

            id = trabajadorAnterior?.id ?: 0,

            nombres = nombres,

            apellidos = apellidos,

            documento = documento,

            telefono = telefono,

            correo = correo,

            cargo = cargo,

            area = area,

            fechaContratacion = fecha,

            sueldoMensual = sueldo,

            estadoLaboral = estadoSeleccionado
        )

        // ======================================
        // EVITAR DOBLE ENVÍO
        // ======================================

        btnGuardar.isEnabled = false

        btnGuardar.text = "GUARDANDO..."

        // ======================================
        // GUARDAR EN ROOM
        // ======================================

        lifecycleScope.launch {

            try {

                // Verificar DNI duplicado.

                val documentoDuplicado =
                    workerRepository.documentoRegistrado(
                        documento,
                        excluirId = trabajadorAnterior?.id
                    )

                if (documentoDuplicado) {

                    btnGuardar.isEnabled = true

                    btnGuardar.text =
                        if (trabajadorAnterior == null) {
                            "REGISTRAR"
                        } else {
                            "GUARDAR CAMBIOS"
                        }

                    mostrarError(
                        etDocumento,
                        "Este documento ya está registrado."
                    )

                    return@launch
                }

                if (trabajadorAnterior == null) {

                    // ==================================
                    // REGISTRAR NUEVO TRABAJADOR
                    // ==================================

                    val nuevoId =
                        workerRepository.registrarTrabajador(
                            trabajadorGuardar
                        )

                    Log.d(
                        TAG,
                        "Trabajador registrado. ID=$nuevoId"
                    )

                    Toast.makeText(
                        this@WorkerFormActivity,
                        "Trabajador registrado correctamente.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    // ==================================
                    // ACTUALIZAR TRABAJADOR
                    // ==================================

                    val actualizado =
                        workerRepository.actualizarTrabajador(
                            trabajadorGuardar
                        )

                    if (!actualizado) {

                        btnGuardar.isEnabled = true

                        btnGuardar.text = "GUARDAR CAMBIOS"

                        Toast.makeText(
                            this@WorkerFormActivity,
                            "No se pudo actualizar el trabajador.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    Log.d(
                        TAG,
                        "Trabajador actualizado. ID=${trabajadorGuardar.id}"
                    )

                    Toast.makeText(
                        this@WorkerFormActivity,
                        "Trabajador actualizado correctamente.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Regresar al listado.

                finish()

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error al guardar trabajador.",
                    e
                )

                btnGuardar.isEnabled = true

                btnGuardar.text =
                    if (trabajadorAnterior == null) {
                        "REGISTRAR"
                    } else {
                        "GUARDAR CAMBIOS"
                    }

                Toast.makeText(
                    this@WorkerFormActivity,
                    e.message ?: "No se pudo guardar el trabajador.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 11. VALIDAR FECHA
    // ==========================================

    private fun validarFecha(
        valor: String
    ): Boolean {

        val formato = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).apply {

            isLenient = false
        }

        val posicion = ParsePosition(0)

        val fechaConvertida = formato.parse(
            valor,
            posicion
        )

        return fechaConvertida != null &&
                posicion.index == valor.length
    }

    // ==========================================
    // 12. MOSTRAR ERRORES
    // ==========================================

    private fun mostrarError(
        campo: EditText,
        mensaje: String
    ) {

        Log.w(
            TAG,
            "Validación: $mensaje"
        )

        campo.error = mensaje

        campo.requestFocus()

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }
}
