package com.example.emprestimos.messaging.dto;

import java.io.Serializable;

public record CompensarLivroCommand(Long sagaId, Long livroId) implements Serializable {}