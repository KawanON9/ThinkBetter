package br.com.thinkbetter.api.dto;

/** Item da lista de pacientes (tela Pacientes do psicólogo). */
public record PacienteResumo(long id, String nome, String sobrenome, String telefone, String plano) { }
