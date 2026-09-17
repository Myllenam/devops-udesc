package com.example.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialSpringDataRepository extends JpaRepository<CredencialJpaEntity, String> {

    Optional<CredencialJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
