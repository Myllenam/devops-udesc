package com.example.usuarios.infrastructure.client;

public record ValidarTokenResponse(
        boolean valido,
        String usuarioId,
        String role,
        String mensagem
) {
}
