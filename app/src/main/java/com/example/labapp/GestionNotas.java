package com.example.labapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GestionNotas extends AppCompatActivity {

    Button btnAgregar, btnVolver;
    TextView txtPromedio, txtEstudiante;
    EditText etxtNota;
    Intent intent;
    ListView listNota;

    int id_estudiante;
    Nota nota;
    ArrayList<String> listaNotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_notas);

        txtPromedio = findViewById(R.id.txtPromedio);
        txtEstudiante = findViewById(R.id.txtEstudiante);
        btnAgregar = findViewById(R.id.btnAgregarNota);
        btnVolver = findViewById(R.id.btnVolver);
        etxtNota = findViewById(R.id.etxtNota);
        listNota = findViewById(R.id.listNota);

        id_estudiante = getIntent().getExtras().getInt("paramsId");
        txtPromedio.setText(String.valueOf(getIntent().getExtras().getDouble("paramsPromedio")));
        txtEstudiante.setText(getIntent().getExtras().getString("paramsNombre"));

        GestionNotas context = this; //Perdón

        listarNota();

        btnAgregar.setOnClickListener(v -> {
            if (!etxtNota.toString().isEmpty()) {
                if (Double.parseDouble(etxtNota.getText().toString()) <= 5.0) {
                    agregarNota();
                    actualizarPromedio();
                    listarNota();
                }
            }
            Toast.makeText(context, "Ingrese una nota entre 0.0 y 5.0", Toast.LENGTH_SHORT).show();
        });

        btnVolver.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });
    }

    private void agregarNota() {
        try {
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase bd = helper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID_ESTUDIANTE", id_estudiante);
            values.put("NOTA", etxtNota.getText().toString());

            long id = bd.insert(Constantes.NOMBRE_TABLA_NOTAS, null, values);
            bd.close();

            Toast.makeText(this, id > 0 ? "Nota registrada correctamente":"Error al registrar nota", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar nota", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarNota() {
        try {
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase bd = helper.getReadableDatabase();
            Cursor cursor = bd.rawQuery("SELECT * FROM notas WHERE id_estudiante = " + id_estudiante, null);

            listaNotas = new ArrayList<>();

            while (cursor.moveToNext()) {
                //Log.i("Ayuda", cursor.getString(0));
                nota = new Nota(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2));
                listaNotas.add(nota.toString());
            }

            listNota.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaNotas));
        } catch (Exception e) {
            Log.i("Ayuda", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPromedio() {
        try {
            DbHelper helper = new DbHelper(this);
            SQLiteDatabase bd = helper.getReadableDatabase();
            Cursor cursor = bd.rawQuery("SELECT COUNT(nota), SUM(nota) FROM notas WHERE id_estudiante = " + id_estudiante, null);
            cursor.moveToFirst();
            double promedio = Double.parseDouble(cursor.getString(1)) / Double.parseDouble(cursor.getString(0));
            txtPromedio.setText(String.format("%,.2f", promedio));

            cursor = bd.rawQuery("SELECT * FROM estudiantes WHERE id = " + id_estudiante, null);
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            values.put("ID", cursor.getInt(0));
            values.put("NOMBRE", cursor.getString(1));
            values.put("PROMEDIO", promedio);

            bd.update(Constantes.NOMBRE_TABLA_ESTUDIANTES, values, "id=" + id_estudiante, null);
            bd.close();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}