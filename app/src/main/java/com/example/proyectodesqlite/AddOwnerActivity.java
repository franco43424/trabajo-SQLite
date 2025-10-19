package com.example.proyectodesqlite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodesqlite.bd.PetDatabase;

public class AddOwnerActivity extends AppCompatActivity {

    private EditText etOwnerName, etOwnerPhone, etOwnerPhotoUri;
    private PetDatabase petDatabase;

    // Variables para almacenar los datos de la mascota pasados por Intent
    private String petName;
    private int petAge;
    private String petPhotoUri;
    private long petRazaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asume que tienes un layout llamado activity_add_owner
        setContentView(R.layout.activity_add_owner);

        // Inicializar la base de datos
        petDatabase = new PetDatabase(this);

        // Obtener los datos de la mascota del Intent
        getPetDataFromIntent();

        // Inicializar Vistas
        etOwnerName = findViewById(R.id.etOwnerName);
        etOwnerPhone = findViewById(R.id.etOwnerPhone);
        etOwnerPhotoUri = findViewById(R.id.etOwnerPhotoUri);
        Button btnSavePet = findViewById(R.id.btnSavePet);

        // Listener del botón para guardar el dueño y la mascota
        btnSavePet.setOnClickListener(v -> saveOwnerAndPet());
    }

    /**
     * Recupera todos los datos de la mascota pasados desde AddPetActivity.
     */
    private void getPetDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            petName = extras.getString("PET_NAME", "");
            petAge = extras.getInt("PET_AGE", 0);
            petPhotoUri = extras.getString("PET_PHOTO_URI", "");
            petRazaId = extras.getLong("PET_RAZA_ID", -1);
        } else {
            Toast.makeText(this, "Error: No se encontraron datos de la mascota.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Valida los datos del dueño, guarda el dueño en la DB,
     * y luego guarda la mascota usando el ID del dueño.
     */
    private void saveOwnerAndPet() {
        String ownerName = etOwnerName.getText().toString().trim();
        String ownerPhone = etOwnerPhone.getText().toString().trim();
        String ownerPhotoUri = etOwnerPhotoUri.getText().toString().trim();

        // 1. Validación del Dueño
        if (TextUtils.isEmpty(ownerName) || TextUtils.isEmpty(ownerPhone)) {
            Toast.makeText(this, "Por favor, completa el nombre y teléfono del dueño.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Guardar Dueño
        long duenoId = petDatabase.insertOwner(ownerName, ownerPhone, ownerPhotoUri);

        if (duenoId == -1) {
            Toast.makeText(this, "Error crítico al guardar el dueño.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Guardar Mascota usando el ID del Dueño
        // 🚨 CORRECCIÓN CLAVE: El orden de los argumentos petRazaId y duenoId fue invertido
        // para coincidir con la firma: (name, age, razaId, duenoId, photoUri)
        long petResult = petDatabase.insertPet(
                petName,
                petAge,
                petRazaId, // Ahora es el 3er argumento (razaId)
                duenoId,   // Ahora es el 4to argumento (duenoId)
                petPhotoUri
        );

        if (petResult > 0) {
            Toast.makeText(this, "Mascota y Dueño guardados correctamente!", Toast.LENGTH_LONG).show();
            // Cerrar ambas actividades de registro y volver al MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            // Flags para limpiar la pila de actividades y volver al inicio
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la mascota.", Toast.LENGTH_LONG).show();
        }
    }
}
