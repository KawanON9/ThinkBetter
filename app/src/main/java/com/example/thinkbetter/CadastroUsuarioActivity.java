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
import android.text.InputType;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.thinkbetter.api.ApiClient;
import com.example.thinkbetter.api.CadastroPacienteRequest;
import com.example.thinkbetter.api.CadastroResponse;
import com.example.thinkbetter.api.Formatos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tela de Cadastro de Usuário.
 * Estende AppCompatActivity (e não BaseActivity/MenuJava) porque é aberta
 * antes do login e o layout já possui o próprio ScrollView.
 */
public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText edtNome, edtSobrenome, edtEmail, edtTelefone;
    private EditText edtCpf, edtNascimento;
    private EditText edtNumero, edtBairro, edtCidade, edtUf;
    private EditText edtCep, edtEndereco, edtComplemento, edtCarteirinha;
    private Spinner spinnerPlanoSaude;
    private static final int SENHA_TAMANHO_MINIMO = 8;
    private EditText edtSenha, edtConfirmarSenha;
    private TextView btnMostrarSenha;
    private boolean senhaVisivel = false;
    private SwitchCompat switchPlanoSaude;
    private LinearLayout containerPlanoSaude;
    private View btnCadastrar;

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
        edtCpf = findViewById(R.id.edtCpf);
        edtNascimento = findViewById(R.id.edtNascimento);

        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtCep = findViewById(R.id.edtCep);
        edtEndereco = findViewById(R.id.edtEndereco);
        edtComplemento = findViewById(R.id.edtComplemento);
        edtNumero = findViewById(R.id.edtNumero);
        edtBairro = findViewById(R.id.edtBairro);
        edtCidade = findViewById(R.id.edtCidade);
        edtUf = findViewById(R.id.edtUf);

        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        btnMostrarSenha = findViewById(R.id.btnMostrarSenha);

        spinnerPlanoSaude = findViewById(R.id.spinnerPlanoSaude);
        edtCarteirinha = findViewById(R.id.edtCarteirinha);
        switchPlanoSaude = findViewById(R.id.switchPlanoSaude);
        containerPlanoSaude = findViewById(R.id.containerPlanoSaude);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Tela pública: sem menu lateral nem avatar
        findViewById(R.id.btnMenu).setVisibility(View.INVISIBLE);
        findViewById(R.id.txtAvatar).setVisibility(View.INVISIBLE);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // Flag Plano de Saúde: mostra/esconde a caixa de texto
        configurarMenuPlanos();

        // Flag Plano de Saúde: mostra/esconde o menu de planos e a carteirinha
        switchPlanoSaude.setOnCheckedChangeListener((btn, ativo) -> {
            containerPlanoSaude.setVisibility(ativo ? View.VISIBLE : View.GONE);
            if (!ativo) {
                spinnerPlanoSaude.setSelection(0);  // volta para "Selecione o plano"
                edtCarteirinha.setText("");
                edtCarteirinha.setError(null);
            }
            atualizarBotaoCadastrar();
        });

            findViewById(R.id.btnCadastrar).setOnClickListener(v -> cadastrar());

        // Mostrar / ocultar as duas senhas
        btnMostrarSenha.setOnClickListener(v -> alternarVisibilidadeSenha());

        // Máscaras simples
        edtTelefone.addTextChangedListener(new MascaraWatcher(edtTelefone, true));
        edtCep.addTextChangedListener(new MascaraWatcher(edtCep, false));
        edtCpf.addTextChangedListener(new MascaraPadrao(edtCpf, "###.###.###-##"));
        edtNascimento.addTextChangedListener(new MascaraPadrao(edtNascimento, "##/##/####"));

        configurarBotaoCadastrar();
            btnCadastrar.setOnClickListener(v -> cadastrar());
    }

/**
 * Faz o botão Cadastrar mudar de cor conforme o preenchimento:
 * qualquer alteração nos campos obrigatórios recalcula o estado.
 */
private void configurarBotaoCadastrar() {
    TextWatcher aoDigitar = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
        @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
        @Override public void afterTextChanged(Editable s) { atualizarBotaoCadastrar(); }
    };
    EditText[] obrigatorios = {edtNome, edtSobrenome, edtCpf, edtNascimento, edtEmail, edtTelefone, edtCep,
            edtEndereco, edtNumero, edtCidade, edtUf, edtSenha, edtConfirmarSenha, edtCarteirinha};
    for (EditText campo : obrigatorios) {
        campo.addTextChangedListener(aoDigitar);
    }

    spinnerPlanoSaude.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            atualizarBotaoCadastrar();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    });

    atualizarBotaoCadastrar();
}

    /**
     * Preenche o menu com as opções de res/values (array "planos_saude").
     * O item 0 ("Selecione o plano") aparece em cinza e não pode ser escolhido.
     */
    private void configurarMenuPlanos() {
        String[] planos = getResources().getStringArray(R.array.planos_saude);
        int corTexto = getResources().getColor(R.color.text_title, getTheme());
        int corDica = getResources().getColor(R.color.text_hint, getTheme());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.item_spinner_plano, planos) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;              // impede escolher o texto de orientação
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(position == 0 ? corDica : corTexto);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(position == 0 ? corDica : corTexto);
                return tv;
            }
        };
        adapter.setDropDownViewResource(R.layout.item_spinner_plano_dropdown);
        spinnerPlanoSaude.setAdapter(adapter);
    }

    /** activated = true troca o fundo e o texto do botão (ver bg_button_cadastrar.xml). */
    private void atualizarBotaoCadastrar() {
        btnCadastrar.setActivated(formularioCompleto());
    }

