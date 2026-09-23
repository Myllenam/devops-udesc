package com.example.emprestimos;

import com.example.emprestimos.exception.LimiteEmprestimosExcedidoException;
import com.example.emprestimos.exception.UsuarioComPendenciaDeAtrasoException;
import com.example.emprestimos.messaging.dto.*;
import com.example.emprestimos.messaging.publisher.SagaCommandPublisher;
import com.example.emprestimos.model.Emprestimo;
import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.model.StatusEmprestimo;
import com.example.emprestimos.repository.EmprestimoRepository;
import com.example.emprestimos.repository.SagaEmprestimoRepository;
import com.example.emprestimos.saga.EmprestimoSagaOrchestrator;
import com.example.emprestimos.saga.SagaStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoSagaOrchestratorTest {

    @Mock
    private SagaCommandPublisher publisher;

    @Mock
    private SagaEmprestimoRepository sagaRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    private EmprestimoSagaOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new EmprestimoSagaOrchestrator(publisher, sagaRepository, emprestimoRepository);
    }

    // ---------- Regras de negócio: limite e atraso ----------
    @Nested
    @DisplayName("Regras de negócio antes de iniciar a saga")
    class RegrasDeNegocio {

        @Test
        @DisplayName("Deve iniciar saga normalmente quando usuário está dentro do limite e sem atraso")
        void deveIniciarSagaComSucesso() {
            when(emprestimoRepository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                    .thenReturn(List.of());
            when(emprestimoRepository.findByUsuarioId(1L)).thenReturn(List.of());

            SagaEmprestimo sagaSalva = new SagaEmprestimo(10L, 1L);
            when(sagaRepository.save(any(SagaEmprestimo.class))).thenReturn(sagaSalva);

            SagaEmprestimo resultado = orchestrator.iniciarSaga(10L, 1L);

            assertThat(resultado).isNotNull();
            verify(publisher).enviarReservarLivro(any(ReservarLivroCommand.class));
        }

        @Test
        @DisplayName("Deve rejeitar quando usuário já tem 3 empréstimos ativos")
        void deveRejeitarPorLimiteExcedido() {
            Emprestimo e1 = new Emprestimo(1L, 10L, LocalDateTime.now().plusDays(14));
            Emprestimo e2 = new Emprestimo(1L, 11L, LocalDateTime.now().plusDays(14));
            Emprestimo e3 = new Emprestimo(1L, 12L, LocalDateTime.now().plusDays(14));

            when(emprestimoRepository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                    .thenReturn(List.of(e1, e2, e3));

            assertThatThrownBy(() -> orchestrator.iniciarSaga(13L, 1L))
                    .isInstanceOf(LimiteEmprestimosExcedidoException.class);

            verify(sagaRepository, never()).save(any());
            verify(publisher, never()).enviarReservarLivro(any());
        }

        @Test
        @DisplayName("Deve rejeitar quando usuário tem empréstimo ativo já vencido (atraso ativo)")
        void deveRejeitarPorAtrasoAtivo() {
            Emprestimo atrasado = new Emprestimo(1L, 10L, LocalDateTime.now().minusDays(6));

            when(emprestimoRepository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                    .thenReturn(List.of(atrasado));

            assertThatThrownBy(() -> orchestrator.iniciarSaga(20L, 1L))
                    .isInstanceOf(UsuarioComPendenciaDeAtrasoException.class);
        }

        @Test
        @DisplayName("Deve rejeitar quando usuário devolveu livro atrasado há menos de 6 dias")
        void deveRejeitarPorAtrasoRecente() {
            Emprestimo devolvidoComAtraso = new Emprestimo(1L, 10L, LocalDateTime.now().minusDays(10));
            devolvidoComAtraso.setDataDevolucao(LocalDateTime.now().minusDays(2));
            devolvidoComAtraso.setStatus(StatusEmprestimo.DEVOLVIDO);

            when(emprestimoRepository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                    .thenReturn(List.of());
            when(emprestimoRepository.findByUsuarioId(1L))
                    .thenReturn(List.of(devolvidoComAtraso));

            assertThatThrownBy(() -> orchestrator.iniciarSaga(20L, 1L))
                    .isInstanceOf(UsuarioComPendenciaDeAtrasoException.class);
        }

        @Test
        @DisplayName("Deve permitir quando a devolução com atraso já passou dos 6 dias de penalidade")
        void devePermitirQuandoPenalidadeExpirou() {
            Emprestimo devolvidoComAtraso = new Emprestimo(1L, 10L, LocalDateTime.now().minusDays(20));
            devolvidoComAtraso.setDataDevolucao(LocalDateTime.now().minusDays(10));
            devolvidoComAtraso.setStatus(StatusEmprestimo.DEVOLVIDO);

            when(emprestimoRepository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                    .thenReturn(List.of());
            when(emprestimoRepository.findByUsuarioId(1L))
                    .thenReturn(List.of(devolvidoComAtraso));
            when(sagaRepository.save(any(SagaEmprestimo.class)))
                    .thenReturn(new SagaEmprestimo(20L, 1L));

            SagaEmprestimo resultado = orchestrator.iniciarSaga(20L, 1L);

            assertThat(resultado).isNotNull();
            verify(publisher).enviarReservarLivro(any());
        }

        @Test
        @DisplayName("Devolução pontual (sem atraso) não deve bloquear novos empréstimos")
        void naoDeveBloquearQuandoDevolucaoFoiPontual() {
            Emprestimo devolvidoNoPrazo = new Emprestimo(1L, 10L, LocalDateTime.now().minusDays(5));
            devolvidoNoPrazo.setDataDevolucao(LocalDateTime.now().minusDays(6)); // devolveu ANTES do prazo
            devolvidoNoPrazo.setStatus(StatusEmprestimo.DEVOLVIDO);

            when(emprestimoRepository.findByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                    .thenReturn(List.of());
            when(emprestimoRepository.findByUsuarioId(1L))
                    .thenReturn(List.of(devolvidoNoPrazo));
            when(sagaRepository.save(any(SagaEmprestimo.class)))
                    .thenReturn(new SagaEmprestimo(20L, 1L));

            SagaEmprestimo resultado = orchestrator.iniciarSaga(20L, 1L);

            assertThat(resultado).isNotNull();
        }
    }

    // ---------- Transições da saga ----------
    @Nested
    @DisplayName("Tratamento de respostas da saga")
    class TratamentoRespostas {

        @Test
        @DisplayName("Deve avançar para LIVRO_RESERVADO e validar usuário quando livro reservado com sucesso")
        void deveAvancarQuandoLivroReservado() {
            SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);
            saga.setId(1L);
            when(sagaRepository.findById(1L)).thenReturn(Optional.of(saga));

            orchestrator.tratarRespostaLivro(new SagaResponseEvent(1L, "LIVROS", true, null));

            assertThat(saga.getStatus()).isEqualTo(SagaStatus.LIVRO_RESERVADO);
            verify(publisher).enviarValidarUsuario(any(ValidarUsuarioCommand.class));
        }

        @Test
        @DisplayName("Deve registrar falha quando livro está indisponível")
        void deveRegistrarFalhaLivroIndisponivel() {
            SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);
            saga.setId(1L);
            when(sagaRepository.findById(1L)).thenReturn(Optional.of(saga));

            orchestrator.tratarRespostaLivro(new SagaResponseEvent(1L, "LIVROS", false, "Sem estoque"));

            assertThat(saga.getStatus()).isEqualTo(SagaStatus.FALHA_LIVRO_INDISPONIVEL);
            verify(publisher, never()).enviarValidarUsuario(any());
        }

        @Test
        @DisplayName("Deve concluir a saga e criar o Emprestimo quando usuário validado com sucesso")
        void deveConcluirSagaECriarEmprestimo() {
            SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);
            saga.setId(1L);
            when(sagaRepository.findById(1L)).thenReturn(Optional.of(saga));

            orchestrator.tratarRespostaUsuario(new SagaResponseEvent(1L, "USUARIOS", true, null));

            assertThat(saga.getStatus()).isEqualTo(SagaStatus.CONCLUIDA);

            ArgumentCaptor<Emprestimo> captor = ArgumentCaptor.forClass(Emprestimo.class);
            verify(emprestimoRepository).save(captor.capture());
            assertThat(captor.getValue().getUsuarioId()).isEqualTo(1L);
            assertThat(captor.getValue().getLivroId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Deve compensar e registrar falha quando usuário é inválido")
        void deveCompensarQuandoUsuarioInvalido() {
            SagaEmprestimo saga = new SagaEmprestimo(10L, 1L);
            saga.setId(1L);
            when(sagaRepository.findById(1L)).thenReturn(Optional.of(saga));

            orchestrator.tratarRespostaUsuario(new SagaResponseEvent(1L, "USUARIOS", false, "Usuário inativo"));

            assertThat(saga.getStatus()).isEqualTo(SagaStatus.FALHA_USUARIO_INVALIDO);
            verify(publisher).enviarCompensarLivro(any(CompensarLivroCommand.class));
            verify(emprestimoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao consultar saga inexistente")
        void deveLancarExcecaoSagaInexistente() {
            when(sagaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orchestrator.consultarSaga(99L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}