package co.com.capacity.model.capacitytechnology.gateways;

import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityTechnologyRepository {
    Mono<CapacityTechnology> save(CapacityTechnology capacityTechnology);
    Flux<CapacityTechnology> saveAll(List<CapacityTechnology> capacityTechnologies);
    Flux<CapacityTechnology> findByCapacityId(Long capacityId);
    Flux<CapacityTechnology> findByCapacityIdIn(List<Long> capacityIds);
    Mono<CapacityTechnology> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<Void> deleteByCapacityId(Long capacityId);
    Mono<Void> deleteByCapacityIdIn(List<Long> capacityIds);
    Mono<Boolean> existsByTechnologyId(Long technologyId);
    Mono<Boolean> existsByTechnologyIdAndCapacityIdNot(Long technologyId, Long excludeCapacityId);
}