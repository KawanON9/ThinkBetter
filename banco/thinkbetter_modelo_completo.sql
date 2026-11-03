-- =====================================================================
--  ThinkBetter — Modelo de dados completo (proposta)
--  Cobre as telas atuais do app: Login/Cadastro, Pacientes, Prontuário,
--  Agenda/Atendimentos, Agendar Consulta, Meus Próximos Atendimentos,
--  Emissão de Documentos e Meus Documentos.
--
--  Compatível com MySQL 8.0.16+ e MariaDB 10.4+
--
--  ATENÇÃO: este script cria o banco do zero. Se você já rodou os scripts
--  anteriores (thinkbetter_schema.sql / v2), apague o banco antigo antes,
--  descomentando a linha abaixo (isso APAGA todos os dados dele):
-- DROP DATABASE IF EXISTS thinkbetter;
-- =====================================================================

-- Garante que acentos ("Bradesco Saúde", "São Paulo") sejam gravados
-- corretamente, qualquer que seja a configuração do cliente mysql.
SET NAMES utf8mb4;

CREATE DATABASE thinkbetter
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE thinkbetter;

-- =====================================================================
--  1. ACESSO
-- =====================================================================

-- Login único para pacientes e psicólogos. A tela de Login não precisa
-- saber o tipo antes: busca o e-mail aqui e o Tipo_Usuario decide a tela.
CREATE TABLE Usuarios (
    ID_Usuario       INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    Email_Usuario    VARCHAR(120)  NOT NULL,
    Senha_Hash       VARCHAR(100)  NOT NULL COMMENT 'BCrypt; a senha nunca é gravada em texto',
    Tipo_Usuario     ENUM('PACIENTE', 'PSICOLOGO') NOT NULL,
    Ativo_Usuario    BOOLEAN       NOT NULL DEFAULT TRUE COMMENT 'FALSE = conta desativada (Excluir Minha Conta)',
    Criado_Em        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_usuarios        PRIMARY KEY (ID_Usuario),
    CONSTRAINT uq_usuarios_email  UNIQUE (Email_Usuario)
) ENGINE = InnoDB;

-- =====================================================================
--  2. CADASTROS
-- =====================================================================

CREATE TABLE Plano_De_Saude (
    ID_Plano          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    Nome_Plano        VARCHAR(80)   NOT NULL,
    Telefone_Plano    VARCHAR(15)   NULL COMMENT 'Somente dígitos; aceita 0800',
    Email_Plano       VARCHAR(120)  NULL,
    Site_Plano        VARCHAR(150)  NULL,

    CONSTRAINT pk_plano       PRIMARY KEY (ID_Plano),
    CONSTRAINT uq_plano_nome  UNIQUE (Nome_Plano),
    CONSTRAINT ck_plano_tel   CHECK (Telefone_Plano IS NULL OR Telefone_Plano REGEXP '^[0-9]{8,15}$')
) ENGINE = InnoDB;

