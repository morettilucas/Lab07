package com.lfmoretti.laboratorio07.Modelo;

import android.widget.ImageView;

import java.io.Serializable;

public class Reclamo implements Serializable {
    private Double latitud;
    private Double longitud;
    private String titulo;
    private String telefono;
    private String email;
    // lo usaremos en el punto 7
    private String imagenPath;

    public Reclamo() {
    }

    public Double getLatitud() {
        return latitud;
    }

    public Reclamo setLatitud(Double latitud) {
        this.latitud = latitud;
        return this;
    }

    public Double getLongitud() {
        return longitud;
    }

    public Reclamo setLongitud(Double longitud) {
        this.longitud = longitud;
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public Reclamo setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public String getTelefono() {
        return telefono;
    }

    public Reclamo setTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Reclamo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public Reclamo setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
        return this;
    }
}