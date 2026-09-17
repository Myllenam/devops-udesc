package com.example.usuarios.infrastructure.persistence;

import com.example.usuarios.domain.entity.Usuario;
import com.example.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioSpringDataRepository springDataRepository;

    public UsuarioRepositoryImpl(UsuarioSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entidade = springDataRepository.save(UsuarioJpaEntity.fromDomain(usuario));
        return entidade.toDomain();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return springDataRepository.findById(id).map(UsuarioJpaEntity::toDomain);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return springDataRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return springDataRepository.existsByEmailAndIdNot(email, id);
    }
}
