package co.com.capacity.usecase.createcapacitytechnology;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacitytechnology.CapacityTechnology;
import co.com.capacity.model.capacitytechnology.CapacityTechnologyEvent;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyEventGateway;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyRepository;
import co.com.capacity.model.capacity.gateways.CapacityRepository;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCapacityTechnologyUseCaseTest {

    @Mock
    private CapacityTechnologyRepository capacityTechnologyRepository;

    @Mock
    private CapacityTechnologyEventGateway eventGateway;

    @Mock
    private CapacityRepository capacityRepository;

    @InjectMocks
    private CreateCapacityTechnologyUseCase useCase;

    @Test
    void create_whenCapacityNotFound_throwsNotFoundException() {
        CapacityTechnology input = CapacityTechnology.builder()
                .capacityId(99L).technologyId(1L).build();

        when(capacityRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(input))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        ((NotFoundException) e).getError() == GlobalExceptionEnum.CAPACITY_NOT_FOUND)
                .verify();
    }

    @Test
    void create_whenCapacityExists_savesAndPublishesEvent() {
        Capacity capacity = Capacity.builder().id(1L).name("Java").description("Lang").build();
        CapacityTechnology input = CapacityTechnology.builder()
                .capacityId(1L).technologyId(1L).build();
        CapacityTechnology saved = CapacityTechnology.builder()
                .id(1L).capacityId(1L).technologyId(1L).build();

        when(capacityRepository.findById(1L)).thenReturn(Mono.just(capacity));
        when(capacityTechnologyRepository.save(any())).thenReturn(Mono.just(saved));
        when(eventGateway.publishCreated(any(CapacityTechnologyEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(input))
                .expectNext(saved)
                .verifyComplete();

        verify(eventGateway).publishCreated(any(CapacityTechnologyEvent.class));
    }
}