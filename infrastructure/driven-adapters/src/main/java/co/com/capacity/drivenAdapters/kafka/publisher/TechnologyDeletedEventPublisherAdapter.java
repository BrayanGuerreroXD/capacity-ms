package co.com.capacity.drivenAdapters.kafka.publisher;

import co.com.capacity.model.technologycatalog.gateways.TechnologyDeletedEventGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TechnologyDeletedEventPublisherAdapter implements TechnologyDeletedEventGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.delete.technology.match}")
    private String topicDeleted;

    public TechnologyDeletedEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> publishDeleted(List<Long> externalIds) {
        return Mono.fromFuture(kafkaTemplate.send(topicDeleted, Map.of(
                "technologyIds", externalIds
        ))).then();
    }
}