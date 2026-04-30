package co.com.capacity.usecase.deletecapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteCapacityUseCase implements DeleteCapacityService {

    private final CapacityRepository capacityRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final CapacityEventGateway eventGateway;

    @Override
    public Mono<Void> delete(Long id) {
        return capacityRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)))
                .flatMap(capacity -> capacityRepository.delete(id)
                        .then(capacityTechnologyRepository.deleteByCapacityId(id))
                        .then(eventGateway.publishDeleted(capacity).then()));
    }
}