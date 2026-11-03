package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.CadastroResponse;
import br.com.thinkbetter.api.dto.ConsultaPaciente;
import br.com.thinkbetter.api.dto.NovaConsultaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
public class ConsultaController {

    private final JdbcTemplate db;

    public ConsultaController(JdbcTemplate db) {
        this.db = db;
    }

    /**
     * GET /pacientes/{id}/consultas: "Meus Próximos Atendimentos".
     * Só consultas futuras (ou em andamento hoje) que não foram canceladas.
     */
    @GetMapping("/pacientes/{id}/consultas")
    public List<ConsultaPaciente> proximas(@PathVariable long id) {
        return db.query("""
                SELECT c.ID_Consulta, d.Hora_Inicio,
                       CONCAT(s.Nome_Psicologo, ' ', s.Sobrenome_Psicologo) AS Psicologo,
                       s.Abordagem_Psicologo, c.Modalidade, c.Status_Consulta, c.Link_Online
                FROM Consultas c
                JOIN Disponibilidades d ON d.ID_Disponibilidade = c.ID_Disponibilidade
                JOIN Psicologos s       ON s.ID_Psicologo = d.ID_Psicologo
                WHERE c.ID_Paciente = ?
                  AND c.Status_Consulta IN ('Pendente', 'Confirmada')
                  AND d.Hora_Termino >= NOW()
                ORDER BY d.Hora_Inicio""",
                (rs, i) -> new ConsultaPaciente(
                        rs.getLong("ID_Consulta"),
                        rs.getTimestamp("Hora_Inicio").toLocalDateTime(),
                        rs.getString("Psicologo"),
                        rs.getString("Abordagem_Psicologo"),
                        rs.getString("Modalidade"),
                        rs.getString("Status_Consulta"),
                        rs.getString("Link_Online")),
                id);
    }

