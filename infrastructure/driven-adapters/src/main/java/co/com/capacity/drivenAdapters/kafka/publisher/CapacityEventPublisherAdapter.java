package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class CapacityEventPublisherAdapter implements CapacityEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sync-capacity-catalog}")
    private String topicCatalog;

    @Value("${kafka.topics.sync-capacity-deleted}")
    private String topicDeleted;

    public CapacityEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> publish(Capacity capacity) {
        return Mono.fromFuture(kafkaTemplate.send(topicCatalog, Map.of(
                "id", capacity.getId(),
                "name", capacity.getName()
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