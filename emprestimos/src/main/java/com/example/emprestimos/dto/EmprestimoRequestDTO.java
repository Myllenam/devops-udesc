package com.example.emprestimos.dto;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequestDTO(
        @NotNull(message = "O id do livro é obrigatório") Long livroId,
        @NotNull(message = "O id do usuário é obrigatório") Long usuarioId
) {}