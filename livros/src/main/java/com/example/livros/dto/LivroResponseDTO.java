package com.example.livros.dto;

public record LivroResponseDTO(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeDisponivel,
        Integer quantidadeTotal
) {}
