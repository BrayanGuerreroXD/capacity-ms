package co.com.capacity.usecase.getauth;

import co.com.capacity.model.auth.LoggedUser;
import reactor.core.publisher.Mono;

public interface GetAuthService {
    Mono<LoggedUser> getByToken(String token);
}