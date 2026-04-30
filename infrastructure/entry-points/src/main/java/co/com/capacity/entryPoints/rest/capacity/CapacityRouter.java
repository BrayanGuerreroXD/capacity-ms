package co.com.capacity.entryPoints.rest.capacity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class CapacityRouter {

    private static final String PATH = "/api/v1/capacities";

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