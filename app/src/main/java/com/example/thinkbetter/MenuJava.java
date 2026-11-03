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

        // Botão/gesto "voltar": a tela inicial do psicólogo pede confirmação para sair;
        // as demais voltam à anterior e ganham o botão ← ao lado do ☰.
        boolean telaInicial = !paciente && (this instanceof MainActivity);
        Navegacao.configurarVoltar(this, drawerLayout, telaInicial, this::tratarVoltar);
        if (!telaInicial) Navegacao.adicionarBotaoVoltar(this);
    }

    /**
     * Sobrescreva para tratar o "voltar" antes do padrão (ex.: voltar uma etapa).
     * @return true se tratou; false para voltar à tela anterior.
     */
    protected boolean tratarVoltar() {
        return false;
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
                    Navegacao.irPara(MenuJava.this, MainActivity.class, MainActivity.class);
                } else if (id == R.id.nav_agenda) {
                    Navegacao.irPara(MenuJava.this, AgendaActivity.class, MainActivity.class);
                } else if (id == R.id.nav_atendimentos) {
                    Navegacao.irPara(MenuJava.this, AtendimentosActivity.class, MainActivity.class);
                } else if (id == R.id.nav_pacientes) {
                    Navegacao.irPara(MenuJava.this, PacienteActivity.class, MainActivity.class);
                } else if (id == R.id.nav_documentos) {
                    Navegacao.irPara(MenuJava.this, EmissaoDocumentoActivity.class, MainActivity.class);
                } else if (id == R.id.nav_configuracoes) {
                    Navegacao.irPara(MenuJava.this, ConfiguracoesActivity.class, MainActivity.class);
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
}