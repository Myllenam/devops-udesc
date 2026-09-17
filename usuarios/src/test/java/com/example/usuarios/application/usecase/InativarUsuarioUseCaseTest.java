package com.example.usuarios.application.usecase;

import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InativarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private InativarUsuarioUseCase inativarUsuarioUseCase;

    @Test
    void deveInativarUsuario() {
        Usuario usuario = new Usuario(1L, "Maria", "maria@email.com", "12345678900",
                "48999998888", StatusUsuario.ATIVO, LocalDate.now());
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        inativarUsuarioUseCase.executar(1L);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).salvar(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusUsuario.INATIVO);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inativarUsuarioUseCase.executar(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }
}
