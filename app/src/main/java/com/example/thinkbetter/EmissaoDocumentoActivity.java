package com.example.thinkbetter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.DocumentoGerado;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.NovoDocumentoRequest;
import com.example.thinkbetter.api.PacienteResumo;
import com.example.thinkbetter.api.Sessao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Emissão de Documentos (psicólogo), em 5 etapas:
 *  1. paciente (lista do banco)  2. tipo  3. período, CID e descrição
 *  4. prévia montada pela API    5. documento gravado → enviar por WhatsApp
 */
public class EmissaoDocumentoActivity extends MenuJava {

    private FrameLayout containerEtapas;
    private TextView step1, step2, step3, step4, step5, tvRecomecar;

    // Estado do documento que está sendo criado (mantido ao voltar para "Editar")
    private PacienteResumo pacienteSelecionado;
    private String tipoDocumentoSelecionado = "";   // Atestado | Declaracao | Laudo
    private String periodoDigitado = "";
    private String cidDigitado = "";
    private String descricaoDigitada = "";
    private NovoDocumentoRequest pedidoAtual;        // usado na prévia e na emissão
    private DocumentoGerado documentoEmitido;
    private int etapaAtual = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_documento);

        setupMenuPsicologoButton(R.id.btnMenu);

        containerEtapas = findViewById(R.id.containerEtapas);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);
        step5 = findViewById(R.id.step5);
        tvRecomecar = findViewById(R.id.tvRecomecar);

        // Avatar com as iniciais de quem está logado
        TextView tvAvatar = findViewById(R.id.tvAvatarTop);
        if (tvAvatar != null) tvAvatar.setText(Formatos.iniciais(Sessao.nome(this)));

        tvRecomecar.setOnClickListener(v -> recomecar());

        // Começa na Etapa 1
        irParaEtapa(1);
    }

    private void recomecar() {
        pacienteSelecionado = null;
        tipoDocumentoSelecionado = "";
        periodoDigitado = "";
        cidDigitado = "";
        descricaoDigitada = "";
        pedidoAtual = null;
        documentoEmitido = null;
        irParaEtapa(1);
    }

    /** Voltar nas etapas 2 a 4 = etapa anterior. Na 1 e na 5 (já emitido), sai da tela. */
    @Override
    protected boolean tratarVoltar() {
        if (etapaAtual >= 2 && etapaAtual <= 4) {
            irParaEtapa(etapaAtual - 1);
            return true;
        }
        return false;
    }

    public void irParaEtapa(int numeroEtapa) {
        etapaAtual = numeroEtapa;
        containerEtapas.removeAllViews();
        atualizarSteppersVisuais(numeroEtapa);

        LayoutInflater inflater = LayoutInflater.from(this);
        View viewEtapa;

        switch (numeroEtapa) {
            case 1:
                viewEtapa = inflater.inflate(R.layout.etapa_1_documento, containerEtapas, false);
                carregarPacientes(viewEtapa);
                break;

            case 2:
                viewEtapa = inflater.inflate(R.layout.etapa_2_documento, containerEtapas, false);
                viewEtapa.findViewById(R.id.cardAtestado).setOnClickListener(v -> escolherTipo("Atestado"));
                viewEtapa.findViewById(R.id.cardDeclaracao).setOnClickListener(v -> escolherTipo("Declaracao"));
                viewEtapa.findViewById(R.id.cardLaudo).setOnClickListener(v -> escolherTipo("Laudo"));
                break;

            case 3:
                viewEtapa = inflater.inflate(R.layout.etapa_3_documento, containerEtapas, false);
                configurarEtapa3(viewEtapa);
                break;

            case 4:
                viewEtapa = inflater.inflate(R.layout.etapa_4_documento, containerEtapas, false);
                configurarEtapa4(viewEtapa);
                break;

            case 5:
                viewEtapa = inflater.inflate(R.layout.etapa_5_documento, containerEtapas, false);
                configurarEtapa5(viewEtapa);
                break;

            default:
                return;
        }

        containerEtapas.addView(viewEtapa);
    }

    // ------------------------------------------------------------------
    //  Etapa 1 — paciente
    // ------------------------------------------------------------------

    /** GET /pacientes → um card por paciente. */
    private void carregarPacientes(View etapa) {
        TextView tvMensagem = etapa.findViewById(R.id.tvMensagemPacientesDoc);
        LinearLayout container = etapa.findViewById(R.id.containerPacientesDoc);

        ApiClient.api().listarPacientes().enqueue(new Callback<List<PacienteResumo>>() {
            @Override
            public void onResponse(Call<List<PacienteResumo>> call, Response<List<PacienteResumo>> resposta) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    tvMensagem.setText(ApiClient.mensagemDeErro(resposta));
                    return;
                }
                List<PacienteResumo> pacientes = resposta.body();
                if (pacientes.isEmpty()) {
                    tvMensagem.setText("Nenhum paciente cadastrado ainda.");
                    return;
                }
                tvMensagem.setText("Selecione o paciente:");

                LayoutInflater inflater = LayoutInflater.from(EmissaoDocumentoActivity.this);
                for (PacienteResumo p : pacientes) {
                    View card = inflater.inflate(R.layout.item_paciente_documento, container, false);
                    String nome = p.nome + " " + p.sobrenome;
                    ((TextView) card.findViewById(R.id.tvIniciaisPacienteDoc)).setText(Formatos.iniciais(nome));
                    ((TextView) card.findViewById(R.id.tvNomePacienteDoc)).setText(nome);
                    ((TextView) card.findViewById(R.id.tvPlanoPacienteDoc)).setText(p.plano);
                    card.setOnClickListener(v -> {
                        pacienteSelecionado = p;
                        irParaEtapa(2);
                    });
                    container.addView(card);
                }
            }

            @Override
            public void onFailure(Call<List<PacienteResumo>> call, Throwable t) {
                tvMensagem.setText(ApiClient.mensagemDeFalha(t));
            }
        });
    }

    // ------------------------------------------------------------------
    //  Etapa 2 — tipo
    // ------------------------------------------------------------------

    private void escolherTipo(String tipo) {
        if (!tipo.equals(tipoDocumentoSelecionado) && "Declaracao".equals(tipo)) {
            cidDigitado = ""; // declaração não leva CID
        }
        tipoDocumentoSelecionado = tipo;
        irParaEtapa(3);
    }

    /** Valor do banco (sem acento) → texto da tela. */
    private static String nomeDoTipo(String tipo) {
        if ("Declaracao".equals(tipo)) return "Declaração";
        return tipo;
    }

    // ------------------------------------------------------------------
    //  Etapa 3 — período, CID e descrição
    // ------------------------------------------------------------------

    private void configurarEtapa3(View etapa) {
        if (pacienteSelecionado == null) { irParaEtapa(1); return; }

        TextView tvResumo = etapa.findViewById(R.id.tvResumoEtapa3);
        EditText edtPeriodo = etapa.findViewById(R.id.edtPeriodo);
        TextView tvLabelCid = etapa.findViewById(R.id.tvLabelCid);
        EditText edtCid = etapa.findViewById(R.id.edtCid);
        TextView tvLabelDescricao = etapa.findViewById(R.id.tvLabelDescricao);
        EditText edtDescricao = etapa.findViewById(R.id.edtDescricao);

        tvResumo.setText("👤 " + pacienteSelecionado.nome + " " + pacienteSelecionado.sobrenome
                + "  ·  " + nomeDoTipo(tipoDocumentoSelecionado));

        // Declaração informa só o comparecimento: sem campo de CID
        boolean declaracao = "Declaracao".equals(tipoDocumentoSelecionado);
        tvLabelCid.setVisibility(declaracao ? View.GONE : View.VISIBLE);
        edtCid.setVisibility(declaracao ? View.GONE : View.VISIBLE);

        // Laudo exige descrição técnica
        boolean laudo = "Laudo".equals(tipoDocumentoSelecionado);
        tvLabelDescricao.setText(laudo ? "DESCRIÇÃO TÉCNICA (OBRIGATÓRIA)" : "DESCRIÇÃO / JUSTIFICATIVA (OPCIONAL)");

        // Mantém o que já foi digitado (ex.: voltando pelo "Editar")
        edtPeriodo.setText(periodoDigitado);
        edtCid.setText(cidDigitado);
        edtDescricao.setText(descricaoDigitada);

        etapa.findViewById(R.id.btnPreVisualizar).setOnClickListener(v -> {
            periodoDigitado = edtPeriodo.getText().toString().trim();
            cidDigitado = declaracao ? "" : edtCid.getText().toString().trim().toUpperCase(Locale.ROOT);
            descricaoDigitada = edtDescricao.getText().toString().trim();

            String[] periodo = interpretarPeriodo(periodoDigitado);
            if (periodo == null) {
                edtPeriodo.setError("Use dd/mm/aaaa ou dd/mm/aaaa a dd/mm/aaaa");
                return;
            }
            if (!cidDigitado.isEmpty() && !cidDigitado.matches("[A-Z][0-9]{2}(\\.[0-9]{1,2})?")) {
                edtCid.setError("CID inválido (ex.: F41.1)");
                return;
            }
            if (laudo && descricaoDigitada.length() < 20) {
                edtDescricao.setError("Descreva a avaliação (mínimo de 20 caracteres)");
                return;
            }

            pedidoAtual = new NovoDocumentoRequest();
            pedidoAtual.idPsicologo = Sessao.id(this);
            pedidoAtual.idPaciente = pacienteSelecionado.id;
            pedidoAtual.tipo = tipoDocumentoSelecionado;
            pedidoAtual.periodoInicio = periodo[0];
            pedidoAtual.periodoFim = periodo[1];
            pedidoAtual.cid = cidDigitado.isEmpty() ? null : cidDigitado;
            pedidoAtual.descricao = descricaoDigitada.isEmpty() ? null : descricaoDigitada;
            irParaEtapa(4);
        });
    }

    /**
     * "" → {null, null} (sem período) · "10/09/2026" → {"2026-09-10", "2026-09-10"}
     * "10/09/2026 a 12/09/2026" → {"2026-09-10", "2026-09-12"} · inválido → null
     */
    private static String[] interpretarPeriodo(String texto) {
        if (texto.isEmpty()) return new String[]{null, null};
        Matcher m = Pattern.compile("\\d{2}/\\d{2}/\\d{4}").matcher(texto);
        List<String> datas = new ArrayList<>();
        while (m.find()) datas.add(m.group());
        if (datas.isEmpty() || datas.size() > 2) return null;

        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        f.setLenient(false); // recusa 31/02
        try {
            for (String d : datas) f.parse(d);
        } catch (ParseException e) {
            return null;
        }
        String inicio = Formatos.dataIso(datas.get(0));
        String fim = Formatos.dataIso(datas.get(datas.size() - 1));
        if (fim.compareTo(inicio) < 0) return null; // fim antes do início
        return new String[]{inicio, fim};
    }

    // ------------------------------------------------------------------
    //  Etapa 4 — prévia (texto montado pela API, ainda sem gravar)
    // ------------------------------------------------------------------

    private void configurarEtapa4(View etapa) {
        if (pedidoAtual == null) { irParaEtapa(3); return; }

        TextView tvTitulo = etapa.findViewById(R.id.tvDocTitulo);
        TextView tvTexto = etapa.findViewById(R.id.tvDocTexto);
        Button btnConfirmar = etapa.findViewById(R.id.btnConfirmarDoc);
        Button btnEditar = etapa.findViewById(R.id.btnEditarDoc);

        tvTitulo.setText(nomeDoTipo(tipoDocumentoSelecionado).toUpperCase(Locale.ROOT));
        tvTexto.setText("Gerando prévia...");
        btnConfirmar.setEnabled(false);
        btnEditar.setOnClickListener(v -> irParaEtapa(3));

        // POST /documentos/previa
        ApiClient.api().previaDocumento(pedidoAtual).enqueue(new Callback<DocumentoGerado>() {
            @Override
            public void onResponse(Call<DocumentoGerado> call, Response<DocumentoGerado> resposta) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    tvTexto.setText(ApiClient.mensagemDeErro(resposta) + "\n\nToque em Editar para corrigir.");
                    return;
                }
                DocumentoGerado previa = resposta.body();
                tvTitulo.setText(previa.titulo.toUpperCase(Locale.ROOT));
                tvTexto.setText(previa.texto);
                btnConfirmar.setEnabled(true);
            }

            @Override
            public void onFailure(Call<DocumentoGerado> call, Throwable t) {
                tvTexto.setText(ApiClient.mensagemDeFalha(t));
            }
        });

        // POST /documentos → grava e vai para a etapa 5
        btnConfirmar.setOnClickListener(v -> {
            btnConfirmar.setEnabled(false);
            ApiClient.api().emitirDocumento(pedidoAtual).enqueue(new Callback<DocumentoGerado>() {
                @Override
                public void onResponse(Call<DocumentoGerado> call, Response<DocumentoGerado> resposta) {
                    if (!resposta.isSuccessful() || resposta.body() == null) {
                        btnConfirmar.setEnabled(true);
                        Toast.makeText(EmissaoDocumentoActivity.this,
                                ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                        return;
                    }
                    documentoEmitido = resposta.body();
                    Toast.makeText(EmissaoDocumentoActivity.this,
                            "Documento emitido e salvo!", Toast.LENGTH_SHORT).show();
                    irParaEtapa(5);
                }

                @Override
                public void onFailure(Call<DocumentoGerado> call, Throwable t) {
                    btnConfirmar.setEnabled(true);
                    Toast.makeText(EmissaoDocumentoActivity.this,
                            ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // ------------------------------------------------------------------
    //  Etapa 5 — documento gravado: enviar
    // ------------------------------------------------------------------

    private void configurarEtapa5(View etapa) {
        if (documentoEmitido == null) { irParaEtapa(1); return; }

        ((TextView) etapa.findViewById(R.id.tvDocResumo)).setText(
                documentoEmitido.titulo + " · " + documentoEmitido.paciente);

        etapa.findViewById(R.id.btnEnviarWhatsApp).setOnClickListener(v -> enviarPorWhatsApp());
    }

    /**
     * Abre o WhatsApp já na conversa com o telefone do paciente (wa.me),
     * com o texto do documento pronto para enviar. Sem WhatsApp, abre no navegador.
     */
    private void enviarPorWhatsApp() {
        String telefone = Formatos.soDigitos(pacienteSelecionado != null ? pacienteSelecionado.telefone : "");
        String mensagem = documentoEmitido.titulo.toUpperCase(Locale.ROOT) + "\n\n" + documentoEmitido.texto;
        String url = "https://wa.me/" + (telefone.isEmpty() ? "" : "55" + telefone)
                + "?text=" + Uri.encode(mensagem);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (RuntimeException e) {
            Toast.makeText(this, "Não foi possível abrir o WhatsApp.", Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------------------------------------------------

    private void atualizarSteppersVisuais(int etapaAtual) {
        TextView[] steps = {step1, step2, step3, step4, step5};
        for (int i = 0; i < steps.length; i++) {
            if ((i + 1) == etapaAtual) {
                steps[i].setBackgroundResource(R.drawable.bg_circle_teal);
                steps[i].setText(String.valueOf(i + 1)); // volta ao número ao retornar pelo "Editar"
                steps[i].setTextColor(getResources().getColor(android.R.color.white));
            } else if ((i + 1) < etapaAtual) {
                steps[i].setBackgroundResource(R.drawable.bg_circle_teal); // Passados concluídos
                steps[i].setText("✓");
                steps[i].setTextColor(getResources().getColor(android.R.color.white));
            } else {
                steps[i].setBackgroundResource(R.drawable.bg_circle_white_border);
                steps[i].setText(String.valueOf(i + 1));
                steps[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
}
