package com.example.proyectodesqlite.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyectodesqlite.bd.PetContract.DuenosEntry;
import com.example.proyectodesqlite.bd.PetContract.MascotasEntry;
import com.example.proyectodesqlite.bd.PetContract.RazasEntry;

/**
 * Data Access Object (DAO) para la gestión de datos de mascotas, dueños y razas.
 * Contiene todos los métodos CRUD.
 */
public class PetDatabase {

    private final DBHelper dbHelper;

    public PetDatabase(Context context) {
        dbHelper = new DBHelper(context);
    }

    // ----------------------------------------------------
    // INSERCIÓN
    // ----------------------------------------------------

    /**
     * Inserta un nuevo dueño en la base de datos.
     * @return El ID de la fila insertada o -1 si hubo error.
     */
    public long insertOwner(String nombre, String telefono) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DuenosEntry.COLUMN_NOMBRE, nombre);
        values.put(DuenosEntry.COLUMN_TELEFONO, telefono);
        long newRowId = db.insert(DuenosEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    /**
     * Inserta una nueva mascota en la base de datos.
     * @return El ID de la fila insertada o -1 si hubo error.
     */
    public long insertPet(String nombre, int edad, long duenoId, long razaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MascotasEntry.COLUMN_NOMBRE, nombre);
        values.put(MascotasEntry.COLUMN_EDAD, edad);
        values.put(MascotasEntry.COLUMN_DUENO_ID, duenoId);
        values.put(MascotasEntry.COLUMN_RAZA_ID, razaId);
        long newRowId = db.insert(MascotasEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // ----------------------------------------------------
    // LECTURA (READ)
    // ----------------------------------------------------

    /**
     * Obtiene un Cursor con todas las mascotas para la ListView de MainActivity.
     */
    public Cursor getAllPetsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT "
                + "m." + MascotasEntry._ID + " AS " + MascotasEntry._ID + ", " // Alias "_id" obligatorio
                + "m." + MascotasEntry.COLUMN_NOMBRE + ", "
                + "m." + MascotasEntry.COLUMN_EDAD + ", "
                + "d." + DuenosEntry.COLUMN_NOMBRE + " AS dueno_nombre, "
                + "r." + RazasEntry.COLUMN_NOMBRE + " AS raza_nombre "
                + "FROM " + MascotasEntry.TABLE_NAME + " m "
                + "JOIN " + DuenosEntry.TABLE_NAME + " d ON m." + MascotasEntry.COLUMN_DUENO_ID + " = d." + DuenosEntry._ID + " "
                + "JOIN " + RazasEntry.TABLE_NAME + " r ON m." + MascotasEntry.COLUMN_RAZA_ID + " = r." + RazasEntry._ID + " "
                + "ORDER BY m." + MascotasEntry.COLUMN_NOMBRE + " ASC";

        return db.rawQuery(sql, null);
    }

    /**
     * Obtiene un Cursor con todos los dueños para el Spinner.
     */
    public Cursor getOwnersCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DuenosEntry._ID + " AS " + DuenosEntry._ID, // Alias _id CRÍTICO
                DuenosEntry.COLUMN_NOMBRE
        };
        return db.query(DuenosEntry.TABLE_NAME, projection, null, null, null, null, DuenosEntry.COLUMN_NOMBRE + " ASC");
    }

    /**
     * Obtiene un Cursor con todas las razas para el Spinner.
     */
    public Cursor getBreedsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                RazasEntry._ID + " AS " + RazasEntry._ID, // Alias _id CRÍTICO
                RazasEntry.COLUMN_NOMBRE
        };
        return db.query(RazasEntry.TABLE_NAME, projection, null, null, null, null, RazasEntry.COLUMN_NOMBRE + " ASC");
    }

    /**
     * Obtiene un Cursor con los detalles de UNA mascota específica (para edición).
     * ESTA FUE LA FUNCIÓN CRÍTICA CORREGIDA para evitar el crash.
     */
    public Cursor getPetDetailsCursor(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT "
                + "m." + MascotasEntry._ID + " AS " + MascotasEntry._ID + ", "
                + "m." + MascotasEntry.COLUMN_NOMBRE + ", "
                + "m." + MascotasEntry.COLUMN_EDAD + ", "
                + "m." + MascotasEntry.COLUMN_DUENO_ID + ", "
                + "m." + MascotasEntry.COLUMN_RAZA_ID + ", "
                + "d." + DuenosEntry.COLUMN_NOMBRE + " AS dueno_nombre, "
                + "d." + DuenosEntry.COLUMN_TELEFONO + " AS dueno_telefono, "
                + "r." + RazasEntry.COLUMN_NOMBRE + " AS raza_nombre " // Añadida la raza para robustez
                + "FROM " + MascotasEntry.TABLE_NAME + " m "
                + "JOIN " + DuenosEntry.TABLE_NAME + " d ON m." + MascotasEntry.COLUMN_DUENO_ID + " = d." + DuenosEntry._ID + " "
                + "JOIN " + RazasEntry.TABLE_NAME + " r ON m." + MascotasEntry.COLUMN_RAZA_ID + " = r." + RazasEntry._ID + " " // Añadida la JOIN a Razas
                + "WHERE m." + MascotasEntry._ID + " = ?";

        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.rawQuery(sql, selectionArgs);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // ----------------------------------------------------
    // ACTUALIZACIÓN (UPDATE)
    // ----------------------------------------------------

    /**
     * 🟢 NUEVO: Actualiza solo los datos de la Mascota. Resuelve el error de 4 argumentos.
     * @return Número de filas afectadas en la tabla de mascotas.
     */
    public int updatePet(long petId, String petNombre, int petEdad, long razaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues petValues = new ContentValues();
        petValues.put(MascotasEntry.COLUMN_NOMBRE, petNombre);
        petValues.put(MascotasEntry.COLUMN_EDAD, petEdad);
        petValues.put(MascotasEntry.COLUMN_RAZA_ID, razaId);

        String petSelection = MascotasEntry._ID + " = ?";
        String[] petSelectionArgs = { String.valueOf(petId) };

        int rowsAffected = db.update(MascotasEntry.TABLE_NAME, petValues, petSelection, petSelectionArgs);
        db.close();
        return rowsAffected;
    }

    /**
     * 🟢 RENOMBRADO: Actualiza la mascota y los datos del dueño asociado (7 argumentos).
     * @return Número de filas afectadas en la tabla de mascotas.
     */
    public int updatePetAndOwner(long petId, String petNombre, int petEdad, long razaId, long duenoId, String duenoNombre, String duenoTelefono) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1. Actualizar el Dueño asociado a la mascota
        ContentValues duenoValues = new ContentValues();
        duenoValues.put(DuenosEntry.COLUMN_NOMBRE, duenoNombre);
        duenoValues.put(DuenosEntry.COLUMN_TELEFONO, duenoTelefono);

        // Actualizar DUENOS donde _ID = duenoId
        String duenoSelection = DuenosEntry._ID + " = ?";
        String[] duenoSelectionArgs = { String.valueOf(duenoId) };
        db.update(DuenosEntry.TABLE_NAME, duenoValues, duenoSelection, duenoSelectionArgs);


        // 2. Actualizar la Mascota
        ContentValues petValues = new ContentValues();
        petValues.put(MascotasEntry.COLUMN_NOMBRE, petNombre);
        petValues.put(MascotasEntry.COLUMN_EDAD, petEdad);
        petValues.put(MascotasEntry.COLUMN_RAZA_ID, razaId);
        petValues.put(MascotasEntry.COLUMN_DUENO_ID, duenoId);

        String petSelection = MascotasEntry._ID + " = ?";
        String[] petSelectionArgs = { String.valueOf(petId) };

        int rowsAffected = db.update(MascotasEntry.TABLE_NAME, petValues, petSelection, petSelectionArgs);
        db.close();
        return rowsAffected;
    }

    // ----------------------------------------------------
    // ELIMINACIÓN (DELETE)
    // ----------------------------------------------------

    /**
     * Elimina una mascota por su ID.
     * @return Número de filas eliminadas (debería ser 1).
     */
    public int deletePet(long petId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = MascotasEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(petId) };

        int deletedRows = db.delete(MascotasEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }
}