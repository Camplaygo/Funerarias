package com.funerarias;

public class Usuario {
    private String nombreUsuario;
    private String contrasena;
    private boolean esAdmin;

    public Usuario(String nombreUsuario, String contrasena, boolean esAdmin) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.esAdmin = esAdmin;
    }

    // Getters y Setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public boolean esAdmin() {
        return esAdmin;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
