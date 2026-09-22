package com.example.emprestimos.exception;

public class UsuarioComPendenciaDeAtrasoException extends RuntimeException {
    public UsuarioComPendenciaDeAtrasoException(Long usuarioId) {
        super("Usuário " + usuarioId + " está bloqueado para novos empréstimos por pendência de atraso");
    }
}