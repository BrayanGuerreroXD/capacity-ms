package co.com.capacity.config;

import co.com.capacity.dto.ErrorData;
import co.com.capacity.dto.GenericResponseData;
import co.com.capacity.model.utils.exception.BadRequestException;
import co.com.capacity.model.utils.exception.ConflictException;
import co.com.capacity.model.utils.exception.NotFoundException;
import co.com.capacity.model.utils.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        GenericResponseData<ErrorData> body = buildErrorBody(ex);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(body))
                .flatMap(bytes -> exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))));
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof BadRequestException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof ConflictException) return HttpStatus.CONFLICT;
        if (ex instanceof UnauthorizedException) return HttpStatus.UNAUTHORIZED;
        if (ex instanceof MethodNotAllowedException) return HttpStatus.METHOD_NOT_ALLOWED;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private GenericResponseData<ErrorData> buildErrorBody(Throwable ex) {
        if (ex instanceof NotFoundException notFoundEx) {
            return GenericResponseData.of(ErrorData.of(notFoundEx.getError()));
        }
        if (ex instanceof ConflictException conflictEx) {
            return GenericResponseData.of(ErrorData.of(conflictEx.getError()));
        }
        if (ex instanceof BadRequestException badRequestEx) {
            return GenericResponseData.of(ErrorData.of(badRequestEx.getError()));
        }
        if (ex instanceof UnauthorizedException unauthorizedEx) {
            return GenericResponseData.of(ErrorData.of(unauthorizedEx.getError()));
        }
        return GenericResponseData.of(new ErrorData("INTERNAL_ERROR", ex.getMessage(), "An unexpected error occurred"));
    }
}