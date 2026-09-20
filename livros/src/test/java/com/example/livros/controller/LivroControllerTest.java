package com.example.livros.controller;

import com.example.livros.dto.AtualizarEstoqueDTO;
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

    @Test
    void deveCadastrarLivroERetornar201() throws Exception {

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

        mockMvc.perform(post("/api/livros")
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

        mockMvc.perform(get("/api/livros"))
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

        mockMvc.perform(get("/api/livros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.autor").value("Robert C. Martin"));

        verify(livroService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarEstoqueERetornar200() throws Exception {

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

        mockMvc.perform(patch("/api/livros/1/estoque")
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

        doNothing()
                .when(livroService)
                .realizarEmprestimo(1L);

        mockMvc.perform(post("/api/livros/1/emprestimo"))
                .andExpect(status().isNoContent());

        verify(livroService).realizarEmprestimo(1L);
    }

    @Test
    void deveRetornar400QuandoTituloEstiverVazio() throws Exception {

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

        mockMvc.perform(post("/api/livros")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isBadRequest());

        verify(livroService, never())
                .cadastrar(any());
    }

    @Test
    void deveRetornar400QuandoQuantidadeTotalForZero() throws Exception {

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

        mockMvc.perform(post("/api/livros")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isBadRequest());

        verify(livroService, never())
                .cadastrar(any());
    }

}
