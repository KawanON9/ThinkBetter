package com.example.thinkbetter.api;

/** Corpo do POST /consultas. inicio: "2026-11-05T14:00:00" · modalidade: Presencial | Online */
public class NovaConsultaRequest {
    public long idPaciente;
    public long idPsicologo;
    public String inicio;
    public String modalidade;

    public NovaConsultaRequest(long idPaciente, long idPsicologo, String inicio, String modalidade) {
        this.idPaciente = idPaciente;
        this.idPsicologo = idPsicologo;
        this.inicio = inicio;
        this.modalidade = modalidade;
    }
}
