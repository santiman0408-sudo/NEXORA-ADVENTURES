package com.example.nexoraadventures

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.Locale

/**
 * Actividad que presenta la información detallada
 * de cada destino turístico.
 *
 * Funcionalidades:
 * - Mostrar información del destino.
 * - Cargar fotografía optimizada.
 * - Consultar precio y actividades.
 * - Recibir coordenadas del destino.
 * - Abrir el formulario de reservas transfiriendo los datos.
 * - Regresar al catálogo.
 */
class DestinationDetailActivity : AppCompatActivity() {

    // ==========================================
    // 1. DECLARACIÓN DE COMPONENTES
    // ==========================================

    private lateinit var imgDestination: ImageView

    private lateinit var txtName: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtCategory: TextView
    private lateinit var txtPrice: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtDuration: TextView
    private lateinit var txtDifficulty: TextView
    private lateinit var txtMeetingPoint: TextView
    private lateinit var txtActivities: TextView

    private lateinit var btnReserveDestination: MaterialButton
    private lateinit var btnBack: MaterialButton

    // ==========================================
    // 2. COORDENADAS DEL DESTINO
    // ==========================================

    private var destinationLatitude: Double = 0.0
    private var destinationLongitude: Double = 0.0

    // ==========================================
    // 3. MÉTODO ONCREATE
    // ==========================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_destination_detail)

        // Recuperar identificador.
        val destinationId = intent.getIntExtra(
            EXTRA_ID,
            -1
        )

