package com.example.thinkbetter.api;

/** Resposta do GET /pacientes/{id} (tela Prontuário). */
public class PacienteDetalhe {
    public long id;
    public String nome;
    public String sobrenome;
    public String cpfMascarado;     // ***.***.***-02
    public String dataNascimento;   // aaaa-mm-dd
    public String telefone;
    public String email;
    public String endereco;         // já formatado pela API
    public String cep;
    public String plano;
    public String carteirinha;
    public String observacoes;      // do prontuário do psicólogo logado (pode ser null)
}
