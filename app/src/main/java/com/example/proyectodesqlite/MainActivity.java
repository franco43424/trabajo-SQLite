package com.example.proyectodesqlite;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodesqlite.bd.PetDatabase;
import com.example.proyectodesqlite.bd.PetCursorAdapter;
import com.example.proyectodesqlite.bd.PetContract;

// Importaciones para los botones de Material Design
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton; // 👈 NUEVA IMPORTACIÓN

/**
 * Muestra una lista de mascotas que fueron ingresadas y almacenadas en la base de datos.
 */
public class MainActivity extends AppCompatActivity {

    private PetDatabase petDatabase;
    private PetCursorAdapter mCursorAdapter;
    private ListView petListView;

    // 👈 NUEVA DECLARACIÓN: Botón para ver las citas
    private MaterialButton btnViewSchedule;

    private static final String TAG = "MainActivity";

    // 🚨 Nueva constante para la clave del ID, que DEBE COINCIDIR con la clave que usa la actividad de edición.
    public static final String EXTRA_PET_ID = "pet_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petDatabase = new PetDatabase(this);

        // 1. Configuración del Floating Action Button (FAB)
        FloatingActionButton fab = findViewById(R.id.fab_add_pet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad para añadir una nueva mascota
                Intent intent = new Intent(MainActivity.this, AddPetActivity.class);
                startActivity(intent);
            }
        });

        // 2. 👈 NUEVA LÓGICA: Configuración del Botón "Ver Citas"
        btnViewSchedule = findViewById(R.id.btn_view_schedule);
        btnViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de Citas
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        // 3. Encontrar la ListView que se llenará con los datos de las mascotas
        petListView = findViewById(R.id.pet_list_view);

        // 4. Configurar la vista vacía
        RelativeLayout emptyView = findViewById(R.id.empty_view_container);
        petListView.setEmptyView(emptyView);

        // 5. Obtener el cursor y configurar el adaptador
        displayDatabaseInfo();

        // 6. Configurar el Listener de clic en los elementos de la lista para edición
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Iniciar la actividad de Edición/Eliminación
                Intent intent = new Intent(MainActivity.this, UpdateDeletePetActivity.class);

                // 🚨 CORRECCIÓN: Usar la constante EXTRA_PET_ID para enviar el ID
                intent.putExtra(EXTRA_PET_ID, id);

                Log.d(TAG, "Mascota seleccionada con ID: " + id + " con clave: " + EXTRA_PET_ID);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Recargar los datos cada vez que la actividad vuelve al primer plano
        displayDatabaseInfo();
    }

    /**
     * Obtiene datos de la base de datos y muestra el cursor en la ListView.
     */
    private void displayDatabaseInfo() {
        // Obtenemos el cursor con todos los datos y JOINs
        Cursor cursor = petDatabase.getAllPetsCursor();

        if (mCursorAdapter == null) {
            // Si el adaptador es nulo (primera carga), lo creamos
            mCursorAdapter = new PetCursorAdapter(this, cursor);
            petListView.setAdapter(mCursorAdapter);
        } else {
            // Si ya existe, actualizamos el cursor para reflejar los cambios en la BD
            mCursorAdapter.swapCursor(cursor);
        }
    }


    // --- Configuración del Menú ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el archivo de menú 'main_menu.xml'
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // 🚨 La acción de agendar una cita ahora está en el botón principal,
        // pero mantenemos la opción de menú por si acaso.
        if (id == R.id.action_schedule) {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
            return true;
        }

        // Maneja la inserción de datos de prueba
        if (id == R.id.action_insert_dummy_data) {
            insertDummyPet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Inserta una mascota de ejemplo en la base de datos para pruebas.
     */
    private void insertDummyPet() {
        // Asumimos que PetDatabase tiene el método insertOwner
        long dummyOwnerId = petDatabase.insertOwner("John Doe (Dummy)", "555-1234", null);

        if (dummyOwnerId == -1) {
            Toast.makeText(this, "Error al insertar dueño de prueba.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Asumimos que PetDatabase tiene el método getBreedIdByName
        long razaId = petDatabase.getBreedIdByName("Labrador Retriever");

        if (razaId == -1) {
            // Si no encuentra la raza, usa 1, que debería ser "Sin Raza" o el ID predeterminado.
            razaId = 1;
        }

        // Asumimos que PetDatabase tiene el método insertPet
        long newRowId = petDatabase.insertPet(
                "Buddy",        // nombre
                3,              // edad
                razaId,         // razaId
                dummyOwnerId,   // duenoId
                null            // photoUri
        );

        if (newRowId != -1) {
            Toast.makeText(this, "Mascota de prueba insertada (ID: " + newRowId + ")", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al insertar mascota de prueba.", Toast.LENGTH_SHORT).show();
        }

        // Recargar la lista para mostrar el nuevo elemento
        displayDatabaseInfo();
    }
}
