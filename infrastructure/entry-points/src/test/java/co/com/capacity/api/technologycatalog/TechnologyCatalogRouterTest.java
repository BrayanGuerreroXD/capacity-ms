package co.com.capacity.api.technologycatalog;

import co.com.capacity.dto.GenericResponseData;
import co.com.capacity.dto.TechnologyCatalogResponse;
import co.com.capacity.entryPoints.rest.technologycatalog.TechnologyCatalogHandler;
import co.com.capacity.entryPoints.rest.technologycatalog.TechnologyCatalogRouter;
import co.com.capacity.mapper.TechnologyCatalogDTOMapper;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.usecase.gettechnologycatalog.GetTechnologyCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.HttpHandlerConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechnologyCatalogRouterTest {

    @Mock
    private GetTechnologyCatalogService getService;

    @Mock
    private TechnologyCatalogDTOMapper mapper;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        TechnologyCatalogHandler handler = new TechnologyCatalogHandler(getService, mapper);
        var routes = new TechnologyCatalogRouter().technologyCatalogRoutes(handler);

        client = WebTestClient.bindToServer(new HttpHandlerConnector(
                WebHttpHandlerBuilder.webHandler(RouterFunctions.toWebHandler(routes)).build()
        )).build();
    }

    @Test
    void GET_technology_catalogs_returns200_withList() {
        TechnologyCatalog t1 = TechnologyCatalog.builder()
                .id(1L).name("Java").externalId(100L).build();
        TechnologyCatalog t2 = TechnologyCatalog.builder()
                .id(2L).name("Kotlin").externalId(101L).build();
        TechnologyCatalogResponse r1 = TechnologyCatalogResponse.builder()
                .id(1L).name("Java").externalId(100L).build();
        TechnologyCatalogResponse r2 = TechnologyCatalogResponse.builder()
                .id(2L).name("Kotlin").externalId(101L).build();

        when(getService.getAll()).thenReturn(Flux.just(t1, t2));
        when(mapper.toResponse(t1)).thenReturn(r1);
        when(mapper.toResponse(t2)).thenReturn(r2);

        client.get().uri("/api/v1/technology-catalogs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Java")
                .jsonPath("$.data[1].id").isEqualTo(2)
                .jsonPath("$.data[1].name").isEqualTo("Kotlin");
    }

    @Test
    void GET_technology_catalogs_returnsEmptyList() {
        when(getService.getAll()).thenReturn(Flux.empty());

        client.get().uri("/api/v1/technology-catalogs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isArray();
    }
}