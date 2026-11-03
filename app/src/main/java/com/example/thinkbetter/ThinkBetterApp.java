package com.example.thinkbetter;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Roda uma vez, quando o app abre (registrado no AndroidManifest em android:name).
 * Reaplica a escolha de "Modo Escuro" feita em Configurações, que antes era
 * esquecida ao fechar o app.
 */
public class ThinkBetterApp extends Application {

    /** Mesmo arquivo de preferências usado pela BaseActivity. */
    static final String PREFS = "settings";
    static final String CHAVE_MODO_ESCURO = "modoEscuro";

    @Override
    public void onCreate() {
        super.onCreate();
        boolean escuro = getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(CHAVE_MODO_ESCURO, false);
        AppCompatDelegate.setDefaultNightMode(
                escuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
