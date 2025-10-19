package com.example.proyectodesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectodesqlite.bd.PetDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para ver, editar y eliminar los detalles de una mascota y su dueño.
 */
public class UpdateDeletePetActivity extends AppCompatActivity {

    private static final String TAG = "UpdateDeleteActivity";

    // 🚨 CORRECCIÓN CRÍTICA: Usamos la clave "pet_id" que ahora se envía desde MainActivity.
    private static final String EXTRA_MASCOTA_ID_KEY = "pet_id";

    private EditText mNombreEditText;
    private EditText mEdadEditText;
    private EditText mNombreDuenoEditText;
    private EditText mTelefonoDuenoEditText;
    private Spinner mRazaSpinner;
    private Button mUpdateButton;
    private Button mDeleteButton;
    private PetDatabase petDatabase;

    private long mCurrentPetId = -1;
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

        Intent intent = getIntent();

        // 1. Intentamos obtener el ID como un Long Extra, usando la clave "pet_id"
        mCurrentPetId = intent.getLongExtra(EXTRA_MASCOTA_ID_KEY, -1);

        // 🚨 Aquí el log mostrará el ID correcto (ej: 1), resolviendo tu error anterior.
        Log.d(TAG, "Intentando cargar mascota, ID recibido con clave '" + EXTRA_MASCOTA_ID_KEY + "': " + mCurrentPetId);


        // 2. Lógica de URI de respaldo (mantenida por si acaso)
        if (mCurrentPetId == -1) {
            Uri currentPetUri = intent.getData();

            if (currentPetUri != null) {
                String petIdString = currentPetUri.getLastPathSegment();
                try {
                    mCurrentPetId = Long.parseLong(petIdString);
                    Log.d(TAG, "Mascota ID obtenida de URI: " + mCurrentPetId);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error: El ID de la mascota en la URI no es un número válido.", e);
                }
            } else {
                Log.e(TAG, "Error: No se encontró un Long Extra con clave '" + EXTRA_MASCOTA_ID_KEY + "' ni una URI válida.");
            }
        }

        // 3. Verificación final del ID
        if (mCurrentPetId == -1) {
            Toast.makeText(this, "Error: No se encontró el ID de la mascota para editar.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar Razas (necesario antes de cargar los detalles)
        setupBreedSpinner();

        // Cargar Detalles de la Mascota (ahora con mCurrentPetId válido)
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
                // Aquí podrías agregar un diálogo de confirmación antes de eliminar
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

        // Añadir una raza por defecto para evitar listas vacías si la BD no tiene datos
        razaNombres.add("Cargando...");
        razaIds.add(-1L);

        if (cursor != null && cursor.moveToFirst()) {
            // Limpiar los valores por defecto
            razaNombres.clear();
            razaIds.clear();

            // Usar getColumnIndex con strings para mayor robustez y resolver error de símbolo
            // Asumimos que la columna de nombre de raza se llama "nombre"
            int nameColumnIndex = cursor.getColumnIndexOrThrow("nombre");
            // Asumimos que la columna ID se llama "_id" (como BaseColumns)
            int idColumnIndex = cursor.getColumnIndexOrThrow("_id");

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
        // Usa el ID que ahora esperamos sea válido (ej: 1)
        Cursor cursor = petDatabase.getPetDetailsCursor(mCurrentPetId);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Extracción de datos de Mascota usando nombres de columna string
                String petNombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                int petEdad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
                long razaId = cursor.getLong(cursor.getColumnIndexOrThrow("raza_id"));
                mCurrentDuenoId = cursor.getLong(cursor.getColumnIndexOrThrow("dueno_id"));

                // Extraer datos de Dueño (asumimos aliases 'dueno_nombre' y 'dueno_telefono' en el JOIN)
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
                } else {
                    Log.w(TAG, "ID de Raza no encontrada en la lista del Spinner.");
                }

                Log.d(TAG, "Detalles cargados exitosamente para ID: " + mCurrentPetId);

            } catch (IllegalArgumentException e) {
                // Esto atrapa errores si los nombres de las columnas (o aliases) en el cursor son incorrectos
                Log.e(TAG, "Error al obtener columnas del cursor en loadPetDetails.", e);
                Toast.makeText(this, "Error de datos: verifica las columnas en PetDatabase.", Toast.LENGTH_LONG).show();
            } finally {
                cursor.close();
            }

        } else {
            // Este es el caso donde la consulta a la DB falla.
            Log.e(TAG, "Error: La mascota con ID " + mCurrentPetId + " no se encontró en la DB.");
            Toast.makeText(this, "Error al cargar detalles de la mascota. ID no encontrado.", Toast.LENGTH_SHORT).show();
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

        // 1. Validación de campos
        if (petNombre.isEmpty() || petEdadStr.isEmpty() || duenoNombre.isEmpty() || duenoTelefono.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int petEdad;
        try {
            petEdad = Integer.parseInt(petEdadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La edad debe ser un número entero válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        long razaId = razaIds.get(mRazaSpinner.getSelectedItemPosition());

        // 2. Actualización de la mascota y el dueño
        int rowsAffected = petDatabase.updatePetAndOwner(
                mCurrentPetId,
                petNombre,
                petEdad,
                razaId,
                mCurrentDuenoId,
                duenoNombre,
                duenoTelefono
        );

        // 3. Resultados
        if (rowsAffected > 0) {
            Toast.makeText(this, "Mascota actualizada con éxito!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar la mascota. Verifica el método en PetDatabase.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Error al eliminar la mascota. Verifica el método en PetDatabase.", Toast.LENGTH_SHORT).show();
        }
    }
}
