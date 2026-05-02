package co.com.capacity.model.technologycatalog.gateways;

import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyDeletedEventGateway {
    Mono<Void> publishDeleted(List<Long> externalIds);
}