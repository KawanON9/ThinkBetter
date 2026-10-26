package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SobreActivity extends MenuJava {

    private TextView btnVoltar;
    private TextView btnCentralAjuda, btnTermosUso, btnPoliticaPrivacidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        setupMenuPsicologoButton(R.id.btnMenu);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnCentralAjuda = findViewById(R.id.btnCentralAjuda);
        btnTermosUso = findViewById(R.id.btnTermosUso);
        btnPoliticaPrivacidade = findViewById(R.id.btnPoliticaPrivacidade);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
    }
}