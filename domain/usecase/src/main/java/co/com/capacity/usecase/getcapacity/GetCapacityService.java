package co.com.capacity.usecase.getcapacity;

import co.com.capacity.model.capacity.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetCapacityService {
    Mono<Capacity> getById(Long id);
    Flux<Capacity> getAll(int page, int size);
}