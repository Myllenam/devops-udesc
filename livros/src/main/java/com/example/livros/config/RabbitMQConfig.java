package com.example.livros.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String SAGA_EXCHANGE = "saga.emprestimo.exchange";
    public static final String RESERVAR_LIVRO_QUEUE = "livros.reservar.queue";
    public static final String COMPENSAR_LIVRO_QUEUE = "livros.compensar.queue";
    public static final String DEVOLVER_LIVRO_QUEUE = "livros.devolver.queue";

    public static final String ROUTING_RESERVAR_LIVRO = "comando.livros.reservar";
    public static final String ROUTING_COMPENSAR_LIVRO = "comando.livros.compensar";
    public static final String ROUTING_RESPOSTA_LIVROS = "resposta.livros";
    public static final String ROUTING_DEVOLVER_LIVRO = "comando.livros.devolver";

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
    public Binding bindReservar() {
        return BindingBuilder.bind(reservarLivroQueue()).to(sagaExchange()).with(ROUTING_RESERVAR_LIVRO);
    }

    @Bean
    public Binding bindCompensar() {
        return BindingBuilder.bind(compensarLivroQueue()).to(sagaExchange()).with(ROUTING_COMPENSAR_LIVRO);
    }

    @Bean
    public Queue devolverLivroQueue() {
        return QueueBuilder.durable(DEVOLVER_LIVRO_QUEUE).build();
    }

    @Bean
    public Binding bindDevolverLivro() {
        return BindingBuilder.bind(devolverLivroQueue())
                .to(sagaExchange()).with(ROUTING_DEVOLVER_LIVRO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}