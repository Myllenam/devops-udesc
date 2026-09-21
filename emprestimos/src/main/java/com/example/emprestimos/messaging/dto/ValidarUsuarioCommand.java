package com.example.emprestimos.messaging.dto;

import java.io.Serializable;

public record ValidarUsuarioCommand(Long sagaId, Long usuarioId) implements Serializable {}