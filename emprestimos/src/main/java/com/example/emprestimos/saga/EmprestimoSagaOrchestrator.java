package com.example.emprestimos.saga;

import com.example.emprestimos.exception.LimiteEmprestimosExcedidoException;
import com.example.emprestimos.exception.UsuarioComPendenciaDeAtrasoException;
import com.example.emprestimos.messaging.dto.*;
import com.example.emprestimos.messaging.publisher.SagaCommandPublisher;
import com.example.emprestimos.model.Emprestimo;
import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.model.StatusEmprestimo;
import com.example.emprestimos.repository.EmprestimoRepository;
import com.example.emprestimos.repository.SagaEmprestimoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EmprestimoSagaOrchestrator {

    private static final int LIMITE_EMPRESTIMOS_ATIVOS = 3;
    private static final int DIAS_BLOQUEIO_POR_ATRASO = 6;

    private final SagaCommandPublisher publisher;
    private final SagaEmprestimoRepository sagaRepository;
    private final EmprestimoRepository emprestimoRepository;

    public EmprestimoSagaOrchestrator(SagaCommandPublisher publisher,
                                       SagaEmprestimoRepository sagaRepository,
                                       EmprestimoRepository emprestimoRepository) {
        this.publisher = publisher;
        this.sagaRepository = sagaRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional
    public SagaEmprestimo iniciarSaga(Long livroId, Long usuarioId) {
        validarRegrasDeNegocio(usuarioId); // NOVO - checagem síncrona, antes de tudo

        SagaEmprestimo saga = new SagaEmprestimo(livroId, usuarioId);
        saga = sagaRepository.save(saga);

        publisher.enviarReservarLivro(new ReservarLivroCommand(saga.getId(), livroId));

        return saga;
    }

    private void validarRegrasDeNegocio(Long usuarioId) {
        List<Emprestimo> ativos = emprestimoRepository.findByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATIVO);

        if (ativos.size() >= LIMITE_EMPRESTIMOS_ATIVOS) {
            throw new LimiteEmprestimosExcedidoException(usuarioId);
        }

        LocalDateTime agora = LocalDateTime.now();

        // Tem algum empréstimo ativo já vencido (atrasado agora)?
        boolean temAtrasoAtivo = ativos.stream()
                .anyMatch(e -> e.getDataDevolucaoPrevista() != null
                        && e.getDataDevolucaoPrevista().isBefore(agora));

        if (temAtrasoAtivo) {
            throw new UsuarioComPendenciaDeAtrasoException(usuarioId);
        }

        // Devolveu algum livro atrasado nos últimos 6 dias? (período de penalidade)
        boolean teveAtrasoRecente = emprestimoRepository.findByUsuarioId(usuarioId).stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.DEVOLVIDO)
                .anyMatch(e -> e.getDataDevolucao() != null
                        && e.getDataDevolucaoPrevista() != null
                        && e.getDataDevolucao().isAfter(e.getDataDevolucaoPrevista())
                        && e.getDataDevolucao().plusDays(DIAS_BLOQUEIO_POR_ATRASO).isAfter(agora));

        if (teveAtrasoRecente) {
            throw new UsuarioComPendenciaDeAtrasoException(usuarioId);
        }
    }

    @Transactional
    public void tratarRespostaLivro(SagaResponseEvent evento) {
        SagaEmprestimo saga = buscarSaga(evento.sagaId());

        if (evento.sucesso()) {
            saga.avancarPara(SagaStatus.LIVRO_RESERVADO);
            sagaRepository.save(saga);

            publisher.enviarValidarUsuario(
                    new ValidarUsuarioCommand(saga.getId(), saga.getUsuarioId()));
        } else {
            saga.registrarFalha(SagaStatus.FALHA_LIVRO_INDISPONIVEL, evento.motivoFalha());
            sagaRepository.save(saga);
        }
    }

    @Transactional
    public void tratarRespostaUsuario(SagaResponseEvent evento) {
        SagaEmprestimo saga = buscarSaga(evento.sagaId());

        if (evento.sucesso()) {
            saga.avancarPara(SagaStatus.CONCLUIDA);
            sagaRepository.save(saga);

            Emprestimo emprestimo = new Emprestimo(
                    saga.getUsuarioId(),
                    saga.getLivroId(),
                    LocalDateTime.now().plusDays(14)
            );
            emprestimoRepository.save(emprestimo);

        } else {
            saga.avancarPara(SagaStatus.COMPENSANDO);
            sagaRepository.save(saga);

            publisher.enviarCompensarLivro(
                    new CompensarLivroCommand(saga.getId(), saga.getLivroId()));

            saga.registrarFalha(SagaStatus.FALHA_USUARIO_INVALIDO, evento.motivoFalha());
            sagaRepository.save(saga);
        }
    }

    @Transactional(readOnly = true)
    public SagaEmprestimo consultarSaga(Long sagaId) {
        return buscarSaga(sagaId);
    }

    private SagaEmprestimo buscarSaga(Long sagaId) {
        return sagaRepository.findById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga não encontrada: " + sagaId));
    }
}
