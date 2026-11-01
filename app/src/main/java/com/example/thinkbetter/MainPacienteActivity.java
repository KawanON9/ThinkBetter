package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainPacienteActivity extends MenuPacienteJava {

    private TextView tvDate, tvContadorAtendimentos, tvSemAtendimentos;
    private LinearLayout containerAtendimentosPaciente;
    private Button btnAgendarNovaConsulta;

    // Classe interna para estruturar os dados do atendimento do paciente
    public static class AtendimentoPaciente {
        String profissional;
        String iniciais;
        String dataHora;
        String modalidade; // "Online" ou "Presencial"
        String status;     // "Pendente" ou "Confirmado"

        public AtendimentoPaciente(String profissional, String iniciais, String dataHora, String modalidade, String status) {
            this.profissional = profissional;
            this.iniciais = iniciais;
            this.dataHora = dataHora;
            this.modalidade = modalidade;
            this.status = status;
        }
    }

    private List<AtendimentoPaciente> listaAtendimentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paciente);

        setupMenuButton(R.id.btnMenu);

        tvDate = findViewById(R.id.tvDate);
        tvContadorAtendimentos = findViewById(R.id.tvContadorAtendimentos);
        tvSemAtendimentos = findViewById(R.id.tvSemAtendimentos);
        containerAtendimentosPaciente = findViewById(R.id.containerAtendimentosPaciente);
        btnAgendarNovaConsulta = findViewById(R.id.btnAgendarNovaConsulta);

        // 1. Exibir data atual formatada dinamicamente
        exibirDataAtual();

        // 2. Popular dados iniciais de exemplo para o paciente
        if (listaAtendimentos.isEmpty()) {
            listaAtendimentos.add(new AtendimentoPaciente("Dra. Ana Souza", "AS", "Ter, 27 de out · 10:00", "Online", "Confirmado"));
            listaAtendimentos.add(new AtendimentoPaciente("Dr. Rafael Costa", "RC", "Qui, 29 de out · 14:00", "Presencial", "Pendente"));
        }

        // 3. Renderizar dinamicamente os atendimentos
        atualizarTelaAtendimentos();

        // 4. Ação para ir ao fluxo de agendamento
        btnAgendarNovaConsulta.setOnClickListener(v -> {
            Intent intent = new Intent(MainPacienteActivity.this, AgendarConsultaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza a tela caso o paciente retorne da tela de agendamento
        atualizarTelaAtendimentos();
    }

    private void exibirDataAtual() {
        if (tvDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
            String dataFormatada = sdf.format(Calendar.getInstance().getTime());
            if (!dataFormatada.isEmpty()) {
                dataFormatada = dataFormatada.substring(0, 1).toUpperCase() + dataFormatada.substring(1);
            }
            tvDate.setText(dataFormatada);
        }
    }

    private void atualizarTelaAtendimentos() {
        containerAtendimentosPaciente.removeAllViews();
        tvContadorAtendimentos.setText(String.valueOf(listaAtendimentos.size()));

        if (listaAtendimentos.isEmpty()) {
            tvSemAtendimentos.setVisibility(View.VISIBLE);
            return;
        }

        tvSemAtendimentos.setVisibility(View.GONE);

        for (int i = 0; i < listaAtendimentos.size(); i++) {
            final int index = i;
            AtendimentoPaciente atendimento = listaAtendimentos.get(i);

            View viewItem = LayoutInflater.from(this).inflate(R.layout.item_atendimento_paciente, containerAtendimentosPaciente, false);

            TextView tvIniciais = viewItem.findViewById(R.id.tvIniciaisProfissional);
            TextView tvNome = viewItem.findViewById(R.id.tvNomeProfissional);
            TextView tvDataHora = viewItem.findViewById(R.id.tvDataHoraAtendimento);
            TextView tvModalidade = viewItem.findViewById(R.id.tvModalidadeAtendimento);
            TextView tvStatus = viewItem.findViewById(R.id.tvStatusAtendimento);
            Button btnIniciar = viewItem.findViewById(R.id.btnIniciarChamada);
            Button btnConfirmar = viewItem.findViewById(R.id.btnConfirmarPresenca);
            Button btnCancelar = viewItem.findViewById(R.id.btnCancelarConsultaPaciente);

            tvIniciais.setText(atendimento.iniciais);
            tvNome.setText(atendimento.profissional);
            tvDataHora.setText(atendimento.dataHora);
            tvModalidade.setText("♦ " + atendimento.modalidade);
            tvStatus.setText(atendimento.status);

            // Estilização dinâmica do status
            if (atendimento.status.equalsIgnoreCase("Confirmado")) {
                tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
                tvStatus.setTextColor(android.graphics.Color.parseColor("#1E7E4A"));
                btnConfirmar.setText("Confirmado ✓");
                btnConfirmar.setEnabled(false);
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
                tvStatus.setTextColor(android.graphics.Color.parseColor("#B85D10"));
                btnConfirmar.setText("Confirmar");
                btnConfirmar.setEnabled(true);
            }

            // Regra do Botão Iniciar: Exibe APENAS se for Online
            if (atendimento.modalidade.equalsIgnoreCase("Online")) {
                btnIniciar.setVisibility(View.VISIBLE);
                btnIniciar.setOnClickListener(v -> {
                    Toast.makeText(MainPacienteActivity.this, "Redirecionando para a chamada do Google Meet...", Toast.LENGTH_SHORT).show();
                });
            } else {
                btnIniciar.setVisibility(View.GONE);
            }

            // Ação de Confirmar Presença
            btnConfirmar.setOnClickListener(v -> {
                atendimento.status = "Confirmado";
                Toast.makeText(MainPacienteActivity.this, "Presença confirmada com sucesso!", Toast.LENGTH_SHORT).show();
                atualizarTelaAtendimentos();
            });

            // Ação de Cancelar Consulta
            btnCancelar.setOnClickListener(v -> {
                listaAtendimentos.remove(index);
                Toast.makeText(MainPacienteActivity.this, "Consulta cancelada.", Toast.LENGTH_SHORT).show();
                atualizarTelaAtendimentos();
            });

            containerAtendimentosPaciente.addView(viewItem);
        }
    }
}
