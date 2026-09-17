package com.example.usuarios.application.usecase;

import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class InativarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public InativarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void executar(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        usuario.inativar();
        usuarioRepository.salvar(usuario);
    }
}
