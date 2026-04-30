package co.com.capacity.usecase.createcapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateCapacityUseCase implements CreateCapacityService {

    private final CapacityRepository capacityRepository;
    private final CapacityEventGateway eventGateway;

    @Override
    public Mono<Capacity> create(Capacity capacity) {
        return capacityRepository.findByName(capacity.getName())
                .flatMap(existing -> Mono.<Capacity>error(
                        new ConflictException(GlobalExceptionEnum.CAPACITY_NAME_ALREADY_EXISTS)))
                .switchIfEmpty(Mono.defer(() -> capacityRepository.save(capacity)))
                .doOnSuccess(saved -> eventGateway.publish(saved)
                        .subscribe(null, error -> {}));
    }
}