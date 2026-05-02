package co.com.capacity.model.capacity.gateways;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SyncTechnologyCapacityGateway {
    Mono<Void> publishSyncMatch(Long capacityId, List<Long> technologyExternalIds);
}