package co.com.capacity.model.technologycatalog.gateways;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import reactor.core.publisher.Mono;

public interface TechnologyCatalogRepository {
    Mono<TechnologyCatalog> save(TechnologyCatalog technologyCatalog);
    Mono<TechnologyCatalog> findByExternalId(Long externalId);
    Mono<TechnologyCatalog> findById(Long id);
    Mono<Boolean> existsByExternalId(Long externalId);
    Mono<Void> deleteByExternalId(Long externalId);
}