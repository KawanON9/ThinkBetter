package com.example.thinkbetter;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgendaActivity extends MenuJava {

    private Button btnTabAgenda, btnTabMes;
    private LinearLayout containerMes, containerAgendaDias;
    private RecyclerView recyclerMonthGrid, recyclerAtendimentosDia;
    private TextView tvMesAno, tvDiaSelecionadoTitulo, tvDiaSelecionadoSub;

    private List<DiaCalendario> mesDiasList;
    private MonthGridAdapter monthGridAdapter;
    private AtendimentoAdapter atendimentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        //Abrir menu lateral do Psicologo
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

        // Configuração das Abas (Alternância Mês vs Agenda)
        btnTabAgenda.setOnClickListener(v -> alternarAbas(true));
        btnTabMes.setOnClickListener(v -> alternarAbas(false));

        // Inicializar Calendário Mensal Dinâmico
        recyclerMonthGrid.setLayoutManager(new GridLayoutManager(this, 7));
        carregarMesDinamico(2026, Calendar.SEPTEMBER);

        // Configurar lista de atendimentos do dia
        recyclerAtendimentosDia.setLayoutManager(new LinearLayoutManager(this));
    }

    private void alternarAbas(boolean isAgenda) {
        if (isAgenda) {
            btnTabAgenda.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark)); // Ou cor teal do projeto
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

    private void carregarMesDinamico(int ano, int mes) {
        mesDiasList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(ano, mes, 1);

        int primeiroDiaDaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Domingo ou ajuste para Seg
        int maxDiasDoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Preencher dias do mês anterior para ajuste da grelha
        cal.add(Calendar.MONTH, -1);
        int maxDiasMesAnterior = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = primeiroDiaDaSemana - 1; i >= 0; i--) {
            mesDiasList.add(new DiaCalendario(maxDiasMesAnterior - i, mes - 1, ano, false, null));
        }

        // Preencher dias do mês atual
        cal.set(ano, mes, 1);
        for (int i = 1; i <= maxDiasDoMes; i++) {
            List<Atendimento> consultasDoDia = new ArrayList<>();
            
            // Exemplo dinâmico baseado na imagem de referência para o dia 9 de Setembro de 2026[cite: 20]
            if (i == 9) {
                consultasDoDia.add(new Atendimento("14:00", "09/09", "Fernanda Lima", "Avaliação · Unimed", "pendente", 0));
                consultasDoDia.add(new Atendimento("17:00", "09/09", "Luciana Rocha", "Retorno · Particular", "confirmado", 1));
            }

            mesDiasList.add(new DiaCalendario(i, mes, ano, true, consultasDoDia));
        }

        monthGridAdapter = new MonthGridAdapter(mesDiasList, diaSelecionado -> {
            // Ação ao clicar num dia específico no modo Mês:
            // Muda para a visão de Agenda e carrega os dados daquele dia instantaneamente
            alternarAbas(true);
            tvDiaSelecionadoTitulo.setText("Dia " + diaSelecionado.getDiaDoMes());
            tvDiaSelecionadoSub.setText("Setembro de 2026");

            atendimentoAdapter = new AtendimentoAdapter(diaSelecionado.getAtendimentos());
            recyclerAtendimentosDia.setAdapter(atendimentoAdapter);
        });

        recyclerMonthGrid.setAdapter(monthGridAdapter);
    }
}