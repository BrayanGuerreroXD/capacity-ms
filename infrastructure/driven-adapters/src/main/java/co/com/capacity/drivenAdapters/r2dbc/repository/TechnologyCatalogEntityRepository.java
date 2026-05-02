package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.TechnologyCatalogEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface TechnologyCatalogEntityRepository extends ReactiveCrudRepository<TechnologyCatalogEntity, Long> {
    Mono<TechnologyCatalogEntity> findByExternalId(Long externalId);
    Flux<TechnologyCatalogEntity> findByExternalIdIn(List<Long> externalIds);
    Mono<Boolean> existsByExternalId(Long externalId);
    Mono<Void> deleteByExternalId(Long externalId);
    Mono<Void> deleteByExternalIdIn(List<Long> externalIds);
}