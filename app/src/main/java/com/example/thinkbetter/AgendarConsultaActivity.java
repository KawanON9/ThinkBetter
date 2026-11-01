package com.example.thinkbetter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgendarConsultaActivity extends MenuPacienteJava {

    private LinearLayout cardProfissionalAna, cardProfissionalRafael;
    private LinearLayout containerHorariosDisponiveis;
    private RecyclerView recyclerMonthGridPaciente;
    private TextView tvMesAnoPaciente, tvDiaSelecionadoTitulo, tvDiaSelecionadoSub;
    private ImageButton btnMesAnterior, btnMesProximo;

    private String profissionalSelecionado = "Dr. Rafael Costa";
    private String modalidadeSelecionada = "Presencial";
    private int currentMonth;
    private int currentYear;
    private int diaSelecionadoAtual = 1;

    private List<DiaCalendario> mesDiasList;
    private MonthGridAdapter monthGridAdapter;

    // Mapa em memória para guardar dinamicamente o status agendado de cada dia/horário (Ex: "1_14:00" -> "Presencial")
    private final Map<String, String> agendamentosUsuarioMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_consulta);

        setupMenuButton(R.id.btnMenu);

        cardProfissionalAna = findViewById(R.id.cardProfissionalAna);
        cardProfissionalRafael = findViewById(R.id.cardProfissionalRafael);
        containerHorariosDisponiveis = findViewById(R.id.containerHorariosDisponiveis);
        recyclerMonthGridPaciente = findViewById(R.id.recyclerMonthGridPaciente);
        tvMesAnoPaciente = findViewById(R.id.tvMesAnoPaciente);
        tvDiaSelecionadoTitulo = findViewById(R.id.tvDiaSelecionadoTitulo);
        tvDiaSelecionadoSub = findViewById(R.id.tvDiaSelecionadoSub);
        btnMesAnterior = findViewById(R.id.btnMesAnterior);
        btnMesProximo = findViewById(R.id.btnMesProximo);

        currentMonth = DiaCalendario.getMesAtual();
        currentYear = DiaCalendario.getAnoAtual();

        // Agendamento padrão inicial de teste (Dia 1 às 17:00 Presencial)
        agendamentosUsuarioMap.put("1_17:00", "Presencial");

        // Alternância de Seleção de Profissional
        cardProfissionalAna.setOnClickListener(v -> selecionarProfissional("Dra. Ana Souza", cardProfissionalAna, cardProfissionalRafael));
        cardProfissionalRafael.setOnClickListener(v -> selecionarProfissional("Dr. Rafael Costa", cardProfissionalRafael, cardProfissionalAna));

        btnMesAnterior.setOnClickListener(v -> mudarMes(-1));
        btnMesProximo.setOnClickListener(v -> mudarMes(1));

        recyclerMonthGridPaciente.setLayoutManager(new GridLayoutManager(this, 7));
        carregarCalendarioPaciente(currentYear, currentMonth);
    }

    private void selecionarProfissional(String nome, LinearLayout cardSelecionado, LinearLayout cardDesmarcado) {
        this.profissionalSelecionado = nome;
        cardSelecionado.setBackgroundResource(R.drawable.bg_card_outlined_selected);
        cardDesmarcado.setBackgroundResource(R.drawable.bg_card_outlined);
        carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear);
    }

    private void mudarMes(int direcao) {
        currentMonth += direcao;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        } else if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        carregarCalendarioPaciente(currentYear, currentMonth);
    }

    private void carregarCalendarioPaciente(int ano, int mes) {
        mesDiasList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(ano, mes, 1);

        String nomeMes = new DateFormatSymbols(new Locale("pt", "BR")).getMonths()[mes];
        String nomeMesCapitalizado = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);
        tvMesAnoPaciente.setText(nomeMesCapitalizado + " " + ano);

        int primeiroDiaDaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDiasDoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.add(Calendar.MONTH, -1);
        int maxDiasMesAnterior = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = primeiroDiaDaSemana - 1; i >= 0; i--) {
            mesDiasList.add(new DiaCalendario(maxDiasMesAnterior - i, mes == 0 ? 11 : mes - 1, mes == 0 ? ano - 1 : ano, false, null));
        }

        for (int i = 1; i <= maxDiasDoMes; i++) {
            mesDiasList.add(new DiaCalendario(i, mes, ano, true, null));
        }

        monthGridAdapter = new MonthGridAdapter(mesDiasList, diaSelecionado -> {
            Calendar hoje = Calendar.getInstance();
            Calendar dataSelecionada = Calendar.getInstance();
            dataSelecionada.set(diaSelecionado.getAno(), diaSelecionado.getMes(), diaSelecionado.getDiaDoMes());

            if (dataSelecionada.before(hoje) && (diaSelecionado.getDiaDoMes() != hoje.get(Calendar.DAY_OF_MONTH))) {
                Toast.makeText(this, "Não é possível agendar consultas em datas passadas.", Toast.LENGTH_SHORT).show();
                return;
            }

            diaSelecionadoAtual = diaSelecionado.getDiaDoMes();
            tvDiaSelecionadoTitulo.setText("Dia " + diaSelecionadoAtual);
            tvDiaSelecionadoSub.setText(nomeMesCapitalizado + " de " + ano);

            carregarHorariosDoDia(diaSelecionadoAtual, mes, ano);
        });

        recyclerMonthGridPaciente.setAdapter(monthGridAdapter);
        carregarHorariosDoDia(diaSelecionadoAtual, mes, ano);
    }

    private void carregarHorariosDoDia(int dia, int mes, int ano) {
        containerHorariosDisponiveis.removeAllViews();
        String[] horariosFixos = {"14:00", "15:00", "16:00", "17:00", "18:00"};

        for (String hora : horariosFixos) {
            String chaveSlot = dia + "_" + hora;
            boolean isOcupadoPorOutro = (dia == 1 && hora.equals("14:00")) || (dia == 10 && hora.equals("17:00"));
            boolean isConsultaDoUsuario = agendamentosUsuarioMap.containsKey(chaveSlot);

            if (isConsultaDoUsuario) {
                // Exibe o slot formatado para o agendamento do próprio usuário
                View viewSlot = LayoutInflater.from(this).inflate(R.layout.item_slot_paciente_mine, containerHorariosDisponiveis, false);
                TextView tvHora = viewSlot.findViewById(R.id.tvSlotHora);
                TextView tvSubtitulo = viewSlot.findViewById(R.id.tvSubtituloSlotMine);

                tvHora.setText(hora);
                String mod = agendamentosUsuarioMap.get(chaveSlot);
                tvSubtitulo.setText("Sua consulta · " + (mod != null ? mod : "Presencial"));

                viewSlot.setOnClickListener(v -> mostrarDialogCancelarConsulta(chaveSlot));
                containerHorariosDisponiveis.addView(viewSlot);
            } else if (!isOcupadoPorOutro) {
                // Exibe o slot livre para novo agendamento
                View viewSlot = LayoutInflater.from(this).inflate(R.layout.item_slot_paciente_available, containerHorariosDisponiveis, false);
                TextView tvHora = viewSlot.findViewById(R.id.tvSlotHora);
                tvHora.setText(hora);

                String dataStr = String.format(Locale.getDefault(), "%02d/%02d", dia, (mes + 1));
                viewSlot.setOnClickListener(v -> mostrarDialogConfirmarAgendamento(chaveSlot, hora, dataStr));
                containerHorariosDisponiveis.addView(viewSlot);
            }
        }
    }

    private void mostrarDialogConfirmarAgendamento(String chaveSlot, String hora, String data) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmar_agendamento);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvDetalhamento = dialog.findViewById(R.id.tvDialogDetalhamento);
        tvDetalhamento.setText("Agendar para " + data + " às " + hora + " com " + profissionalSelecionado);

        Button btnPresencial = dialog.findViewById(R.id.btnModalidadePresencial);
        Button btnOnline = dialog.findViewById(R.id.btnModalidadeOnline);
        Button btnConfirmar = dialog.findViewById(R.id.btnConfirmarAgendamento);
        Button btnCancelar = dialog.findViewById(R.id.btnCancelarDialog);

        modalidadeSelecionada = "Presencial"; // Padrão

        btnPresencial.setOnClickListener(v -> {
            modalidadeSelecionada = "Presencial";
            btnPresencial.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
            btnPresencial.setTextColor(Color.WHITE);
            btnOnline.setBackgroundColor(Color.TRANSPARENT);
            btnOnline.setTextColor(Color.parseColor("#557777"));
        });

        btnOnline.setOnClickListener(v -> {
            modalidadeSelecionada = "Online";
            btnOnline.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
            btnOnline.setTextColor(Color.WHITE);
            btnPresencial.setBackgroundColor(Color.TRANSPARENT);
            btnPresencial.setTextColor(Color.parseColor("#557777"));
        });

        btnConfirmar.setOnClickListener(v -> {
            agendamentosUsuarioMap.put(chaveSlot, modalidadeSelecionada);
            Toast.makeText(AgendarConsultaActivity.this, "Consulta (" + modalidadeSelecionada + ") agendada!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear);
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarDialogCancelarConsulta(String chaveSlot) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancelar_consulta);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnSimCancelar = dialog.findViewById(R.id.btnSimCancelar);
        Button btnManterConsulta = dialog.findViewById(R.id.btnManterConsulta);

        btnSimCancelar.setOnClickListener(v -> {
            agendamentosUsuarioMap.remove(chaveSlot); // Remove o agendamento
            Toast.makeText(AgendarConsultaActivity.this, "Consulta cancelada! Horário liberado.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear); // Atualiza a lista trazendo o slot como Disponível
        });

        btnManterConsulta.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
