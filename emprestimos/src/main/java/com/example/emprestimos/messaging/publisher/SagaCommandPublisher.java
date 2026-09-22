package com.example.emprestimos.messaging.publisher;

import com.example.emprestimos.config.RabbitMQConfig;
import com.example.emprestimos.messaging.dto.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public SagaCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarReservarLivro(ReservarLivroCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.ROUTING_RESERVAR_LIVRO,
                command);
    }

    public void enviarValidarUsuario(ValidarUsuarioCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.ROUTING_VALIDAR_USUARIO,
                command);
    }

    public void enviarCompensarLivro(CompensarLivroCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.ROUTING_COMPENSAR_LIVRO,
                command);
    }

    public void enviarDevolverLivro(DevolverLivroCommand command) {
    rabbitTemplate.convertAndSend(
            RabbitMQConfig.SAGA_EXCHANGE,
            RabbitMQConfig.ROUTING_DEVOLVER_LIVRO,
            command);
}
}