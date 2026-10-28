package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

public class SecurityActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        setupHeader("Segurança e Privacidade");

        bindSwitch(R.id.item2fa, "Autenticação em Dois Fatores (2FA)",
                "Código extra ao entrar no sistema", "2fa", false);
        bindSwitch(R.id.itemBiometria, "Entrar com Biometria",
                "Use digital ou rosto para abrir o app", "biometria", false);
        bindSwitch(R.id.itemBloqueio, "Bloqueio Automático",
                "Bloqueia o app após 5 minutos inativo", "bloqueio", true);

        setupItem(R.id.itemSessoes, "Sessões Ativas", "Veja e encerre dispositivos conectados");
        setupItem(R.id.itemExportar, "Exportar Meus Dados", "Receba uma cópia das suas informações");

        findViewById(R.id.btnAlterarSenha).setOnClickListener(v -> changePassword());

        findViewById(R.id.itemExcluir).setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Excluir Conta")
                        .setMessage("Esta ação não pode ser desfeita. Deseja continuar?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Excluir", (d, w) ->
                                Toast.makeText(this, "Conta excluída (simulação)", Toast.LENGTH_SHORT).show())
                        .show());
    }

    private void setupItem(int id, String title, String subtitle) {
        View item = findViewById(id);
        ((TextView) item.findViewById(R.id.txtTitle)).setText(title);
        ((TextView) item.findViewById(R.id.txtSubtitle)).setText(subtitle);
        item.setOnClickListener(v -> Toast.makeText(this, title, Toast.LENGTH_SHORT).show());
    }

    private void changePassword() {
        TextInputLayout tilNova = findViewById(R.id.tilNova);
        TextInputLayout tilConfirma = findViewById(R.id.tilConfirma);
        String nova = ((EditText) findViewById(R.id.edtSenhaNova)).getText().toString();
        String confirma = ((EditText) findViewById(R.id.edtSenhaConfirma)).getText().toString();

        tilNova.setError(null);
        tilConfirma.setError(null);

        if (nova.length() < 8) {
            tilNova.setError("Use pelo menos 8 caracteres");
            return;
        }
        if (!nova.equals(confirma)) {
            tilConfirma.setError("As senhas não coincidem");
            return;
        }
        // Aqui você chamaria sua API/Firebase para trocar a senha de verdade
        Toast.makeText(this, "Senha alterada", Toast.LENGTH_SHORT).show();
    }
}