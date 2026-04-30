package co.com.capacity.entryPoints.rest.technologycatalog;

import co.com.capacity.dto.GenericResponseData;
import co.com.capacity.mapper.TechnologyCatalogDTOMapper;
import co.com.capacity.usecase.gettechnologycatalog.GetTechnologyCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TechnologyCatalogHandler {

    private final GetTechnologyCatalogService getService;
    private final TechnologyCatalogDTOMapper mapper;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .body(getService.getAll()
                        .map(mapper::toResponse)
                        .collectList()
                        .map(GenericResponseData::of), GenericResponseData.class);
    }
}