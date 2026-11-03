package com.example.thinkbetter.api;

/**
 * Corpo do POST /documentos/previa e POST /documentos.
 * tipo: Atestado | Declaracao | Laudo
 * periodoInicio / periodoFim: "aaaa-mm-dd" ou null · cid: "F41.1" ou null
 */
public class NovoDocumentoRequest {
    public long idPsicologo;
    public long idPaciente;
    public String tipo;
    public String periodoInicio;
    public String periodoFim;
    public String cid;
    public String descricao;
}
