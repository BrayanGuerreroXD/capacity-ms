package co.com.capacity.usecase.synctechnologycapacity;

import co.com.capacity.model.capacity.gateways.SyncTechnologyCapacityGateway;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncTechnologyCapacityUseCaseTest {

    @Mock
    private SyncTechnologyCapacityGateway syncGateway;

    @Mock
    private TechnologyCatalogRepository technologyCatalogRepository;

    @InjectMocks
    private SyncTechnologyCapacityUseCase useCase;

    @Test
    void publishSyncMatch_withTechnologyIds_resolvesExternalIdsAndPublishes() {
        Long capacityId = 1L;
        List<Long> technologyIds = List.of(10L, 20L);
        List<Long> externalIds = List.of(100L, 200L);

        when(technologyCatalogRepository.findAllById(technologyIds))
                .thenReturn(Flux.just(
                        TechnologyCatalog.builder().id(10L).externalId(100L).name("Java").build(),
                        TechnologyCatalog.builder().id(20L).externalId(200L).name("Kotlin").build()
                ));
        when(syncGateway.publishSyncMatch(eq(capacityId), eq(externalIds))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.publishSyncMatch(capacityId, technologyIds))
                .verifyComplete();

        verify(syncGateway).publishSyncMatch(capacityId, externalIds);
    }

    @Test
    void publishSyncMatch_withEmptyList_publishesWithEmptyList() {
        Long capacityId = 1L;
        List<Long> technologyIds = List.of();

        when(syncGateway.publishSyncMatch(eq(capacityId), eq(List.of()))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.publishSyncMatch(capacityId, technologyIds))
                .verifyComplete();

        verify(syncGateway).publishSyncMatch(capacityId, List.of());
    }

    @Test
    void publishSyncMatch_withNull_publishesWithEmptyList() {
        Long capacityId = 1L;

        when(syncGateway.publishSyncMatch(eq(capacityId), eq(List.of()))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.publishSyncMatch(capacityId, null))
                .verifyComplete();

        verify(syncGateway).publishSyncMatch(capacityId, List.of());
    }
}