package com.example.thinkbetter.api;

public class LoginResponse {
    public static final String PACIENTE = "PACIENTE";
    public static final String PSICOLOGO = "PSICOLOGO";

    public long id;          // ID_Paciente ou ID_Psicologo (usado pelas outras telas)
    public long idUsuario;   // ID_Usuario (login)
    public String tipo;      // PACIENTE ou PSICOLOGO
    public String nome;
}
