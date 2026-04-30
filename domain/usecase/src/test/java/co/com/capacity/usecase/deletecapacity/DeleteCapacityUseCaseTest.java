package co.com.capacity.usecase.deletecapacity;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCapacityUseCaseTest {

    @Mock
    private CapacityRepository capacityRepository;

    @Mock
    private CapacityTechnologyRepository capacityTechnologyRepository;

    @Mock
    private CapacityEventGateway eventGateway;

    @InjectMocks
    private DeleteCapacityUseCase useCase;

    @Test
    void delete_whenNotFound_throwsNotFoundException() {
        when(capacityRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete(99L))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        ((NotFoundException) e).getError() == GlobalExceptionEnum.CAPACITY_NOT_FOUND)
                .verify();
    }

    @Test
    void delete_whenFound_deletesCapacityAndTechnologies() {
        Capacity capacity = Capacity.builder()
                .id(1L).name("Java").description("Language")
                .createdAt(LocalDateTime.now()).build();

        when(capacityRepository.findById(1L)).thenReturn(Mono.just(capacity));
        when(capacityRepository.delete(1L)).thenReturn(Mono.empty());
        when(capacityTechnologyRepository.deleteByCapacityId(1L)).thenReturn(Mono.empty());
        when(eventGateway.publishDeleted(capacity)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete(1L))
                .verifyComplete();

        verify(capacityRepository).delete(1L);
        verify(capacityTechnologyRepository).deleteByCapacityId(1L);
    }
}