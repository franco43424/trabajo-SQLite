package com.example.proyectodesqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPetActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText etNombreMascota;
    private Button btnGuardarMascota;
    private Spinner spinnerDueno;
    private Spinner spinnerRaza;
    private long idMascotaParaEditar = -1; // -1 significa que es una nueva mascota

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        dbHelper = new DBHelper(this);
        etNombreMascota = findViewById(R.id.etNombreMascota);
        btnGuardarMascota = findViewById(R.id.btnGuardarMascota);
        spinnerDueno = findViewById(R.id.spinnerDueno);
        spinnerRaza = findViewById(R.id.spinnerRaza);

        // Cargar los Spinners con datos de Dueños y Razas
        setupSpinners();

        // 1. Verifica si se recibió un ID (Modo Edición)
        if (getIntent().hasExtra("ID_MASCOTA")) {
            idMascotaParaEditar = getIntent().getLongExtra("ID_MASCOTA", -1);
            if (idMascotaParaEditar != -1) {
                cargarDatosDeMascota(idMascotaParaEditar);
                btnGuardarMascota.setText("Actualizar");
            }
        }

        btnGuardarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMascota();
            }
        });
    }

    private void setupSpinners() {
        // Lógica para Dueños
        SQLiteDatabase dbDueno = dbHelper.getReadableDatabase();
        Cursor duenoCursor = dbDueno.query(
                PetContract.DuenosEntry.TABLE_NAME,
                new String[]{PetContract.DuenosEntry.COLUMN_ID, PetContract.DuenosEntry.COLUMN_NOMBRE},
                null, null, null, null, null
        );
        SimpleCursorAdapter duenoAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                duenoCursor,
                new String[]{PetContract.DuenosEntry.COLUMN_NOMBRE},
                new int[]{android.R.id.text1},
                0
        );
        duenoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDueno.setAdapter(duenoAdapter);

        // Lógica para Razas
        SQLiteDatabase dbRaza = dbHelper.getReadableDatabase();
        Cursor razaCursor = dbRaza.query(
                PetContract.RazasEntry.TABLE_NAME,
                new String[]{PetContract.RazasEntry.COLUMN_ID, PetContract.RazasEntry.COLUMN_NOMBRE},
                null, null, null, null, null
        );
        SimpleCursorAdapter razaAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                razaCursor,
                new String[]{PetContract.RazasEntry.COLUMN_NOMBRE},
                new int[]{android.R.id.text1},
                0
        );
        razaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRaza.setAdapter(razaAdapter);

        // Nota: Los cursores deben permanecer abiertos, Android los gestiona a través del ciclo de vida del adaptador.
    }

    private void cargarDatosDeMascota(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                PetContract.MascotasEntry.COLUMN_NOMBRE,
                PetContract.MascotasEntry.COLUMN_ID_DUENO,
                PetContract.MascotasEntry.COLUMN_ID_RAZA
        };
        String selection = PetContract.MascotasEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                PetContract.MascotasEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.MascotasEntry.COLUMN_NOMBRE));
            long idDueno = cursor.getLong(cursor.getColumnIndexOrThrow(PetContract.MascotasEntry.COLUMN_ID_DUENO));
            long idRaza = cursor.getLong(cursor.getColumnIndexOrThrow(PetContract.MascotasEntry.COLUMN_ID_RAZA));

            etNombreMascota.setText(nombre);

            // Seleccionar los valores correctos en los Spinners
            seleccionarSpinnerItem(spinnerDueno, idDueno);
            seleccionarSpinnerItem(spinnerRaza, idRaza);

            cursor.close();
        }
        db.close();
    }

    // Método auxiliar para seleccionar un item en el Spinner por ID
    private void seleccionarSpinnerItem(Spinner spinner, long id) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Cursor cursor = (Cursor) adapter.getItem(i);
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(PetContract.DuenosEntry.COLUMN_ID)); // Funciona para ambas tablas
            if (itemId == id) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void guardarMascota() {
        String nombre = etNombreMascota.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener los IDs de las claves foráneas de los Spinners
        Cursor duenoCursor = (Cursor) spinnerDueno.getSelectedItem();
        Cursor razaCursor = (Cursor) spinnerRaza.getSelectedItem();
        long idDueno = duenoCursor.getLong(duenoCursor.getColumnIndexOrThrow(PetContract.DuenosEntry.COLUMN_ID));
        long idRaza = razaCursor.getLong(razaCursor.getColumnIndexOrThrow(PetContract.RazasEntry.COLUMN_ID));

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetContract.MascotasEntry.COLUMN_NOMBRE, nombre);
        values.put(PetContract.MascotasEntry.COLUMN_ID_DUENO, idDueno);
        values.put(PetContract.MascotasEntry.COLUMN_ID_RAZA, idRaza);

        if (idMascotaParaEditar == -1) {
            // Modo de adición (Insertar)
            db.insert(PetContract.MascotasEntry.TABLE_NAME, null, values);
            Toast.makeText(this, "Mascota guardada.", Toast.LENGTH_SHORT).show();
        } else {
            // Modo de edición (Actualizar)
            String selection = PetContract.MascotasEntry.COLUMN_ID + " = ?";
            String[] selectionArgs = { String.valueOf(idMascotaParaEditar) };
            db.update(PetContract.MascotasEntry.TABLE_NAME, values, selection, selectionArgs);
            Toast.makeText(this, "Mascota actualizada.", Toast.LENGTH_SHORT).show();
        }

        db.close();
        finish();
    }
}