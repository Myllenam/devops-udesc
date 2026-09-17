package com.example.auth.application.usecase;

import com.example.auth.application.dto.ValidarTokenResponse;
import com.example.auth.domain.exception.TokenInvalidoException;
import com.example.auth.infrastructure.security.JwtService;
import com.example.auth.infrastructure.security.TokenClaims;
import org.springframework.stereotype.Service;

@Service
public class ValidarTokenUseCase {

    private final JwtService jwtService;

    public ValidarTokenUseCase(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public ValidarTokenResponse executar(String token) {
        try {
            TokenClaims claims = jwtService.validarToken(token);
            return new ValidarTokenResponse(true, claims.usuarioId(), claims.role(), null);
        } catch (TokenInvalidoException ex) {
            return new ValidarTokenResponse(false, null, null, ex.getMessage());
        }
    }
}
