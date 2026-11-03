package com.example.thinkbetter.api;

/**
 * Item do GET /psicologos/{id}/horarios: um horário do dia que NÃO está livre.
 * minha = true  → consulta do paciente logado (vêm idConsulta, modalidade e status)
 * minha = false → ocupado por outra pessoa ou bloqueado (sem detalhes, por sigilo)
 */
public class HorarioOcupado {
    public String hora;          // "14:00"
    public boolean minha;
    public Long idConsulta;
    public String modalidade;
    public String status;
}
