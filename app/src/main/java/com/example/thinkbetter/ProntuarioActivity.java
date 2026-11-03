package com.example.thinkbetter;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.PacienteDetalhe;
import com.example.thinkbetter.api.Sessao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProntuarioActivity extends MenuJava {

    /** Chave do Intent com o ID_Paciente (enviada pelo PacienteAdapter). */
    public static final String EXTRA_ID_PACIENTE = "id_paciente";

    private TextView tvNome, tvCpf, tvNascimento, tvTelefone, tvPlano, tvEmail, tvEndereco, tvObs;
    private ImageButton btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prontuario);

        setupMenuPsicologoButton(R.id.btnMenu);

        // Inicializando os componentes visuais
        tvNome = findViewById(R.id.tvNomePaciente);
        tvCpf = findViewById(R.id.tvCpfPaciente);
        tvNascimento = findViewById(R.id.tvNascimentoPaciente);
        tvTelefone = findViewById(R.id.tvTelefonePaciente);
        tvPlano = findViewById(R.id.tvPlanoPaciente);
        tvEmail = findViewById(R.id.tvEmailPaciente);
        tvEndereco = findViewById(R.id.tvEnderecoPaciente);
        tvObs = findViewById(R.id.tvObsPaciente);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish()); // Volta para a tela anterior

        findViewById(R.id.btnEditarObs).setOnClickListener(v ->
                Toast.makeText(this, "Modo de edição de observações clínicas", Toast.LENGTH_SHORT).show());

        long id = getIntent().getLongExtra(EXTRA_ID_PACIENTE, -1);
        if (id < 0) {
            Toast.makeText(this, "Paciente não informado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvNome.setText("Carregando...");
        carregarPaciente(id);
    }

    /** GET /pacientes/{id}?psicologo={id} e preenche a tela. */
    private void carregarPaciente(long id) {
        // As observações são do prontuário do psicólogo logado com este paciente
        long idPsicologo = Sessao.id(this);
        ApiClient.api().buscarPaciente(id, idPsicologo).enqueue(new Callback<PacienteDetalhe>() {
            @Override
            public void onResponse(Call<PacienteDetalhe> call, Response<PacienteDetalhe> resposta) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    Toast.makeText(ProntuarioActivity.this,
                            ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                PacienteDetalhe p = resposta.body();
                tvNome.setText(p.nome + " " + p.sobrenome);
                tvCpf.setText("CPF: " + p.cpfMascarado);
                tvNascimento.setText("Nascimento: " + Formatos.dataBr(p.dataNascimento));
                tvTelefone.setText(Formatos.telefone(p.telefone));
                tvPlano.setText(p.plano);
                tvEmail.setText(p.email);
                tvEndereco.setText(p.endereco);
                tvObs.setText(p.observacoes != null ? p.observacoes : "Sem observações");
            }

            @Override
            public void onFailure(Call<PacienteDetalhe> call, Throwable t) {
                Toast.makeText(ProntuarioActivity.this,
                        ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        });
    }
}
