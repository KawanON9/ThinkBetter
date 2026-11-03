package com.example.thinkbetter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.SessaoAgenda;
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

public class AgendaActivity extends MenuJava {

    private Button btnTabAgenda, btnTabMes;
    private LinearLayout containerMes, containerAgendaDias;
    private RecyclerView recyclerMonthGrid, recyclerAtendimentosDia;
    private TextView tvMesAno, tvDiaSelecionadoTitulo, tvDiaSelecionadoSub;
    private ImageButton btnMesAnterior, btnMesProximo;

    private List<DiaCalendario> mesDiasList;
    private MonthGridAdapter monthGridAdapter;

    private int currentMonth;
    private int currentYear;

    /** Horários fixos exibidos em cada dia (os mesmos do Agendar Consulta). */
    private static final String[] HORARIOS_FIXOS = {"14:00", "15:00", "16:00", "17:00", "18:00"};

    /** Evita que a resposta de um mês antigo sobrescreva o mês que está na tela. */
    private int versaoDaConsulta = 0;

    /** Dia mostrado na aba Agenda. Começa no dia de hoje. */
    private int diaSelecionado = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        // Inicializa o menu lateral do psicólogo
        setupMenuPsicologoButton(R.id.btnMenu);

        btnTabAgenda = findViewById(R.id.btnTabAgenda);
        btnTabMes = findViewById(R.id.btnTabMes);
        containerMes = findViewById(R.id.containerMes);
        containerAgendaDias = findViewById(R.id.containerAgendaDias);
        recyclerMonthGrid = findViewById(R.id.recyclerMonthGrid);
        recyclerAtendimentosDia = findViewById(R.id.recyclerAtendimentosDia);
        tvMesAno = findViewById(R.id.tvMesAno);
        tvDiaSelecionadoTitulo = findViewById(R.id.tvDiaSelecionadoTitulo);
        tvDiaSelecionadoSub = findViewById(R.id.tvDiaSelecionadoSub);

        btnMesAnterior = findViewById(R.id.btnMesAnterior);
        btnMesProximo = findViewById(R.id.btnMesProximo);

        // Inicializa utilizando as funções criadas em DiaCalendario
        currentMonth = DiaCalendario.getMesAtual();
        currentYear = DiaCalendario.getAnoAtual();

        // Configuração das Abas (Alternância Mês vs Agenda)
        btnTabAgenda.setOnClickListener(v -> alternarAbas(false));
        btnTabMes.setOnClickListener(v -> alternarAbas(true));

        // Navegação entre os meses
        if (btnMesAnterior != null) {
            btnMesAnterior.setOnClickListener(v -> mudarMes(-1));
        }
        if (btnMesProximo != null) {
            btnMesProximo.setOnClickListener(v -> mudarMes(1));
        }

        // Inicializar Calendário Mensal Dinâmico
        recyclerMonthGrid.setLayoutManager(new GridLayoutManager(this, 7));

