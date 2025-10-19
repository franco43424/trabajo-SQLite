package com.example.proyectodesqlite;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodesqlite.bd.PetContract; // Importamos PetContract para las constantes
import com.example.proyectodesqlite.bd.PetDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Nombre de clase corregido
public class ScheduleActivity extends AppCompatActivity {

    private static final String TAG = "ScheduleActivity";

    private EditText etDate, etTime, etReason;
    private Spinner spinnerPet;
    private Button btnSchedule;

    private PetDatabase petDatabase;

    // Listas para manejar los IDs y nombres de las mascotas
    private List<Long> petIds;
    private List<String> petNames;
    private long selectedPetId = -1; // ID de la mascota actualmente seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos el layout original que indicaste
        setContentView(R.layout.activity_schedule);

        // Inicializar la base de datos
        petDatabase = new PetDatabase(this);

        // Inicializar Vistas (IDs tomados del XML del formulario)
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etReason = findViewById(R.id.etReason);
        spinnerPet = findViewById(R.id.spinnerPet);
        btnSchedule = findViewById(R.id.btnSchedule);

        // 1. Cargar las mascotas disponibles en el Spinner
        loadPetsIntoSpinner();

        // 2. Configurar el Listener para el Spinner
        spinnerPet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Guardar el ID de la mascota seleccionada
                if (petIds != null && position >= 0 && position < petIds.size()) {
                    selectedPetId = petIds.get(position);
                    Log.d(TAG, "Mascota seleccionada ID: " + selectedPetId + " (" + petNames.get(position) + ")");
                } else {
                    selectedPetId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPetId = -1;
            }
        });

        // 3. Configurar los selectores de fecha y hora
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        // 4. Listener del botón para guardar la cita
        btnSchedule.setOnClickListener(v -> saveAppointment());

        // Deshabilitar la escritura manual en los campos de fecha y hora
        etDate.setFocusable(false);
        etDate.setLongClickable(false);
        etTime.setFocusable(false);
        etTime.setLongClickable(false);
    }

    /**
     * Carga las mascotas existentes desde la base de datos en el Spinner.
     */
    private void loadPetsIntoSpinner() {
        // Usamos getAllPetsCursor() que devuelve los datos necesarios para identificar la mascota.
        Cursor cursor = petDatabase.getAllPetsCursor();
        petIds = new ArrayList<>();
        petNames = new ArrayList<>();

        // 1. Valor por defecto
        petNames.add("Selecciona una mascota");
        petIds.add(-1L);

        if (cursor != null && cursor.moveToFirst()) {
            // Limpiar si hay datos reales
            petNames.clear();
            petIds.clear();

            try {
                // Usamos las constantes de Mascota de PetContract para mayor robustez
                int nameColumnIndex = cursor.getColumnIndexOrThrow(PetContract.MascotasEntry.COLUMN_NAME_NOMBRE);
                int idColumnIndex = cursor.getColumnIndexOrThrow(PetContract.MascotasEntry._ID);

                do {
                    petNames.add(cursor.getString(nameColumnIndex));
                    petIds.add(cursor.getLong(idColumnIndex));
                } while (cursor.moveToNext());
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error al obtener columnas de la mascota: " + e.getMessage());
                // Si falla, se queda con la lista vacía para que solo se muestre el valor por defecto
            } finally {
                cursor.close();
            }
        }

        // Si no hay mascotas (y la lista se vació) o si solo quedó el placeholder inicial,
        // asegurar que el mensaje de "no hay mascotas" se muestre.
        if (petNames.isEmpty() || (petNames.size() == 1 && petIds.get(0) == -1L)) {
            petNames.clear();
            petIds.clear();
            petNames.add("No hay mascotas registradas");
            petIds.add(-1L);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                petNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPet.setAdapter(adapter);
    }


    /**
     * Muestra el diálogo para seleccionar la fecha y la formatea (DD/MM/AAAA).
     */
    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Formato: DD/MM/AAAA (selectedMonth es base 0, por eso + 1)
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    etDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Muestra el diálogo para seleccionar la hora y la formatea (HH:MM - 24h).
     */
    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    // Formato: HH:MM
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    etTime.setText(time);
                }, hour, minute, true); // true para formato de 24 horas
        timePickerDialog.show();
    }

    /**
     * Valida los datos y guarda la cita en la base de datos, incluyendo el ID de la mascota.
     */
    private void saveAppointment() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        // 1. Validación de campos de texto
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(reason)) {
            Toast.makeText(this, "Por favor, completa la fecha, hora y el motivo de la cita.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validación de selección de mascota
        // selectedPetId <= 0 asegura que no sea el valor por defecto (-1) ni un ID de mascota inválido (0)
        if (selectedPetId <= 0) {
            Toast.makeText(this, "Por favor, selecciona una mascota válida para agendar la cita.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Guardar la cita
        Log.d(TAG, "Intentando guardar cita para Pet ID: " + selectedPetId);

        // Llamada al método ya implementado correctamente en PetDatabase
        long result = petDatabase.insertAppointment(selectedPetId, date, time, reason);

        // 4. Resultados
        if (result > 0) {
            Toast.makeText(this, "Cita agendada correctamente.", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad y regresa a la anterior
        } else {
            Toast.makeText(this, "Error al guardar la cita. Por favor, revisa los logs de la base de datos.", Toast.LENGTH_LONG).show();
        }
    }
}
