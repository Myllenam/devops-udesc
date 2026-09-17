package com.example.auth.domain.exception;

public class UsuarioInativoOuBloqueadoException extends RuntimeException {

    public UsuarioInativoOuBloqueadoException() {
        super("Usuário inativo ou bloqueado");
    }
}
