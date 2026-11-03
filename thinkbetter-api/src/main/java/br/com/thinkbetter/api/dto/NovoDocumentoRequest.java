package br.com.thinkbetter.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corpo do POST /documentos e do POST /documentos/previa (Emissão de Documentos).
 * tipo: Atestado | Declaracao | Laudo
 * periodoInicio / periodoFim: opcionais ("2026-09-10")
 * cid: opcional ("F41.1"); não é aceito em Declaração
 */
public record NovoDocumentoRequest(
        @NotNull(message = "Psicólogo não informado") Long idPsicologo,
        @NotNull(message = "Selecione o paciente") Long idPaciente,
        @NotNull(message = "Selecione o tipo de documento")
        @Pattern(regexp = "Atestado|Declaracao|Laudo", message = "Tipo de documento inválido") String tipo,
        LocalDate periodoInicio,
        LocalDate periodoFim,
        @Pattern(regexp = "|[A-Za-z][0-9]{2}(\\.[0-9]{1,2})?", message = "CID inválido (ex.: F41.1)") String cid,
        @Size(max = 2000, message = "A descrição pode ter no máximo 2000 caracteres") String descricao
) { }
