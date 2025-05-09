package com.miproyecto.appfinanciera.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoria;
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String contenidoIntro; // Texto educativo
    @OneToMany(mappedBy = "leccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pregunta> preguntas;
    private Integer orden;
    @Column(name = "video_url")
    private String videoUrl;


    // Constructor por defecto
    public Leccion() {}

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenidoIntro() {
        return contenidoIntro;
    }

    public void setContenidoIntro(String contenidoIntro) {
        this.contenidoIntro = contenidoIntro;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
