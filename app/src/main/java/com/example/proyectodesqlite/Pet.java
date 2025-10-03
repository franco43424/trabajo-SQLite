package com.example.proyectodesqlite;

public class Pet {

    private int id;
    private String nombre;
    private int idDueno;
    private int idRaza;

    public Pet(int id, String nombre, int idDueno, int idRaza) {
        this.id = id;
        this.nombre = nombre;
        this.idDueno = idDueno;
        this.idRaza = idRaza;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    public int getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(int idRaza) {
        this.idRaza = idRaza;
    }
}