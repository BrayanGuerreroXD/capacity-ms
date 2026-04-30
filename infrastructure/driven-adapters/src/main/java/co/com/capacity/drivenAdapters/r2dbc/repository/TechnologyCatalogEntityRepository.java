package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.TechnologyCatalogEntity;
import reactor.core.publisher.Mono;

@Repository
public interface TechnologyCatalogEntityRepository extends ReactiveCrudRepository<TechnologyCatalogEntity, Long> {
    Mono<TechnologyCatalogEntity> findByExternalId(Long externalId);
    Mono<Boolean> existsByExternalId(Long externalId);
    Mono<Void> deleteByExternalId(Long externalId);
}