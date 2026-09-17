package com.example.usuarios.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioSpringDataRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
