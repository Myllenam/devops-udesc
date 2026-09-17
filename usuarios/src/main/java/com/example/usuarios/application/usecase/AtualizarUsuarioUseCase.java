package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.AtualizarUsuarioRequest;
import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.EmailJaCadastradoException;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AtualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public AtualizarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse executar(Long id, AtualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailJaCadastradoException(request.email());
        }

        usuario.atualizarDados(request.nome(), request.email(), request.telefone());
        Usuario salvo = usuarioRepository.salvar(usuario);
        return UsuarioResponse.fromDomain(salvo);
    }
}
