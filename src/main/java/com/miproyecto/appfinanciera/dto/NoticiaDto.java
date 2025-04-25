package com.miproyecto.appfinanciera.dto;

public class NoticiaDto {

    private String titulo;
    private String enlace;

    public NoticiaDto(String titulo, String enlace) {
        this.titulo = titulo;
        this.enlace = enlace;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEnlace() {
        return enlace;
    }
}
