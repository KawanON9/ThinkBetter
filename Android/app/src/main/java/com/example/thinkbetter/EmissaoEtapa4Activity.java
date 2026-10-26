package com.example.thinkbetter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class EmissaoEtapa4Activity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView btnRecomecar;
    private Button btnEditar, btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_etapa4);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnRecomecar = findViewById(R.id.btnRecomecar);
        btnEditar = findViewById(R.id.btnEditar);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // Ação para abrir o menu lateral
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Ação do botão Recomeçar
        btnRecomecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa4Activity.this, "Reiniciando emissão...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Ação do botão Editar (Retorna à etapa anterior)
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa4Activity.this, "Retornando para edição...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Ação do botão Confirmar (Avança para a Etapa 5 / Conclusão)
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmissaoEtapa4Activity.this, "Documento confirmado! Indo para Etapa 5...", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}