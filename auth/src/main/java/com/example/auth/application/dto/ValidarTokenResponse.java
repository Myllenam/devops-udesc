package com.example.auth.application.dto;

public record ValidarTokenResponse(
        boolean valido,
        String usuarioId,
        String role,
        String mensagem
) {
}
