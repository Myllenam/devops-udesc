package com.example.emprestimos.repository;

import com.example.emprestimos.model.SagaEmprestimo;
import com.example.emprestimos.saga.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaEmprestimoRepository extends JpaRepository<SagaEmprestimo, Long> {

    List<SagaEmprestimo> findByUsuarioId(Long usuarioId);

    List<SagaEmprestimo> findByStatus(SagaStatus status);
}