package com.example.auth.infrastructure.client.dto;

public record CriarUsuarioClientRequest(
        String nome,
        String email,
        String cpf,
        String telefone
) {
}
