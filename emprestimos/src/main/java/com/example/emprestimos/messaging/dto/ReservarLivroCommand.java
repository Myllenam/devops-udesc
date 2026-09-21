package com.example.emprestimos.messaging.dto;

import java.io.Serializable;

public record ReservarLivroCommand(Long sagaId, Long livroId) implements Serializable {}