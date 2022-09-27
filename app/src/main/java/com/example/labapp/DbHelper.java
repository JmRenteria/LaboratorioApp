package com.example.labapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context) {
        super(context, Constantes.NOMBRE_BD, null, Constantes.VERSION_BD);
    }

    @Override //Creación de tablas
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constantes.CREAR_TABLA_ESTUDIANTES);
        sqLiteDatabase.execSQL(Constantes.CREAR_TABLA_NOTAS);
    }

    @Override //Eliminar las tablas si se cambia el archivo .bd
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constantes.NOMBRE_TABLA_ESTUDIANTES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constantes.NOMBRE_TABLA_NOTAS);
    }
}