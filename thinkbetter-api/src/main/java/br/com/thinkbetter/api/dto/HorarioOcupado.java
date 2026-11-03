package br.com.thinkbetter.api.dto;

/**
 * Horário que NÃO está livre num dia (GET /psicologos/{id}/horarios).
 * minha = true → é consulta do próprio paciente (aí vêm idConsulta e modalidade).
 * minha = false → ocupado por outra pessoa ou bloqueado: nenhum detalhe é enviado
 *                 ("Horários de outros pacientes são mantidos em sigilo").
 */
public record HorarioOcupado(String hora, boolean minha, Long idConsulta, String modalidade, String status) { }
