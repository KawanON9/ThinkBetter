package com.example.thinkbetter.api;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda quem está logado (id, tipo e nome) entre as telas.
 * Não guarda senha.
 */
public final class Sessao {
    private static final String ARQUIVO = "sessao";

    private Sessao() { }

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    public static void salvar(Context c, LoginResponse usuario) {
        prefs(c).edit()
                .putLong("id", usuario.id)
                .putLong("idUsuario", usuario.idUsuario)
                .putString("tipo", usuario.tipo)
                .putString("nome", usuario.nome)
                .apply();
    }

    /** ID_Paciente ou ID_Psicologo de quem está logado. */
    public static long id(Context c)        { return prefs(c).getLong("id", -1); }
    public static long idUsuario(Context c) { return prefs(c).getLong("idUsuario", -1); }
    public static String tipo(Context c)  { return prefs(c).getString("tipo", null); }
    public static String nome(Context c)  { return prefs(c).getString("nome", ""); }

    /** Atualiza o nome guardado (ex.: depois de editar o "Meu Perfil"). */
    public static void atualizarNome(Context c, String nome) {
        prefs(c).edit().putString("nome", nome).apply();
    }

    public static void sair(Context c) {
        prefs(c).edit().clear().apply();
    }
}
