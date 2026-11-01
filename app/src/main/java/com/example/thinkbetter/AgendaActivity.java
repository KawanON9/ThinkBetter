package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AgendaActivity extends MenuJava {

    private Button btnTabAgenda, btnTabMes;
    private LinearLayout containerMes, containerAgendaDias;
    private RecyclerView recyclerMonthGrid, recyclerAtendimentosDia;
    private TextView tvMesAno, tvDiaSelecionadoTitulo, tvDiaSelecionadoSub;
    private ImageButton btnMesAnterior, btnMesProximo;

    private List<DiaCalendario> mesDiasList;
    private MonthGridAdapter monthGridAdapter;

    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        // Inicializa o menu lateral do psicólogo
        setupMenuPsicologoButton(R.id.btnMenu);

        btnTabAgenda = findViewById(R.id.btnTabAgenda);
        btnTabMes = findViewById(R.id.btnTabMes);
        containerMes = findViewById(R.id.containerMes);
        containerAgendaDias = findViewById(R.id.containerAgendaDias);
        recyclerMonthGrid = findViewById(R.id.recyclerMonthGrid);
        recyclerAtendimentosDia = findViewById(R.id.recyclerAtendimentosDia);
        tvMesAno = findViewById(R.id.tvMesAno);
        tvDiaSelecionadoTitulo = findViewById(R.id.tvDiaSelecionadoTitulo);
        tvDiaSelecionadoSub = findViewById(R.id.tvDiaSelecionadoSub);

        btnMesAnterior = findViewById(R.id.btnMesAnterior);
        btnMesProximo = findViewById(R.id.btnMesProximo);

        // Inicializa utilizando as funções criadas em DiaCalendario
        currentMonth = DiaCalendario.getMesAtual();
        currentYear = DiaCalendario.getAnoAtual();

        // Configuração das Abas (Alternância Mês vs Agenda)
        btnTabAgenda.setOnClickListener(v -> alternarAbas(true));
        btnTabMes.setOnClickListener(v -> alternarAbas(false));

        // Navegação entre os meses
        if (btnMesAnterior != null) {
            btnMesAnterior.setOnClickListener(v -> mudarMes(-1));
        }
        if (btnMesProximo != null) {
            btnMesProximo.setOnClickListener(v -> mudarMes(1));
        }

        // Inicializar Calendário Mensal Dinâmico
        recyclerMonthGrid.setLayoutManager(new GridLayoutManager(this, 7));
        carregarMesDinamico(currentYear, currentMonth);

        // Configurar lista de atendimentos do dia
        recyclerAtendimentosDia.setLayoutManager(new LinearLayoutManager(this));
    }

    private void alternarAbas(boolean isAgenda) {
        if (isAgenda) {
            btnTabAgenda.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
            btnTabMes.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            containerAgendaDias.setVisibility(View.VISIBLE);
            containerMes.setVisibility(View.GONE);
        } else {
            btnTabMes.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
            btnTabAgenda.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            containerMes.setVisibility(View.VISIBLE);
            containerAgendaDias.setVisibility(View.GONE);
        }
    }

    private void mudarMes(int direcao) {
        currentMonth += direcao;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        } else if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        carregarMesDinamico(currentYear, currentMonth);
    }

    private void carregarMesDinamico(int ano, int mes) {
        mesDiasList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(ano, mes, 1);

        String nomeMes = new DateFormatSymbols(new Locale("pt", "BR")).getMonths()[mes];
        String nomeMesCapitalizado = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);
        tvMesAno.setText(nomeMesCapitalizado + " " + ano);

        int primeiroDiaDaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDiasDoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Preencher dias do mês anterior para ajuste da grade
        cal.add(Calendar.MONTH, -1);
        int maxDiasMesAnterior = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = primeiroDiaDaSemana - 1; i >= 0; i--) {
            mesDiasList.add(new DiaCalendario(maxDiasMesAnterior - i, mes == 0 ? 11 : mes - 1, mes == 0 ? ano - 1 : ano, false, null));
        }

        // Preencher os dias do mês atual e gerar horários das 14:00 às 18:00
        cal.set(ano, mes, 1);
        for (int i = 1; i <= maxDiasDoMes; i++) {
            List<Atendimento> consultasDoDia = new ArrayList<>();

            // Horários fixos solicitados entre 14:00 e 18:00
            String[] horariosFixos = {"14:00", "15:00", "16:00", "17:00", "18:00"};
            String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d", i, (mes + 1));

            for (String hora : horariosFixos) {
                String nomePaciente = "Livre";
                String detalhes = "Horário disponível";
                String status = "livre";
                int tipoStatus = 2; // Tipo padrão para horários livres (teal claro)

                // Exemplo mockado de pacientes agendados em Novembro de 2026
                if (mes == Calendar.NOVEMBER && ano == 2026) {
                    if (i == 1) {
                        if (hora.equals("14:00")) {
                            nomePaciente = "Fernanda Lima";
                            detalhes = "Avaliação · Unimed";
                            status = "pendente";
                            tipoStatus = 0; // Pendente
                        } else if (hora.equals("17:00")) {
                            nomePaciente = "Luciana Rocha";
                            detalhes = "Retorno · Particular";
                            status = "confirmado";
                            tipoStatus = 1; // Confirmado
                        }
                    } else if (i == 10) {
                        if (hora.equals("14:00")) {
                            nomePaciente = "Kawan Fulano";
                            detalhes = "Avaliação · Unimed";
                            status = "pendente";
                            tipoStatus = 0;
                        } else if (hora.equals("17:00")) {
                            nomePaciente = "Luciana do Android";
                            detalhes = "Retorno · Particular";
                            status = "confirmado";
                            tipoStatus = 1;
                        }
                    }
                }

                consultasDoDia.add(new Atendimento(hora, dataFormatada, nomePaciente, detalhes, status, tipoStatus));
            }

            mesDiasList.add(new DiaCalendario(i, mes, ano, true, consultasDoDia));
        }

        // Configuração do clique nos dias do calendário mensal
        monthGridAdapter = new MonthGridAdapter(mesDiasList, diaSelecionado -> {
            alternarAbas(true); // Alterna para a aba Agenda/Dias
            tvDiaSelecionadoTitulo.setText("Dia " + diaSelecionado.getDiaDoMes());
            tvDiaSelecionadoSub.setText(nomeMesCapitalizado + " de " + ano);

            // Usa o adaptador específico da agenda para exibir o botão de Indisponibilizar
            AgendaAtendimentoAdapter agendaAdapter = new AgendaAtendimentoAdapter(diaSelecionado.getAtendimentos());
            recyclerAtendimentosDia.setAdapter(agendaAdapter);
        });

        // ATENÇÃO: Esta linha é obrigatória para popular os dias na tela!
        recyclerMonthGrid.setAdapter(monthGridAdapter);
    }
}
