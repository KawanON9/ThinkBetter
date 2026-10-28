package com.ies300.yen.configuracoes;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupItem(R.id.itemPerfil, "Meu Perfil", "Dados Pessoais e de Contato",
                v -> startActivity(new Intent(this, ProfileActivity.class)));
        setupItem(R.id.itemSeguranca, "Segurança e Privacidade", "Senha, 2FA e Sessões",
                v -> startActivity(new Intent(this, SecurityActivity.class)));
        setupItem(R.id.itemNotificacoes, "Notificações", "Lembretes, Confirmações e Alertas",
                v -> startActivity(new Intent(this, NotificationsActivity.class)));
        setupItem(R.id.itemSobre, "Sobre o app", "Versão, Termos e Privacidade",
                v -> startActivity(new Intent(this, AboutActivity.class)));

        MaterialSwitch switchDark = findViewById(R.id.switchDark);
        switchDark.setOnCheckedChangeListener((button, checked) ->
                AppCompatDelegate.setDefaultNightMode(
                        checked ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO));

        findViewById(R.id.itemSair).setOnClickListener(v -> toast("Sair"));
    }

    private void setupItem(int itemId, String title, String subtitle, View.OnClickListener click) {
        View item = findViewById(itemId);
        ((TextView) item.findViewById(R.id.txtTitle)).setText(title);
        ((TextView) item.findViewById(R.id.txtSubtitle)).setText(subtitle);
        item.setOnClickListener(click);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}