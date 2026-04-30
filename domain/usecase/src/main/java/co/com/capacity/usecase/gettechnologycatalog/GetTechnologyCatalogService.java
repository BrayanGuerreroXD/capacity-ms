package co.com.capacity.usecase.gettechnologycatalog;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import reactor.core.publisher.Flux;

public interface GetTechnologyCatalogService {
    Flux<TechnologyCatalog> getAll();
}