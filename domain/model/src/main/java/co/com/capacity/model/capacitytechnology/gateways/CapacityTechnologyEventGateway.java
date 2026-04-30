package co.com.capacity.model.capacitytechnology.gateways;

import co.com.capacity.model.capacitytechnology.CapacityTechnologyEvent;
import reactor.core.publisher.Mono;

public interface CapacityTechnologyEventGateway {
    Mono<Void> publishCreated(CapacityTechnologyEvent event);
    Mono<Void> publishDeleted(CapacityTechnologyEvent event);
}