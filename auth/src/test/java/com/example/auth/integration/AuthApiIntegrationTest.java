package com.example.auth.integration;

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
 * de "auth" (porta 8083) e "usuarios" (porta 8082) já rodando via docker compose,
 * com seus respectivos Postgres, se comunicando de verdade por HTTP.
 *
 * Pré-requisito: docker compose up -d
 */
class AuthApiIntegrationTest {

    private static final String AUTH_URL = System.getenv().getOrDefault("AUTH_URL", "http://localhost:8083");

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void deveRegistrarLogarEValidarToken() throws Exception {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        String email = "auth." + sufixo + "@email.com";
        String cpf = String.valueOf(System.nanoTime()).substring(0, 11);

        HttpResponse<String> registro = post("/auth/register", """
                {"nome":"Joao Teste","cpf":"%s","email":"%s","telefone":"48999990000","senha":"senha123"}
                """.formatted(cpf, email));
        assertThat(registro.statusCode()).isEqualTo(201);
        String usuarioId = json.readTree(registro.body()).get("usuarioId").asString();

        HttpResponse<String> login = post("/auth/login", """
                {"email":"%s","senha":"senha123"}
                """.formatted(email));
        assertThat(login.statusCode()).isEqualTo(200);
        String token = json.readTree(login.body()).get("accessToken").asString();
        assertThat(token).isNotBlank();

        HttpResponse<String> validacao = post("/auth/validate", """
                {"token":"%s"}
                """.formatted(token));
        assertThat(validacao.statusCode()).isEqualTo(200);
        JsonNode corpo = json.readTree(validacao.body());
        assertThat(corpo.get("valido").asBoolean()).isTrue();
        assertThat(corpo.get("usuarioId").asString()).isEqualTo(usuarioId);
        assertThat(corpo.get("role").asString()).isEqualTo("USER");
    }

    @Test
    void deveRetornar401AoValidarTokenInvalido() throws Exception {
        HttpResponse<String> resposta = post("/auth/validate", """
                {"token":"isso-nao-e-um-jwt"}
                """);
        assertThat(resposta.statusCode()).isEqualTo(401);
        assertThat(json.readTree(resposta.body()).get("valido").asBoolean()).isFalse();
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        String email = "duplicado." + sufixo + "@email.com";
        String cpf1 = String.valueOf(System.nanoTime()).substring(0, 11);
        String cpf2 = String.valueOf(System.nanoTime() + 1).substring(0, 11);

        String primeiroRegistro = """
                {"nome":"Primeiro","cpf":"%s","email":"%s","telefone":"48999990000","senha":"senha123"}
                """.formatted(cpf1, email);
        assertThat(post("/auth/register", primeiroRegistro).statusCode()).isEqualTo(201);

        String segundoRegistro = """
                {"nome":"Segundo","cpf":"%s","email":"%s","telefone":"48999991111","senha":"outrasenha"}
                """.formatted(cpf2, email);
        assertThat(post("/auth/register", segundoRegistro).statusCode()).isEqualTo(409);
    }

    @Test
    void deveRetornar401QuandoSenhaIncorreta() throws Exception {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        String email = "senha." + sufixo + "@email.com";
        String cpf = String.valueOf(System.nanoTime()).substring(0, 11);

        String registro = """
                {"nome":"Teste Senha","cpf":"%s","email":"%s","telefone":"48999990000","senha":"senhaCorreta"}
                """.formatted(cpf, email);
        assertThat(post("/auth/register", registro).statusCode()).isEqualTo(201);

        HttpResponse<String> loginErrado = post("/auth/login", """
                {"email":"%s","senha":"senhaErrada"}
                """.formatted(email));
        assertThat(loginErrado.statusCode()).isEqualTo(401);
    }

    @Test
    void deveRetornar400QuandoDadosInvalidosNoRegistro() throws Exception {
        HttpResponse<String> resposta = post("/auth/register", """
                {"nome":"","cpf":"","email":"nao-e-email","telefone":"","senha":"123"}
                """);
        assertThat(resposta.statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(AUTH_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
