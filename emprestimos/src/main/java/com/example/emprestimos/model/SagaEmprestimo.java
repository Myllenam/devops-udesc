package com.example.emprestimos.model;

import com.example.emprestimos.saga.SagaStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saga_emprestimo")
public class SagaEmprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long livroId;

    @Column(nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Column(nullable = false)
    private LocalDateTime criadaEm;

    private LocalDateTime atualizadaEm;

    private String motivoFalha;

    public SagaEmprestimo() {}

    public SagaEmprestimo(Long livroId, Long usuarioId) {
        this.livroId = livroId;
        this.usuarioId = usuarioId;
        this.status = SagaStatus.INICIADA;
        this.criadaEm = LocalDateTime.now();
    }

    public void avancarPara(SagaStatus novoStatus) {
        this.status = novoStatus;
        this.atualizadaEm = LocalDateTime.now();
    }

    public void registrarFalha(SagaStatus statusFalha, String motivo) {
        this.status = statusFalha;
        this.motivoFalha = motivo;
        this.atualizadaEm = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public Long getLivroId() { return livroId; }
    public Long getUsuarioId() { return usuarioId; }
    public SagaStatus getStatus() { return status; }
    public String getMotivoFalha() { return motivoFalha; }
    public void setId(Long id) { this.id = id; }
}