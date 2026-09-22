package com.example.emprestimos.repository;

import com.example.emprestimos.model.Emprestimo;
import com.example.emprestimos.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByUsuarioId(Long usuarioId);
    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
    long countByUsuarioId(Long usuarioId);
    long countByStatus(StatusEmprestimo status);
}
