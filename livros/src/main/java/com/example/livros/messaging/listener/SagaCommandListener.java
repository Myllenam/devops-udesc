package com.example.livros.messaging.listener;

import com.example.livros.config.RabbitMQConfig;
import com.example.livros.exception.LivroIndisponivelException;
import com.example.livros.messaging.dto.*;
import com.example.livros.service.LivroService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandListener {

    private final LivroService livroService;
    private final RabbitTemplate rabbitTemplate;

    public SagaCommandListener(LivroService livroService, RabbitTemplate rabbitTemplate) {
        this.livroService = livroService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.RESERVAR_LIVRO_QUEUE)
    public void reservarLivro(ReservarLivroCommand command) {
        try {
            livroService.realizarEmprestimo(command.livroId());
            rabbitTemplate.convertAndSend(RabbitMQConfig.SAGA_EXCHANGE,
                    RabbitMQConfig.ROUTING_RESPOSTA_LIVROS,
                    new SagaResponseEvent(command.sagaId(), "LIVROS", true, null));
        } catch (LivroIndisponivelException e) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.SAGA_EXCHANGE,
                    RabbitMQConfig.ROUTING_RESPOSTA_LIVROS,
                    new SagaResponseEvent(command.sagaId(), "LIVROS", false, e.getMessage()));
        }
    }

    @RabbitListener(queues = RabbitMQConfig.COMPENSAR_LIVRO_QUEUE)
    public void compensarLivro(CompensarLivroCommand command) {
        livroService.devolverEmprestimo(command.livroId());
    }

    @RabbitListener(queues = RabbitMQConfig.DEVOLVER_LIVRO_QUEUE)
    public void devolverLivro(DevolverLivroCommand command) {
        livroService.devolverEmprestimo(command.livroId());
    }
}
