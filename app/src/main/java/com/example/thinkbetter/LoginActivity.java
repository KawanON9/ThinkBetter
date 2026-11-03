package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.LoginRequest;
import com.example.thinkbetter.api.LoginResponse;
import com.example.thinkbetter.api.Sessao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private TextView tvMostrarSenha, tvEsqueciSenha;
    private Button btnEntrar;
    private boolean senhaVisivel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializando os componentes da tela
        etEmail = findViewById(R.id.tilEmail);
        etSenha = findViewById(R.id.etSenha);
        tvMostrarSenha = findViewById(R.id.tvMostrarSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);

        // Ação do botão "Mostrar" / "Ocultar" senha
        tvMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senhaVisivel) {
                    // Ocultar senha
                    etSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    tvMostrarSenha.setText("Mostrar");
                    senhaVisivel = false;
                } else {
                    // Mostrar senha
                    etSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    tvMostrarSenha.setText("Ocultar");
                    senhaVisivel = true;
                }
                // Mantém o cursor no final do texto
                etSenha.setSelection(etSenha.getText().length());
            }
        });

        // Ação do botão Entrar
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String senha = etSenha.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else if (email.equals("admin") && senha.equals("admin")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (email.equals("user") && senha.equals("user")) {
                    Intent intent = new Intent(LoginActivity.this, MainPacienteActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Aqui você colocará a lógica de autenticação
                    Toast.makeText(LoginActivity.this, "Tentando entrar...", Toast.LENGTH_SHORT).show();
                    //ToastUtils.mostrar(LoginActivity.this, "Instruções enviadas para " + email);
                }
            }
        });

        // Ação de Esqueci minha senha
        tvEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Tela de recuperação de senha", Toast.LENGTH_SHORT).show();
            }
        });
        // Abrir tela de cadastro
        findViewById(R.id.tvCriarConta).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroUsuarioActivity.class)));
    }

    /**
     * POST /login. A API responde com o tipo do usuário, que decide a tela:
     * PSICOLOGO → MainActivity, PACIENTE → MainPacienteActivity.
     */
    private void entrar(String email, String senha) {
        btnEntrar.setEnabled(false);
        btnEntrar.setText("Entrando...");

        ApiClient.api().login(new LoginRequest(email, senha)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resposta) {
                btnEntrar.setEnabled(true);
                btnEntrar.setText("Entrar");

                if (!resposta.isSuccessful() || resposta.body() == null) {
                    // 401 → "E-mail ou senha incorretos"
                    Toast.makeText(LoginActivity.this,
                            ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                    return;
                }

                LoginResponse usuario = resposta.body();
                Sessao.salvar(LoginActivity.this, usuario);

                Class<?> destino = LoginResponse.PSICOLOGO.equals(usuario.tipo)
                        ? MainActivity.class
                        : MainPacienteActivity.class;
                startActivity(new Intent(LoginActivity.this, destino));
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnEntrar.setEnabled(true);
                btnEntrar.setText("Entrar");
                Toast.makeText(LoginActivity.this,
                        ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        });
    }
}