    /**
     * POST /consultas: Agendar Consulta.
     * O app usa horários fixos (14h–18h). Se ainda não existir a linha em
     * Disponibilidades para aquele horário, ela é criada aqui; depois a
     * consulta ocupa o horário. O UNIQUE de Consultas.Horario_Ativo garante
     * que duas pessoas não marquem o mesmo horário, mesmo ao mesmo tempo.
     */
    @PostMapping("/consultas")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CadastroResponse agendar(@Valid @RequestBody NovaConsultaRequest req) {
        LocalDateTime inicio = req.inicio().withSecond(0).withNano(0);
        if (!inicio.isAfter(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Não é possível agendar em um horário que já passou");
        }
        Integer psicologoExiste = db.queryForObject(
                "SELECT COUNT(*) FROM Psicologos WHERE ID_Psicologo = ?", Integer.class, req.idPsicologo());
        if (psicologoExiste == null || psicologoExiste == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Psicólogo não encontrado");
        }

        // O paciente não pode ter duas consultas no mesmo horário (com psicólogos diferentes)
        Integer conflito = db.queryForObject("""
                SELECT COUNT(*) FROM Consultas c
                JOIN Disponibilidades d ON d.ID_Disponibilidade = c.ID_Disponibilidade
                WHERE c.ID_Paciente = ? AND c.Status_Consulta <> 'Cancelada' AND d.Hora_Inicio = ?""",
                Integer.class, req.idPaciente(), Timestamp.valueOf(inicio));
        if (conflito != null && conflito > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Você já tem uma consulta neste horário");
        }

        // 1) Garante a linha do horário (sessões de 1 hora)
        db.update("""
                INSERT IGNORE INTO Disponibilidades (ID_Psicologo, Hora_Inicio, Hora_Termino)
                VALUES (?, ?, ?)""",
                req.idPsicologo(), Timestamp.valueOf(inicio), Timestamp.valueOf(inicio.plusHours(1)));

        List<Map<String, Object>> horario = db.queryForList("""
                SELECT ID_Disponibilidade, Bloqueada FROM Disponibilidades
                WHERE ID_Psicologo = ? AND Hora_Inicio = ?""",
                req.idPsicologo(), Timestamp.valueOf(inicio));
        if (Boolean.TRUE.equals(toBoolean(horario.get(0).get("Bloqueada")))) {
            throw new ApiException(HttpStatus.CONFLICT, "Este horário está indisponível. Escolha outro.");
        }
        long idDisponibilidade = ((Number) horario.get(0).get("ID_Disponibilidade")).longValue();

        // 2) Ocupa o horário (se alguém marcou antes, o UNIQUE gera 409 "horário ocupado")
        KeyHolder chave = new GeneratedKeyHolder();
        db.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO Consultas (ID_Disponibilidade, ID_Paciente, Tipo_Consulta, Modalidade, Status_Consulta)
                    VALUES (?, ?, 'Sessao', ?, 'Pendente')""", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, idDisponibilidade);
            ps.setLong(2, req.idPaciente());
            ps.setString(3, req.modalidade());
            return ps;
        }, chave);
        return new CadastroResponse(chave.getKey().longValue());
    }

    /** BOOLEAN do MySQL pode chegar como Boolean ou como número (0/1). */
    private static Boolean toBoolean(Object valor) {
        if (valor instanceof Boolean b) return b;
        if (valor instanceof Number n) return n.intValue() != 0;
        return Boolean.FALSE;
    }

    /**
     * POST /consultas/{id}/confirmar
     *   ?paciente={idPaciente}   → paciente confirma presença (tela inicial do paciente)
     *   ?psicologo={idPsicologo} → psicólogo confirma o atendimento (Agenda / Atendimentos)
     * Só confirma consultas Pendentes do próprio paciente / da agenda do próprio psicólogo.
     */
    @PostMapping("/consultas/{id}/confirmar")
    public Map<String, String> confirmar(@PathVariable long id,
                                         @RequestParam(name = "paciente", required = false) Long idPaciente,
                                         @RequestParam(name = "psicologo", required = false) Long idPsicologo) {
        int alteradas;
        if (idPaciente != null) {
            alteradas = db.update("""
                    UPDATE Consultas SET Status_Consulta = 'Confirmada'
                    WHERE ID_Consulta = ? AND ID_Paciente = ? AND Status_Consulta = 'Pendente'""",
                    id, idPaciente);
        } else if (idPsicologo != null) {
            alteradas = db.update("""
                    UPDATE Consultas c
                    JOIN Disponibilidades d ON d.ID_Disponibilidade = c.ID_Disponibilidade
                    SET c.Status_Consulta = 'Confirmada'
                    WHERE c.ID_Consulta = ? AND d.ID_Psicologo = ? AND c.Status_Consulta = 'Pendente'""",
                    id, idPsicologo);
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o paciente ou o psicólogo");
        }
        if (alteradas == 0) throw naoEncontrada();
        return Map.of("status", "Confirmada");
    }

    /**
     * POST /consultas/{id}/cancelar?paciente={idPaciente}: botão "Cancelar".
     * A consulta fica no histórico como Cancelada e o horário volta a ficar livre.
     */
    @PostMapping("/consultas/{id}/cancelar")
    public Map<String, String> cancelar(@PathVariable long id,
                                        @RequestParam("paciente") long idPaciente) {
        int alteradas = db.update("""
                UPDATE Consultas SET Status_Consulta = 'Cancelada', Cancelado_Em = NOW()
                WHERE ID_Consulta = ? AND ID_Paciente = ?
                  AND Status_Consulta IN ('Pendente', 'Confirmada')""",
                id, idPaciente);
        if (alteradas == 0) throw naoEncontrada();
        return Map.of("status", "Cancelada");
    }

    /** Não existe, é de outro paciente ou já não pode mudar (ex.: já cancelada). */
    private static ApiException naoEncontrada() {
        return new ApiException(HttpStatus.NOT_FOUND, "Consulta não encontrada ou já atualizada");
    }
}
