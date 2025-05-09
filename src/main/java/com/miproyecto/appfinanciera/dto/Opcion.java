package com.miproyecto.appfinanciera.dto;

public class Opcion {
    private String texto;
    private int valor;

    public Opcion(String texto, int valor) {
        this.texto = texto;
        this.valor = valor;
    }

    public String getTexto() {
        return texto;
    }

    public int getValor() {
        return valor;
    }
}
