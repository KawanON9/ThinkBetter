-- =====================================================================
--  ThinkBetter — Cadastro dos 2 psicólogos do Agendar Consulta
--  (os mesmos cards da tela: Dra. Ana Souza e Dr. Rafael Costa)
--
--    sudo mysql < thinkbetter_psicologos.sql
--
--  Pode rodar quantas vezes quiser: quem já existe não é duplicado
--  (se você já rodou o thinkbetter_dados_teste.sql, os dois já existem).
--
--  Login dos psicólogos (senha inicial:  Teste123 )
--    Dra. Ana Souza   → ana@thinkbetter.test
--    Dr. Rafael Costa → rafael@thinkbetter.test
--  Dados pessoais fictícios, para a prova de conceito.
-- =====================================================================

SET NAMES utf8mb4;
USE thinkbetter;

-- BCrypt de "Teste123"
SET @senha = '$2a$10$wQ3nzq/d8VdUmyRPwBopW.uwG2IiNOVoma8UubS.yLBN.NbaAYyBe';

-- 1) Logins (tabela Usuarios)
INSERT IGNORE INTO Usuarios (Email_Usuario, Senha_Hash, Tipo_Usuario) VALUES
    ('ana@thinkbetter.test',    @senha, 'PSICOLOGO'),
    ('rafael@thinkbetter.test', @senha, 'PSICOLOGO');

-- 2) Dados profissionais (tabela Psicologos), ligados aos logins acima
INSERT IGNORE INTO Psicologos
    (ID_Usuario, Nome_Psicologo, Sobrenome_Psicologo, CPF_Psicologo, DataNasc_Psicologo,
     Telefone_Psicologo, CRP_Psicologo, Abordagem_Psicologo, Valor_Sessao_Psicologo,
     CEP_Psicologo, Logradouro_Psicologo, Numero_Psicologo, Bairro_Psicologo,
     Cidade_Psicologo, UF_Psicologo)
SELECT u.ID_Usuario, 'Ana', 'Souza', '00000000191', '1988-04-12', '11990000010', '06/12345',
       'Terapia Cognitivo-Comportamental', 180.00,
       '01310100', 'Rua Exemplo', '100', 'Bela Vista', 'São Paulo', 'SP'
FROM Usuarios u WHERE u.Email_Usuario = 'ana@thinkbetter.test';

INSERT IGNORE INTO Psicologos
    (ID_Usuario, Nome_Psicologo, Sobrenome_Psicologo, CPF_Psicologo, DataNasc_Psicologo,
     Telefone_Psicologo, CRP_Psicologo, Abordagem_Psicologo, Valor_Sessao_Psicologo,
     CEP_Psicologo, Logradouro_Psicologo, Numero_Psicologo, Bairro_Psicologo,
     Cidade_Psicologo, UF_Psicologo)
SELECT u.ID_Usuario, 'Rafael', 'Costa', '00000000272', '1984-09-30', '11990000011', '06/67890',
       'Psicanálise', 200.00,
       '01310100', 'Rua Exemplo', '200', 'Bela Vista', 'São Paulo', 'SP'
FROM Usuarios u WHERE u.Email_Usuario = 'rafael@thinkbetter.test';

-- Conferência
SELECT s.ID_Psicologo, CONCAT(s.Nome_Psicologo, ' ', s.Sobrenome_Psicologo) AS Psicologo,
       s.CRP_Psicologo, s.Abordagem_Psicologo, u.Email_Usuario
FROM Psicologos s JOIN Usuarios u USING (ID_Usuario)
ORDER BY s.Nome_Psicologo;
