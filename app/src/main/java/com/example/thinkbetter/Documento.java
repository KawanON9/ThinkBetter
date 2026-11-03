package com.example.thinkbetter;

public class Documento {
    private String titulo;
    private String detalhes;
    private String tipo; // "Recibos", "Atestados", "Declarações", "Laudos"
    private String texto; // texto completo do documento (vem da API); pode ser null

    public Documento(String titulo, String detalhes, String tipo) {
        this.titulo = titulo;
        this.detalhes = detalhes;
        this.tipo = tipo;
    }

    /** Documento vindo do banco, com o texto completo para "Baixar". */
    public Documento(String titulo, String detalhes, String tipo, String texto) {
        this(titulo, detalhes, tipo);
        this.texto = texto;
    }

    public String getTexto() { return texto; }
    public String getTitulo() { return titulo; }
    public String getDetalhes() { return detalhes; }
    public String getTipo() { return tipo; }
}
