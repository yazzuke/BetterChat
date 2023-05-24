package com.example.betterchat;
public class Estado {
    private String texto;
    private String imageUrl;
    private String userId;
    private long timestamp;

    public Estado() {
        // Constructor vacío requerido para Firestore
    }

    public Estado(String texto, String imageUrl, String userId, long timestamp) {
        this.texto = texto;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
