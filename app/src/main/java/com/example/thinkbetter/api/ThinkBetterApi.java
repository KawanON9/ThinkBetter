package com.example.thinkbetter.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Endpoints da ThinkBetter API (ver README da API). */
public interface ThinkBetterApi {

    @POST("pacientes")
    Call<CadastroResponse> cadastrarPaciente(@Body CadastroPacienteRequest dados);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest dados);

    @GET("pacientes")
    Call<List<PacienteResumo>> listarPacientes();

    /** idPsicologo: de quem são as observações do prontuário (o psicólogo logado). */
    @GET("pacientes/{id}")
    Call<PacienteDetalhe> buscarPaciente(@Path("id") long id, @Query("psicologo") long idPsicologo);

    /** data: "aaaa-mm-dd" (dia de hoje na tela inicial do psicólogo). */
    @GET("psicologos/{id}/agenda")
    Call<List<SessaoAgenda>> agendaDoDia(@Path("id") long idPsicologo, @Query("data") String data);

    /** de / ate: "aaaa-mm-dd" (Agenda do mês do psicólogo). */
    @GET("psicologos/{id}/agenda")
    Call<List<SessaoAgenda>> agendaDoPeriodo(@Path("id") long idPsicologo,
                                             @Query("de") String de, @Query("ate") String ate);

    @GET("psicologos")
    Call<List<PsicologoResumo>> listarPsicologos();

    /** Horários do dia que não estão livres (Agendar Consulta). */
    @GET("psicologos/{id}/horarios")
    Call<List<HorarioOcupado>> horariosOcupados(@Path("id") long idPsicologo,
                                                @Query("data") String data,
                                                @Query("paciente") long idPaciente);

    @POST("consultas")
    Call<CadastroResponse> agendarConsulta(@Body NovaConsultaRequest dados);

    @GET("pacientes/{id}/consultas")
    Call<List<ConsultaPaciente>> proximasConsultas(@Path("id") long idPaciente);

    @POST("consultas/{id}/confirmar")
    Call<StatusResponse> confirmarConsulta(@Path("id") long idConsulta, @Query("paciente") long idPaciente);

    /** Psicólogo confirma um atendimento Pendente da própria agenda. */
    @POST("consultas/{id}/confirmar")
    Call<StatusResponse> confirmarConsultaComoPsicologo(@Path("id") long idConsulta, @Query("psicologo") long idPsicologo);

    /** Emissão de Documentos, etapa 4: monta o texto sem gravar. */
    @POST("documentos/previa")
    Call<DocumentoGerado> previaDocumento(@Body NovoDocumentoRequest dados);

    /** Emissão de Documentos, "Confirmar": grava e devolve o texto final. */
    @POST("documentos")
    Call<DocumentoGerado> emitirDocumento(@Body NovoDocumentoRequest dados);

    /** Meus Documentos do paciente. */
    @GET("pacientes/{id}/documentos")
    Call<List<DocumentoGerado>> documentosDoPaciente(@Path("id") long idPaciente);

    @POST("consultas/{id}/cancelar")
    Call<StatusResponse> cancelarConsulta(@Path("id") long idConsulta, @Query("paciente") long idPaciente);
}
