package com.example.emprestimos.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long livroId;

    @Column(nullable = false)
    private LocalDateTime dataEmprestimo;

    private LocalDateTime dataDevolucaoPrevista;

    private LocalDateTime dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo status;

    // Construtor vazio - obrigatório para o JPA
    public Emprestimo() {}

    // Construtor para criar um novo empréstimo
    public Emprestimo(Long usuarioId, Long livroId, LocalDateTime dataDevolucaoPrevista) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataEmprestimo = LocalDateTime.now();
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.status = StatusEmprestimo.ATIVO;
    }

    // Regra de negócio: marcar como devolvido
    public void marcarComoDevolvido() {
        this.dataDevolucao = LocalDateTime.now();
        this.status = StatusEmprestimo.DEVOLVIDO;
    }

    public void marcarComoCancelado() {
        this.status = StatusEmprestimo.CANCELADO;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }

    public LocalDateTime getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDateTime dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDateTime getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) { this.dataDevolucaoPrevista = dataDevolucaoPrevista; }

    public LocalDateTime getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDateTime dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }
}