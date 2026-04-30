package co.com.capacity.api.capacity;

import co.com.capacity.dto.CapacityRequest;
import co.com.capacity.dto.CapacityTechnologyRequest;
import co.com.capacity.dto.GenericResponseData;
import co.com.capacity.mapper.CapacityDTOMapper;
import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.GlobalExceptionEnum;
import co.com.capacity.entryPoints.rest.capacity.CapacityHandler;
import co.com.capacity.entryPoints.rest.capacity.CapacityRouter;
import co.com.capacity.usecase.createcapacity.CreateCapacityService;
import co.com.capacity.usecase.deletecapacity.DeleteCapacityService;
import co.com.capacity.usecase.getcapacity.GetCapacityService;
import co.com.capacity.usecase.updatecapacity.UpdateCapacityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.reactive.server.HttpHandlerConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityRouterTest {

    @Mock
    private CreateCapacityService createService;

    @Mock
    private UpdateCapacityService updateService;

    @Mock
    private GetCapacityService getService;

    @Mock
    private DeleteCapacityService deleteService;

    @Mock
    private CapacityDTOMapper mapper;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        CapacityHandler handler = new CapacityHandler(createService, updateService, getService, deleteService, mapper);
        var routes = new CapacityRouter().capacityRoutes(handler);

        var httpHandler = WebHttpHandlerBuilder
                .webHandler(RouterFunctions.toWebHandler(routes))
                .exceptionHandler((exchange, ex) -> {
                    if (ex instanceof NotFoundException) {
                        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                        return Mono.empty();
                    }
                    if (ex instanceof ConflictException) {
                        exchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
                        return Mono.empty();
                    }
                    return Mono.error(ex);
                })
                .build();

        client = WebTestClient.bindToServer(new HttpHandlerConnector(httpHandler)).build();
    }

    @Test
    void POST_capacities_returns200_withCapacityResponse() {
        CapacityRequest request = new CapacityRequest("Java", "Language", null);
        Capacity domain = Capacity.builder().name("Java").description("Language").build();
        Capacity saved = Capacity.builder().id(1L).name("Java").description("Language")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        var response = co.com.capacity.dto.CapacityResponse.builder()
                .id(1L).name("Java").description("Language").build();

        when(mapper.toModelWithTechnologies(any())).thenReturn(domain);
        when(createService.create(any())).thenReturn(Mono.just(saved));
        when(mapper.toResponse(any())).thenReturn(response);

        client.post().uri("/api/v1/capacities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Java");
    }

    @Test
    void POST_capacities_withTechnologies_returns200() {
        List<CapacityTechnologyRequest> technologies = List.of(
                new CapacityTechnologyRequest(1L),
                new CapacityTechnologyRequest(2L)
        );
        CapacityRequest request = new CapacityRequest("Go", "Language", technologies);
        Capacity domain = Capacity.builder().name("Go").description("Language")
                .technologyIds(List.of(1L, 2L)).build();
        Capacity saved = Capacity.builder().id(1L).name("Go").description("Language").build();
        var response = co.com.capacity.dto.CapacityResponse.builder()
                .id(1L).name("Go").description("Language").build();

        when(mapper.toModelWithTechnologies(any())).thenReturn(domain);
        when(createService.create(any())).thenReturn(Mono.just(saved));
        when(mapper.toResponse(any())).thenReturn(response);

        client.post().uri("/api/v1/capacities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void POST_capacities_returns409_whenNameAlreadyExists() {
        CapacityRequest request = new CapacityRequest("Java", "Language", null);
        Capacity domain = Capacity.builder().name("Java").description("Language").build();

        when(mapper.toModelWithTechnologies(any())).thenReturn(domain);
        when(createService.create(any()))
                .thenReturn(Mono.error(new ConflictException(GlobalExceptionEnum.CAPACITY_NAME_ALREADY_EXISTS)));

        client.post().uri("/api/v1/capacities")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void GET_capacities_byId_returns200_withCapacityResponse() {
        Capacity found = Capacity.builder().id(1L).name("Java").description("Language").build();
        var response = co.com.capacity.dto.CapacityResponse.builder()
                .id(1L).name("Java").description("Language").build();

        when(getService.getById(1L)).thenReturn(Mono.just(found));
        when(mapper.toResponse(any())).thenReturn(response);

        client.get().uri("/api/v1/capacities/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Java");
    }

    @Test
    void GET_capacities_byId_returns404_whenNotFound() {
        when(getService.getById(99L))
                .thenReturn(Mono.error(new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)));

        client.get().uri("/api/v1/capacities/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void GET_capacities_returns200_withList() {
        Capacity c1 = Capacity.builder().id(1L).name("Java").description("Language").build();
        Capacity c2 = Capacity.builder().id(2L).name("Kotlin").description("JVM").build();
        var r1 = co.com.capacity.dto.CapacityResponse.builder().id(1L).name("Java").description("Language").build();
        var r2 = co.com.capacity.dto.CapacityResponse.builder().id(2L).name("Kotlin").description("JVM").build();

        when(getService.getAll(anyInt(), anyInt())).thenReturn(Flux.just(c1, c2));
        when(mapper.toResponse(c1)).thenReturn(r1);
        when(mapper.toResponse(c2)).thenReturn(r2);

        client.get().uri("/api/v1/capacities?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[1].id").isEqualTo(2);
    }

    @Test
    void PUT_capacities_byId_returns200_withUpdatedCapacity() {
        CapacityRequest request = new CapacityRequest("Java Updated", "Updated desc", null);
        Capacity domain = Capacity.builder().name("Java Updated").description("Updated desc").build();
        Capacity updated = Capacity.builder().id(1L).name("Java Updated").description("Updated desc").build();
        var response = co.com.capacity.dto.CapacityResponse.builder()
                .id(1L).name("Java Updated").description("Updated desc").build();

        when(mapper.toModelWithTechnologies(any())).thenReturn(domain);
        when(updateService.update(any())).thenReturn(Mono.just(updated));
        when(mapper.toResponse(any())).thenReturn(response);

        client.put().uri("/api/v1/capacities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("Java Updated");
    }

    @Test
    void PUT_capacities_byId_returns404_whenNotFound() {
        CapacityRequest request = new CapacityRequest("Java", "Language", null);
        Capacity domain = Capacity.builder().name("Java").description("Language").build();

        when(mapper.toModelWithTechnologies(any())).thenReturn(domain);
        when(updateService.update(any()))
                .thenReturn(Mono.error(new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)));

        client.put().uri("/api/v1/capacities/99")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void DELETE_capacities_byId_returns200() {
        when(deleteService.delete(1L)).thenReturn(Mono.empty());

        client.delete().uri("/api/v1/capacities/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void DELETE_capacities_byId_returns404_whenNotFound() {
        when(deleteService.delete(99L))
                .thenReturn(Mono.error(new NotFoundException(GlobalExceptionEnum.CAPACITY_NOT_FOUND)));

        client.delete().uri("/api/v1/capacities/99")
                .exchange()
                .expectStatus().isNotFound();
    }
}