package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityTechnologyEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CapacityTechnologyEntityRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {
    Flux<CapacityTechnologyEntity> findByCapacityId(Long capacityId);
    Flux<CapacityTechnologyEntity> findByTechnologyId(Long technologyId);
    Mono<Void> deleteByCapacityId(Long capacityId);
    Mono<Boolean> existsByTechnologyId(Long technologyId);
}