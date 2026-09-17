package com.example.auth.infrastructure.client.dto;

public record UsuarioClientResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String status
) {
}
