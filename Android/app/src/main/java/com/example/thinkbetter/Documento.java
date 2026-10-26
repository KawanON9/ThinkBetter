package com.example.thinkbetter;

public class Documento {
    private String titulo;
    private String profissionalEData;
    private String tipo; // "Recibos", "Atestados", "Declarações"

    public Documento(String titulo, String profissionalEData, String tipo) {
        this.titulo = titulo;
        this.profissionalEData = profissionalEData;
        this.tipo = tipo;
    }

    public String getTitulo() { return titulo; }
    public String getProfissionalEData() { return profissionalEData; }
    public String getTipo() { return tipo; }
}