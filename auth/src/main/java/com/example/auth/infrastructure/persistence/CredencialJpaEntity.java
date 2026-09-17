package com.example.auth.infrastructure.persistence;

import com.example.auth.domain.entity.Credencial;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "credenciais")
public class CredencialJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String usuarioId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean ativo;

    protected CredencialJpaEntity() {
    }

    public CredencialJpaEntity(String id, String usuarioId, String email, String senhaHash,
                                String role, boolean ativo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
        this.ativo = ativo;
    }

    public static CredencialJpaEntity fromDomain(Credencial credencial) {
        return new CredencialJpaEntity(
                credencial.getId(),
                credencial.getUsuarioId(),
                credencial.getEmail(),
                credencial.getSenhaHash(),
                credencial.getRole(),
                credencial.isAtivo()
        );
    }

    public Credencial toDomain() {
        return new Credencial(id, usuarioId, email, senhaHash, role, ativo);
    }
}
