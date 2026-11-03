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
}
