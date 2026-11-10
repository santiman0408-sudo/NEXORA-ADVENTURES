package com.example.nexoraadventures.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Administra el almacenamiento local de las consultas
 * recibidas por NEXORA ADVENTURES.
 */
class ContactDbHelper(
    context: Context
) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {

        private const val DATABASE_NAME =
            "nexora_contacts.db"

        private const val DATABASE_VERSION = 1

        private const val TABLE_CONTACTOS =
            "contactos"

        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_TELEFONO = "telefono"
        private const val COL_ASUNTO = "asunto"
        private const val COL_MENSAJE = "mensaje"
        private const val COL_FECHA = "fechaRegistro"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val crearTabla = """
            CREATE TABLE $TABLE_CONTACTOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL,
                $COL_TELEFONO TEXT NOT NULL,
                $COL_ASUNTO TEXT NOT NULL,
                $COL_MENSAJE TEXT NOT NULL,
                $COL_FECHA INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(crearTabla)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Reservado para futuras migraciones.
    }

    /**
     * Guarda una consulta en la base local.
     *
     * @return ID generado o -1 si no se pudo insertar.
     */
    fun insertarConsulta(
        nombre: String,
        correo: String,
        telefono: String,
        asunto: String,
        mensaje: String
    ): Long {

        val valores = ContentValues().apply {

            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_TELEFONO, telefono)
            put(COL_ASUNTO, asunto)
            put(COL_MENSAJE, mensaje)
            put(COL_FECHA, System.currentTimeMillis())
        }

        return writableDatabase.insert(
            TABLE_CONTACTOS,
            null,
            valores
        )
    }
}


