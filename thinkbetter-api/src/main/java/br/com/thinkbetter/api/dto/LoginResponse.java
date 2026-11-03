package br.com.thinkbetter.api.dto;

/**
 * id        = ID_Paciente ou ID_Psicologo (o que as outras telas usam)
 * idUsuario = ID_Usuario (login)
 * tipo      = "PACIENTE" ou "PSICOLOGO": o app usa para escolher a tela inicial
 */
public record LoginResponse(long id, long idUsuario, String tipo, String nome) { }
