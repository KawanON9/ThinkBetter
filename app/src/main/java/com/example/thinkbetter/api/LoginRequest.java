package com.example.thinkbetter.api;

public class LoginRequest {
    public String email;
    public String senha;

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
