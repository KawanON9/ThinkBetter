package com.example.thinkbetter.api;

/** Conversões entre o formato da tela e o formato da API. */
public final class Formatos {
    private Formatos() { }

    public static String soDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    /** "11990000002" → "(11) 9 9000-0002";  "1133334444" → "(11) 3333-4444" */
    public static String telefone(String d) {
        d = soDigitos(d);
        if (d.length() == 11) return "(" + d.substring(0, 2) + ") " + d.charAt(2) + " "
                + d.substring(3, 7) + "-" + d.substring(7);
        if (d.length() == 10) return "(" + d.substring(0, 2) + ") "
                + d.substring(2, 6) + "-" + d.substring(6);
        return d;
    }

    /** "1975-11-07" → "07/11/1975" */
    public static String dataBr(String iso) {
        if (iso == null || iso.length() < 10) return "";
        return iso.substring(8, 10) + "/" + iso.substring(5, 7) + "/" + iso.substring(0, 4);
    }

    /** "07/11/1975" → "1975-11-07" (ou null se incompleta) */
    public static String dataIso(String br) {
        String d = soDigitos(br);
        if (d.length() != 8) return null;
        return d.substring(4, 8) + "-" + d.substring(2, 4) + "-" + d.substring(0, 2);
    }

    /** Mesma regra de dígitos verificadores usada na API. */
    public static boolean cpfValido(String cpf) {
        cpf = soDigitos(cpf);
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;
        return digitoCpf(cpf, 9) == cpf.charAt(9) - '0'
                && digitoCpf(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digitoCpf(String cpf, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) soma += (cpf.charAt(i) - '0') * (tamanho + 1 - i);
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
