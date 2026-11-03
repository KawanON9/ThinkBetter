package com.example.thinkbetter;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.SessaoAgenda;
import com.example.thinkbetter.api.Sessao;
import com.example.thinkbetter.api.StatusResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Atendimentos de HOJE do psicólogo logado, vindos do banco. */
public class AtendimentosActivity extends MenuJava {

    private RecyclerView recyclerView;
    private AtendimentoAdapter adapter;
    private final List<Atendimento> atendimentoList = new ArrayList<>();
    private TextView tvSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendimentos);

        setupMenuPsicologoButton(R.id.btnMenu);

        recyclerView = findViewById(R.id.recyclerViewAtendimentos);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AtendimentoAdapter(atendimentoList, this::confirmarAtendimento);
        recyclerView.setAdapter(adapter);
    }

    /** Recarrega ao voltar para a tela, para mostrar consultas recém-marcadas. */
    @Override
    protected void onResume() {
        super.onResume();
        carregarAtendimentosDeHoje();
    }

    /** GET /psicologos/{id}/agenda?data=hoje */
    private void carregarAtendimentosDeHoje() {
        tvSubtitle.setText("Carregando...");
        ApiClient.api().agendaDoDia(Sessao.id(this), Formatos.hojeIso())
                .enqueue(new Callback<List<SessaoAgenda>>() {
                    @Override
                    public void onResponse(Call<List<SessaoAgenda>> call, Response<List<SessaoAgenda>> resposta) {
                        if (!resposta.isSuccessful() || resposta.body() == null) {
                            tvSubtitle.setText(ApiClient.mensagemDeErro(resposta));
                            return;
                        }
                        atendimentoList.clear();
                        for (SessaoAgenda s : resposta.body()) {
                            String data = Formatos.dataBr(s.data).substring(0, 5); // "03/11"
                            boolean confirmada = "Confirmada".equals(s.status);
                            atendimentoList.add(new Atendimento(s.idConsulta, s.hora, data, s.nomePaciente,
                                    nomeDoTipo(s.tipo) + " · " + s.plano + " · " + s.modalidade,
                                    s.status.toLowerCase(), confirmada ? 1 : 0));
                        }
                        adapter.notifyDataSetChanged();
                        tvSubtitle.setText(atendimentoList.size() + " no total para hoje");
                    }

                    @Override
                    public void onFailure(Call<List<SessaoAgenda>> call, Throwable t) {
                        tvSubtitle.setText(ApiClient.mensagemDeFalha(t));
                    }
                });
    }

    /** POST /consultas/{id}/confirmar?psicologo={id} e recarrega a lista. */
    private void confirmarAtendimento(Atendimento atendimento) {
        ApiClient.api().confirmarConsultaComoPsicologo(atendimento.getIdConsulta(), Sessao.id(this))
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> resposta) {
                        String msg = resposta.isSuccessful()
                                ? "Atendimento de " + atendimento.getNomePaciente() + " confirmado!"
                                : ApiClient.mensagemDeErro(resposta);
                        Toast.makeText(AtendimentosActivity.this, msg, Toast.LENGTH_SHORT).show();
                        carregarAtendimentosDeHoje();
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        Toast.makeText(AtendimentosActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
                        carregarAtendimentosDeHoje();
                    }
                });
    }

    /** Valores do banco (sem acento) → texto da tela. */
    private static String nomeDoTipo(String tipo) {
        if ("Avaliacao".equals(tipo)) return "Avaliação";
        if ("Sessao".equals(tipo)) return "Sessão";
        return tipo;
    }
}
