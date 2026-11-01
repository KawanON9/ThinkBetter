package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends MenuJava {

    private TextView tvDate;
    private Button btnVerAgendaCompleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout principal da Home
        setContentView(R.layout.activity_main);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        // Inicializa os elementos da interface
        tvDate = findViewById(R.id.tvDate);
        btnVerAgendaCompleta = findViewById(R.id.btnVerAgendaCompleta);

        // Exibe o dia atual formatado dinamicamente (Ex: "Domingo, 1 de novembro de 2026")
        exibirDataAtual();

        // Configura o redirecionamento para a AgendaActivity
        if (btnVerAgendaCompleta != null) {
            btnVerAgendaCompleta.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
                startActivity(intent);
            });
        }

        // Configuração dos cards de acesso rápido
        setupQuickAccessCards();
    }

    private void exibirDataAtual() {
        if (tvDate != null) {
            // Formato em português do Brasil: "EEEE, d 'de' MMMM 'de' yyyy"
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
            String dataFormatada = sdf.format(Calendar.getInstance().getTime());
            
            // Capitaliza a primeira letra do dia da semana (ex: "domingo" -> "Domingo")
            if (!dataFormatada.isEmpty()) {
                dataFormatada = dataFormatada.substring(0, 1).toUpperCase() + dataFormatada.substring(1);
            }
            
            tvDate.setText(dataFormatada);
        }
    }

    private void setupQuickAccessCards() {
        LinearLayout cardPacientes = findViewById(R.id.cardPacientes);
        if (cardPacientes != null) {
            cardPacientes.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, PacienteActivity.class));
            });
        }

        LinearLayout cardAgenda = findViewById(R.id.cardAgenda);
        if (cardAgenda != null) {
            cardAgenda.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, AgendaActivity.class));
            });
        }

        LinearLayout cardAtendimentos= findViewById(R.id.cardAtendimentos);
        if (cardAtendimentos != null) {
            cardAtendimentos.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, AtendimentosActivity.class));
            });
        }

        LinearLayout cardDoucementos = findViewById(R.id.cardDocuementos);
        if (cardDoucementos != null) {
            cardDoucementos.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, EmissaoDocumentoActivity.class));
            });
        }
    }
}
