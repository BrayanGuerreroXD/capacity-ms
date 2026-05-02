package co.com.capacity.usecase.updatecapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import co.com.capacity.usecase.synctechnologycapacity.SyncTechnologyCapacityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCapacityUseCaseTest {

    @Mock
    private CapacityRepository capacityRepository;

    @Mock
    private CapacityEventGateway eventGateway;

    @Mock
    private TechnologyCatalogRepository technologyCatalogRepository;

    @Mock
    private CapacityTechnologyRepository capacityTechnologyRepository;

    @Mock
    private SyncTechnologyCapacityService syncTechnologyCapacityService;

    @InjectMocks
    private UpdateCapacityUseCase useCase;

    @Test
    void update_whenCapacityNotFound_throwsNotFoundException() {
        Capacity input = Capacity.builder().id(99L).name("Updated").description("desc").build();

        when(capacityRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.update(input))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        ((NotFoundException) e).getError() == GlobalExceptionEnum.CAPACITY_NOT_FOUND)
                .verify();
    }

    @Test
    void update_whenCapacityExists_updatesAndReplacesTechnologies() {
        Capacity existing = Capacity.builder().id(1L).name("Old").description("old desc").build();
        Capacity input = Capacity.builder().id(1L).name("Updated").description("new desc")
                .technologyIds(List.of(3L, 4L)).build();
        Capacity updated = Capacity.builder().id(1L).name("Updated").description("new desc")
                .updatedAt(LocalDateTime.now()).build();
        var goTech = co.com.capacity.model.technologycatalog.TechnologyCatalog.builder().id(3L).name("Go").build();
        var rustTech = co.com.capacity.model.technologycatalog.TechnologyCatalog.builder().id(4L).name("Rust").build();

        when(capacityRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(capacityRepository.update(any())).thenReturn(Mono.just(updated));
        when(capacityTechnologyRepository.findByCapacityId(1L))
                .thenReturn(Flux.just(
                        CapacityTechnology.builder().id(1L).capacityId(1L).technologyId(1L).build(),
                        CapacityTechnology.builder().id(2L).capacityId(1L).technologyId(2L).build()
                ));
        when(capacityTechnologyRepository.existsByTechnologyIdAndCapacityIdNot(any(), any())).thenReturn(Mono.just(false));
        when(capacityTechnologyRepository.deleteByCapacityId(1L)).thenReturn(Mono.empty());
        when(technologyCatalogRepository.findAllById(List.of(3L, 4L)))
                .thenReturn(Flux.just(goTech, rustTech));
        when(capacityTechnologyRepository.saveAll(anyList())).thenReturn(Flux.empty());
        when(eventGateway.publish(any())).thenReturn(Mono.empty());
        when(syncTechnologyCapacityService.publishSyncMatch(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.update(input))
                .expectNextMatches(cap -> cap.getId().equals(1L)
                        && cap.getTechnologies() != null
                        && cap.getTechnologies().size() == 2
                        && cap.getTechnologies().stream().anyMatch(t -> t.getName().equals("Go"))
                        && cap.getTechnologies().stream().anyMatch(t -> t.getName().equals("Rust")))
                .verifyComplete();

        verify(capacityTechnologyRepository).deleteByCapacityId(1L);
        verify(syncTechnologyCapacityService).publishSyncMatch(eq(1L), eq(List.of(3L, 4L)));
    }

    @Test
    void update_whenNoTechnologiesInRequest_deletesExistingTechnologies() {
        Capacity existing = Capacity.builder().id(1L).name("Old").description("old desc").build();
        Capacity input = Capacity.builder().id(1L).name("Updated").description("new desc")
                .technologyIds(List.of()).build();
        Capacity updated = Capacity.builder().id(1L).name("Updated").description("new desc").build();

        when(capacityRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(capacityRepository.update(any())).thenReturn(Mono.just(updated));
        when(capacityTechnologyRepository.findByCapacityId(1L))
                .thenReturn(Flux.just(
                        CapacityTechnology.builder().id(1L).capacityId(1L).technologyId(1L).build()
                ));
        when(capacityTechnologyRepository.deleteByCapacityId(1L)).thenReturn(Mono.empty());
        when(eventGateway.publish(any())).thenReturn(Mono.empty());
        when(syncTechnologyCapacityService.publishSyncMatch(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.update(input))
                .expectNextMatches(cap -> cap.getId().equals(1L)
                        && cap.getTechnologies() != null
                        && cap.getTechnologies().isEmpty())
                .verifyComplete();

        verify(capacityTechnologyRepository).deleteByCapacityId(1L);
        verify(syncTechnologyCapacityService).publishSyncMatch(eq(1L), eq(List.of()));
    }
}