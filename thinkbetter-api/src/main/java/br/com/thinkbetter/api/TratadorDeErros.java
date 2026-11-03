package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.ErroResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte exceções em respostas JSON no formato {"erro": "mensagem"},
 * que o app mostra diretamente ao usuário.
 */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> regra(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErroResponse(e.getMessage()));
    }

    /** Campo inválido (@NotBlank, @Pattern...): devolve a mensagem do primeiro. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getDefaultMessage())
                .orElse("Dados inválidos");
        return ResponseEntity.badRequest().body(new ErroResponse(msg));
    }

    /** JSON malformado ou data em formato errado. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErroResponse("Dados em formato inválido"));
    }

    /** UNIQUE do banco: CPF ou e-mail já cadastrado. */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErroResponse> duplicado(DuplicateKeyException e) {
        String detalhe = String.valueOf(e.getMostSpecificCause().getMessage());
        // Usa o nome da restrição UNIQUE do banco (ex.: 'Pacientes.uq_pacientes_cpf')
        String msg = detalhe.contains("uq_pacientes_cpf") ? "CPF já cadastrado"
                : detalhe.contains("uq_usuarios_email") ? "E-mail já cadastrado"
                : detalhe.contains("uq_consultas_horario") ? "Este horário acabou de ser ocupado. Escolha outro."
                : "Registro já cadastrado";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(msg));
    }

    /** Qualquer outro problema de banco: não expõe detalhes internos ao app. */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErroResponse> banco(DataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponse("Erro ao acessar o banco de dados"));
    }
}
