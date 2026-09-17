package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.SituacaoResponse;
import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.entity.Usuario;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarSituacaoUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConsultarSituacaoUseCase consultarSituacaoUseCase;

    @Test
    void deveRetornarSituacaoDeUsuarioAtivo() {
        Usuario usuario = new Usuario(1L, "Maria", "maria@email.com", "12345678900",
                "48999998888", StatusUsuario.ATIVO, LocalDate.now());
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        SituacaoResponse response = consultarSituacaoUseCase.executar(1L);

        assertThat(response.usuarioId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(StatusUsuario.ATIVO);
        assertThat(response.podeRealizarEmprestimo()).isTrue();
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultarSituacaoUseCase.executar(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }
}
