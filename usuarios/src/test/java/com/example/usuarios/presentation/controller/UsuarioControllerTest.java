package com.example.usuarios.presentation.controller;

import com.example.usuarios.application.dto.CadastrarUsuarioRequest;
import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.application.usecase.AtualizarUsuarioUseCase;
import com.example.usuarios.application.usecase.BuscarUsuarioUseCase;
import com.example.usuarios.application.usecase.CadastrarUsuarioUseCase;
import com.example.usuarios.application.usecase.ConsultarSituacaoUseCase;
import com.example.usuarios.application.usecase.InativarUsuarioUseCase;
import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.exception.CpfJaCadastradoException;
import com.example.usuarios.infrastructure.client.AuthServiceClient;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastrarUsuarioUseCase cadastrarUsuarioUseCase;

    @MockitoBean
    private BuscarUsuarioUseCase buscarUsuarioUseCase;

    @MockitoBean
    private AtualizarUsuarioUseCase atualizarUsuarioUseCase;

    @MockitoBean
    private InativarUsuarioUseCase inativarUsuarioUseCase;

    @MockitoBean
    private ConsultarSituacaoUseCase consultarSituacaoUseCase;

    @MockitoBean
    private AuthServiceClient authServiceClient;

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "Maria", "maria@email.com", "12345678900", "48999998888");
        UsuarioResponse response = new UsuarioResponse(1L, "Maria", "maria@email.com",
                "12345678900", "48999998888", StatusUsuario.ATIVO, LocalDate.now());
        when(cadastrarUsuarioUseCase.executar(any())).thenReturn(response);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest("", "email-invalido", "", "");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoCpfJaCadastrado() throws Exception {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "Maria", "maria@email.com", "12345678900", "48999998888");
        when(cadastrarUsuarioUseCase.executar(any())).thenThrow(new CpfJaCadastradoException("12345678900"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
