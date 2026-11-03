# ThinkBetter API

API REST entre o app Android e o banco MySQL/MariaDB (modelo completo).

## Rodar
1. Banco — rode, nesta ordem, os scripts da pasta `banco/`:
   1. `thinkbetter_modelo_completo.sql`
   2. `thinkbetter_usuario_api.sql`
   3. `thinkbetter_dados_teste.sql` (opcional)
2. Ajuste a senha em `src/main/resources/application.properties`
   (a mesma do `thinkbetter_usuario_api.sql`).
3. `mvn spring-boot:run`  (precisa de Java 21 e Maven: `sudo apt install openjdk-21-jdk maven`)

## Endpoints
| Método | Caminho                               | Uso no app          | Tabelas                                  |
|--------|---------------------------------------|---------------------|------------------------------------------|
| GET    | /health                               | Teste de conexão    | —                                        |
| POST   | /pacientes                            | Cadastro de Usuário | Usuarios + Pacientes (mesma transação)   |
| POST   | /login                                | Login               | Usuarios (+ Pacientes / Psicologos)      |
| GET    | /pacientes                            | Lista de Pacientes  | Pacientes, Plano_De_Saude                |
| GET    | /pacientes/{id}?psicologo={id}        | Prontuário          | Pacientes, Usuarios, Prontuarios         |

Erros sempre voltam como `{"erro": "mensagem"}`.