        // Configurar lista de atendimentos do dia
        recyclerAtendimentosDia.setLayoutManager(new LinearLayoutManager(this));
    }

    /** Recarrega ao voltar para a tela: consultas marcadas pelos pacientes aparecem na hora. */
    @Override
    protected void onResume() {
        super.onResume();
        carregarMesDinamico(currentYear, currentMonth);
    }

    private void alternarAbas(boolean isMes) {
        if (isMes) {
            // Aba Mês selecionada
            btnTabMes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E8B82")));
            btnTabMes.setTextColor(Color.WHITE);

            btnTabAgenda.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnTabAgenda.setTextColor(Color.parseColor("#557777"));

            containerMes.setVisibility(View.VISIBLE);
            containerAgendaDias.setVisibility(View.GONE);
        } else {
            // Aba Agenda selecionada
            btnTabAgenda.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E8B82")));
            btnTabAgenda.setTextColor(Color.WHITE);

            btnTabMes.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnTabMes.setTextColor(Color.parseColor("#557777"));

            containerAgendaDias.setVisibility(View.VISIBLE);
            containerMes.setVisibility(View.GONE);
        }
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
        // Voltando ao mês atual → dia de hoje; outros meses → dia 1
        Calendar hoje = Calendar.getInstance();
        boolean mesAtual = currentMonth == hoje.get(Calendar.MONTH) && currentYear == hoje.get(Calendar.YEAR);
        diaSelecionado = mesAtual ? hoje.get(Calendar.DAY_OF_MONTH) : 1;
        carregarMesDinamico(currentYear, currentMonth);
    }

    /**
     * GET /psicologos/{id}/agenda?de=1º dia&ate=último dia → monta o mês.
     * Cada dia mostra os horários fixos: os que têm consulta no banco aparecem
     * com o paciente; os demais aparecem como "Livre".
     */
    private void carregarMesDinamico(int ano, int mes) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(ano, mes, 1);
        int maxDiasDoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String de = String.format(Locale.US, "%04d-%02d-01", ano, mes + 1);
        String ate = String.format(Locale.US, "%04d-%02d-%02d", ano, mes + 1, maxDiasDoMes);

        final int versao = ++versaoDaConsulta;
        ApiClient.api().agendaDoPeriodo(Sessao.id(this), de, ate)
                .enqueue(new Callback<List<SessaoAgenda>>() {
                    @Override
                    public void onResponse(Call<List<SessaoAgenda>> call, Response<List<SessaoAgenda>> resposta) {
                        if (versao != versaoDaConsulta) return;
                        if (!resposta.isSuccessful() || resposta.body() == null) {
                            Toast.makeText(AgendaActivity.this, ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                            montarMes(ano, mes, new ArrayList<>());
                            return;
                        }
                        montarMes(ano, mes, resposta.body());
                    }

                    @Override
                    public void onFailure(Call<List<SessaoAgenda>> call, Throwable t) {
                        if (versao != versaoDaConsulta) return;
                        Toast.makeText(AgendaActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
                        montarMes(ano, mes, new ArrayList<>());
                    }
                });
    }

    private void montarMes(int ano, int mes, List<SessaoAgenda> sessoes) {
        // "2026-11-04 15:00" → sessão do banco
        Map<String, SessaoAgenda> porDataHora = new HashMap<>();
        for (SessaoAgenda s : sessoes) porDataHora.put(s.data + " " + s.hora, s);

        mesDiasList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(ano, mes, 1);

        String nomeMes = new DateFormatSymbols(new Locale("pt", "BR")).getMonths()[mes];
        String nomeMesCapitalizado = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);
        tvMesAno.setText(nomeMesCapitalizado + " " + ano);

        int primeiroDiaDaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDiasDoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Preencher dias do mês anterior para ajuste da grade
        cal.add(Calendar.MONTH, -1);
        int maxDiasMesAnterior = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = primeiroDiaDaSemana - 1; i >= 0; i--) {
            mesDiasList.add(new DiaCalendario(maxDiasMesAnterior - i, mes == 0 ? 11 : mes - 1, mes == 0 ? ano - 1 : ano, false, null));
        }

        // Dias do mês atual: horários fixos + consultas reais do banco
        for (int i = 1; i <= maxDiasDoMes; i++) {
            List<Atendimento> consultasDoDia = new ArrayList<>();
            String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d", i, (mes + 1));
            String dataIso = String.format(Locale.US, "%04d-%02d-%02d", ano, mes + 1, i);

            for (String hora : HORARIOS_FIXOS) {
                SessaoAgenda s = porDataHora.get(dataIso + " " + hora);
                if (s != null) {
                    boolean confirmada = "Confirmada".equals(s.status);
                    consultasDoDia.add(new Atendimento(s.idConsulta, hora, dataFormatada, s.nomePaciente,
                            nomeDoTipo(s.tipo) + " · " + s.plano + " · " + s.modalidade,
                            s.status.toLowerCase(), confirmada ? 1 : 0));
                } else {
                    consultasDoDia.add(new Atendimento(hora, dataFormatada, "Livre", "Horário disponível", "livre", 2));
                }
            }

            mesDiasList.add(new DiaCalendario(i, mes, ano, true, consultasDoDia));
        }

        // Configuração do clique nos dias do calendário mensal
        monthGridAdapter = new MonthGridAdapter(mesDiasList, dia -> {
            if (!dia.isEDoMesAtual()) return; // dias cinza do mês anterior
            diaSelecionado = dia.getDiaDoMes();
            mostrarDia(dia, nomeMesCapitalizado, ano);
            alternarAbas(false); // Alterna para a aba Agenda/Dias
        });

        recyclerMonthGrid.setAdapter(monthGridAdapter);

        // Abre já mostrando o dia selecionado (hoje, na primeira vez)
        int dia = Math.min(diaSelecionado, maxDiasDoMes);
        for (DiaCalendario d : mesDiasList) {
            if (d.isEDoMesAtual() && d.getDiaDoMes() == dia) {
                mostrarDia(d, nomeMesCapitalizado, ano);
                break;
            }
        }
    }

    /** Marca o dia no calendário e mostra seus horários na aba Agenda. */
    private void mostrarDia(DiaCalendario dia, String nomeMesCapitalizado, int ano) {
        for (DiaCalendario d : mesDiasList) d.setSelecionado(d == dia);
        monthGridAdapter.notifyDataSetChanged();

        Calendar hoje = Calendar.getInstance();
        boolean ehHoje = dia.getDiaDoMes() == hoje.get(Calendar.DAY_OF_MONTH)
                && dia.getMes() == hoje.get(Calendar.MONTH) && dia.getAno() == hoje.get(Calendar.YEAR);
        tvDiaSelecionadoTitulo.setText((ehHoje ? "Hoje · " : "") + "Dia " + dia.getDiaDoMes());
        tvDiaSelecionadoSub.setText(nomeMesCapitalizado + " de " + ano);

        recyclerAtendimentosDia.setAdapter(new AgendaAtendimentoAdapter(dia.getAtendimentos(), this::confirmarAtendimento));
    }

    /** POST /consultas/{id}/confirmar?psicologo={id} e recarrega o mês (mantendo o dia). */
    private void confirmarAtendimento(Atendimento atendimento) {
        ApiClient.api().confirmarConsultaComoPsicologo(atendimento.getIdConsulta(), Sessao.id(this))
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> resposta) {
                        String msg = resposta.isSuccessful()
                                ? "Atendimento de " + atendimento.getNomePaciente() + " confirmado!"
                                : ApiClient.mensagemDeErro(resposta);
                        Toast.makeText(AgendaActivity.this, msg, Toast.LENGTH_SHORT).show();
                        carregarMesDinamico(currentYear, currentMonth);
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        Toast.makeText(AgendaActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
                        carregarMesDinamico(currentYear, currentMonth);
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
