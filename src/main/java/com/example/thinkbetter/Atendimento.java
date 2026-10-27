package com.example.thinkbetter;

public class Atendimento {
    private String hora;
    private String data;
    private String nomePaciente;
    private String detalhes;
    private String status;
    private int tipoStatus; // 0: pendente, 1: confirmado, 2: realizado

    public Atendimento(String hora, String data, String nomePaciente, String detalhes, String status, int tipoStatus) {
        this.hora = hora;
        this.data = data;
        this.nomePaciente = nomePaciente;
        this.detalhes = detalhes;
        this.status = status;
        this.tipoStatus = tipoStatus;
    }

    public String getHora() { return hora; }
    public String getData() { return data; }
    public String getNomePaciente() { return nomePaciente; }
    public String getDetalhes() { return detalhes; }
    public String getStatus() { return status; }
    public int getTipoStatus() { return tipoStatus; }
}