package com.example.proyectodesqlite;

import android.provider.BaseColumns;

public final class PetContract {

    private PetContract() {}

    /* Tabla: dueños */
    public static class DuenosEntry implements BaseColumns {
        public static final String TABLE_NAME = "duenos";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NOMBRE = "nombre";
    }

    /* Tabla: razas */
    public static class RazasEntry implements BaseColumns {
        public static final String TABLE_NAME = "razas";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NOMBRE = "nombre";
    }

    /* Tabla: mascotas (con claves foráneas) */
    public static class MascotasEntry implements BaseColumns {
        public static final String TABLE_NAME = "mascotas";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_ID_DUENO = "id_dueno";
        public static final String COLUMN_ID_RAZA = "id_raza";
    }
}