package com.example.emprestimos.saga;

public enum SagaStatus {
    INICIADA,
    LIVRO_RESERVADO,
    USUARIO_VALIDADO,
    CONCLUIDA,
    FALHA_LIVRO_INDISPONIVEL,
    FALHA_USUARIO_INVALIDO,
    COMPENSANDO,
    COMPENSADA
}