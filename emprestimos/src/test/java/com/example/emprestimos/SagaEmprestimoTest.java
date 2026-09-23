package com.example.emprestimos;

import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.saga.SagaStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SagaEmprestimoTest {

    @Test
    void deveCriarSagaComStatusIniciada() {
        SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.INICIADA);
        assertThat(saga.getLivroId()).isEqualTo(10L);
        assertThat(saga.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    void deveAvancarStatus() {
        SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);

        saga.avancarPara(SagaStatus.LIVRO_RESERVADO);

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.LIVRO_RESERVADO);
    }

    @Test
    void deveRegistrarFalhaComMotivo() {
        SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);

        saga.registrarFalha(SagaStatus.FALHA_LIVRO_INDISPONIVEL, "Sem estoque");

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.FALHA_LIVRO_INDISPONIVEL);
        assertThat(saga.getMotivoFalha()).isEqualTo("Sem estoque");
    }
}
