package com.example.nexoraadventures

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.example.nexoraadventures.data.DestinationData
import com.example.nexoraadventures.model.Destination

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

import com.google.android.material.floatingactionbutton.FloatingActionButton

import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

import java.util.Locale

/**
 * Módulo de mapa turístico de NEXORA ADVENTURES.
 *
 * Funcionalidades:
 * - Inicializar MapLibre.
 * - Cargar OpenFreeMap.
 * - Mostrar cuatro destinos turísticos.
 * - Dibujar cuatro rutas referenciales.
 * - Mostrar puntos de encuentro.
 * - Mostrar puntos intermedios.
 * - Abrir fichas de los destinos.
 * - Solicitar permisos de ubicación.
 * - Obtener ubicación actual o simulada.
 * - Mostrar marcador del dispositivo.
 * - Centrar el mapa en la ubicación obtenida.
 * - Administrar el ciclo de vida de MapView.
 *
 * IMPORTANTE:
 * Las rutas dibujadas son trazados geográficos esquemáticos.
 * No representan navegación vial ni senderos calculados.
 */
class MapFragment : Fragment() {

    // ==========================================
    // 0. CONFIGURACIÓN GENERAL
    // ==========================================

    companion object {

        private const val TAG = "NEXORA_MAPA"

        private const val MAP_STYLE_URL =
            "https://tiles.openfreemap.org/styles/liberty"

        private const val COLOR_RUTA = "#F28C28"

        private const val ANCHO_RUTA = 4.5f

        private const val ZOOM_INICIAL = 5.5

        private const val ZOOM_UBICACION = 15.0
    }

    // ==========================================
    // 1. DECLARACIÓN DE COMPONENTES
    // ==========================================

    private var mapView: MapView? = null

    private var mapLibreMap: MapLibreMap? = null

    private var fabMiUbicacion: FloatingActionButton? = null

    // Marcador de la ubicación actual del dispositivo.
    private var marcadorUbicacionUsuario: Marker? = null

    // Relacionar cada marcador turístico con su destino.
    private val destinosPorMarcador =
        mutableMapOf<String, Destination>()

    // Información de los marcadores de las rutas.
    private val informacionMarcadoresRuta =
        mutableMapOf<String, String>()

    // Token para cancelar solicitudes de ubicación.
    private var cancellationTokenSource:
            CancellationTokenSource? = null

    // ==========================================
    // 2. MODELO INTERNO DE RUTA
    // ==========================================

    /**
     * Representa un trazado referencial entre
     * un punto de encuentro y un punto turístico.
     */
    private data class RutaTuristica(

        val nombre: String,

        val puntoEncuentro: String,

        val coordenadaEncuentro: LatLng,

        val coordenadaDestino: LatLng,

        val puntosIntermedios: List<LatLng> = emptyList(),

        val nombreMarcadorFinal: String? = null

    )

    // ==========================================
    // 3. SOLICITUD DE PERMISOS DE UBICACIÓN
    // ==========================================

    private val solicitudPermisosUbicacion =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->

            val permisoPreciso =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val permisoAproximado =
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (permisoPreciso || permisoAproximado) {

                Log.d(
                    TAG,
                    "Permiso de ubicación concedido."
                )

                obtenerUbicacionActual()

            } else {

                Log.w(
                    TAG,
                    "El usuario denegó los permisos de ubicación."
                )

                if (isAdded) {

                    Toast.makeText(
                        requireContext(),
                        "Necesitamos permiso de ubicación para mostrar tu posición.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    // ==========================================
    // 4. INICIALIZAR MAPLIBRE
    // ==========================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        MapLibre.getInstance(
            requireContext().applicationContext
        )

        Log.d(
            TAG,
            "MapLibre inicializado correctamente."
        )
    }

    // ==========================================
    // 5. CREAR LA PANTALLA
    // ==========================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(
            TAG,
            "Creando pantalla del mapa."
        )

