package com.example.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidarTokenRequest(
        @NotBlank(message = "Token é obrigatório") String token
) {
}
