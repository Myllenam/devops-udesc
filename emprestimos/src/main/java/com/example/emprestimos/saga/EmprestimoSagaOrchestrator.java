package com.example.emprestimos.saga;

import com.example.emprestimos.messaging.publisher.SagaCommandPublisher;
import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.repository.SagaEmprestimoRepository;

import com.example.emprestimos.messaging.dto.CompensarLivroCommand;
import com.example.emprestimos.messaging.dto.ReservarLivroCommand;
import com.example.emprestimos.messaging.dto.SagaResponseEvent;
import com.example.emprestimos.messaging.dto.ValidarUsuarioCommand;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class EmprestimoSagaOrchestrator {

    private final SagaCommandPublisher publisher;
    private final SagaEmprestimoRepository sagaRepository;

    public EmprestimoSagaOrchestrator(SagaCommandPublisher publisher,
            SagaEmprestimoRepository sagaRepository) {
        this.publisher = publisher;
        this.sagaRepository = sagaRepository;
    }

    @Transactional
    public SagaEmprestimo iniciarSaga(Long livroId, Long usuarioId) {
        SagaEmprestimo saga = new SagaEmprestimo(livroId, usuarioId);
        saga = sagaRepository.save(saga);

        publisher.enviarReservarLivro(new ReservarLivroCommand(saga.getId(), livroId));

        return saga;
    }

    // Chamado quando chega a resposta do serviço de Livros
    @Transactional
    public void tratarRespostaLivro(SagaResponseEvent evento) {
        SagaEmprestimo saga = buscarSaga(evento.sagaId());

        if (evento.sucesso()) {
            saga.avancarPara(SagaStatus.LIVRO_RESERVADO);
            sagaRepository.save(saga);

            // Próximo passo: validar usuário
            publisher.enviarValidarUsuario(
                    new ValidarUsuarioCommand(saga.getId(), saga.getUsuarioId()));
        } else {
            saga.registrarFalha(SagaStatus.FALHA_LIVRO_INDISPONIVEL, evento.motivoFalha());
            sagaRepository.save(saga);
            // Não precisa compensar nada — o livro nunca foi reservado
        }
    }

    // Chamado quando chega a resposta do serviço de Usuários
    @Transactional
    public void tratarRespostaUsuario(SagaResponseEvent evento) {
        SagaEmprestimo saga = buscarSaga(evento.sagaId());

        if (evento.sucesso()) {
            saga.avancarPara(SagaStatus.CONCLUIDA);
            sagaRepository.save(saga);
            // Empréstimo confirmado - poderia publicar um evento final aqui
        } else {
            // Usuário inválido → precisa DESFAZER a reserva do livro (compensação)
            saga.avancarPara(SagaStatus.COMPENSANDO);
            sagaRepository.save(saga);

            publisher.enviarCompensarLivro(
                    new CompensarLivroCommand(saga.getId(), saga.getLivroId()));

            saga.registrarFalha(SagaStatus.FALHA_USUARIO_INVALIDO, evento.motivoFalha());
            sagaRepository.save(saga);
        }
    }

    private SagaEmprestimo buscarSaga(Long sagaId) {
        return sagaRepository.findById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Saga não encontrada: " + sagaId));
    }

    @Transactional(readOnly = true)

    public SagaEmprestimo consultarSaga(Long sagaId) {
        return buscarSaga(sagaId);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> montarResposta(HttpStatus notFound, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'montarResposta'");
    }
}
