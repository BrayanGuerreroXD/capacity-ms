package co.com.capacity.drivenAdapters.r2dbc.adapter;

import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityEntity;
import co.com.capacity.drivenAdapters.r2dbc.mapper.CapacityEntityMapper;
import co.com.capacity.drivenAdapters.r2dbc.repository.CapacityEntityRepository;
import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class CapacityRepositoryAdapter implements CapacityRepository {

    private final CapacityEntityRepository entityRepository;
    private final CapacityEntityMapper mapper;

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return entityRepository.save(mapper.toEntity(capacity))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Capacity> update(Capacity capacity) {
        CapacityEntity entity = mapper.toEntity(capacity);
        entity.setUpdatedAt(LocalDateTime.now());
        return entityRepository.save(entity)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Capacity> findById(Long id) {
        return entityRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Capacity> findByName(String name) {
        return entityRepository.findAll()
                .filter(e -> e.getName().equals(name))
                .next()
                .map(mapper::toModel);
    }

    @Override
    public Flux<Capacity> findAll(int page, int size) {
        return entityRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return entityRepository.deleteById(id);
    }

    @Override
    public Mono<Long> count() {
        return entityRepository.count();
    }
}