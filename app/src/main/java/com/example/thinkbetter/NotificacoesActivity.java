package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.widget.SwitchCompat;

public class NotificacoesActivity extends MenuJava {
    private TextView btnVoltar;
    private SwitchCompat switchLembretes, switchConfirmacoes, switchAlertas, switchSilenciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        btnVoltar = findViewById(R.id.btnVoltar);
        switchLembretes = findViewById(R.id.switchLembretesSessao);
        switchConfirmacoes = findViewById(R.id.switchConfirmacoesPacientes);
        switchAlertas = findViewById(R.id.switchAlertasFinanceiros);
        switchSilenciar = findViewById(R.id.switchSilenciarPeriodo);

        // Ação do Botão Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a Activity e volta para a tela anterior
            }
        });

        // Listeners das chaves (Switches)
        switchLembretes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "ativados" : "desativados";
            Toast.makeText(NotificacoesActivity.this, "Lembretes de sessão " + status, Toast.LENGTH_SHORT).show();
        });

        switchConfirmacoes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "ativadas" : "desativadas";
            Toast.makeText(NotificacoesActivity.this, "Confirmações de pacientes " + status, Toast.LENGTH_SHORT).show();
        });

        switchAlertas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "ativados" : "desativados";
            Toast.makeText(NotificacoesActivity.this, "Alertas financeiros " + status, Toast.LENGTH_SHORT).show();
        });

        switchSilenciar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(NotificacoesActivity.this, "Silenciar por período ativado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NotificacoesActivity.this, "Silenciar por período desativado", Toast.LENGTH_SHORT).show();
            }
        });
}}