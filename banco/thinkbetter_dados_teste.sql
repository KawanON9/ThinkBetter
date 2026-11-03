-- =====================================================================
--  ThinkBetter — Dados de TESTE para o modelo completo
--  Rode depois do thinkbetter_modelo_completo.sql:
--    mysql -u root -p < thinkbetter_dados_teste.sql
--
--  Logins de teste (todos com a senha:  Teste123 )
--    Psicóloga : ana@thinkbetter.test
--    Psicólogo : rafael@thinkbetter.test
--    Paciente  : carlos@thinkbetter.test
--    Paciente  : fernanda@thinkbetter.test
--
--  CPFs, telefones e endereços são fictícios.
--  Não use estes dados nem esta senha fora do ambiente de testes.
-- =====================================================================

-- Garante que acentos ("Bradesco Saúde", "São Paulo") sejam gravados
-- corretamente, qualquer que seja a configuração do cliente mysql.
SET NAMES utf8mb4;

USE thinkbetter;

-- BCrypt de "Teste123"
SET @senha = '$2a$10$wQ3nzq/d8VdUmyRPwBopW.uwG2IiNOVoma8UubS.yLBN.NbaAYyBe';

-- ---------------------------------------------------------------------
--  Logins
-- ---------------------------------------------------------------------
INSERT INTO Usuarios (Email_Usuario, Senha_Hash, Tipo_Usuario) VALUES
    ('ana@thinkbetter.test',      @senha, 'PSICOLOGO'),
    ('rafael@thinkbetter.test',   @senha, 'PSICOLOGO'),
    ('carlos@thinkbetter.test',   @senha, 'PACIENTE'),
    ('fernanda@thinkbetter.test', @senha, 'PACIENTE');

-- ---------------------------------------------------------------------
--  Psicólogos
-- ---------------------------------------------------------------------
INSERT INTO Psicologos
    (ID_Usuario, Nome_Psicologo, Sobrenome_Psicologo, CPF_Psicologo, DataNasc_Psicologo,
     Telefone_Psicologo, CRP_Psicologo, Abordagem_Psicologo, Valor_Sessao_Psicologo,
     CEP_Psicologo, Logradouro_Psicologo, Numero_Psicologo, Bairro_Psicologo,
     Cidade_Psicologo, UF_Psicologo)
VALUES
    ((SELECT ID_Usuario FROM Usuarios WHERE Email_Usuario = 'ana@thinkbetter.test'),
     'Ana', 'Souza', '00000000191', '1988-04-12', '11990000010', '06/12345',
     'Terapia Cognitivo-Comportamental', 180.00,
     '01310100', 'Rua Exemplo', '100', 'Bela Vista', 'São Paulo', 'SP'),
    ((SELECT ID_Usuario FROM Usuarios WHERE Email_Usuario = 'rafael@thinkbetter.test'),
     'Rafael', 'Costa', '00000000272', '1984-09-30', '11990000011', '06/67890',
     'Psicanálise', 200.00,
     '01310100', 'Rua Exemplo', '200', 'Bela Vista', 'São Paulo', 'SP');

INSERT INTO Psicologo_Plano (ID_Psicologo, ID_Plano)
SELECT p.ID_Psicologo, pl.ID_Plano
FROM Psicologos p
JOIN Plano_De_Saude pl ON pl.Nome_Plano IN ('Unimed', 'Amil')
WHERE p.CRP_Psicologo = '06/12345';

-- ---------------------------------------------------------------------
--  Pacientes
-- ---------------------------------------------------------------------
INSERT INTO Pacientes
    (ID_Usuario, Nome_Paciente, Sobrenome_Paciente, CPF_Paciente, DataNasc_Paciente,
     Telefone_Paciente, CEP_Paciente, Logradouro_Paciente, Numero_Paciente,
     Complemento_Paciente, Bairro_Paciente, Cidade_Paciente, UF_Paciente,
     ID_Plano, Carteirinha_Paciente)
VALUES
    ((SELECT ID_Usuario FROM Usuarios WHERE Email_Usuario = 'carlos@thinkbetter.test'),
     'Carlos', 'Mendes', '00000000353', '1975-11-07', '11990000002',
     '01310100', 'Av. Paulista', '456', NULL, 'Bela Vista', 'São Paulo', 'SP',
     NULL, NULL),
    ((SELECT ID_Usuario FROM Usuarios WHERE Email_Usuario = 'fernanda@thinkbetter.test'),
     'Fernanda', 'Lima', '00000000434', '1990-02-20', '11990000003',
     '04001000', 'Rua das Flores', '12', 'Apto 31', 'Paraíso', 'São Paulo', 'SP',
     (SELECT ID_Plano FROM Plano_De_Saude WHERE Nome_Plano = 'Unimed'), '0001112223334');

-- Prontuário do Carlos com o Dr. Rafael (aparece na tela Prontuário)
INSERT INTO Prontuarios (ID_Paciente, ID_Psicologo, Observacoes)
SELECT pa.ID_Paciente, ps.ID_Psicologo, 'Depressão leve'
FROM Pacientes pa, Psicologos ps
WHERE pa.CPF_Paciente = '00000000353' AND ps.CRP_Psicologo = '06/67890';

-- ---------------------------------------------------------------------
--  Horários livres: próximos dias úteis a partir de hoje, das 14h às 18h
-- ---------------------------------------------------------------------
INSERT INTO Disponibilidades (ID_Psicologo, Hora_Inicio, Hora_Termino)
SELECT p.ID_Psicologo,
       TIMESTAMP(d.dia, h.hora),
       TIMESTAMP(d.dia, h.hora) + INTERVAL 1 HOUR
FROM Psicologos p
CROSS JOIN (
    SELECT CURDATE() + INTERVAL n DAY AS dia
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
          UNION SELECT 5 UNION SELECT 6) dias
    WHERE DAYOFWEEK(CURDATE() + INTERVAL n DAY) BETWEEN 2 AND 6
) d
CROSS JOIN (
    SELECT TIME('14:00') AS hora UNION SELECT TIME('15:00') UNION SELECT TIME('16:00')
    UNION SELECT TIME('17:00') UNION SELECT TIME('18:00')
) h;
