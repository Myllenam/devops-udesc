package com.example.auth.application.usecase;

import com.example.auth.application.dto.RegistrarRequest;
import com.example.auth.application.dto.RegistrarResponse;
import com.example.auth.domain.entity.Credencial;
import com.example.auth.domain.exception.EmailJaCadastradoException;
import com.example.auth.domain.repository.CredencialRepository;
import com.example.auth.infrastructure.client.UsuarioServiceClient;
import com.example.auth.infrastructure.client.dto.UsuarioClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseTest {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private UsuarioServiceClient usuarioServiceClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Test
    void deveRegistrarUsuarioComSucesso() {
        registrarUsuarioUseCase = new RegistrarUsuarioUseCase(credencialRepository, usuarioServiceClient, passwordEncoder);
        RegistrarRequest request = new RegistrarRequest("Maria", "12345678900", "maria@email.com",
                "48999998888", "senha123");
        when(credencialRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(usuarioServiceClient.criarUsuario(any())).thenReturn(
                new UsuarioClientResponse(1L, "Maria", "maria@email.com", "12345678900", "48999998888", "ATIVO"));
        when(passwordEncoder.encode("senha123")).thenReturn("hash-da-senha");

        RegistrarResponse response = registrarUsuarioUseCase.executar(request);

        assertThat(response.usuarioId()).isEqualTo("1");
        assertThat(response.email()).isEqualTo("maria@email.com");

        ArgumentCaptor<Credencial> captor = ArgumentCaptor.forClass(Credencial.class);
        verify(credencialRepository).salvar(captor.capture());
        assertThat(captor.getValue().getSenhaHash()).isEqualTo("hash-da-senha");
        assertThat(captor.getValue().getUsuarioId()).isEqualTo("1");
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        registrarUsuarioUseCase = new RegistrarUsuarioUseCase(credencialRepository, usuarioServiceClient, passwordEncoder);
        RegistrarRequest request = new RegistrarRequest("Maria", "12345678900", "maria@email.com",
                "48999998888", "senha123");
        when(credencialRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> registrarUsuarioUseCase.executar(request))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioServiceClient, never()).criarUsuario(any());
    }
}
