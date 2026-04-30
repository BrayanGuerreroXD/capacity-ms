package co.com.capacity.usecase.createcapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.model.utils.exception.BadRequestException;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CreateCapacityUseCase implements CreateCapacityService {

    private final CapacityRepository capacityRepository;
    private final CapacityEventGateway eventGateway;
    private final TechnologyCatalogRepository technologyCatalogRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;

    @Override
    public Mono<Capacity> create(Capacity capacity) {
        return capacityRepository.findByName(capacity.getName())
                .flatMap(existing -> Mono.<Capacity>error(
                        new ConflictException(GlobalExceptionEnum.CAPACITY_NAME_ALREADY_EXISTS)))
                .switchIfEmpty(Mono.defer(() -> saveCapacityWithTechnologies(capacity)));
    }

    private Mono<Capacity> saveCapacityWithTechnologies(Capacity capacity) {
        List<Long> technologyIds = capacity.getTechnologyIds();
        if (technologyIds == null || technologyIds.isEmpty()) {
            return capacityRepository.save(capacity)
                    .doOnSuccess(saved -> eventGateway.publish(saved).subscribe(null, error -> {}));
        }
        return validateTechnologiesExist(technologyIds)
                .then(capacityRepository.save(capacity))
                .flatMap(saved -> saveTechnologies(saved.getId(), technologyIds)
                        .thenReturn(saved))
                .doOnSuccess(saved -> eventGateway.publish(saved).subscribe(null, error -> {}));
    }

    private Mono<Void> validateTechnologiesExist(List<Long> technologyIds) {
        return technologyCatalogRepository.findAllById(technologyIds)
                .collectList()
                .flatMap(found -> {
                    if (found.size() != technologyIds.size()) {
                        return Mono.error(new BadRequestException(GlobalExceptionEnum.TECHNOLOGY_NOT_FOUND));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> saveTechnologies(Long capacityId, List<Long> technologyIds) {
        List<CapacityTechnology> associations = technologyIds.stream()
                .map(techId -> CapacityTechnology.builder()
                        .capacityId(capacityId)
                        .technologyId(techId)
                        .build())
                .toList();
        return capacityTechnologyRepository.saveAll(associations).then();
    }
}