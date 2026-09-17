package com.example.auth.infrastructure.persistence;

import com.example.auth.domain.entity.Credencial;
import com.example.auth.domain.repository.CredencialRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CredencialRepositoryImpl implements CredencialRepository {

    private final CredencialSpringDataRepository springDataRepository;

    public CredencialRepositoryImpl(CredencialSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Credencial salvar(Credencial credencial) {
        CredencialJpaEntity entidade = springDataRepository.save(CredencialJpaEntity.fromDomain(credencial));
        return entidade.toDomain();
    }

    @Override
    public Optional<Credencial> buscarPorEmail(String email) {
        return springDataRepository.findByEmail(email).map(CredencialJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }
}
