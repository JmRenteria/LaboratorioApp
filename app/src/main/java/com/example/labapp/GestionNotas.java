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

    //Declaración de elementos de interfaz gráfica
    Button btnAgregar, btnVolver;
    TextView txtPromedio, txtEstudiante;
    EditText etxtNota;
    ListView listNota;

    //Declaración de objetos a utilizar
    Intent intent;
    Nota nota;
    ArrayList<String> listaNotas;

    //Declaración de variables útiles
    int id_estudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_notas);

        //Vinculación de elementos de interfaz gráfica
        btnAgregar = findViewById(R.id.btnAgregarNota);
        btnVolver = findViewById(R.id.btnVolver);
        txtPromedio = findViewById(R.id.txtPromedio);
        txtEstudiante = findViewById(R.id.txtEstudiante);
        etxtNota = findViewById(R.id.etxtNota);
        listNota = findViewById(R.id.listNota);

        //Se recogen los parametros enviados en el Intent
        id_estudiante = getIntent().getExtras().getInt("paramsId");
        txtPromedio.setText(String.valueOf(getIntent().getExtras().getDouble("paramsPromedio")));
        txtEstudiante.setText(getIntent().getExtras().getString("paramsNombre"));

        GestionNotas context = this; //Almacena el context de la activity (No sirve de mucho, pero ayuda llamar un intent dentro de un scope local)

        listarNota(); //Carga la lista de notas

        btnAgregar.setOnClickListener(v -> {
            if (!etxtNota.toString().isEmpty()) {
                if (Double.parseDouble(etxtNota.getText().toString()) <= 5.0) {
                    agregarNota(); //Agrega nota nueva
                    actualizarPromedio(); //Calcula y actualiza el promedio del estudiante
                    listarNota(); //Refresa la lista de notas
                }
            }
            Toast.makeText(context, "Ingrese una nota entre 0.0 y 5.0", Toast.LENGTH_SHORT).show();
        });

        btnVolver.setOnClickListener(v -> {
            intent = new Intent(context, MainActivity.class);
            startActivity(intent); //Carga actividad MainActivity
        });
    }

    private void agregarNota() {
        try {
            DbHelper helper = new DbHelper(this); //Helper
            SQLiteDatabase bd = helper.getReadableDatabase(); //Base de datos
            ContentValues values = new ContentValues(); //Valores a insertar
            values.put("ID_ESTUDIANTE", id_estudiante);
            values.put("NOTA", etxtNota.getText().toString());

            long id = bd.insert(Constantes.NOMBRE_TABLA_NOTAS, null, values);
            bd.close();

            //Si el id > 0, entonces el registro es correcto, de lo contrario, ocurrió un error
            Toast.makeText(this, id > 0 ? "Nota registrada correctamente":"Error al registrar nota", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar nota", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarNota() {
        try {
            DbHelper helper = new DbHelper(this); //Helper
            SQLiteDatabase bd = helper.getReadableDatabase(); //Base de datos
            Cursor cursor = bd.rawQuery("SELECT * FROM notas WHERE id_estudiante = " + id_estudiante, null); //Consulta que se almacena en un cursor

            listaNotas = new ArrayList<>();

            //Itera la consulta a través del cursor y crea un objeto nota que mete en el ArrayList listaNotas como String
            while (cursor.moveToNext()) {
                nota = new Nota(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2));
                listaNotas.add(nota.toString());
            }

            //Adapta el listView listNota usando un adapter que toma el ArrayList<> listaNotas
            listNota.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaNotas));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPromedio() {
        try {
            DbHelper helper = new DbHelper(this); //Helper
            SQLiteDatabase bd = helper.getReadableDatabase(); //Base de datos
            Cursor cursor = bd.rawQuery("SELECT COUNT(nota), SUM(nota) FROM notas WHERE id_estudiante = " + id_estudiante, null); //Consulta que trae la cantidad de notas y la suma de las notas y se almacena en un cursor
            cursor.moveToFirst();
            double promedio = Double.parseDouble(cursor.getString(1)) / Double.parseDouble(cursor.getString(0)); //Calcula el promedio
            txtPromedio.setText(String.format("%,.2f", promedio)); //Muestra el promedio en el txtPromedio

            cursor = bd.rawQuery("SELECT * FROM estudiantes WHERE id = " + id_estudiante, null); //Consulta que se almacena en un cursor
            cursor.moveToFirst();
            ContentValues values = new ContentValues(); //Valores a actualizar
            values.put("ID", cursor.getInt(0));
            values.put("NOMBRE", cursor.getString(1));
            values.put("PROMEDIO", promedio);

            //Actualiza el promedio del estudiante en la base de datos
            bd.update(Constantes.NOMBRE_TABLA_ESTUDIANTES, values, "id=" + id_estudiante, null);
            bd.close();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}