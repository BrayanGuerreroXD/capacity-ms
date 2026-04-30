package co.com.capacity.usecase.createcapacity;

import co.com.capacity.model.capacity.Capacity;
import reactor.core.publisher.Mono;

public interface CreateCapacityService {
    Mono<Capacity> create(Capacity capacity);
}