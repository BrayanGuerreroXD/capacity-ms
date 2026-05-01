package co.com.capacity.model.capacitybootcamp.gateways;

import co.com.capacity.model.capacitybootcamp.CapacityBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityBootcampRepository {
    Mono<CapacityBootcamp> save(CapacityBootcamp capacityBootcamp);
    Flux<CapacityBootcamp> findByBootcampId(Long bootcampId);
    Flux<CapacityBootcamp> findByCapacityId(Long capacityId);
    Mono<Void> deleteByBootcampId(Long bootcampId);
    Mono<Void> deleteByCapacityId(Long capacityId);
    Flux<CapacityBootcamp> saveAll(Flux<CapacityBootcamp> capacityBootcamps);
}