package com.example.livros.dto;

import jakarta.validation.constraints.NotNull;

public record AtualizarEstoqueDTO(
        @NotNull Integer quantidadeDisponivel,
        @NotNull Integer quantidadeTotal
) {}
