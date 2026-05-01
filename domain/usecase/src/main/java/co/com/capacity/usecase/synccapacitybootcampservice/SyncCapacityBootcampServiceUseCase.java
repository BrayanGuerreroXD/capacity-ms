package co.com.capacity.usecase.synccapacitybootcampservice;

import co.com.capacity.model.capacitybootcamp.CapacityBootcamp;
import co.com.capacity.model.capacitybootcamp.gateways.CapacityBootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SyncCapacityBootcampServiceUseCase implements SyncCapacityBootcampService {

    private final CapacityBootcampRepository capacityBootcampRepository;

    @Override
    public Mono<Void> syncBootcampCapacities(Long bootcampId, List<Long> capacityIds) {
        return capacityBootcampRepository.deleteByBootcampId(bootcampId)
                .thenMany(createNewAssociations(bootcampId, capacityIds))
                .then();
    }

    private Flux<CapacityBootcamp> createNewAssociations(Long bootcampId, List<Long> capacityIds) {
        if (capacityIds == null || capacityIds.isEmpty()) {
            return Flux.empty();
        }
        List<CapacityBootcamp> associations = capacityIds.stream()
                .map(capacityId -> CapacityBootcamp.builder()
                        .capacityId(capacityId)
                        .bootcampId(bootcampId)
                        .build())
                .toList();
        return capacityBootcampRepository.saveAll(Flux.fromIterable(associations));
    }
}