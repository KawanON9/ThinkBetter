package com.example.thinkbetter;

import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.Formatos;
import com.example.thinkbetter.api.Perfil;
import com.example.thinkbetter.api.Sessao;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Meu Perfil (paciente e psicólogo): nome, e-mail e telefone vêm do banco
 * e são salvos no banco (GET/PUT /usuarios/{id}/perfil).
 * A foto continua só no celular.
 */
public class ProfileActivity extends BaseActivity
{
    private ImageView imgProfile;
    private EditText edtNome, edtEmail, edtTelefone;
    private TextInputLayout tilEmail;
    private Button btnSalvar;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) showPhoto(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupHeader("Meu Perfil");

        imgProfile = findViewById(R.id.imgProfile);
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        tilEmail = findViewById(R.id.tilEmail);
        btnSalvar = findViewById(R.id.btnSalvar);

        String photo = prefs().getString("photoUri", null);
        if (photo != null) showPhoto(Uri.parse(photo));

        findViewById(R.id.txtEditPhoto).setOnClickListener(v ->
                pickPhoto.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        btnSalvar.setOnClickListener(v -> save());

        carregarPerfil();
    }

    /** GET /usuarios/{id}/perfil */
    private void carregarPerfil() {
        btnSalvar.setEnabled(false);
        ApiClient.api().lerPerfil(Sessao.idUsuario(this)).enqueue(new Callback<Perfil>() {
            @Override
            public void onResponse(Call<Perfil> call, Response<Perfil> resposta) {
                if (isFinishing()) return;
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    Toast.makeText(ProfileActivity.this, ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                    return;
                }
                mostrar(resposta.body());
                btnSalvar.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                if (isFinishing()) return;
                Toast.makeText(ProfileActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrar(Perfil p) {
        edtNome.setText(p.nomeCompleto);
        edtEmail.setText(p.email);
        edtTelefone.setText(Formatos.telefone(p.telefone));
        ((TextView) findViewById(R.id.txtIniciais)).setText(Formatos.iniciais(p.nomeCompleto));
    }

    /** PUT /usuarios/{id}/perfil */
    private void save() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String telefone = Formatos.soDigitos(edtTelefone.getText().toString());

        if (!nome.contains(" ")) {
            edtNome.setError("Informe nome e sobrenome");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            return;
        }
        tilEmail.setError(null);
        if (telefone.length() < 10 || telefone.length() > 11) {
            edtTelefone.setError("Telefone com DDD inválido");
            return;
        }

        Perfil dados = new Perfil();
        dados.nomeCompleto = nome;
        dados.email = email;
        dados.telefone = telefone;

        btnSalvar.setEnabled(false);
        ApiClient.api().salvarPerfil(Sessao.idUsuario(this), dados).enqueue(new Callback<Perfil>() {
            @Override
            public void onResponse(Call<Perfil> call, Response<Perfil> resposta) {
                if (isFinishing()) return;
                btnSalvar.setEnabled(true);
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    // ex.: 409 "E-mail já cadastrado"
                    Toast.makeText(ProfileActivity.this, ApiClient.mensagemDeErro(resposta), Toast.LENGTH_LONG).show();
                    return;
                }
                Perfil salvo = resposta.body();
                Sessao.atualizarNome(ProfileActivity.this, salvo.nomeCompleto); // saudação das telas iniciais
                mostrar(salvo);
                Toast.makeText(ProfileActivity.this, "Perfil salvo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                if (isFinishing()) return;
                btnSalvar.setEnabled(true);
                Toast.makeText(ProfileActivity.this, ApiClient.mensagemDeFalha(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPhoto(Uri u) {
        imgProfile.setImageURI(u);
        findViewById(R.id.txtIniciais).setVisibility(View.GONE);
        prefs().edit().putString("photoUri", u.toString()).apply();
    }
}
