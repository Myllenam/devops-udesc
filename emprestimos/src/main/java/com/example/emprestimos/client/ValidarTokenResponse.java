package com.example.emprestimos.client;

public record ValidarTokenResponse(
        boolean valido,
        String usuarioId,
        String role,
        String mensagem
) {}
