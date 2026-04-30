package co.com.capacity.usecase.updatecapacity;

import co.com.capacity.model.capacity.Capacity;
import reactor.core.publisher.Mono;

public interface UpdateCapacityService {
    Mono<Capacity> update(Capacity capacity);
}