package com.example.thinkbetter.api;

/** Item do GET /psicologos/{id}/agenda (tela inicial, Atendimentos e Agenda do mês). */
public class SessaoAgenda {
    public long idConsulta;
    public String data;          // "2026-11-03"
    public String hora;          // "14:00"
    public long idPaciente;
    public String nomePaciente;
    public String plano;         // "Particular" quando não tem plano
    public String tipo;          // Avaliacao | Sessao | Retorno
    public String modalidade;    // Presencial | Online
    public String status;        // Pendente | Confirmada | Realizada
}
