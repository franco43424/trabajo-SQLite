package com.example.proyectodesqlite.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.proyectodesqlite.bd.PetContract.DuenosEntry;
import com.example.proyectodesqlite.bd.PetContract.RazasEntry;
import com.example.proyectodesqlite.bd.PetContract.MascotasEntry;

public class DBHelper extends SQLiteOpenHelper {

    // 🚨 Versión incrementada a 9 para forzar la recreación de la base de datos
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "mascotas.db";

    // Sentencias SQL de creación
    private static final String SQL_CREATE_DUENOS =
            "CREATE TABLE " + DuenosEntry.TABLE_NAME + " (" +
                    DuenosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // 🚨 CORREGIDO: Usando _ID
                    DuenosEntry.COLUMN_NOMBRE + " TEXT NOT NULL," +
                    DuenosEntry.COLUMN_TELEFONO + " TEXT" +
                    ");";

    private static final String SQL_CREATE_RAZAS =
            "CREATE TABLE " + RazasEntry.TABLE_NAME + " (" +
                    RazasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // 🚨 CORREGIDO: Usando _ID
                    RazasEntry.COLUMN_NOMBRE + " TEXT NOT NULL UNIQUE" +
                    ");";

    private static final String SQL_CREATE_MASCOTAS =
            "CREATE TABLE " + MascotasEntry.TABLE_NAME + " (" +
                    MascotasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // 🚨 CORREGIDO: Usando _ID
                    MascotasEntry.COLUMN_NOMBRE + " TEXT NOT NULL," +
                    MascotasEntry.COLUMN_EDAD + " INTEGER," +
                    MascotasEntry.COLUMN_DUENO_ID + " INTEGER," + // Usamos COLUMN_DUENO_ID del contrato
                    MascotasEntry.COLUMN_RAZA_ID + " INTEGER," + // Usamos COLUMN_RAZA_ID del contrato
                    " FOREIGN KEY (" + MascotasEntry.COLUMN_DUENO_ID + ") REFERENCES " +
                    DuenosEntry.TABLE_NAME + "(" + DuenosEntry._ID + ")," + // 🚨 CORREGIDO: Usando _ID en FK
                    " FOREIGN KEY (" + MascotasEntry.COLUMN_RAZA_ID + ") REFERENCES " +
                    RazasEntry.TABLE_NAME + "(" + RazasEntry._ID + ")" + // 🚨 CORREGIDO: Usando _ID en FK
                    ");";

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
        db.execSQL("DROP TABLE IF EXISTS " + RazasEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DuenosEntry.TABLE_NAME);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Insertar Dueños
        ContentValues valuesDuenos = new ContentValues();
        valuesDuenos.put(DuenosEntry.COLUMN_NOMBRE, "Juan Pérez");
        valuesDuenos.put(DuenosEntry.COLUMN_TELEFONO, "555-1234");
        db.insert(DuenosEntry.TABLE_NAME, null, valuesDuenos);

        valuesDuenos.clear();
        valuesDuenos.put(DuenosEntry.COLUMN_NOMBRE, "María López");
        valuesDuenos.put(DuenosEntry.COLUMN_TELEFONO, "555-5678");
        db.insert(DuenosEntry.TABLE_NAME, null, valuesDuenos);

        // Insertar Razas
        ContentValues valuesRazas = new ContentValues();
        valuesRazas.put(RazasEntry.COLUMN_NOMBRE, "Labrador");
        db.insert(RazasEntry.TABLE_NAME, null, valuesRazas);

        valuesRazas.clear();
        valuesRazas.put(RazasEntry.COLUMN_NOMBRE, "Chihuahua");
        db.insert(RazasEntry.TABLE_NAME, null, valuesRazas);

        // Agrega estas razas adicionales en la sección "Insertar Razas":
        valuesRazas.clear();
        valuesRazas.put(RazasEntry.COLUMN_NOMBRE, "Golden Retriever");
        db.insert(RazasEntry.TABLE_NAME, null, valuesRazas);

        valuesRazas.clear();
        valuesRazas.put(RazasEntry.COLUMN_NOMBRE, "Pastor Alemán");
        db.insert(RazasEntry.TABLE_NAME, null, valuesRazas);

        // Opción "Otro" para razas no listadas:
        valuesRazas.clear();
        valuesRazas.put(RazasEntry.COLUMN_NOMBRE, "Otro");
        db.insert(RazasEntry.TABLE_NAME, null, valuesRazas);
    }
}
