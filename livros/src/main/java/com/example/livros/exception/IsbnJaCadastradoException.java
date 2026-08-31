package com.example.livros.exception;


public class IsbnJaCadastradoException extends RuntimeException {
    public IsbnJaCadastradoException(String isbn) {
        super("Já existe um livro cadastrado com o ISBN: " + isbn);
    }
}