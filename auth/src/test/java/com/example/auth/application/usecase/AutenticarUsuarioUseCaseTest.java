package com.example.auth.application.usecase;

import com.example.auth.application.dto.LoginRequest;
import com.example.auth.application.dto.TokenResponse;
import com.example.auth.domain.entity.Credencial;
import com.example.auth.domain.exception.CredenciaisInvalidasException;
import com.example.auth.domain.exception.UsuarioInativoOuBloqueadoException;
import com.example.auth.domain.repository.CredencialRepository;
import com.example.auth.infrastructure.client.UsuarioServiceClient;
import com.example.auth.infrastructure.client.dto.SituacaoClientResponse;
import com.example.auth.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioUseCaseTest {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private UsuarioServiceClient usuarioServiceClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Test
    void deveAutenticarComSucesso() {
        LoginRequest request = new LoginRequest("maria@email.com", "senha123");
        Credencial credencial = new Credencial("id-1", "1", "maria@email.com", "hash", "USER", true);
        when(credencialRepository.buscarPorEmail("maria@email.com")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(usuarioServiceClient.consultarSituacao("1")).thenReturn(
                new SituacaoClientResponse(1L, "ATIVO", true));
        when(jwtService.gerarToken("1", "USER")).thenReturn("token-jwt");

        TokenResponse response = autenticarUsuarioUseCase.executar(request);

        assertThat(response.accessToken()).isEqualTo("token-jwt");
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoEncontrado() {
        LoginRequest request = new LoginRequest("inexistente@email.com", "senha123");
        when(credencialRepository.buscarPorEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        LoginRequest request = new LoginRequest("maria@email.com", "senhaErrada");
        Credencial credencial = new Credencial("id-1", "1", "maria@email.com", "hash", "USER", true);
        when(credencialRepository.buscarPorEmail("maria@email.com")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("senhaErrada", "hash")).thenReturn(false);

        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarExcecaoQuandoCredencialInativa() {
        LoginRequest request = new LoginRequest("maria@email.com", "senha123");
        Credencial credencial = new Credencial("id-1", "1", "maria@email.com", "hash", "USER", false);
        when(credencialRepository.buscarPorEmail("maria@email.com")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);

        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(UsuarioInativoOuBloqueadoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioBloqueadoNoUsuariosService() {
        LoginRequest request = new LoginRequest("maria@email.com", "senha123");
        Credencial credencial = new Credencial("id-1", "1", "maria@email.com", "hash", "USER", true);
        when(credencialRepository.buscarPorEmail("maria@email.com")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(usuarioServiceClient.consultarSituacao("1")).thenReturn(
                new SituacaoClientResponse(1L, "BLOQUEADO", false));

        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(UsuarioInativoOuBloqueadoException.class);
    }
}
