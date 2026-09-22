package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.AtualizarUsuarioRequest;
import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.EmailJaCadastradoException;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AtualizarUsuarioUseCase atualizarUsuarioUseCase;

    @Test
    void deveAtualizarUsuarioComSucesso() {
        Usuario usuario = new Usuario(1L, "Maria", "maria@email.com", "12345678900",
                "48999998888", StatusUsuario.ATIVO, LocalDate.now());
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "Maria Silva", "maria.silva@email.com", "48988887777");
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailAndIdNot("maria.silva@email.com", 1L)).thenReturn(false);
        when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse response = atualizarUsuarioUseCase.executar(1L, request);

        assertThat(response.nome()).isEqualTo("Maria Silva");
        assertThat(response.email()).isEqualTo("maria.silva@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "Maria Silva", "maria.silva@email.com", "48988887777");
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarUsuarioUseCase.executar(1L, request))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaUsadoPorOutroUsuario() {
        Usuario usuario = new Usuario(1L, "Maria", "maria@email.com", "12345678900",
                "48999998888", StatusUsuario.ATIVO, LocalDate.now());
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "Maria Silva", "outro@email.com", "48988887777");
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailAndIdNot("outro@email.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> atualizarUsuarioUseCase.executar(1L, request))
                .isInstanceOf(EmailJaCadastradoException.class);
    }
}
