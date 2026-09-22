package com.example.emprestimos.controller;

import com.example.emprestimos.dto.EmprestimoRequestDTO;
import com.example.emprestimos.dto.EmprestimoResponseDTO;
import com.example.emprestimos.messaging.dto.DevolverLivroCommand;
import com.example.emprestimos.messaging.publisher.SagaCommandPublisher;
import com.example.emprestimos.model.Emprestimo;
import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.model.StatusEmprestimo;
import com.example.emprestimos.repository.EmprestimoRepository;
import com.example.emprestimos.saga.EmprestimoSagaOrchestrator;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoSagaOrchestrator orchestrator;
    private final EmprestimoRepository emprestimoRepository;
    private final SagaCommandPublisher publisher; // NOVO

    public EmprestimoController(EmprestimoSagaOrchestrator orchestrator,
            EmprestimoRepository emprestimoRepository,
            SagaCommandPublisher publisher) {
        this.orchestrator = orchestrator;
        this.emprestimoRepository = emprestimoRepository;
        this.publisher = publisher;
    }

    // Dispara a SAGA - retorna 202 Accepted, pois o processo é assíncrono
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> solicitarEmprestimo(
            @Valid @RequestBody EmprestimoRequestDTO dto) {

        SagaEmprestimo saga = orchestrator.iniciarSaga(dto.livroId(), dto.usuarioId());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(toResponseDTO(saga));
    }

    // Consulta o andamento/resultado da SAGA
    @GetMapping("/{sagaId}")
    public ResponseEntity<EmprestimoResponseDTO> consultarStatus(@PathVariable Long sagaId) {
        SagaEmprestimo saga = orchestrator.consultarSaga(sagaId);
        return ResponseEntity.ok(toResponseDTO(saga));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Emprestimo>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emprestimoRepository.findByUsuarioId(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/quantidade")
    public ResponseEntity<Long> quantidadeEmprestimosUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emprestimoRepository.countByUsuarioId(usuarioId));
    }

    @GetMapping("/quantidade")
    public ResponseEntity<Long> quantidadeTotalEmprestimos() {
        return ResponseEntity.ok(emprestimoRepository.count());
    }

    @GetMapping("/quantidade/status/{status}")
    public ResponseEntity<Long> quantidadePorStatus(@PathVariable StatusEmprestimo status) {
        return ResponseEntity.ok(emprestimoRepository.countByStatus(status));
    }

    // NOVO - o endpoint que estava faltando
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<Void> devolverLivro(@PathVariable Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Empréstimo não encontrado: " + id));

        emprestimo.marcarComoDevolvido();
        emprestimoRepository.save(emprestimo);

        publisher.enviarDevolverLivro(new DevolverLivroCommand(emprestimo.getLivroId()));

        return ResponseEntity.noContent().build();
    }

    private EmprestimoResponseDTO toResponseDTO(SagaEmprestimo saga) {
        return new EmprestimoResponseDTO(
                saga.getId(),
                saga.getLivroId(),
                saga.getUsuarioId(),
                saga.getStatus(),
                saga.getMotivoFalha());
    }
}