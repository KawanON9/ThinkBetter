package com.example.thinkbetter;

public class Documento {
    private String titulo;
    private String detalhes;
    private String tipo; // "Recibos", "Atestados", "Declarações"

    public Documento(String titulo, String detalhes, String tipo) {
        this.titulo = titulo;
        this.detalhes = detalhes;
        this.tipo = tipo;
    }

    public String getTitulo() { return titulo; }
    public String getDetalhes() { return detalhes; }
    public String getTipo() { return tipo; }
}
