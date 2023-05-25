package com.example.betterchat;

public class Usuario {
    private String username;
    private String displaymame;

    public Usuario() {
        // Constructor vacío necesario para Firebase
    }

    public Usuario(String username, String displayName) {
        this.username = username;
        this.displaymame = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getdisplaymame() {
        return displaymame;
    }
}
