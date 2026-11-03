package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.HorarioOcupado;
import br.com.thinkbetter.api.dto.PsicologoResumo;
import br.com.thinkbetter.api.dto.SessaoAgenda;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/psicologos")
public class PsicologoController {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final JdbcTemplate db;

    public PsicologoController(JdbcTemplate db) {
        this.db = db;
    }

    /** GET /psicologos: psicólogos com login ativo (cards do Agendar Consulta). */
    @GetMapping
    public List<PsicologoResumo> listar() {
        return db.query("""
                SELECT s.ID_Psicologo, s.Nome_Psicologo, s.Sobrenome_Psicologo, s.Abordagem_Psicologo
                FROM Psicologos s
                JOIN Usuarios u ON u.ID_Usuario = s.ID_Usuario AND u.Ativo_Usuario = TRUE
                ORDER BY s.Nome_Psicologo, s.Sobrenome_Psicologo""",
                (rs, i) -> new PsicologoResumo(
                        rs.getLong("ID_Psicologo"),
                        rs.getString("Nome_Psicologo"),
                        rs.getString("Sobrenome_Psicologo"),
                        rs.getString("Abordagem_Psicologo")));
    }

    /**
     * GET /psicologos/{id}/agenda
     *   ?data=2026-11-03                  → um dia (tela inicial, Atendimentos)
     *   ?de=2026-11-01&ate=2026-11-30     → um período (Agenda do mês)
     *   (sem parâmetros)                  → hoje
     * Consultas canceladas não aparecem.
     */
    @GetMapping("/{id}/agenda")
    public List<SessaoAgenda> agenda(
            @PathVariable long id,
            @RequestParam(name = "data", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(name = "de", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(name = "ate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {

        LocalDate inicio;
        LocalDate fim;
        if (de != null && ate != null) {
            if (ate.isBefore(de) || de.plusDays(62).isBefore(ate)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Período inválido (máximo de 62 dias)");
            }
            inicio = de;
            fim = ate;
        } else {
            inicio = data != null ? data : LocalDate.now(FUSO);
            fim = inicio;
        }

        return db.query("""
                SELECT c.ID_Consulta,
                       DATE_FORMAT(d.Hora_Inicio, '%Y-%m-%d') AS Dia,
                       DATE_FORMAT(d.Hora_Inicio, '%H:%i')    AS Hora,
                       p.ID_Paciente, CONCAT(p.Nome_Paciente, ' ', p.Sobrenome_Paciente) AS Paciente,
                       COALESCE(pl.Nome_Plano, 'Particular') AS Plano,
                       c.Tipo_Consulta, c.Modalidade, c.Status_Consulta
                FROM Consultas c
                JOIN Disponibilidades d ON d.ID_Disponibilidade = c.ID_Disponibilidade
                JOIN Pacientes p        ON p.ID_Paciente = c.ID_Paciente
                LEFT JOIN Plano_De_Saude pl ON pl.ID_Plano = p.ID_Plano
                WHERE d.ID_Psicologo = ?
                  AND d.Hora_Inicio >= ? AND d.Hora_Inicio < ?
                  AND c.Status_Consulta <> 'Cancelada'
                ORDER BY d.Hora_Inicio""",
                (rs, i) -> new SessaoAgenda(
                        rs.getLong("ID_Consulta"),
                        rs.getString("Dia"),
                        rs.getString("Hora"),
                        rs.getLong("ID_Paciente"),
                        rs.getString("Paciente"),
                        rs.getString("Plano"),
                        rs.getString("Tipo_Consulta"),
                        rs.getString("Modalidade"),
                        rs.getString("Status_Consulta")),
                id, Date.valueOf(inicio), Date.valueOf(fim.plusDays(1)));
    }

    /**
     * GET /psicologos/{id}/horarios?data=2026-11-05&paciente=3
     * Horários do dia que NÃO estão livres: consultas ativas e horários bloqueados.
     * O app mostra os horários fixos e marca estes como ocupados / "sua consulta".
     */
    @GetMapping("/{id}/horarios")
    public List<HorarioOcupado> horariosOcupados(
            @PathVariable long id,
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam("paciente") long idPaciente) {

        return db.query("""
                SELECT DATE_FORMAT(d.Hora_Inicio, '%H:%i') AS Hora,
                       c.ID_Consulta, c.ID_Paciente, c.Modalidade, c.Status_Consulta
                FROM Disponibilidades d
                LEFT JOIN Consultas c ON c.Horario_Ativo = d.ID_Disponibilidade
                WHERE d.ID_Psicologo = ?
                  AND d.Hora_Inicio >= ? AND d.Hora_Inicio < ?
                  AND (c.ID_Consulta IS NOT NULL OR d.Bloqueada = TRUE)
                ORDER BY d.Hora_Inicio""",
                (rs, i) -> {
                    boolean minha = rs.getObject("ID_Paciente") != null
                            && rs.getLong("ID_Paciente") == idPaciente;
                    return minha
                            ? new HorarioOcupado(rs.getString("Hora"), true, rs.getLong("ID_Consulta"),
                                                 rs.getString("Modalidade"), rs.getString("Status_Consulta"))
                            : new HorarioOcupado(rs.getString("Hora"), false, null, null, null);
                },
                id, Date.valueOf(data), Date.valueOf(data.plusDays(1)));
    }
}
