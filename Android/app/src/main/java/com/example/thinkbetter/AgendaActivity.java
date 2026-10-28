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
    private AtendimentoAdapter atendimentoAdapter;

    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

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

        // Navegação de meses
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

        if((currentMonth == 11 && direcao == 1) || (currentMonth == 0 && direcao == -1)){
            carregarMesDinamico(currentYear, currentMonth);
        } else{
         currentMonth += direcao;
            carregarMesDinamico(currentYear, currentMonth);
        }
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

        // Dias do mês anterior para completar a grade

        cal.add(Calendar.MONTH, -1);
        int maxDiasMesAnterior = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = primeiroDiaDaSemana - 1; i >= 0; i--) {
            mesDiasList.add(new DiaCalendario(maxDiasMesAnterior - i, mes == 0 ? 11 : mes - 1, mes == 0 ? ano - 1 : ano, false, null));
        }

        // Dias do mês atual
        cal.set(ano, mes, 1);
        for (int i = 1; i <= maxDiasDoMes; i++) {
            List<Atendimento> consultasDoDia = new ArrayList<>();

            // Exemplo dinâmico para testes em Setembro de 2026
            if (mes == Calendar.OCTOBER && ano == 2026) {
                if (i == 9) {
                    consultasDoDia.add(new Atendimento("14:00", "09/09", "Fernanda Lima", "Avaliação · Unimed", "pendente", 0));
                    consultasDoDia.add(new Atendimento("17:00", "09/09", "Luciana Rocha", "Retorno · Particular", "confirmado", 1));
                } else if (i == 10) {
                    consultasDoDia.add(new Atendimento("14:00", "10/09", "Kawan Fulano", "Avaliação · Unimed", "pendente", 0));
                    consultasDoDia.add(new Atendimento("17:00", "10/09", "Luciana do Android", "Retorno · Particular", "confirmado", 1));
                }
            }

            mesDiasList.add(new DiaCalendario(i, mes, ano, true, consultasDoDia));
        }

        //Verificar MÊs que aparece na aba Agenda
        monthGridAdapter = new MonthGridAdapter(mesDiasList, diaSelecionado -> {
            alternarAbas(true);
            tvDiaSelecionadoTitulo.setText("Dia " + diaSelecionado.getDiaDoMes());
            tvDiaSelecionadoSub.setText(nomeMesCapitalizado + " de " + ano);

            atendimentoAdapter = new AtendimentoAdapter(diaSelecionado.getAtendimentos());
            recyclerAtendimentosDia.setAdapter(atendimentoAdapter);
        });

        recyclerMonthGrid.setAdapter(monthGridAdapter);
    }
}