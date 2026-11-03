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

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.CadastroResponse;
import com.example.thinkbetter.api.HorarioOcupado;
import com.example.thinkbetter.api.NovaConsultaRequest;
import com.example.thinkbetter.api.PsicologoResumo;
import com.example.thinkbetter.api.Sessao;
import com.example.thinkbetter.api.StatusResponse;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Agendar Consulta (paciente).
 * Os horários continuam fixos (14h às 18h), mas agora:
 *  • quais estão ocupados vem do banco (GET /psicologos/{id}/horarios);
 *  • agendar grava a consulta (POST /consultas) e o horário fica indisponível
 *    para os outros pacientes;
 *  • a consulta aparece na tela inicial e na agenda do psicólogo escolhido.
 */
public class AgendarConsultaActivity extends MenuPacienteJava {

    /** Horários fixos oferecidos todos os dias. */
    private static final String[] HORARIOS_FIXOS = {"14:00", "15:00", "16:00", "17:00", "18:00"};

    /** Nome completo no banco → card da tela. */
    private static final String NOME_ANA = "Ana Souza";
    private static final String NOME_RAFAEL = "Rafael Costa";

    private LinearLayout cardProfissionalAna, cardProfissionalRafael;
    private LinearLayout containerHorariosDisponiveis;
    private RecyclerView recyclerMonthGridPaciente;
    private TextView tvMesAnoPaciente, tvDiaSelecionadoTitulo, tvDiaSelecionadoSub;
    private ImageButton btnMesAnterior, btnMesProximo;

    private String profissionalSelecionado = "Dr. Rafael Costa";
    private String nomeBancoSelecionado = NOME_RAFAEL;
    private String modalidadeSelecionada = "Presencial";
    private int currentMonth;
    private int currentYear;
    private int diaSelecionadoAtual;

    /** "Ana Souza" → ID_Psicologo (preenchido pelo GET /psicologos). */
    private final Map<String, Long> idPorNome = new HashMap<>();

    /** Evita que uma resposta antiga (de outro dia/psicólogo) sobrescreva a atual. */
    private int versaoDaConsulta = 0;

    private List<DiaCalendario> mesDiasList;
    private MonthGridAdapter monthGridAdapter;

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

        // Começa no dia de hoje (antes começava sempre no dia 1)
        Calendar hoje = Calendar.getInstance();
        currentMonth = hoje.get(Calendar.MONTH);
        currentYear = hoje.get(Calendar.YEAR);
        diaSelecionadoAtual = hoje.get(Calendar.DAY_OF_MONTH);

        // Alternância de Seleção de Profissional
        cardProfissionalAna.setOnClickListener(v -> selecionarProfissional(
                "Dra. Ana Souza", NOME_ANA, cardProfissionalAna, cardProfissionalRafael));
        cardProfissionalRafael.setOnClickListener(v -> selecionarProfissional(
                "Dr. Rafael Costa", NOME_RAFAEL, cardProfissionalRafael, cardProfissionalAna));

        btnMesAnterior.setOnClickListener(v -> mudarMes(-1));
        btnMesProximo.setOnClickListener(v -> mudarMes(1));

        recyclerMonthGridPaciente.setLayoutManager(new GridLayoutManager(this, 7));
        carregarCalendarioPaciente(currentYear, currentMonth);

