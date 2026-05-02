package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.AuthEntity;
import reactor.core.publisher.Mono;

@Repository
public interface AuthEntityRepository extends ReactiveCrudRepository<AuthEntity, Long> {
    Mono<AuthEntity> findByToken(String token);
    Mono<AuthEntity> findByEmail(String email);
    Mono<Void> deleteByToken(String token);
    Mono<Void> deleteByEmail(String email);
}