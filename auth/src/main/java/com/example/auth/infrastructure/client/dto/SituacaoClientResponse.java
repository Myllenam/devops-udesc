package com.example.auth.infrastructure.client.dto;

public record SituacaoClientResponse(
        Long usuarioId,
        String status,
        boolean podeRealizarEmprestimo
) {
}
