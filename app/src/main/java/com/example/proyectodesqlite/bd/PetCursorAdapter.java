package com.example.proyectodesqlite.bd;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.proyectodesqlite.bd.PetContract.MascotasEntry;

/**
 * CursorAdapter personalizado para mostrar los detalles de la mascota en la ListView.
 * Muestra: Nombre de la mascota y, en la segunda línea, la edad, la raza y el nombre del dueño.
 * * NOTA: Este adaptador asume que el Cursor devuelto por petDatabase.getAllPetsCursor()
 * contiene columnas aliased como "raza_nombre" y "dueno_nombre".
 * Asume que el layout de cada elemento de lista es simple, con text1 y text2.
 */
public class PetCursorAdapter extends CursorAdapter {

    /**
     * Constructor.
     * @param context El contexto de la aplicación.
     * @param c El cursor de la base de datos desde el que obtener los datos.
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Infla una nueva vista de elemento de lista.
     * Usamos android.R.layout.simple_list_item_2 porque tiene text1 y text2.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
    }

    /**
     * Une los datos del cursor a una vista de elemento de lista existente.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Encontrar los TextViews en el layout simple_list_item_2
        TextView nameTextView = view.findViewById(android.R.id.text1);
        TextView summaryTextView = view.findViewById(android.R.id.text2);

        // Encontrar los índices de las columnas
        int nameColumnIndex = cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_NOMBRE);
        int edadColumnIndex = cursor.getColumnIndexOrThrow(MascotasEntry.COLUMN_EDAD);

        // Asumimos que PetDatabase ha unido las tablas y ha usado estos aliases
        int razaNameColumnIndex = cursor.getColumnIndexOrThrow("raza_nombre");
        int duenoNameColumnIndex = cursor.getColumnIndexOrThrow("dueno_nombre");

        // Leer los atributos de la mascota
        String petName = cursor.getString(nameColumnIndex);
        int petEdad = cursor.getInt(edadColumnIndex);
        String razaName = cursor.getString(razaNameColumnIndex);
        String duenoName = cursor.getString(duenoNameColumnIndex);

        // Rellenar el TextView principal (text1)
        nameTextView.setText(petName);

        // Rellenar el TextView secundario (text2) con un resumen
        String petSummary = "Edad: " + petEdad + " años | Raza: " + razaName + " | Dueño: " + duenoName;
        summaryTextView.setText(petSummary);
    }
}
