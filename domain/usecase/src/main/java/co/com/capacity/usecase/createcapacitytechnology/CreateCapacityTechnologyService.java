package co.com.capacity.usecase.createcapacitytechnology;

import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import reactor.core.publisher.Mono;

public interface CreateCapacityTechnologyService {
    Mono<CapacityTechnology> create(CapacityTechnology capacityTechnology);
}