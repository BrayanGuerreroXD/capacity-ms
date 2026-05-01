package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.capacity.gateways.SyncTechnologyCapacityGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SyncTechnologyCapacityEventPublisherAdapter implements SyncTechnologyCapacityGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sync-technologies-capacities-match}")
    private String topicMatch;

    public SyncTechnologyCapacityEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> publishSyncMatch(Long capacityId, List<Long> technologyExternalIds) {
        return Mono.fromFuture(kafkaTemplate.send(topicMatch, Map.of(
                "capacityId", capacityId,
                "technologyId", technologyExternalIds
        ))).then();
    }
}