package com.example.livros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false)
    private String autor;

    @NotBlank
    @Column(nullable = false, unique = true) // RF05 - ISBN único
    private String isbn;

    @NotNull
    @Column(name = "ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    @NotNull
    @Min(0)
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @NotNull
    @Min(0)
    @Column(name = "quantidade_total", nullable = false)
    private Integer quantidadeTotal;

    public Livro() {}

    public Livro(String titulo, String autor, String isbn, Integer anoPublicacao,
                 Integer quantidadeDisponivel, Integer quantidadeTotal) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeTotal = quantidadeTotal;
    }

    // Regra de negócio (RF04)
    public boolean estaDisponivel() {
        return quantidadeDisponivel != null && quantidadeDisponivel > 0;
    }
    

    public void decrementarDisponivel() {
        if (!estaDisponivel()) {
            throw new IllegalStateException("Livro sem exemplares disponíveis");
        }
        this.quantidadeDisponivel--;
    }

    public void incrementarDisponivel() {
        if (this.quantidadeDisponivel >= this.quantidadeTotal) {
            throw new IllegalStateException("Quantidade disponível não pode exceder o total");
        }
        this.quantidadeDisponivel++;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }
    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }
    public Integer getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(Integer quantidadeTotal) { this.quantidadeTotal = quantidadeTotal; }
}