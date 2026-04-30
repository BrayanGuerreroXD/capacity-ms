package co.com.capacity.usecase.getcapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetCapacityUseCase implements GetCapacityService {

    private final CapacityRepository capacityRepository;

    @Override
    public Mono<Capacity> getById(Long id) {
        return capacityRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)));
    }

    @Override
    public Flux<Capacity> getAll(int page, int size) {
        return capacityRepository.findAll(page, size);
    }
}