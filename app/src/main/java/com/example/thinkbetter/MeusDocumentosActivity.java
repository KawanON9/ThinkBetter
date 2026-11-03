package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.DocumentoGerado;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.Sessao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // 1. A lista começa vazia e é preenchida com os documentos do banco (onResume)
        documentoListCompleta = new ArrayList<>();

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

    /** Recarrega ao abrir a tela: documentos recém-emitidos pelo psicólogo aparecem aqui. */
    @Override
    protected void onResume() {
        super.onResume();
        carregarDocumentos();
    }

    /** GET /pacientes/{id}/documentos */
    private void carregarDocumentos() {
        ApiClient.api().documentosDoPaciente(Sessao.id(this)).enqueue(new Callback<List<DocumentoGerado>>() {
            @Override
            public void onResponse(Call<List<DocumentoGerado>> call, Response<List<DocumentoGerado>> resposta) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    Toast.makeText(MeusDocumentosActivity.this,
                            ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                    return;
                }
                documentoListCompleta.clear();
                for (DocumentoGerado d : resposta.body()) {
                    String detalhes = d.psicologo + " · " + Formatos.dataBr(d.emitidoEm);
                    documentoListCompleta.add(new Documento(d.titulo, detalhes, categoria(d.tipo), d.texto));
                }
                ativarChip(chipTodos);
                adapter.atualizarLista(new ArrayList<>(documentoListCompleta));
                if (documentoListCompleta.isEmpty()) {
                    Toast.makeText(MeusDocumentosActivity.this,
                            "Você ainda não tem documentos emitidos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DocumentoGerado>> call, Throwable t) {
                Toast.makeText(MeusDocumentosActivity.this,
                        ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Tipo no banco → nome do filtro (chips) da tela. */
    private static String categoria(String tipo) {
        switch (tipo) {
            case "Atestado": return "Atestados";
            case "Declaracao": return "Declarações";
            case "Recibo": return "Recibos";
            case "Laudo": return "Laudos";
            default: return tipo;
        }
    }
}
