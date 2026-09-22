package com.example.emprestimos.exception;

public class LimiteEmprestimosExcedidoException extends RuntimeException {
    public LimiteEmprestimosExcedidoException(Long usuarioId) {
        super("Usuário " + usuarioId + " já possui o limite máximo de 3 empréstimos ativos");
    }
}