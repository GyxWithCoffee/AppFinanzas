package com.miproyecto.appfinanciera.dto;

public class NoticiaDto {
    private String titulo;
    private String enlace;
    private String imagenUrl;

    public NoticiaDto(String titulo, String enlace, String imagenUrl) {
        this.titulo = titulo;
        this.enlace = enlace;
        this.imagenUrl = imagenUrl;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEnlace() { return enlace; }
    public void setEnlace(String enlace) { this.enlace = enlace; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
