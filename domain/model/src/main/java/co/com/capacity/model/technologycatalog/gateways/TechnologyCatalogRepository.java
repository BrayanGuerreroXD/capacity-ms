package co.com.capacity.model.technologycatalog.gateways;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyCatalogRepository {
    Mono<TechnologyCatalog> save(TechnologyCatalog technologyCatalog);
    Mono<TechnologyCatalog> findByExternalId(Long externalId);
    Mono<TechnologyCatalog> findById(Long id);
    Flux<TechnologyCatalog> findAllById(List<Long> ids);
    Mono<Boolean> existsByExternalId(Long externalId);
    Mono<Void> deleteByExternalId(Long externalId);
}