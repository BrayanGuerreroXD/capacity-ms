package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.capacitytechnology.CapacityTechnologyEvent;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyEventGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class CapacityTechnologyEventPublisherAdapter implements CapacityTechnologyEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sync-capacity-technology-catalog}")
    private String topicCreated;

    @Value("${kafka.topics.sync-capacity-technology-deleted}")
    private String topicDeleted;

    public CapacityTechnologyEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> publishCreated(CapacityTechnologyEvent event) {
        return Mono.fromFuture(kafkaTemplate.send(topicCreated, Map.of(
                "capacityId", event.getCapacityId(),
                "technologyCatalogId", event.getTechnologyCatalogId()
        ))).then();
    }

    @Override
    public Mono<Void> publishDeleted(CapacityTechnologyEvent event) {
        return Mono.fromFuture(kafkaTemplate.send(topicDeleted, Map.of(
                "capacityId", event.getCapacityId(),
                "technologyCatalogId", event.getTechnologyCatalogId()
        ))).then();
    }
}