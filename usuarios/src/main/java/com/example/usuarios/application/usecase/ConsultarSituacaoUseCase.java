package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.SituacaoResponse;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.UsuarioNaoEncontradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsultarSituacaoUseCase {

    private final UsuarioRepository usuarioRepository;

    public ConsultarSituacaoUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public SituacaoResponse executar(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        return SituacaoResponse.fromDomain(usuario);
    }
}
