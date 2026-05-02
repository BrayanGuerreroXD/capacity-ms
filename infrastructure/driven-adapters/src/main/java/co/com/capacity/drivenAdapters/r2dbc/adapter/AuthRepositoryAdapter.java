package co.com.capacity.drivenAdapters.r2dbc.adapter;

import co.com.capacity.drivenAdapters.r2dbc.entity.AuthEntity;
import co.com.capacity.drivenAdapters.r2dbc.mapper.AuthEntityMapper;
import co.com.capacity.drivenAdapters.r2dbc.repository.AuthEntityRepository;
import co.com.capacity.model.auth.Auth;
import co.com.capacity.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryAdapter implements AuthRepository {

    private final AuthEntityRepository entityRepository;
    private final AuthEntityMapper mapper;

    @Override
    public Mono<Auth> save(Auth auth) {
        AuthEntity entity = mapper.toEntity(auth);
        if (entity.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
@Override
    public Mono<Auth> findByEmail(String email) {
        return entityRepository.findByEmail(email)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteByEmail(String email) {
        return entityRepository.findByEmail(email)
                .flatMap(entity -> entityRepository.deleteById(entity.getId()))
                .then();
    }
}
        return entityRepository.save(entity)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Auth> findByToken(String token) {
        return entityRepository.findByToken(token)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteByToken(String token) {
        return entityRepository.findByToken(token)
                .flatMap(entity -> entityRepository.deleteById(entity.getId()))
                .then();
    }
}