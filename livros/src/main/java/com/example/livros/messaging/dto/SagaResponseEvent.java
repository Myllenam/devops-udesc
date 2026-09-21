package com.example.livros.messaging.dto;

public record SagaResponseEvent(Long sagaId, String origem, boolean sucesso, String motivoFalha) {}
