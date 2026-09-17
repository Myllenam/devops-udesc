package com.example.usuarios.domain.entity;

import java.time.LocalDate;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private StatusUsuario status;
    private LocalDate dataCadastro;

    public Usuario(Long id, String nome, String email, String cpf, String telefone,
                   StatusUsuario status, LocalDate dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.status = status;
        this.dataCadastro = dataCadastro;
    }

    public static Usuario novo(String nome, String email, String cpf, String telefone) {
        return new Usuario(null, nome, email, cpf, telefone, StatusUsuario.ATIVO, LocalDate.now());
    }

    public void inativar() {
        this.status = StatusUsuario.INATIVO;
    }

    public void atualizarDados(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public boolean podeRealizarEmprestimo() {
        return this.status == StatusUsuario.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }
}
