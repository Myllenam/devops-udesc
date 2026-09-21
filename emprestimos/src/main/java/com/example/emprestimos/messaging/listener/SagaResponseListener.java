package com.example.emprestimos.messaging.listener;

import com.example.emprestimos.config.RabbitMQConfig;
import com.example.emprestimos.messaging.dto.SagaResponseEvent;
import com.example.emprestimos.saga.EmprestimoSagaOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SagaResponseListener {

    private final EmprestimoSagaOrchestrator orchestrator;

    public SagaResponseListener(EmprestimoSagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @RabbitListener(queues = RabbitMQConfig.SAGA_REPLY_QUEUE)
    public void receberResposta(SagaResponseEvent evento) {
        switch (evento.origem()) {
            case "LIVROS" -> orchestrator.tratarRespostaLivro(evento);
            case "USUARIOS" -> orchestrator.tratarRespostaUsuario(evento);
            default -> throw new IllegalArgumentException(
                    "Origem desconhecida: " + evento.origem());
        }
    }
}
