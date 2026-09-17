package com.example.usuarios.infrastructure.persistence;

import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status;

    @Column(nullable = false)
    private LocalDate dataCadastro;

    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(Long id, String nome, String email, String cpf, String telefone,
                             StatusUsuario status, LocalDate dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.status = status;
        this.dataCadastro = dataCadastro;
    }

    public static UsuarioJpaEntity fromDomain(Usuario usuario) {
        return new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getStatus(),
                usuario.getDataCadastro()
        );
    }

    public Usuario toDomain() {
        return new Usuario(id, nome, email, cpf, telefone, status, dataCadastro);
    }

    public Long getId() {
        return id;
    }
}
