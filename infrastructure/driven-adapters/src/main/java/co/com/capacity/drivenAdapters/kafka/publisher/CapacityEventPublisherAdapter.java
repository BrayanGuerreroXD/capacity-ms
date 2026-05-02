package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import co.com.capacity.model.technologycatalog.TechnologyCatalog;
import co.com.capacity.model.technologycatalog.gateways.TechnologyCatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CapacityEventPublisherAdapter implements CapacityEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TechnologyCatalogRepository technologyCatalogRepository;

    public CapacityEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate,
                                         TechnologyCatalogRepository technologyCatalogRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.technologyCatalogRepository = technologyCatalogRepository;
    }

    @Value("${kafka.topics.sync-capacity-catalog}")
    private String topicCatalog;

    @Value("${kafka.topics.sync-capacity-deleted}")
    private String topicDeleted;

    @Override
    public Mono<Void> publish(Capacity capacity) {
        return enrichWithTechnologies(capacity)
                .flatMap(this::sendCapacityEvent);
    }

    private Mono<Capacity> enrichWithTechnologies(Capacity capacity) {
        if (capacity.getTechnologyIds() == null || capacity.getTechnologyIds().isEmpty()) {
            return Mono.just(capacity);
        }
        return technologyCatalogRepository.findAllById(capacity.getTechnologyIds())
                .collectList()
                .map(technologies -> capacity.toBuilder()
                        .technologies(technologies)
                        .build());
    }

    private Mono<Void> sendCapacityEvent(Capacity capacity) {
        return Mono.fromFuture(kafkaTemplate.send(topicCatalog, Map.of(
                "id", capacity.getId(),
                "name", capacity.getName(),
                "technologies", capacity.getTechnologies() != null
                        ? capacity.getTechnologies().stream()
                                .map(tech -> Map.of("id", tech.getExternalId(), "name", tech.getName()))
                                .toList()
                        : List.of()
        ))).then();
    }

    @Override
    public Mono<Void> publishDeleted(Capacity capacity) {
        return Mono.fromFuture(kafkaTemplate.send(topicDeleted, Map.of(
                "id", capacity.getId(),
                "name", capacity.getName()
        ))).then();
    }
}