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

public class PerfilActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView btnVoltar;
    private Button btnAlterarFoto, btnSalvar;
    private EditText etNome, etEmail, etTelefone, etCrp, etEspecialidade, etEmergencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Referências das views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnAlterarFoto = findViewById(R.id.btnAlterarFoto);
        btnSalvar = findViewById(R.id.btnSalvar);

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etCrp = findViewById(R.id.etCrp);
        etEspecialidade = findViewById(R.id.etEspecialidade);
        etEmergencia = findViewById(R.id.etEmergencia);

        // Preenchimento dos dados com as informações de exemplo da tela
        carregarDadosPerfil();

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
                finish(); // Retorna à Activity anterior
            }
        });

        // Alterar Foto
        btnAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Selecionar nova foto...", Toast.LENGTH_SHORT).show();
            }
        });

        // Salvar Alterações
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        // Trata a navegação pelo Menu Lateral
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void carregarDadosPerfil() {
        etNome.setText("Dr. Rafael Costa");
        etEmail.setText("rafael@gmail.com.br");
        etTelefone.setText("(11) 9 9000-0000");
        etCrp.setText("06/67890");
        etEspecialidade.setText("Terapia Cognitivo-Comportamental");
        etEmergencia.setText("");
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