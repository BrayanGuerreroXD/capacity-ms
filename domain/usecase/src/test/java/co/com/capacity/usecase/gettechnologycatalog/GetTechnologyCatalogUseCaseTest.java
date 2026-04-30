package co.com.capacity.usecase.gettechnologycatalog;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTechnologyCatalogUseCaseTest {

    @Mock
    private TechnologyCatalogRepository technologyCatalogRepository;

    @InjectMocks
    private GetTechnologyCatalogUseCase useCase;

    @Test
    void getAll_returnsAllTechnologies() {
        TechnologyCatalog t1 = TechnologyCatalog.builder().id(1L).name("Java").externalId(100L).build();
        TechnologyCatalog t2 = TechnologyCatalog.builder().id(2L).name("Kotlin").externalId(101L).build();

        when(technologyCatalogRepository.findAll()).thenReturn(Flux.just(t1, t2));

        StepVerifier.create(useCase.getAll())
                .expectNext(t1, t2)
                .verifyComplete();
    }
}