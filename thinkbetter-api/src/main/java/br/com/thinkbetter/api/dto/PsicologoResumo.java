package br.com.thinkbetter.api.dto;

/** Item do GET /psicologos (cards "Escolha o profissional"). */
public record PsicologoResumo(long id, String nome, String sobrenome, String abordagem) { }
