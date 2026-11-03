package com.example.thinkbetter.api;

/** Item da lista GET /pacientes. */
public class PacienteResumo {
    public long id;
    public String nome;
    public String sobrenome;
    public String telefone;
    public String plano;   // "Particular" quando não tem plano
}
