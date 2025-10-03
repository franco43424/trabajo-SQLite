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
    private static final int DATABASE_VERSION = 2; // Incrementar la versión para forzar onCreate()

    // Sentencia SQL para crear la tabla de dueños
    private static final String SQL_CREATE_DUENOS =
            "CREATE TABLE " + DuenosEntry.TABLE_NAME + " (" +
                    DuenosEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DuenosEntry.COLUMN_NOMBRE + " TEXT)";

    // Sentencia SQL para crear la tabla de razas
    private static final String SQL_CREATE_RAZAS =
            "CREATE TABLE " + RazasEntry.TABLE_NAME + " (" +
                    RazasEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RazasEntry.COLUMN_NOMBRE + " TEXT)";

    // Sentencia SQL para crear la tabla de mascotas (incluyendo FOREIGN KEYS)
    private static final String SQL_CREATE_MASCOTAS =
            "CREATE TABLE " + MascotasEntry.TABLE_NAME + " (" +
                    MascotasEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MascotasEntry.COLUMN_NOMBRE + " TEXT," +
                    MascotasEntry.COLUMN_ID_DUENO + " INTEGER," +
                    MascotasEntry.COLUMN_ID_RAZA + " INTEGER," +
                    "FOREIGN KEY(" + MascotasEntry.COLUMN_ID_DUENO + ") REFERENCES " +
                    DuenosEntry.TABLE_NAME + "(" + DuenosEntry.COLUMN_ID + ")," +
                    "FOREIGN KEY(" + MascotasEntry.COLUMN_ID_RAZA + ") REFERENCES " +
                    RazasEntry.TABLE_NAME + "(" + RazasEntry.COLUMN_ID + "))";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DUENOS);
        db.execSQL(SQL_CREATE_RAZAS);
        db.execSQL(SQL_CREATE_MASCOTAS);

        // Agregamos datos iniciales (Seed data)
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MascotasEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DuenosEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RazasEntry.TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar datos de prueba
    private void insertInitialData(SQLiteDatabase db) {
        // Insertar Dueños
        ContentValues duenoValues = new ContentValues();
        duenoValues.put(PetContract.DuenosEntry.COLUMN_NOMBRE, "Alice Johnson");
        db.insert(PetContract.DuenosEntry.TABLE_NAME, null, duenoValues);
        duenoValues.put(PetContract.DuenosEntry.COLUMN_NOMBRE, "Bob Smith");
        db.insert(PetContract.DuenosEntry.TABLE_NAME, null, duenoValues);

        // Insertar Razas
        ContentValues razaValues = new ContentValues();
        razaValues.put(PetContract.RazasEntry.COLUMN_NOMBRE, "Labrador");
        db.insert(PetContract.RazasEntry.TABLE_NAME, null, razaValues);
        razaValues.put(PetContract.RazasEntry.COLUMN_NOMBRE, "Golden Retriever");
        db.insert(PetContract.RazasEntry.TABLE_NAME, null, razaValues);
        razaValues.put(PetContract.RazasEntry.COLUMN_NOMBRE, "Siames");
        db.insert(PetContract.RazasEntry.TABLE_NAME, null, razaValues);
    }
}