package com.example.proyectodesqlite;

import android.provider.BaseColumns;

public final class PetContract {

    private PetContract() {}

    /* Tabla: dueños */
    public static class DuenosEntry implements BaseColumns {
        public static final String TABLE_NAME = "duenos";
        public static final String COLUMN_ID = "id_dueño"; // Nuevo PK
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_TELEFONO = "teléfono"; // Nuevo
        public static final String COLUMN_EDAD_DUENO = "edad"; // Nuevo
        public static final String COLUMN_CANT_MASCOTAS = "cant_mascotas"; // Nuevo
    }

    /* Tabla: razas */
    public static class RazasEntry implements BaseColumns {
        public static final String TABLE_NAME = "razas";
        public static final String COLUMN_ID = "id_raza"; // Nuevo PK
        public static final String COLUMN_TIPO_MASCOTA = "tipo_mascota"; // Nuevo
        public static final String COLUMN_NOMBRE_RAZA = "nombre_raza"; // Actualizado
    }

    /* Tabla: mascotas (con claves foráneas) */
    public static class MascotasEntry implements BaseColumns {
        public static final String TABLE_NAME = "mascotas";
        public static final String COLUMN_ID = "id_mascota"; // Nuevo PK
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_EDAD_MASCOTA = "edad_mascota"; // Actualizado
        public static final String COLUMN_FK_ID_DUENO = "fk_id_dueño"; // Nuevo FK
        public static final String COLUMN_FK_ID_RAZA = "fk_id_raza"; // Nuevo FK
    }
}