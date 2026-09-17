package com.example.usuarios.application.dto;

import com.example.usuarios.domain.entity.StatusUsuario;
import com.example.usuarios.domain.entity.Usuario;

public record SituacaoResponse(
        Long usuarioId,
        StatusUsuario status,
        boolean podeRealizarEmprestimo
) {

    public static SituacaoResponse fromDomain(Usuario usuario) {
        return new SituacaoResponse(usuario.getId(), usuario.getStatus(), usuario.podeRealizarEmprestimo());
    }
}
