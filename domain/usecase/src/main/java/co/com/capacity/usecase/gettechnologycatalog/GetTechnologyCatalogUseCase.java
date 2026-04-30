package co.com.capacity.usecase.gettechnologycatalog;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetTechnologyCatalogUseCase implements GetTechnologyCatalogService {

    private final TechnologyCatalogRepository technologyCatalogRepository;

    @Override
    public Flux<TechnologyCatalog> getAll() {
        return technologyCatalogRepository.findAll();
    }
}