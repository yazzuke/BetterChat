package com.example.betterchat;

public class Amigo {
    private String nombre;
    private String fotoPerfil;

    public Amigo(String nombre, String fotoPerfil) {
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }
}
