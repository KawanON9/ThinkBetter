package br.com.thinkbetter.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corpo do POST /pacientes, enviado pela tela Cadastro de Usuário.
 * Gera uma linha em Usuarios (login) e outra em Pacientes (dados pessoais).
 */
public record CadastroPacienteRequest(
        // --- login (tabela Usuarios)
        @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") @Size(max = 120) String email,
        @NotNull(message = "Informe uma senha") @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres") String senha,

        // --- dados pessoais (tabela Pacientes)
        @NotBlank(message = "Informe o nome") @Size(max = 80) String nome,
        @NotBlank(message = "Informe o sobrenome") @Size(max = 80) String sobrenome,
        @NotNull(message = "Informe o CPF") @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos") String cpf,
        @NotNull(message = "Informe a data de nascimento") @Past(message = "Data de nascimento inválida") LocalDate dataNascimento,
        @NotNull(message = "Informe o telefone") @Pattern(regexp = "\\d{10,11}", message = "Telefone com DDD inválido") String telefone,

        // --- endereço em partes
        @NotNull(message = "Informe o CEP") @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos") String cep,
        @NotBlank(message = "Informe a rua") @Size(max = 120) String logradouro,
        @NotBlank(message = "Informe o número") @Size(max = 10) String numero,
        @Size(max = 80) String complemento,
        @Size(max = 80) String bairro,
        @NotBlank(message = "Informe a cidade") @Size(max = 80) String cidade,
        @NotNull(message = "Informe a UF") @Pattern(regexp = "[A-Za-z]{2}", message = "UF deve ter 2 letras") String uf,

        // --- plano (opcional: null = Particular)
        String planoSaude,
        @Pattern(regexp = "\\d{0,25}", message = "Carteirinha deve conter só números") String carteirinha
) { }
