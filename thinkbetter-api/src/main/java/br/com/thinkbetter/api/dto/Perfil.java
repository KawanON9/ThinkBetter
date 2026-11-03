package br.com.thinkbetter.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * "Meu Perfil" (GET e PUT /usuarios/{id}/perfil), igual para paciente e psicólogo.
 * nomeCompleto: "Carlos Mendes" — a 1ª palavra vira o nome, o resto o sobrenome.
 * tipo: PACIENTE | PSICOLOGO (só na resposta; ignorado no PUT)
 */
public record Perfil(
        @NotBlank(message = "Informe o nome completo") @Size(max = 160) String nomeCompleto,
        @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") @Size(max = 120) String email,
        @NotBlank(message = "Informe o telefone")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone com DDD inválido") String telefone,
        String tipo
) { }
