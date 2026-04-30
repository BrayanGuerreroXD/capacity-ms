package co.com.capacity.model.capacity.gateways;

import co.com.capacity.model.capacity.Capacity;
import reactor.core.publisher.Mono;

public interface CapacityEventGateway {
    Mono<Void> publish(Capacity capacity);
    Mono<Void> publishDeleted(Capacity capacity);
}