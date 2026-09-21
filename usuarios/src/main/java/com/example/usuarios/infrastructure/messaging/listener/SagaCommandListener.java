package com.example.usuarios.infrastructure.messaging.listener;

import com.example.usuarios.application.dto.SituacaoResponse;
import com.example.usuarios.application.usecase.ConsultarSituacaoUseCase;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.infrastructure.messaging.config.RabbitMQConfig;
import com.example.usuarios.infrastructure.messaging.dto.SagaResponseEvent;
import com.example.usuarios.infrastructure.messaging.dto.ValidarUsuarioCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandListener {

    private final ConsultarSituacaoUseCase consultarSituacaoUseCase;
    private final RabbitTemplate rabbitTemplate;

    public SagaCommandListener(ConsultarSituacaoUseCase consultarSituacaoUseCase,
                                RabbitTemplate rabbitTemplate) {
        this.consultarSituacaoUseCase = consultarSituacaoUseCase;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.VALIDAR_USUARIO_QUEUE)
    public void validarUsuario(ValidarUsuarioCommand command) {
        try {
            SituacaoResponse situacao = consultarSituacaoUseCase.executar(command.usuarioId());

            boolean sucesso = situacao.podeRealizarEmprestimo();
            String motivoFalha = sucesso ? null :
                    "Usuário " + command.usuarioId() + " não pode realizar empréstimo (status: " + situacao.status() + ")";

            responder(command.sagaId(), sucesso, motivoFalha);

        } catch (UsuarioNaoEncontradoException e) {
            responder(command.sagaId(), false, e.getMessage());
        }
    }

    private void responder(Long sagaId, boolean sucesso, String motivoFalha) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.ROUTING_RESPOSTA_USUARIOS,
                new SagaResponseEvent(sagaId, "USUARIOS", sucesso, motivoFalha));
    }
}
