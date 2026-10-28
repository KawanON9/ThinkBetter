package com.ies300.yen.configuracoes; // confira se é o mesmo pacote das suas outras classes

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupHeader("Sobre o App");

        ((TextView) findViewById(R.id.txtTermos)).setText(lerTermos());

        String version = "1.0";
        try {
            version = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) { }
        ((TextView) findViewById(R.id.txtVersao)).setText(version);
    }

    private String lerTermos() {
        try (InputStream is = getResources().openRawResource(R.raw.termos_privacidade);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            return "Não foi possível carregar o texto.";
        }
    }
}