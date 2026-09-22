package com.example.auth.application.usecase;

import com.example.auth.application.dto.ValidarTokenResponse;
import com.example.auth.domain.exception.TokenInvalidoException;
import com.example.auth.infrastructure.security.JwtService;
import com.example.auth.infrastructure.security.TokenClaims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarTokenUseCaseTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ValidarTokenUseCase validarTokenUseCase;

    @Test
    void deveRetornarValidoQuandoTokenOk() {
        when(jwtService.validarToken("token-valido")).thenReturn(new TokenClaims("1", "USER"));

        ValidarTokenResponse response = validarTokenUseCase.executar("token-valido");

        assertThat(response.valido()).isTrue();
        assertThat(response.usuarioId()).isEqualTo("1");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void deveRetornarInvalidoQuandoTokenExpirado() {
        when(jwtService.validarToken("token-expirado")).thenThrow(new TokenInvalidoException("Token expirado"));

        ValidarTokenResponse response = validarTokenUseCase.executar("token-expirado");

        assertThat(response.valido()).isFalse();
        assertThat(response.mensagem()).isEqualTo("Token expirado");
    }
}
