package br.com.thinkbetter.api;

import br.com.thinkbetter.api.dto.CadastroPacienteRequest;
import br.com.thinkbetter.api.dto.CadastroResponse;
import br.com.thinkbetter.api.dto.PacienteDetalhe;
import br.com.thinkbetter.api.dto.PacienteResumo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final JdbcTemplate db;
    private final BCryptPasswordEncoder bcrypt;

    public PacienteController(JdbcTemplate db, BCryptPasswordEncoder bcrypt) {
        this.db = db;
        this.bcrypt = bcrypt;
    }

    /**
     * POST /pacientes: tela Cadastro de Usuário.
     * Grava em DUAS tabelas (Usuarios e Pacientes) dentro de uma transação:
     * se a segunda falhar (ex.: CPF repetido), a primeira é desfeita e não
     * sobra um login "órfão" no banco.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CadastroResponse cadastrar(@Valid @RequestBody CadastroPacienteRequest req) {
        if (!Cpf.valido(req.cpf())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }

        // Plano: o app envia o nome ("Unimed"); o banco guarda o ID_Plano.
        Long idPlano = null;
        String carteirinha = null;
        if (temTexto(req.planoSaude())) {
            List<Long> ids = db.queryForList(
                    "SELECT ID_Plano FROM Plano_De_Saude WHERE Nome_Plano = ?",
                    Long.class, req.planoSaude().trim());
            if (ids.isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Plano de saúde não encontrado");
            }
            if (!temTexto(req.carteirinha())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o número da carteirinha");
            }
            idPlano = ids.get(0);
            carteirinha = req.carteirinha();
        }

        // 1) Login
        long idUsuario = inserir("""
                INSERT INTO Usuarios (Email_Usuario, Senha_Hash, Tipo_Usuario)
                VALUES (?, ?, 'PACIENTE')""",
                req.email().trim().toLowerCase(),
                bcrypt.encode(req.senha()));

        // 2) Dados pessoais
        long idPaciente = inserir("""
                INSERT INTO Pacientes
                    (ID_Usuario, Nome_Paciente, Sobrenome_Paciente, CPF_Paciente, DataNasc_Paciente,
                     Telefone_Paciente, CEP_Paciente, Logradouro_Paciente, Numero_Paciente,
                     Complemento_Paciente, Bairro_Paciente, Cidade_Paciente, UF_Paciente,
                     ID_Plano, Carteirinha_Paciente)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                idUsuario,
                req.nome().trim(),
                req.sobrenome().trim(),
                req.cpf(),
                Date.valueOf(req.dataNascimento()),
                req.telefone(),
                req.cep(),
                req.logradouro().trim(),
                req.numero().trim(),
                textoOuNull(req.complemento()),
                textoOuNull(req.bairro()),
                req.cidade().trim(),
                req.uf().toUpperCase(),
                idPlano,
                carteirinha);

        return new CadastroResponse(idPaciente);
    }

    /** GET /pacientes: tela Pacientes (lista em ordem alfabética). */
    @GetMapping
    public List<PacienteResumo> listar() {
        return db.query("""
                SELECT p.ID_Paciente, p.Nome_Paciente, p.Sobrenome_Paciente,
                       p.Telefone_Paciente, COALESCE(pl.Nome_Plano, 'Particular') AS Plano
                FROM Pacientes p
                JOIN Usuarios u ON u.ID_Usuario = p.ID_Usuario AND u.Ativo_Usuario = TRUE
                LEFT JOIN Plano_De_Saude pl ON pl.ID_Plano = p.ID_Plano
                ORDER BY p.Nome_Paciente, p.Sobrenome_Paciente""",
                (rs, i) -> new PacienteResumo(
                        rs.getLong("ID_Paciente"),
                        rs.getString("Nome_Paciente"),
                        rs.getString("Sobrenome_Paciente"),
                        rs.getString("Telefone_Paciente"),
                        rs.getString("Plano")));
    }

    /**
     * GET /pacientes/{id}?psicologo={idPsicologo}: tela Prontuário.
     * As observações vêm do prontuário DESSE psicólogo com o paciente.
     */
    @GetMapping("/{id}")
    public PacienteDetalhe detalhar(@PathVariable long id,
                                    @RequestParam(name = "psicologo", required = false) Long idPsicologo) {
        List<PacienteDetalhe> achados = db.query("""
                SELECT p.*, u.Email_Usuario,
                       COALESCE(pl.Nome_Plano, 'Particular') AS Plano,
                       pr.Observacoes
                FROM Pacientes p
                JOIN Usuarios u ON u.ID_Usuario = p.ID_Usuario
                LEFT JOIN Plano_De_Saude pl ON pl.ID_Plano = p.ID_Plano
                LEFT JOIN Prontuarios pr ON pr.ID_Paciente = p.ID_Paciente
                                        AND pr.ID_Psicologo = ?
                WHERE p.ID_Paciente = ?""",
                (rs, i) -> new PacienteDetalhe(
                        rs.getLong("ID_Paciente"),
                        rs.getString("Nome_Paciente"),
                        rs.getString("Sobrenome_Paciente"),
                        Cpf.mascarado(rs.getString("CPF_Paciente")),
                        rs.getDate("DataNasc_Paciente").toLocalDate(),
                        rs.getString("Telefone_Paciente"),
                        rs.getString("Email_Usuario"),
                        enderecoFormatado(rs),
                        rs.getString("CEP_Paciente"),
                        rs.getString("Plano"),
                        rs.getString("Carteirinha_Paciente"),
                        rs.getString("Observacoes")),
                idPsicologo, id);
        if (achados.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Paciente não encontrado");
        }
        return achados.get(0);
    }

    /** "Av. Paulista, 456 - Apto 31 - Bela Vista, São Paulo/SP" */
    private static String enderecoFormatado(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder()
                .append(rs.getString("Logradouro_Paciente"))
                .append(", ").append(rs.getString("Numero_Paciente"));
        String complemento = rs.getString("Complemento_Paciente");
        if (temTexto(complemento)) sb.append(" - ").append(complemento);
        String bairro = rs.getString("Bairro_Paciente");
        if (temTexto(bairro)) sb.append(" - ").append(bairro);
        String cidade = rs.getString("Cidade_Paciente");
        if (temTexto(cidade)) {
            sb.append(", ").append(cidade);
            String uf = rs.getString("UF_Paciente");
            if (temTexto(uf)) sb.append("/").append(uf);
        }
        return sb.toString();
    }

    /** Executa um INSERT e devolve o ID gerado (AUTO_INCREMENT). */
    private long inserir(String sql, Object... valores) {
        KeyHolder chave = new GeneratedKeyHolder();
        db.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
            return ps;
        }, chave);
        return chave.getKey().longValue();
    }

    private static boolean temTexto(String s) {
        return s != null && !s.isBlank();
    }

    private static String textoOuNull(String s) {
        return temTexto(s) ? s.trim() : null;
    }
}
