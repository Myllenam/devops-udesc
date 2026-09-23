package com.example.emprestimos;

import org.junit.jupiter.api.Test;

import com.example.emprestimos.model.Emprestimo;
import com.example.emprestimos.model.StatusEmprestimo;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EmprestimoTest {

    @Test
    void deveCriarEmprestimoComStatusAtivo() {
        Emprestimo emprestimo = new Emprestimo(1L, 10L, LocalDateTime.now().plusDays(14));

        assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.ATIVO);
        assertThat(emprestimo.getUsuarioId()).isEqualTo(1L);
        assertThat(emprestimo.getLivroId()).isEqualTo(10L);
        assertThat(emprestimo.getDataEmprestimo()).isNotNull();
    }

    @Test
    void deveMarcarComoDevolvido() {
        Emprestimo emprestimo = new Emprestimo(1L, 10L, LocalDateTime.now().plusDays(14));

        emprestimo.marcarComoDevolvido();

        assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.DEVOLVIDO);
        assertThat(emprestimo.getDataDevolucao()).isNotNull();
    }

    @Test
    void deveMarcarComoCancelado() {
        Emprestimo emprestimo = new Emprestimo(1L, 10L, LocalDateTime.now().plusDays(14));

        emprestimo.marcarComoCancelado();

        assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.CANCELADO);
    }
}