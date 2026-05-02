package co.com.capacity.model.capacitybootcamp.gateways;

import co.com.capacity.model.capacitybootcamp.CapacityBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityBootcampRepository {
    Mono<CapacityBootcamp> save(CapacityBootcamp capacityBootcamp);
    Flux<CapacityBootcamp> findByBootcampId(Long bootcampId);
    Flux<CapacityBootcamp> findByBootcampIdAndCapacityIdIn(Long bootcampId, List<Long> capacityIds);
    Flux<CapacityBootcamp> findByCapacityId(Long capacityId);
    Mono<Void> deleteByBootcampId(Long bootcampId);
    Mono<Void> deleteByBootcampIdAndCapacityIdIn(Long bootcampId, List<Long> capacityIds);
    Mono<Void> deleteByCapacityId(Long capacityId);
    Flux<CapacityBootcamp> saveAll(Flux<CapacityBootcamp> capacityBootcamps);
}