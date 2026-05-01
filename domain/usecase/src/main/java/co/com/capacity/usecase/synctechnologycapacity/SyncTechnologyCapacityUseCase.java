package co.com.capacity.usecase.synctechnologycapacity;

import co.com.capacity.model.capacity.gateways.SyncTechnologyCapacityGateway;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SyncTechnologyCapacityUseCase implements SyncTechnologyCapacityService {

    private final SyncTechnologyCapacityGateway syncGateway;
    private final TechnologyCatalogRepository technologyCatalogRepository;

    @Override
    public Mono<Void> publishSyncMatch(Long capacityId, List<Long> technologyIds) {
        return resolveExternalIds(technologyIds)
                .flatMap(externalIds -> syncGateway.publishSyncMatch(capacityId, externalIds));
    }

    private Mono<List<Long>> resolveExternalIds(List<Long> technologyIds) {
        if (technologyIds == null || technologyIds.isEmpty()) {
            return Mono.just(List.of());
        }
        return technologyCatalogRepository.findAllById(technologyIds)
                .map(tech -> tech.getExternalId())
                .collectList();
    }
}