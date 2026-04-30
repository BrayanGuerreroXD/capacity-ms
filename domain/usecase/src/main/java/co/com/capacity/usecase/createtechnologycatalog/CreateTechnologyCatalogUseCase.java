package co.com.capacity.usecase.createtechnologycatalog;

import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateTechnologyCatalogUseCase implements CreateTechnologyCatalogService {

    private final TechnologyCatalogRepository technologyCatalogRepository;

    @Override
    public Mono<TechnologyCatalog> create(TechnologyCatalog technologyCatalog) {
        return technologyCatalogRepository.findByExternalId(technologyCatalog.getExternalId())
                .flatMap(existing -> {
                    TechnologyCatalog updated = TechnologyCatalog.builder()
                            .id(existing.getId())
                            .externalId(technologyCatalog.getExternalId())
                            .name(technologyCatalog.getName())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return technologyCatalogRepository.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    TechnologyCatalog newCatalog = TechnologyCatalog.builder()
                            .externalId(technologyCatalog.getExternalId())
                            .name(technologyCatalog.getName())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return technologyCatalogRepository.save(newCatalog);
                }));
    }
}