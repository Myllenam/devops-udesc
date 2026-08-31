package com.example.livros.service;

import com.example.livros.dto.*;
import com.example.livros.exception.*;
import com.example.livros.model.Livro;
import com.example.livros.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    // RF01 + RF05
    @Transactional
    public LivroResponseDTO cadastrar(LivroRequestDTO dto) {
        if (livroRepository.existsByIsbn(dto.isbn())) {
            throw new IsbnJaCadastradoException(dto.isbn());
        }

        Livro livro = new Livro(
                dto.titulo(),
                dto.autor(),
                dto.isbn(),
                dto.anoPublicacao(),
                dto.quantidadeDisponivel(),
                dto.quantidadeTotal()
        );

        Livro salvo = livroRepository.save(livro);
        return toResponseDTO(salvo);
    }

    // RF02
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarTodos() {
        return livroRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public LivroResponseDTO buscarPorId(Long id) {
        Livro livro = buscarEntidadePorId(id);
        return toResponseDTO(livro);
    }

    // RF03
    @Transactional
    public LivroResponseDTO atualizarEstoque(Long id, AtualizarEstoqueDTO dto) {
        Livro livro = buscarEntidadePorId(id);

        if (dto.quantidadeDisponivel() > dto.quantidadeTotal()) {
            throw new IllegalArgumentException(
                    "Quantidade disponível não pode ser maior que a quantidade total");
        }

        livro.setQuantidadeDisponivel(dto.quantidadeDisponivel());
        livro.setQuantidadeTotal(dto.quantidadeTotal());

        return toResponseDTO(livroRepository.save(livro));
    }

    // RF04
    @Transactional
    public void realizarEmprestimo(Long id) {
        Livro livro = buscarEntidadePorId(id);

        if (!livro.estaDisponivel()) {
            throw new LivroIndisponivelException(id);
        }

        livro.decrementarDisponivel();
        livroRepository.save(livro);
    }

    private Livro buscarEntidadePorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

    private LivroResponseDTO toResponseDTO(Livro livro) {
        return new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getQuantidadeDisponivel(),
                livro.getQuantidadeTotal()
        );
    }
}