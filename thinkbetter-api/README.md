# ThinkBetter API

API REST entre o app Android e o banco MySQL/MariaDB (modelo completo).

## Rodar
1. Banco — rode, nesta ordem, os scripts da pasta `banco/`:
   1. `thinkbetter_modelo_completo.sql`
   2. `thinkbetter_usuario_api.sql`
   3. `thinkbetter_dados_teste.sql` (opcional)
   4. `thinkbetter_psicologos.sql` (garante os 2 psicólogos dos cards; não duplica)
   5. `thinkbetter_consultas_teste.sql` (opcional — consultas de hoje e amanhã para as telas iniciais)
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
| GET    | /psicologos                           | Cards do Agendar Consulta | Psicologos, Usuarios             |
| GET    | /psicologos/{id}/agenda?data=AAAA-MM-DD | Início e Atendimentos do psicólogo (um dia) | Consultas, Disponibilidades, Pacientes |
| GET    | /psicologos/{id}/agenda?de=AAAA-MM-DD&ate=AAAA-MM-DD | Agenda do mês do psicólogo | Consultas, Disponibilidades, Pacientes |
| GET    | /psicologos/{id}/horarios?data=AAAA-MM-DD&paciente={id} | Horários ocupados no Agendar Consulta | Disponibilidades, Consultas |
| POST   | /consultas                            | Agendar Consulta (marcar horário) | Disponibilidades, Consultas |
| GET    | /pacientes/{id}/consultas             | Início do paciente ("Meus Próximos Atendimentos") | Consultas, Disponibilidades, Psicologos |
| POST   | /consultas/{id}/confirmar?paciente={id} | Botão "Confirmar" do paciente | Consultas                  |
| POST   | /consultas/{id}/confirmar?psicologo={id} | Botão "Confirmar" do psicólogo (Agenda / Atendimentos) | Consultas, Disponibilidades |
| POST   | /consultas/{id}/cancelar?paciente={id}  | Botão "Cancelar"  | Consultas                              |
| POST   | /documentos/previa                    | Emissão de Documentos, etapa 4 (prévia, sem gravar) | Pacientes, Psicologos |
| POST   | /documentos                           | Emissão de Documentos, "Confirmar" (grava)          | Documentos            |
| GET    | /pacientes/{id}/documentos            | Meus Documentos do paciente                         | Documentos            |
| GET    | /usuarios/{id}/perfil                 | Meu Perfil (ler)                                    | Usuarios, Pacientes / Psicologos |
| PUT    | /usuarios/{id}/perfil                 | Meu Perfil (salvar)                                 | Usuarios, Pacientes / Psicologos |

Erros sempre voltam como `{"erro": "mensagem"}`.
