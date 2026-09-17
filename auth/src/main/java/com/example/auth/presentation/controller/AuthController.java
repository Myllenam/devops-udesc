package com.example.auth.presentation.controller;

import com.example.auth.application.dto.LoginRequest;
import com.example.auth.application.dto.RegistrarRequest;
import com.example.auth.application.dto.RegistrarResponse;
import com.example.auth.application.dto.TokenResponse;
import com.example.auth.application.dto.ValidarTokenRequest;
import com.example.auth.application.dto.ValidarTokenResponse;
import com.example.auth.application.usecase.AutenticarUsuarioUseCase;
import com.example.auth.application.usecase.RegistrarUsuarioUseCase;
import com.example.auth.application.usecase.ValidarTokenUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final ValidarTokenUseCase validarTokenUseCase;

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                           AutenticarUsuarioUseCase autenticarUsuarioUseCase,
                           ValidarTokenUseCase validarTokenUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.validarTokenUseCase = validarTokenUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrarResponse> registrar(@Valid @RequestBody RegistrarRequest request) {
        RegistrarResponse response = registrarUsuarioUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticarUsuarioUseCase.executar(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidarTokenResponse> validar(@Valid @RequestBody ValidarTokenRequest request) {
        ValidarTokenResponse response = validarTokenUseCase.executar(request.token());
        HttpStatus status = response.valido() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}
