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

    /**
     * true quando quem está logado é PACIENTE. As telas comuns aos dois perfis
     * (Configurações, Meu Perfil, Segurança, Notificações, Sobre) herdam desta
     * classe, então elas precisam mostrar o menu do paciente nesse caso.
     */
    protected boolean logadoComoPaciente() {
        return com.example.thinkbetter.api.LoginResponse.PACIENTE
                .equals(com.example.thinkbetter.api.Sessao.tipo(this));
    }

    @Override
    public void setContentView(int layoutResID) {
        // 1. Infla o layout com o DrawerLayout e o NavigationView do perfil logado
        //    (activity_main tem o menu do psicólogo; activity_main_paciente, o do paciente)
        boolean paciente = logadoComoPaciente();
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(
                paciente ? R.layout.activity_main_paciente : R.layout.activity_main, null);

        super.setContentView(fullView);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Se a Activity filha NÃO for a MainActivity, nós substituímos o conteúdo do ScrollView pelo XML da tela filha
        if (paciente || !(this instanceof MainActivity)) {
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

                if (logadoComoPaciente()) {
                    // Tela comum aberta por um paciente: usa a navegação do paciente
                    MenuPacienteJava.navegarComoPaciente(MenuJava.this, id);
                } else if (id == R.id.nav_inicio) {
                    if (!(MenuJava.this instanceof MainActivity)) {
                        startActivity(new Intent(MenuJava.this, MainActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_agenda) {
                    if (!(MenuJava.this instanceof AgendaActivity)) {
                        startActivity(new Intent(MenuJava.this, AgendaActivity.class));
                        finish();
                    }
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
                } else if (id == R.id.nav_configuracoes) {
                    if (!(MenuJava.this instanceof ConfiguracoesActivity)) {
                        startActivity(new Intent(MenuJava.this, ConfiguracoesActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_sair) {
                    MenuPacienteJava.sairDoSistema(MenuJava.this);
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