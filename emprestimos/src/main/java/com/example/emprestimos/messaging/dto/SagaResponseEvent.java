package com.example.emprestimos.messaging.dto;

import java.io.Serializable;

// Evento genérico de resposta — os serviços downstream publicam isso de volta
public record SagaResponseEvent(
        Long sagaId,
        String origem,      // "LIVROS" ou "USUARIOS"
        boolean sucesso,
        String motivoFalha
) implements Serializable {}