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

public class EmissaoEtapa4Activity extends MenuJava {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView btnRecomecar;
    private Button btnEditar, btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_etapa4);

        btnRecomecar = findViewById(R.id.btnRecomecar);
        btnEditar = findViewById(R.id.btnEditar);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

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

}}