package com.example.labapp;

public class Nota {
    int id, id_estudiante;
    double nota;

    @Override
    public String toString() {
        return String.valueOf(nota);
    }

    public Nota(int id, int id_estudiante, double nota) {
        this.id = id;
        this.id_estudiante = id_estudiante;
        this.nota = nota;
    }
}
