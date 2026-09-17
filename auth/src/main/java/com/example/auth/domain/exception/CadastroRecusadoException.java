package com.example.auth.domain.exception;

public class CadastroRecusadoException extends RuntimeException {

    public CadastroRecusadoException(String mensagem) {
        super(mensagem);
    }
}
