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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MeusDocumentosActivity extends MenuPacienteJava {

    private RecyclerView recyclerView;
    private DocumentoAdapter adapter;
    private List<Documento> documentoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_documentos);


        setupMenuPacienteButton(R.id.btnMenu);
        recyclerView = findViewById(R.id.recyclerViewDocumentos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dados extraídos da imagem de referência
        documentoList = new ArrayList<>();
        documentoList.add(new Documento("Recibo de consulta", "Dra. Ana Souza · 20/10/2026", "Recibos"));
        documentoList.add(new Documento("Atestado de comparecimento", "Dra. Ana Souza · 13/10/2026", "Atestados"));
        documentoList.add(new Documento("Recibo de consulta", "Dr. Rafael Costa · 06/10/2026", "Recibos"));
        documentoList.add(new Documento("Declaração de acompanhamento", "Dra. Ana Souza · 22/09/2026", "Declarações"));

        adapter = new DocumentoAdapter(documentoList);
        recyclerView.setAdapter(adapter);


    }

}