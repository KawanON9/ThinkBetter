package br.com.thinkbetter.api.dto;

import java.time.LocalDateTime;

/**
 * Uma consulta em "Meus Próximos Atendimentos" do paciente.
 * status: Pendente | Confirmada · linkOnline: null se não houver
 */
public record ConsultaPaciente(
        long idConsulta,
        LocalDateTime inicio,
        String psicologo,
        String abordagem,
        String modalidade,
        String status,
        String linkOnline
) { }
