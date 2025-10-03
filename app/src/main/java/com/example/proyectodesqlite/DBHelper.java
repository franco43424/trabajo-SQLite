package com.example.proyectodesqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.proyectodesqlite.PetContract.DuenosEntry;
import com.example.proyectodesqlite.PetContract.MascotasEntry;
import com.example.proyectodesqlite.PetContract.RazasEntry;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mascotas.db";
    private static final int DATABASE_VERSION = 3; // Version incrementada por cambios de esquema

    // Sentencia SQL para crear la tabla de dueños (Nuevos campos)
    private static final String SQL_CREATE_DUENOS =
            "CREATE TABLE " + DuenosEntry.TABLE_NAME + " (" +
                    DuenosEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DuenosEntry.COLUMN_NOMBRE + " TEXT," +
                    DuenosEntry.COLUMN_TELEFONO + " TEXT," +
                    DuenosEntry.COLUMN_EDAD_DUENO + " INTEGER," +
                    DuenosEntry.COLUMN_CANT_MASCOTAS + " INTEGER DEFAULT 0)";

    // Sentencia SQL para crear la tabla de razas (Nuevos campos)
    private static final String SQL_CREATE_RAZAS =
            "CREATE TABLE " + RazasEntry.TABLE_NAME + " (" +
                    RazasEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RazasEntry.COLUMN_TIPO_MASCOTA + " TEXT," +
                    RazasEntry.COLUMN_NOMBRE_RAZA + " TEXT)";

    // Sentencia SQL para crear la tabla de mascotas (Nuevos/Actualizados PKs/FKs)
    private static final String SQL_CREATE_MASCOTAS =
            "CREATE TABLE " + MascotasEntry.TABLE_NAME + " (" +
                    MascotasEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MascotasEntry.COLUMN_NOMBRE + " TEXT," +
                    MascotasEntry.COLUMN_EDAD_MASCOTA + " INTEGER," +
                    MascotasEntry.COLUMN_FK_ID_DUENO + " INTEGER," +
                    MascotasEntry.COLUMN_FK_ID_RAZA + " INTEGER," +
                    "FOREIGN KEY(" + MascotasEntry.COLUMN_FK_ID_DUENO + ") REFERENCES " +
                    DuenosEntry.TABLE_NAME + "(" + DuenosEntry.COLUMN_ID + ")," +
                    "FOREIGN KEY(" + MascotasEntry.COLUMN_FK_ID_RAZA + ") REFERENCES " +
                    RazasEntry.TABLE_NAME + "(" + RazasEntry.COLUMN_ID + "))";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DUENOS);
        db.execSQL(SQL_CREATE_RAZAS);
        db.execSQL(SQL_CREATE_MASCOTAS);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MascotasEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DuenosEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RazasEntry.TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar datos de prueba actualizados
    private void insertInitialData(SQLiteDatabase db) {
        // Insertar Dueños (con nuevos campos)
        ContentValues duenoValues = new ContentValues();
        duenoValues.put(DuenosEntry.COLUMN_NOMBRE, "Alice Johnson");
        duenoValues.put(DuenosEntry.COLUMN_TELEFONO, "555-1234");
        duenoValues.put(DuenosEntry.COLUMN_EDAD_DUENO, 35);
        db.insert(DuenosEntry.TABLE_NAME, null, duenoValues);

        duenoValues.clear();
        duenoValues.put(DuenosEntry.COLUMN_NOMBRE, "Bob Smith");
        duenoValues.put(DuenosEntry.COLUMN_TELEFONO, "555-5678");
        duenoValues.put(DuenosEntry.COLUMN_EDAD_DUENO, 42);
        db.insert(DuenosEntry.TABLE_NAME, null, duenoValues);

        // Insertar Razas (con nuevos campos)
        ContentValues razaValues = new ContentValues();
        razaValues.put(RazasEntry.COLUMN_TIPO_MASCOTA, "Perro");
        razaValues.put(RazasEntry.COLUMN_NOMBRE_RAZA, "Labrador");
        db.insert(RazasEntry.TABLE_NAME, null, razaValues);

        razaValues.clear();
        razaValues.put(RazasEntry.COLUMN_TIPO_MASCOTA, "Perro");
        razaValues.put(RazasEntry.COLUMN_NOMBRE_RAZA, "Golden Retriever");
        db.insert(RazasEntry.TABLE_NAME, null, razaValues);

        razaValues.clear();
        razaValues.put(RazasEntry.COLUMN_TIPO_MASCOTA, "Gato");
        razaValues.put(RazasEntry.COLUMN_NOMBRE_RAZA, "Siames");
        db.insert(RazasEntry.TABLE_NAME, null, razaValues);
    }
}