        // Descobre o ID de cada psicólogo no banco e então carrega os horários
        carregarPsicologos();
    }

    // ------------------------------------------------------------------
    //  Psicólogos
    // ------------------------------------------------------------------

    /** GET /psicologos → liga os cards "Dra. Ana Souza" e "Dr. Rafael Costa" aos IDs do banco. */
    private void carregarPsicologos() {
        ApiClient.api().listarPsicologos().enqueue(new Callback<List<PsicologoResumo>>() {
            @Override
            public void onResponse(Call<List<PsicologoResumo>> call, Response<List<PsicologoResumo>> resposta) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    mostrarMensagemNosHorarios(ApiClient.mensagemDeErro(resposta));
                    return;
                }
                idPorNome.clear();
                for (PsicologoResumo p : resposta.body()) {
                    idPorNome.put(p.nome + " " + p.sobrenome, p.id);
                }
                carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear);
            }

            @Override
            public void onFailure(Call<List<PsicologoResumo>> call, Throwable t) {
                mostrarMensagemNosHorarios(ApiClient.mensagemDeFalha(t));
            }
        });
    }

    private void selecionarProfissional(String nomeTela, String nomeBanco,
                                        LinearLayout cardSelecionado, LinearLayout cardDesmarcado) {
        this.profissionalSelecionado = nomeTela;
        this.nomeBancoSelecionado = nomeBanco;
        cardSelecionado.setBackgroundResource(R.drawable.bg_card_outlined_selected);
        cardDesmarcado.setBackgroundResource(R.drawable.bg_card_outlined);
        carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear);
    }

    // ------------------------------------------------------------------
    //  Calendário
    // ------------------------------------------------------------------

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
            if (ehDiaPassado(diaSelecionado.getAno(), diaSelecionado.getMes(), diaSelecionado.getDiaDoMes())) {
                Toast.makeText(this, "Não é possível agendar consultas em datas passadas.", Toast.LENGTH_SHORT).show();
                return;
            }
            diaSelecionadoAtual = diaSelecionado.getDiaDoMes();
            carregarHorariosDoDia(diaSelecionadoAtual, mes, ano);
        });

        recyclerMonthGridPaciente.setAdapter(monthGridAdapter);

        // Ao trocar de mês, mantém o dia escolhido dentro do mês (ex.: 31 → 30)
        diaSelecionadoAtual = Math.min(diaSelecionadoAtual, maxDiasDoMes);
        carregarHorariosDoDia(diaSelecionadoAtual, mes, ano);
    }

    private static boolean ehDiaPassado(int ano, int mes, int dia) {
        Calendar hoje = Calendar.getInstance();
        hoje.set(Calendar.HOUR_OF_DAY, 0);
        hoje.set(Calendar.MINUTE, 0);
        hoje.set(Calendar.SECOND, 0);
        hoje.set(Calendar.MILLISECOND, 0);
        Calendar data = Calendar.getInstance();
        data.clear();
        data.set(ano, mes, dia);
        return data.before(hoje);
    }

    // ------------------------------------------------------------------
    //  Horários do dia
    // ------------------------------------------------------------------

    /** Busca no banco os horários ocupados do dia e desenha os horários fixos. */
    private void carregarHorariosDoDia(int dia, int mes, int ano) {
        String nomeMes = new DateFormatSymbols(new Locale("pt", "BR")).getMonths()[mes];
        tvDiaSelecionadoTitulo.setText("Dia " + dia);
        tvDiaSelecionadoSub.setText(nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1) + " de " + ano);

        Long idPsicologo = idPorNome.get(nomeBancoSelecionado);
        if (idPsicologo == null) {
            // Ainda carregando a lista, ou o psicólogo não está cadastrado no banco
            mostrarMensagemNosHorarios(idPorNome.isEmpty()
                    ? "Carregando horários..."
                    : profissionalSelecionado + " não está cadastrado(a) no sistema.");
            return;
        }

        if (ehDiaPassado(ano, mes, dia)) {
            mostrarMensagemNosHorarios("Escolha uma data a partir de hoje.");
            return;
        }

        String dataIso = String.format(Locale.US, "%04d-%02d-%02d", ano, mes + 1, dia);
        final int versao = ++versaoDaConsulta;

        ApiClient.api().horariosOcupados(idPsicologo, dataIso, Sessao.id(this))
                .enqueue(new Callback<List<HorarioOcupado>>() {
                    @Override
                    public void onResponse(Call<List<HorarioOcupado>> call, Response<List<HorarioOcupado>> resposta) {
                        if (versao != versaoDaConsulta) return; // o usuário já trocou de dia/psicólogo
                        if (!resposta.isSuccessful() || resposta.body() == null) {
                            mostrarMensagemNosHorarios(ApiClient.mensagemDeErro(resposta));
                            return;
                        }
                        desenharHorarios(resposta.body(), idPsicologo, dataIso, dia, mes, ano);
                    }

                    @Override
                    public void onFailure(Call<List<HorarioOcupado>> call, Throwable t) {
                        if (versao != versaoDaConsulta) return;
                        mostrarMensagemNosHorarios(ApiClient.mensagemDeFalha(t));
                    }
                });
    }

    private void desenharHorarios(List<HorarioOcupado> ocupados, long idPsicologo, String dataIso,
                                  int dia, int mes, int ano) {
        containerHorariosDisponiveis.removeAllViews();

        Map<String, HorarioOcupado> porHora = new HashMap<>();
        for (HorarioOcupado h : ocupados) porHora.put(h.hora, h);

        String dataTela = String.format(Locale.getDefault(), "%02d/%02d", dia, mes + 1);
        Calendar agora = Calendar.getInstance();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String hora : HORARIOS_FIXOS) {
            HorarioOcupado ocupado = porHora.get(hora);

            if (ocupado != null && ocupado.minha) {
                // Consulta do próprio paciente → toque para cancelar
                View slot = inflater.inflate(R.layout.item_slot_paciente_mine, containerHorariosDisponiveis, false);
                ((TextView) slot.findViewById(R.id.tvSlotHora)).setText(hora);
                ((TextView) slot.findViewById(R.id.tvSubtituloSlotMine)).setText(
                        "Sua consulta · " + ocupado.modalidade + " · " + ocupado.status);
                slot.setOnClickListener(v -> mostrarDialogCancelarConsulta(ocupado, dataTela, hora));
                containerHorariosDisponiveis.addView(slot);
                continue;
            }

            View slot = inflater.inflate(R.layout.item_slot_paciente_available, containerHorariosDisponiveis, false);
            TextView tvHora = slot.findViewById(R.id.tvSlotHora);
            TextView tvStatus = slot.findViewById(R.id.tvSlotStatus);
            TextView tvIcone = slot.findViewById(R.id.tvSlotIcone);
            tvHora.setText(hora);

            if (ocupado != null || jaPassou(agora, ano, mes, dia, hora)) {
                // Ocupado por outra pessoa (sem detalhes, por sigilo) ou horário de hoje que já passou
                tvStatus.setText(ocupado != null ? "Indisponível" : "Encerrado");
                tvIcone.setText("");
                slot.setAlpha(0.45f);
                slot.setClickable(false);
            } else {
                slot.setOnClickListener(v ->
                        mostrarDialogConfirmarAgendamento(idPsicologo, dataIso, hora, dataTela));
            }
            containerHorariosDisponiveis.addView(slot);
        }
    }

    private static boolean jaPassou(Calendar agora, int ano, int mes, int dia, String hora) {
        Calendar inicio = Calendar.getInstance();
        inicio.clear();
        inicio.set(ano, mes, dia, Integer.parseInt(hora.substring(0, 2)), Integer.parseInt(hora.substring(3, 5)));
        return !inicio.after(agora);
    }

    private void mostrarMensagemNosHorarios(String texto) {
        containerHorariosDisponiveis.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(Color.parseColor("#557777"));
        tv.setPadding(8, 24, 8, 24);
        containerHorariosDisponiveis.addView(tv);
    }

    // ------------------------------------------------------------------
    //  Agendar
    // ------------------------------------------------------------------

    private void mostrarDialogConfirmarAgendamento(long idPsicologo, String dataIso, String hora, String dataTela) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmar_agendamento);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvDetalhamento = dialog.findViewById(R.id.tvDialogDetalhamento);
        tvDetalhamento.setText("Agendar para " + dataTela + " às " + hora + " com " + profissionalSelecionado);

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
            btnConfirmar.setEnabled(false);
            NovaConsultaRequest dados = new NovaConsultaRequest(
                    Sessao.id(this), idPsicologo, dataIso + "T" + hora + ":00", modalidadeSelecionada);

            // POST /consultas
            ApiClient.api().agendarConsulta(dados).enqueue(new Callback<CadastroResponse>() {
                @Override
                public void onResponse(Call<CadastroResponse> call, Response<CadastroResponse> resposta) {
                    dialog.dismiss();
                    String msg = resposta.isSuccessful()
                            ? "Consulta (" + modalidadeSelecionada + ") agendada com " + profissionalSelecionado + "!"
                            : ApiClient.mensagemDeErro(resposta); // ex.: "Este horário acabou de ser ocupado."
                    Toast.makeText(AgendarConsultaActivity.this, msg, Toast.LENGTH_LONG).show();
                    carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear);
                }

                @Override
                public void onFailure(Call<CadastroResponse> call, Throwable t) {
                    btnConfirmar.setEnabled(true);
                    Toast.makeText(AgendarConsultaActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
                }
            });
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ------------------------------------------------------------------
    //  Cancelar
    // ------------------------------------------------------------------

    private void mostrarDialogCancelarConsulta(HorarioOcupado consulta, String dataTela, String hora) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancelar_consulta);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ((TextView) dialog.findViewById(R.id.tvDialogPsicologo)).setText(profissionalSelecionado);
        ((TextView) dialog.findViewById(R.id.tvDialogData)).setText(dataTela + "/" + currentYear);
        ((TextView) dialog.findViewById(R.id.tvDialogHorario)).setText(hora);

        Button btnSimCancelar = dialog.findViewById(R.id.btnSimCancelar);
        Button btnManterConsulta = dialog.findViewById(R.id.btnManterConsulta);

        btnSimCancelar.setOnClickListener(v -> {
            dialog.dismiss();
            // POST /consultas/{id}/cancelar
            ApiClient.api().cancelarConsulta(consulta.idConsulta, Sessao.id(this))
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> resposta) {
                            String msg = resposta.isSuccessful()
                                    ? "Consulta cancelada! Horário liberado."
                                    : ApiClient.mensagemDeErro(resposta);
                            Toast.makeText(AgendarConsultaActivity.this, msg, Toast.LENGTH_SHORT).show();
                            carregarHorariosDoDia(diaSelecionadoAtual, currentMonth, currentYear);
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            Toast.makeText(AgendarConsultaActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnManterConsulta.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
