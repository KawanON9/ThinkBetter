package com.example.thinkbetter.api;

/**
 * "Meu Perfil" (GET e PUT /usuarios/{id}/perfil).
 * nomeCompleto: "Carlos Mendes" · telefone: só dígitos · tipo: só na resposta
 */
public class Perfil {
    public String nomeCompleto;
    public String email;
    public String telefone;
    public String tipo;
}
