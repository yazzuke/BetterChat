package com.example.betterchat;

public class Usuario {
    private String username;
    private String displayname;

    public Usuario() {
        // Constructor vacío necesario para Firebase
    }

    public Usuario(String username, String displayname) {
        this.username = username;
        this.displayname = displayname;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayname() {
        return displayname;
    }
}
