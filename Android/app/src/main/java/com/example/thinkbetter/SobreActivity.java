package com.example.thinkbetter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SobreActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView btnVoltar;
    private TextView btnCentralAjuda, btnTermosUso, btnPoliticaPrivacidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        // Inicialização dos componentes
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnCentralAjuda = findViewById(R.id.btnCentralAjuda);
        btnTermosUso = findViewById(R.id.btnTermosUso);
        btnPoliticaPrivacidade = findViewById(R.id.btnPoliticaPrivacidade);

        // Ação do Menu Hamburguer
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Ação do Botão Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a tela atual e retorna à anterior
            }
        });

        // Eventos de clique nas opções da lista
        btnCentralAjuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SobreActivity.this, "Abrindo Central de Ajuda...", Toast.LENGTH_SHORT).show();
            }
        });

        btnTermosUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SobreActivity.this, "Abrindo Termos de Uso...", Toast.LENGTH_SHORT).show();
            }
        });

        btnPoliticaPrivacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SobreActivity.this, "Abrindo Política de Privacidade...", Toast.LENGTH_SHORT).show();
            }
        });

        // Gerenciador de navegação no menu lateral
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void onBackPressedDispatcher() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }
}