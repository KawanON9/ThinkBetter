package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.DocumentoGerado;
import br.com.thinkbetter.api.dto.NovoDocumentoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/** Emissão de Documentos (psicólogo) e Meus Documentos (paciente). */
@RestController
public class DocumentoController {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final JdbcTemplate db;

    public DocumentoController(JdbcTemplate db) {
        this.db = db;
    }

    /** POST /documentos/previa: monta o texto da etapa 4 sem gravar nada. */
    @PostMapping("/documentos/previa")
    public DocumentoGerado previa(@Valid @RequestBody NovoDocumentoRequest req) {
        validar(req);
        return montar(0, req.tipo(), req.idPaciente(), req.idPsicologo(),
                req.periodoInicio(), fimOuInicio(req), cidNormalizado(req.cid()), req.descricao(),
                LocalDate.now(FUSO));
    }

    /** POST /documentos: grava o documento (etapa 4 → "Confirmar") e devolve o texto final. */
    @PostMapping("/documentos")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoGerado emitir(@Valid @RequestBody NovoDocumentoRequest req) {
        validar(req);
        String cid = cidNormalizado(req.cid());
        LocalDate fim = fimOuInicio(req);

        KeyHolder chave = new GeneratedKeyHolder();
        db.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO Documentos
                        (ID_Paciente, ID_Psicologo, Tipo_Documento, Titulo_Documento,
                         Periodo_Inicio, Periodo_Fim, CID_Documento, Descricao)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, req.idPaciente());
            ps.setLong(2, req.idPsicologo());
            ps.setString(3, req.tipo());
            ps.setString(4, RedatorDeDocumentos.titulo(req.tipo()));
            ps.setObject(5, req.periodoInicio() != null ? Date.valueOf(req.periodoInicio()) : null);
            ps.setObject(6, fim != null ? Date.valueOf(fim) : null);
            ps.setString(7, cid);
            ps.setString(8, temTexto(req.descricao()) ? req.descricao().trim() : null);
            return ps;
        }, chave);

        return montar(chave.getKey().longValue(), req.tipo(), req.idPaciente(), req.idPsicologo(),
                req.periodoInicio(), fim, cid, req.descricao(), LocalDate.now(FUSO));
    }

    /** GET /pacientes/{id}/documentos: "Meus Documentos" (mais recentes primeiro). */
    @GetMapping("/pacientes/{id}/documentos")
    public List<DocumentoGerado> doPaciente(@PathVariable long id) {
        return db.query("""
                SELECT ID_Documento, Tipo_Documento, ID_Psicologo, Periodo_Inicio, Periodo_Fim,
                       CID_Documento, Descricao, DATE(Emitido_Em) AS Emitido
                FROM Documentos
                WHERE ID_Paciente = ?
                ORDER BY Emitido_Em DESC, ID_Documento DESC""",
                (rs, i) -> montar(
                        rs.getLong("ID_Documento"),
                        rs.getString("Tipo_Documento"),
                        id,
                        rs.getLong("ID_Psicologo"),
                        rs.getDate("Periodo_Inicio") != null ? rs.getDate("Periodo_Inicio").toLocalDate() : null,
                        rs.getDate("Periodo_Fim") != null ? rs.getDate("Periodo_Fim").toLocalDate() : null,
                        rs.getString("CID_Documento"),
                        rs.getString("Descricao"),
                        rs.getDate("Emitido").toLocalDate()),
                id);
    }

    // ------------------------------------------------------------------

    /** Regras que o @Valid sozinho não cobre. */
    private void validar(NovoDocumentoRequest req) {
        if (req.periodoFim() != null && req.periodoInicio() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe a data de início do período");
        }
        if (req.periodoInicio() != null && req.periodoFim() != null && req.periodoFim().isBefore(req.periodoInicio())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "O fim do período deve ser depois do início");
        }
        if ("Declaracao".equals(req.tipo()) && temTexto(req.cid())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Declarações não podem conter CID (informam só o comparecimento)");
        }
        if ("Laudo".equals(req.tipo()) && (!temTexto(req.descricao()) || req.descricao().trim().length() < 20)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "O laudo precisa de uma descrição técnica (mínimo de 20 caracteres)");
        }
    }

    /** Busca paciente e psicólogo no banco e escreve o texto. */
    private DocumentoGerado montar(long id, String tipo, long idPaciente, long idPsicologo,
                                   LocalDate inicio, LocalDate fim, String cid, String descricao,
                                   LocalDate emitidoEm) {
        List<String[]> paciente = db.query("""
                SELECT CONCAT(Nome_Paciente, ' ', Sobrenome_Paciente), CPF_Paciente
                FROM Pacientes WHERE ID_Paciente = ?""",
                (rs, i) -> new String[]{rs.getString(1), rs.getString(2)}, idPaciente);
        if (paciente.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Paciente não encontrado");

        List<String[]> psicologo = db.query("""
                SELECT CONCAT(Nome_Psicologo, ' ', Sobrenome_Psicologo), CRP_Psicologo,
                       Cidade_Psicologo, UF_Psicologo
                FROM Psicologos WHERE ID_Psicologo = ?""",
                (rs, i) -> new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)},
                idPsicologo);
        if (psicologo.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Psicólogo não encontrado");

        String[] pa = paciente.get(0);
        String[] ps = psicologo.get(0);
        String texto = RedatorDeDocumentos.texto(new RedatorDeDocumentos.Dados(
                tipo, pa[0], Cpf.mascarado(pa[1]), ps[0], ps[1], ps[2], ps[3],
                inicio, fim, cid, descricao, emitidoEm));

        return new DocumentoGerado(id, tipo, RedatorDeDocumentos.titulo(tipo), pa[0], ps[0], ps[1],
                emitidoEm.toString(), texto);
    }

    private static LocalDate fimOuInicio(NovoDocumentoRequest req) {
        return req.periodoFim() != null ? req.periodoFim() : req.periodoInicio();
    }

    private static String cidNormalizado(String cid) {
        return temTexto(cid) ? cid.trim().toUpperCase() : null;
    }

    private static boolean temTexto(String s) {
        return s != null && !s.isBlank();
    }
}
