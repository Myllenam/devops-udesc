package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse executar(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        return UsuarioResponse.fromDomain(usuario);
    }
}
