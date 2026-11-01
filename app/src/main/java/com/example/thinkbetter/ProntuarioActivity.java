package com.example.thinkbetter;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProntuarioActivity extends MenuJava {

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

        // Recuperando os dados enviados pela Intent da tela de listagem de pacientes
        String nome = getIntent().getStringExtra("nome_paciente");
        String plano = getIntent().getStringExtra("plano_paciente");
        String telefone = getIntent().getStringExtra("telefone_paciente");

        // Preenchendo os dados de forma dinâmica (se vieram vazios, usa dados de exemplo ou do Carlos Mendes[cite: 6])
        if (nome != null) {
            tvNome.setText(nome);
            tvPlano.setText(plano != null ? plano : "Particular");
            tvTelefone.setText(telefone != null ? telefone : "(11) 9 9000-0002");
            tvCpf.setText("CPF: ***.***.***-02");
            tvNascimento.setText("Nascimento: 07/11/1975");
            tvEmail.setText("carlos@email.com");
            tvEndereco.setText("Av. Paulista, 456");
            tvObs.setText("Depressão leve");
        } else {
            // Dados padrão baseados na sua imagem de exemplo (Carlos Mendes)[cite: 6]
            tvNome.setText("Carlos Mendes");
            tvCpf.setText("CPF: ***.***.***-02");
            tvNascimento.setText("Nascimento: 07/11/1975");
            tvTelefone.setText("(11) 9 9000-0002");
            tvPlano.setText("Particular");
            tvEmail.setText("carlos@email.com");
            tvEndereco.setText("Av. Paulista, 456");
            tvObs.setText("Depressão leve");
        }

        findViewById(R.id.btnEditarObs).setOnClickListener(v -> {
            Toast.makeText(this, "Modo de edição de observações clínicas", Toast.LENGTH_SHORT).show();
        });
    }
}
