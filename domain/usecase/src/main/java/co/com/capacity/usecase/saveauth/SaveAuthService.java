package co.com.capacity.usecase.saveauth;

import co.com.capacity.model.auth.Auth;
import reactor.core.publisher.Mono;

public interface SaveAuthService {
    Mono<Auth> save(Auth auth);
    Mono<Auth> saveWithEmailValidation(Auth auth);
}