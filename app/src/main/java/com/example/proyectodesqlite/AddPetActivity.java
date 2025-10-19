package com.example.proyectodesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectodesqlite.bd.PetDatabase;

import java.util.List;

/**
 * Actividad para añadir una nueva mascota y su dueño asociado.
 * Esta actividad gestiona la inserción completa de Dueño y Mascota en un solo paso.
 */
public class AddPetActivity extends AppCompatActivity {

    private static final String TAG = "AddPetActivity";

    private EditText mNombreMascotaEditText;
    private EditText mEdadMascotaEditText;
    private EditText mNombreDuenoEditText;
    private EditText mTelefonoDuenoEditText;
    private Spinner mRazaSpinner;
    private Button mSaveButton;

    private PetDatabase petDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos el layout que contiene tanto los campos de la mascota como los del dueño
        setContentView(R.layout.activity_add_pet);

        petDatabase = new PetDatabase(this);

        // Inicializar Vistas
        mNombreMascotaEditText = findViewById(R.id.edit_pet_name);
        mEdadMascotaEditText = findViewById(R.id.edit_pet_age);
        mNombreDuenoEditText = findViewById(R.id.edit_owner_name);
        mTelefonoDuenoEditText = findViewById(R.id.edit_owner_phone);
        mRazaSpinner = findViewById(R.id.spinner_breed);
        mSaveButton = findViewById(R.id.BtnSavePet);

        // Cargar las razas en el Spinner
        setupBreedSpinner();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePet();
            }
        });
    }

    /**
     * Obtiene los nombres de las razas de la BD y los carga en el Spinner.
     */
    private void setupBreedSpinner() {
        try {
            // Este método DEBE existir en PetDatabase.java
            List<String> breedNames = petDatabase.getAllBreedNames();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    breedNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mRazaSpinner.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar el Spinner de Razas", e);
            Toast.makeText(this, "Error al cargar las razas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Recoge los datos y los inserta primero en la tabla Dueños y luego en Mascotas,
     * usando el ID del Dueño insertado como clave foránea.
     */
    private void savePet() {
        // Recoger datos de la mascota
        String petNombre = mNombreMascotaEditText.getText().toString().trim();
        String petEdadStr = mEdadMascotaEditText.getText().toString().trim();
        String razaNombre = (String) mRazaSpinner.getSelectedItem();

        // Recoger datos del dueño
        String duenoNombre = mNombreDuenoEditText.getText().toString().trim();
        String duenoTelefono = mTelefonoDuenoEditText.getText().toString().trim();

        // 1. Validación de campos obligatorios
        if (petNombre.isEmpty() || petEdadStr.isEmpty() || duenoNombre.isEmpty() || duenoTelefono.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        int petEdad;
        // Validación de formato numérico de la edad
        try {
            petEdad = Integer.parseInt(petEdadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La edad debe ser un número entero válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 2. Insertar Dueño (Esto debe hacerse primero para obtener el duenoId)
            // Asumimos que no hay URI de foto por ahora (pasamos null)
            long duenoId = petDatabase.insertOwner(duenoNombre, duenoTelefono, null);

            if (duenoId == -1) {
                Toast.makeText(this, "Error crítico al guardar el dueño.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Obtener ID de la Raza
            long razaId = petDatabase.getBreedIdByName(razaNombre);

            if (razaId == -1) {
                Log.w(TAG, "Advertencia: Raza no encontrada. Usando ID 1 (Asumido 'Sin Raza').");
                Toast.makeText(this, "Raza no encontrada. Usando valor por defecto.", Toast.LENGTH_SHORT).show();
                // Se asume que la ID 1 corresponde a una raza por defecto (ej: 'Sin Raza')
                razaId = 1;
            }

            // 4. Insertar Mascota (Usando las Claves Foráneas de Dueño y Raza)
            // Asumimos que no hay URI de foto por ahora (pasamos null)
            long petId = petDatabase.insertPet(petNombre, petEdad, razaId, duenoId, null);

            if (petId != -1) {
                Toast.makeText(this, "Mascota y Dueño guardados con ID: " + petId, Toast.LENGTH_LONG).show();

                // Indica a la actividad principal que se ha realizado un cambio
                setResult(RESULT_OK);
                finish(); // Cerrar la actividad y volver a MainActivity
            } else {
                Toast.makeText(this, "Error al guardar la mascota.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error durante la inserción de datos", e);
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
