package com.example.proyectodesqlite.bd;

import android.provider.BaseColumns;

/**
 * Clase de contrato que define los nombres de las tablas y las columnas de la base de datos.
 */
public final class PetContract {

    // Constructor privado para evitar instanciación accidental
    private PetContract() {}

    /**
     * Clase interna que define el contenido de la tabla de Dueños.
     */
    public static abstract class DuenosEntry implements BaseColumns {
        public static final String TABLE_NAME = "duenos";
        public static final String _ID = BaseColumns._ID; // ID de la fila
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_TELEFONO = "telefono";
    }

    /**
     * Clase interna que define el contenido de la tabla de Razas.
     */
    public static abstract class RazasEntry implements BaseColumns {
        public static final String TABLE_NAME = "razas";
        public static final String _ID = BaseColumns._ID; // ID de la fila
        public static final String COLUMN_NOMBRE = "nombre";
    }

    /**
     * Clase interna que define el contenido de la tabla de Mascotas.
     */
    public static abstract class MascotasEntry implements BaseColumns {
        public static final String TABLE_NAME = "mascotas";
        public static final String _ID = BaseColumns._ID; // ID de la fila
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_EDAD = "edad";

        // 🚨 CONSTANTE FALTANTE AÑADIDA PARA RESOLVER EL ERROR DE COMPILACIÓN
        public static final String COLUMN_DUENO_ID = "dueno_id";

        public static final String COLUMN_RAZA_ID = "raza_id";
    }
}
