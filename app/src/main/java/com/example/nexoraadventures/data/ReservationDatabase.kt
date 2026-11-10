package com.example.nexoraadventures.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Base de datos principal de NEXORA ADVENTURES.
 *
 * Administra:
 * - Reservas turísticas.
 * - Trabajadores.
 *
 * Versión 2:
 * Incorpora la tabla workers.
 */
@Database(
    entities = [
        ReservationEntity::class,
        WorkerEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ReservationDatabase : RoomDatabase() {

    // ==========================================
    // 1. DAO DE RESERVAS
    // ==========================================

    abstract fun reservationDao(): ReservationDao

    // ==========================================
    // 2. DAO DE TRABAJADORES
    // ==========================================

    abstract fun workerDao(): WorkerDao

    // ==========================================
    // 3. MIGRACIÓN DE VERSIÓN 1 A 2
    // ==========================================

    companion object {

        /**
         * Agrega la tabla workers.
         *
         * Conserva todos los datos existentes
         * de la tabla reservations.
         */
        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    // Crear tabla de trabajadores.

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `workers` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `nombres` TEXT NOT NULL,
                            `apellidos` TEXT NOT NULL,
                            `documento` TEXT NOT NULL,
                            `telefono` TEXT NOT NULL,
                            `correo` TEXT NOT NULL,
                            `cargo` TEXT NOT NULL,
                            `area` TEXT NOT NULL,
                            `fechaContratacion` TEXT NOT NULL,
                            `sueldoMensual` REAL NOT NULL,
                            `estadoLaboral` TEXT NOT NULL
                        )
                        """.trimIndent()
                    )

                    // Crear índice único del documento.

                    database.execSQL(
                        """
                        CREATE UNIQUE INDEX IF NOT EXISTS
                        `index_workers_documento`
                        ON `workers` (`documento`)
                        """.trimIndent()
                    )
                }
            }

        // ======================================
        // 4. INSTANCIA ÚNICA
        // ======================================

        @Volatile
        private var INSTANCE: ReservationDatabase? = null

        // ======================================
        // 5. OBTENER BASE DE DATOS
        // ======================================

        fun obtenerBaseDeDatos(
            context: Context
        ): ReservationDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    ReservationDatabase::class.java,
                    "nexora_adventures.db"
                )
                    .addMigrations(
                        MIGRATION_1_2
                    )
                    .build()

                INSTANCE = instancia

                instancia
            }
        }
    }
}
