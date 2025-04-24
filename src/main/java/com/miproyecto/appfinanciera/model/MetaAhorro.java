package com.miproyecto.appfinanciera.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class MetaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion; // El sueño

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDate fechaCreacion = LocalDate.now();// Fecha inicial del plan
    @Column(nullable = true)
    private String categoria;

    @Column(nullable = true)
    private Long monto;

    private LocalDate fechaFinal;
    private String frecuencia;
    // "Diario", "Semanal", "Mensual"
    @Column(nullable = true)
    private Long abonado = 0L;

    public Long getAbonado() {
        return abonado;
    }

    public void setAbonado(Long abonado) {
        this.abonado = abonado;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Long getMonto() { return monto; }
    public void setMonto(Long monto) { this.monto = monto; }

    public LocalDate getFechaFinal() { return fechaFinal; }
    public void setFechaFinal(LocalDate fechaFinal) { this.fechaFinal = fechaFinal; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }
}