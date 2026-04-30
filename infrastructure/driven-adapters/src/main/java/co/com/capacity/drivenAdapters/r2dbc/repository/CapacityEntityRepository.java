package co.com.capacity.drivenAdapters.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import co.com.capacity.drivenAdapters.r2dbc.entity.CapacityEntity;

@Repository
public interface CapacityEntityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
}