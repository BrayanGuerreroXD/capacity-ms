package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.model.capacitybootcamp.dto.CapacityBootcampSyncEvent;
import co.com.capacity.usecase.synccapacitybootcampservice.SyncCapacityBootcampService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacityBootcampSyncConsumer {

    private final SyncCapacityBootcampService syncCapacityBootcampService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.sync-capacities-bootcamps-match}")
    public void consume(String message) {
        try {
            CapacityBootcampSyncEvent event = objectMapper.readValue(message, CapacityBootcampSyncEvent.class);
            syncCapacityBootcampService.syncBootcampCapacities(event.getBootcampId(), event.getCapacityIds())
                    .subscribe(null, error -> log.error("Error syncing bootcamp capacities: {}", error.getMessage()));
        } catch (JsonProcessingException e) {
            log.error("Error parsing capacity bootcamp sync message: {}", e.getMessage());
        }
    }
}