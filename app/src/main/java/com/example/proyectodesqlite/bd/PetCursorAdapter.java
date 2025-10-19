package com.example.proyectodesqlite.bd;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.proyectodesqlite.R;
import com.example.proyectodesqlite.bd.PetContract.MascotasEntry; // Importamos la clase de entrada de Mascotas

/**
 * Un CursorAdapter que toma un Cursor de mascotas (que incluye dueños y razas)
 * y crea/actualiza los elementos de la ListView en MainActivity.
 * Utiliza los IDs del layout list_item_pet.xml.
 * * NOTA: Asume que el Cursor incluye los ALIAS 'owner_name_alias' y 'breed_name_alias'
 * generados por PetDatabase.getAllPetsCursor().
 */
public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Hace una nueva vista de elemento de lista.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the list_item_pet.xml layout
        return LayoutInflater.from(context).inflate(R.layout.list_item_pet, parent, false);
    }

    /**
     * Vincula los datos del Cursor a las dos vistas principales en el layout:
     * 1. pet_name (Nombre de la Mascota)
     * 2. pet_details (Raza, Edad, Dueño)
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Encontrar las vistas usando los IDs corregidos de list_item_pet.xml:
        TextView petNameTextView = view.findViewById(R.id.pet_name);
        TextView petDetailsTextView = view.findViewById(R.id.pet_details);

        // 1. Obtener índices de columna (Usando constantes del contrato para evitar errores de tipo "column does not exist")

        // Nombre de la Mascota
        int petNameColumnIndex = cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_NAME_NOMBRE);

        // Edad
        int petAgeColumnIndex = cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_NAME_EDAD);

        // Aliases para Dueño y Raza (DEBEN coincidir con PetDatabase.getAllPetsCursor() si usa JOIN)
        int ownerNameColumnIndex = cursor.getColumnIndexOrThrow("owner_name_alias");
        int breedNameColumnIndex = cursor.getColumnIndexOrThrow("breed_name_alias");

        // 2. Extraer valores del Cursor
        String petName = cursor.getString(petNameColumnIndex);
        int petAge = cursor.getInt(petAgeColumnIndex);
        String ownerName = cursor.getString(ownerNameColumnIndex);
        String breedName = cursor.getString(breedNameColumnIndex);

        // 3. Crear el texto de detalles combinado
        String details = String.format("Raza: %s | Edad: %d años | Dueño: %s",
                breedName, petAge, ownerName);

        // 4. Actualizar TextViews
        petNameTextView.setText(petName);
        petDetailsTextView.setText(details);
    }
}
