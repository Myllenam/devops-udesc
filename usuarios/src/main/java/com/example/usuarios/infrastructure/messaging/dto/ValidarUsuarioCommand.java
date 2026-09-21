package com.example.usuarios.infrastructure.messaging.dto;

public record ValidarUsuarioCommand(Long sagaId, Long usuarioId) {}
