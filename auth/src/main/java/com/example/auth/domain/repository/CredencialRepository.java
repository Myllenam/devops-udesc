package com.example.auth.domain.repository;

import com.example.auth.domain.entity.Credencial;

import java.util.Optional;

public interface CredencialRepository {

    Credencial salvar(Credencial credencial);

    Optional<Credencial> buscarPorEmail(String email);

    boolean existsByEmail(String email);
}
