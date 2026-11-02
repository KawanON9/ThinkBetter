package com.example.thinkbetter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EsqueciSenhaActivity extends AppCompatActivity {

    private EditText etEmailRecuperacao;
    private Button btnEnviarInstrucoes;
    private TextView tvVoltarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        etEmailRecuperacao = findViewById(R.id.etEmailRecuperacao);
        btnEnviarInstrucoes = findViewById(R.id.btnEnviarInstrucoes);
        tvVoltarLogin = findViewById(R.id.tvVoltarLogin);

        // Ação do botão de enviar instruções
        btnEnviarInstrucoes.setOnClickListener(v -> {
            String email = etEmailRecuperacao.getText().toString().trim();

            if (email.isEmpty()) {
                etEmailRecuperacao.setError("Por favor, digite seu e-mail");
                etEmailRecuperacao.requestFocus();
                // Exemplo de uso direto em qualquer botão ou evento da sua Activity:
            } else {
                // Utilizando o seu Toast personalizado com a logo
                Toast.makeText(EsqueciSenhaActivity.this, "Enivados instruções paro o Email " + email, Toast.LENGTH_LONG).show();
                
                // Finaliza a tela e volta para o login após solicitar
                finish();
            }
        });

        // Ação para retornar à tela de login
        tvVoltarLogin.setOnClickListener(v -> {
            finish(); // Fecha a tela atual e retorna à anterior (Login)
        });
    }
}
