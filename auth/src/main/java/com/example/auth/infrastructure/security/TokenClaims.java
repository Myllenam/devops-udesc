package com.example.auth.infrastructure.security;

public record TokenClaims(
        String usuarioId,
        String role
) {
}
