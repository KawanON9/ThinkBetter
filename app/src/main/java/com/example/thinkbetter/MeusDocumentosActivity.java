package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MeusDocumentosActivity extends MenuPacienteJava {

    private ImageButton btnMenu;
    private RecyclerView recyclerView;
    private DocumentoAdapter adapter;
    private List<Documento> documentoListCompleta;

    private TextView chipTodos, chipRecibos, chipAtestados, chipDeclaracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_documentos);

        setupMenuButton(R.id.btnMenu);
        btnMenu = findViewById(R.id.btnMenu);
        recyclerView = findViewById(R.id.recyclerViewDocumentos);

        chipTodos = findViewById(R.id.chipTodos);
        chipRecibos = findViewById(R.id.chipRecibos);
        chipAtestados = findViewById(R.id.chipAtestados);
        chipDeclaracoes = findViewById(R.id.chipDeclaracoes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1. Populando a base completa de documentos (Recibos, Atestados e Declarações)
        documentoListCompleta = new ArrayList<>();
        documentoListCompleta.add(new Documento("Recibo de consulta", "Dra. Ana Souza · 20/10/2026", "Recibos"));
        documentoListCompleta.add(new Documento("Atestado de comparecimento", "Dra. Ana Souza · 13/10/2026", "Atestados"));
        documentoListCompleta.add(new Documento("Recibo de consulta", "Dr. Rafael Costa · 06/10/2026", "Recibos"));
        documentoListCompleta.add(new Documento("Declaração de acompanhamento", "Dra. Ana Souza · 22/09/2026", "Declarações"));
        documentoListCompleta.add(new Documento("Atestado médico psicológico", "Dr. Rafael Costa · 15/09/2026", "Atestados"));
        documentoListCompleta.add(new Documento("Declaração de aptidão", "Dra. Ana Souza · 01/09/2026", "Declarações"));

        // 2. Configura o adapter inicial com todos os itens
        adapter = new DocumentoAdapter(documentoListCompleta);
        recyclerView.setAdapter(adapter);

        // 3. Configuração dos cliques nos Chips de Filtro
        chipTodos.setOnClickListener(v -> {
            ativarChip(chipTodos);
            adapter.atualizarLista(documentoListCompleta);
        });

        chipRecibos.setOnClickListener(v -> {
            ativarChip(chipRecibos);
            filtrarPorTipo("Recibos");
        });

        chipAtestados.setOnClickListener(v -> {
            ativarChip(chipAtestados);
            filtrarPorTipo("Atestados");
        });

        chipDeclaracoes.setOnClickListener(v -> {
            ativarChip(chipDeclaracoes);
            filtrarPorTipo("Declarações");
        });
    }

    private void filtrarPorTipo(String tipo) {
        List<Documento> listaFiltrada = new ArrayList<>();
        for (Documento doc : documentoListCompleta) {
            if (doc.getTipo().equalsIgnoreCase(tipo)) {
                listaFiltrada.add(doc);
            }
        }
        adapter.atualizarLista(listaFiltrada);
    }

    private void ativarChip(TextView chipAtivo) {
        // Reseta todos os chips para o estado não selecionado
        chipTodos.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipTodos.setTextColor(android.graphics.Color.parseColor("#557777"));

        chipRecibos.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipRecibos.setTextColor(android.graphics.Color.parseColor("#557777"));

        chipAtestados.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipAtestados.setTextColor(android.graphics.Color.parseColor("#557777"));

        chipDeclaracoes.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipDeclaracoes.setTextColor(android.graphics.Color.parseColor("#557777"));

        // Ativa o chip selecionado
        chipAtivo.setBackgroundResource(R.drawable.bg_chip_selected);
        chipAtivo.setTextColor(android.graphics.Color.WHITE);
    }
}
