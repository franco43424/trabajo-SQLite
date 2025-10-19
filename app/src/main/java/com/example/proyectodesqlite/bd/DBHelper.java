package com.example.proyectodesqlite.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.proyectodesqlite.bd.PetContract.CitasEntry;
import com.example.proyectodesqlite.bd.PetContract.MascotasEntry;
import com.example.proyectodesqlite.bd.PetContract.OwnerEntry;
import com.example.proyectodesqlite.bd.PetContract.RazaEntry;

/**
 * Clase que gestiona la creación y actualización de la base de datos (DB Helper).
 * Utiliza las constantes definidas en PetContract.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Necesario para que las claves foráneas funcionen
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Habilitar claves foráneas
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Importante: Eliminar primero las tablas que tienen claves foráneas a otras
        db.execSQL("DROP TABLE IF EXISTS " + CitasEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MascotasEntry.TABLE_NAME);

        // Eliminar Dueños y Razas
        db.execSQL("DROP TABLE IF EXISTS " + OwnerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RazaEntry.TABLE_NAME);

        // Volver a crear la base de datos con el nuevo esquema
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecutar las sentencias de creación
        // El orden DEBE mantenerse: Raza -> Dueño -> Mascota -> Citas
        // para que las Claves Foráneas funcionen.
        db.execSQL(SQL_CREATE_RAZAS_TABLE);
        db.execSQL(SQL_CREATE_DUENOS_TABLE);
        db.execSQL(SQL_CREATE_MASCOTAS_TABLE);
        db.execSQL(SQL_CREATE_CITAS_TABLE);

        Log.d(TAG, "Base de datos y tablas creadas exitosamente.");
    }

    /**
     * Sentencia SQL para crear la tabla de Citas (Agenda).
     * Ahora usa la clave foránea a la tabla de Mascotas.
     */
    private static final String SQL_CREATE_CITAS_TABLE = "CREATE TABLE " + CitasEntry.TABLE_NAME + " ("
            + CitasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CitasEntry.COLUMN_NAME_DATE + " TEXT NOT NULL,"
            + CitasEntry.COLUMN_NAME_TIME + " TEXT NOT NULL,"
            + CitasEntry.COLUMN_NAME_REASON + " TEXT,"
            // ID de la Mascota, referenciando la tabla Mascotas
            + CitasEntry.COLUMN_NAME_PET_ID + " INTEGER NOT NULL,"

            // Definición de Clave Foránea a la tabla Mascotas
            + " FOREIGN KEY (" + CitasEntry.COLUMN_NAME_PET_ID + ") REFERENCES " + MascotasEntry.TABLE_NAME + "(" + MascotasEntry._ID + ") ON DELETE CASCADE"
            + ");";


    /**
     * Sentencia SQL para crear la tabla de Mascotas.
     * Incluye claves foráneas (FOREIGN KEY) a Dueños y Razas.
     */
    private static final String SQL_CREATE_MASCOTAS_TABLE = "CREATE TABLE " + MascotasEntry.TABLE_NAME + " ("
            + MascotasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + MascotasEntry.COLUMN_NAME_NOMBRE + " TEXT NOT NULL,"
            + MascotasEntry.COLUMN_NAME_EDAD + " INTEGER NOT NULL DEFAULT 0," // Aseguramos que 'edad' se crea
            + MascotasEntry.COLUMN_NAME_PHOTO_URI + " TEXT,"

            // Clave Foránea a Razas.
            + MascotasEntry.COLUMN_NAME_RAZA_ID + " INTEGER NOT NULL,"

            // Clave Foránea a Dueños.
            + MascotasEntry.COLUMN_NAME_DUENO_ID + " INTEGER NOT NULL,"

            // Definición de Clave Foránea a la tabla Razas (ON DELETE CASCADE opcional, pero buena práctica)
            + " FOREIGN KEY (" + MascotasEntry.COLUMN_NAME_RAZA_ID + ") REFERENCES " + RazaEntry.TABLE_NAME + "(" + RazaEntry._ID + ") ON DELETE CASCADE,"

            // Definición de Clave Foránea a la tabla Dueños (ON DELETE CASCADE opcional)
            + " FOREIGN KEY (" + MascotasEntry.COLUMN_NAME_DUENO_ID + ") REFERENCES " + OwnerEntry.TABLE_NAME + "(" + OwnerEntry._ID + ") ON DELETE CASCADE"
            + ");";

    /**
     * Sentencia SQL para crear la tabla de Dueños.
     */
    private static final String SQL_CREATE_DUENOS_TABLE = "CREATE TABLE " + OwnerEntry.TABLE_NAME + " ("
            + OwnerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + OwnerEntry.COLUMN_NAME_NOMBRE + " TEXT NOT NULL,"
            + OwnerEntry.COLUMN_NAME_TELEFONO + " TEXT,"
            + OwnerEntry.COLUMN_NAME_PHOTO_URI + " TEXT" // Columna Photo URI
            + ");";

    /**
     * Sentencia SQL para crear la tabla de Razas.
     */
    private static final String SQL_CREATE_RAZAS_TABLE = "CREATE TABLE " + RazaEntry.TABLE_NAME + " ("
            + RazaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RazaEntry.COLUMN_NAME_NOMBRE + " TEXT NOT NULL UNIQUE"
            + ");";


    public DBHelper(Context context) {
        // La llamada al constructor base usa el nombre y la versión del contrato.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String TAG = "DBHelper";

    // Usamos el nombre y la versión definidos en PetContract (asumiendo versión 2)
    public static final int DATABASE_VERSION = PetContract.DATABASE_VERSION;
    public static final String DATABASE_NAME = PetContract.DATABASE_NAME;
}
