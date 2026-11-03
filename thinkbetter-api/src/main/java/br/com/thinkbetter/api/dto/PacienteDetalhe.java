package br.com.thinkbetter.api.dto;

import java.time.LocalDate;

/**
 * Dados da tela Prontuário.
 * cpfMascarado: ***.***.***-02
 * endereco: já formatado ("Av. Paulista, 456 - Apto 31 - Bela Vista, São Paulo/SP")
 * observacoes: do prontuário do psicólogo informado em ?psicologo= (ou null)
 */
public record PacienteDetalhe(
        long id,
        String nome,
        String sobrenome,
        String cpfMascarado,
        LocalDate dataNascimento,
        String telefone,
        String email,
        String endereco,
        String cep,
        String plano,
        String carteirinha,
        String observacoes
) { }
