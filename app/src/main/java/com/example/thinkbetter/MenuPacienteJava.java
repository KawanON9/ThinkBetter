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
    }

    private void setupDrawerActions() {
        if (navigationView == null) return;

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_paciente_inicio) {
                    if (!(MenuPacienteJava.this instanceof MainPacienteActivity)) {
                        startActivity(new Intent(MenuPacienteJava.this, MainPacienteActivity.class));
                        finish();
                    }
                } else if (id == R.id.nav_paciente_agendar) {
                        Intent intent = new Intent(MenuPacienteJava.this, AgendarConsultaActivity.class);
                        startActivity(intent);
                        finish();
                } else if (id == R.id.nav_paciente_documentos) {
                        Intent intent = new Intent(MenuPacienteJava.this, MeusDocumentosActivity.class);
                        startActivity(intent);
                } else if (id == R.id.nav_paciente_configuracoes) {
                    // Crie ou direcione para a tela de configurações do paciente se houver
                    Intent intent = new Intent(MenuPacienteJava.this, ConfiguracoesActivity.class);
                    startActivity(intent);
                    Toast.makeText(MenuPacienteJava.this, "Configurações", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_paciente_sair) {
                    Toast.makeText(MenuPacienteJava.this, "Saindo do sistema...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MenuPacienteJava.this, LoginActivity.class);
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

    public void onBackPressedDispatcher() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}