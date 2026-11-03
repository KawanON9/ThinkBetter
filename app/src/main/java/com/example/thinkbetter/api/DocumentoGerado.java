package com.example.thinkbetter.api;

/**
 * Documento devolvido pela API (prévia, emitido ou em "Meus Documentos").
 * tipo: Atestado | Declaracao | Laudo | Recibo · emitidoEm: "2026-11-03"
 * texto: corpo completo, já com local, data e assinatura.
 */
public class DocumentoGerado {
    public long id;          // 0 na prévia
    public String tipo;
    public String titulo;    // "Atestado psicológico"
    public String paciente;
    public String psicologo;
    public String crp;
    public String emitidoEm;
    public String texto;
}
