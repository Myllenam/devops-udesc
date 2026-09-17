package com.example.usuarios.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    void deveNascerAtivoAoCriarNovoUsuario() {
        Usuario usuario = Usuario.novo("Maria", "maria@email.com", "12345678900", "48999998888");

        assertThat(usuario.getStatus()).isEqualTo(StatusUsuario.ATIVO);
        assertThat(usuario.getDataCadastro()).isNotNull();
        assertThat(usuario.podeRealizarEmprestimo()).isTrue();
    }

    @Test
    void deveFicarInativoAoInativar() {
        Usuario usuario = Usuario.novo("Maria", "maria@email.com", "12345678900", "48999998888");

        usuario.inativar();

        assertThat(usuario.getStatus()).isEqualTo(StatusUsuario.INATIVO);
        assertThat(usuario.podeRealizarEmprestimo()).isFalse();
    }

    @Test
    void naoDevePermitirEmprestimoQuandoBloqueado() {
        Usuario usuario = new Usuario(1L, "Maria", "maria@email.com", "12345678900",
                "48999998888", StatusUsuario.BLOQUEADO, java.time.LocalDate.now());

        assertThat(usuario.podeRealizarEmprestimo()).isFalse();
    }

    @Test
    void deveAtualizarDadosMantendoDocumentosOriginais() {
        Usuario usuario = Usuario.novo("Maria", "maria@email.com", "12345678900", "48999998888");

        usuario.atualizarDados("Maria Silva", "maria.silva@email.com", "48988887777");

        assertThat(usuario.getNome()).isEqualTo("Maria Silva");
        assertThat(usuario.getEmail()).isEqualTo("maria.silva@email.com");
        assertThat(usuario.getTelefone()).isEqualTo("48988887777");
        assertThat(usuario.getCpf()).isEqualTo("12345678900");
    }
}
