package com.example.livros.controller;

import com.example.livros.client.AuthServiceClient;
import com.example.livros.client.ValidarTokenResponse;
import com.example.livros.dto.LivroResponseDTO;
import com.example.livros.service.LivroService;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private LivroService livroService;

        @MockitoBean
        private AuthServiceClient authServiceClient;

        private void configurarTokenValido() {

                ValidarTokenResponse response = new ValidarTokenResponse(
                                true,
                                "1",
                                "ADMIN",
                                "Token válido");
                when(authServiceClient.validar("token-teste"))
                                .thenReturn(response);
        }

        @Test
        void deveCadastrarLivroERetornar201() throws Exception {

                configurarTokenValido();

                LivroResponseDTO response = new LivroResponseDTO(
                                1L,
                                "Clean Code",
                                "Robert C. Martin",
                                "9780132350884",
                                2008,
                                5,
                                5);

                when(livroService.cadastrar(any()))
                                .thenReturn(response);

                String json = """
                                {
                                    "titulo": "Clean Code",
                                    "autor": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "anoPublicacao": 2008,
                                    "quantidadeDisponivel": 5,
                                    "quantidadeTotal": 5
                                }
                                """;

                mockMvc.perform(post("/livros")
                                .header("Authorization", "Bearer token-teste")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                                .andExpect(jsonPath("$.autor").value("Robert C. Martin"))
                                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                                .andExpect(jsonPath("$.quantidadeDisponivel").value(5));

                verify(livroService).cadastrar(any());
        }

        @Test
        void deveListarTodosOsLivrosERetornar200() throws Exception {

                LivroResponseDTO livro1 = new LivroResponseDTO(
                                1L,
                                "Clean Code",
                                "Robert C. Martin",
                                "111",
                                2008,
                                5,
                                5);

                LivroResponseDTO livro2 = new LivroResponseDTO(
                                2L,
                                "Effective Java",
                                "Joshua Bloch",
                                "222",
                                2018,
                                3,
                                3);

                when(livroService.listarTodos())
                                .thenReturn(List.of(livro1, livro2));

                mockMvc.perform(get("/livros"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                                .andExpect(jsonPath("$[1].titulo").value("Effective Java"));

                verify(livroService).listarTodos();
        }

        @Test
        void deveBuscarLivroPorIdERetornar200() throws Exception {

                LivroResponseDTO response = new LivroResponseDTO(
                                1L,
                                "Clean Code",
                                "Robert C. Martin",
                                "111",
                                2008,
                                5,
                                5);

                when(livroService.buscarPorId(1L))
                                .thenReturn(response);

                mockMvc.perform(get("/livros/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                                .andExpect(jsonPath("$.autor").value("Robert C. Martin"));

                verify(livroService).buscarPorId(1L);
        }

        @Test
        void deveAtualizarEstoqueERetornar200() throws Exception {

                configurarTokenValido();

                LivroResponseDTO response = new LivroResponseDTO(
                                1L,
                                "Clean Code",
                                "Robert C. Martin",
                                "111",
                                2008,
                                8,
                                10);

                when(livroService.atualizarEstoque(eq(1L), any()))
                                .thenReturn(response);

                String json = """
                                {
                                    "quantidadeDisponivel": 8,
                                    "quantidadeTotal": 10
                                }
                                """;

                mockMvc.perform(patch("/livros/1/estoque")
                                .header("Authorization", "Bearer token-teste")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.quantidadeDisponivel").value(8))
                                .andExpect(jsonPath("$.quantidadeTotal").value(10));

                verify(livroService)
                                .atualizarEstoque(eq(1L), any());
        }

        @Test
        void deveRealizarEmprestimoERetornar204() throws Exception {

                configurarTokenValido();

                doNothing()
                                .when(livroService)
                                .realizarEmprestimo(1L);

                mockMvc.perform(post("/livros/1/emprestimo")
                                .header("Authorization", "Bearer token-teste"))
                                .andExpect(status().isNoContent());

                verify(livroService).realizarEmprestimo(1L);
        }

        @Test
        void deveRetornar400QuandoTituloEstiverVazio() throws Exception {

                configurarTokenValido();

                String json = """
                                {
                                    "titulo": "",
                                    "autor": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "anoPublicacao": 2008,
                                    "quantidadeDisponivel": 5,
                                    "quantidadeTotal": 5
                                }
                                """;

                mockMvc.perform(post("/livros")
                                .header("Authorization", "Bearer token-teste")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isBadRequest());

                verify(livroService, never())
                                .cadastrar(any());
        }

        @Test
        void deveRetornar400QuandoQuantidadeTotalForZero() throws Exception {

                configurarTokenValido();

                String json = """
                                {
                                    "titulo": "Clean Code",
                                    "autor": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "anoPublicacao": 2008,
                                    "quantidadeDisponivel": 0,
                                    "quantidadeTotal": 0
                                }
                                """;

                mockMvc.perform(post("/livros")
                                .header("Authorization", "Bearer token-teste")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isBadRequest());

                verify(livroService, never())
                                .cadastrar(any());
        }

        @Test
        void deveRetornar401QuandoCadastrarLivroSemToken() throws Exception {

                String json = """
                                {
                                    "titulo": "Clean Code",
                                    "autor": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "anoPublicacao": 2008,
                                    "quantidadeDisponivel": 5,
                                    "quantidadeTotal": 5
                                }
                                """;

                mockMvc.perform(post("/livros")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isUnauthorized());

                verify(livroService, never())
                                .cadastrar(any());
        }

        @Test
        void deveRetornar401QuandoTokenForInvalido() throws Exception {

                when(authServiceClient.validar("token-invalido"))
                                .thenReturn(new ValidarTokenResponse(
                                                false,
                                                null,
                                                null,
                                                "Token inválido"));

                String json = """
                                {
                                    "titulo": "Clean Code",
                                    "autor": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "anoPublicacao": 2008,
                                    "quantidadeDisponivel": 5,
                                    "quantidadeTotal": 5
                                }
                                """;

                mockMvc.perform(post("/livros")
                                .header("Authorization", "Bearer token-invalido")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isUnauthorized());

                verify(livroService, never())
                                .cadastrar(any());
        }
}