CREATE TABLE Psicologos (
    ID_Psicologo             INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Usuario               INT UNSIGNED  NOT NULL,
    Nome_Psicologo           VARCHAR(80)   NOT NULL,
    Sobrenome_Psicologo      VARCHAR(80)   NOT NULL,
    CPF_Psicologo            CHAR(11)      NOT NULL COMMENT 'Somente dígitos',
    DataNasc_Psicologo       DATE          NOT NULL,
    Telefone_Psicologo       VARCHAR(11)   NOT NULL COMMENT 'DDD + número, somente dígitos',
    CRP_Psicologo            VARCHAR(10)   NOT NULL COMMENT 'Ex.: 06/67890',
    Abordagem_Psicologo      VARCHAR(80)   NULL COMMENT 'Ex.: Terapia Cognitivo-Comportamental (card do Agendar Consulta)',
    Valor_Sessao_Psicologo   DECIMAL(8,2)  NULL COMMENT 'Valor padrão, usado nos recibos',
    -- Endereço do consultório (consultas presenciais)
    CEP_Psicologo            CHAR(8)       NULL,
    Logradouro_Psicologo     VARCHAR(120)  NULL,
    Numero_Psicologo         VARCHAR(10)   NULL,
    Complemento_Psicologo    VARCHAR(80)   NULL,
    Bairro_Psicologo         VARCHAR(80)   NULL,
    Cidade_Psicologo         VARCHAR(80)   NULL,
    UF_Psicologo             CHAR(2)       NULL,

    CONSTRAINT pk_psicologos          PRIMARY KEY (ID_Psicologo),
    CONSTRAINT uq_psicologos_usuario  UNIQUE (ID_Usuario),
    CONSTRAINT uq_psicologos_cpf      UNIQUE (CPF_Psicologo),
    CONSTRAINT uq_psicologos_crp      UNIQUE (CRP_Psicologo),
    CONSTRAINT fk_psicologos_usuario  FOREIGN KEY (ID_Usuario)
        REFERENCES Usuarios (ID_Usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_psicologos_cpf      CHECK (CPF_Psicologo REGEXP '^[0-9]{11}$'),
    CONSTRAINT ck_psicologos_tel      CHECK (Telefone_Psicologo REGEXP '^[0-9]{10,11}$'),
    CONSTRAINT ck_psicologos_crp      CHECK (CRP_Psicologo REGEXP '^[0-9]{2}/[0-9]{4,6}$'),
    CONSTRAINT ck_psicologos_cep      CHECK (CEP_Psicologo IS NULL OR CEP_Psicologo REGEXP '^[0-9]{8}$'),
    CONSTRAINT ck_psicologos_valor    CHECK (Valor_Sessao_Psicologo IS NULL OR Valor_Sessao_Psicologo >= 0)
) ENGINE = InnoDB;

CREATE TABLE Pacientes (
    ID_Paciente             INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Usuario              INT UNSIGNED  NOT NULL,
    Nome_Paciente           VARCHAR(80)   NOT NULL,
    Sobrenome_Paciente      VARCHAR(80)   NOT NULL,
    CPF_Paciente            CHAR(11)      NOT NULL COMMENT 'Somente dígitos',
    DataNasc_Paciente       DATE          NOT NULL,
    Telefone_Paciente       VARCHAR(11)   NOT NULL COMMENT 'DDD + número, somente dígitos',
    -- Endereço em partes (permite filtrar por cidade, gerar documentos etc.)
    CEP_Paciente            CHAR(8)       NOT NULL,
    Logradouro_Paciente     VARCHAR(120)  NOT NULL,
    Numero_Paciente         VARCHAR(10)   NOT NULL,
    Complemento_Paciente    VARCHAR(80)   NULL,
    Bairro_Paciente         VARCHAR(80)   NULL,
    Cidade_Paciente         VARCHAR(80)   NULL,
    UF_Paciente             CHAR(2)       NULL,
    -- Plano de saúde (NULL = Particular)
    ID_Plano                INT UNSIGNED  NULL,
    Carteirinha_Paciente    VARCHAR(25)   NULL,

    CONSTRAINT pk_pacientes          PRIMARY KEY (ID_Paciente),
    CONSTRAINT uq_pacientes_usuario  UNIQUE (ID_Usuario),
    CONSTRAINT uq_pacientes_cpf      UNIQUE (CPF_Paciente),
    CONSTRAINT fk_pacientes_usuario  FOREIGN KEY (ID_Usuario)
        REFERENCES Usuarios (ID_Usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_pacientes_plano    FOREIGN KEY (ID_Plano)
        REFERENCES Plano_De_Saude (ID_Plano) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_pacientes_cpf      CHECK (CPF_Paciente REGEXP '^[0-9]{11}$'),
    CONSTRAINT ck_pacientes_tel      CHECK (Telefone_Paciente REGEXP '^[0-9]{10,11}$'),
    CONSTRAINT ck_pacientes_cep      CHECK (CEP_Paciente REGEXP '^[0-9]{8}$'),
    CONSTRAINT ck_pacientes_cart     CHECK (Carteirinha_Paciente IS NULL OR Carteirinha_Paciente REGEXP '^[0-9]{1,25}$')
) ENGINE = InnoDB;

-- Planos que cada psicólogo atende (N:N). Permite mostrar ao paciente
-- só os psicólogos que aceitam o plano dele.
CREATE TABLE Psicologo_Plano (
    ID_Psicologo  INT UNSIGNED NOT NULL,
    ID_Plano      INT UNSIGNED NOT NULL,

    CONSTRAINT pk_psicologo_plano  PRIMARY KEY (ID_Psicologo, ID_Plano),
    CONSTRAINT fk_pp_psicologo     FOREIGN KEY (ID_Psicologo)
        REFERENCES Psicologos (ID_Psicologo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_pp_plano         FOREIGN KEY (ID_Plano)
        REFERENCES Plano_De_Saude (ID_Plano) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- =====================================================================
--  3. AGENDA
-- =====================================================================

-- Horários que o psicólogo abre na agenda (1 linha = 1 horário).
-- Bloqueada = TRUE quando ele usa "Indisponibilizar".
CREATE TABLE Disponibilidades (
    ID_Disponibilidade  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Psicologo        INT UNSIGNED  NOT NULL,
    Hora_Inicio         DATETIME      NOT NULL,
    Hora_Termino        DATETIME      NOT NULL,
    Bloqueada           BOOLEAN       NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_disponibilidades  PRIMARY KEY (ID_Disponibilidade),
    CONSTRAINT uq_disp_inicio       UNIQUE (ID_Psicologo, Hora_Inicio),
    CONSTRAINT fk_disp_psicologo    FOREIGN KEY (ID_Psicologo)
        REFERENCES Psicologos (ID_Psicologo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_disp_intervalo    CHECK (Hora_Termino > Hora_Inicio)
) ENGINE = InnoDB;

-- Uma consulta ocupa um horário. Consultas canceladas ficam no histórico,
-- mas liberam o horário para outra pessoa (ver Horario_Ativo abaixo).
CREATE TABLE Consultas (
    ID_Consulta         INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Disponibilidade  INT UNSIGNED  NOT NULL,
    ID_Paciente         INT UNSIGNED  NOT NULL,
    Tipo_Consulta       ENUM('Avaliacao', 'Sessao', 'Retorno')                    NOT NULL DEFAULT 'Sessao',
    Modalidade          ENUM('Presencial', 'Online')                              NOT NULL DEFAULT 'Presencial',
    Status_Consulta     ENUM('Pendente', 'Confirmada', 'Realizada', 'Cancelada')  NOT NULL DEFAULT 'Pendente',
    Valor_Consulta      DECIMAL(8,2)  NULL COMMENT 'NULL = coberto pelo plano',
    Link_Online         VARCHAR(255)  NULL COMMENT 'Link da videochamada (modalidade Online)',
    Criado_Em           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Cancelado_Em        DATETIME      NULL,
    -- Só consultas NÃO canceladas preenchem esta coluna; o UNIQUE nela
    -- impede duas consultas ativas no mesmo horário (NULLs podem repetir).
    Horario_Ativo       INT UNSIGNED  AS (IF(Status_Consulta = 'Cancelada', NULL, ID_Disponibilidade)) STORED,

    CONSTRAINT pk_consultas              PRIMARY KEY (ID_Consulta),
    CONSTRAINT uq_consultas_horario      UNIQUE (Horario_Ativo),
    -- RESTRICT nos dois casos: o MySQL não permite CASCADE na coluna-base
    -- de uma coluna gerada (Horario_Ativo).
    CONSTRAINT fk_consultas_horario      FOREIGN KEY (ID_Disponibilidade)
        REFERENCES Disponibilidades (ID_Disponibilidade) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_consultas_paciente     FOREIGN KEY (ID_Paciente)
        REFERENCES Pacientes (ID_Paciente) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_consultas_valor        CHECK (Valor_Consulta IS NULL OR Valor_Consulta >= 0),
    CONSTRAINT ck_consultas_cancelada    CHECK ((Status_Consulta = 'Cancelada') = (Cancelado_Em IS NOT NULL))
) ENGINE = InnoDB;

CREATE INDEX ix_consultas_paciente ON Consultas (ID_Paciente, Status_Consulta);

-- =====================================================================
--  4. PRONTUÁRIO
-- =====================================================================

-- Um prontuário por par paciente–psicólogo: as observações de um
-- profissional não aparecem para outro.
CREATE TABLE Prontuarios (
    ID_Prontuario     INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Paciente       INT UNSIGNED  NOT NULL,
    ID_Psicologo      INT UNSIGNED  NOT NULL,
    Observacoes       TEXT          NULL COMMENT 'Card "Observações clínicas"',
    Atualizado_Em     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_prontuarios        PRIMARY KEY (ID_Prontuario),
    CONSTRAINT uq_prontuario_par     UNIQUE (ID_Paciente, ID_Psicologo),
    CONSTRAINT fk_prontuario_pac     FOREIGN KEY (ID_Paciente)
        REFERENCES Pacientes (ID_Paciente) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_prontuario_psi     FOREIGN KEY (ID_Psicologo)
        REFERENCES Psicologos (ID_Psicologo) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- Registro de cada sessão (evolução). Fica no histórico, sem sobrescrever.
CREATE TABLE Evolucoes (
    ID_Evolucao     INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Prontuario   INT UNSIGNED  NOT NULL,
    ID_Consulta     INT UNSIGNED  NULL,
    Texto_Evolucao  TEXT          NOT NULL,
    Registrado_Em   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_evolucoes        PRIMARY KEY (ID_Evolucao),
    CONSTRAINT fk_evol_prontuario  FOREIGN KEY (ID_Prontuario)
        REFERENCES Prontuarios (ID_Prontuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_evol_consulta    FOREIGN KEY (ID_Consulta)
        REFERENCES Consultas (ID_Consulta) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB;

-- =====================================================================
--  5. DOCUMENTOS
-- =====================================================================

-- Emissão de Documentos (psicólogo) e Meus Documentos (paciente).
CREATE TABLE Documentos (
    ID_Documento      INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    ID_Paciente       INT UNSIGNED  NOT NULL,
    ID_Psicologo      INT UNSIGNED  NOT NULL,
    ID_Consulta       INT UNSIGNED  NULL COMMENT 'Ex.: recibo ou atestado de comparecimento de uma consulta',
    Tipo_Documento    ENUM('Atestado', 'Declaracao', 'Laudo', 'Recibo') NOT NULL,
    Titulo_Documento  VARCHAR(120)  NOT NULL COMMENT 'Ex.: Atestado de comparecimento',
    Periodo_Inicio    DATE          NULL,
    Periodo_Fim       DATE          NULL,
    CID_Documento     VARCHAR(10)   NULL COMMENT 'Opcional; ex.: F41.1',
    Descricao         TEXT          NULL,
    Valor_Documento   DECIMAL(8,2)  NULL COMMENT 'Só para recibos',
    Arquivo_PDF       VARCHAR(255)  NULL COMMENT 'Caminho do PDF gerado (o arquivo fica fora do banco)',
    Emitido_Em        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_documentos        PRIMARY KEY (ID_Documento),
    CONSTRAINT fk_doc_paciente      FOREIGN KEY (ID_Paciente)
        REFERENCES Pacientes (ID_Paciente) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_doc_psicologo     FOREIGN KEY (ID_Psicologo)
        REFERENCES Psicologos (ID_Psicologo) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_doc_consulta      FOREIGN KEY (ID_Consulta)
        REFERENCES Consultas (ID_Consulta) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT ck_doc_periodo       CHECK (Periodo_Fim IS NULL OR Periodo_Inicio IS NULL OR Periodo_Fim >= Periodo_Inicio),
    CONSTRAINT ck_doc_cid           CHECK (CID_Documento IS NULL OR CID_Documento REGEXP '^[A-Z][0-9]{2}(\\.[0-9]{1,2})?$'),
    CONSTRAINT ck_doc_recibo        CHECK (Tipo_Documento <> 'Recibo' OR Valor_Documento IS NOT NULL)
) ENGINE = InnoDB;

CREATE INDEX ix_documentos_paciente ON Documentos (ID_Paciente, Tipo_Documento, Emitido_Em);

-- =====================================================================
--  6. DADOS INICIAIS
-- =====================================================================
INSERT INTO Plano_De_Saude (Nome_Plano) VALUES
    ('Amil'), ('Bradesco Saúde'), ('Prevent Senior'), ('Unimed');
