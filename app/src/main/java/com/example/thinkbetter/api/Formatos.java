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

    private static final java.util.Locale PT_BR = new java.util.Locale("pt", "BR");

    /** Data de hoje por extenso: "Terça-feira, 3 de novembro de 2026". */
    public static String hojePorExtenso() {
        String d = new java.text.SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", PT_BR)
                .format(new java.util.Date());
        return d.isEmpty() ? d : d.substring(0, 1).toUpperCase(PT_BR) + d.substring(1);
    }

    /** Data de hoje no formato da API: "2026-11-03". */
    public static String hojeIso() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date());
    }

    /** "2026-11-04T10:00:00" → "Qua, 4 de nov · 10:00" (como no card do paciente). */
    public static String dataHoraCurta(String iso) {
        if (iso == null || iso.length() < 16) return "";
        try {
            java.util.Date d = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm", java.util.Locale.US)
                    .parse(iso.substring(0, 16));
            String dia = new java.text.SimpleDateFormat("EEE", PT_BR).format(d).replace(".", "");
            String resto = new java.text.SimpleDateFormat("d 'de' MMM · HH:mm", PT_BR).format(d).replace(".", "");
            return dia.substring(0, 1).toUpperCase(PT_BR) + dia.substring(1) + ", " + resto;
        } catch (java.text.ParseException e) {
            return iso;
        }
    }

    /** "2026-11-04T10:00:00" → "04/11/2026" */
    public static String dataDeDataHora(String iso) {
        return iso == null || iso.length() < 10 ? "" : dataBr(iso.substring(0, 10));
    }

    /** "2026-11-04T10:00:00" → "10:00" */
    public static String horaDeDataHora(String iso) {
        return iso == null || iso.length() < 16 ? "" : iso.substring(11, 16);
    }

    /** "Rafael Costa" → "Rafael" */
    public static String primeiroNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return "";
        return nome.trim().split("\\s+")[0];
    }

    /** "Rafael Costa" → "RC" · "Ana" → "A" */
    public static String iniciais(String nome) {
        if (nome == null || nome.trim().isEmpty()) return "";
        String[] p = nome.trim().split("\\s+");
        String a = p[0].substring(0, 1);
        String b = p.length > 1 ? p[p.length - 1].substring(0, 1) : "";
        return (a + b).toUpperCase(PT_BR);
    }
}
