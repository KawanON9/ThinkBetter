package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends MenuJava {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout principal da Home
        setContentView(R.layout.activity_main);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        // Opcional: Se você quiser que os cards de acesso rápido da tela inicial
        // também abram as respectivas telas, você pode configurá-los aqui:
        setupQuickAccessCards();
    }

    private void setupQuickAccessCards() {
        // Exemplo caso queira adicionar cliques nos cards da sua home (você pode dar IDs neles no XML se quiser):

        LinearLayout cardPacientes = findViewById(R.id.cardPacientes); // Dê um ID no XML se for usar
        if (cardPacientes != null) {
            cardPacientes.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, PacienteActivity.class));
            });
        }

    }
}