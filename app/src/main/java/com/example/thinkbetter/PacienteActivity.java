package com.example.thinkbetter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.PacienteResumo;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteActivity extends MenuJava {

    private RecyclerView recyclerView;
    private PacienteAdapter adapter;
    private List<Paciente> pacienteList;
    private Button btnSort;
    private EditText etSearch;
    private TextView tvSubtitle;

    /** Todos os pacientes vindos do banco; a lista na tela é este conjunto filtrado e ordenado. */
    private final List<Paciente> todosPacientes = new ArrayList<>();
    private boolean ordemCrescente = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        btnSort = findViewById(R.id.btnSort);
        etSearch = findViewById(R.id.etSearch);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        recyclerView = findViewById(R.id.recyclerViewPacientes);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // A lista começa vazia e é preenchida com os dados do banco (GET /pacientes)
        pacienteList = new ArrayList<>();
        adapter = new PacienteAdapter(pacienteList);
        recyclerView.setAdapter(adapter);


        // Ordenar: alterna A→Z / Z→A (antes abria a tela inicial por engano)
        btnSort.setOnClickListener(v -> {
            ordemCrescente = !ordemCrescente;
            btnSort.setText(ordemCrescente ? "A→Z" : "Z→A");
            aplicarFiltro();
        });

        // Busca: filtra pelo nome enquanto digita (ignora maiúsculas e acentos)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { aplicarFiltro(); }
        });
    }

    /** Mostra só os pacientes cujo nome contém o texto da busca, na ordem escolhida. */
    private void aplicarFiltro() {
        String busca = semAcento(etSearch.getText().toString().trim());
        List<Paciente> visiveis = new ArrayList<>();
        for (Paciente p : todosPacientes) {
            if (busca.isEmpty() || semAcento(p.getNome()).contains(busca)) visiveis.add(p);
        }
        Comparator<Paciente> porNome = (a, b) -> semAcento(a.getNome()).compareTo(semAcento(b.getNome()));
        Collections.sort(visiveis, ordemCrescente ? porNome : Collections.reverseOrder(porNome));
        adapter.atualizar(visiveis);

        int total = todosPacientes.size();
        tvSubtitle.setText(busca.isEmpty()
                ? total + (total == 1 ? " cadastrado" : " cadastrados")
                : visiveis.size() + " de " + total + " pacientes");
    }

    /** "José" → "jose": para buscar e ordenar sem depender de acento ou maiúscula. */
    private static String semAcento(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    /** Recarrega sempre que a tela volta a aparecer (ex.: depois de um cadastro). */
    @Override
    protected void onResume() {
        super.onResume();
        carregarPacientes();
    }

    private void carregarPacientes() {
        ApiClient.api().listarPacientes().enqueue(new Callback<List<PacienteResumo>>() {
            @Override
            public void onResponse(Call<List<PacienteResumo>> call, Response<List<PacienteResumo>> resposta) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    Toast.makeText(PacienteActivity.this,
                            ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                    return;
                }
                todosPacientes.clear();
                for (PacienteResumo p : resposta.body()) {
                    // Mesmo formato de antes: "Unimed · (11) 9 9000-0003"
                    String detalhes = p.plano + " · " + Formatos.telefone(p.telefone);
                    todosPacientes.add(new Paciente(p.id, p.nome + " " + p.sobrenome, detalhes));
                }
                aplicarFiltro(); // mantém a busca e a ordem escolhidas
                if (todosPacientes.isEmpty()) {
                    Toast.makeText(PacienteActivity.this,
                            "Nenhum paciente cadastrado ainda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PacienteResumo>> call, Throwable t) {
                Toast.makeText(PacienteActivity.this,
                        ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        });
    }
}
