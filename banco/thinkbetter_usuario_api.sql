-- =====================================================================
--  ThinkBetter — Usuário do banco usado pela API
--  Rode depois do thinkbetter_modelo_completo.sql:
--    mysql -u root -p < thinkbetter_usuario_api.sql
--
--  Só acessa o banco 'thinkbetter', só a partir do próprio notebook,
--  e não pode alterar a estrutura das tabelas.
--  TROQUE a senha e use a mesma no application.properties da API.
-- =====================================================================
CREATE USER IF NOT EXISTS 'api'@'localhost' IDENTIFIED BY 'troque-esta-senha';
GRANT SELECT, INSERT, UPDATE, DELETE ON thinkbetter.* TO 'api'@'localhost';
FLUSH PRIVILEGES;
