package com.example.thinkbetter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        // Ação para abrir o menu lateral ao clicar no botão hamburguer
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Lógica de clique nos itens do menu lateral
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    Toast.makeText(MainActivity.this, "Início", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_agenda) {
                    Toast.makeText(MainActivity.this, "Agenda", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_atendimentos) {
                    Toast.makeText(MainActivity.this, "Atendimentos", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_pacientes) {
                    Toast.makeText(MainActivity.this, "Pacientes", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_documentos) {
                    Toast.makeText(MainActivity.this, "Documentos", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_configuracoes) {
                    Toast.makeText(MainActivity.this, "Configurações", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_sair) {
                    Toast.makeText(MainActivity.this, "Saindo do sistema...", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a Activity
                }

                // Fecha o menu após selecionar uma opção
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    // Fecha o menu com o botão "Voltar" do aparelho, se estiver aberto
    public void onBackPressedDispatcher() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}