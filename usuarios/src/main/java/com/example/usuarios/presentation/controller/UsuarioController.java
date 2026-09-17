package com.example.usuarios.presentation.controller;

import com.example.usuarios.application.dto.AtualizarUsuarioRequest;
import com.example.usuarios.application.dto.CadastrarUsuarioRequest;
import com.example.usuarios.application.dto.SituacaoResponse;
import com.example.usuarios.application.dto.UsuarioResponse;
import com.example.usuarios.application.usecase.AtualizarUsuarioUseCase;
import com.example.usuarios.application.usecase.BuscarUsuarioUseCase;
import com.example.usuarios.application.usecase.CadastrarUsuarioUseCase;
import com.example.usuarios.application.usecase.ConsultarSituacaoUseCase;
import com.example.usuarios.application.usecase.InativarUsuarioUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CadastrarUsuarioUseCase cadastrarUsuarioUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final InativarUsuarioUseCase inativarUsuarioUseCase;
    private final ConsultarSituacaoUseCase consultarSituacaoUseCase;

    public UsuarioController(CadastrarUsuarioUseCase cadastrarUsuarioUseCase,
                              BuscarUsuarioUseCase buscarUsuarioUseCase,
                              AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                              InativarUsuarioUseCase inativarUsuarioUseCase,
                              ConsultarSituacaoUseCase consultarSituacaoUseCase) {
        this.cadastrarUsuarioUseCase = cadastrarUsuarioUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.inativarUsuarioUseCase = inativarUsuarioUseCase;
        this.consultarSituacaoUseCase = consultarSituacaoUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastrarUsuarioRequest request) {
        UsuarioResponse response = cadastrarUsuarioUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(buscarUsuarioUseCase.executar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody AtualizarUsuarioRequest request) {
        return ResponseEntity.ok(atualizarUsuarioUseCase.executar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        inativarUsuarioUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/situacao")
    public ResponseEntity<SituacaoResponse> situacao(@PathVariable Long id) {
        return ResponseEntity.ok(consultarSituacaoUseCase.executar(id));
    }
}
