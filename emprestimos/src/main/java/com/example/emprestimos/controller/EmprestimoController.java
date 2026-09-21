package com.example.emprestimos.controller;

import com.example.emprestimos.dto.EmprestimoRequestDTO;
import com.example.emprestimos.dto.EmprestimoResponseDTO;
import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.saga.EmprestimoSagaOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoSagaOrchestrator orchestrator;

    public EmprestimoController(EmprestimoSagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
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

    private EmprestimoResponseDTO toResponseDTO(SagaEmprestimo saga) {
        return new EmprestimoResponseDTO(
                saga.getId(),
                saga.getLivroId(),
                saga.getUsuarioId(),
                saga.getStatus(),
                saga.getMotivoFalha()
        );
    }
}