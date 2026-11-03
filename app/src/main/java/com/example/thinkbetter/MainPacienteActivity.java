package com.example.thinkbetter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.ConsultaPaciente;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.Sessao;
import com.example.thinkbetter.api.StatusResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Tela inicial do PACIENTE: saudação, data de hoje e próximas consultas vindas do banco. */
public class MainPacienteActivity extends MenuPacienteJava {

    private TextView tvDate, tvGreeting, tvAvatarTop;
    private TextView tvContadorAtendimentos, tvSemAtendimentos;
    private LinearLayout containerAtendimentosPaciente;
    private Button btnAgendarNovaConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paciente);

        setupMenuButton(R.id.btnMenu);

        tvDate = findViewById(R.id.tvDate);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvAvatarTop = findViewById(R.id.tvAvatarTop);
        tvContadorAtendimentos = findViewById(R.id.tvContadorAtendimentos);
        tvSemAtendimentos = findViewById(R.id.tvSemAtendimentos);
        containerAtendimentosPaciente = findViewById(R.id.containerAtendimentosPaciente);
        btnAgendarNovaConsulta = findViewById(R.id.btnAgendarNovaConsulta);

        // Nome de quem fez login (guardado pela LoginActivity)
        String nome = Sessao.nome(this);
        tvGreeting.setText("Olá, " + Formatos.primeiroNome(nome));
        tvAvatarTop.setText(Formatos.iniciais(nome));

        // Ação para ir ao fluxo de agendamento
        btnAgendarNovaConsulta.setOnClickListener(v ->
                startActivity(new Intent(MainPacienteActivity.this, AgendarConsultaActivity.class)));
    }

    /**
     * Roda ao abrir a tela e toda vez que ela volta a aparecer
     * (ex.: voltando do Agendar Consulta): data e consultas sempre atualizadas.
     */
    @Override
    protected void onResume() {
        super.onResume();
        tvDate.setText(Formatos.hojePorExtenso());
        carregarConsultas();
    }

    /** GET /pacientes/{id}/consultas */
    private void carregarConsultas() {
        ApiClient.api().proximasConsultas(Sessao.id(this))
                .enqueue(new Callback<List<ConsultaPaciente>>() {
                    @Override
                    public void onResponse(Call<List<ConsultaPaciente>> call, Response<List<ConsultaPaciente>> resposta) {
                        if (!resposta.isSuccessful() || resposta.body() == null) {
                            mostrarMensagem(ApiClient.mensagemDeErro(resposta));
                            return;
                        }
                        mostrarConsultas(resposta.body());
                    }

                    @Override
                    public void onFailure(Call<List<ConsultaPaciente>> call, Throwable t) {
                        mostrarMensagem(ApiClient.mensagemDeFalha(t));
                    }
                });
    }

    private void mostrarConsultas(List<ConsultaPaciente> consultas) {
        containerAtendimentosPaciente.removeAllViews();
        tvContadorAtendimentos.setText(String.valueOf(consultas.size()));

        if (consultas.isEmpty()) {
            mostrarMensagem("Nenhum atendimento agendado no momento.");
            return;
        }
        tvSemAtendimentos.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (ConsultaPaciente c : consultas) {
            View item = inflater.inflate(R.layout.item_atendimento_paciente, containerAtendimentosPaciente, false);

            TextView tvIniciais = item.findViewById(R.id.tvIniciaisProfissional);
            TextView tvNome = item.findViewById(R.id.tvNomeProfissional);
            TextView tvDataHora = item.findViewById(R.id.tvDataHoraAtendimento);
            TextView tvModalidade = item.findViewById(R.id.tvModalidadeAtendimento);
            TextView tvStatus = item.findViewById(R.id.tvStatusAtendimento);
            Button btnIniciar = item.findViewById(R.id.btnIniciarChamada);
            Button btnConfirmar = item.findViewById(R.id.btnConfirmarPresenca);
            Button btnCancelar = item.findViewById(R.id.btnCancelarConsultaPaciente);

            tvIniciais.setText(Formatos.iniciais(c.psicologo));
            tvNome.setText(c.psicologo);
            tvDataHora.setText(Formatos.dataHoraCurta(c.inicio));
            tvModalidade.setText("♦ " + c.modalidade);
            tvStatus.setText(c.status);

            // Estilização do status (mesmas cores de antes)
            boolean confirmada = "Confirmada".equals(c.status);
            if (confirmada) {
                tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
                tvStatus.setTextColor(Color.parseColor("#1E7E4A"));
                btnConfirmar.setText("Confirmada ✓");
                btnConfirmar.setEnabled(false);
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
                tvStatus.setTextColor(Color.parseColor("#B85D10"));
                btnConfirmar.setText("Confirmar");
                btnConfirmar.setEnabled(true);
            }

            // Botão Iniciar: só para consultas Online; abre o link da chamada, se houver
            if ("Online".equals(c.modalidade)) {
                btnIniciar.setVisibility(View.VISIBLE);
                btnIniciar.setOnClickListener(v -> abrirChamada(c));
            } else {
                btnIniciar.setVisibility(View.GONE);
            }

            btnConfirmar.setOnClickListener(v -> confirmar(c));
            btnCancelar.setOnClickListener(v -> perguntarCancelamento(c));

            containerAtendimentosPaciente.addView(item);
        }
    }

    private void abrirChamada(ConsultaPaciente c) {
        if (c.linkOnline == null || c.linkOnline.isEmpty()) {
            Toast.makeText(this, "O link da chamada ainda não foi enviado pelo psicólogo.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(c.linkOnline)));
    }

    /** POST /consultas/{id}/confirmar */
    private void confirmar(ConsultaPaciente c) {
        ApiClient.api().confirmarConsulta(c.idConsulta, Sessao.id(this))
                .enqueue(callbackDeAcao("Presença confirmada com sucesso!"));
    }

    /** Usa o diálogo de cancelamento do app, agora com os dados da consulta. */
    private void perguntarCancelamento(ConsultaPaciente c) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancelar_consulta);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ((TextView) dialog.findViewById(R.id.tvDialogPsicologo)).setText(c.psicologo);
        ((TextView) dialog.findViewById(R.id.tvDialogData)).setText(Formatos.dataDeDataHora(c.inicio));
        ((TextView) dialog.findViewById(R.id.tvDialogHorario)).setText(Formatos.horaDeDataHora(c.inicio));

        dialog.findViewById(R.id.btnSimCancelar).setOnClickListener(v -> {
            dialog.dismiss();
            ApiClient.api().cancelarConsulta(c.idConsulta, Sessao.id(this))
                    .enqueue(callbackDeAcao("Consulta cancelada. O horário foi liberado."));
        });
        dialog.findViewById(R.id.btnManterConsulta).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /** Resposta de confirmar/cancelar: mostra a mensagem e recarrega a lista. */
    private Callback<StatusResponse> callbackDeAcao(String mensagemDeSucesso) {
        return new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> resposta) {
                String msg = resposta.isSuccessful() ? mensagemDeSucesso : ApiClient.mensagemDeErro(resposta);
                Toast.makeText(MainPacienteActivity.this, msg, Toast.LENGTH_SHORT).show();
                carregarConsultas();
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(MainPacienteActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void mostrarMensagem(String texto) {
        containerAtendimentosPaciente.removeAllViews();
        tvSemAtendimentos.setText(texto);
        tvSemAtendimentos.setVisibility(View.VISIBLE);
    }
}
