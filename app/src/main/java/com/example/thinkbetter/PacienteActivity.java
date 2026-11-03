package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteActivity extends MenuJava {

    private RecyclerView recyclerView;
    private PacienteAdapter adapter;
    private List<Paciente> pacienteList;
    private Button btnSort;
    private EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        btnSort = findViewById(R.id.btnSort);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerViewPacientes);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // A lista começa vazia e é preenchida com os dados do banco (GET /pacientes)
        pacienteList = new ArrayList<>();
        adapter = new PacienteAdapter(pacienteList);
        recyclerView.setAdapter(adapter);


        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(PacienteActivity.this, "Ordenação A-Z ativada", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        tvSeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PacienteActivity.this, ProntuarioActivity.class);
                startActivity(intent);
            }
        });

         */
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
                List<Paciente> lista = new ArrayList<>();
                for (PacienteResumo p : resposta.body()) {
                    // Mesmo formato de antes: "Unimed · (11) 9 9000-0003"
                    String detalhes = p.plano + " · " + Formatos.telefone(p.telefone);
                    lista.add(new Paciente(p.id, p.nome + " " + p.sobrenome, detalhes));
                }
                adapter.atualizar(lista);
                if (lista.isEmpty()) {
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
