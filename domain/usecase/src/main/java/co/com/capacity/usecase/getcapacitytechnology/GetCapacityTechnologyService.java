package co.com.capacity.usecase.getcapacitytechnology;

import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetCapacityTechnologyService {
    Mono<CapacityTechnology> getById(Long id);
    Flux<CapacityTechnology> findByCapacityId(Long capacityId);
}