        return inflater.inflate(
            R.layout.fragment_map,
            container,
            false
        )
    }

    // ==========================================
    // 6. PREPARAR MAPVIEW Y BOTÓN
    // ==========================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        // Obtener MapView desde XML.
        val mapa = view.findViewById<MapView>(
            R.id.mapView
        )

        mapView = mapa

        // Obtener botón flotante.
        fabMiUbicacion = view.findViewById(
            R.id.fabMiUbicacion
        )

        // Inicializar ciclo de vida.
        mapa.onCreate(savedInstanceState)

        // Configurar botón de ubicación.
        fabMiUbicacion?.setOnClickListener {

            verificarPermisoUbicacion()
        }

        Log.d(
            TAG,
            "MapView y botón de ubicación preparados."
        )

        // Esperar a que MapLibre esté disponible.
        mapa.getMapAsync { mapaListo ->

            if (!isAdded || mapView !== mapa) {

                return@getMapAsync
            }

            mapLibreMap = mapaListo

            Log.d(
                TAG,
                "Mapa disponible. Cargando estilo."
            )

            cargarEstiloMapa(
                mapaListo
            )
        }
    }

    // ==========================================
    // 7. CARGAR ESTILO OPENFREEMAP
    // ==========================================

    private fun cargarEstiloMapa(
        mapa: MapLibreMap
    ) {

        val estilo = Style.Builder()
            .fromUri(MAP_STYLE_URL)

        mapa.setStyle(
            estilo,
            object : Style.OnStyleLoaded {

                override fun onStyleLoaded(
                    style: Style
                ) {

                    if (!isAdded || mapLibreMap !== mapa) {
                        return
                    }

                    Log.d(
                        TAG,
                        "Estilo Liberty cargado correctamente."
                    )

                    // Centrar mapa en el sur del Perú.
                    centrarMapaEnSurPeru(mapa)

                    // Dibujar las cuatro rutas.
                    agregarRutasTuristicas(mapa)

                    // Agregar marcadores de los destinos.
                    agregarMarcadoresTuristicos(mapa)

                    // Configurar eventos de las ventanas.
                    configurarEventosMarcadores(mapa)

                    Log.d(
                        TAG,
                        "Mapa turístico completamente inicializado."
                    )
                }
            }
        )
    }

    // ==========================================
    // 8. CENTRAR MAPA EN EL SUR DEL PERÚ
    // ==========================================

    private fun centrarMapaEnSurPeru(
        mapa: MapLibreMap
    ) {

        val posicionInicial =
            CameraPosition.Builder()

                .target(
                    LatLng(
                        -13.52,
                        -73.78
                    )
                )

                .zoom(ZOOM_INICIAL)

                .build()

        mapa.cameraPosition = posicionInicial

        Log.d(
            TAG,
            "Mapa centrado en el sur del Perú."
        )
    }

    // ==========================================
    // 9. OBTENER COORDENADAS POR DESTINO
    // ==========================================

    private fun obtenerCoordenadas(
        destino: Destination
    ): LatLng? {

        return when (destino.id) {

            // MACHU PICCHU.
            1 -> LatLng(
                -13.1631,
                -72.5456
            )

            // LAGUNA HUMANTAY.
            2 -> LatLng(
                -13.411589,
                -72.617342
            )

            // MONTAÑA DE SIETE COLORES.
            3 -> LatLng(
                -13.869534,
                -71.30296
            )

            // RESERVA NACIONAL DE PARACAS.
            4 -> LatLng(
                -13.8350,
                -76.2500
            )

            else -> null
        }
    }

    // ==========================================
    // 10. DEFINIR RUTAS TURÍSTICAS
    // ==========================================

    /**
     * Las coordenadas son puntos geográficos
     * de referencia proporcionados para el proyecto.
     *
     * Las líneas son esquemáticas y no representan
     * rutas viales ni senderos calculados.
     */
    private fun obtenerRutasTuristicas():
            List<RutaTuristica> {

        return listOf(

            // ==================================
            // RUTA 1: MACHU PICCHU
            // ==================================

            RutaTuristica(

                nombre = "Aguas Calientes → Machu Picchu",

                puntoEncuentro = "Aguas Calientes, Cusco",

                coordenadaEncuentro = LatLng(
                    -13.1547,
                    -72.5254
                ),

                coordenadaDestino = LatLng(
                    -13.1631,
                    -72.5456
                )
            ),

            // ==================================
            // RUTA 2: LAGUNA HUMANTAY
            // ==================================

            RutaTuristica(

                nombre = "Mollepata → Laguna Humantay",

                puntoEncuentro = "Mollepata, Cusco",

                coordenadaEncuentro = LatLng(
                    -13.50863,
                    -72.52764
                ),

                coordenadaDestino = LatLng(
                    -13.411589,
                    -72.617342
                )
            ),

            // ==================================
            // RUTA 3: MONTAÑA DE SIETE COLORES
            // ==================================

            RutaTuristica(

                nombre = "Cusipata → Vinicunca",

                puntoEncuentro = "Cusipata, Cusco",

                coordenadaEncuentro = LatLng(
                    -13.90664,
                    -71.5028
                ),

                coordenadaDestino = LatLng(
                    -13.869534,
                    -71.30296
                )
            ),

            // ==================================
            // RUTA 4: RESERVA NACIONAL DE PARACAS
            // ==================================

            RutaTuristica(

                nombre = "El Chaco → Mirador Istmo → Playa Roja",

                puntoEncuentro = "El Chaco, Paracas",

                coordenadaEncuentro = LatLng(
                    -13.83539,
                    -76.24856
                ),

                coordenadaDestino = LatLng(
                    -13.89258,
                    -76.30708
                ),

                puntosIntermedios = listOf(

                    // MIRADOR ISTMO.
                    LatLng(
                        -13.89455,
                        -76.29622
                    )
                ),

                nombreMarcadorFinal =
                    "Playa Roja - Reserva Nacional de Paracas"
            )
        )
    }

    // ==========================================
    // 11. DIBUJAR RUTAS TURÍSTICAS
    // ==========================================

    /**
     * Dibuja las líneas referenciales y agrega
     * marcadores para puntos de encuentro.
     */
    @Suppress("DEPRECATION")
    private fun agregarRutasTuristicas(
        mapa: MapLibreMap
    ) {

        val rutas = obtenerRutasTuristicas()

        informacionMarcadoresRuta.clear()

        Log.d(
            TAG,
            "Iniciando dibujo de ${rutas.size} rutas referenciales."
        )

        var rutasDibujadas = 0

        rutas.forEach { ruta ->

            // ==================================
            // CONSTRUIR COORDENADAS
            // ==================================

            val puntos = mutableListOf<LatLng>()

            // Primer punto: encuentro.
            puntos.add(
                ruta.coordenadaEncuentro
            )

            // Puntos intermedios.
            puntos.addAll(
                ruta.puntosIntermedios
            )

            // Último punto: destino.
            puntos.add(
                ruta.coordenadaDestino
            )

            // Validar cantidad de coordenadas.
            if (puntos.size < 2) {

                Log.w(
                    TAG,
                    "Ruta descartada por falta de coordenadas: ${ruta.nombre}"
                )

                return@forEach
            }

            try {

                // ==================================
                // DIBUJAR LÍNEA NARANJA
                // ==================================

                val opcionesLinea =
                    PolylineOptions()

                        .addAll(puntos)

                        .color(
                            Color.parseColor(COLOR_RUTA)
                        )

                        .width(
                            ANCHO_RUTA
                        )

                        .alpha(0.90f)

                mapa.addPolyline(
                    opcionesLinea
                )

                // ==================================
                // MARCADOR DEL PUNTO DE ENCUENTRO
                // ==================================

                val tituloEncuentro =
                    "Encuentro: ${ruta.puntoEncuentro}"

                mapa.addMarker(

                    MarkerOptions()

                        .position(
                            ruta.coordenadaEncuentro
                        )

                        .title(
                            tituloEncuentro
                        )

                        .snippet(
                            "Inicio referencial · ${ruta.nombre}"
                        )
                )

                informacionMarcadoresRuta[
                    tituloEncuentro
                ] =
                    "Punto de encuentro: ${ruta.puntoEncuentro}\n" +
                            "Recorrido: ${ruta.nombre}"

                // ==================================
                // MARCADORES INTERMEDIOS
                // ==================================

                ruta.puntosIntermedios.forEachIndexed {
                        indice,
                        coordenada ->

                    val nombrePunto = if (
                        ruta.nombre.contains("Paracas")
                    ) {

                        "Mirador Istmo"

                    } else {

                        "Punto intermedio ${indice + 1}"
                    }

                    mapa.addMarker(

                        MarkerOptions()

                            .position(coordenada)

                            .title(nombrePunto)

                            .snippet(
                                "Punto intermedio de ${ruta.nombre}"
                            )
                    )

                    informacionMarcadoresRuta[
                        nombrePunto
                    ] =
                        "$nombrePunto\n${ruta.nombre}"
                }

                // ==================================
                // MARCADOR FINAL ESPECIAL
                // ==================================

                ruta.nombreMarcadorFinal?.let { nombreFinal ->

                    val tituloFinal =
                        "Final: $nombreFinal"

                    mapa.addMarker(

                        MarkerOptions()

                            .position(
                                ruta.coordenadaDestino
                            )

                            .title(tituloFinal)

                            .snippet(
                                "Punto final referencial de ${ruta.nombre}"
                            )
                    )

                    informacionMarcadoresRuta[
                        tituloFinal
                    ] =
                        "Destino final: $nombreFinal"
                }

                rutasDibujadas++

                Log.d(
                    TAG,
                    "Ruta dibujada: ${ruta.nombre}"
                )

            } catch (error: Exception) {

                Log.e(
                    TAG,
                    "Error dibujando la ruta ${ruta.nombre}.",
                    error
                )
            }
        }

        Log.d(
            TAG,
            "Total de rutas dibujadas: $rutasDibujadas"
        )
    }

    // ==========================================
    // 12. AGREGAR MARCADORES TURÍSTICOS
    // ==========================================

    @Suppress("DEPRECATION")
    private fun agregarMarcadoresTuristicos(
        mapa: MapLibreMap
    ) {

        destinosPorMarcador.clear()

        val destinos = DestinationData.destinations

        Log.d(
            TAG,
            "Iniciando carga de ${destinos.size} destinos del catálogo."
        )

        destinos.forEach { destino ->

            val coordenadas = obtenerCoordenadas(
                destino
            )

            if (coordenadas == null) {

                Log.w(
                    TAG,
                    "No existen coordenadas para: ${destino.name}"
                )

                return@forEach
            }

            // Crear marcador del destino.
            mapa.addMarker(

                MarkerOptions()

                    .position(coordenadas)

                    .title(destino.name)

                    .snippet(
                        "${destino.location} · ${destino.category}"
                    )
            )

            // Guardar asociación con destino.
            destinosPorMarcador[
                destino.name
            ] = destino

            Log.d(
                TAG,
                "Marcador turístico agregado: ${destino.name}"
            )
        }

        Log.d(
            TAG,
            "Total de marcadores turísticos: ${destinosPorMarcador.size}"
        )
    }

    // ==========================================
    // 13. CONFIGURAR EVENTOS DE MARCADORES
    // ==========================================

    private fun configurarEventosMarcadores(
        mapa: MapLibreMap
    ) {

        mapa.setOnInfoWindowClickListener { marcador ->

            Log.d(
                TAG,
                "Ventana seleccionada: ${marcador.title}"
            )

            // Primero comprobar si es un destino.
            val destino = destinosPorMarcador[
                marcador.title
            ]

            if (destino != null) {

                abrirFichaDestino(destino)

                true

            } else {

                // Si no es un destino, comprobar rutas.
                val informacionRuta =
                    informacionMarcadoresRuta[
                        marcador.title
                    ]

                if (informacionRuta != null) {

                    if (isAdded) {

                        Toast.makeText(
                            requireContext(),
                            informacionRuta,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    Log.d(
                        TAG,
                        "Marcador de ruta seleccionado: ${marcador.title}"
                    )

                    true

                } else if (
                    marcador.title == "Mi ubicación actual"
                ) {

                    if (isAdded) {

                        Toast.makeText(
                            requireContext(),
                            marcador.snippet ?: "Ubicación del dispositivo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true

                } else {

                    false
                }
            }
        }

        Log.d(
            TAG,
            "Eventos de marcadores configurados."
        )
    }

    // ==========================================
    // 14. ABRIR FICHA DETALLADA DEL DESTINO
    // ==========================================

    private fun abrirFichaDestino(
        destino: Destination
    ) {

        if (!isAdded) return

        Log.d(
            TAG,
            "Abriendo ficha: ${destino.name}"
        )

        try {

            val coordenadas = obtenerCoordenadas(
                destino
            )

            val intent = Intent(
                requireContext(),
                DestinationDetailActivity::class.java
            ).apply {

                // Identificador.
                putExtra(
                    DestinationDetailActivity.EXTRA_ID,
                    destino.id
                )

                // Nombre.
                putExtra(
                    DestinationDetailActivity.EXTRA_NAME,
                    destino.name
                )

                // Ubicación.
                putExtra(
                    DestinationDetailActivity.EXTRA_LOCATION,
                    destino.location
                )

                // Categoría.
                putExtra(
                    DestinationDetailActivity.EXTRA_CATEGORY,
                    destino.category
                )

                // Descripción.
                putExtra(
                    DestinationDetailActivity.EXTRA_DESCRIPTION,
                    destino.description
                )

                // Precio.
                putExtra(
                    DestinationDetailActivity.EXTRA_PRICE,
                    destino.price
                )

                // Duración.
                putExtra(
                    DestinationDetailActivity.EXTRA_DURATION,
                    destino.duration
                )

                // Dificultad.
                putExtra(
                    DestinationDetailActivity.EXTRA_DIFFICULTY,
                    destino.difficulty
                )

                // Punto de encuentro.
                putExtra(
                    DestinationDetailActivity.EXTRA_MEETING_POINT,
                    destino.meetingPoint
                )

                // Actividades.
                putExtra(
                    DestinationDetailActivity.EXTRA_ACTIVITIES,
                    destino.activities
                )

                // Imagen.
                putExtra(
                    DestinationDetailActivity.EXTRA_IMAGE_RES_ID,
                    destino.imageResId
                )

                // Transferir latitud y longitud.
                if (coordenadas != null) {

                    putExtra(
                        DestinationDetailActivity.EXTRA_LATITUDE,
                        coordenadas.latitude
                    )

                    putExtra(
                        DestinationDetailActivity.EXTRA_LONGITUDE,
                        coordenadas.longitude
                    )
                }
            }

            if (coordenadas != null) {

                Log.d(
                    TAG,
                    "Coordenadas transferidas para ${destino.name}: " +
                            "Latitud=${coordenadas.latitude}, " +
                            "Longitud=${coordenadas.longitude}"
                )
            }

            startActivity(intent)

            Log.d(
                TAG,
                "Ficha abierta correctamente: ${destino.name}"
            )

        } catch (error: Exception) {

            Log.e(
                TAG,
                "Error al abrir ficha turística.",
                error
            )

            if (isAdded) {

                Toast.makeText(
                    requireContext(),
                    "No se pudo abrir la ficha del destino.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ==========================================
    // 15. COMPROBAR PERMISOS DE UBICACIÓN
    // ==========================================

    private fun verificarPermisoUbicacion() {

        if (!isAdded) return

        val contexto = requireContext()

        val permisoPreciso =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val permisoAproximado =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (permisoPreciso || permisoAproximado) {

            Log.d(
                TAG,
                "El permiso ya está concedido."
            )

            obtenerUbicacionActual()

        } else {

            Log.d(
                TAG,
                "Solicitando permiso de ubicación al usuario."
            )

            solicitudPermisosUbicacion.launch(

                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ==========================================
    // 16. OBTENER UBICACIÓN ACTUAL
    // ==========================================

    private fun obtenerUbicacionActual() {

        if (!isAdded) return

        val contexto = requireContext()

        val mapa = mapLibreMap

        // Comprobar que el mapa esté listo.
        if (mapa == null || mapa.style == null) {

            Toast.makeText(
                contexto,
                "El mapa todavía está cargando. Inténtalo nuevamente.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Comprobar permisos.
        val permisoPreciso =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val permisoAproximado =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!permisoPreciso && !permisoAproximado) {

            Log.w(
                TAG,
                "No existe permiso para consultar ubicación."
            )

            Toast.makeText(
                contexto,
                "No se ha concedido el permiso de ubicación.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        try {

            // Obtener proveedor de ubicación.
            val proveedorUbicacion =
                LocationServices.getFusedLocationProviderClient(
                    contexto
                )

            // Elegir precisión según el permiso.
            val prioridad = if (permisoPreciso) {

                Priority.PRIORITY_HIGH_ACCURACY

            } else {

                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }

            Log.d(
                TAG,
                "Solicitando ubicación al dispositivo."
            )

            // Cancelar consulta anterior, si existe.
            cancellationTokenSource?.cancel()

            // Crear nuevo token.
            val token = CancellationTokenSource()

            cancellationTokenSource = token

            // Solicitar ubicación actual.
            proveedorUbicacion.getCurrentLocation(
                prioridad,
                token.token
            )
                .addOnSuccessListener { ubicacion ->

                    if (!isAdded) return@addOnSuccessListener

                    if (ubicacion != null) {

                        val latitud = ubicacion.latitude

                        val longitud = ubicacion.longitude

                        Log.d(
                            TAG,
                            "Ubicación obtenida: " +
                                    "Latitud=$latitud, " +
                                    "Longitud=$longitud"
                        )

                        mostrarUbicacionEnMapa(
                            latitud,
                            longitud
                        )

                    } else {

                        Log.w(
                            TAG,
                            "El proveedor no devolvió una ubicación."
                        )

                        Toast.makeText(
                            requireContext(),
                            "No se obtuvo ubicación. Activa la ubicación del dispositivo o configura una ubicación simulada.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { error ->

                    if (!isAdded) return@addOnFailureListener

                    Log.e(
                        TAG,
                        "Error al obtener ubicación.",
                        error
                    )

                    Toast.makeText(
                        requireContext(),
                        "No se pudo obtener la ubicación del dispositivo.",
                        Toast.LENGTH_LONG
                    ).show()
                }

        } catch (error: SecurityException) {

            Log.e(
                TAG,
                "Permiso de ubicación insuficiente.",
                error
            )

            Toast.makeText(
                contexto,
                "Verifica los permisos de ubicación.",
                Toast.LENGTH_LONG
            ).show()

        } catch (error: Exception) {

            Log.e(
                TAG,
                "Error inesperado al consultar ubicación.",
                error
            )

            Toast.makeText(
                contexto,
                "Ocurrió un error al consultar la ubicación.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // 17. MOSTRAR UBICACIÓN EN EL MAPA
    // ==========================================

    @Suppress("DEPRECATION")
    private fun mostrarUbicacionEnMapa(
        latitud: Double,
        longitud: Double
    ) {

        if (!isAdded) return

        val mapa = mapLibreMap ?: return

        if (mapa.style == null) return

        // Validar coordenadas.
        if (
            !latitud.isFinite() ||
            !longitud.isFinite() ||
            latitud !in -90.0..90.0 ||
            longitud !in -180.0..180.0
        ) {

            Toast.makeText(
                requireContext(),
                "La ubicación recibida no es válida.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val coordenadas = LatLng(
            latitud,
            longitud
        )

        // Eliminar marcador anterior.
        marcadorUbicacionUsuario?.let { marcadorAnterior ->

            mapa.removeMarker(
                marcadorAnterior
            )
        }

        // Agregar marcador de ubicación actual.
        marcadorUbicacionUsuario = mapa.addMarker(

            MarkerOptions()

                .position(coordenadas)

                .title("Mi ubicación actual")

                .snippet(
                    String.format(
                        Locale.US,
                        "Latitud: %.5f · Longitud: %.5f",
                        latitud,
                        longitud
                    )
                )
        )

        // Centrar la cámara.
        mapa.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                coordenadas,
                ZOOM_UBICACION
            ),
            1200
        )

        Log.d(
            TAG,
            "Marcador de ubicación agregado correctamente."
        )

        Toast.makeText(
            requireContext(),
            "Ubicación encontrada y mostrada en el mapa.",
            Toast.LENGTH_LONG
        ).show()
    }

    // ==========================================
    // 18. CICLO DE VIDA: INICIAR
    // ==========================================

    override fun onStart() {

        super.onStart()

        mapView?.onStart()
    }

    // ==========================================
    // 19. CICLO DE VIDA: REANUDAR
    // ==========================================

    override fun onResume() {

        super.onResume()

        mapView?.onResume()
    }

    // ==========================================
    // 20. CICLO DE VIDA: PAUSAR
    // ==========================================

    override fun onPause() {

        mapView?.onPause()

        super.onPause()
    }

    // ==========================================
    // 21. CICLO DE VIDA: DETENER
    // ==========================================

    override fun onStop() {

        mapView?.onStop()

        super.onStop()
    }

    // ==========================================
    // 22. GUARDAR ESTADO
    // ==========================================

    override fun onSaveInstanceState(
        outState: Bundle
    ) {

        mapView?.onSaveInstanceState(
            outState
        )

        super.onSaveInstanceState(
            outState
        )
    }

    // ==========================================
    // 23. GESTIONAR POCA MEMORIA
    // ==========================================

    override fun onLowMemory() {

        super.onLowMemory()

        mapView?.onLowMemory()
    }

    // ==========================================
    // 24. LIBERAR RECURSOS
    // ==========================================

    override fun onDestroyView() {

        // Cancelar solicitud de ubicación pendiente.
        cancellationTokenSource?.cancel()

        cancellationTokenSource = null

        // Desconectar botón.
        fabMiUbicacion?.setOnClickListener(null)

        fabMiUbicacion = null

        // Liberar marcadores.
        marcadorUbicacionUsuario = null

        destinosPorMarcador.clear()

        informacionMarcadoresRuta.clear()

        // Destruir MapView.
        mapView?.onDestroy()

        mapView = null

        mapLibreMap = null

        Log.d(
            TAG,
            "Recursos del mapa liberados correctamente."
        )

        super.onDestroyView()
    }
}
