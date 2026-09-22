package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.CadastrarUsuarioRequest;
import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.CpfJaCadastradoException;
import com.example.usuarios.domain.exception.EmailJaCadastradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CadastrarUsuarioUseCase cadastrarUsuarioUseCase;

    @Test
    void deveCadastrarUsuarioComSucesso() {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "Maria", "maria@email.com", "12345678900", "48999998888");
        when(usuarioRepository.existsByCpf("12345678900")).thenReturn(false);
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            return new Usuario(1L, usuario.getNome(), usuario.getEmail(), usuario.getCpf(),
                    usuario.getTelefone(), usuario.getStatus(), usuario.getDataCadastro());
        });

        UsuarioResponse response = cadastrarUsuarioUseCase.executar(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("maria@email.com");
        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "Maria", "maria@email.com", "12345678900", "48999998888");
        when(usuarioRepository.existsByCpf("12345678900")).thenReturn(true);

        assertThatThrownBy(() -> cadastrarUsuarioUseCase.executar(request))
                .isInstanceOf(CpfJaCadastradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "Maria", "maria@email.com", "12345678900", "48999998888");
        when(usuarioRepository.existsByCpf("12345678900")).thenReturn(false);
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> cadastrarUsuarioUseCase.executar(request))
                .isInstanceOf(EmailJaCadastradoException.class);
    }
}
