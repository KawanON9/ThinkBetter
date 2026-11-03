package br.com.thinkbetter.api;

/** Validação dos dígitos verificadores do CPF. */
public final class Cpf {
    private Cpf() { }

    public static boolean valido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) return false;
        if (cpf.chars().distinct().count() == 1) return false; // 111.111.111-11 etc.
        return digito(cpf, 9) == cpf.charAt(9) - '0'
                && digito(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digito(String cpf, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (cpf.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    /** "12345678902" → "***.***.***-02" (para exibir sem expor o CPF inteiro). */
    public static String mascarado(String cpf) {
        return cpf == null || cpf.length() != 11 ? "" : "***.***.***-" + cpf.substring(9);
    }
}
