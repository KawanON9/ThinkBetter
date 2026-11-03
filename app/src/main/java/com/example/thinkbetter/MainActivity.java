package com.example.thinkbetter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.SessaoAgenda;
import com.example.thinkbetter.api.Sessao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Tela inicial do PSICÓLOGO: saudação, data de hoje e "Agenda de Hoje" vinda do banco. */
public class MainActivity extends MenuJava {

    private TextView tvDate, tvGreeting, tvAvatarTop;
    private TextView tvContadorSessoes, tvSemSessoes;
    private LinearLayout containerSessoesHoje;
    private Button btnVerAgendaCompleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout principal da Home
        setContentView(R.layout.activity_main);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuPsicologoButton(R.id.btnMenu);

        // Inicializa os elementos da interface
        tvDate = findViewById(R.id.tvDate);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvAvatarTop = findViewById(R.id.tvAvatarTop);
        tvContadorSessoes = findViewById(R.id.tvContadorSessoes);
        tvSemSessoes = findViewById(R.id.tvSemSessoes);
        containerSessoesHoje = findViewById(R.id.containerSessoesHoje);
        btnVerAgendaCompleta = findViewById(R.id.btnVerAgendaCompleta);

        // Nome de quem fez login (guardado pela LoginActivity)
        String nome = Sessao.nome(this);
        tvGreeting.setText("Olá, " + Formatos.primeiroNome(nome) + "!");
        tvAvatarTop.setText(Formatos.iniciais(nome));

        // Configura o redirecionamento para a AgendaActivity
        if (btnVerAgendaCompleta != null) {
            btnVerAgendaCompleta.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, AgendaActivity.class)));
        }

        // Configuração dos cards de acesso rápido
        setupQuickAccessCards();
    }

    /**
     * Roda ao abrir a tela e toda vez que ela volta a aparecer
     * (ex.: voltando do Prontuário ou depois de deixar o app em segundo plano):
     * a data e as sessões ficam sempre atualizadas.
     */
    @Override
    protected void onResume() {
        super.onResume();
        tvDate.setText(Formatos.hojePorExtenso());
        carregarAgendaDeHoje();
    }

    /** GET /psicologos/{id}/agenda?data=hoje */
    private void carregarAgendaDeHoje() {
        long idPsicologo = Sessao.id(this);
        ApiClient.api().agendaDoDia(idPsicologo, Formatos.hojeIso())
                .enqueue(new Callback<List<SessaoAgenda>>() {
                    @Override
                    public void onResponse(Call<List<SessaoAgenda>> call, Response<List<SessaoAgenda>> resposta) {
                        if (!resposta.isSuccessful() || resposta.body() == null) {
                            mostrarMensagem(ApiClient.mensagemDeErro(resposta));
                            return;
                        }
                        mostrarSessoes(resposta.body());
                    }

                    @Override
                    public void onFailure(Call<List<SessaoAgenda>> call, Throwable t) {
                        mostrarMensagem(ApiClient.mensagemDeFalha(t));
                    }
                });
    }

    private void mostrarSessoes(List<SessaoAgenda> sessoes) {
        containerSessoesHoje.removeAllViews();
        int total = sessoes.size();
        tvContadorSessoes.setText(total == 1 ? "1 sessão" : total + " sessões");

        if (sessoes.isEmpty()) {
            mostrarMensagem("Nenhuma sessão agendada para hoje.");
            return;
        }
        tvSemSessoes.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (SessaoAgenda s : sessoes) {
            View item = inflater.inflate(R.layout.item_sessao_hoje, containerSessoesHoje, false);

            ((TextView) item.findViewById(R.id.tvHoraSessao)).setText(s.hora);
            ((TextView) item.findViewById(R.id.tvNomePacienteSessao)).setText(s.nomePaciente);
            ((TextView) item.findViewById(R.id.tvDetalhesSessao)).setText(
                    s.plano + " · " + nomeDoTipo(s.tipo) + " · " + s.modalidade);
            pintarStatus(item.findViewById(R.id.tvStatusSessao), s.status);

            // Toque na sessão → prontuário do paciente
            item.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProntuarioActivity.class);
                intent.putExtra(ProntuarioActivity.EXTRA_ID_PACIENTE, s.idPaciente);
                startActivity(intent);
            });

            containerSessoesHoje.addView(item);
        }
    }

    /** Mesmas cores das etiquetas de antes: laranja = pendente, verde = confirmada. */
    private static void pintarStatus(TextView tv, String status) {
        tv.setText(status.toLowerCase());
        if ("Confirmada".equals(status)) {
            tv.setBackgroundResource(R.drawable.bg_tag_green);
            tv.setTextColor(Color.parseColor("#1E7E4A"));
        } else if ("Realizada".equals(status)) {
            tv.setBackgroundResource(R.drawable.bg_tag_teal_light);
            tv.setTextColor(Color.parseColor("#2E8B82"));
        } else {
            tv.setBackgroundResource(R.drawable.bg_tag_orange);
            tv.setTextColor(Color.parseColor("#B85D10"));
        }
    }

    /** Valores do banco (sem acento) → texto da tela. */
    private static String nomeDoTipo(String tipo) {
        if ("Avaliacao".equals(tipo)) return "Avaliação";
        if ("Sessao".equals(tipo)) return "Sessão";
        return tipo;
    }

    private void mostrarMensagem(String texto) {
        containerSessoesHoje.removeAllViews();
        tvSemSessoes.setText(texto);
        tvSemSessoes.setVisibility(View.VISIBLE);
    }

    private void setupQuickAccessCards() {
        LinearLayout cardPacientes = findViewById(R.id.cardPacientes);
        if (cardPacientes != null) {
            cardPacientes.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PacienteActivity.class)));
        }

        LinearLayout cardAgenda = findViewById(R.id.cardAgenda);
        if (cardAgenda != null) {
            cardAgenda.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, AgendaActivity.class)));
        }

        LinearLayout cardAtendimentos = findViewById(R.id.cardAtendimentos);
        if (cardAtendimentos != null) {
            cardAtendimentos.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, AtendimentosActivity.class)));
        }

        LinearLayout cardDoucementos = findViewById(R.id.cardDocuementos);
        if (cardDoucementos != null) {
            cardDoucementos.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, EmissaoDocumentoActivity.class)));
        }
    }
}
