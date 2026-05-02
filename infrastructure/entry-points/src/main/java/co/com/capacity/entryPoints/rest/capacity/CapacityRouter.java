package co.com.capacity.entryPoints.rest.capacity;

import co.com.capacity.dto.CapacityRequest;
import co.com.capacity.dto.GenericResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
public class CapacityRouter {

    private static final String PATH = "/api/v1/capacities";

    @RouterOperations({
            @RouterOperation(path = PATH, method = RequestMethod.POST,
                    beanClass = CapacityHandler.class, beanMethod = "create",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    operation = @Operation(
                            operationId = "createCapacity",
                            summary = "Create a new capacity",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CapacityRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Capacity created successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponseData.class)))
                            }
                    )),
            @RouterOperation(path = PATH + "/{id}", method = RequestMethod.GET,
                    beanClass = CapacityHandler.class, beanMethod = "getById",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    operation = @Operation(
                            operationId = "getCapacityById",
                            summary = "Get capacity by ID",
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Capacity found",
                                            content = @Content(schema = @Schema(implementation = GenericResponseData.class)))
                            }
                    )),
            @RouterOperation(path = PATH, method = RequestMethod.GET,
                    beanClass = CapacityHandler.class, beanMethod = "getAll",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    operation = @Operation(
                            operationId = "getAllCapacities",
                            summary = "Get all capacities",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List of capacities",
                                            content = @Content(schema = @Schema(implementation = GenericResponseData.class)))
                            }
                    )),
            @RouterOperation(path = PATH + "/{id}", method = RequestMethod.PUT,
                    beanClass = CapacityHandler.class, beanMethod = "update",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    operation = @Operation(
                            operationId = "updateCapacity",
                            summary = "Update an existing capacity",
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CapacityRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Capacity updated successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponseData.class)))
                            }
                    )),
            @RouterOperation(path = PATH + "/{id}", method = RequestMethod.DELETE,
                    beanClass = CapacityHandler.class, beanMethod = "delete",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    operation = @Operation(
                            operationId = "deleteCapacity",
                            summary = "Delete a capacity",
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Capacity deleted successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponseData.class)))
                            }
                    ))
    })
    @Bean
    public RouterFunction<ServerResponse> capacityRoutes(CapacityHandler handler) {
        return RouterFunctions.route()
                .POST(PATH, handler::create)
                .PUT(PATH + "/{id}", handler::update)
                .GET(PATH + "/{id}", handler::getById)
                .GET(PATH, handler::getAll)
                .DELETE(PATH + "/{id}", handler::delete)
                .build();
    }
}