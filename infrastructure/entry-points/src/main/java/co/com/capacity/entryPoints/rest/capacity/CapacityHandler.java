package co.com.capacity.entryPoints.rest.capacity;

import co.com.capacity.mapper.CapacityDTOMapper;
import co.com.capacity.dto.CapacityRequest;
import co.com.capacity.dto.GenericResponseData;
import co.com.capacity.usecase.createcapacity.CreateCapacityService;
import co.com.capacity.usecase.deletecapacity.DeleteCapacityService;
import co.com.capacity.usecase.getcapacity.GetCapacityService;
import co.com.capacity.usecase.updatecapacity.UpdateCapacityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapacityHandler {

    private final CreateCapacityService createService;
    private final UpdateCapacityService updateService;
    private final GetCapacityService getService;
    private final DeleteCapacityService deleteService;
    private final CapacityDTOMapper mapper;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CapacityRequest.class)
                .flatMap(dto -> createService.create(mapper.toModelWithTechnologies(dto)))
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .bodyValue(GenericResponseData.of(response)));
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return ServerResponse.ok()
                .body(getService.getById(id)
                        .map(mapper::toResponse)
                        .map(GenericResponseData::of), GenericResponseData.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        return ServerResponse.ok()
                .body(getService.getAll(page, size)
                        .map(mapper::toResponse)
                        .collectList()
                        .map(GenericResponseData::of), GenericResponseData.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(CapacityRequest.class)
                .flatMap(dto -> {
                    var capacity = mapper.toModelWithTechnologies(dto);
                    capacity.setId(id);
                    return updateService.update(capacity);
                })
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .bodyValue(GenericResponseData.of(response)));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return deleteService.delete(id)
                .thenReturn(GenericResponseData.of("Capacity deleted successfully"))
                .flatMap(data -> ServerResponse.ok().bodyValue(data));
    }
}