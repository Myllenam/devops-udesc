package com.example.auth.domain.exception;

public class UsuarioServiceIndisponivelException extends RuntimeException {

    public UsuarioServiceIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
