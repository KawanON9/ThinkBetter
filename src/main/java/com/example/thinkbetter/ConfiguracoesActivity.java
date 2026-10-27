package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

public class ConfiguracoesActivity extends MenuJava {
    private LinearLayout cardMeuPerfil, cardSeguranca, cardNotificacoes, cardSobre, cardSair;
    private SwitchCompat switchModoEscuro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        cardMeuPerfil = findViewById(R.id.cardMeuPerfil);
        cardSeguranca = findViewById(R.id.cardSeguranca);
        cardNotificacoes = findViewById(R.id.cardNotificacoes);
        cardSobre = findViewById(R.id.cardSobre);
        cardSair = findViewById(R.id.cardSair);
        switchModoEscuro = findViewById(R.id.switchModoEscuro);

        // Eventos de clique nas opções
        cardMeuPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        cardSeguranca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConfiguracoesActivity.this, "Abrindo Segurança e Privacidade...", Toast.LENGTH_SHORT).show();
            }
        });

        switchModoEscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(ConfiguracoesActivity.this, "Modo Escuro ativado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ConfiguracoesActivity.this, "Modo Escuro desativado", Toast.LENGTH_SHORT).show();
            }
        });

        cardNotificacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, NotificacoesActivity.class);
                startActivity(intent);
                Toast.makeText(ConfiguracoesActivity.this, "Abrindo Notificações...", Toast.LENGTH_SHORT).show();
            }
        });

        cardSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, SobreActivity.class);
                startActivity(intent);
            }
        });

        cardSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(ConfiguracoesActivity.this, "Saindo do sistema...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}