package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityBootcampEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CapacityBootcampEntityRepository extends ReactiveCrudRepository<CapacityBootcampEntity, Long> {
    Flux<CapacityBootcampEntity> findByBootcampId(Long bootcampId);
    Flux<CapacityBootcampEntity> findByBootcampIdAndCapacityIdIn(Long bootcampId, List<Long> capacityIds);
    Flux<CapacityBootcampEntity> findByCapacityId(Long capacityId);
    Mono<Void> deleteByBootcampId(Long bootcampId);
    Mono<Void> deleteByBootcampIdAndCapacityIdIn(Long bootcampId, List<Long> capacityIds);
    Mono<Void> deleteByCapacityId(Long capacityId);
}