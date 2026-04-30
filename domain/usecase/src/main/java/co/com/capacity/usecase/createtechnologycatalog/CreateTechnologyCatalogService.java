package co.com.capacity.usecase.createtechnologycatalog;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import reactor.core.publisher.Mono;

public interface CreateTechnologyCatalogService {
    Mono<TechnologyCatalog> create(TechnologyCatalog technologyCatalog);
}