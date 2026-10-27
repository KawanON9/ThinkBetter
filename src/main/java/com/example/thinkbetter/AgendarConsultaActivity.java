package com.example.thinkbetter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AgendarConsultaActivity extends MenuPacienteJava {
    private LinearLayout slotConsultaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_consulta);

        //Menu do Paciente
        setupMenuPacienteButton(R.id.btnMenu);
        slotConsultaUsuario = findViewById(R.id.slotConsultaUsuario);

        // Interação: Clicar na consulta marcada abre o modal de cancelamento
        slotConsultaUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogCancelarConsulta();
            }
        });

    }

    private void mostrarDialogCancelarConsulta() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancelar_consulta);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnSimCancelar = dialog.findViewById(R.id.btnSimCancelar);
        Button btnManterConsulta = dialog.findViewById(R.id.btnManterConsulta);

        btnSimCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AgendarConsultaActivity.this, "Consulta cancelada com sucesso!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnManterConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}