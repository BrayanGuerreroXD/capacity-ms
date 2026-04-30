package co.com.capacity.usecase.deletetechnologycatalog;

import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteTechnologyCatalogUseCase implements DeleteTechnologyCatalogService {

    private final TechnologyCatalogRepository technologyCatalogRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;

    @Override
    public Mono<Void> deleteByExternalId(Long externalId) {
        return capacityTechnologyRepository.existsByTechnologyId(externalId)
                .flatMap(inUse -> {
                    if (inUse) {
                        return Mono.error(new ConflictException(GlobalExceptionEnum.TECHNOLOGY_IN_USE));
                    }
                    return technologyCatalogRepository.deleteByExternalId(externalId);
                });
    }
}