package com.example.betterchat;


public class SolicitudAmistad {
    private String usuarioEnviadorId;
    private String usuarioReceptorId;
    private String usuarioEnviadorNombre;
    private String usuarioEnviadorFotoPerfil;
    private String usuarioEnviadorUsername;

    public SolicitudAmistad() {
        // Constructor vacío requerido para Firebase
    }

    public SolicitudAmistad(String usuarioEnviadorId, String usuarioReceptorId, String usuarioEnviadorNombre, String usuarioEnviadorFotoPerfil, String usuarioEnviadorUsername) {
        this.usuarioEnviadorId = usuarioEnviadorId;
        this.usuarioReceptorId = usuarioReceptorId;
        this.usuarioEnviadorNombre = usuarioEnviadorNombre;
        this.usuarioEnviadorFotoPerfil = usuarioEnviadorFotoPerfil;
        this.usuarioEnviadorUsername = usuarioEnviadorUsername;
    }

    public String getUsuarioEnviadorId() {
        return usuarioEnviadorId;
    }

    public void setUsuarioEnviadorId(String usuarioEnviadorId) {
        this.usuarioEnviadorId = usuarioEnviadorId;
    }

    public String getUsuarioReceptorId() {
        return usuarioReceptorId;
    }

    public void setUsuarioReceptorId(String usuarioReceptorId) {
        this.usuarioReceptorId = usuarioReceptorId;
    }

    public String getUsuarioEnviadorNombre() {
        return usuarioEnviadorNombre;
    }

    public void setUsuarioEnviadorNombre(String usuarioEnviadorNombre) {
        this.usuarioEnviadorNombre = usuarioEnviadorNombre;
    }

    public String getUsuarioEnviadorFotoPerfil() {
        return usuarioEnviadorFotoPerfil;
    }

    public void setUsuarioEnviadorFotoPerfil(String usuarioEnviadorFotoPerfil) {
        this.usuarioEnviadorFotoPerfil = usuarioEnviadorFotoPerfil;
    }

    public String getUsuarioEnviadorUsername() {
        return usuarioEnviadorUsername;
    }

    public void setUsuarioEnviadorUsername(String usuarioEnviadorUsername) {
        this.usuarioEnviadorUsername = usuarioEnviadorUsername;
    }
}
