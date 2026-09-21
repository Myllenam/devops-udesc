package com.example.emprestimos.dto;

import com.example.emprestimos.saga.SagaStatus;

public record EmprestimoResponseDTO(
        Long sagaId,
        Long livroId,
        Long usuarioId,
        SagaStatus status,
        String motivoFalha
) {}