package com.example.labapp;

public class Constantes {
    public static final String NOMBRE_TABLA_ESTUDIANTES = "ESTUDIANTES";
    public static final String NOMBRE_TABLA_NOTAS = "NOTAS";
    public static final String NOMBRE_BD = "BD_NOTAS.bd";
    public static final int VERSION_BD = 1;

    public static final String CREAR_TABLA_ESTUDIANTES = "CREATE TABLE ESTUDIANTES (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "NOMBRE TEXT NOT NULL," +
            "PROMEDIO REAL NOT NULL)";

    public static final String CREAR_TABLA_NOTAS = "CREATE TABLE NOTAS (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ID_ESTUDIANTE INTEGER," +
            "NOTA REAL NOT NULL," +
            "FOREIGN KEY (ID_ESTUDIANTE) REFERENCES ESTUDIANTES (ID))";
}
