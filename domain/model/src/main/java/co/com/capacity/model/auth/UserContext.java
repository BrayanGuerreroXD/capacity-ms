package co.com.capacity.model.auth;

import reactor.core.publisher.Mono;

public interface UserContext {
    Mono<LoggedUser> getCurrentUser();
}