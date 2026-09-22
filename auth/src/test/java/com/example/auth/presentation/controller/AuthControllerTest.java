package com.example.auth.presentation.controller;

import com.example.auth.application.dto.LoginRequest;
import com.example.auth.application.dto.RegistrarRequest;
import com.example.auth.application.dto.RegistrarResponse;
import com.example.auth.application.dto.TokenResponse;
import com.example.auth.application.dto.ValidarTokenRequest;
import com.example.auth.application.dto.ValidarTokenResponse;
import com.example.auth.application.usecase.AutenticarUsuarioUseCase;
import com.example.auth.application.usecase.RegistrarUsuarioUseCase;
import com.example.auth.application.usecase.ValidarTokenUseCase;
import com.example.auth.domain.exception.CredenciaisInvalidasException;
import com.example.auth.domain.exception.EmailJaCadastradoException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @MockitoBean
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @MockitoBean
    private ValidarTokenUseCase validarTokenUseCase;

    @Test
    void deveRegistrarERetornar201() throws Exception {
        RegistrarRequest request = new RegistrarRequest("Maria", "12345678900", "maria@email.com",
                "48999998888", "senha123");
        when(registrarUsuarioUseCase.executar(any())).thenReturn(new RegistrarResponse("1", "maria@email.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value("1"));
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastradoNoRegistro() throws Exception {
        RegistrarRequest request = new RegistrarRequest("Maria", "12345678900", "maria@email.com",
                "48999998888", "senha123");
        when(registrarUsuarioUseCase.executar(any())).thenThrow(new EmailJaCadastradoException("maria@email.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveLogarERetornarToken() throws Exception {
        LoginRequest request = new LoginRequest("maria@email.com", "senha123");
        when(autenticarUsuarioUseCase.executar(any())).thenReturn(new TokenResponse("token-jwt"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token-jwt"));
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        LoginRequest request = new LoginRequest("maria@email.com", "senhaErrada");
        when(autenticarUsuarioUseCase.executar(any())).thenThrow(new CredenciaisInvalidasException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveValidarTokenComSucesso() throws Exception {
        ValidarTokenRequest request = new ValidarTokenRequest("token-valido");
        when(validarTokenUseCase.executar("token-valido"))
                .thenReturn(new ValidarTokenResponse(true, "1", "USER", null));

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        ValidarTokenRequest request = new ValidarTokenRequest("token-invalido");
        when(validarTokenUseCase.executar("token-invalido"))
                .thenReturn(new ValidarTokenResponse(false, null, null, "Token inválido"));

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valido").value(false));
    }
}
