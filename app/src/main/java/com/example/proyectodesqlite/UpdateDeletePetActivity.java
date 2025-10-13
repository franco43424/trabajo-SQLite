package com.example.proyectodesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectodesqlite.bd.PetContract.MascotasEntry;
import com.example.proyectodesqlite.bd.PetContract.RazasEntry;
import com.example.proyectodesqlite.bd.PetDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para ver, editar y eliminar los detalles de una mascota y su dueño.
 */
public class UpdateDeletePetActivity extends AppCompatActivity {

    private EditText mNombreEditText;
    private EditText mEdadEditText;
    private EditText mNombreDuenoEditText;
    private EditText mTelefonoDuenoEditText;
    private Spinner mRazaSpinner;
    private Button mUpdateButton;
    private Button mDeleteButton;
    private PetDatabase petDatabase;

    private long mCurrentPetId;
    private long mCurrentDuenoId;
    private List<Long> razaIds; // Para mapear la posición del Spinner con el ID de la raza

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_pet);

        petDatabase = new PetDatabase(this);

        // Inicializar Vistas
        mNombreEditText = findViewById(R.id.edit_pet_name);
        mEdadEditText = findViewById(R.id.edit_pet_age);
        mNombreDuenoEditText = findViewById(R.id.edit_owner_name);
        mTelefonoDuenoEditText = findViewById(R.id.edit_owner_phone);
        mRazaSpinner = findViewById(R.id.spinner_breed);
        mUpdateButton = findViewById(R.id.BtnUpdatePet);
        mDeleteButton = findViewById(R.id.BtnDeletePet);

        // Obtener el ID de la mascota a editar
        Intent intent = getIntent();
        // Usamos la misma clave que enviaremos desde MainActivity
        mCurrentPetId = intent.getLongExtra("pet_id", -1);

        if (mCurrentPetId == -1) {
            Toast.makeText(this, "Error: No se encontró el ID de la mascota.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar Razas (necesario antes de cargar los detalles)
        setupBreedSpinner();

        // Cargar Detalles de la Mascota
        loadPetDetails();

        // Configurar Listeners
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePet();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePet();
            }
        });
    }

    /**
     * Carga las razas disponibles y las popula en el Spinner.
     */
    private void setupBreedSpinner() {
        Cursor cursor = petDatabase.getBreedsCursor();
        List<String> razaNombres = new ArrayList<>();
        razaIds = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndexOrThrow(RazasEntry.COLUMN_NOMBRE);
            int idColumnIndex = cursor.getColumnIndexOrThrow(RazasEntry._ID);

            do {
                razaNombres.add(cursor.getString(nameColumnIndex));
                razaIds.add(cursor.getLong(idColumnIndex));
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                razaNombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRazaSpinner.setAdapter(adapter);
    }

    /**
     * Carga los datos de la mascota actual en la UI.
     */
    private void loadPetDetails() {
        Cursor cursor = petDatabase.getPetDetailsCursor(mCurrentPetId);

        if (cursor != null && cursor.moveToFirst()) {
            // Extraer datos de Mascota
            String petNombre = cursor.getString(cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_NOMBRE));
            int petEdad = cursor.getInt(cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_EDAD));
            long razaId = cursor.getLong(cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_RAZA_ID));
            mCurrentDuenoId = cursor.getLong(cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_DUENO_ID));

            // Extraer datos de Dueño (aliased in PetDatabase)
            // Asegúrate que el método getPetDetailsCursor en PetDatabase usa "dueno_nombre" y "dueno_telefono"
            String duenoNombre = cursor.getString(cursor.getColumnIndexOrThrow("dueno_nombre"));
            String duenoTelefono = cursor.getString(cursor.getColumnIndexOrThrow("dueno_telefono"));

            // Rellenar campos
            mNombreEditText.setText(petNombre);
            mEdadEditText.setText(String.valueOf(petEdad));
            mNombreDuenoEditText.setText(duenoNombre);
            mTelefonoDuenoEditText.setText(duenoTelefono);

            // Seleccionar Raza
            int razaPosition = razaIds.indexOf(razaId);
            if (razaPosition != -1) {
                mRazaSpinner.setSelection(razaPosition);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Error al cargar detalles.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Recoge los datos de los campos y llama a la función de actualización en la base de datos.
     */
    private void updatePet() {
        String petNombre = mNombreEditText.getText().toString().trim();
        String petEdadStr = mEdadEditText.getText().toString().trim();
        String duenoNombre = mNombreDuenoEditText.getText().toString().trim();
        String duenoTelefono = mTelefonoDuenoEditText.getText().toString().trim();

        // Validación simple
        if (petNombre.isEmpty() || petEdadStr.isEmpty() || duenoNombre.isEmpty() || duenoTelefono.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int petEdad = Integer.parseInt(petEdadStr);
        long razaId = razaIds.get(mRazaSpinner.getSelectedItemPosition());

        // Usamos el método que actualiza mascota y dueño
        int rowsAffected = petDatabase.updatePetAndOwner(
                mCurrentPetId,
                petNombre,
                petEdad,
                razaId,
                mCurrentDuenoId,
                duenoNombre,
                duenoTelefono
        );

        if (rowsAffected > 0) {
            Toast.makeText(this, "Mascota actualizada con éxito!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar la mascota.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Elimina la mascota actual de la base de datos.
     */
    private void deletePet() {
        int deletedRows = petDatabase.deletePet(mCurrentPetId);

        if (deletedRows > 0) {
            Toast.makeText(this, "Mascota eliminada con éxito!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar la mascota.", Toast.LENGTH_SHORT).show();
        }
    }
}