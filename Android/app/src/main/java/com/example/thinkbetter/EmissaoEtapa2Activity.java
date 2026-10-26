package com.example.thinkbetter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class EmissaoEtapa2Activity extends MenuJava {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView btnRecomecar;
    private LinearLayout cardAtestado, cardDeclaracao, cardLaudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_etapa2);

        //Menu psicologo
        setupMenuPsicologoButton(R.id.btnMenu);

        btnRecomecar = findViewById(R.id.btnRecomecar);

        cardAtestado = findViewById(R.id.cardAtestado);
        cardDeclaracao = findViewById(R.id.cardDeclaracao);
        cardLaudo = findViewById(R.id.cardLaudo);

        // Ação do botão Recomeçar
        btnRecomecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa2Activity.this, "Reiniciando emissão...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Seleção de Atestado
        cardAtestado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa2Activity.this, "Atestado selecionado - Indo para Etapa 3", Toast.LENGTH_SHORT).show();
            }
        });

        // Seleção de Declaração
        cardDeclaracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa2Activity.this, "Declaração selecionada - Indo para Etapa 3", Toast.LENGTH_SHORT).show();
            }
        });

        // Seleção de Laudo
        cardLaudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa2Activity.this, "Laudo selecionado - Indo para Etapa 3", Toast.LENGTH_SHORT).show();
            }
        });
    }
}