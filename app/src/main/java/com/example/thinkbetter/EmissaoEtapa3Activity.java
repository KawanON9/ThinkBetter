package com.example.thinkbetter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class EmissaoEtapa3Activity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView btnRecomecar;
    private EditText etPeriodo, etCid, etDescricao;
    private Button btnPreVisualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_etapa3);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnRecomecar = findViewById(R.id.btnRecomecar);

        etPeriodo = findViewById(R.id.etPeriodo);
        etCid = findViewById(R.id.etCid);
        etDescricao = findViewById(R.id.etDescricao);
        btnPreVisualizar = findViewById(R.id.btnPreVisualizar);

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
                Toast.makeText(EmissaoEtapa3Activity.this, "Reiniciando emissão...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Ação do botão Pré-visualizar (Avança para a Etapa 4)
        btnPreVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String periodo = etPeriodo.getText().toString().trim();
                
                if (periodo.isEmpty()) {
                    Toast.makeText(EmissaoEtapa3Activity.this, "Informe o período do atestado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmissaoEtapa3Activity.this, "Avançando para Etapa 4 (Pré-visualização)...", Toast.LENGTH_SHORT).show();
                }
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