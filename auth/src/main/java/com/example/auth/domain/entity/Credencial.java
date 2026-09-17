package com.example.auth.domain.entity;

public class Credencial {

    private String id;
    private String usuarioId;
    private String email;
    private String senhaHash;
    private String role;
    private boolean ativo;

    public Credencial(String id, String usuarioId, String email, String senhaHash, String role, boolean ativo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
        this.ativo = ativo;
    }

    public static Credencial nova(String usuarioId, String email, String senhaHash) {
        return new Credencial(null, usuarioId, email, senhaHash, "USER", true);
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getRole() {
        return role;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
