package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainPacienteActivity extends MenuPacienteJava {

    private Button btnAgendarNovaConsulta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paciente);

        setupMenuPacienteButton(R.id.btnMenu);
        btnAgendarNovaConsulta = findViewById(R.id.btnAgendarNovaConsulta);


        // Ação do botão Agendar Nova Consulta
        btnAgendarNovaConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPacienteActivity.this, AgendarConsultaActivity.class);
                startActivity(intent);
                Toast.makeText(MainPacienteActivity.this, "Abrindo fluxo de agendamento...", Toast.LENGTH_SHORT).show();
            }
        });

    }

}