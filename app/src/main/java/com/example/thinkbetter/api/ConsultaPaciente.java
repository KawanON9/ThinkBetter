package com.example.thinkbetter.api;

/** Item do GET /pacientes/{id}/consultas ("Meus Próximos Atendimentos"). */
public class ConsultaPaciente {
    public long idConsulta;
    public String inicio;        // "2026-11-04T10:00:00"
    public String psicologo;     // "Ana Souza"
    public String abordagem;     // pode ser null
    public String modalidade;    // Presencial | Online
    public String status;        // Pendente | Confirmada
    public String linkOnline;    // pode ser null
}
