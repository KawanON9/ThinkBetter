package com.example.thinkbetter;

public class Atendimento {
    private String hora;
    private String data;
    private String nomePaciente;
    private String detalhes;
    private String status;
    private int tipoStatus; // 0: pendente, 1: confirmado, 2: realizado, 3: indisponível/livre
    private long idConsulta; // ID_Consulta no banco (0 = horário livre, sem consulta)

    public Atendimento(String hora, String data, String nomePaciente, String detalhes, String status, int tipoStatus) {
        this.hora = hora;
        this.data = data;
        this.nomePaciente = nomePaciente;
        this.detalhes = detalhes;
        this.status = status;
        this.tipoStatus = tipoStatus;
    }

    /** Atendimento vindo do banco: guarda o ID da consulta para confirmar pela API. */
    public Atendimento(long idConsulta, String hora, String data, String nomePaciente, String detalhes, String status, int tipoStatus) {
        this(hora, data, nomePaciente, detalhes, status, tipoStatus);
        this.idConsulta = idConsulta;
    }

    public long getIdConsulta() { return idConsulta; }
    public String getHora() { return hora; }
    public String getData() { return data; }
    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }
    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTipoStatus() { return tipoStatus; }
    public void setTipoStatus(int tipoStatus) { this.tipoStatus = tipoStatus; }
}
