package com.example.proyectodesqlite.bd;

import android.provider.BaseColumns;

/**
 * Clase de contrato que define los nombres de las tablas y las columnas de la base de datos.
 */
public final class PetContract {

    // Constructor privado para evitar instanciación accidental
    private PetContract() {}

    // Información general de la base de datos (útil para DBHelper)
    public static final String DATABASE_NAME = "pet_clinic.db";
    // Versión incrementada para forzar la recreación de tablas y añadir la columna 'edad'
    public static final int DATABASE_VERSION = 26;

    // ------------------------------------------------------------------
    /**
     * Clase interna que define el contenido de la tabla de Dueños.
     */
    public static class OwnerEntry implements BaseColumns {
        public static final String TABLE_NAME = "duenos";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_TELEFONO = "telefono";
        public static final String COLUMN_NAME_PHOTO_URI = "photo_uri";
    }

    // ------------------------------------------------------------------
    /**
     * Clase interna que define el contenido de la tabla de Razas.
     */
    public static class RazaEntry implements BaseColumns {
        public static final String TABLE_NAME = "razas";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
    }

    // ------------------------------------------------------------------
    /**
     * Clase interna que define el contenido de la tabla de Mascotas.
     */
    public static class MascotasEntry implements BaseColumns {
        public static final String TABLE_NAME = "mascotas";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_EDAD = "edad"; // Columna 'edad' requerida

        // Claves Foráneas
        public static final String COLUMN_NAME_DUENO_ID = "dueno_id";
        public static final String COLUMN_NAME_RAZA_ID = "raza_id";

        public static final String COLUMN_NAME_PHOTO_URI = "photo_uri";
    }

    // ------------------------------------------------------------------
    /**
     * Clase interna que define el contenido de la tabla de Citas (Agenda).
     */
    public static class CitasEntry implements BaseColumns {
        public static final String TABLE_NAME = "citas";
        // 🚨 CAMBIO CRÍTICO: Añadimos el ID de la mascota (FK a MascotasEntry)
        public static final String COLUMN_NAME_PET_ID = "pet_id";

        public static final String COLUMN_NAME_DATE = "fecha";
        public static final String COLUMN_NAME_TIME = "hora";
        public static final String COLUMN_NAME_REASON = "motivo";
        // ❌ Eliminado: COLUMN_NAME_OWNER_ID - Se obtiene a través de la mascota.
    }
}
