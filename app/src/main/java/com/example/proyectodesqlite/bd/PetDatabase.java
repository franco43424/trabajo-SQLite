package com.example.proyectodesqlite.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder; // Necesario para consultas complejas
import android.util.Log; // Necesario para logging
import java.util.ArrayList;
import java.util.List;

// Importaciones de clases internas del contrato
import com.example.proyectodesqlite.bd.PetContract.CitasEntry;
import com.example.proyectodesqlite.bd.PetContract.OwnerEntry;
import com.example.proyectodesqlite.bd.PetContract.MascotasEntry;
import com.example.proyectodesqlite.bd.PetContract.RazaEntry;

public class PetDatabase {

    private static final String TAG = "PetDatabase";
    private final DBHelper dbHelper;

    public PetDatabase(Context context) {
        // Asegúrate de que DBHelper.java esté definido en el mismo paquete
        dbHelper = new DBHelper(context);
        // Aseguramos que las razas iniciales existan al crear la instancia del DAO
        insertInitialBreeds();
    }

    // --- MÉTODOS DE INSERCIÓN INICIAL DE RAZAS ---

    /**
     * Inserta las razas predeterminadas si la tabla está vacía.
     */
    private void insertInitialBreeds() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Usamos RazaEntry.TABLE_NAME
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + RazaEntry.TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            Log.d(TAG, "Insertando razas iniciales...");
            db.beginTransaction();
            try {
                // Insertamos las razas necesarias para el Spinner usando el método interno
                insertBreedInternal(db, "Sin Raza");
                insertBreedInternal(db, "Labrador Retriever");
                insertBreedInternal(db, "Pastor Alemán");
                insertBreedInternal(db, "Bulldog Francés");
                insertBreedInternal(db, "Poodle");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        db.close(); // Cierra la base de datos después de la operación inicial
    }

