package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.LoginRequest;
import br.com.thinkbetter.api.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * POST /login: uma única busca em Usuarios. O LEFT JOIN traz o perfil
 * (paciente ou psicólogo) ligado a esse login.
 */
@RestController
public class AuthController {

    private final JdbcTemplate db;
    private final BCryptPasswordEncoder bcrypt;

    public AuthController(JdbcTemplate db, BCryptPasswordEncoder bcrypt) {
        this.db = db;
        this.bcrypt = bcrypt;
    }

    private record Credencial(long idUsuario, String tipo, String hash, boolean ativo,
                              long idPerfil, String nome) { }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        List<Credencial> achados = db.query("""
                SELECT u.ID_Usuario, u.Tipo_Usuario, u.Senha_Hash, u.Ativo_Usuario,
                       COALESCE(pa.ID_Paciente, ps.ID_Psicologo) AS ID_Perfil,
                       COALESCE(CONCAT(pa.Nome_Paciente, ' ', pa.Sobrenome_Paciente),
                                CONCAT(ps.Nome_Psicologo, ' ', ps.Sobrenome_Psicologo)) AS Nome
                FROM Usuarios u
                LEFT JOIN Pacientes  pa ON pa.ID_Usuario = u.ID_Usuario
                LEFT JOIN Psicologos ps ON ps.ID_Usuario = u.ID_Usuario
                WHERE u.Email_Usuario = ?""",
                (rs, i) -> new Credencial(
                        rs.getLong("ID_Usuario"),
                        rs.getString("Tipo_Usuario"),
                        rs.getString("Senha_Hash"),
                        rs.getBoolean("Ativo_Usuario"),
                        rs.getLong("ID_Perfil"),
                        rs.getString("Nome")),
                req.email().trim().toLowerCase());

        // Mesma mensagem para e-mail inexistente, senha errada e conta desativada:
        // não revela quais e-mails estão cadastrados.
        if (achados.isEmpty()) throw naoAutorizado();
        Credencial c = achados.get(0);
        if (!c.ativo() || c.nome() == null || !bcrypt.matches(req.senha(), c.hash())) {
            throw naoAutorizado();
        }
        return new LoginResponse(c.idPerfil(), c.idUsuario(), c.tipo(), c.nome());
    }

    private static ApiException naoAutorizado() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos");
    }
}
