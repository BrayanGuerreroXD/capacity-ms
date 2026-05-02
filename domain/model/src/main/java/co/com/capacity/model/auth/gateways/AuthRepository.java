package co.com.capacity.model.auth.gateways;

import co.com.capacity.model.auth.Auth;
import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<Auth> save(Auth auth);
    Mono<Auth> findByToken(String token);
    Mono<Auth> findByEmail(String email);
    Mono<Void> deleteByToken(String token);
    Mono<Void> deleteByEmail(String email);
}