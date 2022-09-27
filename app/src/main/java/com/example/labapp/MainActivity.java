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

    //Declaración de elementos de interfaz gráfica
    Button btnAgregar;
    EditText etxtNombre;
    ListView listEstudiante;

    //Declaración de objetos a utilizar
    Intent intent;
    Estudiante estudiante;
    ArrayList<String> listaEstudiantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Vinculación de elementos de interfaz gráfica
        btnAgregar = findViewById(R.id.btnAgregarEstudiante);
        etxtNombre = findViewById(R.id.etxtNombre);
        listEstudiante = findViewById(R.id.listEstudiante);

        MainActivity context = this; //Almacena el context de la activity (No sirve de mucho, pero ayuda llamar un intent dentro de un scope local)

        listarEstudiante(); //Carga la lista de estudiantes

        btnAgregar.setOnClickListener(v -> {
            if (!etxtNombre.getText().toString().isEmpty()) {
                agregarEstudiante(); //Agrega estudiante nuevo
                listarEstudiante(); //Refresca la lista de estudiantes
            } else {
                Toast.makeText(context, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        listEstudiante.setOnItemClickListener((adapterView, view, i, l) -> {
            String[] cadenas = listEstudiante.getItemAtPosition(i).toString().split(" - "); //Parte el elemento de la lista cada '-' y lo guarda en un String[]
            int id = Integer.parseInt(cadenas[0]); //Toma solo el id del String[] cadenas
            double promedio = Double.parseDouble(cadenas[1]);  //Toma solo el promedio del String[] cadenas
            String nombre = cadenas[2]; //Toma solo el nombre del String[] cadenas
            intent = new Intent(context, GestionNotas.class);
            intent.putExtra("paramsId", id);
            intent.putExtra("paramsPromedio", promedio);
            intent.putExtra("paramsNombre", nombre);
            startActivity(intent); //Carga actividad GestionNotas
        });
    }

    private void agregarEstudiante() {
        try {
            DbHelper helper = new DbHelper(this); //Helper
            SQLiteDatabase bd = helper.getReadableDatabase(); //Base de datos
            ContentValues values = new ContentValues(); //Valores a insertar
            values.put("NOMBRE", etxtNombre.getText().toString().toUpperCase());
            values.put("PROMEDIO", 0.0);

            long id = bd.insert(Constantes.NOMBRE_TABLA_ESTUDIANTES, null, values);
            bd.close();

            //Si el id > 0, entonces el registro es correcto, de lo contrario, ocurrió un error
            Toast.makeText(this, id > 0 ? "Estudiante registrado correctamente":"Error al registrar estudiante", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar estudiante", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarEstudiante() {
        try {
            DbHelper helper = new DbHelper(this); //Helper
            SQLiteDatabase bd = helper.getReadableDatabase(); //Base de datos
            Cursor cursor = bd.rawQuery("SELECT * FROM estudiantes", null); //Consulta que se almacena en un cursor

            listaEstudiantes = new ArrayList<>();

            //Itera la consulta a través del cursor y crea un objeto nota que mete en el ArrayList listaEstudiantes como String
            while (cursor.moveToNext()) {
                estudiante = new Estudiante(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2));
                listaEstudiantes.add(estudiante.toString());
            }

            //Adapta el listView listEstudiante usando un adapter que toma el ArrayList<> listaEstudiantes
            listEstudiante.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaEstudiantes));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}