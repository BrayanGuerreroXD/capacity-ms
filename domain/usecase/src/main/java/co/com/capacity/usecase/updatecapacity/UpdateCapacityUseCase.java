package co.com.capacity.usecase.updatecapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.model.utils.exception.BadRequestException;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class UpdateCapacityUseCase implements UpdateCapacityService {

    private final CapacityRepository capacityRepository;
    private final CapacityEventGateway eventGateway;
    private final TechnologyCatalogRepository technologyCatalogRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;

    @Override
    public Mono<Capacity> update(Capacity capacity) {
        return capacityRepository.findById(capacity.getId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)))
                .flatMap(existing -> updateCapacityWithTechnologies(capacity));
    }

    private Mono<Capacity> updateCapacityWithTechnologies(Capacity capacity) {
        return capacityRepository.update(capacity)
                .flatMap(updated -> updateTechnologies(capacity.getId(), capacity.getTechnologyIds())
                        .thenReturn(updated))
                .flatMap(updated -> eventGateway.publish(updated).thenReturn(updated));
    }

    private Mono<Void> updateTechnologies(Long capacityId, List<Long> newTechnologyIds) {
        if (newTechnologyIds == null) {
            newTechnologyIds = List.of();
        }
        final List<Long> technologyIdsToProcess = newTechnologyIds;

        return validateNoDuplicateTechnologies(technologyIdsToProcess)
                .then(validateTechnologiesNotAssigned(technologyIdsToProcess, capacityId))
                .then(capacityTechnologyRepository.findByCapacityId(capacityId)
                        .collectList()
                        .flatMap(existingAssociations -> {
                            Set<Long> existingTechIds = new HashSet<>();
                            for (CapacityTechnology ct : existingAssociations) {
                                existingTechIds.add(ct.getTechnologyId());
                            }
                            Set<Long> newTechIds = new HashSet<>(technologyIdsToProcess);

                            Set<Long> toDelete = new HashSet<>(existingTechIds);
                            toDelete.removeAll(newTechIds);

                            Set<Long> toAdd = new HashSet<>(newTechIds);
                            toAdd.removeAll(existingTechIds);

                            return processTechnologyChanges(capacityId, toDelete, toAdd);
                        }));
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
                                .flatMap(exists -> exists ? Mono.empty() : Mono.error(new BadRequestException(GlobalExceptionEnum.TECHNOLOGY_ALREADY_ASSIGNED))))
                        .toList()
        );
    }

    private Mono<Void> processTechnologyChanges(Long capacityId, Set<Long> toDelete, Set<Long> toAdd) {
        Mono<Void> deleteMono = toDelete.isEmpty()
                ? Mono.empty()
                : capacityTechnologyRepository.deleteByCapacityId(capacityId)
                        .then(Mono.empty());

        Mono<Void> addMono = toAdd.isEmpty()
                ? Mono.empty()
                : validateAndSaveTechnologies(capacityId, toAdd);

        return deleteMono.then(addMono);
    }

    private Mono<Void> validateAndSaveTechnologies(Long capacityId, Set<Long> technologyIds) {
        if (technologyIds.isEmpty()) {
            return Mono.empty();
        }
        return technologyCatalogRepository.findAllById(List.copyOf(technologyIds))
                .collectList()
                .flatMap(found -> {
                    if (found.size() != technologyIds.size()) {
                        return Mono.error(new BadRequestException(GlobalExceptionEnum.TECHNOLOGY_NOT_FOUND));
                    }
                    return saveNewTechnologies(capacityId, technologyIds);
                });
    }

    private Mono<Void> saveNewTechnologies(Long capacityId, Set<Long> technologyIds) {
        List<CapacityTechnology> associations = technologyIds.stream()
                .map(techId -> CapacityTechnology.builder()
                        .capacityId(capacityId)
                        .technologyId(techId)
                        .build())
                .toList();
        return capacityTechnologyRepository.saveAll(associations).then();
    }
}