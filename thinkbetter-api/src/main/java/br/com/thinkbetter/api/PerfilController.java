package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.Perfil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * "Meu Perfil": nome, e-mail e telefone de quem está logado.
 * O e-mail fica em Usuarios; nome e telefone em Pacientes ou Psicologos.
 */
@RestController
public class PerfilController {

    private final JdbcTemplate db;

    public PerfilController(JdbcTemplate db) {
        this.db = db;
    }

    /** GET /usuarios/{idUsuario}/perfil */
    @GetMapping("/usuarios/{id}/perfil")
    public Perfil ler(@PathVariable long id) {
        List<Perfil> achados = db.query("""
                SELECT u.Email_Usuario, u.Tipo_Usuario,
                       COALESCE(CONCAT(pa.Nome_Paciente, ' ', pa.Sobrenome_Paciente),
                                CONCAT(ps.Nome_Psicologo, ' ', ps.Sobrenome_Psicologo)) AS Nome,
                       COALESCE(pa.Telefone_Paciente, ps.Telefone_Psicologo) AS Telefone
                FROM Usuarios u
                LEFT JOIN Pacientes  pa ON pa.ID_Usuario = u.ID_Usuario
                LEFT JOIN Psicologos ps ON ps.ID_Usuario = u.ID_Usuario
                WHERE u.ID_Usuario = ?""",
                (rs, i) -> new Perfil(rs.getString("Nome"), rs.getString("Email_Usuario"),
                        rs.getString("Telefone"), rs.getString("Tipo_Usuario")),
                id);
        if (achados.isEmpty() || achados.get(0).nomeCompleto() == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        return achados.get(0);
    }

    /** PUT /usuarios/{idUsuario}/perfil — grava em Usuarios e no perfil, numa transação só. */
    @PutMapping("/usuarios/{id}/perfil")
    @Transactional
    public Perfil salvar(@PathVariable long id, @Valid @RequestBody Perfil novo) {
        String[] partes = novo.nomeCompleto().trim().split("\\s+", 2);
        if (partes.length < 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe nome e sobrenome");
        }
        String nome = partes[0];
        String sobrenome = partes[1];
        if (nome.length() > 80 || sobrenome.length() > 80) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nome ou sobrenome muito longo (máx. 80 letras cada)");
        }

        String tipo = ler(id).tipo(); // também confirma que o usuário existe

        // E-mail repetido gera DuplicateKeyException → 409 "E-mail já cadastrado"
        db.update("UPDATE Usuarios SET Email_Usuario = ? WHERE ID_Usuario = ?",
                novo.email().trim().toLowerCase(), id);

        if ("PACIENTE".equals(tipo)) {
            db.update("""
                    UPDATE Pacientes SET Nome_Paciente = ?, Sobrenome_Paciente = ?, Telefone_Paciente = ?
                    WHERE ID_Usuario = ?""", nome, sobrenome, novo.telefone(), id);
        } else {
            db.update("""
                    UPDATE Psicologos SET Nome_Psicologo = ?, Sobrenome_Psicologo = ?, Telefone_Psicologo = ?
                    WHERE ID_Usuario = ?""", nome, sobrenome, novo.telefone(), id);
        }
        return ler(id);
    }
}
