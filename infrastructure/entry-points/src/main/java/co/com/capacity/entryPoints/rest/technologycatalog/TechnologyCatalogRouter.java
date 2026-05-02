package co.com.capacity.entryPoints.rest.technologycatalog;

import co.com.capacity.dto.GenericResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TechnologyCatalogRouter {

    private static final String PATH = "/api/v1/technology-catalogs";

    @RouterOperations({
            @RouterOperation(path = PATH, method = RequestMethod.GET,
                    beanClass = TechnologyCatalogHandler.class, beanMethod = "getAll",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    operation = @Operation(
                            operationId = "getAllTechnologyCatalogs",
                            summary = "Get all technology catalogs",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List of technology catalogs",
                                            content = @Content(schema = @Schema(implementation = GenericResponseData.class)))
                            }
                    ))
    })
    @Bean
    public RouterFunction<ServerResponse> technologyCatalogRoutes(TechnologyCatalogHandler handler) {
        return RouterFunctions.route()
                .GET(PATH, handler::getAll)
                .build();
    }
}