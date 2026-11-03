package br.com.thinkbetter.api.dto;

/**
 * Uma sessão da agenda do psicólogo.
 * data: "2026-11-03" · hora: "14:00" · status: Pendente | Confirmada | Realizada
 * tipo: Avaliacao | Sessao | Retorno · modalidade: Presencial | Online
 */
public record SessaoAgenda(
        long idConsulta,
        String data,
        String hora,
        long idPaciente,
        String nomePaciente,
        String plano,
        String tipo,
        String modalidade,
        String status
) { }
