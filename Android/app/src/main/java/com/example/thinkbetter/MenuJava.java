package com.example.thinkbetter;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MenuJava extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    public void setContentView(int layoutResID) {
        // 1. Infla o layout principal (MainActivity) que contém o DrawerLayout e o NavigationView
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main, null);

        // 2. Encontra o ScrollView que envolve o conteúdo da MainActivity
        // (No seu XML da MainActivity, o conteúdo está dentro de um ScrollView)
        ScrollView contentScrollView = fullView.findViewById(android.R.id.content); // ou pegamos pelo ID se houver, mas vamos injetar direto no ScrollView ou substituir o seu conteúdo

        // Como o seu XML da MainActivity usa um ScrollView envolvendo o ConstraintLayout principal,
        // vamos procurar o ScrollView para substituir seu conteúdo interno, ou injetar de forma limpa:
        super.setContentView(fullView);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Se a Activity filha NÃO for a MainActivity, nós substituímos o conteúdo do ScrollView pelo XML da tela filha
        if (!(this instanceof MainActivity)) {
            ScrollView scrollView = fullView.findViewById(R.id.scrollViewContent); // Vamos dar ID no passo 2
            if (scrollView != null) {
                scrollView.removeAllViews();
                // Infla o XML específico da tela filha (ex: activity_paciente) dentro do ScrollView
                getLayoutInflater().inflate(layoutResID, scrollView, true);
            }
        }

        setupDrawerActions();
    }

    private void setupDrawerActions() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    if (!(MenuJava.this instanceof MainActivity)) {
                        startActivity(new Intent(MenuJava.this, MainActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_agenda) {
                    Toast.makeText(MenuJava.this, "Agenda", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_atendimentos) {
                    if (!(MenuJava.this instanceof AtendimentosActivity)) {
                        startActivity(new Intent(MenuJava.this, AtendimentosActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_pacientes) {
                    if (!(MenuJava.this instanceof PacienteActivity)) {
                        startActivity(new Intent(MenuJava.this, PacienteActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_documentos) {
                    Toast.makeText(MenuJava.this, "Documentos", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_configuracoes) {
                    Toast.makeText(MenuJava.this, "Configurações", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_sair) {
                    Toast.makeText(MenuJava.this, "Saindo do sistema...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MenuJava.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    // Método para ligar o botão de abrir o menu em qualquer tela
    protected void setupMenuButton(int btnMenuId) {
        ImageButton btnMenu = findViewById(btnMenuId);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    public void onBackPressedDispather() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}