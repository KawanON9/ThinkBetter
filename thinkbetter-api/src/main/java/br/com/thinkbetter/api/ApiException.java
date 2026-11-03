package br.com.thinkbetter.api;

import org.springframework.http.HttpStatus;

/** Erro de regra de negócio, devolvido ao app como {"erro": "..."}. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
