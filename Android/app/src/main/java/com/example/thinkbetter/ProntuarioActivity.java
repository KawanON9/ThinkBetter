package com.example.thinkbetter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ProntuarioActivity extends MenuJava {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu, btnVoltar;
    private TextView btnEditarObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prontuario);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuButton(R.id.btnMenu);


        btnMenu = findViewById(R.id.btnMenu);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnEditarObs = findViewById(R.id.btnEditarObs);

        // Ação para abrir o menu lateral
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Ação do botão voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Retorna para a tela anterior
            }
        });

        // Ação do botão editar observações
        btnEditarObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProntuarioActivity.this, "Editar Observações Clínicas", Toast.LENGTH_SHORT).show();
            }
        });

        // Navegação pelo menu lateral
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}