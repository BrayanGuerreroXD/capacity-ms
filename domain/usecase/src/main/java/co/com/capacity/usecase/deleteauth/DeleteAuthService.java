package co.com.capacity.usecase.deleteauth;

import reactor.core.publisher.Mono;

public interface DeleteAuthService {
    Mono<Void> deleteByToken(String token);
}