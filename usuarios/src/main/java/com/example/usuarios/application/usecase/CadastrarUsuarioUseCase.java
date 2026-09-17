package com.example.usuarios.application.usecase;

import com.example.usuarios.application.dto.CadastrarUsuarioRequest;
import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.exception.CpfJaCadastradoException;
import com.example.usuarios.domain.exception.EmailJaCadastradoException;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CadastrarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public CadastrarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse executar(CadastrarUsuarioRequest request) {
        if (usuarioRepository.existsByCpf(request.cpf())) {
            throw new CpfJaCadastradoException(request.cpf());
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = Usuario.novo(request.nome(), request.email(), request.cpf(), request.telefone());
        Usuario salvo = usuarioRepository.salvar(usuario);
        return UsuarioResponse.fromDomain(salvo);
    }
}
