package co.com.capacity.usecase.createcapacitytechnology;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.CapacityTechnologyEvent;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyEventGateway;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateCapacityTechnologyUseCase implements CreateCapacityTechnologyService {

    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final CapacityTechnologyEventGateway eventGateway;
    private final CapacityRepository capacityRepository;

    @Override
    public Mono<CapacityTechnology> create(CapacityTechnology capacityTechnology) {
        return capacityRepository.findById(capacityTechnology.getCapacityId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)))
                .flatMap(capacity -> capacityTechnologyRepository.save(capacityTechnology))
                .flatMap(saved -> {
                    CapacityTechnologyEvent event = CapacityTechnologyEvent.builder()
                            .capacityId(saved.getCapacityId())
                            .technologyCatalogId(saved.getTechnologyId())
                            .build();
                    return eventGateway.publishCreated(event)
                            .thenReturn(saved);
                });
    }
}