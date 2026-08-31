package com.example.livros.exception;


public class LivroIndisponivelException extends RuntimeException {
    public LivroIndisponivelException(Long id) {
        super("Livro com id " + id + " está indisponível para empréstimo");
    }
}