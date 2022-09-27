package com.example.labapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnAgregar;
    EditText etxtNombre;
    Intent intent;
    ListView listEstudiante;

    Estudiante estudiante;
    ArrayList<String> listaEstudiantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAgregar = findViewById(R.id.btnAgregarEstudiante);
        etxtNombre = findViewById(R.id.etxtNombre);
        listEstudiante = findViewById(R.id.listEstudiante);

        MainActivity context = this;

        listarEstudiante();

        btnAgregar.setOnClickListener(v -> {
            if (!etxtNombre.getText().toString().isEmpty()) {
                agregarEstudiante();
                listarEstudiante();
            } else {
                Toast.makeText(context, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        listEstudiante.setOnItemClickListener((adapterView, view, i, l) -> {
            String[] cadenas = listEstudiante.getItemAtPosition(i).toString().split(" - ");
            int id = Integer.parseInt(cadenas[0]);
            double promedio = Double.parseDouble(cadenas[1]);
            String nombre = cadenas[2];
            intent = new Intent(context, GestionNotas.class);
            intent.putExtra("paramsId", id);
            intent.putExtra("paramsPromedio", promedio);
            intent.putExtra("paramsNombre", nombre);
            startActivity(intent);
        });
    }

    private void agregarEstudiante() {
        try {
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase bd = helper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("NOMBRE", etxtNombre.getText().toString().toUpperCase());
            values.put("PROMEDIO", 0.0);

            long id = bd.insert(Constantes.NOMBRE_TABLA_ESTUDIANTES, null, values);
            bd.close();

            Toast.makeText(this, id > 0 ? "Estudiante registrado correctamente":"Error al registrar estudiante", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar estudiante", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarEstudiante() {
        try {
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase bd = helper.getReadableDatabase();
            Cursor cursor = bd.rawQuery("SELECT * FROM estudiantes", null);

            listaEstudiantes = new ArrayList<>();

            while (cursor.moveToNext()) {
                Log.i("Ayuda", cursor.getString(1));
                estudiante = new Estudiante(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2));
                listaEstudiantes.add(estudiante.toString());
            }

            listEstudiante.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaEstudiantes));
        } catch (Exception e) {
            Log.i("Ayuda", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}