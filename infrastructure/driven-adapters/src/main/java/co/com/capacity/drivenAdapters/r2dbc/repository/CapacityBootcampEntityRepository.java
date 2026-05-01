package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityBootcampEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CapacityBootcampEntityRepository extends ReactiveCrudRepository<CapacityBootcampEntity, Long> {
    Flux<CapacityBootcampEntity> findByBootcampId(Long bootcampId);
    Flux<CapacityBootcampEntity> findByCapacityId(Long capacityId);
    Mono<Void> deleteByBootcampId(Long bootcampId);
    Mono<Void> deleteByCapacityId(Long capacityId);
}