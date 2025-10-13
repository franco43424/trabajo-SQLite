package com.example.proyectodesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.Cursor;
import com.example.proyectodesqlite.bd.PetContract.RazasEntry;
import com.example.proyectodesqlite.bd.PetDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para agregar una nueva mascota (y su dueño, ya que el dueño existente fue eliminado).
 */
public class AddPetActivity extends AppCompatActivity {

    private EditText mNombreEditText;
    private EditText mEdadEditText;
    private EditText mNombreDuenoEditText;
    private EditText mTelefonoDuenoEditText;
    private Spinner mRazaSpinner;
    private PetDatabase petDatabase;

    private List<Long> razaIds; // Para mapear la posición del Spinner con el ID de la raza

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet); // Usamos el layout actualizado

        // Inicializar la base de datos
        petDatabase = new PetDatabase(this);

        // Inicializar Vistas
        mNombreEditText = findViewById(R.id.edit_pet_name);
        mEdadEditText = findViewById(R.id.edit_pet_age);
        mNombreDuenoEditText = findViewById(R.id.edit_owner_name);
        mTelefonoDuenoEditText = findViewById(R.id.edit_owner_phone);
        mRazaSpinner = findViewById(R.id.spinner_breed);
        Button saveButton = findViewById(R.id.BtnSavePet);

        // Configurar Spinners
        setupBreedSpinner();

        // Configurar Listener del botón de guardar
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarMascota();
            }
        });
    }

    /**
     * Carga las razas disponibles desde la base de datos y las popula en el Spinner.
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
     * Lee la entrada del usuario y guarda el nuevo Dueño y la nueva Mascota en la base de datos.
     * Esta es la función principal que resuelve el error de validación y guarda los datos.
     */
    private void guardarMascota() {
        String petNombre = mNombreEditText.getText().toString().trim();
        String petEdadStr = mEdadEditText.getText().toString().trim();
        String duenoNombre = mNombreDuenoEditText.getText().toString().trim();
        String duenoTelefono = mTelefonoDuenoEditText.getText().toString().trim();

        // 1. Validar que todos los campos obligatorios estén llenos
        if (petNombre.isEmpty() || petEdadStr.isEmpty() || duenoNombre.isEmpty() || duenoTelefono.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los datos de la Mascota y el Dueño.", Toast.LENGTH_LONG).show();
            return;
        }

        int petEdad = Integer.parseInt(petEdadStr);

        // Obtener el ID de la raza seleccionada
        int selectedPosition = mRazaSpinner.getSelectedItemPosition();
        if (selectedPosition == Spinner.INVALID_POSITION || razaIds.isEmpty()) {
            Toast.makeText(this, "Error al seleccionar la raza.", Toast.LENGTH_SHORT).show();
            return;
        }
        long razaId = razaIds.get(selectedPosition);

        // 2. Insertar el nuevo Dueño y obtener su ID
        long duenoId = petDatabase.insertOwner(duenoNombre, duenoTelefono);

        if (duenoId == -1) {
            Toast.makeText(this, "Error al guardar el Dueño.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Insertar la Mascota usando el duenoId recién generado
        long petId = petDatabase.insertPet(petNombre, petEdad, duenoId, razaId);

        if (petId == -1) {
            // Si la mascota falla, considera revertir la inserción del dueño (transacción)
            Toast.makeText(this, "Error al guardar la Mascota.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Mascota guardada con éxito!", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad para volver a la lista principal
        }
    }
}