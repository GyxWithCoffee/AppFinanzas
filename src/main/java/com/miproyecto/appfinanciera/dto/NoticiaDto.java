package com.miproyecto.appfinanciera.dto;

public class NoticiaDto {
    private String titulo;
    private String enlace;
    private String imagen;

    public NoticiaDto(String titulo, String enlace, String imagen) {
        this.titulo = titulo;
        this.enlace = enlace;
        this.imagen = imagen;
    }

    // Getters y setters
    public String getTitulo() { return titulo; }
    public String getEnlace() { return enlace; }
    public String getImagen() { return imagen; }
}
