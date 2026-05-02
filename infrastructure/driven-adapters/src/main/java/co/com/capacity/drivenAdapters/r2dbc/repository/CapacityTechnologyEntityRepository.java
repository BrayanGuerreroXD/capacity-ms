package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityTechnologyEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CapacityTechnologyEntityRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {
    Flux<CapacityTechnologyEntity> findByCapacityId(Long capacityId);
    Flux<CapacityTechnologyEntity> findByCapacityIdIn(List<Long> capacityIds);
    Flux<CapacityTechnologyEntity> findByTechnologyId(Long technologyId);
    Mono<Void> deleteByCapacityId(Long capacityId);
    Mono<Void> deleteByCapacityIdIn(List<Long> capacityIds);
    Mono<Boolean> existsByTechnologyId(Long technologyId);

    @Query("SELECT EXISTS(SELECT 1 FROM capacity_technologies WHERE technology_id = :technologyId AND capacity_id != :excludeCapacityId)")
    Mono<Boolean> existsByTechnologyIdAndCapacityIdNot(Long technologyId, Long excludeCapacityId);
}