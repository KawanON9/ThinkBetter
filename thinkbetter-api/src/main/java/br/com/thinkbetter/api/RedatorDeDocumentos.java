package br.com.thinkbetter.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Monta o texto dos documentos a partir dos dados do banco.
 * Fica em um lugar só para que a prévia (etapa 4), o documento emitido e o
 * "Meus Documentos" do paciente mostrem exatamente o mesmo texto.
 *
 * Os modelos seguem a ideia da Resolução CFP nº 06/2019: a declaração
 * informa só o comparecimento (sem diagnóstico / CID); o atestado pode
 * trazer o CID quando o profissional julgar necessário; o laudo traz a
 * descrição técnica escrita pelo psicólogo.
 */
public final class RedatorDeDocumentos {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter POR_EXTENSO = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PT_BR);

    private RedatorDeDocumentos() { }

    /** Dados necessários para escrever qualquer documento. */
    public record Dados(
            String tipo,
            String paciente,
            String cpfMascarado,
            String psicologo,
            String crp,
            String cidadePsicologo,
            String ufPsicologo,
            LocalDate periodoInicio,
            LocalDate periodoFim,
            String cid,
            String descricao,
            LocalDate emitidoEm
    ) { }

    public static String titulo(String tipo) {
        return switch (tipo) {
            case "Atestado" -> "Atestado psicológico";
            case "Declaracao" -> "Declaração de comparecimento";
            case "Laudo" -> "Laudo psicológico";
            case "Recibo" -> "Recibo de consulta";
            default -> tipo;
        };
    }

    public static String texto(Dados d) {
        String identificacao = d.paciente() + ", CPF " + d.cpfMascarado();
        String periodo = periodo(d.periodoInicio(), d.periodoFim());
        StringBuilder t = new StringBuilder();

        switch (d.tipo()) {
            case "Atestado" -> {
                t.append("Atesto, para os devidos fins, que ").append(identificacao)
                 .append(", encontra-se em acompanhamento psicológico sob meus cuidados profissionais");
                if (periodo != null) {
                    t.append(", necessitando de afastamento de suas atividades ").append(periodo);
                }
                t.append(".");
                if (temTexto(d.cid())) t.append(" CID: ").append(d.cid()).append(".");
                if (temTexto(d.descricao())) t.append("\n\n").append(d.descricao().trim());
            }
            case "Declaracao" -> {
                t.append("Declaro, para os devidos fins, que ").append(identificacao)
                 .append(", compareceu a atendimento psicológico");
                t.append(periodo != null ? " " + periodo : " nesta data").append(".");
                if (temTexto(d.descricao())) t.append("\n\n").append(d.descricao().trim());
            }
            case "Laudo" -> {
                t.append("Laudo psicológico referente a ").append(identificacao).append(".");
                if (periodo != null) t.append("\nPeríodo de avaliação: ").append(periodoCurto(d.periodoInicio(), d.periodoFim())).append(".");
                if (temTexto(d.cid())) t.append("\nCID: ").append(d.cid()).append(".");
                if (temTexto(d.descricao())) t.append("\n\n").append(d.descricao().trim());
            }
            default -> {
                t.append(titulo(d.tipo())).append(" referente a ").append(identificacao).append(".");
                if (temTexto(d.descricao())) t.append("\n\n").append(d.descricao().trim());
            }
        }

        // Local e data
        String local = temTexto(d.cidadePsicologo())
                ? d.cidadePsicologo() + (temTexto(d.ufPsicologo()) ? "/" + d.ufPsicologo() : "")
                : null;
        t.append("\n\n").append(local != null ? local + ", " : "")
         .append(d.emitidoEm().format(POR_EXTENSO)).append(".");

        // Assinatura
        t.append("\n\n").append(d.psicologo()).append("\nPsicólogo(a) · CRP ").append(d.crp());
        return t.toString();
    }

    /** "no dia 10/09/2026" ou "no período de 10/09/2026 a 12/09/2026" (ou null). */
    private static String periodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null) return null;
        if (fim == null || fim.equals(inicio)) return "no dia " + inicio.format(DATA);
        return "no período de " + inicio.format(DATA) + " a " + fim.format(DATA);
    }

    private static String periodoCurto(LocalDate inicio, LocalDate fim) {
        if (fim == null || fim.equals(inicio)) return inicio.format(DATA);
        return inicio.format(DATA) + " a " + fim.format(DATA);
    }

    private static boolean temTexto(String s) {
        return s != null && !s.isBlank();
    }
}
