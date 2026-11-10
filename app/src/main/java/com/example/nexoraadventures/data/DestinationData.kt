package com.example.nexoraadventures.data

import com.example.nexoraadventures.R
import com.example.nexoraadventures.model.Destination

/**
 * Repositorio local de destinos turísticos.
 *
 * Contiene los destinos y sus fotografías
 * para el catálogo de NEXORA ADVENTURES.
 *
 * Este archivo contiene una única declaración
 * del objeto DestinationData.
 */
object DestinationData {

    // Lista de destinos turísticos disponibles
    val destinations: List<Destination> = listOf(

        // ==================================
        // 1. MACHU PICCHU
        // ==================================

        Destination(
            id = 1,
            name = "Machu Picchu",
            location = "Cusco, Perú",
            category = "Turismo cultural",
            description = "Descubre la histórica ciudadela inca de Machu Picchu, " +
                    "rodeada de montañas y paisajes andinos. " +
                    "Una experiencia cultural y arqueológica inolvidable.",
            price = 250.00,
            duration = "1 día",
            difficulty = "Moderado",
            meetingPoint = "Aguas Calientes, Cusco",
            activities = "Visita arqueológica, caminata y fotografía",
            imageResId = R.drawable.dest_machu_picchu
        ),

        // ==================================
        // 2. LAGUNA HUMANTAY
        // ==================================

        Destination(
            id = 2,
            name = "Laguna Humantay",
            location = "Cusco, Perú",
            category = "Turismo de aventura",
            description = "Explora una impresionante laguna de aguas turquesas " +
                    "rodeada de montañas nevadas. Ideal para disfrutar " +
                    "de la naturaleza y realizar senderismo en los Andes.",
            price = 180.00,
            duration = "1 día",
            difficulty = "Exigente",
            meetingPoint = "Mollepata, Cusco",
            activities = "Senderismo, fotografía y observación del paisaje",
            imageResId = R.drawable.dest_laguna_humantay
        ),

        // ==================================
        // 3. MONTAÑA DE SIETE COLORES
        // ==================================

        Destination(
            id = 3,
            name = "Montaña de Siete Colores",
            location = "Vinicunca, Cusco, Perú",
            category = "Turismo de naturaleza",
            description = "Conoce la famosa montaña Vinicunca, reconocida " +
                    "por sus franjas de colores naturales y sus " +
                    "impresionantes paisajes de altura.",
            price = 200.00,
            duration = "1 día",
            difficulty = "Exigente",
            meetingPoint = "Cusipata, Cusco",
            activities = "Trekking, fotografía y observación de paisajes",
            imageResId = R.drawable.dest_montana_siete_colores
        ),

        // ==================================
        // 4. RESERVA NACIONAL DE PARACAS
        // ==================================

        Destination(
            id = 4,
            name = "Reserva Nacional de Paracas",
            location = "Ica, Perú",
            category = "Turismo ecológico",
            description = "Explora los paisajes costeros de Paracas, " +
                    "sus playas, formaciones naturales y diversidad " +
                    "de fauna marina frente al océano Pacífico.",
            price = 150.00,
            duration = "1 día",
            difficulty = "Fácil",
            meetingPoint = "Embarcadero El Chaco, Paracas",
            activities = "Paseo turístico, fotografía y observación de fauna",
            imageResId = R.drawable.dest_paracas
        )

    )

    // Buscar destino por identificador
    fun getDestinationById(id: Int): Destination? {

        return destinations.find { destination ->
            destination.id == id
        }
    }

    // Buscar destinos por nombre, ubicación o categoría
    fun searchDestinations(query: String): List<Destination> {

        if (query.isBlank()) {
            return destinations
        }

        return destinations.filter { destination ->

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
    }
}