        if (destinationId == -1) {

            Toast.makeText(
                this,
                "No se recibió un destino válido.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // ======================================
        // 4. RECUPERAR INFORMACIÓN RECIBIDA
        // ======================================

        val name = intent.getStringExtra(
            EXTRA_NAME
        ).orEmpty()

        val location = intent.getStringExtra(
            EXTRA_LOCATION
        ).orEmpty()

        val category = intent.getStringExtra(
            EXTRA_CATEGORY
        ).orEmpty()

        val description = intent.getStringExtra(
            EXTRA_DESCRIPTION
        ).orEmpty()

        val price = intent.getDoubleExtra(
            EXTRA_PRICE,
            0.0
        )

        val duration = intent.getStringExtra(
            EXTRA_DURATION
        ).orEmpty()

        val difficulty = intent.getStringExtra(
            EXTRA_DIFFICULTY
        ).orEmpty()

        val meetingPoint = intent.getStringExtra(
            EXTRA_MEETING_POINT
        ).orEmpty()

        val activities = intent.getStringExtra(
            EXTRA_ACTIVITIES
        ).orEmpty()

        val imageResId = intent.getIntExtra(
            EXTRA_IMAGE_RES_ID,
            0
        )

        // ======================================
        // 5. RECUPERAR COORDENADAS GEOGRÁFICAS
        // ======================================

        val receivedLatitude = intent.getDoubleExtra(
            EXTRA_LATITUDE,
            0.0
        )

        val receivedLongitude = intent.getDoubleExtra(
            EXTRA_LONGITUDE,
            0.0
        )

        // Validar coordenadas recibidas.
        val coordinatesAreValid =
            receivedLatitude.isFinite() &&
                    receivedLongitude.isFinite() &&
                    receivedLatitude in -90.0..90.0 &&
                    receivedLongitude in -180.0..180.0 &&
                    (receivedLatitude != 0.0 || receivedLongitude != 0.0)

        if (coordinatesAreValid) {

            destinationLatitude = receivedLatitude
            destinationLongitude = receivedLongitude

        } else {

            destinationLatitude = 0.0
            destinationLongitude = 0.0
        }

        // ======================================
        // 6. INICIALIZAR COMPONENTES
        // ======================================

        inicializarComponentes()

        // ======================================
        // 7. MOSTRAR INFORMACIÓN DEL DESTINO
        // ======================================

        txtName.text = name

        txtLocation.text = location

        txtCategory.text = category

        txtPrice.text = String.format(
            Locale.US,
            "S/ %.2f",
            price
        )

        txtDescription.text = description

        txtDuration.text = "Duración: $duration"

        txtDifficulty.text = "Dificultad: $difficulty"

        txtMeetingPoint.text = meetingPoint

        txtActivities.text = activities

        // ======================================
        // 8. CARGAR FOTOGRAFÍA
        // ======================================

        cargarImagenOptimizada(
            imgDestination,
            imageResId
        )

        imgDestination.contentDescription =
            "Fotografía de $name"

        // ======================================
        // 9. CONFIGURAR BOTÓN DE RESERVA
        // ======================================

        btnReserveDestination.setOnClickListener {

            abrirFormularioReserva(
                name,
                meetingPoint
            )
        }

        // ======================================
        // 10. REGRESAR AL CATÁLOGO
        // ======================================

        btnBack.setOnClickListener {

            finish()
        }
    }

    // ==========================================
    // 11. INICIALIZAR COMPONENTES
    // ==========================================

    private fun inicializarComponentes() {

        imgDestination = findViewById(
            R.id.imgDetailDestination
        )

        txtName = findViewById(
            R.id.txtDetailName
        )

        txtLocation = findViewById(
            R.id.txtDetailLocation
        )

        txtCategory = findViewById(
            R.id.txtDetailCategory
        )

        txtPrice = findViewById(
            R.id.txtDetailPrice
        )

        txtDescription = findViewById(
            R.id.txtDetailDescription
        )

        txtDuration = findViewById(
            R.id.txtDetailDuration
        )

        txtDifficulty = findViewById(
            R.id.txtDetailDifficulty
        )

        txtMeetingPoint = findViewById(
            R.id.txtDetailMeetingPoint
        )

        txtActivities = findViewById(
            R.id.txtDetailActivities
        )

        btnReserveDestination = findViewById(
            R.id.btnReserveDestination
        )

        btnBack = findViewById(
            R.id.btnBackToCatalog
        )
    }

    // ==========================================
    // 12. ABRIR FORMULARIO DE RESERVA
    // ==========================================

    private fun abrirFormularioReserva(
        nombreDestino: String,
        puntoEncuentro: String
    ) {

        if (nombreDestino.isBlank()) {

            Toast.makeText(
                this,
                "No se identificó el destino turístico.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        try {

            val intentFormulario = Intent(
                this,
                ReservationFormActivity::class.java
            ).apply {

                // Transferir nombre del destino.
                putExtra(
                    ReservationFormActivity.EXTRA_DESTINATION_NAME,
                    nombreDestino
                )

                // Transferir punto de encuentro.
                putExtra(
                    ReservationFormActivity.EXTRA_MEETING_POINT,
                    puntoEncuentro
                )

                // Transferir latitud.
                putExtra(
                    ReservationFormActivity.EXTRA_LATITUDE,
                    destinationLatitude
                )

                // Transferir longitud.
                putExtra(
                    ReservationFormActivity.EXTRA_LONGITUDE,
                    destinationLongitude
                )
            }

            startActivity(intentFormulario)

        } catch (error: Exception) {

            Toast.makeText(
                this,
                "No se pudo abrir el formulario de reserva.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // 13. CARGAR FOTOGRAFÍA OPTIMIZADA
    // ==========================================

    private fun cargarImagenOptimizada(
        imageView: ImageView,
        imageResId: Int
    ) {

        if (imageResId == 0) {

            imageView.setImageResource(
                android.R.drawable.ic_menu_gallery
            )

            return
        }

        imageView.post {

            try {

                val boundsOptions =
                    BitmapFactory.Options().apply {

                        inJustDecodeBounds = true
                        inScaled = false
                    }

                // Leer dimensiones originales.
                BitmapFactory.decodeResource(
                    resources,
                    imageResId,
                    boundsOptions
                )

                if (
                    boundsOptions.outWidth <= 0 ||
                    boundsOptions.outHeight <= 0
                ) {

                    imageView.setImageResource(
                        android.R.drawable.ic_menu_gallery
                    )

                    return@post
                }

                val requestedWidth =
                    imageView.width.coerceAtLeast(1)

                val requestedHeight =
                    imageView.height.coerceAtLeast(1)

                // Calcular reducción necesaria.
                val options =
                    BitmapFactory.Options().apply {

                        inSampleSize = calcularInSampleSize(
                            boundsOptions.outWidth,
                            boundsOptions.outHeight,
                            requestedWidth,
                            requestedHeight
                        )

                        inPreferredConfig = Bitmap.Config.RGB_565

                        inScaled = false
                    }

                // Cargar fotografía reducida.
                val bitmap = BitmapFactory.decodeResource(
                    resources,
                    imageResId,
                    options
                )

                if (bitmap != null) {

                    imageView.setImageBitmap(bitmap)

                } else {

                    imageView.setImageResource(
                        android.R.drawable.ic_menu_gallery
                    )
                }

            } catch (error: OutOfMemoryError) {

                imageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }
        }
    }

    // ==========================================
    // 14. CALCULAR TAMAÑO DE IMAGEN
    // ==========================================

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

    // ==========================================
    // 15. IDENTIFICADORES PARA TRANSFERIR DATOS
    // ==========================================

    companion object {

        const val EXTRA_ID =
            "destination_id"

        const val EXTRA_NAME =
            "destination_name"

        const val EXTRA_LOCATION =
            "destination_location"

        const val EXTRA_CATEGORY =
            "destination_category"

        const val EXTRA_DESCRIPTION =
            "destination_description"

        const val EXTRA_PRICE =
            "destination_price"

        const val EXTRA_DURATION =
            "destination_duration"

        const val EXTRA_DIFFICULTY =
            "destination_difficulty"

        const val EXTRA_MEETING_POINT =
            "destination_meeting_point"

        const val EXTRA_ACTIVITIES =
            "destination_activities"

        const val EXTRA_IMAGE_RES_ID =
            "destination_image_res_id"

        // Nuevos identificadores geográficos.
        const val EXTRA_LATITUDE =
            "destination_latitude"

        const val EXTRA_LONGITUDE =
            "destination_longitude"
    }
}
