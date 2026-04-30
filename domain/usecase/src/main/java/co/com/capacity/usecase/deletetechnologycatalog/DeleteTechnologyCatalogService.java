package co.com.capacity.usecase.deletetechnologycatalog;

import reactor.core.publisher.Mono;

public interface DeleteTechnologyCatalogService {
    Mono<Void> deleteByExternalId(Long externalId);
}