package co.com.capacity.usecase.createcapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.usecase.synctechnologycapacity.SyncTechnologyCapacityService;
import co.com.capacity.model.utils.exception.BadRequestException;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class CreateCapacityUseCase implements CreateCapacityService {

    private final CapacityRepository capacityRepository;
    private final CapacityEventGateway eventGateway;
    private final TechnologyCatalogRepository technologyCatalogRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final SyncTechnologyCapacityService syncTechnologyCapacityService;

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
                    .doOnSuccess(saved -> {
                        eventGateway.publish(saved).subscribe(null, error -> {});
                        syncTechnologyCapacityService.publishSyncMatch(saved.getId(), List.of()).subscribe(null, error -> {});
                    });
        }
        return validateTechnologiesExist(technologyIds)
                .then(validateNoDuplicateTechnologies(technologyIds))
                .then(validateTechnologiesNotAssigned(technologyIds, null))
                .then(capacityRepository.save(capacity))
                .flatMap(saved -> saveTechnologies(saved.getId(), technologyIds)
                        .thenReturn(saved))
                .doOnSuccess(saved -> {
                    eventGateway.publish(saved).subscribe(null, error -> {});
                    syncTechnologyCapacityService.publishSyncMatch(saved.getId(), technologyIds).subscribe(null, error -> {});
                });
    }

    private Mono<Void> validateTechnologiesExist(List<Long> technologyIds) {
        if (technologyIds == null || technologyIds.isEmpty()) {
            return Mono.empty();
        }
        return technologyCatalogRepository.findAllById(technologyIds)
                .collectList()
                .flatMap(found -> {
                    if (found.size() != technologyIds.size()) {
                        return Mono.error(new BadRequestException(GlobalExceptionEnum.TECHNOLOGY_NOT_FOUND));
                    }
                    return Mono.<Void>empty();
                });
    }

    private Mono<Void> validateNoDuplicateTechnologies(List<Long> technologyIds) {
        if (technologyIds == null || technologyIds.isEmpty()) {
            return Mono.empty();
        }
        Set<Long> uniqueIds = new HashSet<>(technologyIds);
        if (uniqueIds.size() != technologyIds.size()) {
            return Mono.error(new BadRequestException(GlobalExceptionEnum.INVALID_REQUEST));
        }
        return Mono.empty();
    }

    private Mono<Void> validateTechnologiesNotAssigned(List<Long> technologyIds, Long excludeCapacityId) {
        if (technologyIds == null || technologyIds.isEmpty()) {
            return Mono.empty();
        }
        return Mono.when(
                technologyIds.stream()
                        .map(techId -> capacityTechnologyRepository.existsByTechnologyIdAndCapacityIdNot(techId, excludeCapacityId)
                                .flatMap(exists -> exists ? Mono.error(new BadRequestException(GlobalExceptionEnum.TECHNOLOGY_ALREADY_ASSIGNED)) : Mono.empty()))
                        .toList()
        );
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