package com.example.thinkbetter;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
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

        super.setContentView(fullView);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Se a Activity filha NÃO for a MainActivity, nós substituímos o conteúdo do ScrollView pelo XML da tela filha
        if (!(this instanceof MainActivity)) {
            ScrollView scrollView = fullView.findViewById(R.id.scrollViewContent);
            if (scrollView != null) {
                scrollView.removeAllViews();
                // Infla o XML específico da tela filha dentro do ScrollView
                getLayoutInflater().inflate(layoutResID, scrollView, true);
            }
        }

        setupDrawerActions();
    }

    private void setupDrawerActions() {
        if (navigationView == null) return;

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
                    startActivity(new Intent(MenuJava.this, AgendaActivity.class));
                    finish();
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
                    if (!(MenuJava.this instanceof EmissaoDocumentoActivity)) {
                        startActivity(new Intent(MenuJava.this, EmissaoDocumentoActivity.class));
                        finish();
                    }
                    Toast.makeText(MenuJava.this, "Documentos", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_configuracoes) {
                    if (!(MenuJava.this instanceof ConfiguracoesActivity)) {
                        startActivity(new Intent(MenuJava.this, ConfiguracoesActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_sair) {
                    Toast.makeText(MenuJava.this, "Saindo do sistema...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MenuJava.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });
    }

    // Método para ligar o botão de abrir o menu em qualquer tela
    protected void setupMenuPsicologoButton(int btnMenuId) {
        ImageButton btnMenu = findViewById(btnMenuId);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        }
    }

    public void onBackPressedDispatcher() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}