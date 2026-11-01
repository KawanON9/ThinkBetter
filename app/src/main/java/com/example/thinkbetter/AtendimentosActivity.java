package com.example.thinkbetter;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AtendimentosActivity extends MenuJava {

    private RecyclerView recyclerView;
    private AtendimentoAdapter adapter;
    private List<Atendimento> atendimentoList;
    private TextView tvSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendimentos);

        setupMenuPsicologoButton(R.id.btnMenu);

        recyclerView = findViewById(R.id.recyclerViewAtendimentos);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtém a data atual do sistema formatada (ex: "01/11")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", new Locale("pt", "BR"));
        String dataAtual = sdf.format(Calendar.getInstance().getTime());

        // Populando a lista geral (com atendimentos simulados para a data de hoje para aparecerem imediatamente)
        List<Atendimento> todosAtendimentos = new ArrayList<>();
        todosAtendimentos.add(new Atendimento("14:00", dataAtual, "Fernanda Lima", "Avaliação · Unimed", "pendente", 0));
        todosAtendimentos.add(new Atendimento("17:00", dataAtual, "Luciana Rocha", "Retorno · Particular", "confirmado", 1));
        todosAtendimentos.add(new Atendimento("14:00", "08/09", "Outro Paciente", "Consulta Antiga", "realizado", 2));

        // Filtra estritamente para o dia atual
        atendimentoList = new ArrayList<>();
        for (Atendimento item : todosAtendimentos) {
            if (item.getData().equals(dataAtual)) {
                atendimentoList.add(item);
            }
        }

        tvSubtitle.setText(atendimentoList.size() + " no total para hoje");

        adapter = new AtendimentoAdapter(atendimentoList);
        recyclerView.setAdapter(adapter);
    }
}
