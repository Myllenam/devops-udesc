package com.example.emprestimos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SAGA_EXCHANGE = "saga.emprestimo.exchange";

    public static final String RESERVAR_LIVRO_QUEUE = "livros.reservar.queue";
    public static final String COMPENSAR_LIVRO_QUEUE = "livros.compensar.queue";
    public static final String VALIDAR_USUARIO_QUEUE = "usuarios.validar.queue";
    public static final String SAGA_REPLY_QUEUE = "saga.emprestimo.reply.queue";

    public static final String ROUTING_RESERVAR_LIVRO = "comando.livros.reservar";
    public static final String ROUTING_COMPENSAR_LIVRO = "comando.livros.compensar";
    public static final String ROUTING_VALIDAR_USUARIO = "comando.usuarios.validar";
    public static final String ROUTING_RESPOSTA = "resposta.#";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue reservarLivroQueue() {
        return QueueBuilder.durable(RESERVAR_LIVRO_QUEUE).build();
    }

    @Bean
    public Queue compensarLivroQueue() {
        return QueueBuilder.durable(COMPENSAR_LIVRO_QUEUE).build();
    }

    @Bean
    public Queue validarUsuarioQueue() {
        return QueueBuilder.durable(VALIDAR_USUARIO_QUEUE).build();
    }

    @Bean
    public Queue sagaReplyQueue() {
        return QueueBuilder.durable(SAGA_REPLY_QUEUE).build();
    }

    @Bean
    public Binding bindReservarLivro() {
        return BindingBuilder.bind(reservarLivroQueue())
                .to(sagaExchange()).with(ROUTING_RESERVAR_LIVRO);
    }

    @Bean
    public Binding bindCompensarLivro() {
        return BindingBuilder.bind(compensarLivroQueue())
                .to(sagaExchange()).with(ROUTING_COMPENSAR_LIVRO);
    }

    @Bean
    public Binding bindValidarUsuario() {
        return BindingBuilder.bind(validarUsuarioQueue())
                .to(sagaExchange()).with(ROUTING_VALIDAR_USUARIO);
    }

    @Bean
    public Binding bindSagaReply() {
        return BindingBuilder.bind(sagaReplyQueue())
                .to(sagaExchange()).with(ROUTING_RESPOSTA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}