package com.example.betterchat;

public class Estado {

    private String imageUrl;

    public Estado() {
        // Constructor vacío requerido para Firestore
    }

    public Estado(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
