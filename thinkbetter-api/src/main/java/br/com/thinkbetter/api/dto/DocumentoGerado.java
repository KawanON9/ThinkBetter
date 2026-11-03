package br.com.thinkbetter.api.dto;

/**
 * Documento pronto para exibir (prévia, emissão e "Meus Documentos").
 * tipo: Atestado | Declaracao | Laudo | Recibo
 * emitidoEm: "2026-11-03" · id = 0 na prévia (ainda não foi gravado)
 * texto: corpo completo, já com local, data e assinatura.
 */
public record DocumentoGerado(
        long id,
        String tipo,
        String titulo,
        String paciente,
        String psicologo,
        String crp,
        String emitidoEm,
        String texto
) { }
