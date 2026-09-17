package com.example.auth.application.usecase;

import com.example.auth.application.dto.RegistrarRequest;
import com.example.auth.application.dto.RegistrarResponse;
import com.example.auth.domain.entity.Credencial;
import com.example.auth.domain.exception.EmailJaCadastradoException;
import com.example.auth.domain.repository.CredencialRepository;
import com.example.auth.infrastructure.client.UsuarioServiceClient;
import com.example.auth.infrastructure.client.dto.CriarUsuarioClientRequest;
import com.example.auth.infrastructure.client.dto.UsuarioClientResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrarUsuarioUseCase {

    private final CredencialRepository credencialRepository;
    private final UsuarioServiceClient usuarioServiceClient;
    private final PasswordEncoder passwordEncoder;

    public RegistrarUsuarioUseCase(CredencialRepository credencialRepository,
                                    UsuarioServiceClient usuarioServiceClient,
                                    PasswordEncoder passwordEncoder) {
        this.credencialRepository = credencialRepository;
        this.usuarioServiceClient = usuarioServiceClient;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrarResponse executar(RegistrarRequest request) {
        if (credencialRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        UsuarioClientResponse usuarioCriado = usuarioServiceClient.criarUsuario(
                new CriarUsuarioClientRequest(request.nome(), request.email(), request.cpf(), request.telefone()));

        String senhaHash = passwordEncoder.encode(request.senha());
        Credencial credencial = Credencial.nova(String.valueOf(usuarioCriado.id()), request.email(), senhaHash);
        credencialRepository.salvar(credencial);

        return new RegistrarResponse(String.valueOf(usuarioCriado.id()), request.email());
    }
}
