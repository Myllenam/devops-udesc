package com.example.livros.client;

public record ValidarTokenResponse(
        boolean valido,
        String usuarioId,
        String role,
        String mensagem
) {}
