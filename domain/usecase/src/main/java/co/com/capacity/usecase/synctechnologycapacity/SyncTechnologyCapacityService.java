package co.com.capacity.usecase.synctechnologycapacity;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SyncTechnologyCapacityService {
    Mono<Void> publishSyncMatch(Long capacityId, List<Long> technologyIds);
}