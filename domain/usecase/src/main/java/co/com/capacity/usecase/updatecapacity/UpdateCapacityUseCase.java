package co.com.capacity.usecase.updatecapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateCapacityUseCase implements UpdateCapacityService {

    private final CapacityRepository capacityRepository;
    private final CapacityEventGateway eventGateway;

    @Override
    public Mono<Capacity> update(Capacity capacity) {
        return capacityRepository.findById(capacity.getId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)))
                .flatMap(existing -> capacityRepository.update(capacity))
                .flatMap(updated -> eventGateway.publish(updated)
                        .thenReturn(updated));
    }
}