/**
 * Mesmas regras do cadastrar(), mas sem mostrar erros:
 * só responde se o formulário já pode ser enviado.
 */
private boolean formularioCompleto() {
    String senha = edtSenha.getText().toString();
    boolean dadosOk = !texto(edtNome).isEmpty()
            && !texto(edtSobrenome).isEmpty()
            && Formatos.cpfValido(texto(edtCpf))
            && validarNascimento(texto(edtNascimento)) == null
            && Patterns.EMAIL_ADDRESS.matcher(texto(edtEmail)).matches()
            && soDigitos(texto(edtTelefone)).length() >= 10
            && soDigitos(texto(edtCep)).length() == 8
            && !texto(edtEndereco).isEmpty()
            && !texto(edtNumero).isEmpty()
            && !texto(edtCidade).isEmpty()
            && texto(edtUf).matches("[A-Za-z]{2}")
            && validarSenha(senha) == null
            && senha.equals(edtConfirmarSenha.getText().toString());
    if (!dadosOk) return false;

    if (switchPlanoSaude.isChecked()) {
        return spinnerPlanoSaude.getSelectedItemPosition() > 0
                && !soDigitos(texto(edtCarteirinha)).isEmpty();
    }
    return true;
}


    //Lógica do botão "Mostrar a senha"
    private void alternarVisibilidadeSenha() {
        senhaVisivel = !senhaVisivel;
        int tipo = InputType.TYPE_CLASS_TEXT | (senhaVisivel
                ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_TEXT_VARIATION_PASSWORD);
        for (EditText campo : new EditText[]{edtSenha, edtConfirmarSenha}) {
            campo.setInputType(tipo);
            campo.setSelection(campo.getText().length()); // mantém o cursor no fim
        }
        btnMostrarSenha.setText(senhaVisivel ? R.string.ocultar : R.string.mostrar);
    }

    /** Erro se a senha for inválida */
    private static String validarSenha(String senha) {
        if (senha.isEmpty()) return "Informe uma senha";
        if (senha.length() < SENHA_TAMANHO_MINIMO)
            return "A senha deve ter pelo menos " + SENHA_TAMANHO_MINIMO + " caracteres";
        return null;
    }

    // Aqui vai rolar as bruxarias do banco de dados
    private void cadastrar() {
        String nome = texto(edtNome);
        String sobrenome = texto(edtSobrenome);
        String email = texto(edtEmail);
        String telefone = texto(edtTelefone);
        String cep = texto(edtCep);
        String endereco = texto(edtEndereco);
        String complemento = texto(edtComplemento);
        String senha = edtSenha.getText().toString();            // sem trim: espaços contam
        String confirmacao = edtConfirmarSenha.getText().toString();
        boolean temPlano = switchPlanoSaude.isChecked();
        int posicaoPlano = spinnerPlanoSaude.getSelectedItemPosition();
        String plano = posicaoPlano > 0 ? spinnerPlanoSaude.getSelectedItem().toString() : "";
        String carteirinha = soDigitos(texto(edtCarteirinha));

        boolean ok = true;
        if (nome.isEmpty()) { edtNome.setError("Informe o nome"); ok = false; }
        if (sobrenome.isEmpty()) { edtSobrenome.setError("Informe o sobrenome"); ok = false; }
        if (!Formatos.cpfValido(texto(edtCpf))) { edtCpf.setError("CPF inválido"); ok = false; }
        String erroNascimento = validarNascimento(texto(edtNascimento));
        if (erroNascimento != null) { edtNascimento.setError(erroNascimento); ok = false; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError("E-mail inválido"); ok = false; }
        if (soDigitos(telefone).length() < 10) { edtTelefone.setError("Telefone com DDD inválido"); ok = false; }
        if (soDigitos(cep).length() != 8) { edtCep.setError("CEP deve ter 8 dígitos"); ok = false; }
        if (endereco.isEmpty()) { edtEndereco.setError("Informe a rua"); ok = false; }
        if (texto(edtNumero).isEmpty()) { edtNumero.setError("Informe o número"); ok = false; }
        if (texto(edtCidade).isEmpty()) { edtCidade.setError("Informe a cidade"); ok = false; }
        if (!texto(edtUf).matches("[A-Za-z]{2}")) { edtUf.setError("UF com 2 letras"); ok = false; }

        String erroSenha = validarSenha(senha);
        if (erroSenha != null) { edtSenha.setError(erroSenha); ok = false; }
        if (!senha.equals(confirmacao)) { edtConfirmarSenha.setError("As senhas não coincidem"); ok = false; }

        if (temPlano) {
            if (posicaoPlano == 0) {
                // Spinner não tem setError: o erro vai no texto exibido dentro dele
                TextView selecionado = (TextView) spinnerPlanoSaude.getSelectedView();
                if (selecionado != null) selecionado.setError("Selecione o plano");
                ok = false;
            }
            if (carteirinha.isEmpty()) { edtCarteirinha.setError("Informe o número da carteirinha"); ok = false; }
        }
        if (!ok) return;

        // Monta o JSON no formato que a API espera (só dígitos, data aaaa-mm-dd)
        CadastroPacienteRequest dados = new CadastroPacienteRequest();
        dados.nome = nome;
        dados.sobrenome = sobrenome;
        dados.cpf = soDigitos(texto(edtCpf));
        dados.dataNascimento = Formatos.dataIso(texto(edtNascimento));
        dados.email = email;
        dados.telefone = soDigitos(telefone);
        dados.cep = soDigitos(cep);
        dados.logradouro = endereco;
        dados.numero = texto(edtNumero);
        dados.complemento = complemento.isEmpty() ? null : complemento;
        dados.bairro = texto(edtBairro).isEmpty() ? null : texto(edtBairro);
        dados.cidade = texto(edtCidade);
        dados.uf = texto(edtUf).toUpperCase(Locale.ROOT);
        dados.planoSaude = temPlano ? plano : null;
        dados.carteirinha = temPlano ? carteirinha : null;
        dados.senha = senha;

        enviarCadastro(dados, nome + " " + sobrenome, email, telefone);
    }

    /**
     * Envia o cadastro para a API (POST /pacientes) em segundo plano.
     * O botão fica bloqueado durante o envio para evitar cadastro duplicado.
     */
    private void enviarCadastro(CadastroPacienteRequest dados,
                                String nomeCompleto, String email, String telefone) {
        btnCadastrar.setEnabled(false);
        ((TextView) btnCadastrar).setText("Enviando...");

        ApiClient.api().cadastrarPaciente(dados).enqueue(new Callback<CadastroResponse>() {
            @Override
            public void onResponse(Call<CadastroResponse> call, Response<CadastroResponse> resposta) {
                liberarBotao();
                if (!resposta.isSuccessful()) {
                    // Ex.: 409 "CPF já cadastrado" / "E-mail já cadastrado"
                    mostrarErro(ApiClient.mensagemDeErro(resposta));
                    return;
                }

                // Mantém o "Meu Perfil" preenchido, como antes (sem a senha)
                SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
                prefs.edit()
                        .putString("nome", nomeCompleto)
                        .putString("email", email)
                        .putString("telefone", telefone)
                        .apply();

                edtSenha.setText("");
                edtConfirmarSenha.setText("");
                Toast.makeText(CadastroUsuarioActivity.this,
                        "Cadastro realizado! Faça login para continuar.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CadastroUsuarioActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<CadastroResponse> call, Throwable t) {
                liberarBotao();
                mostrarErro(ApiClient.mensagemDeFalha(t));
            }
        });
    }

    private void liberarBotao() {
        btnCadastrar.setEnabled(true);
        ((TextView) btnCadastrar).setText(R.string.btn_cadastrar);
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    /** Data no formato dd/mm/aaaa, existente e no passado. Retorna o erro ou null. */
    private static String validarNascimento(String br) {
        if (soDigitos(br).length() != 8) return "Use o formato dd/mm/aaaa";
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        f.setLenient(false); // recusa 31/02/2000
        try {
            Date data = f.parse(br);
            if (data == null || !data.before(new Date())) return "Data de nascimento inválida";
            if (br.substring(6).compareTo("1900") < 0) return "Data de nascimento inválida";
            return null;
        } catch (ParseException e) {
            return "Data de nascimento inválida";
        }
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

    /**
     * Máscara genérica: cada "#" do padrão é um dígito.
     * Ex.: "###.###.###-##" (CPF) e "##/##/####" (data).
     */
    private static class MascaraPadrao implements TextWatcher {
        private final EditText campo;
        private final String padrao;
        private boolean editando = false;

        MascaraPadrao(EditText campo, String padrao) {
            this.campo = campo;
            this.padrao = padrao;
        }

        @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
        @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (editando) return;
            editando = true;
            String d = soDigitos(s.toString());
            StringBuilder f = new StringBuilder();
            int i = 0;
            for (char c : padrao.toCharArray()) {
                if (i >= d.length()) break;
                f.append(c == '#' ? d.charAt(i++) : c);
            }
            campo.setText(f.toString());
            campo.setSelection(campo.getText().length());
            editando = false;
        }
    }
}
