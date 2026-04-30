package co.com.capacity.usecase.deletecapacitytechnology;

import reactor.core.publisher.Mono;

public interface DeleteCapacityTechnologyService {
    Mono<Void> deleteById(Long id);
}