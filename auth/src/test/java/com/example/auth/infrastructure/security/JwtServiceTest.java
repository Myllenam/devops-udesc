package com.example.auth.infrastructure.security;

import com.example.auth.domain.exception.TokenInvalidoException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "chave-de-teste-com-no-minimo-256-bits-para-assinar-o-token-jwt";

    private final JwtService jwtService = new JwtService(SECRET, 3600000);

    @Test
    void deveGerarEValidarTokenComSucesso() {
        String token = jwtService.gerarToken("1", "USER");

        TokenClaims claims = jwtService.validarToken(token);

        assertThat(claims.usuarioId()).isEqualTo("1");
        assertThat(claims.role()).isEqualTo("USER");
    }

    @Test
    void deveLancarExcecaoQuandoTokenExpirado() {
        JwtService jwtServiceExpirado = new JwtService(SECRET, -1000);
        String token = jwtServiceExpirado.gerarToken("1", "USER");

        assertThatThrownBy(() -> jwtServiceExpirado.validarToken(token))
                .isInstanceOf(TokenInvalidoException.class)
                .hasMessage("Token expirado");
    }

    @Test
    void deveLancarExcecaoQuandoAssinaturaInvalida() {
        SecretKey outraChave = Keys.hmacShaKeyFor(
                "outra-chave-completamente-diferente-com-no-minimo-256-bits".getBytes());
        String tokenComOutraChave = Jwts.builder()
                .subject("1")
                .claim("role", "USER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(outraChave)
                .compact();

        assertThatThrownBy(() -> jwtService.validarToken(tokenComOutraChave))
                .isInstanceOf(TokenInvalidoException.class)
                .hasMessage("Token inválido");
    }

    @Test
    void deveLancarExcecaoQuandoTokenMalFormado() {
        assertThatThrownBy(() -> jwtService.validarToken("token-invalido"))
                .isInstanceOf(TokenInvalidoException.class)
                .hasMessage("Token inválido");
    }
}
