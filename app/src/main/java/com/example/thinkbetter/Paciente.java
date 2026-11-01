package com.example.thinkbetter;

public class Paciente {
    private String nome;
    private String detalhes;

    public Paciente(String nome, String detalhes) {
        this.nome = nome;
        this.detalhes = detalhes;
    }

    public String getNome() {
        return nome;
    }

    public String getDetalhes() {
        return detalhes;
    }
}