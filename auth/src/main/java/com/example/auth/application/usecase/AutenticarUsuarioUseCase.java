package com.example.auth.application.usecase;

import com.example.auth.application.dto.LoginRequest;
import com.example.auth.application.dto.TokenResponse;
import com.example.auth.domain.entity.Credencial;
import com.example.auth.domain.exception.CredenciaisInvalidasException;
import com.example.auth.domain.exception.UsuarioInativoOuBloqueadoException;
import com.example.auth.domain.repository.CredencialRepository;
import com.example.auth.infrastructure.client.UsuarioServiceClient;
import com.example.auth.infrastructure.client.dto.SituacaoClientResponse;
import com.example.auth.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuarioUseCase {

    private final CredencialRepository credencialRepository;
    private final UsuarioServiceClient usuarioServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticarUsuarioUseCase(CredencialRepository credencialRepository,
                                     UsuarioServiceClient usuarioServiceClient,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService) {
        this.credencialRepository = credencialRepository;
        this.usuarioServiceClient = usuarioServiceClient;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse executar(LoginRequest request) {
        Credencial credencial = credencialRepository.buscarPorEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), credencial.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        if (!credencial.isAtivo()) {
            throw new UsuarioInativoOuBloqueadoException();
        }

        SituacaoClientResponse situacao = usuarioServiceClient.consultarSituacao(credencial.getUsuarioId());
        if (!situacao.podeRealizarEmprestimo()) {
            throw new UsuarioInativoOuBloqueadoException();
        }

        String token = jwtService.gerarToken(credencial.getUsuarioId(), credencial.getRole());
        return new TokenResponse(token);
    }
}
