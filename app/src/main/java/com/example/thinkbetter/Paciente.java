package com.example.thinkbetter;

public class Paciente {
    private final long id;          // ID_Paciente no banco (usado para abrir o Prontuário)
    private final String nome;
    private final String detalhes;

    public Paciente(long id, String nome, String detalhes) {
        this.id = id;
        this.nome = nome;
        this.detalhes = detalhes;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDetalhes() {
        return detalhes;
    }
}
