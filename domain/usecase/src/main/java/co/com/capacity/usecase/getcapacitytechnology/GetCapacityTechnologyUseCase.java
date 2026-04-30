package co.com.capacity.usecase.getcapacitytechnology;

import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetCapacityTechnologyUseCase implements GetCapacityTechnologyService {

    private final CapacityTechnologyRepository capacityTechnologyRepository;

    @Override
    public Mono<CapacityTechnology> getById(Long id) {
        return capacityTechnologyRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException(GlobalExceptionEnum.CAPACITY_TECHNOLOGY_NOT_FOUND)));
    }

    @Override
    public Flux<CapacityTechnology> findByCapacityId(Long capacityId) {
        return capacityTechnologyRepository.findByCapacityId(capacityId);
    }
}