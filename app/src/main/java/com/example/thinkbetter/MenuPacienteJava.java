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

public class MenuPacienteJava extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    public void setContentView(int layoutResID) {
        // Infla o layout principal que contém o DrawerLayout e o NavigationView do Paciente
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main_paciente, null);

        super.setContentView(fullView);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Certifique-se de inflar o menu correto via código ou via app:menu no XML do NavigationView
        // navigationView.inflateMenu(R.id.drawer_menu_paciente);

        // Se a Activity filha NÃO for a MainPacienteActivity, substituímos o conteúdo do ScrollView
        if (!(this instanceof MainPacienteActivity)) {
            ScrollView scrollView = fullView.findViewById(R.id.scrollViewContent);
            if (scrollView != null) {
                scrollView.removeAllViews();
                getLayoutInflater().inflate(layoutResID, scrollView, true);
            }
        }

        setupDrawerActions();

        // Botão/gesto "voltar": a tela inicial do paciente pede confirmação para sair;
        // as demais voltam à anterior e ganham o botão ← ao lado do ☰.
        boolean telaInicial = this instanceof MainPacienteActivity;
        Navegacao.configurarVoltar(this, drawerLayout, telaInicial, null);
        if (!telaInicial) Navegacao.adicionarBotaoVoltar(this);
    }

    private void setupDrawerActions() {
        if (navigationView == null) return;

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navegarComoPaciente(MenuPacienteJava.this, item.getItemId());

                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });
    }

    /**
     * Itens do menu do PACIENTE. É estático para ser usado também pelas telas
     * comuns aos dois perfis (Configurações, Meu Perfil...), que herdam de MenuJava.
     */
    static void navegarComoPaciente(AppCompatActivity tela, int id) {
        if (id == R.id.nav_paciente_inicio) {
            Navegacao.irPara(tela, MainPacienteActivity.class, MainPacienteActivity.class);
        } else if (id == R.id.nav_paciente_agendar) {
            Navegacao.irPara(tela, AgendarConsultaActivity.class, MainPacienteActivity.class);
        } else if (id == R.id.nav_paciente_documentos) {
            Navegacao.irPara(tela, MeusDocumentosActivity.class, MainPacienteActivity.class);
        } else if (id == R.id.nav_paciente_configuracoes) {
            Navegacao.irPara(tela, ConfiguracoesActivity.class, MainPacienteActivity.class);
        } else if (id == R.id.nav_paciente_sair) {
            sairDoSistema(tela);
        }
    }

    /** Esquece o usuário logado e volta ao Login, sem deixar telas abertas por trás. */
    static void sairDoSistema(AppCompatActivity tela) {
        Toast.makeText(tela, "Saindo do sistema...", Toast.LENGTH_SHORT).show();
        com.example.thinkbetter.api.Sessao.sair(tela);
        Intent intent = new Intent(tela, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        tela.startActivity(intent);
        tela.finish();
    }

    protected void setupMenuButton(int btnMenuId) {
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