package com.example.thinkbetter.api;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Ponto único de acesso à API.
 *
 * BASE_URL — troque pelo IP do notebook na rede do roteador:
 *   • celular físico no Wi-Fi do roteador → "http://192.168.0.10:8080/" (IP do notebook)
 *   • emulador rodando no próprio notebook → "http://10.0.2.2:8080/"
 * O mesmo IP precisa estar em res/xml/network_security_config.xml.
 */
public final class ApiClient {

    public static final String BASE_URL = "http://192.168.0.10:8080/";

    private static ThinkBetterApi api;
    private static final Gson GSON = new Gson();

    private ApiClient() { }

    public static synchronized ThinkBetterApi api() {
        if (api == null) {
            OkHttpClient http = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)   // notebook fora do ar → erro rápido
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            api = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(http)
                    .addConverterFactory(GsonConverterFactory.create(GSON))
                    .build()
                    .create(ThinkBetterApi.class);
        }
        return api;
    }

    /** Lê o {"erro": "..."} que a API devolve quando algo dá errado. */
    public static String mensagemDeErro(Response<?> resposta) {
        try (ResponseBody corpo = resposta.errorBody()) {
            if (corpo != null) {
                ErroResponse erro = GSON.fromJson(corpo.charStream(), ErroResponse.class);
                if (erro != null && erro.erro != null) return erro.erro;
            }
        } catch (RuntimeException ignorado) {
            // corpo vazio ou não é JSON: cai na mensagem genérica
        }
        return "Erro no servidor (" + resposta.code() + ")";
    }

    /** Mensagem amigável para falhas de rede (sem resposta do servidor). */
    public static String mensagemDeFalha(Throwable t) {
        return "Não foi possível conectar ao servidor. "
                + "Confira se o celular está no Wi-Fi do roteador e se a API está rodando.";
    }
}
