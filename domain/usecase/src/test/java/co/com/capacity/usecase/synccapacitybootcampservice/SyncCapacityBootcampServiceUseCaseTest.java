package co.com.capacity.usecase.synccapacitybootcampservice;

import co.com.capacity.model.capacitybootcamp.CapacityBootcamp;
import co.com.capacity.model.capacitybootcamp.gateways.CapacityBootcampRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SyncCapacityBootcampServiceUseCaseTest {

    @Mock
    private CapacityBootcampRepository capacityBootcampRepository;

    @InjectMocks
    private SyncCapacityBootcampServiceUseCase useCase;

    @Test
    void syncBootcampCapacities_withCapacityIds_deletesAndRecreates() {
        Long bootcampId = 1L;
        List<Long> capacityIds = List.of(10L, 20L, 30L);

        when(capacityBootcampRepository.deleteByBootcampId(bootcampId)).thenReturn(Mono.empty());
        when(capacityBootcampRepository.saveAll(any()))
                .thenReturn(Flux.just(
                        CapacityBootcamp.builder().id(1L).bootcampId(bootcampId).capacityId(10L).build(),
                        CapacityBootcamp.builder().id(2L).bootcampId(bootcampId).capacityId(20L).build(),
                        CapacityBootcamp.builder().id(3L).bootcampId(bootcampId).capacityId(30L).build()
                ));

        StepVerifier.create(useCase.syncBootcampCapacities(bootcampId, capacityIds))
                .verifyComplete();

        verify(capacityBootcampRepository).deleteByBootcampId(bootcampId);
        verify(capacityBootcampRepository).saveAll(any());
    }

    @Test
    void syncBootcampCapacities_withEmptyList_deletesAndCreatesEmpty() {
        Long bootcampId = 1L;
        List<Long> capacityIds = List.of();

        when(capacityBootcampRepository.deleteByBootcampId(bootcampId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.syncBootcampCapacities(bootcampId, capacityIds))
                .verifyComplete();

        verify(capacityBootcampRepository).deleteByBootcampId(bootcampId);
    }

    @Test
    void syncBootcampCapacities_withNull_deletesAndCreatesEmpty() {
        Long bootcampId = 1L;

        when(capacityBootcampRepository.deleteByBootcampId(bootcampId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.syncBootcampCapacities(bootcampId, null))
                .verifyComplete();

        verify(capacityBootcampRepository).deleteByBootcampId(bootcampId);
    }
}