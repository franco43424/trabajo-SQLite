package com.example.proyectodesqlite;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listaMascotas;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        listaMascotas = findViewById(R.id.lista_mascotas);
        fabAdd = findViewById(R.id.fab_add);

        // 1. Configuración de la navegación (Crear)
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPetActivity.class);
                startActivity(intent);
            }
        });

        // 2. Configuración del menú de opciones (Clic largo para Editar/Eliminar)
        listaMascotas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                mostrarOpciones(id);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarga la lista cada vez que la actividad vuelve a estar en primer plano
        cargarDatos();
    }

    private void cargarDatos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 3. Consulta SQL con LEFT JOIN para obtener el nombre de la mascota, dueño y raza
        String selectQuery = "SELECT " +
                "T1." + PetContract.MascotasEntry.COLUMN_ID + " AS " + PetContract.MascotasEntry.COLUMN_ID + ", " +
                "T1." + PetContract.MascotasEntry.COLUMN_NOMBRE + ", " +
                "T2." + PetContract.DuenosEntry.COLUMN_NOMBRE + " AS nombre_dueno, " +
                "T3." + PetContract.RazasEntry.COLUMN_NOMBRE + " AS nombre_raza " +
                "FROM " + PetContract.MascotasEntry.TABLE_NAME + " T1 " +
                "LEFT JOIN " + PetContract.DuenosEntry.TABLE_NAME + " T2 " +
                "ON T1." + PetContract.MascotasEntry.COLUMN_ID_DUENO + " = T2." + PetContract.DuenosEntry.COLUMN_ID +
                " LEFT JOIN " + PetContract.RazasEntry.TABLE_NAME + " T3 " +
                "ON T1." + PetContract.MascotasEntry.COLUMN_ID_RAZA + " = T3." + PetContract.RazasEntry.COLUMN_ID;

        // Ejecutar la consulta
        Cursor cursor = db.rawQuery(selectQuery, null);

        // 4. Configuración del SimpleCursorAdapter con los alias de las columnas
        String[] fromColumns = {
                PetContract.MascotasEntry.COLUMN_NOMBRE,
                "nombre_dueno",
                "nombre_raza"
        };

        int[] toViews = {
                R.id.tvNombreMascota,
                R.id.tvNombreDueno,
                R.id.tvNombreRaza
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_pet,
                cursor,
                fromColumns,
                toViews,
                0
        );
        listaMascotas.setAdapter(adapter);
    }

    private void mostrarOpciones(final long idMascota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones");
        builder.setMessage("¿Qué deseas hacer con esta mascota?");

        // Opción: Eliminar
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarMascota(idMascota);
            }
        });

        // Opción: Editar
        builder.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, AddPetActivity.class);
                intent.putExtra("ID_MASCOTA", idMascota);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Cancelar", null);
        builder.show();
    }

    private void eliminarMascota(long idMascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = PetContract.MascotasEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(idMascota) };

        // Ejecutar el comando de borrado (Delete)
        db.delete(
                PetContract.MascotasEntry.TABLE_NAME,
                selection,
                selectionArgs);

        db.close();
        cargarDatos(); // Recarga la lista para mostrar el cambio
    }
}