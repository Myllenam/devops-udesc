package com.example.livros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.livros.model.Livro;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    boolean existsByIsbn(String isbn);
    Optional<Livro> findByIsbn(String isbn);
}