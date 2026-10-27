package com.example.thinkbetter;

import java.util.Calendar;
import java.util.List;

public class DiaCalendario {
    private int diaDoMes;
    private int mes;
    private int ano;
    private boolean eDoMesAtual;
    private boolean selecionado;
    private List<Atendimento> atendimentos;

    public DiaCalendario(int diaDoMes, int mes, int ano, boolean eDoMesAtual, List<Atendimento> atendimentos) {
        this.diaDoMes = diaDoMes;
        this.mes = mes;
        this.ano = ano;
        this.eDoMesAtual = eDoMesAtual;
        this.atendimentos = atendimentos;
        this.selecionado = false;
    }

    public int getDiaDoMes() { return diaDoMes; }
    public boolean isEDoMesAtual() { return eDoMesAtual; }
    public boolean isSelecionado() { return selecionado; }
    public void setSelecionado(boolean selecionado) { this.selecionado = selecionado; }
    public List<Atendimento> getAtendimentos() { return atendimentos; }
}