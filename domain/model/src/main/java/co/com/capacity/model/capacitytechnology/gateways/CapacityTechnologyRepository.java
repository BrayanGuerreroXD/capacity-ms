package co.com.capacity.model.capacitytechnology.gateways;

import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityTechnologyRepository {
    Mono<CapacityTechnology> save(CapacityTechnology capacityTechnology);
    Flux<CapacityTechnology> findByCapacityId(Long capacityId);
    Mono<CapacityTechnology> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<Void> deleteByCapacityId(Long capacityId);
    Mono<Boolean> existsByTechnologyId(Long technologyId);
}