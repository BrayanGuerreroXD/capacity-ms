package co.com.capacity.usecase.deletecapacity;

import reactor.core.publisher.Mono;

public interface DeleteCapacityService {
    Mono<Void> delete(Long id);
}