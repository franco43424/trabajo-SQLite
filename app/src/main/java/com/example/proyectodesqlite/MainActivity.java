package com.example.proyectodesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ImageButton;

import com.example.proyectodesqlite.bd.PetCursorAdapter;
import com.example.proyectodesqlite.bd.PetDatabase;

/**
 * Actividad principal que muestra la lista de mascotas y maneja la navegación para agregar
 * y editar/eliminar.
 */
public class MainActivity extends AppCompatActivity {

    private PetDatabase petDatabase;
    private ListView listViewMascotas;
    private ImageButton btnAddPet;

    // Usamos PetCursorAdapter que acabas de crear para mostrar datos complejos
    private PetCursorAdapter petCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar DAO y componentes
        petDatabase = new PetDatabase(this);
        listViewMascotas = findViewById(R.id.listViewMascotas); // ID de la ListView
        btnAddPet = findViewById(R.id.BtnAddPet); // ID del botón '+'

        // Inicializar adaptador (con cursor nulo, se actualizará en onResume)
        petCursorAdapter = new PetCursorAdapter(this, null);
        listViewMascotas.setAdapter(petCursorAdapter);

        // 🟢 Abrir la pantalla de agregar mascota
        btnAddPet.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPetActivity.class);
            startActivity(intent);
        });

        // 🟡 Abrir la pantalla de edición/eliminación al hacer clic en un ítem
        listViewMascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                // 'id' es el _ID de la mascota en la base de datos

                Intent intent = new Intent(MainActivity.this, UpdateDeletePetActivity.class);
                intent.putExtra("pet_id", id); // Usamos la clave "pet_id" para enviar el ID
                startActivity(intent);
            }
        });
    }

    /**
     * Carga la lista de mascotas desde la base de datos y actualiza la ListView.
     */
    private void loadPets() {
        // Obtenemos el Cursor, que ahora incluye nombres de Dueño y Raza gracias al DAO.
        Cursor cursor = petDatabase.getAllPetsCursor();

        // Intercambiamos el cursor del adaptador para refrescar la lista
        petCursorAdapter.swapCursor(cursor);
    }

    /**
     * Recargar la lista cada vez que se vuelve a la actividad (después de agregar/editar/eliminar)
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPets();
    }
}
