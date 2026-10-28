package com.example.thinkbetter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/**
 * Tela de Cadastro de Usuário.
 * Estende AppCompatActivity (e não BaseActivity/MenuJava) porque é aberta
 * antes do login e o layout já possui o próprio ScrollView.
 */
public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText edtNome, edtSobrenome, edtEmail, edtTelefone;
    private EditText edtCep, edtEndereco, edtComplemento, edtPlanoSaude;
    private SwitchCompat switchPlanoSaude;
    private LinearLayout containerPlanoSaude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        // Edge-to-edge (Android 15+): reserva espaço para barras do sistema e teclado
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootCadastro), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right,
                    Math.max(barras.bottom, teclado.bottom));
            return WindowInsetsCompat.CONSUMED;
        });

        edtNome = findViewById(R.id.edtNome);
        edtSobrenome = findViewById(R.id.edtSobrenome);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtCep = findViewById(R.id.edtCep);
        edtEndereco = findViewById(R.id.edtEndereco);
        edtComplemento = findViewById(R.id.edtComplemento);
        edtPlanoSaude = findViewById(R.id.edtPlanoSaude);
        switchPlanoSaude = findViewById(R.id.switchPlanoSaude);
        containerPlanoSaude = findViewById(R.id.containerPlanoSaude);

        // Tela pública: sem menu lateral nem avatar
        findViewById(R.id.btnMenu).setVisibility(View.INVISIBLE);
        findViewById(R.id.txtAvatar).setVisibility(View.INVISIBLE);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // Flag Plano de Saúde: mostra/esconde a caixa de texto
        switchPlanoSaude.setOnCheckedChangeListener((btn, ativo) -> {
            containerPlanoSaude.setVisibility(ativo ? View.VISIBLE : View.GONE);
            if (ativo) {
                edtPlanoSaude.requestFocus();
            } else {
                edtPlanoSaude.setText("");
                edtPlanoSaude.setError(null);
            }
        });

        // Máscaras simples
        edtTelefone.addTextChangedListener(new MascaraWatcher(edtTelefone, true));
        edtCep.addTextChangedListener(new MascaraWatcher(edtCep, false));

        findViewById(R.id.btnCadastrar).setOnClickListener(v -> cadastrar());
    }

    private void cadastrar() {
        String nome = texto(edtNome);
        String sobrenome = texto(edtSobrenome);
        String email = texto(edtEmail);
        String telefone = texto(edtTelefone);
        String cep = texto(edtCep);
        String endereco = texto(edtEndereco);
        String complemento = texto(edtComplemento);
        boolean temPlano = switchPlanoSaude.isChecked();
        String plano = texto(edtPlanoSaude);

        boolean ok = true;
        if (nome.isEmpty()) { edtNome.setError("Informe o nome"); ok = false; }
        if (sobrenome.isEmpty()) { edtSobrenome.setError("Informe o sobrenome"); ok = false; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError("E-mail inválido"); ok = false; }
        if (soDigitos(telefone).length() < 10) { edtTelefone.setError("Telefone com DDD inválido"); ok = false; }
        if (soDigitos(cep).length() != 8) { edtCep.setError("CEP deve ter 8 dígitos"); ok = false; }
        if (endereco.isEmpty()) { edtEndereco.setError("Informe o endereço"); ok = false; }
        if (temPlano && plano.isEmpty()) { edtPlanoSaude.setError("Informe o plano de saúde"); ok = false; }
        if (!ok) return;

        // Mesmo SharedPreferences usado pela BaseActivity/ProfileActivity,
        // assim "Meu Perfil" já abre com nome, e-mail e telefone preenchidos.
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        prefs.edit()
                .putString("nome", nome + " " + sobrenome)
                .putString("email", email)
                .putString("telefone", telefone)
                .putString("cep", cep)
                .putString("endereco", endereco)
                .putString("complemento", complemento)
                .putBoolean("temPlanoSaude", temPlano)
                .putString("planoSaude", temPlano ? plano : "")
                .apply();

        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private static String texto(EditText e) {
        return e.getText().toString().trim();
    }

    private static String soDigitos(String s) {
        return s.replaceAll("\\D", "");
    }

    /** Formata telefone como (11) 9 0000-0000 e CEP como 00000-000. */
    private static class MascaraWatcher implements TextWatcher {
        private final EditText campo;
        private final boolean telefone;
        private boolean editando = false;

        MascaraWatcher(EditText campo, boolean telefone) {
            this.campo = campo;
            this.telefone = telefone;
        }

        @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
        @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (editando) return;
            editando = true;
            String d = soDigitos(s.toString());
            String f = telefone ? formatarTelefone(d) : formatarCep(d);
            campo.setText(f);
            campo.setSelection(campo.getText().length());
            editando = false;
        }

        private String formatarCep(String d) {
            if (d.length() > 8) d = d.substring(0, 8);
            return d.length() > 5 ? d.substring(0, 5) + "-" + d.substring(5) : d;
        }

        private String formatarTelefone(String d) {
            if (d.length() > 11) d = d.substring(0, 11);
            StringBuilder sb = new StringBuilder();
            if (d.isEmpty()) return "";
            sb.append("(").append(d.substring(0, Math.min(2, d.length())));
            if (d.length() <= 2) return sb.toString();
            sb.append(") ");
            String resto = d.substring(2);
            if (d.length() == 11) { // celular: 9 0000-0000
                sb.append(resto.charAt(0)).append(" ");
                resto = resto.substring(1);
            }
            if (resto.length() > 4) {
                sb.append(resto, 0, 4).append("-").append(resto.substring(4));
            } else {
                sb.append(resto);
            }
            return sb.toString();
        }
    }
}