package co.com.capacity.entryPoints.kafka.consumer;

import co.com.capacity.model.capacitybootcamp.dto.CapacityBootcampSyncEvent;
import co.com.capacity.usecase.synccapacitybootcampservice.SyncCapacityBootcampService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacityBootcampSyncConsumer {

    private final SyncCapacityBootcampService syncCapacityBootcampService;

    @KafkaListener(topics = "${kafka.topics.sync-capacities-bootcamps-match}")
    public void consume(String message) {
        try {
            CapacityBootcampSyncEvent event = parseMessage(message);
            syncCapacityBootcampService.syncBootcampCapacities(event.getBootcampId(), event.getCapacityIds())
                    .subscribe(null, error -> log.error("Error syncing bootcamp capacities: {}", error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing capacity bootcamp sync message: {}", e.getMessage());
        }
    }

    private CapacityBootcampSyncEvent parseMessage(String message) {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.readValue(message, CapacityBootcampSyncEvent.class);
    }
}