package co.com.capacity.model.capacity.gateways;

import co.com.capacity.model.capacity.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityRepository {
    Mono<Capacity> save(Capacity capacity);
    Mono<Capacity> update(Capacity capacity);
    Mono<Capacity> findById(Long id);
    Mono<Capacity> findByName(String name);
    Flux<Capacity> findAll(int page, int size);
    Mono<Void> delete(Long id);
    Mono<Long> count();
}