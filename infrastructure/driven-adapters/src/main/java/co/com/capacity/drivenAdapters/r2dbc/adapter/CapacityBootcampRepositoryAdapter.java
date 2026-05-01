package co.com.capacity.drivenAdapters.r2dbc.adapter;

import co.com.capacity.drivenAdapters.r2dbc.mapper.CapacityBootcampEntityMapper;
import co.com.capacity.drivenAdapters.r2dbc.repository.CapacityBootcampEntityRepository;
import co.com.capacity.model.capacitybootcamp.CapacityBootcamp;
import co.com.capacity.model.capacitybootcamp.gateways.CapacityBootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CapacityBootcampRepositoryAdapter implements CapacityBootcampRepository {

    private final CapacityBootcampEntityRepository entityRepository;
    private final CapacityBootcampEntityMapper mapper;

    @Override
    public Mono<CapacityBootcamp> save(CapacityBootcamp capacityBootcamp) {
        return entityRepository.save(mapper.toEntity(capacityBootcamp))
                .map(mapper::toModel);
    }

    @Override
    public Flux<CapacityBootcamp> findByBootcampId(Long bootcampId) {
        return entityRepository.findByBootcampId(bootcampId)
                .map(mapper::toModel);
    }

    @Override
    public Flux<CapacityBootcamp> findByCapacityId(Long capacityId) {
        return entityRepository.findByCapacityId(capacityId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteByBootcampId(Long bootcampId) {
        return findByBootcampId(bootcampId)
                .flatMap(cb -> entityRepository.deleteById(cb.getId()))
                .then();
    }

    @Override
    public Mono<Void> deleteByCapacityId(Long capacityId) {
        return findByCapacityId(capacityId)
                .flatMap(cb -> entityRepository.deleteById(cb.getId()))
                .then();
    }

    @Override
    public Flux<CapacityBootcamp> saveAll(Flux<CapacityBootcamp> capacityBootcamps) {
        return entityRepository.saveAll(capacityBootcamps.map(mapper::toEntity))
                .map(mapper::toModel);
    }
}