package com.example.thinkbetter.api;

/**
 * Corpo do POST /pacientes. Os nomes dos campos são os nomes no JSON,
 * então precisam ser iguais aos da API.
 * Formatos: cpf, telefone e cep só com dígitos; dataNascimento "aaaa-mm-dd".
 * A API grava o login em Usuarios e o resto em Pacientes.
 */
public class CadastroPacienteRequest {
    // login (Usuarios)
    public String email;
    public String senha;

    // dados pessoais (Pacientes)
    public String nome;
    public String sobrenome;
    public String cpf;
    public String dataNascimento;
    public String telefone;

    // endereço em partes
    public String cep;
    public String logradouro;
    public String numero;
    public String complemento;  // opcional
    public String bairro;       // opcional
    public String cidade;
    public String uf;

    // plano (null = Particular)
    public String planoSaude;
    public String carteirinha;
}
