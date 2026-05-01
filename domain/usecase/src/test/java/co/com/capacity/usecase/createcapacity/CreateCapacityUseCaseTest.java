package co.com.capacity.usecase.createcapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import co.com.capacity.usecase.synctechnologycapacity.SyncTechnologyCapacityService;
import co.com.capacity.model.utils.exception.BadRequestException;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
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
class CreateCapacityUseCaseTest {

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
    private CreateCapacityUseCase useCase;

    @Test
    void create_whenNameAlreadyExists_throwsConflictException() {
        Capacity existing = Capacity.builder().id(1L).name("Java").description("desc").build();
        Capacity input = Capacity.builder().name("Java").description("new desc").build();

        when(capacityRepository.findByName("Java")).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.create(input))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        ((ConflictException) e).getError() == GlobalExceptionEnum.CAPACITY_NAME_ALREADY_EXISTS)
                .verify();
    }

    @Test
    void create_whenNameIsUniqueAndNoTechnologies_savesCapacity() {
        Capacity input = Capacity.builder().name("Kotlin").description("JVM language").build();
        Capacity saved = Capacity.builder().id(2L).name("Kotlin").description("JVM language")
                .createdAt(LocalDateTime.now()).build();

        when(capacityRepository.findByName("Kotlin")).thenReturn(Mono.empty());
        when(capacityRepository.save(any())).thenReturn(Mono.just(saved));
        when(eventGateway.publish(saved)).thenReturn(Mono.empty());
        when(syncTechnologyCapacityService.publishSyncMatch(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(input))
                .expectNext(saved)
                .verifyComplete();

        verify(eventGateway).publish(saved);
        verify(syncTechnologyCapacityService).publishSyncMatch(eq(2L), eq(List.of()));
    }

    @Test
    void create_whenTechnologiesExist_savesCapacityAndAssociations() {
        Capacity input = Capacity.builder().name("Go").description("Language")
                .technologyIds(List.of(1L, 2L)).build();
        Capacity saved = Capacity.builder().id(3L).name("Go").description("Language")
                .createdAt(LocalDateTime.now()).build();

        when(capacityRepository.findByName("Go")).thenReturn(Mono.empty());
        when(technologyCatalogRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(Flux.just(
                        TechnologyCatalog.builder().id(1L).name("Java").build(),
                        TechnologyCatalog.builder().id(2L).name("Kotlin").build()
                ));
        when(capacityTechnologyRepository.existsByTechnologyIdAndCapacityIdNot(any(), any())).thenReturn(Mono.just(false));
        when(capacityRepository.save(any())).thenReturn(Mono.just(saved));
        when(capacityTechnologyRepository.saveAll(anyList())).thenReturn(Flux.empty());
        when(eventGateway.publish(saved)).thenReturn(Mono.empty());
        when(syncTechnologyCapacityService.publishSyncMatch(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(input))
                .expectNext(saved)
                .verifyComplete();

        verify(capacityTechnologyRepository).saveAll(anyList());
        verify(syncTechnologyCapacityService).publishSyncMatch(eq(3L), eq(List.of(1L, 2L)));
    }

    @Test
    void create_whenTechnologyNotFound_throwsBadRequestException() {
        Capacity input = Capacity.builder().name("Rust").description("Language")
                .technologyIds(List.of(99L)).build();

        when(capacityRepository.findByName("Rust")).thenReturn(Mono.empty());
        when(technologyCatalogRepository.findAllById(List.of(99L))).thenReturn(Flux.empty());

        StepVerifier.create(useCase.create(input))
                .expectError()
                .verify();
    }
}