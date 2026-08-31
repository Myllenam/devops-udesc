package com.example.livros.controller;

import com.example.livros.dto.*;
import com.example.livros.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping
    public ResponseEntity<LivroResponseDTO> cadastrar(@Valid @RequestBody LivroRequestDTO dto) {
        LivroResponseDTO response = livroService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> listarTodos() {
        return ResponseEntity.ok(livroService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<LivroResponseDTO> atualizarEstoque(
            @PathVariable Long id, @Valid @RequestBody AtualizarEstoqueDTO dto) {
        return ResponseEntity.ok(livroService.atualizarEstoque(id, dto));
    }

    @PostMapping("/{id}/emprestimo")
    public ResponseEntity<Void> realizarEmprestimo(@PathVariable Long id) {
        livroService.realizarEmprestimo(id);
        return ResponseEntity.noContent().build();
    }
}