package co.com.capacity.usecase.synccapacitybootcampservice;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SyncCapacityBootcampService {
    Mono<Void> syncBootcampCapacities(Long bootcampId, List<Long> capacityIds);
}