package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilActivity extends MenuJava {

    private TextView btnVoltar;
    private Button btnAlterarFoto, btnSalvar;
    private EditText etNome, etEmail, etTelefone, etCrp, etEspecialidade, etEmergencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        setupMenuPsicologoButton(R.id.btnMenu);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnAlterarFoto = findViewById(R.id.btnAlterarFoto);
        btnSalvar = findViewById(R.id.btnSalvar);

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etCrp = findViewById(R.id.etCrp);
        etEspecialidade = findViewById(R.id.etEspecialidade);
        etEmergencia = findViewById(R.id.etEmergencia);

        carregarDadosPerfil();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Selecionar nova foto...", Toast.LENGTH_SHORT).show();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();
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
}