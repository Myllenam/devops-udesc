package com.example.usuarios.application.dto;

import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.entity.Usuario;

import java.time.LocalDate;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        StatusUsuario status,
        LocalDate dataCadastro
) {

    public static UsuarioResponse fromDomain(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getStatus(),
                usuario.getDataCadastro()
        );
    }
}
