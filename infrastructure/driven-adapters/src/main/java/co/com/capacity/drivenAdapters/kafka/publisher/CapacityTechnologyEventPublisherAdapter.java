package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.capacitytechnology.CapacityTechnologyEvent;
import co.com.capacity.model.capacitytechnology.gateways.CapacityTechnologyEventGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CapacityTechnologyEventPublisherAdapter implements CapacityTechnologyEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicCreated;
    private final String topicDeleted;

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