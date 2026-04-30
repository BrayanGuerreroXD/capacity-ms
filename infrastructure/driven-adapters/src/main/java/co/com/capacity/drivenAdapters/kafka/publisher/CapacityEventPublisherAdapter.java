package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.capacity.Capacity;
import co.com.capacity.model.capacity.gateways.CapacityEventGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CapacityEventPublisherAdapter implements CapacityEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicCatalog;
    private final String topicDeleted;

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