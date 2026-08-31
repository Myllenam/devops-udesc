package com.example.livros.exception;

public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(Long id) {
        super("Livro não encontrado com id: " + id);
    }
}