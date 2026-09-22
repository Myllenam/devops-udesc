package com.example.usuarios.integration;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de ponta a ponta, sem mocks: exigem os containers reais
 * de "usuarios" (porta 8082) e "auth" (porta 8083) já rodando via docker compose,
 * com seus respectivos Postgres, se comunicando de verdade por HTTP.
 *
 * Pré-requisito: docker compose up -d
 */
class UsuarioApiIntegrationTest {

    private static final String USUARIOS_URL = System.getenv().getOrDefault("USUARIOS_URL", "http://localhost:8082");
    private static final String AUTH_URL = System.getenv().getOrDefault("AUTH_URL", "http://localhost:8083");

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void deveCadastrarViaAuthEPermitirAcessoAutenticadoAteAInativacao() throws Exception {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        String email = "usuario." + sufixo + "@email.com";
        String cpf = String.valueOf(System.nanoTime()).substring(0, 11);

        HttpResponse<String> registro = post(AUTH_URL + "/auth/register", """
                {"nome":"Usuario Teste","cpf":"%s","email":"%s","telefone":"48999990000","senha":"senha123"}
                """.formatted(cpf, email), null);
        assertThat(registro.statusCode()).isEqualTo(201);
        String usuarioId = json.readTree(registro.body()).get("usuarioId").asString();

        HttpResponse<String> login = post(AUTH_URL + "/auth/login", """
                {"email":"%s","senha":"senha123"}
                """.formatted(email), null);
        assertThat(login.statusCode()).isEqualTo(200);
        String token = json.readTree(login.body()).get("accessToken").asString();

        HttpResponse<String> semToken = get(USUARIOS_URL + "/usuarios/" + usuarioId, null);
        assertThat(semToken.statusCode()).isEqualTo(401);

        HttpResponse<String> comToken = get(USUARIOS_URL + "/usuarios/" + usuarioId, token);
        assertThat(comToken.statusCode()).isEqualTo(200);
        JsonNode usuario = json.readTree(comToken.body());
        assertThat(usuario.get("email").asString()).isEqualTo(email);
        assertThat(usuario.get("status").asString()).isEqualTo("ATIVO");

        HttpResponse<String> situacaoAntes = get(USUARIOS_URL + "/usuarios/" + usuarioId + "/situacao", null);
        assertThat(situacaoAntes.statusCode()).isEqualTo(200);
        assertThat(json.readTree(situacaoAntes.body()).get("podeRealizarEmprestimo").asBoolean()).isTrue();

        HttpResponse<String> atualizacao = put(USUARIOS_URL + "/usuarios/" + usuarioId, """
                {"nome":"Usuario Atualizado","email":"%s","telefone":"48988887777"}
                """.formatted(email), token);
        assertThat(atualizacao.statusCode()).isEqualTo(200);
        assertThat(json.readTree(atualizacao.body()).get("nome").asString()).isEqualTo("Usuario Atualizado");

        HttpResponse<String> inativacao = delete(USUARIOS_URL + "/usuarios/" + usuarioId, token);
        assertThat(inativacao.statusCode()).isEqualTo(204);

        HttpResponse<String> situacaoDepois = get(USUARIOS_URL + "/usuarios/" + usuarioId + "/situacao", null);
        JsonNode situacao = json.readTree(situacaoDepois.body());
        assertThat(situacao.get("status").asString()).isEqualTo("INATIVO");
        assertThat(situacao.get("podeRealizarEmprestimo").asBoolean()).isFalse();

        HttpResponse<String> loginAposInativar = post(AUTH_URL + "/auth/login", """
                {"email":"%s","senha":"senha123"}
                """.formatted(email), null);
        assertThat(loginAposInativar.statusCode()).isEqualTo(403);
    }

    @Test
    void deveRetornar409QuandoCpfJaCadastrado() throws Exception {
        String cpf = String.valueOf(System.nanoTime()).substring(0, 11);
        String corpo = """
                {"nome":"Primeiro","email":"primeiro.%s@email.com","cpf":"%s","telefone":"48999990000"}
                """.formatted(cpf, cpf);

        assertThat(post(USUARIOS_URL + "/usuarios", corpo, null).statusCode()).isEqualTo(201);

        String corpoDuplicado = """
                {"nome":"Segundo","email":"segundo.%s@email.com","cpf":"%s","telefone":"48999991111"}
                """.formatted(cpf, cpf);
        assertThat(post(USUARIOS_URL + "/usuarios", corpoDuplicado, null).statusCode()).isEqualTo(409);
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        HttpResponse<String> resposta = get(USUARIOS_URL + "/usuarios/999999999/situacao", null);
        assertThat(resposta.statusCode()).isEqualTo(404);
    }

    @Test
    void deveRetornar400QuandoDadosInvalidosNoCadastro() throws Exception {
        HttpResponse<String> resposta = post(USUARIOS_URL + "/usuarios", """
                {"nome":"","email":"email-invalido","cpf":"","telefone":""}
                """, null);
        assertThat(resposta.statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> post(String url, String body, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        autenticar(builder, token);
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> put(String url, String body, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body));
        autenticar(builder, token);
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String url, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).GET();
        autenticar(builder, token);
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String url, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).DELETE();
        autenticar(builder, token);
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void autenticar(HttpRequest.Builder builder, String token) {
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
    }
}
