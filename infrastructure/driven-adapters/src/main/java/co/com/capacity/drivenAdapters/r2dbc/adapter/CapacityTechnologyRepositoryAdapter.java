package co.com.capacity.drivenAdapters.r2dbc.adapter;

import co.com.capacity.drivenAdapters.r2dbc.mapper.CapacityTechnologyEntityMapper;
import co.com.capacity.drivenAdapters.r2dbc.repository.CapacityTechnologyEntityRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CapacityTechnologyRepositoryAdapter implements CapacityTechnologyRepository {

    private final CapacityTechnologyEntityRepository entityRepository;
    private final CapacityTechnologyEntityMapper mapper;

    @Override
    public Mono<CapacityTechnology> save(CapacityTechnology capacityTechnology) {
        return entityRepository.save(mapper.toEntity(capacityTechnology))
                .map(mapper::toModel);
    }

    @Override
    public Flux<CapacityTechnology> saveAll(List<CapacityTechnology> capacityTechnologies) {
        return entityRepository.saveAll(capacityTechnologies.stream()
                .map(mapper::toEntity)
                .toList())
                .map(mapper::toModel);
    }

    @Override
    public Flux<CapacityTechnology> findByCapacityId(Long capacityId) {
        return entityRepository.findByCapacityId(capacityId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<CapacityTechnology> findById(Long id) {
        return entityRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return entityRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByCapacityId(Long capacityId) {
        return findByCapacityId(capacityId)
                .flatMap(ct -> entityRepository.deleteById(ct.getId()))
                .then();
    }

    @Override
    public Mono<Boolean> existsByTechnologyId(Long technologyId) {
        return entityRepository.existsByTechnologyId(technologyId);
    }
}