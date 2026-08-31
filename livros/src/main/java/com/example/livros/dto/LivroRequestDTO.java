package com.example.livros.dto;

import jakarta.validation.constraints.*;

public record LivroRequestDTO(
        @NotBlank(message = "Título é obrigatório") String titulo,
        @NotBlank(message = "Autor é obrigatório") String autor,
        @NotBlank(message = "ISBN é obrigatório") String isbn,
        @NotNull @Min(1000) Integer anoPublicacao,
        @NotNull @Min(0) Integer quantidadeDisponivel,
        @NotNull @Min(1) Integer quantidadeTotal
) {}