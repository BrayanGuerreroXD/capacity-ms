package co.com.capacity.usecase.deletebootcampmatch;

import co.com.capacity.model.capacitybootcamp.gateways.CapacityBootcampRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.gateways.TechnologyDeletedEventGateway;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class DeleteBootcampMatchUseCase implements DeleteBootcampMatchService {

    private final CapacityBootcampRepository capacityBootcampRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final TechnologyCatalogRepository technologyCatalogRepository;
    private final CapacityRepository capacityRepository;
    private final TechnologyDeletedEventGateway technologyDeletedEventGateway;

    @Override
    public Mono<Void> deleteMatch(Long bootcampId, List<Long> capacityIds) {
        return capacityBootcampRepository.findByBootcampIdAndCapacityIdIn(bootcampId, capacityIds)
                .map(cb -> cb.getCapacityId())
                .collectList()
                .flatMap(matchedCapacityIds -> {
                    if (matchedCapacityIds.isEmpty()) {
                        return Mono.empty();
                    }
                    return capacityTechnologyRepository.findByCapacityIdIn(matchedCapacityIds)
                            .map(CapacityTechnology::getTechnologyId)
                            .collectList()
                            .flatMap(technologyIds ->
                                capacityBootcampRepository.deleteByBootcampIdAndCapacityIdIn(bootcampId, matchedCapacityIds)
                                    .then(capacityTechnologyRepository.deleteByCapacityIdIn(matchedCapacityIds))
                                    .then(capacityRepository.deleteAllById(matchedCapacityIds))
                                    .then(technologyDeletedEventGateway.publishDeleted(technologyIds))
                            );
                });
    }
}