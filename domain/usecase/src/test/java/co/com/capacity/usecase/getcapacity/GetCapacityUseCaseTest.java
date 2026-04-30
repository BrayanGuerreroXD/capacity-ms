package co.com.capacity.usecase.getcapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCapacityUseCaseTest {

    @Mock
    private CapacityRepository capacityRepository;

    @InjectMocks
    private GetCapacityUseCase useCase;

    @Test
    void getById_whenNotFound_throwsNotFoundException() {
        when(capacityRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getById(99L))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        ((NotFoundException) e).getError() == GlobalExceptionEnum.CAPACITY_NOT_FOUND)
                .verify();
    }

    @Test
    void getById_whenFound_returnsCapacity() {
        Capacity capacity = Capacity.builder()
                .id(1L).name("Java").description("Language")
                .createdAt(LocalDateTime.now()).build();

        when(capacityRepository.findById(1L)).thenReturn(Mono.just(capacity));

        StepVerifier.create(useCase.getById(1L))
                .expectNext(capacity)
                .verifyComplete();
    }

    @Test
    void getAll_returnsPaginatedCapacities() {
        Capacity c1 = Capacity.builder().id(1L).name("Java").description("Lang").build();
        Capacity c2 = Capacity.builder().id(2L).name("Kotlin").description("JVM").build();

        when(capacityRepository.findAll(0, 10)).thenReturn(Flux.just(c1, c2));

        StepVerifier.create(useCase.getAll(0, 10))
                .expectNext(c1, c2)
                .verifyComplete();
    }
}