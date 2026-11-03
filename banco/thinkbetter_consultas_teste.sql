-- =====================================================================
--  ThinkBetter — Consultas de TESTE (para as telas iniciais)
--  Rode depois do thinkbetter_dados_teste.sql (pode rodar de novo
--  em outro dia: ele cria os horários de HOJE e dos próximos dias):
--    sudo mysql < thinkbetter_consultas_teste.sql
--
--  Resultado:
--    • Dr. Rafael (rafael@thinkbetter.test) vê HOJE, na "Agenda de Hoje":
--        14:00 Carlos Mendes (Pendente) · 16:00 Fernanda Lima (Confirmada)
--    • Carlos (carlos@thinkbetter.test) vê em "Meus Próximos Atendimentos":
--        hoje 14:00 com o Dr. Rafael (enquanto não terminar) · amanhã 10:00
--        com a Dra. Ana (Online)
-- =====================================================================

SET NAMES utf8mb4;
USE thinkbetter;

SET @rafael   = (SELECT ID_Psicologo FROM Psicologos WHERE CRP_Psicologo = '06/67890');
SET @ana      = (SELECT ID_Psicologo FROM Psicologos WHERE CRP_Psicologo = '06/12345');
SET @carlos   = (SELECT ID_Paciente  FROM Pacientes  WHERE CPF_Paciente  = '00000000353');
SET @fernanda = (SELECT ID_Paciente  FROM Pacientes  WHERE CPF_Paciente  = '00000000434');

-- Horários usados pelas consultas (INSERT IGNORE: não duplica se já existirem)
INSERT IGNORE INTO Disponibilidades (ID_Psicologo, Hora_Inicio, Hora_Termino) VALUES
    (@rafael, TIMESTAMP(CURDATE(), '14:00'),                   TIMESTAMP(CURDATE(), '15:00')),
    (@rafael, TIMESTAMP(CURDATE(), '16:00'),                   TIMESTAMP(CURDATE(), '17:00')),
    (@ana,    TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '10:00'),  TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '11:00'));

-- Consultas (INSERT IGNORE: se o horário já tiver consulta ativa, pula)
INSERT IGNORE INTO Consultas (ID_Disponibilidade, ID_Paciente, Tipo_Consulta, Modalidade, Status_Consulta)
SELECT ID_Disponibilidade, @carlos, 'Sessao', 'Presencial', 'Pendente'
FROM Disponibilidades WHERE ID_Psicologo = @rafael AND Hora_Inicio = TIMESTAMP(CURDATE(), '14:00');

INSERT IGNORE INTO Consultas (ID_Disponibilidade, ID_Paciente, Tipo_Consulta, Modalidade, Status_Consulta)
SELECT ID_Disponibilidade, @fernanda, 'Avaliacao', 'Online', 'Confirmada'
FROM Disponibilidades WHERE ID_Psicologo = @rafael AND Hora_Inicio = TIMESTAMP(CURDATE(), '16:00');

INSERT IGNORE INTO Consultas (ID_Disponibilidade, ID_Paciente, Tipo_Consulta, Modalidade, Status_Consulta, Link_Online)
SELECT ID_Disponibilidade, @carlos, 'Retorno', 'Online', 'Confirmada', 'https://meet.google.com/'
FROM Disponibilidades WHERE ID_Psicologo = @ana AND Hora_Inicio = TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '10:00');
