package com.example.usuarios.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SAGA_EXCHANGE = "saga.emprestimo.exchange";
    public static final String VALIDAR_USUARIO_QUEUE = "usuarios.validar.queue";
    public static final String ROUTING_VALIDAR_USUARIO = "comando.usuarios.validar";
    public static final String ROUTING_RESPOSTA_USUARIOS = "resposta.usuarios";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue validarUsuarioQueue() {
        return QueueBuilder.durable(VALIDAR_USUARIO_QUEUE).build();
    }

    @Bean
    public Binding bindValidarUsuario() {
        return BindingBuilder.bind(validarUsuarioQueue())
                .to(sagaExchange()).with(ROUTING_VALIDAR_USUARIO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
