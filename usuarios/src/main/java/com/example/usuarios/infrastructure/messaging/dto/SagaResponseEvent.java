package com.example.usuarios.infrastructure.messaging.dto;

public record SagaResponseEvent(Long sagaId, String origem, boolean sucesso, String motivoFalha) {}
