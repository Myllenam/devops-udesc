package com.example.livros.service;

import com.example.livros.dto.AtualizarEstoqueDTO;
import com.example.livros.dto.LivroRequestDTO;
import com.example.livros.dto.LivroResponseDTO;
import com.example.livros.exception.IsbnJaCadastradoException;
import com.example.livros.exception.LivroIndisponivelException;
import com.example.livros.exception.LivroNaoEncontradoException;
import com.example.livros.model.Livro;
import com.example.livros.repository.LivroRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    @Test
    void deveCadastrarLivroQuandoIsbnNaoExistir() {

        LivroRequestDTO dto = new LivroRequestDTO(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                5,
                5);

        when(livroRepository.existsByIsbn("9780132350884"))
                .thenReturn(false);

        Livro livroSalvo = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                5,
                5);

        livroSalvo.setId(1L);

        when(livroRepository.save(any(Livro.class)))
                .thenReturn(livroSalvo);

        LivroResponseDTO resultado = livroService.cadastrar(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Clean Code", resultado.titulo());
        assertEquals("Robert C. Martin", resultado.autor());
        assertEquals("9780132350884", resultado.isbn());
        assertEquals(5, resultado.quantidadeDisponivel());
        assertEquals(5, resultado.quantidadeTotal());

        verify(livroRepository).existsByIsbn("9780132350884");
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoIsbnJaEstiverCadastrado() {

        LivroRequestDTO dto = new LivroRequestDTO(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                5,
                5);

        when(livroRepository.existsByIsbn("9780132350884"))
                .thenReturn(true);

        assertThrows(
                IsbnJaCadastradoException.class,
                () -> livroService.cadastrar(dto));

        verify(livroRepository).existsByIsbn("9780132350884");

        verify(livroRepository, never())
                .save(any(Livro.class));
    }

    @Test
    void deveBuscarLivroPorId() {

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                3,
                5);

        livro.setId(1L);

        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));

        LivroResponseDTO resultado = livroService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Clean Code", resultado.titulo());

        verify(livroRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoForEncontrado() {

        when(livroRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                LivroNaoEncontradoException.class,
                () -> livroService.buscarPorId(99L));

        verify(livroRepository).findById(99L);
    }

    @Test
    void deveListarTodosOsLivros() {

        Livro livro1 = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "111",
                2008,
                5,
                5);

        Livro livro2 = new Livro(
                "Effective Java",
                "Joshua Bloch",
                "222",
                2018,
                3,
                3);

        livro1.setId(1L);
        livro2.setId(2L);

        when(livroRepository.findAll())
                .thenReturn(List.of(livro1, livro2));

        List<LivroResponseDTO> resultado = livroService.listarTodos();

        assertEquals(2, resultado.size());

        assertEquals(
                "Clean Code",
                resultado.get(0).titulo());

        assertEquals(
                "Effective Java",
                resultado.get(1).titulo());

        verify(livroRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremLivros() {

        when(livroRepository.findAll())
                .thenReturn(List.of());

        List<LivroResponseDTO> resultado = livroService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveAtualizarEstoque() {

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "111",
                2008,
                5,
                5);

        livro.setId(1L);

        AtualizarEstoqueDTO dto = new AtualizarEstoqueDTO(8, 10);

        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));

        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LivroResponseDTO resultado = livroService.atualizarEstoque(1L, dto);

        assertEquals(8, resultado.quantidadeDisponivel());
        assertEquals(10, resultado.quantidadeTotal());

        verify(livroRepository).save(livro);
    }

    @Test
    void naoDevePermitirQuantidadeDisponivelMaiorQueTotal() {

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "111",
                2008,
                5,
                5);

        livro.setId(1L);

        AtualizarEstoqueDTO dto = new AtualizarEstoqueDTO(11, 10);

        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));

        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.atualizarEstoque(1L, dto));

        verify(livroRepository, never())
                .save(any(Livro.class));
    }

    @Test
    void deveRealizarEmprestimoQuandoLivroEstiverDisponivel() {

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "111",
                2008,
                3,
                5);

        livro.setId(1L);

        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));

        livroService.realizarEmprestimo(1L);

        assertEquals(2, livro.getQuantidadeDisponivel());

        verify(livroRepository).save(livro);
    }

    @Test
    void naoDeveRealizarEmprestimoQuandoLivroEstiverIndisponivel() {

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                "111",
                2008,
                0,
                5);

        livro.setId(1L);

        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));

        assertThrows(
                LivroIndisponivelException.class,
                () -> livroService.realizarEmprestimo(1L));

        assertEquals(0, livro.getQuantidadeDisponivel());

        verify(livroRepository, never())
                .save(any(Livro.class));
    }

}
