package co.com.capacity.usecase.deletecapacitytechnology;

import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.CapacityTechnologyEvent;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyEventGateway;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteCapacityTechnologyUseCase implements DeleteCapacityTechnologyService {

    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final CapacityTechnologyEventGateway eventGateway;

    @Override
    public Mono<Void> deleteById(Long id) {
        return capacityTechnologyRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_TECHNOLOGY_NOT_FOUND)))
                .flatMap(capacityTechnology -> {
                    CapacityTechnologyEvent event = CapacityTechnologyEvent.builder()
                            .capacityId(capacityTechnology.getCapacityId())
                            .technologyCatalogId(capacityTechnology.getTechnologyId())
                            .build();
                    return capacityTechnologyRepository.deleteById(id)
                            .then(eventGateway.publishDeleted(event));
                });
    }
}