    /**
     * Helper INTERNO para insertar una sola raza dentro de una transacción.
     * Es private y requiere un objeto SQLiteDatabase abierto.
     */
    private long insertBreedInternal(SQLiteDatabase db, String breedName) {
        ContentValues values = new ContentValues();
        // Usamos COLUMN_NAME_NOMBRE
        values.put(RazaEntry.COLUMN_NAME_NOMBRE, breedName);
        // Usamos RazaEntry.TABLE_NAME
        return db.insertWithOnConflict(RazaEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    // -------------------------------------------------------------
    // OPERACIONES CRUD - RAZA

    /**
     * Obtiene el ID de una raza dado su nombre.
     * @param breedName Nombre de la raza a buscar.
     * @return El ID de la raza o -1 si no se encuentra.
     */
    public long getBreedIdByName(String breedName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long breedId = -1;

        String[] projection = { RazaEntry._ID };
        // Usamos COLUMN_NAME_NOMBRE
        String selection = RazaEntry.COLUMN_NAME_NOMBRE + " = ?";
        String[] selectionArgs = { breedName };

        Cursor cursor = db.query(
                RazaEntry.TABLE_NAME, // Usamos RazaEntry.TABLE_NAME
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Obtener el ID de la columna _ID
                breedId = cursor.getLong(cursor.getColumnIndexOrThrow(RazaEntry._ID));
            }
            cursor.close();
        }
        db.close();
        return breedId;
    }

    /**
     * Obtiene una lista de todos los nombres de razas existentes.
     * @return List<String> con los nombres de las razas ordenados alfabéticamente.
     */
    public List<String> getAllBreedNames() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> breedNames = new ArrayList<>();

        // Usamos COLUMN_NAME_NOMBRE
        String[] projection = { RazaEntry.COLUMN_NAME_NOMBRE };

        Cursor cursor = db.query(
                RazaEntry.TABLE_NAME, // Usamos RazaEntry.TABLE_NAME
                projection,
                null,
                null,
                null,
                null,
                // Usamos COLUMN_NAME_NOMBRE
                RazaEntry.COLUMN_NAME_NOMBRE + " ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Usamos COLUMN_NAME_NOMBRE
                int nameColumnIndex = cursor.getColumnIndex(RazaEntry.COLUMN_NAME_NOMBRE);
                do {
                    breedNames.add(cursor.getString(nameColumnIndex));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return breedNames;
    }

    /**
     * Inserta una nueva raza en la base de datos (método público).
     * @param breedName El nombre de la nueva raza.
     * @return El ID de la fila insertada o -1 si ya existe (por CONFLICT_IGNORE).
     */
    public long insertBreed(String breedName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;

        db.beginTransaction();
        try {
            // Llama al método interno para realizar la inserción
            id = insertBreedInternal(db, breedName);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return id;
    }

    /**
     * Cuenta el número total de razas existentes en la tabla.
     * @return El número de razas.
     */
    public long getBreedCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = 0;

        // Usamos RazaEntry.TABLE_NAME
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + RazaEntry.TABLE_NAME, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getLong(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * Devuelve un Cursor con todas las razas, incluyendo el _ID y el nombre.
     * Necesario para llenar el Spinner en UpdateDeletePetActivity.
     */
    public Cursor getBreedsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                RazaEntry._ID, // Necesario para la posición/mapeo
                // Usamos COLUMN_NAME_NOMBRE
                RazaEntry.COLUMN_NAME_NOMBRE
        };

        // El cursor no se cierra aquí; debe cerrarse donde se usa (Activity).
        return db.query(
                RazaEntry.TABLE_NAME, // Usamos RazaEntry.TABLE_NAME
                projection,
                null,
                null,
                null,
                null,
                RazaEntry.COLUMN_NAME_NOMBRE + " ASC" // Usamos COLUMN_NAME_NOMBRE
        );
    }


    // -------------------------------------------------------------
    // OPERACIONES CRUD - DUEÑO (Owner)

    /**
     * Inserta un nuevo dueño en la base de datos (Paso 2).
     * @param name Nombre del dueño.
     * @param phone Teléfono del dueño.
     * @param photoUri URI de la foto del dueño (puede ser null).
     * @return El ID de la fila insertada.
     */
    public long insertOwner(String name, String phone, String photoUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Usamos OwnerEntry.COLUMN_NAME_NOMBRE
        values.put(OwnerEntry.COLUMN_NAME_NOMBRE, name);
        // Usamos OwnerEntry.COLUMN_NAME_TELEFONO
        values.put(OwnerEntry.COLUMN_NAME_TELEFONO, phone);
        // Usamos OwnerEntry.COLUMN_NAME_PHOTO_URI
        values.put(OwnerEntry.COLUMN_NAME_PHOTO_URI, photoUri);
        // Usamos OwnerEntry.TABLE_NAME
        long id = db.insert(OwnerEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // -------------------------------------------------------------
    // OPERACIONES CRUD - MASCOTA (Lectura/Actualización/Eliminación)

    /**
     * Inserta una nueva mascota en la base de datos.
     */
    public long insertPet(String name, int age, long razaId, long duenoId, String photoUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Usamos COLUMN_NAME_...
        values.put(MascotasEntry.COLUMN_NAME_NOMBRE, name);
        values.put(MascotasEntry.COLUMN_NAME_EDAD, age);
        values.put(MascotasEntry.COLUMN_NAME_RAZA_ID, razaId);
        values.put(MascotasEntry.COLUMN_NAME_DUENO_ID, duenoId);
        values.put(MascotasEntry.COLUMN_NAME_PHOTO_URI, photoUri);
        long id = db.insert(MascotasEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Devuelve un Cursor con los detalles completos de una mascota específica,
     * incluyendo los datos del dueño y la raza, usando JOINs.
     * @param petId El ID de la mascota a buscar.
     * @return Cursor con los detalles.
     */
    public Cursor getPetDetailsCursor(long petId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Tablas y Alias: Mascotas (M), Dueño (D), Razas (R)
        qb.setTables(
                MascotasEntry.TABLE_NAME + " M " +
                        "INNER JOIN " + OwnerEntry.TABLE_NAME + " D ON M." + MascotasEntry.COLUMN_NAME_DUENO_ID + " = D." + OwnerEntry._ID +
                        " INNER JOIN " + RazaEntry.TABLE_NAME + " R ON M." + MascotasEntry.COLUMN_NAME_RAZA_ID + " = R." + RazaEntry._ID
        );

        // Columnas a devolver (incluyendo los alias esperados por la Activity)
        String[] projectionIn = {
                "M." + MascotasEntry._ID,
                "M." + MascotasEntry.COLUMN_NAME_NOMBRE,
                "M." + MascotasEntry.COLUMN_NAME_EDAD,
                "M." + MascotasEntry.COLUMN_NAME_RAZA_ID,
                "M." + MascotasEntry.COLUMN_NAME_DUENO_ID,
                "D." + OwnerEntry.COLUMN_NAME_NOMBRE + " AS dueno_nombre", // Alias
                "D." + OwnerEntry.COLUMN_NAME_TELEFONO + " AS dueno_telefono", // Alias
                "R." + RazaEntry.COLUMN_NAME_NOMBRE + " AS raza_nombre"
        };

        String selection = "M." + MascotasEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(petId) };

        // El Cursor no se cierra aquí.
        return qb.query(
                db,
                projectionIn,
                selection,
                selectionArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
    }

    /**
     * Actualiza los datos de la mascota y el dueño asociado en una sola transacción.
     */
    public int updatePetAndOwner(long petId, String petName, int petAge, long razaId, long duenoId, String duenoNombre, String duenoTelefono) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int totalRowsAffected = 0;

        db.beginTransaction();
        try {
            // 1. Actualizar Mascota
            ContentValues petValues = new ContentValues();
            petValues.put(MascotasEntry.COLUMN_NAME_NOMBRE, petName);
            petValues.put(MascotasEntry.COLUMN_NAME_EDAD, petAge);
            petValues.put(MascotasEntry.COLUMN_NAME_RAZA_ID, razaId);

            int petUpdated = db.update(
                    MascotasEntry.TABLE_NAME,
                    petValues,
                    MascotasEntry._ID + " = ?",
                    new String[]{String.valueOf(petId)}
            );
            totalRowsAffected += petUpdated;

            // 2. Actualizar Dueño
            ContentValues ownerValues = new ContentValues();
            ownerValues.put(OwnerEntry.COLUMN_NAME_NOMBRE, duenoNombre); // Columna corregida
            ownerValues.put(OwnerEntry.COLUMN_NAME_TELEFONO, duenoTelefono); // Columna corregida

            int ownerUpdated = db.update(
                    OwnerEntry.TABLE_NAME, // Tabla corregida
                    ownerValues,
                    OwnerEntry._ID + " = ?",
                    new String[]{String.valueOf(duenoId)}
            );
            totalRowsAffected += ownerUpdated;

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        return totalRowsAffected;
    }

    /**
     * Elimina una mascota de la base de datos por su ID.
     */
    public int deletePet(long petId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                MascotasEntry.TABLE_NAME,
                MascotasEntry._ID + " = ?",
                new String[]{String.valueOf(petId)}
        );
        db.close();
        return rowsDeleted;
    }


    // -------------------------------------------------------------
    // OPERACIONES CRUD - AGENDA (Citas)

    /**
     * Inserta una nueva cita en la base de datos.
     * Ahora recibe el ID de la mascota (petId) para asociar la cita.
     * @param petId El ID de la mascota a la que se asocia la cita.
     * @param date Fecha de la cita (DD/MM/AAAA).
     * @param time Hora de la cita (HH:MM).
     * @param reason Motivo de la cita.
     * @return El ID de la fila insertada o -1 si hubo un error.
     */
    public long insertAppointment(long petId, String date, String time, String reason) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d(TAG, "Intentando insertar cita para Mascota ID: " + petId
                + " con motivo: " + reason + " y columna: " + CitasEntry.COLUMN_NAME_PET_ID);

        // 🚨 ESTA ES LA CLAVE FORÁNEA: Añadimos la columna para el ID de la mascota
        // Si sigue fallando, es porque la base de datos en tu dispositivo no ha sido actualizada.
        values.put(CitasEntry.COLUMN_NAME_PET_ID, petId);

        // Usamos COLUMN_NAME_...
        values.put(CitasEntry.COLUMN_NAME_DATE, date);
        values.put(CitasEntry.COLUMN_NAME_TIME, time);
        values.put(CitasEntry.COLUMN_NAME_REASON, reason);

        long id = db.insert(CitasEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "FALLÓ la inserción de la cita. Asegúrate de que la BASE DE DATOS ha sido actualizada (reinstala la app después de cambiar la versión en DBHelper).");
        } else {
            Log.d(TAG, "Cita insertada correctamente con ID: " + id);
        }

        db.close();
        return id;
    }

    /**
     * Ejecuta la consulta con múltiples JOINs para obtener todas las citas
     * junto con los detalles de la mascota, el dueño y la raza.
     * @return Cursor con los detalles de las citas, listo para un CursorAdapter.
     */
    public Cursor getAppointmentsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Tablas y Alias: Citas (C), Mascotas (M), Dueño (D), Razas (R)
        qb.setTables(
                CitasEntry.TABLE_NAME + " C " +
                        // JOIN de Citas a Mascotas
                        "INNER JOIN " + MascotasEntry.TABLE_NAME + " M ON C." + CitasEntry.COLUMN_NAME_PET_ID + " = M." + MascotasEntry._ID +
                        // JOIN de Mascotas a Dueños
                        " INNER JOIN " + OwnerEntry.TABLE_NAME + " D ON M." + MascotasEntry.COLUMN_NAME_DUENO_ID + " = D." + OwnerEntry._ID +
                        // JOIN de Mascotas a Razas
                        " INNER JOIN " + RazaEntry.TABLE_NAME + " R ON M." + MascotasEntry.COLUMN_NAME_RAZA_ID + " = R." + RazaEntry._ID
        );

        // Columnas a devolver (deben incluir el _ID de la tabla principal 'Citas' y los alias)
        String[] projectionIn = {
                "C." + CitasEntry._ID, // Necesario para el CursorAdapter
                "C." + CitasEntry.COLUMN_NAME_DATE,
                "C." + CitasEntry.COLUMN_NAME_TIME,
                "C." + CitasEntry.COLUMN_NAME_REASON,

                // Detalles de la mascota
                "M." + MascotasEntry.COLUMN_NAME_NOMBRE + " AS pet_name_alias",

                // Detalles del dueño
                "D." + OwnerEntry.COLUMN_NAME_NOMBRE + " AS owner_name_alias",

                // Detalles de la raza
                "R." + RazaEntry.COLUMN_NAME_NOMBRE + " AS breed_name_alias"
        };

        // Ordenar por fecha y hora
        String orderBy = "C." + CitasEntry.COLUMN_NAME_DATE + " ASC, C." + CitasEntry.COLUMN_NAME_TIME + " ASC";

        Cursor cursor = qb.query(
                db,
                projectionIn,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                orderBy
        );
        // NOTA: El cursor no se cierra aquí, debe cerrarse donde se usa (e.g., Loader/Activity).
        return cursor;
    }


    // -------------------------------------------------------------
    // OPERACIONES CRUD - LECTURA (Listado)

    /**
     * Ejecuta la consulta con JOINs para obtener todas las mascotas junto con los nombres
     * del dueño y la raza.
     */
    public Cursor getAllPetsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Tablas y Alias: Mascotas (M), Dueño (D), Razas (R)
        qb.setTables(
                MascotasEntry.TABLE_NAME + " M " +
                        "INNER JOIN " + OwnerEntry.TABLE_NAME + " D ON M." + MascotasEntry.COLUMN_NAME_DUENO_ID + " = D." + OwnerEntry._ID +
                        " INNER JOIN " + RazaEntry.TABLE_NAME + " R ON M." + MascotasEntry.COLUMN_NAME_RAZA_ID + " = R." + RazaEntry._ID
        );

        // Columnas a devolver (deben coincidir con el PetCursorAdapter)
        String[] projectionIn = {
                "M." + MascotasEntry._ID, // Necesario para el CursorAdapter
                "M." + MascotasEntry.COLUMN_NAME_NOMBRE,
                "M." + MascotasEntry.COLUMN_NAME_EDAD,
                // Usamos alias que concuerdan con PetCursorAdapter
                "D." + OwnerEntry.COLUMN_NAME_NOMBRE + " AS owner_name_alias",
                "R." + RazaEntry.COLUMN_NAME_NOMBRE + " AS breed_name_alias"
        };

        // Ordenar por el nombre de la mascota para evitar ambigüedad
        String orderBy = "M." + MascotasEntry.COLUMN_NAME_NOMBRE + " ASC";

        Cursor cursor = qb.query(
                db,
                projectionIn,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                orderBy
        );
        // NOTA: El cursor no se cierra aquí, debe cerrarse donde se usa (e.g., Loader/Activity).
        return cursor;
    }
}
