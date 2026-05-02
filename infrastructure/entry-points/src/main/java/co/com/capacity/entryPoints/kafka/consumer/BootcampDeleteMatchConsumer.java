package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.model.capacitybootcamp.dto.BootcampDeleteMatchEvent;
import co.com.capacity.usecase.deletebootcampmatch.DeleteBootcampMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampDeleteMatchConsumer {

    private final DeleteBootcampMatchService deleteBootcampMatchService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.delete-bootcamp-match}")
    public void consume(String message) {
        try {
            BootcampDeleteMatchEvent event = objectMapper.readValue(message, BootcampDeleteMatchEvent.class);
            deleteBootcampMatchService.deleteMatch(event.getBootcampId(), event.getCapacityIds())
                    .subscribe(null, error -> log.error("Error deleting bootcamp match: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing bootcamp delete match message: {}", e.getMessage());
        }
    }
}