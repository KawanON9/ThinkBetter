package br.com.thinkbetter.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/** Corpo do POST /consultas (botão "Confirmar" do Agendar Consulta). */
public record NovaConsultaRequest(
        @NotNull(message = "Paciente não informado") Long idPaciente,
        @NotNull(message = "Psicólogo não informado") Long idPsicologo,
        @NotNull(message = "Horário não informado") LocalDateTime inicio,
        @NotNull(message = "Informe a modalidade")
        @Pattern(regexp = "Presencial|Online", message = "Modalidade inválida") String modalidade
) { }
