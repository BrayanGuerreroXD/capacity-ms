package co.com.capacity.drivenAdapters.r2dbc.adapter;

import co.com.capacity.drivenAdapters.r2dbc.entity.TechnologyCatalogEntity;
import co.com.capacity.drivenAdapters.r2dbc.mapper.TechnologyCatalogEntityMapper;
import co.com.capacity.drivenAdapters.r2dbc.repository.TechnologyCatalogEntityRepository;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TechnologyCatalogRepositoryAdapter implements TechnologyCatalogRepository {

    private final TechnologyCatalogEntityRepository entityRepository;
    private final TechnologyCatalogEntityMapper mapper;

    @Override
    public Mono<TechnologyCatalog> save(TechnologyCatalog technologyCatalog) {
        TechnologyCatalogEntity entity = mapper.toEntity(technologyCatalog);
        if (entity.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        } else {
            entity.setUpdatedAt(LocalDateTime.now());
        }
        return entityRepository.save(entity)
                .map(mapper::toModel);
    }

    @Override
    public Mono<TechnologyCatalog> findByExternalId(Long externalId) {
        return entityRepository.findByExternalId(externalId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<TechnologyCatalog> findById(Long id) {
        return entityRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Flux<TechnologyCatalog> findAll() {
        return entityRepository.findAll()
                .map(mapper::toModel);
    }

    @Override
    public Flux<TechnologyCatalog> findAllById(List<Long> ids) {
        return entityRepository.findAllById(ids)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByExternalId(Long externalId) {
        return entityRepository.existsByExternalId(externalId);
    }

    @Override
    public Mono<Void> deleteByExternalId(Long externalId) {
        return entityRepository.findByExternalId(externalId)
                .flatMap(entity -> entityRepository.deleteById(entity.getId()))
                .then();
    }
}